# 搭建Maxwell高可用集群



## 一、编写Dockerfile

```
FROM centos:7
MAINTAINER chao9038@gmail.com
ADD jdk-11.0.10_linux-x64_bin.tar.gz /usr/local
ENV JAVA_HOME=/usr/local/jdk-11.0.10
ENV JRE_HOME=$JAVA_HOME/jre
ENV CLASSPATH=$JAVA_HOME/lib:$JRE_HOME/lib:$CLASSPATH
ENV PATH=$JAVA_HOME/bin:$JRE_HOME/bin:$PATH
ADD maxwell-1.33.0.tar.gz /usr/local
ADD raft.xml /usr/local/maxwell-1.33.0/bin
WORKDIR /usr/local/maxwell-1.33.0
#RUN yum -y install vim
#CMD /bin/bash
#启动容器时，执行maxwell，并且在最后传递一大串参数
CMD ["param"]
ENTRYPOINT ["/usr/local/maxwell-1.33.0/bin/maxwell"]
```

OR

```
FROM openjdk:11
MAINTAINER chao9038@gmail.com
ADD maxwell-1.33.0.tar.gz /usr/local
ADD raft.xml /usr/local/maxwell-1.33.0/bin
WORKDIR /usr/local/maxwell-1.33.0
#启动容器时，执行maxwell，并且在最后传递一大串参数
CMD ["param"]
ENTRYPOINT ["/usr/local/maxwell-1.33.0/bin/maxwell"]
```



## 二、生成镜像

```
#在dockerfile所在目录中执行：
docker build -t xlvchao/maxwell:1.33.0 .
```



## 三、启动镜像

```
#节点1
docker run -d --restart always --hostname maxwell1 --name maxwell1 xlvchao/maxwell:1.33.0 --log_level=DEBUG --producer=rabbitmq --daemon=true --output_nulls=true --jdbc_options='autoReconnet=true' --host 10.0.0.5 --port=3306 --user=root --password=Pwd@123 --filter='exclude:*.*,include:spartacus.tb_cos_resource,include: spartacus.tb_article,include: spartacus.tb_login_record' --rabbitmq_host=10.0.0.3 --rabbitmq_port=5672 --rabbitmq_user=root --rabbitmq_pass=Pwd@123 --rabbitmq_virtual_host=/dev --rabbitmq_exchange=spartacus --rabbitmq_exchange_type=topic --rabbitmq_exchange_durable=true --rabbitmq_exchange_autodelete=false --rabbitmq_routing_key_template='%db%.%table%' --rabbitmq_message_persistent=true --rabbitmq_declare_exchange=true --ha --raft_member_id=A --jgroups_config=raft.xml

#节点2
docker run -d --restart always --hostname maxwell2 --name maxwell2 xlvchao/maxwell:1.33.0 --log_level=DEBUG --producer=rabbitmq --daemon=true --output_nulls=true --jdbc_options='autoReconnet=true' --host 10.0.0.5 --port=3306 --user=root --password=Pwd@123 --filter='exclude:*.*,include:spartacus.tb_cos_resource,include: spartacus.tb_article,include: spartacus.tb_login_record' --rabbitmq_host=10.0.0.3 --rabbitmq_port=5672 --rabbitmq_user=root --rabbitmq_pass=Pwd@123 --rabbitmq_virtual_host=/dev --rabbitmq_exchange=spartacus --rabbitmq_exchange_type=topic --rabbitmq_exchange_durable=true --rabbitmq_exchange_autodelete=false --rabbitmq_routing_key_template='%db%.%table%' --rabbitmq_message_persistent=true --rabbitmq_declare_exchange=true --ha --raft_member_id=B --jgroups_config=raft.xml

#节点3
docker run -d --restart always --hostname maxwell3 --name maxwell3 xlvchao/maxwell:1.33.0 --log_level=DEBUG --producer=rabbitmq --daemon=true --output_nulls=true --jdbc_options='autoReconnet=true' --host 10.0.0.5 --port=3306 --user=root --password=Pwd@123 --filter='exclude:*.*,include:spartacus.tb_cos_resource,include: spartacus.tb_article,include: spartacus.tb_login_record' --rabbitmq_host=10.0.0.3 --rabbitmq_port=5672 --rabbitmq_user=root --rabbitmq_pass=Pwd@123 --rabbitmq_virtual_host=/dev --rabbitmq_exchange=spartacus --rabbitmq_exchange_type=topic --rabbitmq_exchange_durable=true --rabbitmq_exchange_autodelete=false --rabbitmq_routing_key_template='%db%.%table%' --rabbitmq_message_persistent=true --rabbitmq_declare_exchange=true --ha --raft_member_id=C --jgroups_config=raft.xml


#日志级别
log_level=DEBUG
#输出地
producer=rabbitmq
#以后台守护进程执行
daemon=true
#监控的数据库, mysql用户必须拥有读取binlog权限和新建库表的权限
host=1.14.160.92
user=root
port=3306
password=Pwd@123
#records include fields with null values (default true).
#If this is false, fields where the value is null will be omitted entirely from output
output_nulls=true
#options to pass into the jdbc connection, given as opt=val&opt2=val2
jdbc_options=autoReconnet=true
#监控数据库中的哪些表
filter=exclude: *.*,include: spartacus.tb_cos_resource,include: spartacus.tb_article

#每台实例的replica_server_id 、 client_id必须唯一（默认值是1、maxwell）
#集群部署时请忽略这两项配置，保持默认值即可，或者各个节点配置成一样的值！
replica_server_id=123
client_id=maxwell

#rabbitmq相关配置
rabbitmq_host=1.14.160.92
rabbitmq_port=5672
rabbitmq_user=root
rabbitmq_pass=Pwd@123
rabbitmq_virtual_host=/dev
rabbitmq_exchange=maxwell.topic
rabbitmq_exchange_type=topic
rabbitmq_exchange_durable=false
rabbitmq_exchange_autodelete=false
rabbitmq_routing_key_template=%db%.%table%
rabbitmq_message_persistent=true
rabbitmq_declare_exchange=true

注：
相关topic、routing_key、queue都要事先创建好！
exchange(topic): spartacus
routing_key: spartacus.tb_cos_resource、spartacus.tb_article、spartacus.tb_login_record
queue: spartacus.tb_cos_resource、spartacus.tb_article、spartacus.tb_login_record

```



## 四、容器启动后（maxwell数据库创建完成），执行一次全量同步

```
#初始化同步用户
#创建一个有同步数据的用户maxwell
create user 'maxwell'@'*' identified by 'maxwell';

#此用户maxwell要有对需要同步的数据库表有操作权限
grant all privileges on spartacus.* to 'maxwell'@'%' identified by 'maxwell';

#给maxwell有同步数据的权限
grant select,replication client,replication slave on *.* to 'maxwell'@'%' identified by 'maxwell';

# Maxwell需要在schema_database选项指定的数据库中存储状态的权限（默认库名称为maxwell）
grant all privileges on maxwell.* to 'maxwell'@'%' identified by 'maxwell';

#创建全量同步任务（而后maxwell会自动触发执行）
insert into maxwell.bootstrap (database_name, table_name, client_id) values ('spartacus', 'tb_article', 'maxwell');
insert into maxwell.bootstrap (database_name, table_name, client_id) values ('spartacus', 'tb_cos_resource', 'maxwell');
insert into maxwell.bootstrap (database_name, table_name, client_id) values ('spartacus', 'tb_login_record', 'maxwell');

注意：此处client_id要与三中的入参client_id必须一样！
```

