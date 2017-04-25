package com.kafka;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.ArrayList;
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
            ZooKeeper zk = new ZooKeeper("localhost:2181", 1000, null, false);
            String zkNodeName = "/brokers/topics/" + topic + "/partitions";
            try {
                int numPartiton = zk.getChildren(zkNodeName, false).size();
                return numPartiton;
            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //useful functions to get information from zookeeper
    public List<String> getBorkerList() {
        try {
            ZooKeeper zk = new ZooKeeper("localhost:2181", 1000, null, false);
            String zkNodeName = "/brokers/ids";
            List<String> Brokers = zk.getChildren(zkNodeName, false);
            return Brokers;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }
        return null;
    }

    //useful functions to get information from zookeeper
    public List<String> getTopicList() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper("localhost:2181", 1000, null, false);
            String zkNodeName = "/brokers/topics";
            List<String> TopicsList = zk.getChildren(zkNodeName, false);
            return TopicsList;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @return the groupId
     */
    public String getGroupId() {
        return groupId;
    }
}

