//spark dependencies
import org.apache.spark.*;
import org.apache.spark.streaming.*;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.api.java.JavaSparkContext;

import com.kafka_producer.*;
import com.traffic_capturing.*;

public class TrafficLimit {
    private static String[] filters;
    protected static KafkaProducer kafkaproducer = new KafkaProducer();
    private static int minLimit = 1024;
    private static int maxLimit = 1073741824;

    public static void captureThreadInit (){
        TrafficCaptureThr capturing = new TrafficCaptureThr(filters);
        capturing.start();
    }
    public static void counterThreadInit (){
        TrafficCounterSpark counter = new TrafficCounterSpark();
        counter.start();
    }
    //length - packet length, direction - chose between min/max limits false/true
    public static void sendAlert(int length, boolean direction){
        String alertMsg;
        if (direction == true){
            alertMsg = "GOING BEYOND THE MAXIMUM LIMIT: " + maxLimit + " : " + length;
        }else{
            alertMsg = "GOING BEYOND THE MINIMUM LIMIT: " + minLimit + " : " + length;
        }
            kafkaproducer.sendLog(alertMsg);
    }

    public static void main(String[] args) throws InterruptedException{
        filters = args;
        captureThreadInit();
        counterThreadInit();
        //sendAlert(500, true);
    }

}