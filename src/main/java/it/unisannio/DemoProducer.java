package it.unisannio;

import it.unisannio.model.DevicePiece;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.Scanner;

public class DemoProducer {
    private static Logger log = LoggerFactory.getLogger(DemoProducer.class);
    public static void main(String[] args){
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);
        Scanner in = new Scanner(System.in);
        int i = 100;
        while(true){
            System.out.println("Enter piece ID:");
            int idPiece = Integer.parseInt(in.nextLine());
            System.out.println("Enter device ID:");
            String idDevice = in.nextLine();
            //log.info("Added new piece...");
            DevicePiece dp = new DevicePiece(idPiece,idDevice);
            producer.send(new ProducerRecord<>("museo", Integer.toString(i), dp.toString()));
            if(idPiece == 0)
                break;
        }
        producer.close();
    }
}
