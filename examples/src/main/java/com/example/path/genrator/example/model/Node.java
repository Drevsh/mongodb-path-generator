package com.example.path.genrator.example.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.stahlmann.metamodel.processor.Entity;
import de.stahlmann.metamodel.processor.Property;
import org.immutables.value.Value;

import java.util.Collection;
import java.util.UUID;

@Entity
@Value.Immutable
@JsonSerialize(as = ImmutableNode.class)
@JsonDeserialize(as = ImmutableNode.class)
public interface Node extends Base<String> {
    @Property
    String description();

    @Property
    int value();

    @Property
    Collection<Node> children();
}
