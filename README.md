# SparkTrafficLimit test task
### Tasks
#### Preparation work work: :white_check_mark:
Download, install and run any relational database (Postgres, MySQL, etc.), as well as Apache Kafka (if you wish, you can use Docker).

#### Create a relational DB: :white_check_mark:
Create a _traffic_limits_ schema in the database with the _limits_per_hour_ table.

The table must contain 3 columns: 
+ _limit_name_
+ _limit_value_
+ _effective_date_
 
Set 2 limits:
+ _min_ = 1024
+ _max_ = 1073741824
 
In the _effective_date_ column, enter the date from which these limits come into effect.

#### Make TrafficLimits logger with SparkStreaming$Kafka: :white_check_mark:
Write an application with **SparkStreaming** using any public library for traffic processing (**Pcap4J, jpcap, etc.**).
It will count the amount of captured traffic in 5 minutes and if it goes beyond the minimum and maximum values, it will send a message to **Kafka** in the _alert_ topic
A message is sent whenever the traffic volume in 5 minutes crosses any of the thresholds.

#### LimitsUpdate
The application should update the thresholds every 20 minutes (values with the maximum _effective_date_ should be taken).

#### UnitTests :black_square_button:

#### TrafficFilter :white_check_mark:
Provide the ability to count only the traffic that is _sent / received to / from_ a specific IP address, which is specified as an argument during the submit. By default (if no IP is specified) all traffic should be considered.

#### DB Listener
Provide the ability to update the threshold values immediately after they are updated in the database.



### introduction
#### To build and use this service, use the Maven builder:

```
mvn compile

mvn exec:java -Dexec.mainClass="com.main.TrafficLimit"
```

Or use intelliji IDEA IDE or same, with Maven framework.

#### For easy start, you can use a virtual machine image(**Oracle Virtual Box**)
https://yadi.sk/d/s4MqFVFzD-YiJQ

```
user: dins
password: dins
```
#### To check local ip of your virtual machine, you can use:
```
sudo ifconfig enp0s3 | grep inet
```
And take _inet_ addres.
#### For correct work you need to change ip in docker-compose.yml
```
dins@somename:~$ cd ./Downloads/
dins@somename:~/Downloads$ nano docker-compose.yml
```
In **- KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT:** line:
```
version: "3"
services:
  zookeeper:
    image: 'bitnami/zookeeper:latest'
    ports:
      - '2181:2181'
    environment:
      - ALLOW_ANONYMOUS_LOGIN=yes
  kafka:
    image: 'bitnami/kafka:latest'
    ports:
      - '9092:9092'
    environment:
      - KAFKA_BROKER_ID=1
      - KAFKA_CFG_LISTENERS=PLAINTEXT://:9092
      - KAFKA_CFG_ADVERTISED_LISTENERS=PLAINTEXT://YOUR_VIRTUAL_BOX_ADDRESS:9092
      - KAFKA_CFG_ZOOKEEPER_CONNECT=zookeeper:2181
      - ALLOW_PLAINTEXT_LISTENER=yes
    depends_on:
      - zookeeper
```
After this, exec next command:
```
docker-compose up -d
```
#### To check kafka consumer do the following sequence:
```
dins@somename:~$ docker exec -it kafka1 bash
I have no name!@1426926d20b9:/$ kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic alerts --from-beginning --max-messages 100
Sun Jul 18 20:51:28 MSK 2021 GOING BEYOND THE MINIMUM LIMIT: 1024 : 296
^CProcessed a total of 1 messages
```
#### To check DB creation:

```
dins@somename:~$ sudo -u postgres psql
psql (12.7 (Debian 12.7-1.pgdg100+1))
Type "help" for help.

postgres=# \c test
You are now connected to database "test" as user "postgres".
test=# \d traffic_limits.limits_per_hour
```
Then you get this table:

|     Column     |          Type          | Collation | Nullable |                          Default
|----------------|------------------------|-----------|----------|-----------------------------------------------------------|
|id             | bigint                 |           | not null | nextval('traffic_limits.limits_per_hour_id_seq'::regclass)|
| limit_name     | character varying(50)  |           | not null |
| limit_value    | integer                |           | not null |
| effective_date | time without time zone |           | not null |
```
Indexes:
    "limits_per_hour_pkey" PRIMARY KEY, btree (id)
```

Enter \ q to exit and write this command to get values from the table:
```
test=# SELECT * FROM traffic_limits.limits_per_hour;
```
| id | limit_name | limit_value | effective_date
|----|------------|------------:|----------------
|  1 | min        |        1024 | 17:40:00
|  2 | max        |  1073741824 | 04:15:00


### The APP startup 
#### Code edit:
Setup ip addres of your VirtualMachine or "localhost"(When the working environment is on your pc)
In TrafficLimit class 10:38 (line:column)
```java
public static String ip_server = "192.168.1.16";
```
And change absolute path to **hadoop-3.0.0** folder
In TrafficCounterSpark class 17:28 (line:column)
```java
System.setProperty("hadoop.home.dir", "C:\\Users\\i_ver\\IdeaProjects\\trafficLimitsPCAP\\hadoop-3.0.0");
```
#### Arguments explanation:
In our application we have three arguments **-s** and **-d**, and ip addres after:
+ **-s** mean the source ip addres
+ **-d** mean the destination ip addres

As example setup destination github addres in **IDEA**:

![](https://github.com/ASURA-KaMi/TrafficLimitsViaSpark/blob/master/arguments_setup.PNG?raw=true)

#### Select the network interface

![](https://github.com/ASURA-KaMi/TrafficLimitsViaSpark/blob/master/interface_select.PNG?raw=true)

You need to enter number of preffered interface.

#### Get Experience
