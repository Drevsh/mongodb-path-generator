package com.example.path.genrator.example;

import com.example.path.genrator.example.model.ImmutableNode;
import com.example.path.genrator.example.model.Node;
import com.example.path.genrator.example.model.Node_;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.model.Filters;
import com.mongodb.reactivestreams.client.FindPublisher;
import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import org.bson.codecs.configuration.CodecRegistry;
import org.immutables.criteria.mongo.bson4jackson.BsonModule;
import org.immutables.criteria.mongo.bson4jackson.IdAnnotationModule;
import org.immutables.criteria.mongo.bson4jackson.JacksonCodecs;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

@Testcontainers
public class PathGeneratorTest {
    public static final String NODES = "nodes";
    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");

    @Test
    void test() throws InterruptedException {
        // create MongoDatabase
        MongoDatabase database = createMongoDatabase();

        // insert example data
        insertExampleData(database);

        // **************************
        // actual use of library
        // **************************

        // create search path
        // searchPath = children.children.value
        String searchPath1 = Node_.root()
                .children()
                .children()
                .value();

        // use Filters and search path based on the generated metamodel to filter
        List<Node> nodesWhereChildrenChildrenHaveValue0 = Observable.fromPublisher(database.getCollection(NODES, Node.class)
                        .find(Filters.eq(searchPath1, 0)))
                .toList()
                .blockingGet();
        System.out.println("Found nodes where [root->children->children->value=0]: %s".formatted(nodesWhereChildrenChildrenHaveValue0));

        // create search path
        // searchPath = children.description
        String searchPath2 = Node_.root()
                .children()
                .description();

        // use Filters and search path based on the generated metamodel to filter
        List<Node> nodesWhereChildrenHaveDescriptionR1C1 = Observable.fromPublisher(database.getCollection(NODES, Node.class)
                        .find(Filters.eq(searchPath2, "R1C1")))
                .toList()
                .blockingGet();
        System.out.println("Found nodes where [root->children->description=R1C1]:  %s".formatted(nodesWhereChildrenHaveDescriptionR1C1));

        // create search path
        // searchPath = children.children.value
        String searchPath3 = Node_.root()
                .children()
                .children()
                .value();

        // use Filters and search path based on the generated metamodel to filter
        List<Node> nodesWhereChildrenChildrenHaveValue1 = Observable.fromPublisher(database.getCollection(NODES, Node.class)
                        .find(Filters.eq(searchPath3, 1)))
                .toList()
                .blockingGet();
        System.out.println("Found nodes where [root->children->children->value=1]: %s".formatted(nodesWhereChildrenChildrenHaveValue1));
    }

    @NotNull
    private static MongoDatabase createMongoDatabase() {
        ObjectMapper mapper = new ObjectMapper().registerModule(new BsonModule())
                .registerModule(new Jdk8Module())
                .registerModule(new IdAnnotationModule());
        CodecRegistry registry = JacksonCodecs.registryFromMapper(mapper);

        ConnectionString connString = new ConnectionString(mongoDBContainer.getConnectionString());

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connString)
                .retryWrites(true)
                .codecRegistry(registry)
                .build();

        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase("test")
                .withCodecRegistry(registry);
        return database;
    }

    private static void insertExampleData(MongoDatabase database) {
        ImmutableNode root1 = ImmutableNode.builder()
                .id("R1")
                .description("R1")
                .value(0)
                .children(List.of(ImmutableNode.builder()
                        .description("R1C1")
                        .id("C1")
                        .value(0)
                        .children(List.of(ImmutableNode.builder()
                                .description("R1C1C1")
                                .id("C1_1")
                                .value(1)
                                .children(List.of())
                                .build(), ImmutableNode.builder()
                                .description("R1C1C2")
                                .id("C1_2")
                                .value(1)
                                .children(List.of())
                                .build()))
                        .build(), ImmutableNode.builder()
                        .description("R1C2")
                        .id("C2")
                        .value(0)
                        .children(List.of(ImmutableNode.builder()
                                .description("R1C2C1")
                                .id("C2_1")
                                .value(1)
                                .children(List.of())
                                .build(), ImmutableNode.builder()
                                .description("R1C2C2")
                                .id("C2_2")
                                .value(1)
                                .children(List.of())
                                .build()))
                        .build()))
                .build();

        ImmutableNode root2 = ImmutableNode.builder()
                .id("R2")
                .description("R2")
                .value(0)
                .children(List.of(ImmutableNode.builder()
                        .description("R2C1")
                        .id("C1")
                        .value(0)
                        .children(List.of(ImmutableNode.builder()
                                .description("R2C1C1")
                                .id("C1_1")
                                .value(0)
                                .children(List.of())
                                .build(), ImmutableNode.builder()
                                .description("R2C1C2")
                                .id("C1_2")
                                .value(1)
                                .children(List.of())
                                .build()))
                        .build(), ImmutableNode.builder()
                        .description("R2C2")
                        .id("C2")
                        .value(0)
                        .children(List.of(ImmutableNode.builder()
                                .description("R2C2C1")
                                .id("C2_1")
                                .value(0)
                                .children(List.of())
                                .build(), ImmutableNode.builder()
                                .description("R2C2C2")
                                .id("C2_2")
                                .value(1)
                                .children(List.of())
                                .build()))
                        .build()))
                .build();

        // insert
        Single.fromPublisher(database.getCollection(NODES, Node.class)
                        .insertOne(root1))
                .blockingGet();
        Single.fromPublisher(database.getCollection(NODES, Node.class)
                        .insertOne(root2))
                .blockingGet();

        // fetch all
        List<Node> nodes = Observable.fromPublisher(database.getCollection(NODES, Node.class)
                        .find())
                .toList()
                .blockingGet();

        System.out.println("All available nodes: %s".formatted(nodes));
    }

}
