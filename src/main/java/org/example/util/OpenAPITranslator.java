package org.example.util;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.example.graph.EventGraph;
import org.example.graph.Link;
import org.example.graph.Node;
import org.example.graph.NodeType;

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

        Node serviceNode = new Node(title, NodeType.SERVICE);
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
                    Node incomingTopic = new Node(topic, NodeType.TOPIC);
                    if (!eventGraph.containsNode(topic, NodeType.TOPIC)) {
                        eventGraph.addNode(incomingTopic);
                    } else {
                        incomingTopic = eventGraph.getNode(topic, NodeType.TOPIC);
                    }
                    Link incomingLink = new Link(incomingTopic, serviceNode, schemaName);
                    eventGraph.addLink(incomingLink);
                });
            }

            if (extensions.containsKey("x-outgoing")) {
                Map<String, Object> xOutgoing = (Map<String, Object>) extensions.get("x-outgoing");
                List<String> topics = (List<String>) xOutgoing.get("topics");
                // add topics to Node list, if not already there
                topics.forEach(topic -> {
                    Node outgoingTopic = new Node(topic, NodeType.TOPIC);
                    if (!eventGraph.containsNode(topic, NodeType.TOPIC)) {
                        eventGraph.addNode(outgoingTopic);
                    } else {
                        outgoingTopic = eventGraph.getNode(topic, NodeType.TOPIC);
                    }
                    Link outgoungLink = new Link(serviceNode, outgoingTopic, schemaName);
                    eventGraph.addLink(outgoungLink);
                });
            }
        });
        return eventGraph;
    }
}
