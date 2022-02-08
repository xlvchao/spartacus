# 基础镜像
FROM openjdk:8-jre-alpine

# 指定维护者信息
MAINTAINER xlvchao chao9038@gmail.com

#构建镜像时
#在容器内创建apollo环境配置文件
RUN mkdir -p /opt/settings
RUN touch /opt/settings/server.properties
RUN echo "env=DEV" >> /opt/settings/server.properties
#时区设置
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo "Asia/Shanghai" >> /etc/timezone

# 容器的数据卷挂载到宿主机（好处是容器关闭后，容器的数据还保留在宿主机）
#VOLUME /tmp

# 申明build镜像期间的构建参数
ARG JAR_FILE

# 申明run容器期间的环境变量
ENV APOLLO_META=mustGiven
ENV APOLLO_ACCESSKEY_SECRET=mustGiven
ENV JAVA_OPTS="-server -Xms128M -Xmx128M -Xmn65M -XX:MetaspaceSize=128M -XX:MaxMetaspaceSize=128M"

# 复制文件到容器
ADD target/${JAR_FILE} spartacus-app.jar

# 声明需要暴露的端口
EXPOSE 0-65535

# 配置容器启动后执行的命令
#ENTRYPOINT ["java","${JAVA_OPTS} -Duser.timezone=Asia/shanghai -Djava.security.egd=file:/dev/./urandom -Dapollo.meta=${APOLLO_META} -Dapollo.accesskey.secret=${APOLLO_ACCESSKEY_SECRET}","-jar","/spartacus-app.jar"]
ENTRYPOINT exec java ${JAVA_OPTS} -Djava.security.egd=file:/dev/./urandom -Dapollo.meta=${APOLLO_META} -Dapollo.accesskey.secret=${APOLLO_ACCESSKEY_SECRET} -jar /spartacus-app.jar