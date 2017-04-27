package com.kafka;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by aturbillon on 07/04/2017.
 */
public class ConsumerGroup {

    private final int numberOfConsumers;
    private final String groupId;
    private final String topic;
    private final String brokers;
    private List<ConsumerTopic> consumers;
    private static  final String ZookeeperId="localhost:2181";

    final Logger logger = LoggerFactory.getLogger(ConsumerGroup.class);

    public ConsumerGroup(String brokers, String groupId, String topic) {
        this.brokers = brokers;
        this.topic = topic;
        this.groupId = groupId;
        this.numberOfConsumers = getNumberOfPartition() - 1; //we put -1 because we want less consumer than the number of partition
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

    public int getNumberOfPartition() {

        try {
            ZooKeeper zk = new ZooKeeper(ZookeeperId, 1000, null, false);
            String zkNodeName = "/brokers/topics/" + topic + "/partitions";
            try {
                return  zk.getChildren(zkNodeName, false).size();
            } catch (KeeperException|InterruptedException e ) {
                logger.debug("trouble with zookeeper "+e);
            }
        }catch (IOException e) {
            logger.debug("In/out exception "+e);
        }
        return 0;
    }

    //useful functions to get information from zookeeper
    public List<String> getBorkerList() {
        try {
            ZooKeeper zk = new ZooKeeper(ZookeeperId, 1000, null, false);
            String zkNodeName = "/brokers/ids";
           return zk.getChildren(zkNodeName, false);
        } catch (IOException|InterruptedException e) {
            logger.debug("In/out exception "+e);
        } catch (KeeperException e) {
            logger.debug("Zookeeper error "+e);
        }
        return Collections.emptyList();
    }

    //useful functions to get information from zookeeper
    public List<String> getTopicList() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(ZookeeperId, 1000, null, false);
            String zkNodeName = "/brokers/topics";
            return  zk.getChildren(zkNodeName, false);
        } catch (IOException|InterruptedException e) {
            logger.debug("exception "+e);
        }
        catch (KeeperException ez) {
            logger.debug("Zookeeper error "+ez);
        }

        return Collections.emptyList();
    }

    /**
     * @return the groupId
     */
    public String getGroupId() {
        return groupId;
    }
}

