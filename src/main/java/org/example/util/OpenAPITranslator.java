package org.example.util;

import io.swagger.v3.core.util.Json;
import io.swagger.v3.oas.models.*;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.example.graph.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OpenAPITranslator {

    public static EventGraph parseOPenAPI(String filePath) {
        EventGraph eventGraph = new EventGraph();
        SwaggerParseResult result = new OpenAPIV3Parser()
                .readLocation(filePath,
                        null, null);
        OpenAPI openAPI = result.getOpenAPI();
        Components components = openAPI.getComponents();
        Info info = openAPI.getInfo();
        String title = info.getTitle();

        Map<String, BrokerType> brokers = new HashMap<>();
        Map<String, String> consumerGroup = new HashMap<>();

        openAPI.getPaths().forEach((key, value) -> {
            // broker type is in the first part of the link
            String[] parts = key.split("/");
            String brokerTypeString = parts[1];
            BrokerType brokerType = BrokerType.fromValue(brokerTypeString);
            // if 4 parts , topic is on 2th index. if 5 topic is the 3d one.
            String topic = parts.length == 4 ? parts[2] : parts[3];
            brokers.put(topic, brokerType);
            if(brokerType == BrokerType.KAFKA) {
                consumerGroup.put(topic, parts[2]);
            }
        });

        eventGraph.setName(title);
        Node serviceNode = new Node(title, NodeType.SERVICE, title, null);
        eventGraph.addNode(serviceNode);
        Map<String, Schema> schemas = components.getSchemas();

        schemas.forEach((key, value) -> {
            Map extensions = value.getExtensions();
            String schemaName = key;
            if (extensions.containsKey("x-incoming")) {
                Map<String, Object> xIncoming = (Map<String, Object>) extensions.get("x-incoming");
                List<String> topics = (List<String>) xIncoming.get("topics");
                // add topics to Node list, if not already there
                topics.forEach(topic -> {
                    BrokerType brokerType = brokers.get(topic);
                    Node incomingTopic = new Node(topic, NodeType.TOPIC, title, brokerType);
                    if (!eventGraph.containsNode(topic, NodeType.TOPIC)) {
                        eventGraph.addNode(incomingTopic);
                    } else {
                        incomingTopic = eventGraph.getNode(topic, NodeType.TOPIC);
                    }

                    String group = consumerGroup.get(topic);
                    Link incomingLink = new Link(incomingTopic, serviceNode, schemaName, title, value, brokerType, group);
                    eventGraph.addLink(incomingLink);
                });
            }

            if (extensions.containsKey("x-outgoing")) {
                Map<String, Object> xOutgoing = (Map<String, Object>) extensions.get("x-outgoing");
                List<String> topics = (List<String>) xOutgoing.get("topics");
                // add topics to Node list, if not already there
                topics.forEach(topic -> {
                    Node outgoingTopic = new Node(topic, NodeType.TOPIC, title, null);
                    if (!eventGraph.containsNode(topic, NodeType.TOPIC)) {
                        eventGraph.addNode(outgoingTopic);
                    } else {
                        outgoingTopic = eventGraph.getNode(topic, NodeType.TOPIC);
                    }
                    Link outgoungLink = new Link(serviceNode, outgoingTopic, schemaName, title, value,
                            null,
                            null);
                    eventGraph.addLink(outgoungLink);
                });
            }
        });
        return eventGraph;
    }

    public static void saveOpenAPISpecification(EventGraph eventGraph, String folderPath) {
        Map<String, OpenAPI> openAPIMap;
        // create info  for each openAPI specification by nodes with type SERVICE
        openAPIMap = eventGraph.getNodes().stream()
                .filter(node -> node.getType() == NodeType.SERVICE)
                .collect(
                        HashMap::new,
                        (map, node) -> map.put(node.getName(), createOpenAPI(node)),
                        Map::putAll
                );
        // create operations in each graph by incoming link. Each operation is a link.
        // Each operation has a schema (schema is in the link).
        // format of url: /kafka/{group}/{topic}/{modelName} method post.
        // body schema: schema
        eventGraph.getLinks().stream()
                .filter(link -> link.getTo().getType() == NodeType.SERVICE
                        && link.getFrom().getType() == NodeType.TOPIC)
                .forEach(link -> {
                    OpenAPI openAPI = openAPIMap.get(link.getTo().getName());
                    String linkPath;
                    if(link.getBrokerType() == null) {
                        linkPath = "/" + "no_type"
                                + "/" + link.getFrom().getName()
                                + "/" + link.getWhat();
                    } else {
                        if (BrokerType.KAFKA == link.getBrokerType()) {
                            linkPath = "/" + link.getBrokerType().getValue()
                                    + "/" + link.getGroup()
                                    + "/" + link.getFrom().getName()
                                    + "/" + link.getWhat();
                        } else {
                            linkPath = "/" + link.getBrokerType().getValue()
                                    + "/" + link.getFrom().getName()
                                    + "/" + link.getWhat();
                        }
                    }
                    // add operation in openAPI
                    if (openAPI.getPaths() == null) {
                        openAPI.setPaths(new Paths());
                    }
                    openAPI.getPaths()
                            .addPathItem(linkPath, createOperation(link));
                });

        // add schemas from input links
        eventGraph.getLinks().stream()
                .filter(link -> link.getTo().getType() == NodeType.SERVICE
                        && link.getFrom().getType() == NodeType.TOPIC)
                .forEach(link -> {
                    OpenAPI openAPI = openAPIMap.get(link.getTo().getName());
                    Schema schema = link.getSchema();
                    if(openAPI.getComponents() == null) {
                        openAPI.setComponents(new Components());
                    }
                    openAPI.getComponents().addSchemas(link.getWhat(), schema);
                });

        // open folder. check if folder exists.
        if (Files.notExists(Path.of(folderPath))) {
            try {
                Files.createDirectory(Path.of(folderPath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // write in each file each specification from the map.
        openAPIMap.forEach((key, value) -> {
            // key - the name of file. ext = json
            // create file and write spec into it
            String fileName = folderPath + "/" + key + ".json";
            try {
                // map value (OpenAPI) to json or json-string
                String jsonValue = Json.pretty(value);
                // write json into file
                Files.writeString(Path.of(fileName), jsonValue);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }



    private static PathItem createOperation(Link link) {
        PathItem pathItem = new PathItem();
        pathItem.setDescription("Operation for " + link.getWhat());
        pathItem.post(createRequestBody(link.getSchema()));
        return pathItem;
    }

    private static Operation createRequestBody(Schema schema) {
        Operation operation = new Operation();
        RequestBody requestBody = new RequestBody();
        requestBody.setRequired(true);
        requestBody.setContent(createContent(schema));
        operation.setRequestBody(requestBody);
        operation.setResponses(createResponses());
        return operation;
    }

    private static ApiResponses createResponses() {
        ApiResponses apiResponses = new ApiResponses();
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setDescription("Success. No content.");
        apiResponses.addApiResponse("200", apiResponse);
        return apiResponses;
    }

    private static Content createContent(Schema schema) {
        Content content = new Content();
        MediaType mediaType = new MediaType();
        Schema ref = new Schema();
        ref.set$ref("#/components/schemas/" + schema.getName());
        mediaType.setSchema(ref);
        mediaType.setSchema(ref);
        content.addMediaType("application/json", mediaType);
        return content;
    }

    private static OpenAPI createOpenAPI(Node node) {
        OpenAPI openAPI = new OpenAPI();
        Info info = new Info();
        info.setVersion("1.0.0"); // TODO get version
        info.setTitle(node.getName());
        info.setDescription("axenapi Specification for " + node.getName());
        openAPI.setInfo(info);
        openAPI.setPaths(new Paths());
        return openAPI;
    }
}
