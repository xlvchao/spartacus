# 搭建mysql高可用集群(vip+keepalived+haproxy+pxc)



## 前置知识

```
mysql高可用集群方案，不建议使用Replication，建议使用pxc。因为弱一致性会有问题，比如说a节点数据库显示我购买成功，b 节点数据库显示没有成功，这就麻烦了，而pxc方案是在全部节点都写入成功之后才会告诉你成功，是可读可写双向同步的。
```

![Replication、PXC](Replication、PXC.png)
	

```
Replication
速度快，但仅能保证弱一致性，适用于保存价值不高的数据，比如日志、帖子、新闻等。
采用master-slave结构，在master写入会同步到slave，能从slave读出；但在slave写入无法同步到master。
采用异步复制，master写入成功就向客户端返回成功，但是同步slave可能失败，会造成无法从slave读出的结果。

PXC (Percona XtraDB Cluster，MySQL改进版，性能提升很大)
速度慢，但能保证强一致性，适用于保存价值较高的数据，比如订单、客户、支付等。
数据同步是双向的，在任一节点写入数据，都会同步到其他所有节点，在任何节点上都能同时读写。
采用同步复制，向任一节点写入数据，只有所有节点都同步成功后，才会向客户端返回成功。事务在所有节点要么同时提交，要么不提交。
```

## HAProxy是啥

```
HAProxy 是一个免费的负载均衡软件，可以运行于大部分主流的 Linux 操作系统上。
HAProxy 提供了L4（TCP）和L7（HTTP）两种负载均衡能力，具备丰富的功能。
HAProxy 的社区非常活跃，版本更新快速（最新稳定版1.7.2于2017/01/13推出）。最关键的，HAProxy具备媲美商用负载均衡器的性能和稳定性。
因为 HAProxy 的上述优点，它当前不仅仅是免费负载均衡软件的首选，更几乎成为了唯一选择。
```

### HAProxy 的核心功能

```
负载均衡：L4和L7两种模式，支持RR/静态RR/LC/IP Hash/URI Hash/URL_PARAM Hash/HTTP_HEADER Hash 等丰富的负载均衡算法
健康检查：支持TCP和HTTP两种健康检查模式
会话保持：对于未实现会话共享的应用集群，可通过 Insert Cookie/Rewrite Cookie/Prefix Cookie，以及上述的多种Hash方式实现会话保持
SSL：HAProxy 可以解析 HTTPS 协议，并能够将请求解密为 HTTP 后向后端传输
HTTP 请求重写与重定向
监控与统计：HAProxy 提供了基于 Web 的统计信息页面，展现健康状态和流量数据。基于此功能，使用者可以开发监控程序来监控 HAProxy 的状态
```

### HAProxy的关键特性

```
1、性能
采用单线程、事件驱动、非阻塞模型，减少上下文切换的消耗，能在1ms内处理数百个请求。并且每个会话只占用数KB的内存。
大量精细的性能优化，如O(1)复杂度的事件检查器、延迟更新技术、Single-buffereing、Zero-copy forwarding等等，这些技术使得HAProxy在中等负载下只占用极低的CPU资源。
HAProxy大量利用操作系统本身的功能特性，使得其在处理请求时能发挥极高的性能，通常情况下，HAProxy自身只占用15%的处理时间，剩余的85%都是在系统内核层完成的。
HAProxy作者在8年前（2009）年使用1.4版本进行了一次测试，单个HAProxy进程的处理能力突破了10万请求/秒，并轻松占满了10Gbps的网络带宽。

2、稳定性
作为建议以单进程模式运行的程序，HAProxy对稳定性的要求是十分严苛的，按照作者的说法，HAProxy在13年间从未出现过一个会导致其崩溃的BUG，HAProxy一旦成功启动，除非操作系统或硬件故障，否则就不会崩溃（这里多少还是有点夸大的成分，哈哈）。
在上文中提到过，HAProxy的大部分工作都是在操作系统内核完成的，所以HAProxy的稳定性主要依赖于操作系统，作者建议使用2.6或3.x的Linux内核，对sysctls参数进行精细的优化，并且确保主机有足够的内存，这样HAProxy就能够持续满负载稳定运行数年之久。
```



## 一、腾讯云-控制台-创建私有网络(VPC)-创建私有子网络

```
https://console.cloud.tencent.com/vpc/vpc?rid=1
```



## 二、腾讯云-购买5台云主机CVM，网络选项中选择自己的VPC网络

