package com.kafka;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by aturbillon on 07/04/2017.
 */
public class MainProducerConsumerPartition {


    public static void main(String[] args) throws IOException {

        //properties for the servers
    //    String brokers = "localhost:9092,localhost:9093,localhost:9094";
//with this we get the kafka broker values
        Properties properties = new Properties();
        InputStream inputStream = MainProducerConsumerPartition.class.getClassLoader()
                .getResourceAsStream("kafka_broker.properties");
        properties.load(inputStream);

       String brokers= String.valueOf(properties.get("BROKER"));

        //properties for the topic
        int replicationFactor = 2;//r factor max = Nbroker -1
        int partitionNumber = 4;

        //properties for the producer
        String groupId = "test";
        String topic = "Ticket";
        String path = String.valueOf(properties.get("FILE_PATH"));

        //creation of the topic
        TopicCreation t = new TopicCreation(topic, partitionNumber, replicationFactor);
        Thread TopicCreation = new Thread(t);
        TopicCreation.start();

        // Start Producer Thread
        ProducerThread producerThread = new ProducerThread(brokers, topic, path);
        Thread CreateProducer = new Thread(producerThread);


        //this launch an simple consumer for a given partition
        ConsumerThread consumerPart0 =
                new ConsumerThread(brokers, groupId, topic, 0); //i will specify witch partition we read
        Thread consumerPartition0 = new Thread(consumerPart0);

        ConsumerThread consumerPart1 =
                new ConsumerThread(brokers, groupId, topic, 1); //i will specify witch partition we read
        Thread consumerPartition1 = new Thread(consumerPart1);

        ConsumerThread consumerPart2 =
                new ConsumerThread(brokers, groupId, topic, 2); //i will specify witch partition we read
        Thread consumerPartition2 = new Thread(consumerPart2);

        ConsumerThread consumerPart3 =
                new ConsumerThread(brokers, groupId, topic, 3); //i will specify witch partition we read
        Thread consumerPartition3 = new Thread(consumerPart3);


        //the launching part of all the thread
        CreateProducer.start();

        consumerPartition0.start();
        consumerPartition1.start();
        consumerPartition2.start();
        consumerPartition3.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
        }

    }

}