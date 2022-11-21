package com.thrift;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.thrift.ThriftUtils._TThreadPoolServer;
import static com.thrift.ThriftUtils._TThreadPoolServer_TMultiplexedProcessor;

/**
 * @author huangxun
 */
@SpringBootApplication
public class ThriftClientApplication {

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ThriftClientApplication.class, args);
        System.out.println("---------------------------1--------------------------");
////        ThriftUtils.sync();
////        System.out.println("---------------------------2--------------------------");
////        Thread.sleep(10000);
//        ThriftUtils.async();
//        System.out.println("---------------------------3--------------------------");
//        Thread.sleep(10000);
//        ThriftUtils.async2();

//        _TThreadPoolServer();

        _TThreadPoolServer_TMultiplexedProcessor();
    }

}