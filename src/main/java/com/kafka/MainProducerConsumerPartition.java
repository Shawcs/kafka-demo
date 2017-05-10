package com.kafka;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by aturbillon on 07/04/2017.
 */
public class MainProducerConsumerPartition {


    public static void main(String[] args) throws IOException, InterruptedException {
        //properties for the servers
        //with this we get the kafka broker values
        Properties properties = new Properties();
        InputStream inputStream = MainProducerConsumerPartition.class.getClassLoader()
                .getResourceAsStream("kafka_broker.properties");
        properties.load(inputStream);

        String brokers = String.valueOf(properties.get("BROKER"));

        //properties for the topic
        int replicationFactor = 2;//r factor max = Nbroker -1
        int partitionNumber = 4;

        //properties for the producer
        String groupId = "test";
        String topic = "Ticket";
        String path = String.valueOf(properties.get("FILE_PATH"));
        String zookeeper = String.valueOf(properties.get("ZOOKEEPER"));

        //creation of the topic
        TopicCreation t = new TopicCreation(topic, partitionNumber, replicationFactor, zookeeper);
        Thread TopicCreation = new Thread(t);
        TopicCreation.start();
        TopicCreation.join();//wait the thread to finish

        // Start Producer Thread
        ProducerThread producerThread = new ProducerThread(brokers, topic, path);
        Thread CreateProducer = new Thread(producerThread);
        CreateProducer.start();
        CreateProducer.join();

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

        consumerPartition0.start();
            consumerPartition0.join();
        consumerPartition1.start();
            consumerPartition1.join();
        consumerPartition2.start();
            consumerPartition2.join();
        consumerPartition3.start();
            consumerPartition3.join();


    }

}