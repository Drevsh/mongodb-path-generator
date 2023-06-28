package de.stahlmann.metamodel.processor.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Node {
    private final String packageName;
    private final String className;

    public Node(String packageName, String className) {
        this.packageName = packageName;
        this.className = className;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    public String getFullyQualifiedClassName() {
        return "%s.%s".formatted(packageName, className);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Node node = (Node) o;
        return packageName.equals(node.packageName) && className.equals(node.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(packageName, className);
    }
}
