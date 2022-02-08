##新增配置
[client]
default-character-set=utf8mb4

##新增配置
[mysql]
default-character-set=utf8mb4

[mysqld]
##原始配置
pid-file        = /var/run/mysqld/mysqld.pid
socket          = /var/run/mysqld/mysqld.sock
datadir         = /var/lib/mysql
#log-error      = /var/log/mysql/error.log
# By default we only accept connections from localhost
#bind-address   = 127.0.0.1
# Disabling symbolic-links is recommended to prevent assorted security risks
symbolic-links=0

##新增配置
#设置编码
character-set-client-handshake=FALSE
character-set-server=utf8mb4
collation-server=utf8mb4_bin
init_connect='SET NAMES utf8mb4'

#选项可以禁用dns解析，加快连接速度，但是这样不能在mysql的授权表中使用主机名了，只能使用IP
skip-name-resolve 
#sql_mode=NO_ENGINE_SUBSTITUTION,STRICT_TRANS_TABLES 这个有问题，在创建完新用户登录时报错
#sql_mode=NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION
sql_mode=NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION

#为服务器添加唯一的编号
server-id=1
log-bin=mysql-bin
binlog_format=ROW
#是不是只读模式，0则不是
read-only=0

#等待时间、时区等设置
wait_timeout=28800
interactive_timeout=28800
default-time-zone='+08:00'

#指定哪些库需要同步，如果是多个指定多行即可
binlog-do-db=ApolloPortalDB
binlog-do-db=ApolloConfigDB
binlog-do-db=maxwell
binlog-do-db=spartacus
binlog-do-db=xxl_job

#在主从同步的环境中，replicate-ignore-db用来设置不需要同步的库
replicate-ignore-db=mysql
replicate-ignore-db=sys
replicate-ignore-db=information_schema
replicate-ignore-db=performance_schema