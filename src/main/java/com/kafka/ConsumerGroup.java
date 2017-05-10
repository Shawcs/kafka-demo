package com.kafka;

import com.google.common.collect.Lists;
import kafka.common.TopicAndPartition;
import kafka.utils.ZKStringSerializer;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.I0Itec.zkclient.serialize.ZkSerializer;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.collection.JavaConverters;
import scala.collection.Seq;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created by aturbillon on 07/04/2017.
 */
public class ConsumerGroup implements Watcher{

    private final int numberOfConsumers;
    private final String groupId;
    private final String topic;
    private final String brokers;
    private List<ConsumerTopic> consumers;
    private final String ZookeeperId;

    final Logger logger = LoggerFactory.getLogger("ConsumerGroup.class");

    public ConsumerGroup(String brokers, String groupId, String topic,String zookeeper) {
        this.brokers = brokers;
        this.topic = topic;
        this.groupId = groupId;
        this.ZookeeperId=zookeeper;
        this.numberOfConsumers = getNumPartitions() - 1; //we put -1 because we want less consumer than the number of partition
        consumers = new ArrayList<>();

        for (int i = 0; i < this.numberOfConsumers; i++) {
            ConsumerTopic ncThread =
                    new ConsumerTopic(this.brokers, this.groupId, this.topic); //i will specify witch partition we read
            consumers.add(ncThread);
        }
    }

    public void execute() {
        for (ConsumerTopic ncThread : consumers) {
            Thread t = new Thread(ncThread);
            t.start();
        }
    }
    /**
     * @return the numberOfPartition
     */
    public  int getNumPartitions() {
        Properties properties = new Properties();
        InputStream inputStream = MainProducerConsumerPartition.class.getClassLoader()
                .getResourceAsStream("kafka_broker.properties");
        try {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String zookeeper = String.valueOf(properties.get("ZOOKEEPER"));
       ZkClient zkClient = new ZkClient(zookeeper,
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

        ZkUtils zkUtils = new ZkUtils(zkClient, new ZkConnection(zookeeper), false);
        Map<TopicAndPartition, Seq<Object>> existingPartitionsReplicaList =
                JavaConverters.mapAsJavaMapConverter(
                        zkUtils.getReplicaAssignmentForTopics(
                                JavaConverters.asScalaBufferConverter(Lists.newArrayList(topic)).asScala()
                                        .toSeq())).asJava();
        return existingPartitionsReplicaList.size();
    }

    //TODO this is not working in my distant server. I think it's because of slow response from zookeeper
    //useful functions to get information from zookeeper
    public List<String> getBorkerListConsumer() {
        try {
            ZooKeeper zk = new ZooKeeper(ZookeeperId, 3000, null, false);
            String zkNodeName = "/brokers/ids";
           return zk.getChildren(zkNodeName, false);
        } catch (IOException|InterruptedException e) {
            logger.error("In/out exception "+e);
        } catch (KeeperException e) {
            logger.error("Zookeeper error "+e);
        }
        return Collections.emptyList();
    }

    //useful functions to get information from zookeeper
    public List<String> getTopicListConsumer() {
        ZooKeeper zk ;
        try {
            zk = new ZooKeeper(ZookeeperId, 3000, null, false);
            String zkNodeName = "/brokers/topics";
            return  zk.getChildren(zkNodeName, false);
        } catch (IOException|InterruptedException e) {
            logger.error("exception "+e);
        }
        catch (KeeperException ez) {
            logger.error("Zookeeper error "+ez);
        }

        return Collections.emptyList();
    }

    /**
     * @return the groupId
     */
    public String getGroupId() {
        return groupId;
    }

    @Override
    public void process(WatchedEvent event) {


    }
}

