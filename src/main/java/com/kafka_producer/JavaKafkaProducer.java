package com.kafka_producer;


import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Date;
import java.util.Properties;
import java.util.concurrent.ExecutionException;


public class JavaKafkaProducer {
    private final static String TOPIC = "alerts";
    private static String BOOTSTRAP_SERVERS;
    private static Date date = new Date();

    public static void kafkaSendMsg(String msg) throws ExecutionException, InterruptedException {
        String topicName = "alerts";

        final Properties props = new Properties();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());

        final Producer<Long, String> producer = new KafkaProducer<>(props);

        RecordMetadata recordMetadata = (RecordMetadata) producer.send(new ProducerRecord(topicName,  msg)).get();
        if (recordMetadata.hasOffset())
            System.out.println("Message sent successfully");
        producer.close();
    }
    public void setAddr(String ipAddr){
        BOOTSTRAP_SERVERS = ipAddr + ":9092";
        System.out.println(BOOTSTRAP_SERVERS);
    }
    public void sendLog(String alertMsg) throws ExecutionException, InterruptedException{
        date.setTime(System.currentTimeMillis());
        kafkaSendMsg(date + " " + alertMsg );
    }
}
