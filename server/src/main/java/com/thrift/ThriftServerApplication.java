package com.thrift;

import com.thrift.impl.AsyncHelloServiceImpl;
import com.thrift.impl.HelloServiceImpl;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.*;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author huangxun
 */
@SpringBootApplication
public class ThriftServerApplication {

    public static final Logger logger = LoggerFactory.getLogger(ThriftServerApplication.class);
    public static void main(String[] args) throws Exception{
        SpringApplication.run(ThriftServerApplication.class, args);
        logger.info("thrift-server 启动完毕");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Integer port = 9898;
                    TProcessor tprocessor = new Hello.Processor<Hello.Iface>(new HelloServiceImpl());
                    // 简单的单线程服务模型
                    TServerSocket serverTransport = new TServerSocket(port);
                    TServer.Args tArgs = new TServer.Args(serverTransport);
                    tArgs.processor(tprocessor);
                    tArgs.protocolFactory(new TBinaryProtocol.Factory());
                    TServer server = new TSimpleServer(tArgs);
                    logger.info("TServer start....port:"+port);
                    server.serve();
                } catch (TTransportException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        async();
        async2();
        new Thread(()->_TThreadPoolServer()).start();
        new Thread(()->_TThreadPoolServer_TMultiplexedProcessor()).start();
    }

    public static void async() throws Exception{
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TProcessor tprocessor = new Hello.Processor<Hello.Iface>(new HelloServiceImpl());
                    // 传输通道 - 非阻塞方式
                    Integer port = 9899;
                    TNonblockingServerSocket serverTransport = new TNonblockingServerSocket(port);
                    //半同步半异步
                    THsHaServer.Args tArgs = new THsHaServer.Args(serverTransport);
                    tArgs.processor(tprocessor);
                    tArgs.transportFactory(new TFramedTransport.Factory());
                    //二进制协议
                    tArgs.protocolFactory(new TBinaryProtocol.Factory());
                    // 半同步半异步的服务模型
                    TServer server = new THsHaServer(tArgs);
                    logger.info("HelloTHsHaServer start....port:"+port);
                    server.serve(); // 启动服务
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 多线程Half-sync/Half-async的服务模型.
     * 需要指定为： TFramedTransport 数据传输的方式。
     */
    public static void async2() throws Exception{
//        TProcessor tprocessor = new Hello.Processor<Hello.Iface>(new HelloServiceImpl());
        Hello.AsyncProcessor processor = new Hello.AsyncProcessor<Hello.AsyncIface>(new AsyncHelloServiceImpl());
        // 传输通道 - 非阻塞方式
        Runnable server = new Runnable() {
            public void run() {
                threadedSelectorServer(processor);
            }
        };
        new Thread(server).start();
    }

    private static void threadedSelectorServer(Hello.AsyncProcessor processor){
        try {
            Integer port = 9897;
            TNonblockingServerSocket serverTransport = new TNonblockingServerSocket(port);
            TThreadedSelectorServer.Args args = new TThreadedSelectorServer.Args(serverTransport)
                    .processor(processor)
                    .transportFactory(new TFramedTransport.Factory())
                    .protocolFactory(new TBinaryProtocol.Factory())
                    .selectorThreads(2)
                    .workerThreads(10);
            TServer server = new TThreadedSelectorServer(args);
            logger.info("TThreadedSelectorServer start....port："+port);
            server.serve();
        } catch (Exception e){
            e.printStackTrace();
        }
    }


    public static void _TThreadPoolServer(){
        try {
            TServerSocket serverSocket = new TServerSocket(9896);
            TThreadPoolServer.Args serverParams = new TThreadPoolServer.Args(serverSocket);
            serverParams.protocolFactory(new TBinaryProtocol.Factory());
            serverParams.processor(new Hello.Processor<Hello.Iface>(new HelloServiceImpl()));
            TServer server = new TThreadPoolServer(serverParams); //简单的单线程服务模型，常用于测试
            logger.info("TThreadPoolServer start..... 9896");
            server.serve();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void _TThreadPoolServer_TMultiplexedProcessor(){
        try {
            TServerSocket serverSocket = new TServerSocket(9895);
            TThreadPoolServer.Args serverParams = new TThreadPoolServer.Args(serverSocket);
            TMultiplexedProcessor multiplexedProcessor = new TMultiplexedProcessor();

            serverParams.protocolFactory(new TBinaryProtocol.Factory());

            // 注册所有的processor到multiplexedProcessor中
            multiplexedProcessor.registerProcessor("HelloService", new Hello.Processor<Hello.Iface>(new HelloServiceImpl()));

            serverParams.processor(multiplexedProcessor);

            TServer server = new TThreadPoolServer(serverParams); //简单的单线程服务模型，常用于测试
            logger.info("_TThreadPoolServer_TMultiplexedProcessor start..... 9895");
            server.serve();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}






