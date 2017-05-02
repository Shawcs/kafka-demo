package com.kafka;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by aturbillon on 07/04/2017.
 */
//this is just to product new message in the topic to see the reader working in real time
public class MainProduceMore {

    public static void main(String[] args) throws IOException {

        //properties for the servers
        Properties properties = new Properties();
        InputStream inputStream = MainProducerConsumerPartition.class.getClassLoader()
                .getResourceAsStream("kafka_broker.properties");
        properties.load(inputStream);
        String brokers= String.valueOf(properties.get("BROKER"));

        //properties for the producer
        String topic = "Ticket";
        String path = String.valueOf(properties.get("FILE_PATH_MORE"));

        // Start Producer Thread
        ProducerThread producerThread = new ProducerThread(brokers, topic, path);
        Thread CreateProducer = new Thread(producerThread);
        CreateProducer.start();

    }

}