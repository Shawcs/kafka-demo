package com.kafka;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by aturbillon on 07/04/2017.
 */
//this is the class to consumer a topic by a consumer group
public class MainConsumerTopic {

    public static void main(String[] args) throws IOException {
     //properties for the servers
        Properties properties = new Properties();
        InputStream inputStream = MainProducerConsumerPartition.class.getClassLoader()
                .getResourceAsStream("kafka_broker.properties");
        properties.load(inputStream);
        String brokers= String.valueOf(properties.get("BROKER"));
        String zookeeper = String.valueOf(properties.get("ZOOKEEPER"));

        //properties for the producer
        String groupId = "test";
        String topic = "Ticket";

        //this launch an simple consumer alone
        // Start group of Notification Consumers
        ConsumerGroup consumerGroup =
                new ConsumerGroup(brokers, groupId, topic,zookeeper);

        consumerGroup.execute();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ie) {
        }

    }

}