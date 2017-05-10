package com.kafka;


import com.google.common.collect.Lists;
import kafka.admin.AdminOperationException;
import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.common.TopicAndPartition;
import kafka.utils.ZKStringSerializer;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.kafka.common.errors.TopicExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.JavaConverters;
import scala.collection.Seq;

import java.util.Map;
import java.util.Properties;

/**
 * link http://stackoverflow.com/questions/38810066/can-adminutils-createtopic-api-connect-to-multiple-zookeeper-nodes
 * link stackoverflow.com/questions/27036923/how-to-create-a-topic-in-kafka-through-java
 * Created by kafka on 18/04/17.
 */
public class TopicCreation implements Runnable {

    final Logger logger = LoggerFactory.getLogger("TopicCreation.class");
    private final String topicName;
    private final int nbrOfPartition;
    private final int nbrOfReplication;
    private final String zookeeperHost;
    private ZkClient zkClient;

    public TopicCreation(String TopicName, int NbrOfPartition, int NbrOfReplication,String zookeeper) {
        this.topicName = TopicName;
        this.nbrOfPartition = NbrOfPartition;
        this.nbrOfReplication = NbrOfReplication;
        this.zookeeperHost=zookeeper;

        zkClient = new ZkClient(zookeeperHost,
                1000,
                100,
                new ZkSerializer() {
                    @Override
                    public byte[] serialize(Object data) {
                        return ZKStringSerializer.serialize(data);
                    }
                    @Override
                    public Object deserialize(byte[] bytes) {
                        return ZKStringSerializer.deserialize(bytes);
                    }
                });
    }

    public void run() {
        ZkUtils zkUtils ;
            zkUtils = new ZkUtils(zkClient, new ZkConnection(zookeeperHost), false);
           Properties topicConfiguration = new Properties();

        //we handle the fact that the topic can already exist and if it's the case we can try tu update it
           if(!AdminUtils.topicExists(zkUtils,topicName)){
               try {
                       AdminUtils.createTopic(zkUtils, topicName, nbrOfPartition, nbrOfReplication, topicConfiguration, RackAwareMode.Disabled$.MODULE$);
                       //just to print a bit of setting in the console
                       logger.info("######## \n we created the new TOPIC :" + topicName + ", it has " +
                               nbrOfPartition + " partitions, and " + nbrOfReplication + " replication \n #######");
                   } catch (TopicExistsException ex) {
                       logger.error("####### \n the topic " + topicName + " already exist  \n #######");
                   } finally {
                   if (zkClient != null) {
                       zkClient.close();
                   }
               }
           } else {
               logger.info("########Topic exists. name: {}", topicName);
               if (nbrOfPartition > getNumPartitions()) { //we can only increase the partition factor
                   try {
                       AdminUtils.addPartitions(zkUtils, topicName, nbrOfPartition, "", true,RackAwareMode.Disabled$.MODULE$);
                       logger.info("########Topic altered. name: {}, partitions: {}", topicName, nbrOfPartition);
                   } catch (AdminOperationException e) {
                       logger.error("########Failed to add partitions########", e);
                   }
               }else{
                   logger.info("########The Topic have the same configuration");
                }
           }
    }

    public  int getNumPartitions() {
        ZkUtils zkUtils = new ZkUtils(zkClient, new ZkConnection(zookeeperHost), false);
        Map<TopicAndPartition, Seq<Object>> existingPartitionsReplicaList =
                JavaConverters.mapAsJavaMapConverter(
                        zkUtils.getReplicaAssignmentForTopics(
                                JavaConverters.asScalaBufferConverter(Lists.newArrayList(topicName)).asScala()
                                        .toSeq())).asJava();
        return existingPartitionsReplicaList.size();
    }

}
