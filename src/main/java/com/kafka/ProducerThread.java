package com.kafka;

//lien interressant howtoprogram.xyz/2016/05/29/create-multi-threaded-apache-kafka-consumer/
//schema registry http://docs.confluent.io/1.0/schema-registry/docs/serializer-formatter.html
//partitionner http://howtoprogram.xyz/2016/06/04/write-apache-kafka-custom-partitioner/

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serialiser.Ticket;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

import static java.lang.Thread.currentThread;


/**
 * Created by aturbillon on 07/04/2017.
 */
public class ProducerThread implements Runnable {

    final Logger logger = LoggerFactory.getLogger(ProducerThread.class);
    private final KafkaProducer<String, String> producer;
    private final String topic;
    private final String DATA_SOURCE;
    private final String BROKERS;

    public ProducerThread(String brokers, String topic, String data_source) {

        Properties prop = createProducerConfig(brokers);
        this.producer = new KafkaProducer<>(prop);
        this.topic = topic;
        this.DATA_SOURCE = data_source; //we added source because we fake stream and we are ready a file
        this.BROKERS = brokers;
    }

    private static Properties createProducerConfig(String brokers) {
        Properties props = new Properties();
        props.put("bootstrap.servers", brokers);
        props.put("client.id","client_1");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 10);
        props.put("buffer.memory", 33554432);
        props.put("request.timeout.ms",200);//see if work
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return props;
    }

    /**
     * This function is used to generate the message Key (it's a part of the meta data of a broker/message) based on the Type field
     **/
    public  String setKeyValue(String recordValue) throws JsonMappingException {
        String key ;
        ObjectMapper mapper = new ObjectMapper();
        Ticket currentTicket = null;
        try {
            currentTicket = mapper.readValue(recordValue, Ticket.class);
        } catch (JsonGenerationException e) {
            logger.error("Json exception "+e);
        } catch (JsonMappingException e) {
            logger.error("Json map "+e);
        } catch (IOException e) {
          logger.error("Io exception "+e);
        }

    String type = currentTicket.getType();

        if ("error".equals(type.toLowerCase())) {
            key = "E";
        } else if ("warning".equals(type.toLowerCase())) {
            key = "W";
        } else if ("critical_error".equals(type.toLowerCase())) {
            key = "CE";
        } else {
            key = "ND";
        }
        return key;
    }

    /**
     * This function is used to check the key of the message and set the partition id linked to this key
     **/
    public static int setPartitionID(String keyvalue) {
        int partitionId;

        if ("E".equals(keyvalue)) {
            partitionId = 2;
        } else if ("W".equals(keyvalue)) {
            partitionId = 1;
        } else if ("CE".equals(keyvalue)) {
            partitionId = 0;
        } else {
            partitionId = 3;
        }
        return partitionId;
    }

    //TODO put those two method in a separated class
@Override
    public void run() {
    try {
        System.out.println("we have " + getNbrPartition(topic, BROKERS) + " partition in the topic " + topic + " where we are writing on");// this is time consuming because it's evey heavy for the server
    } catch (IOException e) {
       logger.error(""+e);
    }
    Reader fileReader = null;// Read file in order to fake the  data flow
    try {
        fileReader = new InputStreamReader(new FileInputStream(DATA_SOURCE));
         BufferedReader bufferedReader = new BufferedReader(fileReader);
            String strLine;
            int i = 0;

        while ((strLine = bufferedReader.readLine()) != null) {
                i = i + 1;

                final String finalStrLine1 = strLine;//To display it in the loop
                final int finalI = i;//To display it in the loop

                //it's our simple custom key generator and serializer
                String keyValue = setKeyValue(strLine);
                int partitionNbr = setPartitionID(keyValue);

                producer.send(new ProducerRecord<>(topic, partitionNbr, keyValue, strLine), new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception e) {
                        if (e != null) {
                          logger.error("error on completion "+e);
                        }
                        logger.info("\n Sent message: \n ##########" + finalStrLine1 + "\n to, Partition: " + metadata.partition() + ", Offset: "
                                        + metadata.offset() + ",key " + finalI + " to topic '" + metadata.topic() + "' by thread " + currentThread().getId() + "\n ######");
                        System.out.println("\n Sent message: \n ##########" + finalStrLine1 + "\n to, Partition: " + metadata.partition() + ", Offset: "
                                + metadata.offset() + ",key " + finalI + " to topic '" + metadata.topic() + "' by thread " + currentThread().getId() + "\n ######");
                         }
                });
          /*      try {
                    Thread.sleep(1000); //to avoid flood in console
                } catch (InterruptedException ie) {
                    logger.debug("exception  "+ie);
                }*/
            }
            } catch (IOException e) {
                logger.error("file "+e);
            }
          finally {
                try {
                    fileReader.close();//see if this not blocking
                } catch (IOException e) {
                    logger.error(""+e);
            }
        producer.close();
        }
    }

    //we call the function from consumerGroup that calculate the number of partition from a topic + broker list the group id is just here to keep trace from who is doing what in zookeeper
   public int getNbrPartition(String topicName, String brokers) throws IOException {
       InputStream inputStream = MainProducerConsumerPartition.class.getClassLoader()
               .getResourceAsStream("kafka_broker.properties");
       Properties properties = new Properties();
       properties.load(inputStream);

       String zook= String.valueOf(properties.get("ZOOKEEPER"));

        ConsumerGroup c = new ConsumerGroup(brokers, "nbrRequester", topicName,zook);
        return c.getNumberOfPartition();
    }

}
