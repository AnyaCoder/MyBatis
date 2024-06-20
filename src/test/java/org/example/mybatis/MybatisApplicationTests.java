package org.example.mybatis;

import org.example.mybatis.entity.User;
import org.example.mybatis.mapper.UserMapper;
import org.example.mybatis.service.AsyncUserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

@SpringBootTest
public class MybatisApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AsyncUserService asyncUserService;

    @Test
    void contextLoads() {
    }

    @Test
    void noIndexPressureTest() throws InterruptedException, IOException {
        int numThreads = 20; // 20 线程模拟并发插入
        int totalInserts = 100000; // 总插入量
        int insertsPerSecond = 500; // 每秒插入量
        int numInserts = totalInserts / numThreads; // 每个线程的插入量
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        long nowtime = System.currentTimeMillis() / 1000;
        AtomicLong totalQueries = new AtomicLong();
        // 文件写入器
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("operation_times_" + nowtime + ".txt"))) {
            // 多线程插入数据
            AtomicLong totalInsertTime = new AtomicLong();
            AtomicLong totalQueryTime = new AtomicLong();
            writer.write("Operation,Time(ms)\n");
            for (int i = 0; i < numThreads; i++) {
                executorService.execute(() -> {
                    try {
                        for (int j = 0; j < numInserts; j++) {
                            User user = new User();
                            user.setUsername("user_" + j);
                            user.setPassword("password" + j);
                            user.setEmail("user_" + j + "@example.com");
                            user.setPhoneNumber("1234567890");
                            user.setRegistrationDate(Timestamp.valueOf(LocalDateTime.now()));
                            user.setGender(1);
                            long startTime, endTime;

                            // 插入操作
                            startTime = System.currentTimeMillis();
                            userMapper.insertUser(user);
                            endTime = System.currentTimeMillis();
                            long insertTime = endTime - startTime;
                            totalInsertTime.addAndGet(insertTime);
                            String insertLog = "Insert," + insertTime;
                            System.out.println(insertLog);
                            writer.write(insertLog + "\n");

                            // 每5次插入执行1询操作
                            if (j % (insertsPerSecond / 100) == 0) {
                                startTime = System.currentTimeMillis();
                                List<User> queriedUser = userMapper.findUsersByEmail("user_" + j + "@example.com");
                                endTime = System.currentTimeMillis();
                                long queryTime = endTime - startTime;
                                totalQueryTime.addAndGet(queryTime);
                                totalQueries.addAndGet(1);
                                String queryLog = "Query," + queryTime;
                                System.out.println(queryLog);
                                writer.write(queryLog + "\n");

                                // 暂停1秒
                                Thread.sleep(1000);
                            }
                        }
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            // 等待所有线程完成
            executorService.shutdown();
            while (!executorService.isTerminated()) {
            }
            String totalInsertLog = "Total Insert Time: " + totalInsertTime.get() + " ms\n";
            String totalQueryLog = "Total Query Time: " + totalQueryTime.get() + " ms\n";
            String avgInsertLog = "Average Insert Time: " + totalInsertTime.get() / (totalInserts) + " ms\n";
            String avgQueryLog = "Average Query Time: " + (totalQueryTime.get() / totalQueries.get()) + " ms\n";
            System.out.println(totalInsertLog);
            System.out.println(totalQueryLog);
            System.out.println(avgInsertLog);
            System.out.println(avgQueryLog);
//			writer.write(totalInsertLog);
//			writer.write(totalQueryLog);
//			writer.write(avgInsertLog);
//			writer.write(avgQueryLog);
        }
    }


    @Test
    void indexedPressureTest() throws InterruptedException, IOException {
        int numThreads = 20; // 20 线程模拟并发插入
        int totalInserts = 1000000; // 总插入量
        int insertsPerSecond = 500; // 每秒插入量
        int numInserts = totalInserts / numThreads; // 每个线程的插入量
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        long nowtime = System.currentTimeMillis() / 1000;
        AtomicLong totalQueries = new AtomicLong();
        // 文件写入器
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("operation_times_with_index_" + nowtime + ".txt"))) {
            // 多线程插入数据
            AtomicLong totalInsertTime = new AtomicLong();
            AtomicLong totalQueryTime = new AtomicLong();
            writer.write("Operation,Time(ms)\n");
            for (int i = 0; i < numThreads; i++) {
                executorService.execute(() -> {
                    try {
                        for (int j = 0; j < numInserts; j++) {
                            User user = new User();
                            user.setUsername("user_" + j);
                            user.setPassword("password" + j);
                            user.setEmail("user_" + j + "@example.com");
                            user.setPhoneNumber("1234567890");
                            user.setRegistrationDate(Timestamp.valueOf(LocalDateTime.now()));
                            user.setGender(1);
                            long startTime, endTime;

                            // 插入操作
                            startTime = System.currentTimeMillis();
                            userMapper.insertUser(user);
                            endTime = System.currentTimeMillis();
                            long insertTime = endTime - startTime;
                            totalInsertTime.addAndGet(insertTime);
                            String insertLog = "Insert," + insertTime;
                            System.out.println(insertLog);
                            writer.write(insertLog + "\n");

                            // 每500次插入执行1次查询操作
                            if (j % (insertsPerSecond) == 0) {
                                startTime = System.currentTimeMillis();
                                List<User> queriedUser = userMapper.findUsersByEmail("user_" + j + "@example.com");
                                endTime = System.currentTimeMillis();
                                long queryTime = endTime - startTime;
                                totalQueryTime.addAndGet(queryTime);
                                totalQueries.addAndGet(1);
                                String queryLog = "Query," + queryTime;
                                System.out.println(queryLog);
                                writer.write(queryLog + "\n");

                                // 暂停1秒
                                Thread.sleep(1000);
                            }
                        }
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            // 等待所有线程完成
            executorService.shutdown();
            while (!executorService.isTerminated()) {
            }

            String totalInsertLog = "Total Insert Time: " + totalInsertTime.get() + " ms\n";
            String totalQueryLog = "Total Query Time: " + totalQueryTime.get() + " ms\n";
            String avgInsertLog = "Average Insert Time: " + totalInsertTime.get() / (totalInserts) + " ms\n";
            String avgQueryLog = "Average Query Time: " + (totalQueryTime.get() / totalQueries.get()) + " ms\n";
            System.out.println(totalInsertLog);
            System.out.println(totalQueryLog);
            System.out.println(avgInsertLog);
            System.out.println(avgQueryLog);
            writer.write(totalInsertLog);
            writer.write(totalQueryLog);
            writer.write(avgInsertLog);
            writer.write(avgQueryLog);
        }
    }


}
