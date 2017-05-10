package com.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.log4j.Level;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by aturbillon on 07/04/2017.
 */
public class ConsumerThread implements Runnable {

    final org.slf4j.Logger logger = LoggerFactory.getLogger("ConsumerThread.class");
    private final KafkaConsumer<String, String> consumer;
    private final int partNbr;
    private String topic;

    public ConsumerThread(String brokers, String groupId, String topic, int partitionNumber) {
        org.apache.log4j.Logger.getRootLogger().setLevel(Level.OFF);//set the lvl at debug
        Properties prop = createConsumerConfig(brokers, groupId);
        this.consumer = new KafkaConsumer<>(prop);
        this.topic = topic;
        this.partNbr = partitionNumber;
    }

    private static Properties createConsumerConfig(String brokers, String groupId) {
        Properties props = new Properties();
        props.put("bootstrap.servers", brokers);
        props.put("group.id", groupId);
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "3000");
        props.put("heartbeat.interval.ms", "1000");//this is the heartbeat setting from zookeeper
        props.put("auto.offset.reset", "earliest");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        return props;
    }

    @Override
    public void run() {

            //to read a specific partition of the topic
            List<TopicPartition> partitions = new ArrayList<>();
            partitions.add(new TopicPartition(topic, partNbr));

            consumer.assign(partitions);
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("\n Read message:  \n #########" + record.value() + "\n From , Partition: "
                        + record.partition() + ", Offset: " + record.offset() + ", with key " + record.key() + ", by ThreadID: " +
                        +Thread.currentThread().getId() + "\n ########");
            }
        }


}
