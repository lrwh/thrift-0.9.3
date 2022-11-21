package com.thrift;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ClientController {

    @GetMapping("/")
    public String call() {
        ThriftUtils.sync();
        return "success";
    }

    @GetMapping("/async")
    public String async(){
        ThriftUtils.async();
        return "async success";
    }
    @GetMapping("/async2")
    public String async2(){
        ThriftUtils.async2();
        return "async success";
    }
    @GetMapping("/ttm")
    public String _TThreadPoolServer_TMultiplexedProcessor(){
        ThriftUtils._TThreadPoolServer_TMultiplexedProcessor();
        return "_TThreadPoolServer_TMultiplexedProcessor success";
    }

    @GetMapping("/host")
    public String getHost(){
        return ThriftUtils.getHost();
    }
}