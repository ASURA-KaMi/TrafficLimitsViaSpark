//spark dependencies
import org.apache.spark.*;
import org.apache.spark.streaming.*;
import org.apache.spark.streaming.api.java.*;
import org.apache.spark.api.java.JavaSparkContext;

import com.traffic_capturing.*;

public class TrafficLimit {
    private static String[] filters;
    protected static JavaSparkContext sc;
    protected static SparkConf sparkConf;

    public static void sparkInit(){
        //Comment if using in cluster
        System.setProperty("hadoop.home.dir", "C:\\Users\\i_ver\\IdeaProjects\\trafficLimitsPCAP\\hadoop-3.0.0");
        sparkConf = new SparkConf().setMaster("local[1]")
                .setAppName("TrafficCounter");
        sc = new JavaSparkContext(sparkConf);
        JavaStreamingContext ssc = new JavaStreamingContext(sc, Durations.minutes( 5 ));
    }
    public static void captureThreadInit (){
        TrafficCaptureThr capturing = new TrafficCaptureThr(filters);
        capturing.start();
    }

    public static void main(String[] args){
        filters = args;
        captureThreadInit();
        sparkInit();
        /*JavaStreamingContext ssc = new JavaStreamingContext(sc, Durations.minutes( 5 ));
//test commit
        ssc.start();
        ssc.awaitTermination(); */
    }

}