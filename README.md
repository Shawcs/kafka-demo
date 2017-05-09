
# Project goal

This project use kafka to proceed messages coming from a file into kafka topics. Those messages are JSON schema that we serialize to java object (or not)
 in order to store them or a part of them in a Cassandra DB with kafka consumers.


## Pre-requisites

For more information go here https://github.com/Shawcs/kafka-demo/tree/master/setup 
(you really should read this before)

You need a linux based OS. This code have been developed on an Ubuntu OS.

Download all this git repo and install kafka 0.10.2.0 at least

All the rest of the project
```
$ git clone https://github.com/Shawcs/kafka-demo.git
$ cd kafka_cassandra
```

### Step 1: Strat servers

Start a ZooKeeper server on a terminal.

```
$ cd kafka_2.1.10-0.10.2.0
$ bin/zookeeper-server-start.sh config/zookeeper.properties
```

Now start 3 Kafka broker in different console:

```
$ bin/kafka-server-start.sh config/server1.properties
$ bin/kafka-server-start.sh config/server2.properties
$ bin/kafka-server-start.sh config/server3.properties
```


### Step 2: Basic commande for Kafka

see running procees

```
$ apt get-install jps (if you don't have jsp installed)
$ jps
```

See the kafka topics
```
$ bin/kafka-topics.sh --zookeeper localhost:2181 --list
```
if nothing is returned that means you have no topic

describe a Topic if you need to know the setting of the topic (replication,master ...)
```
$ bin/kafka-topics.sh --zookeeper localhost:2181 --describe --topic TOPIC_NAME
```
We will see more command later

## Now for the real fun

At this point, you should have a working Kafka broker running on your
machine. The next steps are to launch the project

### Step 3: Looking the code

There is several point to pay attention in the project code. First of all the Properties that we will set to Kafka and to our topics.
All the prop are hard coded in the Producers/Consumers Classes but you also can use the properties files located in ressources folder.
You must focus your intention in The bootstrapping servers list, It must fit with your server.props when you launch the kafka brokers.
Moreover Topics,ands groupId are important variable that you must set.



### Step 4: Run the Main class

Now we have a classe named "MainProducerConsumer". This will create a topic named Ticket with for partition and a replication factor of 2 in our 3 Kafka brocker. And then it will send the content of Data_user in the topic Ticket. Then it will read all the partition one by one as it is specified in the code. The running task will now wait for new messages to come in the topic Ticket.

### Step 6: Produce more information

Now you can launch (without stoping the other one) the classe MainProceMore. This will take the content of More_Data_user and write them into the topic Ticket.

## Cleaning Up

When you are done playing, stop all your Kafka servers and after Zookeeper. Kill Zookeeper in last if you don't kafka broker will go trought some trouble to stop properly. If you want to delete all trace of use you can delete the
data directories they were using and writing from /tmp

```
$ rm -rf /tmp/zookeeper/version-2/log.1  ; rm -rf /tmp/kafka-*
```
# fault tolerance test

The interesting thing with setting up a cluster ( here 3 broker in a single node with a single zookeeper) is that a broker can go down (1, 2 or 3 whatever) we still can write and read from all the partition all the same as if all the broker were up.
To check that you can stop a broker and running again MainProduceMore. You will see that you get all the information and get no loss. If you wake the broker up again everything will come back to normal all the data will be moved to the broker and you will find a perfect working cluster with no data loss.

##Tips
to see better what is happening in the broker you can open 3 new terminal and do the following:
in the first one you want the information about the topic (replication, synchronisation ..)
```
bin/kafka-topics.sh --zookeeper localhost:2181 --describe --topic Ticket
```

in the next one you want to consume the topic to get all the message in the console
```
bin/kafka-console-consumer.sh --bootstrap-server localhost:9092,localhost:9093,localhost:9094 --topic Ticket  --from-beginning
```
in another one you will have a producer like this you can produce data directly from the console. To keep track of what you are doing. For instance when you kill the broker you can type "broker X killed"
and you will see if you got the message every where.
```
bin/kafka-console-producer.sh  --broker-list localhost:9092,localhost:9093,localhost:9094 --topic Ticket
```

# Links to get more information

Kafka/zookeeper setup
```
http://www.michael-noll.com/blog/2013/03/13/running-a-multi-broker-apache-kafka-cluster-on-a-single-node/
```
Kafka consumer API 0.10.2 detailed information from confluent
```
https://www.confluent.io/blog/tutorial-getting-started-with-the-new-apache-kafka-0-10-consumer-client/
```
Kafka documentation
```
https://kafka.apache.org/quickstart
```
Kafka threading
```
http://howtoprogram.xyz/2016/05/29/create-multi-threaded-apache-kafka-consumer/
```

Kafka official documentation
```
https://kafka.apache.org/090/javadoc/org/apache/kafka/clients/producer/KafkaProducer.html
```
Example of insert to cassandra, payload, all tools for cassandra writing
```
https://github.com/datastax/java-driver
```
Connector for mongoDB for the evolution of the solution
```
https://github.com/DataReply/kafka-connect-mongodb
```

# TODO

We could monitor our thread because for the moment the code is not 100% thread safe. They are not handed properly even if there is no reason that we face thread race condition.

Do some benchmark on the code and then some optimisation

The other thing to do is to take ca re of the logs files. Due to the Nohup command and to kafka in general we create a lot of log in logs directory
we should pay more attention to kafka and zookeeper log size setting to decrease the logs size on disc. This to avoid memory issues to fast.
If we keep using Nohup and we are concern about the files size, logrotate seems to be a good tool to work with.