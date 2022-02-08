# 搭建redis cluster高可用集群



## 前置知识

<img src='D:\Workspaces\Idea_workspace\spartacus\docs\redis\redis cluster.png' style='float:left; width:1000px;'/>

```
Redis Cluster是社区版推出的Redis分布式集群解决方案，主要解决Redis分布式方面的需求，比如，当遇到单机内存，并发和流量等瓶颈的时候，Redis Cluster能起到很好的负载均衡的目的。

Redis Cluster着眼于提高并发量。

群集至少需要3主3从，且每个实例使用不同的配置文件。

在redis-cluster架构中，redis-master节点一般用于接收读写，而redis-slave节点则一般只用于备份， 其与对应的master拥有相同的slot集合，若某个redis-master意外失效，则再将其对应的slave进行升级为临时redis-master。

在redis的官方文档中，对redis-cluster架构上，有这样的说明：在cluster架构下，默认的，一般redis-master用于接收读写，而redis-slave则用于备份，当有请求是在向slave发起时，会直接重定向到对应key所在的master来处理。 但如果不介意读取的是redis-cluster中有可能过期的数据并且对写请求不感兴趣时，则亦可通过readonly命令，将slave设置成可读，然后通过slave获取相关的key，达到读写分离。具体可以参阅redis官方文档等相关内容。

优点:
高可用
解决分布式负载均衡的问题。具体解决方案是分片/虚拟槽slot
可实现动态扩容
P2P模式，无中心化

缺点:
不能保证数据强一致
为了性能提升，客户端需要缓存路由表信息
Slave在集群中充当“冷备”，不能缓解读压力
```

```
#常用操作
rm -rf /data/redis/redis-cluster/637{1..6}
rm -rf /data/redis/redis-cluster/637{1..6}/data/*
```



## 一、搭建集群

##### 1、拉取镜像

```
docker pull redis:6.2.5
```

##### 2、创建docker网络

```
docker network create --subnet=172.19.0.0/16 redis_net

说明：–subnet=172.19.0.0/16 自定义网段172.18.0.0，16是指16位子网掩码 redis_net 网段名称
```

##### 3、创建目录，编写配置文件模板

```
# 创建目录
mkdir -p /data/redis/redis-cluster
# 切换至指定目录
cd /data/redis/redis-cluster/
# 编写 redis-cluster.tmpl 文件
vim redis-cluster.tmpl

port ${PORT}
requirepass Pwd@123
masterauth Pwd@123
protected-mode no
tcp-keepalive 60
daemonize no
appendonly yes
cluster-enabled yes
cluster-config-file nodes.conf
cluster-node-timeout 15000
cluster-announce-ip 1.14.160.92  #云服务器公网地址
cluster-announce-port ${PORT}
cluster-announce-bus-port 1${PORT}

解释：
port：节点端口；
requirepass：添加访问认证；
masterauth：如果主节点开启了访问认证，从节点访问主节点需要认证；
protected-mode：保护模式，默认值yes，即开启。开启保护模式以后，需配置bind ip或者设置访问密码；关闭保护模式，外部网络可以直接访问；
tcp-keepalive：连接保活时长，超过该只自动断开与客户端的连接，单位秒
daemonize：是否以守护线程的方式启动（后台启动），默认 no；
appendonly：是否开启 AOF 持久化模式，默认 no；
cluster-enabled：是否开启集群模式，默认 no；
cluster-config-file：集群节点信息文件；
cluster-node-timeout：集群节点连接超时时间；
cluster-announce-ip：集群节点 IP，这里需要特别注意一下，如果要对外提供访问功能，需要填写宿主机的 IP，如果填写 Docker 分配的 IP（172.x.x.x），会导致外部无法正常访问集群；
cluster-announce-port：集群节点映射端口；
cluster-announce-bus-port：集群节点总线端口。
　　
每个 Redis 集群节点都需要打开两个 TCP 连接。一个用于为客户端提供服务的正常 Redis TCP 端口，例如 6379。还有一个基于 6379 端口加 10000 的端口，比如 16379。
第二个端口用于集群总线，这是一个使用二进制协议的节点到节点通信通道。节点使用集群总线进行故障检测、配置更新、故障转移授权等等。客户端永远不要尝试与集群总线端口通信，与正常的 Redis 命令端口通信即可，但是请确保防火墙中的这两个端口都已经打开，否则 Redis 集群节点将无法通信。
```

##### 4、创建各个节点的目录、配置文件

```
#在 redis-cluster 目录下执行以下命令：

for port in `seq 6371 6376`; do \
  mkdir -p ${port}/conf \
  && PORT=${port} envsubst < redis-cluster.tmpl > ${port}/conf/redis.conf \
  && mkdir -p ${port}/data;\
done

注：上面是一段 shell for 语句，意思就是循环创建 6371 ~ 6376 相关的目录及文件。

#在 redis-cluster 目录查看命令执行结果：tree  (如果没有 tree 命令先安装 yum install -y tree)

#查看各个配置文件内容：
cat /data/redis/redis-cluster/637{1..6}/conf/redis.conf
```

##### 5、创建容器

```
#将宿主机的 6371 ~ 6376 之间的端口与 6 个 Redis 容器映射，并将宿主机的目录与容器内的目录进行映射（目录挂载）

for port in $(seq 6371 6376); do \
  docker run -di -p ${port}:${port} -p 1${port}:1${port} \
  --restart always --name redis-${port} --net redis_net \
  -v /data/redis/redis-cluster/${port}/conf/redis.conf:/usr/local/etc/redis/redis.conf \
  -v /data/redis/redis-cluster/${port}/data:/data \
  redis:6.2.5 redis-server /usr/local/etc/redis/redis.conf; \
done

#查看容器是否创建成功:
docker ps -n 6

#查看给每个节点分配的IP信息:
docker network inspect redis_net | grep -i -E "name|ipv4address"
```

