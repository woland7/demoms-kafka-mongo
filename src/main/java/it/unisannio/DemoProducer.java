package it.unisannio;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.Scanner;

public class DemoProducer {
    public static void main(String[] args){
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:9092");
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);
        Scanner in = new Scanner(System.in);
        int i = 0;
        boolean check = true;
        while(check){
            String prova = in.nextLine();
            if(prova.equals("fine")) {
                check = false;
                break;
            }
            producer.send(new ProducerRecord<>("demoms", Integer.toString(i), prova));
        }
        producer.close();
    }
}
