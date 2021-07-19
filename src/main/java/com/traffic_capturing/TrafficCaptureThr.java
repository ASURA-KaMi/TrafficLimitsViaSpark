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
    private static PcapHandle handle;
    public TrafficCaptureThr(PcapHandle mainHandle){
        this.handle = mainHandle;
    }
    private static ServerSocket server;
    private static Socket streamOut;
    private static BufferedWriter msgOut;



    @Override
    public void run() {
        try {
        PacketListener listener = new PacketListener() {
            @Override
            public void gotPacket(Packet packet) {
                try {
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
