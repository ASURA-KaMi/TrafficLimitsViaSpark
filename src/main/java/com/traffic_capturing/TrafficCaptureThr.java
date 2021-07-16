package com.traffic_capturing;

import org.pcap4j.core.*;
import org.pcap4j.packet.Packet;
import org.pcap4j.util.NifSelector;

import java.io.IOException;

public class TrafficCaptureThr extends Thread {
    private final String[] filters;
    public TrafficCaptureThr(String[] args){
        this.filters = args;
    }
    private static PcapHandle handle;

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
    public void run()
    {
        try {
        handlerInit();
        //check app arguments
        if (this.filters.length > 0)
            setFilters();

        PacketListener listener = new PacketListener() {
            @Override
            public void gotPacket(Packet packet) {
                System.out.println(packet);
            }
        };
            handle.loop(10, listener);
        }catch(PcapNativeException| InterruptedException | NotOpenException e) {}

        handle.close();
    }
}