# Docker部署sentinel-dashboard控制台



## 一、编写Dockerfile

```
# 基础镜像
FROM openjdk:8-jre-alpine

# 指定维护者信息
MAINTAINER xlvchao chao9038@gmail.com

#构建镜像时
#时区设置
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo "Asia/Shanghai" >> /etc/timezone

# 申明run容器期间的环境变量
ENV JAVA_OPTS="-server -Xms128M -Xmx128M -Xmn65M -XX:MetaspaceSize=128M -XX:MaxMetaspaceSize=128M"
ENV PARAMS="-Dserver.port=8848 -Dcsp.sentinel.dashboard.server=localhost:8848 -Dproject.name=spartacus-sentinel -Dsentinel.dashboard.auth.username=sentinel -Dsentinel.dashboard.auth.password=sentinel"

ADD sentinel-dashboard-1.8.1.jar sentinel-dashboard-1.8.1.jar

# 声明需要暴露的端口
EXPOSE 8848

# 配置容器启动后执行的命令
ENTRYPOINT exec java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom ${PARAMS} -jar /sentinel-dashboard-1.8.1.jar
```



## 二、生成镜像

```
#在dockerfile所在目录中执行：
docker build -t xlvchao/sentinel-dashboard:1.8.1 .
```



## 三、启动镜像

```
docker run -d --net app-net -p 8848:8848 --name sentinel-dashboard -e PARAMS='-Dserver.port=8848 -Dcsp.sentinel.dashboard.server=10.0.0.10:8848 -Dproject.name=sentinel-dashboard -Dsentinel.dashboard.auth.username=root -Dsentinel.dashboard.auth.password=Pwd@123' xlvchao/sentinel-dashboard:1.8.1

#sentinel-dashboard的服务端口
-Dserver.port=8848
#向客户端指定控制台的地址（这里sentinel本身也作为客户端）
-Dcsp.sentinel.dashboard.server=10.0.0.10:8848 
#客户端的应用名称
-Dproject.name=sentinel-dashboard 
#sentinel-dashboard登录用户名
-Dsentinel.dashboard.auth.username=root 
#sentinel-dashboard登录密码
-Dsentinel.dashboard.auth.password=Pwd@123

注意：--net app-net，客户端必须也要使用该网段，必须保证客户端、sentinel.dashboard都在同一网段
```



## 四、客户端配置

```
#客户端接入sentinel之后，启动时可通过JVM需加上以下参数，且至少有过一次请求，控制台才能看见客户端
#Sentinel控制台地址
-Dcsp.sentinel.dashboard.server=10.0.0.10:8848
#当前应用在控制台显示的项目名称
-Dproject.name=spartacus-gateway
#指定当前客户端监控API的端口（Sentinel-dashboard需要通过客户端API来监控和操控客户端限流、降级策略）
-Dcsp.sentinel.api.port=8719
#当前应用是网关类型
-Dcsp.sentinel.app.type=1

注意：--net app-net，客户端必须也要使用该网段，必须保证客户端、sentinel.dashboard都在同一网段

客户端支持的所有配置：https://github.com/alibaba/Sentinel/tree/master/sentinel-dashboard
```

