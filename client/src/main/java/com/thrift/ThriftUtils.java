package com.thrift;

import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class ThriftUtils {

    public static final Logger logger = LoggerFactory.getLogger(ThriftUtils.class);
    
    public static void sync() {
        TTransport transport = null;
        try {
            transport = new TSocket(getHost(), 9898, 30000);
            // 协议要和服务端一致
            TProtocol protocol = new TBinaryProtocol(transport);
            Hello.Client client = new Hello.Client(protocol);
            transport.open();
            String result = client.sayHello("sync_tom");
            logger.info(result);
        } catch (Exception e) {
            logger.error("调用异常：",e);
        } finally {
            if (null != transport) {
                transport.close();
            }
        }
    }

    public static void async() {
        try {
            //异步调用管理器
            TAsyncClientManager clientManager = new TAsyncClientManager();
            //设置传输通道，调用非阻塞IO
            TNonblockingTransport transport = new TNonblockingSocket(getHost(), 9899, 30000);
            // 协议要和服务端一致
            TProtocolFactory tprotocol = new TBinaryProtocol.Factory();

            Hello.AsyncClient client = new Hello.AsyncClient(tprotocol, clientManager, transport);
            CountDownLatch latch = new CountDownLatch(1);
            AsynCallback callBack = new AsynCallback(latch);
            logger.info("call method sayHello start ...");
            try {
                client.sayHello("jack", callBack);
            } catch (Exception e) {
                logger.error("调用异常：",e);
            }
            logger.info("call method sayHello .... end");
            //等待完成异步调用
            boolean wait = latch.await(2, TimeUnit.SECONDS);
            logger.info("latch.await =:" + wait);
        }catch (Exception e){
            logger.error("调用异常：",e);
        }
    }

    public static void async2() {
        try {
            //设置传输通道，调用非阻塞IO
            Hello.AsyncClient client = new Hello.AsyncClient(new TBinaryProtocol.Factory(),
                    new TAsyncClientManager(),
                    new TNonblockingSocket(getHost(), 9897, 30000));
            logger.info("call method sayHello start ...");
            // 调用服务
            client.sayHello("jack02", new AsyncMethodCallback<Hello.AsyncClient.sayHello_call>() {
                @Override
                public void onComplete(Hello.AsyncClient.sayHello_call response) {
                    logger.info("onComplete");
                    try {
                        logger.info("<<<<<< AsynCall result :" + response.getResult());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Exception exception) {
                    logger.info("onError :" + exception.getMessage());
                }
            });
        }catch (Exception e){
            logger.error("调用异常：",e);
        }
        logger.info("call method sayHello .... end");
        logger.info("--------------------end .......................");
    }

    public static void _TThreadPoolServer(){
        try {
            TTransport transport = new TSocket(getHost(), 9896);
            TProtocol protocol = new TBinaryProtocol(transport);
            Hello.Client client = new Hello.Client(protocol);
            transport.open();
            int i = 5;
            while (i > 0) {
                logger.info("client调用返回：" + client.sayHello("张三"));
                i--;
            }
            transport.close();
        }catch (Exception e){
            logger.error("调用异常：",e);
        }
    }
    public static void _TThreadPoolServer_TMultiplexedProcessor(){
        try {
            TTransport transport = new TSocket(getHost(), 9895);
            TProtocol protocol = new TBinaryProtocol(transport);

            // TMultiplexedProtocol
            TMultiplexedProtocol multiplexedProtocol = new TMultiplexedProtocol(protocol,"HelloService");
            // 按名称获取服务端注册的service
            Hello.Client client = new Hello.Client(multiplexedProtocol);
            transport.open();
            logger.info("<_TThreadPoolServer_TMultiplexedProcessor> client调用返回：" + client.sayHello("张三"));
            transport.close();
        }catch (Exception e){
            logger.error("调用异常：",e);
        }
    }

    public static String getHost(){
        return SpringContextHolder.getProperty("thrift.server","localhost");
    }
}



class AsynCallback implements AsyncMethodCallback<Hello.AsyncClient.sayHello_call> {
    public static final Logger logger = LoggerFactory.getLogger(AsynCallback.class);
    private CountDownLatch latch;

    public AsynCallback(CountDownLatch latch) {
        this.latch = latch;
    }

    @Override
    public void onComplete(Hello.AsyncClient.sayHello_call response) {
        logger.info("onComplete");
        try {
            logger.info("<<<<<< AsynCall result :" + response.getResult());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            latch.countDown();
        }
    }

    @Override
    public void onError(Exception exception) {
        logger.info("onError :" + exception.getMessage());
        latch.countDown();
    }
}

