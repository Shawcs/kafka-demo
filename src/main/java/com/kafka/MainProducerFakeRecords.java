package com.kafka;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by aturbillon on 02/05/2017.
 */

//this main class is used to create a topic with fake data. be careful it will create data until you stop the thread
public class MainProducerFakeRecords {

    public static void main(String[] args) throws IOException {

        //properties for the servers
     //   String brokers = "localhost:9092,localhost:9093,localhost:9094";
        Properties properties = new Properties();
        InputStream inputStream = MainProducerConsumerPartition.class.getClassLoader()
                .getResourceAsStream("kafka_broker.properties");
        properties.load(inputStream);

        String brokers= String.valueOf(properties.get("BROKER"));
        //properties for the producer
        String topic = "Fake_Data";

        // Start Producer Thread
        ProducerThreadFakeData producerFakeThread = new ProducerThreadFakeData(brokers, topic);
        Thread CreateProducer = new Thread(producerFakeThread);
        CreateProducer.start();

    }

}
