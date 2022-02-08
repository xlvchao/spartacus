# 前置知识

```
后台启动容器：docker-compose up -d
查看容器运行情况：docker-compose ps
停止容器：docker-compose stop
启动容器：docker-compose start
停止并删除容器：docker-compose down
停止并删除容器并删除volume：docker-compose down --volumes
```



# 0、给云主机eth0网卡共设置3个内网IP

```
参考：https://cloud.tencent.com/document/product/1199/43866

10.0.0.11
10.0.0.9
10.0.0.16
设置完之后，一定要重启服务器！

验证是否设置成功：ping ip1/ip2/ip3...    or    ip address

设置多内网IP的原因:

一方面，部署多个apollo-configservice和apollo-adminservice实例时，如果实例的IP相同，则无法重复注册到内置eureka上

另一方面，实例注册到内置eureka上时，不能使用docker网卡的分配的IP，因为这会导致内网中其他机器上的应用实例无法访问apollo
```



# 1、修改apollo源码，调整网络策略

```
参考：https://www.apolloconfig.com/#/zh/deployment/distributed-deployment-guide?id=_2314-%e9%80%9a%e8%bf%87%e6%ba%90%e7%a0%81%e6%9e%84%e5%bb%ba-docker-%e9%95%9c%e5%83%8f

1、需要忽略的网卡
apollo-configservice/src/main/resources/application.yml
apollo-adminservice/src/main/resources/application.yml

spring:
   cloud:
    inetutils:
     ignoredInterfaces:
      - docker0
      - veth.*
    
    
2、动态指定实例注册时的IP
apollo-configservice/src/main/resources/application.yml
apollo-adminservice/src/main/resources/application.yml

>configservice
eureka:
  instance:
    hostname: ${eureka.instance.ip-address}
    preferIpAddress: true
    ip-address: ${eureka.instance.ip-address}
    instance-id: ${eureka.instance.ip-address}:${server.port}
  client:
    register-with-eureka: true
    fetch-registry: true

>adminservice
eureka:
  instance:
    hostname: ${eureka.instance.ip-address}
    preferIpAddress: true
    ip-address: ${eureka.instance.ip-address}
    instance-id: ${eureka.instance.ip-address}:${server.port}

3、编译打包、构建镜像（记得推送到docker官方仓库）
.\scripts\build.bat
mvn docker:build -pl apollo-configservice,apollo-adminservice,,apollo-portal
```



# 2、提前修改好数据库中的配置
```
先导入apolloportaldb.sql、再导入apolloconfigdb.sql

#修改注册中心地址
select * from ApolloConfigDB.ServerConfig;
eureka.service.url=http://10.0.0.11:8080/eureka/,http://10.0.0.9:8081/eureka/,http://10.0.0.16:8082/eureka/

#修改对应环境的Meta Service地址
select * from ApolloPortalDB.ServerConfig;
apollo.portal.meta.servers={"DEV":"http://10.0.0.11:8080,http://10.0.0.9:8081,http://10.0.0.16:8082"}
```



# 3、搭建3个configservice

```
1、创建docker-compose.yml

version: "3.4"
services:
  configservice1:
    image: xlvchao/apollo-configservice:1.8.1
    container_name: configservice1
    restart: always
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://10.0.0.3:3306/ApolloConfigDB?characterEncoding=utf8&useSSL=false
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=Pwd@123
      - hostname=configservice1
      - eureka.instance.ip-address=10.0.0.11
    volumes:
      - /data/apollo/configservice/1/logs:/opt/logs
    ports:
      - 8080:8080
  configservice2:
    image: xlvchao/apollo-configservice:1.8.1
    container_name: configservice2
    restart: always
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://10.0.0.3:3306/ApolloConfigDB?characterEncoding=utf8&useSSL=false
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=Pwd@123
      - hostname=configservice2
      - eureka.instance.ip-address=10.0.0.9
    volumes:
      - /data/apollo/configservice/2/logs:/opt/logs
    ports:
      - 8081:8080
  configservice3:
    image: xlvchao/apollo-configservice:1.8.1
    container_name: configservice3
    restart: always
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://10.0.0.3:3306/ApolloConfigDB?characterEncoding=utf8&useSSL=false
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=Pwd@123
      - hostname=configservice3
      - eureka.instance.ip-address=10.0.0.16
    volumes:
      - /data/apollo/configservice/3/logs:/opt/logs
    ports:
      - 8082:8080

2、后台启动
docker-compose up -d
```



# 4、搭建3个adminservice

```
1、创建docker-compose.yml

version: "3.4"
services:
  adminservice1:
    image: xlvchao/apollo-adminservice:1.8.1
    container_name: adminservice1
    restart: always
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://10.0.0.3:3306/ApolloConfigDB?characterEncoding=utf8&useSSL=false
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=Pwd@123
      - eureka.instance.ip-address=10.0.0.11
    volumes:
      - /data/apollo/adminservice/1/logs:/opt/logs
    ports:
      - 8090:8090
  adminservice2:
    image: xlvchao/apollo-adminservice:1.8.1
    container_name: adminservice2
    restart: always
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://10.0.0.3:3306/ApolloConfigDB?characterEncoding=utf8&useSSL=false
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=Pwd@123
      - eureka.instance.ip-address=10.0.0.9
    volumes:
      - /data/apollo/adminservice/2/logs:/opt/logs
    ports:
      - 8091:8090
  adminservice3:
    image: xlvchao/apollo-adminservice:1.8.1
    container_name: adminservice3
    restart: always
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://10.0.0.3:3306/ApolloConfigDB?characterEncoding=utf8&useSSL=false
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=Pwd@123
      - eureka.instance.ip-address=10.0.0.16
    volumes:
      - /data/apollo/adminservice/3/logs:/opt/logs
    ports:
      - 8092:8090

2、后台启动
docker-compose up -d
```



# 5、搭建1个portal

```
1、创建docker-compose.yml

version: "3.4"
services:
  portalservice:
    image: xlvchao/apollo-portal:1.8.1
    container_name: portalservice
    restart: always
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://10.0.0.3:3306/ApolloPortalDB?characterEncoding=utf8&useSSL=false
      - SPRING_DATASOURCE_USERNAME=root
      - SPRING_DATASOURCE_PASSWORD=Pwd@123
    volumes:
      - /data/apollo/portal/logs:/opt/logs
    ports:
      - 8070:8070

2、后台启动
docker-compose up -d

3、记得开放portal所在云主机的端口8070！！
```

