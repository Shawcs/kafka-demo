package com.kafka;

/**
 * Created by aturbillon on 07/04/2017.
 */
//this is just to product new message in the topic to see the reader working in real time
public class MainProduceMore {

    public static void main(String[] args) {

        //properties for the servers
        String brokers = "localhost:9092,localhost:9093,localhost:9094";

        //properties for the producer
        String topic = "Ticket";
        String path = "/home/kafka/Téléchargements/KAFKA_demo/src/main/resources/User_more_data";

        // Start Producer Thread
        ProducerThread producerThread = new ProducerThread(brokers, topic, path);
        Thread CreateProducer = new Thread(producerThread);
        CreateProducer.start();

    }

}