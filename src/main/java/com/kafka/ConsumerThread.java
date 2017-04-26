package com.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by aturbillon on 07/04/2017.
 */
public class ConsumerThread implements Runnable {

    private final KafkaConsumer<String, String> consumer;
    private final int partNbr;
    private String topic;

    public ConsumerThread(String brokers, String groupId, String topic, int partitionNumber) {
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
        props.put("session.timeout.ms", "30000");
        props.put("auto.offset.reset", "earliest");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("consumer.interceptor.classes", "io.confluent.monitoring.clients.interceptor.MonitoringConsumerInterceptor");
        return props;
    }

    @Override
    public void run() {
        while (true) {

            //to read a specific partition of the topic
            List<TopicPartition> partitions = new ArrayList<>();
            partitions.add(new TopicPartition(topic, partNbr));

            consumer.assign(partitions);
            // consumer.assign(Arrays.asList(partDesc));
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                System.out.println("\n Read message:  \n #########" + record.value() + "\n From , Partition: "
                        + record.partition() + ", Offset: " + record.offset() + ", with key " + record.key() + ", by ThreadID: " +
                        +Thread.currentThread().getId() + "\n ########");
            /*    try {
                    Thread.sleep(1000); //just not to flood the console
                } catch (InterruptedException ie) {
                }*/

            }
        }
    }


}