##### 6、创建 Redis Cluster 集群

```
# 进入容器
docker exec -it redis-6371 bash
# 切换至指定目录
cd /usr/local/bin/
# 创建集群
redis-cli -a Pwd@123 --cluster create 1.14.160.92:6371 1.14.160.92:6372 1.14.160.92:6373 1.14.160.92:6374 1.14.160.92:6375 1.14.160.92:6376 --cluster-replicas 1
```



# 二、测试集群

##### 1、检查集群状态

```
1、检查集群状态
# 进入容器
docker exec -it redis-6371 bash
# 切换至指定目录
cd /usr/local/bin/
# 使用 IP
redis-cli -a Pwd@123 --cluster check 172.19.0.2:6371
# 或者使用容器名称
redis-cli -a Pwd@123 --cluster check redis-6371:6371

2、查看集群信息和节点信息
# 连接至集群某个节点
redis-cli -c -a Pwd@123 -h redis-6373 -p 6373
# 查看集群信息
cluster info
# 查看集群结点信息
cluster nodes

注意：默认Redis在持久化快照失败后，会设置为禁止更新Redis，此时会导致程序无法写入KV键值对，会报错：
(error) MISCONF Redis is configured to save RDB snapshots, but it is currently not able to persist on disk. Commands that may modify the data set are disabled, because this instance is configured to report errors during writes if RDB snapshotting fails (stop-writes-on-bgsave-error option). Please check the Redis logs for details about the RDB error.

#所以最好在集群部署完后，逐个登录redis各个节点，提前将 stop-writes-on-bgsave-error 由yes设置为no：
config set stop-writes-on-bgsave-error no
```

##### 2、写入、读取测试

```
# 进入容器并连接至集群某个节点
docker exec -it redis-6371 /usr/local/bin/redis-cli -c -a Pwd@123 -h redis-6371 -p 6371
# 写入数据
set name jack
set aaa 111
set bbb 222
# 读取数据
get name
get aaa
get bbb
```

<img src='D:\Workspaces\Idea_workspace\spartacus\docs\redis\redis写入、读取.png' style='float:left; width:1000px;'/>

**总结下来就是：**在 **Redis Cluster 集群模式**中，**无论连接哪个节点**，每次我们**执行写入或者读取**操作的时候，所有的**键会根据哈希函数运算并映射到 0 ~ 16383 整数槽内**，如果恰好对应的**槽**就在你**当前连接的节点**中，则**直接执行**命令，**否则重定向**至对应节点执行命令。



# 三、注意事项

##### 1、集群架构

```
#所有的 redis 节点彼此互联(PING-PONG 机制)，内部使用二进制协议优（gossip）化传输速度和带宽。

gossip协议简单解释：各节点之间都会保持通讯，当某一个节点挂掉或者新增的时候，与它相邻的节点就会感知到，这时候此节点就是失去链接或者创建链接。
ping：每个节点都会频繁给其他节点发送ping，其中包含自己的状态还有自己维护的集群元数据，互相通过ping交换元数据；
pong: 返回ping和meet，包含自己的状态和其他信息，也可以用于信息广播和更新；
fail: 某个节点判断另一个节点fail之后，就发送fail给其他节点，通知其他节点，指定的节点宕机了。
meet：某个节点发送meet给新加入的节点，让新节点加入集群中，然后新节点就会开始与其他节点进行通信，不需要发送形成网络的所需的所有CLUSTER MEET命令。

#节点的 fail 是通过集群中超过半数的节点对其进行检测失效时才生效。

#客户端与 redis 节点直连、提高速度，不需要中间 proxy 层，客户端不需要连接集群所有节点，连接集群中任何一个 可用节点即可。

#redis-cluster 把所有的物理节点映射到[0-16383]slot上，cluster 负责维护 node<->slot<->value
```

##### 2、Master 选举过程

```
#如果半数以上 master 节点与 master 节点通信超过 cluster-node-timeout，认为当前 master 节点挂掉，集群进入选举流程。

#当slave发现自己的master不可用时，便尝试进行Failover，以便成为新的master。由于挂掉的master可能会有多个slave，从而存在多个slave竞争成为master节点的过程，其过程如下：

（1）slave发现自己的master不可用；
（2）slave将记录集群的currentEpoch（选举周期）加1，并广播FAILOVER_AUTH_REQUEST信息进行选举；
（3）其他节点收到FAILOVER_AUTH_REQUEST后，只有其他的master可以进行响应，master收到消息后返回FAILOVER_AUTH_ACK信息，对于同一个Epoch，只能响应一次ack；
（4）slave收集maste返回的ack消息；
（5）slave判断收到的ack消息个数是否大于半数的master个数，若是，则变成新的master；
（6）slave广播Pong消息通知其他集群节点，自己已经成为新的master。
```

##### 3、什么时候整个集群不可用(cluster_state:fail)

```
#如果集群任意 master 挂掉，且当前 master 没有 slave，集群进入 fail 状态，也可以理解成集群的 slot 映 射[0-16383]不完全时进入 fail 状态。

ps : redis-3.0.0.rc1 加入 cluster-require-full-coverage 参数，默认关闭, 打开则集群兼容部分失败。

#如果集群超过半数以上 master 挂掉，无论是否有 slave 集群进入 fail 状态。

ps:当集群不可用时，所有对集群的操作都会失败，客户端会收到(The cluster is down) 错误。
```
