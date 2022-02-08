# 前置知识

```
后台启动容器：docker-compose up -d
查看容器运行情况：docker-compose ps
停止容器：docker-compose stop
启动容器：docker-compose start
停止并删除容器：docker-compose down
停止并删除容器并删除volume：docker-compose down --volumes
```



# 1、搭建zipkin集群

```
1、创建docker-compose.yml

version: "3.4"
services:
  zipkin1:
    image: openzipkin/zipkin:2.23
    container_name: zipkin1
    restart: always
    environment:
      - STORAGE_TYPE=elasticsearch
      - ES_HOSTS=http://10.0.0.5:9200,http://10.0.0.5:9201,http://10.0.0.5:9202
      - ES_USERNAME=elastic
      - ES_PASSWORD=Pwd@123
    ports:
      - 9411:9411
    networks:
      - net-zipkin
  zipkin2:
    image: openzipkin/zipkin:2.23
    container_name: zipkin2
    restart: always
    environment:
      - STORAGE_TYPE=elasticsearch
      - ES_HOSTS=http://10.0.0.5:9200,http://10.0.0.5:9201,http://10.0.0.5:9202
      - ES_USERNAME=elastic
      - ES_PASSWORD=Pwd@123
    ports:
      - 9412:9411
    networks:
      - net-zipkin
  zipkin3:
    image: openzipkin/zipkin:2.23
    container_name: zipkin3
    restart: always
    environment:
      - STORAGE_TYPE=elasticsearch
      - ES_HOSTS=http://10.0.0.5:9200,http://10.0.0.5:9201,http://10.0.0.5:9202
      - ES_USERNAME=elastic
      - ES_PASSWORD=Pwd@123
    ports:
      - 9413:9411
    networks:
      - net-zipkin
    
networks:
  net-zipkin:
    driver: bridge
    

2、后台启动
docker-compose up -d
```



# 2、使用haproxy实现负载均衡

详见haproxy.cfg

