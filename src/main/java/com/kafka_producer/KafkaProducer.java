package com.kafka_producer;

import java.util.Date;
public class KafkaProducer {
    private final static String TOPIC = "alerts";
    private final static String BOOTSTRAP_SERVERS = "192.168.1.16:9092";
    private static Date date = new Date();

    public void sendLog(String alertMsg) {
        date.setTime(System.currentTimeMillis());
        System.out.println(date + " " + alertMsg );
    }
}
