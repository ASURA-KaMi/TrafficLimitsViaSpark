package com.main;
import com.kafka_producer.*;
import com.traffic_capturing.*;

import java.util.concurrent.ExecutionException;

public class TrafficLimit {
    private static String[] filters;
    protected static JavaKafkaProducer kafkaproducer = new JavaKafkaProducer();
    private static int minLimit = 1024;
    private static int maxLimit = 1073741824;
    public static String ip_server = "192.168.1.16";
    public static void captureThreadInit (){
        TrafficCaptureThr capturing = new TrafficCaptureThr(filters);
        capturing.start();
    }
    //length - packet length, direction - chose between min/max limits false/true
    public static void sendAlert(int length, boolean direction) throws ExecutionException, InterruptedException{
        String alertMsg;
        if (direction == true){
            alertMsg = "GOING BEYOND THE MAXIMUM LIMIT: " + maxLimit + " : " + length;
        }else{
            alertMsg = "GOING BEYOND THE MINIMUM LIMIT: " + minLimit + " : " + length;
        }
            kafkaproducer.sendLog(alertMsg);
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException{
        filters = args;
        //captureThreadInit();
        kafkaproducer.setAddr(ip_server);
        sendAlert(296, false);
    }

}