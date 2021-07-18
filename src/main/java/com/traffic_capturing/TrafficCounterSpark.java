package com.traffic_capturing;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;

public class TrafficCounterSpark extends Thread{
    protected static JavaSparkContext sc;
    protected static SparkConf sparkConf;
    protected static JavaStreamingContext jssc;

    public static void sparkInit(){
        //Comment if using in cluster
        System.setProperty("hadoop.home.dir", "C:\\Users\\i_ver\\IdeaProjects\\trafficLimitsPCAP\\hadoop-3.0.0");
        sparkConf = new SparkConf().setMaster("local[1]")
                .setAppName("TrafficCounter");
        sc = new JavaSparkContext(sparkConf);
        jssc = new JavaStreamingContext(sc, Durations.minutes(5));
    }

    @Override
    public void run(){
        try {
            try {
                sparkInit();
                JavaReceiverInputDStream<String> streamIn = jssc.socketTextStream("localhost", 9999);
                JavaDStream<Integer> packetLengths = streamIn.map(Integer::parseInt);
                JavaDStream<Integer> totalLengthDS = packetLengths.reduce((a, b) -> a + b);
                totalLengthDS.print();
                //sumCheck(totalLengthDS);
                jssc.start();
                jssc.awaitTermination();
            } finally {
                jssc.close();
            }
        }catch (InterruptedException e) {}
    }
}
