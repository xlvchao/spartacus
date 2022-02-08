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