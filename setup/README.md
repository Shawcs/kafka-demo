 # Prerequisite 
- ubuntu or other unix server in 64 bit 
- Java sdk 1.8 64 bit or more  http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html
- kafka  https://kafka.apache.org/downloads

# Note about the following configuration

We will deploy Zookeeper and Kafka server. Our goal is to be fault tolerant, be highly scalable
and reliable. For the we have to launch several kafka servers to create data replication to be
fault tolerant on kafka server error and we have several Zookeeper server too, inorder to be
fault tolerant on zookeeper server. Keep in mind that if Zookeeper go completely down kafka will
fall with him. The fault tolerance is not the same in Zookeeper and Kafka. To be fault tolerant in
Zookeeper you need at least 3 server (a MASTER and two WORKER). With this minimum configuration you are allowed to lose 
at most one Zookeeper server (MASTER remain one WORKER). The recommended configuration is
five Zookeeper server to be safe (see performance and reliability section https://zookeeper.apache.org/doc/trunk/zookeeperOver.html).
In the following config you will see that all the server are in the same place (10.23.75.126) this
is of course for test purpose because every single running instance should be in a different server
rack.

 # Configuration of zookeeper 
 Help: 	
- https://myjeeva.com/zookeeper-cluster-setup.html 
- https://zookeeper.apache.org/doc/r3.1.2/zookeeperAdmin.html#sc_zkMulitServerSetup

Create the directory
```
mkdir -p /home/kafka/kafka_2.10-0.10.2.0/zookeeper/data/zk1 /home/kafka/kafka_2.10-0.10.2.0/zookeeper/data/zk2 /home/kafka/kafka_2.10-0.10.2.0/zookeeper/data/zk3 /home/kafka/kafka_2.10-0.10.2.0/zookeeper/data/zk4 /home/kafka/kafka_2.10-0.10.2.0/zookeeper/data/zk5
mkdir -p /home/kafka/kafka_2.10-0.10.2.0/zookeeper/log/zk1 /home/kafka/kafka_2.10-0.10.2.0/zookeeper/log/zk2 /home/kafka/kafka_2.10-0.10.2.0/zookeeper/log/zk3 /home/kafka/kafka_2.10-0.10.2.0/zookeeper/log/zk4 /home/kafka/kafka_2.10-0.10.2.0/zookeeper/log/zk5
```
Create the id file
```
cd /home/kafka/kafka_2.10-0.10.2.0/zookeeper/data/zk1 && echo "1" > myid && 
cd /home/kafka/kafka_2.10-0.10.2.0/zookeeper/data/zk2 && echo "2" > myid &&
cd /home/kafka/kafka_2.10-0.10.2.0/zookeeper/data/zk3 && echo "3" > myid &&
cd /home/kafka/kafka_2.10-0.10.2.0/zookeeper/data/zk4 && echo "4" > myid &&
cd /home/kafka/kafka_2.10-0.10.2.0/zookeeper/data/zk5 && echo "5" > myid 
```

# Creation of configuration file for zookeeper
### File 1
```
echo "
# the directory where the snapshot is stored.
dataDir=/home/kafka/kafka_2.10-0.10.2.0/zookeeper/data/zk1
# the directory where log are located
dataLogDir=/home/kafka/kafka_2.10-0.10.2.0/zookeeper/log/zk1

# the port at which the clients will connect
clientPort=2181

minSessionTimeout=600
maxSessionTimeout=3000

# Amount of time, in ticks, to allow followers to connect and sync to a leader. Increased this value as needed, if the amount of data managed by ZooKeeper is large.
initLimit=10 
# Amount of time, in ticks, to allow followers to sync with ZooKeeper. If followers fall too far behind a leader, they will be dropped.
syncLimit=2 

server.1=10.23.75.126:2888:3888
server.2=10.23.75.126:2889:3889
server.3=10.23.75.126:2890:3890

# disable the per-ip limit on the number of connections since this is a non-production config
#maxClientCnxns=0 " > zookeeper1.properties
```
### File 2
```
echo "
# the directory where the snapshot is stored.
dataDir=/home/kafka/kafka_2.10-0.10.2.0/zookeeper/data/zk2
# the directory where log are located
dataLogDir=/home/kafka/kafka_2.10-0.10.2.0/zookeeper/log/zk2

# the port at which the clients will connect
clientPort=2182

minSessionTimeout=600
maxSessionTimeout=3000

# Amount of time, in ticks, to allow followers to connect and sync to a leader. Increased this value as needed, if the amount of data managed by ZooKeeper is large.
initLimit=10 
# Amount of time, in ticks, to allow followers to sync with ZooKeeper. If followers fall too far behind a leader, they will be dropped.
syncLimit=2 

server.1=10.23.75.126:2888:3888
server.2=10.23.75.126:2889:3889
server.3=10.23.75.126:2890:3890

# disable the per-ip limit on the number of connections since this is a non-production config
#maxClientCnxns=0 " > zookeeper2.properties
```
### File 3
```
echo "
# the directory where the snapshot is stored.
dataDir=/home/kafka/kafka_2.10-0.10.2.0/zookeeper/data/zk3
# the directory where log are located
dataLogDir=/home/kafka/kafka_2.10-0.10.2.0/zookeeper/log/zk3

# the port at which the clients will connect
clientPort=2183

minSessionTimeout=600
maxSessionTimeout=3000

# Amount of time, in ticks, to allow followers to connect and sync to a leader. Increased this value as needed, if the amount of data managed by ZooKeeper is large.
initLimit=10 
# Amount of time, in ticks, to allow followers to sync with ZooKeeper. If followers fall too far behind a leader, they will be dropped.
syncLimit=2 

server.1=10.23.75.126:2888:3888
server.2=10.23.75.126:2889:3889
server.3=10.23.75.126:2890:3890

# disable the per-ip limit on the number of connections since this is a non-production config
#maxClientCnxns=0 " > zookeeper3.properties
```

End of the creation of conf file for zookeeper 

# configuration file for kafka brokers
### File 1
```
cd /home/kafka/kafka_2.10-0.10.2.0/config
echo "

# see kafka.server.KafkaConfig for additional details and defaults

############################# Server Basics #############################

# The id of the broker. This must be set to a unique integer for each broker.
broker.id=1

# Switch to enable topic deletion or not, default value is false
delete.topic.enable=true

############################# Socket Server Settings #############################

# The address the socket server listens on. It will get the value returned from 
# java.net.InetAddress.getCanonicalHostName() if not configured.
#   FORMAT:
#     listeners = listener_name://host_name:port
#   EXAMPLE:
#     listeners = PLAINTEXT://your.host.name:9092
listeners=PLAINTEXT://10.23.75.126:9092

# Hostname and port the broker will advertise to producers and consumers. If not set, 
# it uses the value for "listeners" if configured.  Otherwise, it will use the value
# returned from java.net.InetAddress.getCanonicalHostName().
advertised.listeners=PLAINTEXT://10.23.75.126:9092


# Maps listener names to security protocols, the default is for them to be the same. See the config documentation for more details
#listener.security.protocol.map=PLAINTEXT:PLAINTEXT,SSL:SSL,SASL_PLAINTEXT:SASL_PLAINTEXT,SASL_SSL:SASL_SSL

# The number of threads handling network requests
num.network.threads=3

# The number of threads doing disk I/O
num.io.threads=8

# The send buffer (SO_SNDBUF) used by the socket server
socket.send.buffer.bytes=102400

# The receive buffer (SO_RCVBUF) used by the socket server
socket.receive.buffer.bytes=102400

# The maximum size of a request that the socket server will accept (protection against OOM)
socket.request.max.bytes=104857600

port=9092
############################# Log Basics #############################

# A comma seperated list of directories under which to store log files
log.dirs=/tmp/kafka-logs-1

# The default number of log partitions per topic. More partitions allow greater
# parallelism for consumption, but this will also result in more files across
# the brokers.
num.partitions=3

# The number of threads per data directory to be used for log recovery at startup and flushing at shutdown.
# This value is recommended to be increased for installations with data dirs located in RAID array.
num.recovery.threads.per.data.dir=1

############################# Log Flush Policy #############################

# Messages are immediately written to the filesystem but by default we only fsync() to sync
# the OS cache lazily. The following configurations control the flush of data to disk.
# There are a few important trade-offs here:
#    1. Durability: Unflushed data may be lost if you are not using replication.
#    2. Latency: Very large flush intervals may lead to latency spikes when the flush does occur as there will be a lot of data to flush.
#    3. Throughput: The flush is generally the most expensive operation, and a small flush interval may lead to exceessive seeks.
# The settings below allow one to configure the flush policy to flush data after a period of time or
# every N messages (or both). This can be done globally and overridden on a per-topic basis.

# The number of messages to accept before forcing a flush of data to disk
#log.flush.interval.messages=10000

# The maximum amount of time a message can sit in a log before we force a flush
#log.flush.interval.ms=1000

############################# Log Retention Policy #############################

# The following configurations control the disposal of log segments. The policy can
# be set to delete segments after a period of time, or after a given size has accumulated.
# A segment will be deleted whenever *either* of these criteria are met. Deletion always happens
# from the end of the log.

# The minimum age of a log file to be eligible for deletion due to age
log.retention.hours=168

# A size-based retention policy for logs. Segments are pruned from the log as long as the remaining
# segments don't drop below log.retention.bytes. Functions independently of log.retention.hours.
#log.retention.bytes=1073741824

# The maximum size of a log segment file. When this size is reached a new log segment will be created.
log.segment.bytes=1073741824

# The interval at which log segments are checked to see if they can be deleted according
# to the retention policies
log.retention.check.interval.ms=300000

############################# Zookeeper #############################

# Zookeeper connection string (see zookeeper docs for details).
# This is a comma separated host:port pairs, each corresponding to a zk
# server. e.g. "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002".
# You can also append an optional chroot string to the urls to specify the
# root directory for all kafka znodes.
zookeeper.connect=10.23.75.126:2181,10.23.75.126:2182,10.23.75.126:2183

# Timeout in ms for connecting to zookeeper
zookeeper.connection.timeout.ms=6000
" > server1.properties
```
### File 2
```
echo "
# see kafka.server.KafkaConfig for additional details and defaults

############################# Server Basics #############################

# The id of the broker. This must be set to a unique integer for each broker.
broker.id=2

# Switch to enable topic deletion or not, default value is false
delete.topic.enable=true

############################# Socket Server Settings #############################

# The address the socket server listens on. It will get the value returned from 
# java.net.InetAddress.getCanonicalHostName() if not configured.
#   FORMAT:
#     listeners = listener_name://host_name:port
#   EXAMPLE:
#     listeners = PLAINTEXT://your.host.name:9092
listeners=PLAINTEXT://10.23.75.126:9093

# Hostname and port the broker will advertise to producers and consumers. If not set, 
# it uses the value for "listeners" if configured.  Otherwise, it will use the value
# returned from java.net.InetAddress.getCanonicalHostName().
#advertised.listeners=PLAINTEXT://your.host.name:9092
advertised.listeners=PLAINTEXT://10.23.75.126:9093
# Maps listener names to security protocols, the default is for them to be the same. See the config documentation for more details
#listener.security.protocol.map=PLAINTEXT:PLAINTEXT,SSL:SSL,SASL_PLAINTEXT:SASL_PLAINTEXT,SASL_SSL:SASL_SSL

# The number of threads handling network requests
num.network.threads=3

# The number of threads doing disk I/O
num.io.threads=8

# The send buffer (SO_SNDBUF) used by the socket server
socket.send.buffer.bytes=102400

# The receive buffer (SO_RCVBUF) used by the socket server
socket.receive.buffer.bytes=102400

# The maximum size of a request that the socket server will accept (protection against OOM)
socket.request.max.bytes=104857600

port=9093
############################# Log Basics #############################

# A comma seperated list of directories under which to store log files
log.dirs=/tmp/kafka-logs-2

# The default number of log partitions per topic. More partitions allow greater
# parallelism for consumption, but this will also result in more files across
# the brokers.
num.partitions=3

# The number of threads per data directory to be used for log recovery at startup and flushing at shutdown.
# This value is recommended to be increased for installations with data dirs located in RAID array.
num.recovery.threads.per.data.dir=1

############################# Log Flush Policy #############################

# Messages are immediately written to the filesystem but by default we only fsync() to sync
# the OS cache lazily. The following configurations control the flush of data to disk.
# There are a few important trade-offs here:
#    1. Durability: Unflushed data may be lost if you are not using replication.
#    2. Latency: Very large flush intervals may lead to latency spikes when the flush does occur as there will be a lot of data to flush.
#    3. Throughput: The flush is generally the most expensive operation, and a small flush interval may lead to exceessive seeks.
# The settings below allow one to configure the flush policy to flush data after a period of time or
# every N messages (or both). This can be done globally and overridden on a per-topic basis.

# The number of messages to accept before forcing a flush of data to disk
#log.flush.interval.messages=10000

# The maximum amount of time a message can sit in a log before we force a flush
#log.flush.interval.ms=1000

############################# Log Retention Policy #############################

# The following configurations control the disposal of log segments. The policy can
# be set to delete segments after a period of time, or after a given size has accumulated.
# A segment will be deleted whenever *either* of these criteria are met. Deletion always happens
# from the end of the log.

# The minimum age of a log file to be eligible for deletion due to age
log.retention.hours=168

# A size-based retention policy for logs. Segments are pruned from the log as long as the remaining
# segments don't drop below log.retention.bytes. Functions independently of log.retention.hours.
#log.retention.bytes=1073741824

# The maximum size of a log segment file. When this size is reached a new log segment will be created.
log.segment.bytes=1073741824

# The interval at which log segments are checked to see if they can be deleted according
# to the retention policies
log.retention.check.interval.ms=300000

############################# Zookeeper #############################

# Zookeeper connection string (see zookeeper docs for details).
# This is a comma separated host:port pairs, each corresponding to a zk
# server. e.g. "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002".
# You can also append an optional chroot string to the urls to specify the
# root directory for all kafka znodes.
zookeeper.connect=10.23.75.126:2181,10.23.75.126:2182,10.23.75.126:2183

# Timeout in ms for connecting to zookeeper
zookeeper.connection.timeout.ms=6000 " > server2.properties
```
### File 3
```
echo "
# see kafka.server.KafkaConfig for additional details and defaults

############################# Server Basics #############################

# The id of the broker. This must be set to a unique integer for each broker.
broker.id=3

# Switch to enable topic deletion or not, default value is false
delete.topic.enable=true

############################# Socket Server Settings #############################

# The address the socket server listens on. It will get the value returned from 
# java.net.InetAddress.getCanonicalHostName() if not configured.
#   FORMAT:
#     listeners = listener_name://host_name:port
#   EXAMPLE:
#     listeners = PLAINTEXT://your.host.name:9092
listeners=PLAINTEXT://10.23.75.126:9094

# Hostname and port the broker will advertise to producers and consumers. If not set, 
# it uses the value for "listeners" if configured.  Otherwise, it will use the value
# returned from java.net.InetAddress.getCanonicalHostName().
#advertised.listeners=PLAINTEXT://your.host.name:9092
advertised.listeners=PLAINTEXT://10.23.75.126:9094

# Maps listener names to security protocols, the default is for them to be the same. See the config documentation for more details
#listener.security.protocol.map=PLAINTEXT:PLAINTEXT,SSL:SSL,SASL_PLAINTEXT:SASL_PLAINTEXT,SASL_SSL:SASL_SSL

# The number of threads handling network requests
num.network.threads=3

# The number of threads doing disk I/O
num.io.threads=8

# The send buffer (SO_SNDBUF) used by the socket server
socket.send.buffer.bytes=102400

# The receive buffer (SO_RCVBUF) used by the socket server
socket.receive.buffer.bytes=102400

# The maximum size of a request that the socket server will accept (protection against OOM)
socket.request.max.bytes=104857600

port=9094
############################# Log Basics #############################

# A comma seperated list of directories under which to store log files

log.dirs=/tmp/kafka-logs-3

# The default number of log partitions per topic. More partitions allow greater
# parallelism for consumption, but this will also result in more files across
# the brokers.
num.partitions=3

# The number of threads per data directory to be used for log recovery at startup and flushing at shutdown.
# This value is recommended to be increased for installations with data dirs located in RAID array.
num.recovery.threads.per.data.dir=1

############################# Log Flush Policy #############################

# Messages are immediately written to the filesystem but by default we only fsync() to sync
# the OS cache lazily. The following configurations control the flush of data to disk.
# There are a few important trade-offs here:
#    1. Durability: Unflushed data may be lost if you are not using replication.
#    2. Latency: Very large flush intervals may lead to latency spikes when the flush does occur as there will be a lot of data to flush.
#    3. Throughput: The flush is generally the most expensive operation, and a small flush interval may lead to exceessive seeks.
# The settings below allow one to configure the flush policy to flush data after a period of time or
# every N messages (or both). This can be done globally and overridden on a per-topic basis.

# The number of messages to accept before forcing a flush of data to disk
#log.flush.interval.messages=10000

# The maximum amount of time a message can sit in a log before we force a flush
#log.flush.interval.ms=1000

############################# Log Retention Policy #############################

# The following configurations control the disposal of log segments. The policy can
# be set to delete segments after a period of time, or after a given size has accumulated.
# A segment will be deleted whenever *either* of these criteria are met. Deletion always happens
# from the end of the log.

# The minimum age of a log file to be eligible for deletion due to age
log.retention.hours=168

# A size-based retention policy for logs. Segments are pruned from the log as long as the remaining
# segments don't drop below log.retention.bytes. Functions independently of log.retention.hours.
#log.retention.bytes=1073741824

# The maximum size of a log segment file. When this size is reached a new log segment will be created.
log.segment.bytes=1073741824

# The interval at which log segments are checked to see if they can be deleted according
# to the retention policies
log.retention.check.interval.ms=300000

############################# Zookeeper #############################

# Zookeeper connection string (see zookeeper docs for details).
# This is a comma separated host:port pairs, each corresponding to a zk
# server. e.g. "127.0.0.1:3000,127.0.0.1:3001,127.0.0.1:3002".
# You can also append an optional chroot string to the urls to specify the
# root directory for all kafka znodes.
zookeeper.connect=10.23.75.126:2181,10.23.75.126:2182,10.23.75.126:2183

# Timeout in ms for connecting to zookeeper
zookeeper.connection.timeout.ms=6000" > server3.properties
```

# Tuning of the JVM
The JVM can run out of memory if you have some zookeeper/kafka instances running 
this depend on the work load of the servers. It's recommended to run some test on the machine 
to define your needs  (help  http://blog.ippon.fr/2013/11/15/cas-pratique-de-tuning-jvm-dune-application/)
go to kafka/bin and edit the file zookeeper_server_start.sh
modify the export KAFKA_HEAP_OPTS part

- GC = garbage collector
- Xms Setting minimum heap size
- Xmx Setting maximum heap size
- XX+use force to use garbage collector G1GC
- XX MaxGCPauseMillis Sets a target for the maximum GC pause time. This is a soft goal, and the JVM will make its best effort to achieve it.
- more info here https://docs.oracle.com/cd/E40972_01/doc.70/e40973/cnf_jvmgc.htm#autoId2

```
vi kafka/bin/zookeeper-server-start.sh

 export KAFKA_HEAP_OPTS="-Xms2g -Xmx2g -XX:MetaspaceSize=96m -XX:+UseG1GC -XX:MaxGCPauseMillis=20 -XX:InitiatingHeapOccupancyPercent=35 -XX:G1HeapRegionSize=16M -XX:MinMetaspaceFreeRatio=50 -XX:MaxMetaspaceFreeRatio=80"
```
Same for the file kafka-server-start.sh
```
vi kafka/bin/kafka-server-start.sh
export KAFKA_HEAP_OPTS="-Xms1g -Xmx1g -XX:MetaspaceSize=96m -XX:+UseG1GC -XX:MaxGCPauseMillis=20 -XX:InitiatingHeapOccupancyPercent=35 -XX:G1HeapRegionSize=16M -XX:MinMetaspaceFreeRatio=50 -XX:MaxMetaspaceFreeRatio=80"
 ```
The modification of the file descriptor is also recommended because kafka need a large number of socket and read operation. 
More detail in the confluent documentation here http://docs.confluent.io/2.0.1/kafka/deployment.html#file-descriptors-and-mmap
to do this with an ubuntu distribution : https://underyx.me/2015/05/18/raising-the-maximum-number-of-file-descriptors
walk-through the doc 

```
ulimit -n #give the size of the file descriptor
```
To update it
```
vi /etc/security/limits.conf
```
Add
```
*softnofile 10000 #the value is up to you, if we follow confluent documentation they say 100 000 is a good number for a big cluster
*hard nofile 10000
#or this if you want to specify a user
{user} soft nofile 10000
{user} hard nofile 10000
```
Update 
```
vi /etc/pam.d/common-session
```
Add
```
session required pam_limits.so
```
Last one
```
vi /etc/pam.d/common-session-noninteractive 
```
Add
```
session required pam_limits.so
```
Reboot
```
sudo reboot
```
Do again 
```
ulimit -n
#here you can see the value you have just set before so that worked ! if not do it again
```

# Launching servers + kafka tools

Go to your kafka directory
```
cd /home/kafka/kafka_2.10-0.10.2.0
```
Launch zookeeper servers
```
bin/zookeeper-server-start.sh config/zookeeper1.properties
bin/zookeeper-server-start.sh config/zookeeper2.properties
bin/zookeeper-server-start.sh config/zookeeper3.properties
```
See process 
```
jps
```

Launch kafka servers
```
bin/kafka-server-start.sh config/server1.properties
bin/kafka-server-start.sh config/server2.properties
bin/kafka-server-start.sh config/server3.properties
```
List all topic 
```
bin/kafka-topics.sh --zookeeper 10.23.75.126:2181 --list
```
Describe a topic
```
bin/kafka-topics.sh --zookeeper 10.23.75.126:2181 --describe --topic Ticket
```
Alternative way from the logs
```
tree /tmp/kafka-logs-{1,2,3}
```
Create a topics
```
bin/kafka-topics.sh --zookeeper 10.23.75.126:2181 --create --topic test --partitions 3 --replication-factor 2

bin/kafka-topics.sh --zookeeper 10.23.75.126:2181 --create --topic summary-markers --partitions 3 --replication-factor 2
```
Write to a topic
```
 bin/kafka-console-producer.sh --broker-list 10.23.75.126:9092,10.23.75.126:9093,10.23.75.126:9094 --topic Ticket
 ```
 
Read from a topic
```
bin/kafka-console-consumer.sh --bootstrap-server 10.23.75.126:9092,10.23.75.126:9093,10.23.75.126:9094 --topic Ticket  --from-beginning
bin/kafka-console-consumer.sh --bootstrap-server 10.23.75.126:9092,10.23.75.126:9093,10.23.75.126:9094 --topic Fake_Data  --from-beginning
```
Inspect a consumer group
```
bin/kafka-consumer-groups.sh -new-consumer -describe -group test -bootstrap-server 10.23.75.126:9092,10.23.75.126:9093,10.23.75.126:9094,10.23.75.179:9095
```
Delete topic you could have to wait before the deletion is effective
```
bin/kafka-topics.sh --zookeeper 10.23.75.126:2181 --delete --topic Ticket
```
To read the result of the kafka stream error count 
```
bin/kafka-console-consumer.sh --topic WordResult --from-beginning \
                             --new-consumer --bootstrap-server 10.23.75.126:9092,10.23.75.126:9093,10.23.75.126:9094 \
                              --property print.key=true \
                              --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer
```
	