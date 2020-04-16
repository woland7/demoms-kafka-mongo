package it.unisannio;

import com.google.gson.Gson;
import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.UpdateOptions;
import it.unisannio.model.DevicePiece;
import it.unisannio.model.Piece;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Properties;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Updates.*;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class DemoKafkaMongoDB {
    private static Logger log = LoggerFactory.getLogger(DemoKafkaMongoDB.class);
    public static void main(String[] args) {
        CodecRegistry pojoCodecRegistry = fromProviders(PojoCodecProvider.builder().automatic(true).build());
        CodecRegistry codecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(), pojoCodecRegistry);
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
                .codecRegistry(codecRegistry)
                .build();
        MongoClient mongo = MongoClients.create(clientSettings);
        // if database doesn't exists, MongoDB will create it for you
        MongoDatabase db = mongo.getDatabase("museo");
        // if collection doesn't exists, MongoDB will create it for you
        MongoCollection<Document> collectionPieces = db.getCollection("pieces");
        MongoCollection<Document> collectionVisits = db.getCollection("visits");

        Properties props = new Properties();
        props.setProperty("bootstrap.servers", "localhost:9092");
        props.setProperty("group.id", "test");
        props.setProperty("enable.auto.commit", "false");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("museo"));
        Gson gson = new Gson();
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> record : records){
                DevicePiece dp = gson.fromJson(record.value(),DevicePiece.class);
                if(dp.getIdPiece() != 0) {
                    collectionPieces.updateOne(eq("_id", dp.getIdPiece()),
                            combine(inc("count", 1)), new UpdateOptions().upsert(true));
                    consumer.commitAsync();
                }
                else {
                    AggregateIterable<Document> ai = collectionPieces.aggregate(
                            Collections.singletonList(
                                    Aggregates.group(null, Accumulators.avg("avg", "$count"))
                            )
                    );
                    double avg = 0;
                    for(Document d: ai)
                        avg = d.getDouble("avg");
                    MongoCursor<Document> cursor = collectionPieces.find(gt("count", avg)).
                            projection(fields(include("_id"))).iterator();
                    ArrayList<Integer> visit = new ArrayList<Integer>();
                    while(cursor.hasNext())
                        visit.add((Integer) cursor.next().get("_id"));
                    collectionVisits.insertOne(new Document().append("visit",visit));
                }
            }
        }
    }
}