```
https://buy.cloud.tencent.com/cvm?tab=custom
```



## 三、选择其中3台安装PXC集群

0、配置域名解析

```
vim /etc/hosts
#IP 主机名
192.168.0.5 VM-0-5-centos
192.168.0.6 VM-0-6-centos
192.168.0.7 VM-0-7-centos
```

1、安装percona yum源

```
yum -y install https://repo.percona.com/yum/percona-release-latest.noarch.rpm
```

2、测试percona yum源

```
yum list | grep -i percona
```

3、安装PXC

```
yum -y install Percona-XtraDB-Cluster-57

备注：安装后生成的比较重要的配置文件：
/etc/percona-xtradb-cluster.conf.d/mysqld.cnf
/etc/percona-xtradb-cluster.conf.d/mysqld_safe.cnf
/etc/percona-xtradb-cluster.conf.d/wsrep.cnf
```

4、创建软链接：

```
mv /etc/my.cnf /etc/my.cnf.bak
update-alternatives --install /etc/my.cnf my.cnf "/etc/percona-xtradb-cluster.cnf" 200
```

5、master1节点修改mysqld.cnf配置文件：

```
vim /etc/percona-xtradb-cluster.conf.d/mysqld.cnf
```

```
[mysql]
default-character-set=utf8mb4

[mysqld]
server-id=1
binlog_format=ROW
log_bin=mysql-bin
log_bin_index=mysql-bin.index

datadir=/var/lib/mysql
socket=/var/lib/mysql/mysql.sock
log-error=/var/log/mysqld.log
pid-file=/var/run/mysqld/mysqld.pid

log_slave_updates
expire_logs_days=7
symbolic-links=0

port=3306
lower_case_table_names=0
innodb_file_per_table=1
skip_name_resolve=1
slow_query_log=1
slow_query_log_file=mysql-slow.log

character-set-client-handshake=FALSE
character-set-server=utf8mb4
collation-server=utf8mb4_bin
init_connect='SET NAMES utf8mb4'

sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION

wait_timeout=28800
interactive_timeout=28800
default-time-zone='+08:00'

[client]
default-character-set=utf8mb4
socket=/var/lib/mysql/mysql.sock

备注：
master1节点中的server-id=1，master2节点中的server-id=2，master3节点中的server-id=3
```

6、修改wsrep.cnf配置文件

```
vim /etc/percona-xtradb-cluster.conf.d/wsrep.cnf
```

```
[mysqld]
wsrep_provider=/usr/lib64/galera3/libgalera_smm.so
wsrep_provider_options="gcache.size=2G"

wsrep_cluster_address="gcomm://192.168.0.5,192.168.0.6,192.168.0.7"

binlog_format=ROW
default_storage_engine=InnoDB

wsrep_slave_threads= 8
wsrep_log_conflicts

innodb_autoinc_lock_mode=2

wsrep_node_address=192.168.0.5
wsrep_cluster_name=pxc-cluster
wsrep_node_name=VM-0-5-centos

pxc_strict_mode=ENFORCING

wsrep_sst_method=xtrabackup-v2
wsrep_sst_auth="sstuser:s3cretPass"

备注：
master2节点中的wsrep_node_address=192.168.0.6，wsrep_node_name=VM-0-6-centos
master3节点中的wsrep_node_address=192.168.0.7，wsrep_node_name=VM-0-7-centos
```

7、初始化MySQL数据

```
mysqld --initialize --user=mysql

备注：
确保初始化前/var/lib/mysql目录为空，初始化完成后会在此目录中生成各类文件
```

8、master1节点启动MySQL服务（作为初始化PXC集群的节点，在初始化集群启动mysql服务时需要以特别的方式启动）：

```
systemctl start mysql@bootstrap.service
ss -tunlp | grep mysqld
systemctl enable mysql@bootstrap.service
systemctl status mysql@bootstrap.service
tail -100 /var/log/mysqld.log
```

9、master1节点执行MySQL安全向导：

```
grep "password" /var/log/mysqld.log
mysql_secure_installation

备注：后续master2和master3节点无需分别执行MySQL安全向导，会自动从master1节点同步
```

10、master1节点授权root用户远程登录和创建PXC传输用户sstuser：

```
mysql -uroot -p

mysql> create user 'root'@'%' identified by 'Pwd@123';
mysql> grant all on *.* to 'root'@'%';
mysql> flush privileges;
mysql> create user 'sstuser'@'localhost' identified by 's3cretPass';
mysql> grant reload, lock tables, process, replication client on *.* to 'sstuser'@'localhost';
mysql> flush privileges;

备注：后续master2和master3节点无需再分别授权root用户远程登录，会自动从master1节点同步
```

