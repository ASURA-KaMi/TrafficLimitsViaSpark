package com.traffic_capturing;

import java.io.*;
import java.net.Socket;

public class dummyCounter extends Thread{
    private static Socket clientSocket;
    private static BufferedReader in;
        @Override
        public void run() {
            try {
                    clientSocket = new Socket("localhost", 9999);
            }catch (IOException e) {}

        }
}
