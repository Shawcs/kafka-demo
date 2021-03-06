package com.kafka;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by aturbillon on 02/05/2017.
 */

//this main class is used to create a topic with fake data. be careful it will create data until you stop the thread
public class MainInfos {

    public static void main(String[] args) throws IOException {

        //properties for the servers
        Properties properties = new Properties();
        InputStream inputStream = MainProducerConsumerPartition.class.getClassLoader()
                .getResourceAsStream("kafka_broker.properties");
        properties.load(inputStream);

        String brokers= String.valueOf(properties.get("BROKER"));
        String zookeeper = String.valueOf(properties.get("ZOOKEEPER"));
        //properties for the producer
        String topic = "Fake_Data";

        // Start Producer Thread

        ConsumerGroup consumerGroup =
                new ConsumerGroup(brokers, "test", topic,zookeeper);


      consumerGroup.getBorkerListConsumer().toString();

        consumerGroup.getTopicListConsumer().toString();

    }

}
