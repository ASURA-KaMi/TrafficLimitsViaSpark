package com.traffic_capturing;

import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;
import org.pcap4j.packet.SctpPacket;
import org.pcap4j.util.NifSelector;

import java.io.BufferedWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;
public class TrafficCaptureThr extends Thread {
    private final String[] filters;
    public TrafficCaptureThr(String[] args){
        this.filters = args;
    }
    private static PcapHandle handle;
    private static ServerSocket server;
    private static Socket streamOut;
    private static BufferedWriter msgOut;
    private static PcapNetworkInterface getNetworkDevice() {
        PcapNetworkInterface device = null;
        try {
            device = new NifSelector().selectNetworkInterface();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return device;
    }

    public static void handlerInit() throws PcapNativeException, NotOpenException {
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
        handle = device.openLive(snapshotLength, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, readTimeout);
    }

    public static void counterThreadInit (){
        TrafficCounterSpark counter = new TrafficCounterSpark();
        counter.start();
    }
    public static void dummyCounterThreadInit (){
        dummyCounter counter = new dummyCounter();
        counter.start();
    }
    private void setFilters() throws PcapNativeException, NotOpenException{
        String filter;
        switch (this.filters[0]){
            case ("-s"):
                filter = "src host " + this.filters[1];
                break;
            case("-d"):
                filter = "dst host " + this.filters[1];
                break;
            default:
                filter = "";
                break;
        }
        handle.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE);
    }

    @Override
    public void run() {
        try {
        handlerInit();
        //counterThreadInit(); idk how to convert DStream<Integer> to int and call an external procedure cuz im use dummyCounter

        //check app arguments
        if (this.filters.length > 0)
            setFilters();

        PacketListener listener = new PacketListener() {
            @Override
            public void gotPacket(Packet packet) {
                try {
                        System.out.println(packet);
                        msgOut.write(packet.length() + "\n");
                        msgOut.flush();
                }catch (IOException e) {}
            }
        };
        try {
            server = new ServerSocket(9999);
            streamOut = server.accept();
            System.out.println("Connection Started");
            msgOut = new BufferedWriter(new OutputStreamWriter(streamOut.getOutputStream()));
            handle.loop(-1, listener);
        }finally {
            streamOut.close();
            msgOut.close();
            server.close();
            handle.close();
        }
        }catch(PcapNativeException| InterruptedException | NotOpenException | IOException e) {}

    }
}