11、按照上述步骤安装好master2和master3节点，再分别启动MySQL服务

```
systemctl start mysql.service
ss -tunlp | grep mysqld
systemctl enable mysql.service
systemctl status mysql.service
tail -100 /var/log/mysqld.log
```


12、查看PXC集群状态

```
mysql> show global status like 'wsrep%';

常用PXC集群状态监控指标说明：
（1）wsrep_local_state_uuid：与wsrep_cluster_state_uuid的值一致，且所有节点该值都相同
（2）wsrep_cluster_state_uuid：所有节点该值都相同，如果有不同值的节点，说明该节点没有与集群建立连接
（3）wsrep_last_committed：集群已经提交事务的数量，是一个累计值，所有节点该值都相同，如果出现不一致，说明事务有延迟，可以用来计算延
（4）wsrep_replicated：从本节点复制出去的写集数量，wsrep_replicated_bytes为写集的总字节数，可以用于参考节点间的负载均衡是否平衡，该值较大的节点较为繁忙
（5）wsrep_received：与wsrep_replicated对应，本节点接收来自其它节点的写集数量
（6）wsrep_local_state：所有节点该值都应该为4，表示正常，节点状态有如下6个取值
     a、取值1：节点启动并与集群建立连接
     b、取值2：当节点成功执行状态传输请求时，该节点开始缓存写集
     c、取值3：节点接收了SST全量数据传输，该节点现在拥有所有集群数据，并开始应用已缓存的写集
     d、取值4：节点完成与集群数据的同步，它的从属队列现在是空的，并启用流控使其保持为空
     e、取值5：节点接收了状态传输请求，该节点现在对donor不执行流控，该节点已缓存所有的写集但无法应用
     f、取值6：节点完成对joiner节点的状态传输
（7）wsrep_incoming_addresses：集群中所有节点的IP，且每个节点该值都相同
（8）wsrep_cluster_size：集群中的节点数量，所有节点该值都相同
（9）wsrep_cluster_conf_id：所有节点该值都相同，如果值不同，说明该节点被临时“分区”了
（10）wsrep_cluster_status：集群组成的状态，所有节点该值都为“Primary”，如果该值不为“Primary”，说明该节点出现“分区”或“脑裂”现象
（11）wsrep_connected：所有节点该值都为“ON”，表示本节点已经与集群建立连接
（12）wsrep_ready：所有节点该值都为“ON”，表示本节点可以正常提供服务
```

13、当所有节点添加完毕时，将master1的mysql服务关闭，再次启动请以正常方式尝试启动mysql服务！

```
systemctl stop mysql@bootstrap.service
systemctl start mysql.service

备注：
如果报 Failed to start mysql.service: Unit is masked 等错误，为了省事儿，直接卸载master1，然后按照master2步骤重装！
yum remove Percona-XtraDB-Cluster-57
```

14、再次检查集群状态

```
mysql> show global status like 'wsrep%';
```



## 四、剩余两台2台安装keepalived+haproxy

1、安装keepalived

```
#安装步骤参考（注意一定要申请havip）
https://cloud.tencent.com/document/product/215/20186#step1
```

```
#编辑配置文件
vim /etc/keepalived/keepalived.conf
```

```
#配置内容
global_defs {
 notification_email {
   acassen@firewall.loc
   failover@firewall.loc
   sysadmin@firewall.loc
 }
 notification_email_from Alexandre.Cassen@firewall.loc
 smtp_server 192.168.200.1
 smtp_connect_timeout 30
 router_id LVS_DEVEL
 vrrp_skip_check_adv_addr
 vrrp_garp_interval 0
 vrrp_gna_interval 0
}
vrrp_instance VI_1 {

# 注意主备参数选择
  state BACKUP              # 设置初始状态均为“备“
  interface eth0          # 设置绑定 VIP 的网卡 例如 eth0
  virtual_router_id 51    # 配置集群 virtual_router_id 值
  nopreempt               # 设置非抢占模式，

# preempt_delay 10      # 仅 state MASTER 时生效

  priority 100            # 两设备是相同值的等权重节点
  advert_int 5
  authentication {
      auth_type PASS
      auth_pass 1111
  }
  unicast_src_ip 192.168.0.4  # 设置本机内网IP地址
  unicast_peer {
      192.168.0.2             # 对端设备的 IP 地址
  }
  virtual_ipaddress {
      192.168.0.10           # 设置高可用虚拟 VIP
  }
  notify_master "/etc/keepalived/notify_action.sh MASTER"
  notify_backup "/etc/keepalived/notify_action.sh BACKUP"
  notify_fault "/etc/keepalived/notify_action.sh FAULT"
  notify_stop "/etc/keepalived/notify_action.sh STOP"
  garp_master_delay 1    # 设置当切为主状态后多久更新 ARP 缓存
  garp_master_refresh 5   # 设置主节点发送 ARP 报文的时间间隔
  track_interface {
    eth0               # 使用绑定 VIP 的宿主机网卡 例如 eth0
  }
}
```

