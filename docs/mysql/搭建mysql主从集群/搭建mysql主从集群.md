# 搭建MYSQL主从集群

```java
### mysql-master实例并启动
mkdir -p /data/mysql/master/log && mkdir -p /data/mysql/master/data && mkdir -p /data/mysql/master/conf

docker run -p 3306:3306 --name mysql-master -v /data/mysql/master/log:/var/log/mysql -v /data/mysql/master/conf:/etc/mysql/mysql.conf.d -v /data/mysql/master/data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=Pwd@123 -d mysql:5.7

#添加配置文件后，重启容器
mysqld.cnf
    
#进入容器，授权root用户远程访问
docker exec -it --user root mysql-master  /bin/bash
#登录
mysql -uroot -pPwd@123
#设置root可以远程访问权限
grant all privileges on *.* to 'root'@'%' identified by 'Pwd@123' with grant option;
#刷新权限生效
flush privileges;

# 查看是否开启binlog
show variables like '%log_bin%';

#添加用于同步的账号backup（此时可通过远程登录主库执行以下脚本）
GRANT REPLICATION SLAVE ON *.* to 'backup'@'%' identified by 'Pwd@123';
#刷新权限生效
flush privileges;
#查看用户是否创建成功
select user,host from mysql.user;

#设置最大连接数（此时可通过远程登录主库执行以下脚本）
show variables like '%connect%';
set global max_connections=1000;
set global max_user_connections=500;
FLUSH PRIVILEGES;



    
### mysql-slave1实例并启动
mkdir -p /data/mysql/slave1/log && mkdir -p /data/mysql/slave1/data && mkdir -p /data/mysql/slave1/conf

docker run -p 3307:3306 --name mysql-slave1 -v /data/mysql/slave1/log:/var/log/mysql -v /data/mysql/slave1/conf:/etc/mysql/mysql.conf.d -v /data/mysql/slave1/data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=Pwd@123 -d mysql:5.7
    
#添加配置文件后，重启容器
mysqld.cnf
    
#进入容器，授权root用户远程访问
docker exec -it --user root mysql-slave1  /bin/bash
#登录
mysql -uroot -pPwd@123
#设置root可以远程访问权限
grant all privileges on *.* to 'root'@'%' identified by 'Pwd@123' with grant option;
#刷新权限生效
flush privileges;

# 查看是否开启binlog
show variables like '%log_bin%';

#开启slave1同步master的数据
#docker inspect container_id 查看容器IP，如果master与当前slave不在同一局域网内，则master_host最好换成公网地址
change master to
master_host='10.0.0.5',
master_port=3306,
master_user='backup',
master_password='Pwd@123',
master_log_file='mysql-bin.000001',
master_log_pos=0;
#开启同步
start slave; #停止同步 stop slave;
#查看从服务状态
show slave status;

#设置最大连接数（此时可通过远程登录主库执行以下脚本）
show variables like '%connect%';
set global max_connections=1000;
set global max_user_connections=500;
FLUSH PRIVILEGES;




    
### mysql-slave2实例并启动
mkdir -p /data/mysql/slave2/log && mkdir -p /data/mysql/slave2/data && mkdir -p /data/mysql/slave2/conf

docker run -p 3308:3306 --name mysql-slave2 -v /data/mysql/slave2/log:/var/log/mysql -v /data/mysql/slave2/conf:/etc/mysql/mysql.conf.d -v /data/mysql/slave2/data:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=Pwd@123 -d mysql:5.7

#添加配置文件后，重启容器
mysqld.cnf
    
#进入容器，授权root用户远程访问
docker exec -it --user root mysql-slave2  /bin/bash
#登录
mysql -uroot -pPwd@123
#设置root可以远程访问权限
grant all privileges on *.* to 'root'@'%' identified by 'Pwd@123' with grant option;
#刷新权限生效
flush privileges;

# 查看是否开启binlog
show variables like '%log_bin%';


#开启slave2同步master的数据
#docker inspect container_id 查看容器IP，如果master与当前slave不在同一局域网内，则master_host最好换成公网地址
change master to
master_host='10.0.0.5',
master_port=3306,
master_user='backup',
master_password='Pwd@123',
master_log_file='mysql-bin.000001',
master_log_pos=0;
#开启同步
start slave; #停止同步 stop slave;
#查看从服务状态
show slave status;



以上解决了数据库主从备份问题，但是没有解决单库单表数据量较大带来的性能问题，要解决这个问题我们需要引入数据分片概念，即见同一库和同一表的数据分拆到若干子库子表中存储。目前市面上有很多数据分片中间件工具，如mycat、shardingsphere等，后续有空再折腾吧！
```

