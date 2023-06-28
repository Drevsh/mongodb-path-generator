package de.stahlmann.metamodel.processor.graph;

import java.util.Objects;

public class Edge {
    private final Node source;
    private final Node destination;
    private final String propertyName;

    public Edge(Node source, Node destination, String propertyName) {
        this.source = source;
        this.destination = destination;
        this.propertyName = propertyName;
    }

    public Node getSource() {
        return source;
    }

    public Node getDestination() {
        return destination;
    }

    public String getPropertyName() {
        return propertyName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Edge edge = (Edge) o;
        return source.equals(edge.source) && destination.equals(edge.destination);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, destination);
    }
}
