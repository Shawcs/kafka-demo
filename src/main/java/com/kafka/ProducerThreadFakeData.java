package com.kafka;

//lien interressant howtoprogram.xyz/2016/05/29/create-multi-threaded-apache-kafka-consumer/
//schema registry http://docs.confluent.io/1.0/schema-registry/docs/serializer-formatter.html
//partitionner http://howtoprogram.xyz/2016/06/04/write-apache-kafka-custom-partitioner/

import com.recordGenerator.javafaker.Faker;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Locale;
import java.util.Properties;

import static java.lang.Thread.currentThread;


/**
 * Created by aturbillon on 07/04/2017.
 */
public class ProducerThreadFakeData implements Runnable {

    public static final String ANSI_RESET = "\u001B[0m";//just to put some colors in the prompt and because i have time to do it !
    public static final String ANSI_GREEN = "\u001B[32m";

    final Logger logger = LoggerFactory.getLogger(ProducerThreadFakeData.class);
    private final KafkaProducer<String, String> producer;
    private final String topic;
    private final String BROKERS;

    public ProducerThreadFakeData(String brokers, String topic) {

        Properties prop = createProducerConfig(brokers);
        this.producer = new KafkaProducer<>(prop);
        this.topic = topic;
        this.BROKERS = brokers;
    }

    private static Properties createProducerConfig(String brokers) {
        Properties props = new Properties();
        props.put("bootstrap.servers", brokers);
        props.put("acks", "all"); //guarantees that not only did the partition leader accept the write, but it was successfully replicated to all of the in-sync replicas
        props.put("retries", 0);//value larger than 0 (which is the default), then message reordering may occur since the retry may occur after a following write succeeded
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("enable.auto.commit", "false");//offsets will not commit automatically from the config
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("producer.interceptor.classes", "io.confluent.monitoring.clients.interceptor.MonitoringProducerInterceptor");
        return props;
    }

@Override
    public void run() {
        int i = 0;
        while(true){
            logger.info("we have " + getNbrPartition(topic, BROKERS) + " partition in the topic " + topic + " where we are writing on");
                String record =  fakeRecord(); //we create a fake record

                producer.send(new ProducerRecord<>(topic,record ), new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception e) {
                        if (e != null) {
                          logger.info("error on completion "+e);
                        }
                        logger.info("\n Sent message: \n ##########" + record + "\n to, Partition: " + metadata.partition() + ", Offset: "
                                        + metadata.offset() + " to topic '" + metadata.topic() + "' by thread " + currentThread().getId() + "\n ######");
                    }
                });
                i++;
                try {
                    Thread.sleep(1000); //to avoid flood in console
                } catch (InterruptedException ie) {
                    logger.debug("exception  "+ie);
                }
                if(i>=100000||record == null){ //we block the loop if we go over i nbr of data send it's just in case you forget to stop the thread in order to not over flow the memory
                    logger.debug("exception record null");
                    break;
                }
            }
    }


    //you can produce fake record with this function. You have a lot of field that you can generate they are all in recordGenerator folder see https://github.com/DiUS/java-faker for more details
    public String fakeRecord(){
        Faker faker = new Faker(new Locale("fr"));

            String name = faker.name().fullName();
            Date birthDate = faker.date().birthday();
            String streetAddress = faker.address().streetAddress();
            String emailAddress = faker.internet().emailAddress();
            String phoneNumber = faker.phoneNumber().cellPhone();
            String creditCard = faker.finance().creditCard();

            String record =ANSI_GREEN+"Name: " +ANSI_RESET+ name +ANSI_GREEN+ "/ Birth: " +ANSI_RESET+ birthDate +ANSI_GREEN+ "/ Email: " +ANSI_RESET+ emailAddress +
                    ANSI_GREEN+ " / Phone " +ANSI_RESET+ phoneNumber +ANSI_GREEN+ " / Street: "+ANSI_RESET+ streetAddress+ANSI_GREEN+" / Credit Card: "+ ANSI_RESET+creditCard ;

            System.out.println(record);
            return record;
        }

    //we call the function from consumerGroup that calculate the number of partition from a topic + broker list the group id is just here to keep trace from who is doing what in zookeeper
    public int getNbrPartition(String topicName, String brokers) {
        ConsumerGroup c = new ConsumerGroup(brokers, "nbrRequester", topicName);
        return c.getNumberOfPartition();
    }

}