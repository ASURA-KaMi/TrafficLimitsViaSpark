package com.main;
import com.kafka_producer.*;
import com.traffic_capturing.*;
import org.pcap4j.core.*;
import org.pcap4j.util.NifSelector;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class TrafficLimit {
    private static int minLimit = 1024;
    private static int maxLimit = 1073741824;
    public static String ip_server = "192.168.1.16";
    public static PcapHandle handlerInit() throws PcapNativeException, NotOpenException {
        // init interface
        PcapNetworkInterface device = getNetworkDevice();
        System.out.println("Your chose: " + device);

        // device not found exception
        if (device == null) {
            System.out.println("No device chosen.");
            System.exit(1);
        }

        // init handler
        int snapshotLength = 65536; // in bytes
        int readTimeout = 100;      // in milliseconds
        PcapHandle handle = device.openLive(snapshotLength, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, readTimeout);
        return handle;
    }

    private static PcapNetworkInterface getNetworkDevice() {
        PcapNetworkInterface device = null;
        try {
            device = new NifSelector().selectNetworkInterface();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return device;
    }

    public static void counterThreadInit (){
        TrafficCounterSpark counter = new TrafficCounterSpark();
        counter.start();
    }
    public static dummyCounter dummyCounterThreadInit (int min, int max){
        dummyCounter counter = new dummyCounter();
        counter.setLimits(min,max);
        counter.setAddres(ip_server);
        counter.start();
        return counter;
    }
    public static void setFilters(String[] filters, PcapHandle handle) throws PcapNativeException, NotOpenException{
        if (filters.length > 0) {
            String filter;
            switch (filters[0]) {
                case ("-s"):
                    filter = "src host " + filters[1];
                    break;
                case ("-d"):
                    filter = "dst host " + filters[1];
                    break;
                default:
                    filter = "";
                    break;
            }
            handle.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE);
        }
    }
    public static void captureThreadInit (PcapHandle handle){
        TrafficCaptureThr capturing = new TrafficCaptureThr(handle);
        capturing.start();
    }
    public static void main(String[] args) throws ExecutionException, InterruptedException,NotOpenException , PcapNativeException{
        PcapHandle handle = handlerInit();
        dummyCounter counter;
        setFilters(args, handle);
        captureThreadInit(handle);
        //counterThreadInit(); idk how to convert DStream<Integer> to int and call an external procedure cuz im use dummyCounter
        counter = dummyCounterThreadInit(minLimit, maxLimit);
    }

}