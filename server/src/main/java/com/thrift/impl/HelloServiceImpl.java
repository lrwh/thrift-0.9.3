package com.thrift.impl;

import com.thrift.Hello;
import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloServiceImpl implements Hello.Iface {
    public static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String sayHello(String username) throws TException {
        logger.info("do func");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        logger.info("called sayHello username - >: "+username);
        return "hello: " + username;
    }
}
