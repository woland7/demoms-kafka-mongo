package it.unisannio;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.model.*;
import com.mongodb.*;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.*;
import static com.mongodb.client.model.Updates.*;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.*;
import org.bson.Document;
import org.bson.types.ObjectId;
import it.unisannio.model.Piece;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.ArrayList;
import java.util.Arrays;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class Prova {
    public static void main(String[] args) {
        MongoClientSettings clientSettings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString("mongodb://localhost:27017"))
                .build();
        MongoClient mongo = MongoClients.create(clientSettings);
        // if database doesn't exists, MongoDB will create it for you
        MongoDatabase db = mongo.getDatabase("testdb");
        // if collection doesn't exists, MongoDB will create it for you
        MongoCollection<Document> collection = db.getCollection("pieces1");
        collection.updateOne(eq("_id",4),
                combine(inc("count",3)),new UpdateOptions().upsert(true));
        AggregateIterable<Document> ai = collection.aggregate(
                Arrays.asList(
                        Aggregates.group(null, Accumulators.avg("avg", "$count"))
                )
        );
        double avg = 0;
        for(Document d: ai) {
            avg = d.getDouble("avg");
            System.out.println(d.getDouble("avg"));
            String json = com.mongodb.util.JSON.serialize(d);
            System.out.println(json);
        }
        MongoCursor<Document> cursor = collection.find(gt("count", 4)).
                projection(fields(include("_id"))).iterator();
        ArrayList<Integer> visit = new ArrayList<>();
        while(cursor.hasNext())
            visit.add((Integer) cursor.next().get("_id"));
        for(Integer i: visit)
            System.out.println(i);

    }
}
