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
import org.example.demojavafx.datamodel.Color;
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
            String topic = parts[parts.length - 2];
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
            Color color = eventGraph.getEventColor();
            final Event event = eventGraph.getEvent(schemaName) == null ?
                    new Event(value, color, schemaName) : eventGraph.getEvent(schemaName);
            eventGraph.addEvent(event);
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
                    Link incomingLink = new Link(incomingTopic, serviceNode, group, event);
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
                    Link outgoungLink = new Link(serviceNode, outgoingTopic, null, event);
                    eventGraph.addLink(outgoungLink);
                });
            }
        });
        return eventGraph;
    }

    public static void saveOpenAPISpecification(EventGraph eventGraph, String folderPath) {
        // create info  for each openAPI specification by nodes with type SERVICE
        Map<String, OpenAPI> openAPIMap = eventGraph.getNodes().stream()
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
        for (Link link : eventGraph.getLinks()) {
            if(link.getTo().getType() == NodeType.SERVICE
                    && link.getFrom().getType() == NodeType.TOPIC){
                OpenAPI openAPI = openAPIMap.get(link.getTo().getName());
                if(openAPI.getComponents() == null) {
                    openAPI.setComponents(new Components());
                }
                createPaths(link, openAPI);
                // add schemas from input links
                openAPI.getComponents().addSchemas(link.getEvent().getName(), link.getEvent().getSchema());
            }else if(link.getFrom().getType() == NodeType.SERVICE
                    && link.getTo().getType() == NodeType.TOPIC){
                OpenAPI openAPI = openAPIMap.get(link.getFrom().getName());
                if(openAPI.getComponents() == null) {
                    openAPI.setComponents(new Components());
                }
                openAPI.getComponents().addSchemas(link.getEvent().getName(), link.getEvent().getSchema());
            }
        }
        // write in each file each specification from the map.
        openAPIMap.forEach((key, value) -> {
            // key - the name of file. ext = json
            // create file and write spec into it
            String fileName = folderPath + "/" + key.replaceAll("\\s+", "_") + ".json";
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

    private static void createPaths(Link link, final OpenAPI openAPI) {
        String linkPath;
        BrokerType brokerType = link.getBrokerType();
        if(brokerType == null) {
            linkPath = "/" + "no_type"
                    + "/" + link.getFrom().getName()
                    + "/" + link.getEvent().getName();
        } else {
            if (BrokerType.KAFKA == brokerType) {
                linkPath = "/" + brokerType.getValue()
                        + "/" + link.getGroup()
                        + "/" + link.getFrom().getName()
                        + "/" + link.getEvent().getName();
            } else {
                linkPath = "/" + brokerType.getValue()
                        + "/" + link.getFrom().getName()
                        + "/" + link.getEvent().getName();
            }
        }
        // add operation in openAPI
        if (openAPI.getPaths() == null) {
            openAPI.setPaths(new Paths());
        }
        openAPI.getPaths()
                .addPathItem(linkPath, createOperation(link));
    }


    private static PathItem createOperation(Link link) {
        PathItem pathItem = new PathItem();
        pathItem.setDescription("Operation for " + link.getEvent().getName());
        pathItem.post(createRequestBody(link.getEvent().getSchema(), link.getEvent().getName()));
        return pathItem;
    }

    private static Operation createRequestBody(Schema schema, String name) {
        Operation operation = new Operation();
        RequestBody requestBody = new RequestBody();
        requestBody.setRequired(true);
        requestBody.setContent(createContent(name));
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

    private static Content createContent(String schemaName) {
        Content content = new Content();
        MediaType mediaType = new MediaType();
        Schema ref = new Schema();
        ref.set$ref("#/components/schemas/" + schemaName);
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
