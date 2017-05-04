package com.kafka;


import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.apache.kafka.common.errors.TopicExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * link http://stackoverflow.com/questions/38810066/can-adminutils-createtopic-api-connect-to-multiple-zookeeper-nodes
 * link stackoverflow.com/questions/27036923/how-to-create-a-topic-in-kafka-through-java
 * Created by kafka on 18/04/17.
 */
public class TopicCreation implements Runnable {

    final Logger logger = LoggerFactory.getLogger(TopicCreation.class);
    private final String topicName;
    private final int nbrOfPartition;
    private final int nbrOfReplication;
    private final String zookeeperHost;

    public TopicCreation(String TopicName, int NbrOfPartition, int NbrOfReplication,String zookeeper) {
        this.topicName = TopicName;
        this.nbrOfPartition = NbrOfPartition;
        this.nbrOfReplication = NbrOfReplication;
        this.zookeeperHost=zookeeper;
    }

    public int getNbrOfReplication() {
        return nbrOfReplication;
    }

    public int getNbrOfPartition() {
        return nbrOfPartition;
    }

    public void run() {
        ZkClient zkClient = null;
        ZkUtils zkUtils = null;
        try {

            zkClient = new ZkClient(zookeeperHost, 15000, 10000, ZKStringSerializer$.MODULE$);
            zkUtils = new ZkUtils(zkClient, new ZkConnection(zookeeperHost), false);

            Properties topicConfiguration = new Properties();

            AdminUtils.createTopic(zkUtils, topicName, nbrOfPartition, nbrOfReplication, topicConfiguration, RackAwareMode.Disabled$.MODULE$);

            //just to print a bit of setting in the console
            logger.info("we created the new TOPIC :" + topicName + ", it has " +
                    nbrOfPartition + " partitions, and " + nbrOfReplication + " replication");

        } catch (TopicExistsException ex) {
           logger.error("the topic " + topicName + " already exist");
        } finally {
            if (zkClient != null) {
                zkClient.close();
            }
        }
    }


}
