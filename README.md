
thrift  0.9.3 版本
___

## thrift-server

| socket类型       | 端口   | processor 模型 | server模型   | protocol 模型 |
|----------------|------|-------------------|---------|-------------|
| TServerSocket | 9898 | TProcessor   | TServer  | TBinaryProtocol |
| TNonblockingServerSocket | 9899 | TProcessor  | THsHaServer  | TBinaryProtocol |
| TNonblockingServerSocket | 9897 | AsyncProcessor | TThreadedSelectorServer | TBinaryProtocol |
| TServerSocket | 9896 | TProcessor  | TThreadPoolServer  | TBinaryProtocol |
| TServerSocket | 9895 | TMultiplexedProcessor |  TThreadPoolServer | TBinaryProtocol |

## thrift-client

| socket类型       | 端口   | protocol 模型          | client 模型   |
|----------------|------|----------------------|-------------|
| TSocket | 9898 | TBinaryProtocol      | Client      |
| TNonblockingServerSocket | 9899 | TBinaryProtocol      | AsyncClient |
| TNonblockingServerSocket | 9897 | TBinaryProtocol      | AsyncClient |
| TSocket | 9896 | TBinaryProtocol      | Client      |
| TSocket | 9895 | TMultiplexedProtocol | Client      |

## Others

`yaml`文件使用了`dd-java-agent`,如果不需要，可以把相关配置进行移除。