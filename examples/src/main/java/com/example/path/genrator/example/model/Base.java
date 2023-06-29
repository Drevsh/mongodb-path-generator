package com.example.path.genrator.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.stahlmann.metamodel.processor.Property;

import java.time.LocalDateTime;

public interface Base<T> {
    @Property
    @JsonProperty("_id")
    T id();

    @Property
    default LocalDateTime creationDate() {
        return LocalDateTime.now();
    }
}
