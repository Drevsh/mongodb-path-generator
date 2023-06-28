package de.stahlmann.metamodel.processor.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Graph {
    private final List<Node> nodes = new ArrayList<>();
    private final Map<Node, List<Edge>> edges = new HashMap<>();

    public Node addNode(Node node) {
        // node already present
        if (nodes.contains(node)) {
            return nodes.get(nodes.indexOf(node));
        }

        nodes.add(node);
        edges.put(node, new ArrayList<>());

        return node;
    }

    public Node find(String packageName, String className) {
        return nodes.stream()
                .filter(n -> packageName.equals(n.getPackageName()) && className.equals(n.getClassName()))
                .findAny()
                .get();
    }

    public void addEdge(Edge edge) {
        List<Edge> currentEdges = edges.get(edge.getSource());
        currentEdges.add(edge);

        // not strictly needed but better programming style (edges.get could return a new list in the future)
        edges.put(edge.getSource(), currentEdges);
    }

    public List<Node> getNodes() {
        return new ArrayList<>(edges.keySet());
    }

    public List<Edge> getEdges(Node node) {
        return edges.get(node);
    }

    public List<Edge> getEdges() {
        return edges.values()
                .stream()
                .flatMap(Collection::stream)
                .toList();
    }

    public void print() {
        for (Map.Entry<Node, List<Edge>> entry : edges.entrySet()) {
            Node node = entry.getKey();
            List<Edge> nodeEdges = entry.getValue();

            for (Edge e : nodeEdges) {
                System.out.println(
                        "Node: %s -- %s --> %s".formatted(e.getSource(), e.getPropertyName(), e.getDestination()));
            }
        }
    }

}
