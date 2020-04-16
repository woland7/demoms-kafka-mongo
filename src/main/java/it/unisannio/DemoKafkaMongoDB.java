package it.unisannio;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.bson.Document;

import java.time.Duration;
import java.util.*;

public class DemoKafkaMongoDB {
    public static void main(String[] args) {
        MongoClient mongo = new MongoClient("localhost", 27017);
        // if database doesn't exists, MongoDB will create it for you
        MongoDatabase db = mongo.getDatabase("testdb");
        // if collection doesn't exists, MongoDB will create it for you
        MongoCollection<Document> collection = db.getCollection("user");

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "localhost:9092");
        props.setProperty("group.id", "test");
        props.setProperty("enable.auto.commit", "false");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("demoms"));
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, String> record : records){
                Document document = new Document("name", record.value());
                collection.insertOne(document);
                consumer.commitSync();
            }
        }
    }
}
