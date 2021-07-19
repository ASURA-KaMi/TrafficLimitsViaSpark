package com.jdbc;


import com.traffic_capturing.dummyCounter;
import java.util.concurrent.TimeUnit;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalTime;

public class LimitsListner {
    private static Statement statement;
    private static dummyCounter counter;
    public LimitsListner(String ipAddr, dummyCounter cnt) {
        counter = cnt;
        DBWorker worker = new DBWorker(ipAddr);
        try {
                statement = worker.getConnection().createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    public void loop(){
        String minQuery;
        String maxQuery;
        long startTime = System.currentTimeMillis();
        while (true) {
            LocalTime localTime = LocalTime.now();
            minQuery = "select limit_value from traffic_limits.limits_per_hour\n" +
                    "where limit_name = 'min' and effective_date < '" + localTime + "'\n" +
                    "order by effective_date  desc\n" +
                    "limit 1;";

            maxQuery = "select limit_value from traffic_limits.limits_per_hour\n" +
                    "where limit_name = 'max' and effective_date < '" + localTime + "'\n" +
                    "order by effective_date  desc\n" +
                    "limit 1;";
            try {
                ResultSet resultSet = statement.executeQuery(minQuery);
                if (resultSet.next()){
                    counter.setMin(resultSet.getInt(1));
                    System.out.println(resultSet.getInt(1));
                }
                resultSet = statement.executeQuery(maxQuery);
                if (resultSet.next()){
                    counter.setMax(resultSet.getInt(1));
                    System.out.println(resultSet.getInt(1));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                TimeUnit.MINUTES.sleep(20); //Wait for 20 minutes
            }catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}