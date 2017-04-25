package com.kafka;

//lien interressant howtoprogram.xyz/2016/05/29/create-multi-threaded-apache-kafka-consumer/
//schema registry http://docs.confluent.io/1.0/schema-registry/docs/serializer-formatter.html
//partitionner http://howtoprogram.xyz/2016/06/04/write-apache-kafka-custom-partitioner/

import com.Serialiser.Ticket;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.io.*;
import java.util.Properties;

import static java.lang.Thread.currentThread;


/**
 * Created by aturbillon on 07/04/2017.
 */
public class ProducerThread implements Runnable {

    private final KafkaProducer<String, String> producer;
    private final String topic;
    private final String DATA_SOURCE;
    private final String BROKERS;

    public ProducerThread(String brokers, String topic, String data_source) {

        Properties prop = createProducerConfig(brokers);
        this.producer = new KafkaProducer<String, String>(prop);
        this.topic = topic;
        this.DATA_SOURCE = data_source; //we added source because we fake stream and we are ready a file
        this.BROKERS = brokers;
    }

    private static Properties createProducerConfig(String brokers) {
        Properties props = new Properties();
        props.put("bootstrap.servers", brokers);
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("enable.auto.commit", "false");//offsets will not commit automatically from the config
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("producer.interceptor.classes", "io.confluent.monitoring.clients.interceptor.MonitoringProducerInterceptor");
        return props;
    }

    /**
     * This function is used to generate the message Key (it's a part of the meta data of a broker/message) based on the Type field
     **/
    public static String setKeyValue(String RecordValue) {
        String key = null;
        ObjectMapper mapper = new ObjectMapper();
        Ticket t = null;
        try {

            t = mapper.readValue(RecordValue, Ticket.class);

        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String type = t.getType();
        if (type.equals("error")) {
            key = "E";
        } else if (type.equals("warning")) {
            key = "W";
        } else if (type.equals("critical_error")) {
            key = "CE";
        } else {
            key = "ND";
        }
        return key;
    }

    /**
     * This function is used to check the key of the message and set the partition id linked to this key
     **/
    public static int SetPartitionID(String KeyValue) {
        int partitionId;

        if (KeyValue.equals("E")) {
            partitionId = 2;
        } else if (KeyValue.equals("W")) {
            partitionId = 1;
        } else if (KeyValue.equals("CE")) {
            partitionId = 0;
        } else {
            partitionId = 3;
        }
        return partitionId;
    }

    //TODO put those two method in a separated class

    public void run() {
        try {
            // Read file in order to fake the  data flow
            Reader FileReader = null;
            try {
                FileReader = new InputStreamReader(new FileInputStream(DATA_SOURCE));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            BufferedReader bufferedReader = new BufferedReader(FileReader);
            String strLine;
            int i = 0;

            System.out.println("we have " + getNbrPartition(topic, BROKERS) + " partition in the topic " + topic + " where we are writing on");
            while ((strLine = bufferedReader.readLine()) != null) {
                i = i + 1;

                final String finalStrLine = strLine;
                final String finalStrLine1 = strLine;//To display it in the loop
                final int finalI = i;//To display it in the loop

                //it's our simple custom key generator and serializer
                String KeyValue = setKeyValue(strLine);
                int PartitionNbr = SetPartitionID(KeyValue);

                producer.send(new ProducerRecord<>(topic, PartitionNbr, KeyValue, strLine), new Callback() {
                    public void onCompletion(RecordMetadata metadata, Exception e) {
                        if (e != null) {
                            e.printStackTrace();
                        }
                        System.out.println("\n Sent message: \n ##########" + finalStrLine1 + "\n to, Partition: " + metadata.partition() + ", Offset: "
                                + metadata.offset() + ",key " + finalI + " to topic '" + metadata.topic() + "' by thread " + currentThread().getId() + "\n ######");
                    }
                });
                try {
                    Thread.sleep(1000); //to avoid flood in console
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            producer.close();
        }
    }

    //we call the function from consumerGroup that calculate the number of partition from a topic + broker list the group id is just here to keep trace from who is doing what in zookeeper
    public int getNbrPartition(String topicName, String brokers) {
        ConsumerGroup c = new ConsumerGroup(brokers, "nbrRequester", topicName);
        return c.getNumberOfPartition();
    }

}
