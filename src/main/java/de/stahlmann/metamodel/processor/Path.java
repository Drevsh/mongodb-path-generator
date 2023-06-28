package de.stahlmann.metamodel.processor;

public abstract class Path {
    protected String path = "";

    protected void add(String path) {
        if("".equals(this.path)) {
            this.path = path;
        } else {
            this.path += "." + path;
        }
    }

    public String getPath() {
        return path;
    }
}
