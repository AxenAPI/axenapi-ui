package org.example.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.graph.EventGraph;
import org.example.graph.Link;
import org.example.graph.Node;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public class EventGraphUtil {
    public static boolean equalsGraphs(EventGraph g1, EventGraph g2) {
//        g1.removeNodesWithoutLinks();
//        g2.removeNodesWithoutLinks();
        Set<Node> g1WithLinks = new HashSet<>();
        Set<Node> g2WithLinks = new HashSet<>();
        for (Node node : g1.getNodes()) {
            if(g1.getLinks().stream()
                    .anyMatch(link -> link.getFrom().equals(node) || link.getTo().equals(node))) {
                g1WithLinks.add(node);
            }
        }

        for (Node node : g2.getNodes()) {
            if(g2.getLinks().stream()
                    .anyMatch(link -> link.getFrom().equals(node) || link.getTo().equals(node))) {
                g2WithLinks.add(node);
            }
        }

        if (g1WithLinks.size() != g2WithLinks.size() || g1.getLinks().size() != g2.getLinks().size()) {
            return false;
        }
        // compare nodes. ignore order
        for (Node n1 : g1WithLinks) {
            if (!g2WithLinks.contains(n1)) {
                return false;
            }
        }

        for (Node n2 : g2WithLinks) {
            if (!g1WithLinks.contains(n2)) {
                return false;
            }
        }

        // compare links. ignore order
        Set<Link> g2Links = g2.getLinks();
        for (Link l1 : g1.getLinks()) {
            if (!g2Links.contains(l1)) {
                return false;
            }
        }
        return true;
    }

    public static void saveGraphAsJson(EventGraph eventGraph, String filePath) throws IOException {
        // create file if not exist
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
        }

        // create an ObjectMapper instance
        ObjectMapper mapper = new ObjectMapper();

        // convert the object to JSON
        String json = mapper.writeValueAsString(eventGraph);
        // write json to file
        java.nio.file.Files.write(java.nio.file.Paths.get(filePath), json.getBytes());
    }
    public static EventGraph loadGraph(String filePath) throws IOException {
        byte[] bytes = Files.readAllBytes(Paths.get(filePath));
        String json = new String(bytes);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, EventGraph.class);
    }
}