```
#设置开机自启
systemctl enable keepalived.service

#重启 keepalived 进程使配置生效
systemctl start keepalived
```

2、安装haproxy

```
#创建目录
mkdir /apps

#下载2.3.9版本源码压缩包：http://www.haproxy.org/download/2.3/src/，拷贝至/apps中后，进行解压
cd /apps
tar -zxvf haproxy-2.3.9.tar.gz

#进入haproxy-2.3.9目录进行编译(编译完成后会生成haproxy可执行文件)
cd /apps/haproxy-2.3.9
make TARGET=generic

#加入环境变量
vim /etc/profile
export PATH=$PATH:/apps/haproxy-2.3.9

#使配置生效
source /etc/profile

#进入haproxy-2.3.9目录，创建配置文件，编辑配置
vim /apps/haproxy-2.3.9/haproxy.cfg

#配置内容
global
    #工作目录
    #chroot /usr/local/etc/haproxy
    #日志文件，使用rsyslog服务中local5日志设备（/var/log/local5），等级info
    log 127.0.0.1 local5 info
    #守护进程运行
    daemon
defaults
    log global
    mode    http
    #日志格式
    option  httplog
    #日志中不记录负载均衡的心跳检测记录
    option  dontlognull
    #连接超时（毫秒）
    timeout connect 5000
    #客户端超时（毫秒）
    timeout client  50000
    #服务器超时（毫秒）
    timeout server  50000
#监控界面   
listen haproxy-admin-stats
    #监控界面的访问的IP和端口
    bind  0.0.0.0:8888
    #访问协议
    mode        http
    #URI相对地址
    stats uri   /haproxy
    #统计报告格式
    stats realm     Global\ statistics
    #登陆帐户信息
    stats auth  root:Pwd@123
#数据库负载均衡
listen  proxy-pxc-cluster
    #访问的IP和端口
    bind  0.0.0.0:3306
    #网络协议
    mode  tcp
    #负载均衡算法（轮询算法）
    #轮询算法：roundrobin
    #权重算法：static-rr
    #最少连接算法：leastconn
    #请求源IP算法：source
    balance  roundrobin
    #日志格式
    option  tcplog
    #在MySQL中创建一个没有权限的haproxy用户，密码为空。Haproxy使用这个账户对MySQL数据库心跳检测
    option  mysql-check user haproxy
    server  pxc_master 192.168.0.5:3306 check weight 1 maxconn 2000
    server  pxc_slave1 192.168.0.6:3306 check weight 1 maxconn 2000
    server  pxc_slave2 192.168.0.7:3306 check weight 1 maxconn 2000
    #使用keepalive检测死链
    option  tcpka
    
#启动haproxy
haproxy -f /apps/haproxy-2.3.9/haproxy.cfg

#查看haproxy是否启动成功
lsof -i:8888

#如何重启：查看haproxy进程pid，杀掉重启
ps -ef | grep haproxy
kill -9 pid
haproxy -f /apps/haproxy-2.3.9/haproxy.cfg
```



## 五、云服务器安全组

```
首先，2台安装了keepalived+haproxy的服务器所使用的安全组记得一定要开放3306、8888端口；另外，就是安全组内的所有服务器一定要内网互通，也就是说，内网服务器之间所有端口互相开放！
```

### 1、keepalived+haproxy所在服务器安全组规则

![keepalived+haproxy安全组](D:\Workspaces\Idea_workspace\spartacus\docs\mysql\keepalived+haproxy安全组.png)

### 2、pxc集群内各节点所在服务器安全组规则

![pxc集群安全组](D:\Workspaces\Idea_workspace\spartacus\docs\mysql\pxc集群安全组.png)







