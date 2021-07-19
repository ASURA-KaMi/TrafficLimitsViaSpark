package com.traffic_capturing;

import com.kafka_producer.JavaKafkaProducer;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.ExecutionException;

public class dummyCounter extends Thread{
    protected static JavaKafkaProducer kafkaProducer = new JavaKafkaProducer();
    private static Socket clientSocket;
    private static BufferedReader in;
    private static final int timeInSec = 9000;
    private static String ipAddr;
    private static int minLimit = 0;
    private static int maxLimit = 0;
    //length - packet length, direction - chose between min/max limits false/true
    public static void sendAlert(int length, boolean direction) throws ExecutionException, InterruptedException{
        String alertMsg;
        if (direction == true){
            alertMsg = "GOING BEYOND THE MAXIMUM LIMIT: " + maxLimit + " : " + length;
        }else{
            alertMsg = "GOING BEYOND THE MINIMUM LIMIT: " + minLimit + " : " + length;
        }
        kafkaProducer.sendLog(alertMsg);
    }
        private void loop() throws IOException, InterruptedException, ExecutionException{
            long offsetTime = timeInSec * 1000;
            long endTime = 0;
            int trafficLength = 0;
            while(true){
                endTime = System.currentTimeMillis() + offsetTime;
                trafficLength = 0;
                while (System.currentTimeMillis() < endTime){
                String serverWord = in.readLine();
                trafficLength += Integer.parseInt(serverWord);
                }
                System.out.println(trafficLength);
                if (trafficLength > maxLimit){
                    sendAlert(trafficLength, true);
                }else if (trafficLength < minLimit){
                    sendAlert(trafficLength, false);
                }
            }
        }
        public void setLimits(int min, int max){
        minLimit = min;
        maxLimit = max;
        }
        public void setAddres(String addres){
        ipAddr = addres;
        }
        @Override
        public void run() {
        kafkaProducer.setAddr(ipAddr);
            try {
                try {
                    clientSocket = new Socket("localhost", 9999); // этой строкой мы запрашиваем
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    loop();
                } finally {
                    clientSocket.close();
                    in.close();
                }
            } catch (IOException | ExecutionException | InterruptedException e) {
                System.err.println(e);
            }

        }
}
