
# Partition number
Tip: You can decide the number of partitions for your Kafka topic with the general rule of thumb shown below:

Partition count ≧ consumer instance count

Daily/seasonal off-peak hours: partition count > consumer instance count

Daily/seasonal peak hours: partition count = consumer instance count

Moreover, the number of partitions should be linked to the business growth forecast.
>[source](https://www.confluent.io/blog/kafka-clusters-and-cluster-management/#:~:text=not%20auto%2Dscalable.-,An%20aside,-Tip%3A%20You%20can)

Limits: single cluster is 200,000 partitions or 4,000 partitions per broker for zookeeper installation. In kraft even with 2 millions partitions no performance change observed 
>[source](https://www.confluent.io/blog/kafka-without-zookeeper-a-sneak-peek/#scaling-up)

# Cluster, when do we need more ?

As much as possible, keep or aggregate all of your business-critical data in the same cluster—this facilitates faster business insights and timely action

Consider a second cluster for non-mission-critical data like data from internal corporate systems (HR, finance, or operations)
Compliance: For example, PCI DSS requires a Chinese wall between payment card data and other product data, so you will end up using separate clusters.

Doing business in multiple geographic regions: It is usually best for cost, latency, and development time to have a Kafka cluster available in every region where applications will be running.

Disaster recovery (DR): Setting up a disaster recovery cluster in a separate cloud or geographic region so that your business can continue operations even during a regional outage.

Allowing individual lines of business to operate and grow their Kafka deployments independently, with the scale, administration, and setup that works best for them.


# topic naming

full topic name template: < company_application_name >--< BusinessObjectName >--< FreePlaceholder >--< Exposition >--< Typology >--< Version >

### company_application_name
The name of your company logical regroupment. Field is mandatory

### BusinessObjectName
**Goals**: Give the business name of the object that transit in the topic. Field is mandatory

### FreePlaceholder
**Goals**: This is a free field that can be use to better fit your needs, like technical functionality on your side. Field is optional

> Avoid using it to much and don't use long string inside.

### Exposition
**Goals**: Whether your topic is for internal (int) or external (ext) clients. This can be critical for some team to separate topic with strong contract and long support from the one that can drastically change and may be unstable. Field is mandatory

This thumb rule will be:

if you expose data to others or even outside your company use the ext name
if the topic is solely for your own or own team use int name
> note: Later this could be handled by a Kafka gateway more transparently. https://docs.conduktor.io/ or https://kroxylicious.io/

### Typology
**Goals**: it means to give information about what kind of purpose the topic serve. Field is mandatory

This is the list of authorized values:

- reqest
- notification
- errors
- event
- entitycompact
- entityvolatile
- entitycompactvolatile

| **Owner is what** | **Owner send what** | **Data Retention** | **Typology** | **Typology Abbreviation** | **Example** |
|-------------------|---------------------|--------------------|--------------|--------------------------|-------------|
| Producer | The full business object | Delete | Entityvolatile | Entvol | Stock price with a TTL. You are not sure to get the price for a stock that hasn't been produced in the last time window. You also get for a stock all the information produced in the time window. |
| Producer | The full business object | Compact | Entitycompact | Entcpt | Stock price with history. You are sure to get at least one price for each stock that has been produced once. |
| Producer | The full business object | Compact Delete | Entitycompactvolatile | Entcptvol | Stock price with only the indices that moved in the last time window but with the assurance that you will get at least one price. You also will get at least one information for each stock but not all of them. |
| Producer | Part of the business object | Delete (usually), Compact, Compact-Delete | Event | Evt | Event about a business object that gives information to consumers, like the state of an order. This should be non-vital information and data could be missed or badly ordered without impact. |
| Producer | Part or the full business object | Compact, Delete, Compact-Delete | Notification | Notif | Notification about a business object that gives information to consumers, like the state of an order. In this kind of topic, order is important. |
| Consumer | A request object exposed by the contract | Compact, Delete, Compact-Delete | Request | Req | An application is placing orders for others and is listening on a topic to take new orders. |
| Consumer and Producer | Errors information | Compact, Delete, Compact-Delete | Error | Err | Usually an internal topic used to manage error related to Kafka. |

### Version
**Goals**: give the abbility to do breaking change in schema for topic without impacting your clients directly. Field is mandatory

start at 1 monotonically increase +1 for each breaking change  

### Examples
market data for exchange 1
trading_app--market--exchange-1--int--entcptvol--1
