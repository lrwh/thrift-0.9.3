package com.thrift.impl;

import com.thrift.Hello;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncHelloServiceImpl  implements Hello.AsyncIface {
    public static final Logger logger = LoggerFactory.getLogger(AsyncHelloServiceImpl.class);

    @Override
    public void sayHello(String username, AsyncMethodCallback resultHandler) throws TException {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        resultHandler.onComplete("async called success.say hello:"+username);
        logger.info("async called sayHello username - >: "+username);
    }
}