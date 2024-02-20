# 前置知识

```
后台启动容器：docker-compose up -d
查看容器运行情况：docker-compose ps
停止容器：docker-compose stop
启动容器：docker-compose start
停止并删除容器：docker-compose down
停止并删除容器并删除volume：docker-compose down --volumes
```



# 1、搭建xxl-job-admin集群

```
1、创建docker-compose.yml

version: "3.4"
services:
  xxl1:
    image: xuxueli/xxl-job-admin:2.3.0
    container_name: xxl-job-admin-1
    restart: always
    environment:
      - PARAMS="--spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver --spring.datasource.url=jdbc:mysql://10.0.0.5:3306/xxl_job?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai --spring.datasource.username=root --spring.datasource.password=Pwd@123 --spring.mail.host=smtp.exmail.qq.com --spring.mail.port=465 --spring.mail.username=notice@baoxue123.com --spring.mail.password=Pwd@123"
    ports:
      - 1111:8080
    networks:
      - net-xxl
  xxl2:
    image: xuxueli/xxl-job-admin:2.3.0
    container_name: xxl-job-admin-2
    restart: always
    environment:
      - PARAMS="--spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver --spring.datasource.url=jdbc:mysql://10.0.0.5:3306/xxl_job?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai --spring.datasource.username=root --spring.datasource.password=Pwd@123 --spring.mail.host=smtp.exmail.qq.com --spring.mail.port=465 --spring.mail.username=notice@baoxue123.com --spring.mail.password=Pwd@123"
    ports:
      - 1112:8080
    networks:
      - net-xxl
  xxl3:
    image: xuxueli/xxl-job-admin:2.3.0
    container_name: xxl-job-admin-3
    restart: always
    environment:
      - PARAMS="--spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver --spring.datasource.url=jdbc:mysql://10.0.0.5:3306/xxl_job?useUnicode=true&characterEncoding=UTF-8&autoReconnect=true&serverTimezone=Asia/Shanghai --spring.datasource.username=root --spring.datasource.password=Pwd@123 --spring.mail.host=smtp.exmail.qq.com --spring.mail.port=465 --spring.mail.username=notice@baoxue123.com --spring.mail.password=Pwd@123"
    ports:
      - 1113:8080
    networks:
      - net-xxl
    
networks:
  net-xxl:
    driver: bridge
    

2、后台启动
docker-compose up -d
```



# 2、使用haproxy实现负载均衡

详见haproxy.cfg

