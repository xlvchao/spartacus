<div align=center><img src="https://s4.ax1x.com/2022/02/10/HYAETK.png"></div>
<p align="center">Born To Freedom</p>

![](https://img.shields.io/badge/Spring%20Boot-2.3.x-blue.svg?logo=Spring%20Boot)  ![](https://img.shields.io/badge/Spring%20Cloud-Hoxton.SR5-blue.svg?logo=Spring)  ![](https://img.shields.io/badge/Python-3.6.x-brightgreen.svg?logo=Python) ![](https://img.shields.io/badge/Docker%20Build-passing-brightgreen.svg?logo=Docker)  ![](https://img.shields.io/badge/Version-1.0.0-brightgreen.svg?logo=Verizon)  ![](https://img.shields.io/badge/License-Apache2.0-000000.svg?logo=Apache)



# 项目背景

那是疫情来临之前的某年某日，作者第一次在CSDN上写技术博客，反复修改了三五日之后终于完成，怀着激动的心情点击发表按钮，意料之外的是还需要平台审核，通过后才能正式发表。等了一天之后，万万没想到审核没通过，更加出乎意料的是CSDN直接删了我的文章，令我非常恼火！从此萌生了一个念头，一定要搭建一个只属于自己的写作平台。在熬了无数个深夜和肝了无数个通宵之后，终于迎来了如今的spartacus（读音：斯巴达克斯）😂



# 快速体验

**温馨提示：**

1、只能使用电脑访问、手机端不行，没做自适应

2、只能用 Chrome 浏览器，其他浏览器没做适配

3、屏幕太小的电脑可能适配效果也不好，请将网页缩小到70%左右（14寸笔记本缩放到67%最佳，超过25寸的忽略）

**4、体验服配置低、带宽小，体验时略显卡顿，请各位手下留情，不要对其进行压测(一经发现直接封禁IP)，跪谢！😐**

博客端在线体验：http://tiyan.spartacus.run/spartacus-sunday/index.html

管理端在线体验：http://tiyan.spartacus.run/spartacus-friday/login.html



# 项目文档

部署文档：https://www.yuque.com/xlvchao/spartacus/kwkno9

使用文档：https://www.yuque.com/xlvchao/spartacus/wydhqk

开发文档：https://www.yuque.com/xlvchao/spartacus/yfhmdl



# 系统架构

**spartacus**是一个基于Spring Boot 2.3.x、Spring Cloud Hoxton.SR5、Spring Security 2.3.x、OAuth2.0、Python3等开源框架构建的分布式系统，亦是一个功能完备的微服务脚手架。下图为体验服系统架构图（Chrome浏览器右键图片“**在新标签页中打开图片**”，即可展示**高清图**）：

![Architecture](https://s4.ax1x.com/2022/02/10/HYiI78.png)

**Redis Cluster**：Redis集群；认证/资源服务器、网关/资源服务器、业务集群的必须依赖，基于官方推荐的Redis Cluster集群部署架构，详情请看部署文档。

**Mysql Cluster**：Mysql集群；采用简单的读写分离的Master-slave主从集群部署架构，或者采用PXC的高可用集群部署架构，详情请看部署文档。

**Maxwell Cluster**：数据同步组件；结合Rabbitmq共同完成Mysql到Elasticsearch的数据同步，采用官方推荐的基于Raft协议的集群部署架构，详情请看部署文档。

**Rabbitmq Cluster**：消息队列&Stomp消息代理；一方面与Maxwell共同完成数据同步，另一方面作为聊天微服务的Stomp消息代理，采用官方推荐的HA集群部署架构，详情请看部署文档。

**Elasticsearch Cluster**：搜索引擎；文章数据/资源数据/系统日志/链路追踪等数据的持久化、全文检索，采用官方推荐的集群部署架构，详情请看部署文档。

**Logstash & Kibana**：Logstash作为数据传输管道，所有应用的日志均通过Logstash传输到Elasticsearch中，Kibana则负责数据可视化、索引管理等功能，详情请看部署文档。

**认证/授权服务器**：支持微信/QQ/短信/系统账号4种登陆方式，登陆成功后会下发特定签名的token给到客户端，同时支持远程token验签，详情请看部署文档。

**Eureka Cluster**：注册中心高可用集群；基于REST的方式提供服务注册、发现功能，同时具备心跳检测功能，以此达到监控服务可用性的目的，详情请看部署文档。

**Apollo Cluster**：配置中心高可用集群；所有微服务的配置均托管在配置中心，同时支持动态配置/修改接口的访问权限，详情请看部署文档。

**Xxl-job Cluster**：任务调度平台高可用集群；xxl-job支持动态启停任务、分片广播任务、失败重试、故障转移等各种特性和策略，详情请看部署文档。

**CSP**：即Cloud Service Provider（云服务提供商）的缩写；spartacus分别调用了百度的地图服务、阿里的天气服务、腾讯的对象存储服务，详情请看部署文档。

**Sleuth & Zipkin**：链路追踪；Sleuth负责记录所有接口的调用链路数据，Zipkin负责收集并持久化到Elasticsearch集群中，以及可视化调用链路信息，详情请看部署文档。

**网关/资源服务器**：高可用的WebFlux Gateway集群；主要负责路由、权限校验、负载均衡（结合Ribbon）、限流/熔断/降级（结合Sentinel）、黑名单访问拦截、记录接口调用记录等功能，详情请看部署文档。

**代理/负载均衡**：基于Vip+KeepAlived+Haproxy搭建的高可用LB集群；主要用于负载均衡认证/授权服务器及网关/资源服务器等的请求，详情请看部署文档。

**业务集群**：由数据监控、文章管理、评论管理、资源管理、系统设置、数据同步、聊天系统、博客前端、管理前端，共9个模块组成，详情请看部署文档。

**客户端**：客户端实际上是部署在业务集群里的Friday(管理端)和Sunday(博客端)，这里的客户端是指用户通过PC浏览器访问博客端网站或管理端网站，详情请看部署文档。



# 业务介绍

**管理端**

数据监控：支持流量总览、访问统计、封禁/解禁异常高频访问用户、实时查看服务器负载情况等功能，<a href="https://s4.ax1x.com/2022/02/10/HYASW4.png" target="_blank">功能思维导图</a>

文章管理：支持写文章、缓存到本地/从本地加载、发表文章、存草稿箱等功能，<a href="https://s4.ax1x.com/2022/02/10/HYkOe0.png" target="_blank">功能思维导图</a>

评论管理：支持评论审核、回复评论、禁止/解禁用户评论等功能，<a href="https://s4.ax1x.com/2022/02/10/HYA9SJ.png" target="_blank">功能思维导图</a>

资源管理：支持照片/视频等资源的批量上传、资源重命名、设置资源访问权限等功能，<a href="https://s4.ax1x.com/2022/02/10/HYkzYF.png" target="_blank">功能思维导图</a>

系统设置：支持查看系统登录足迹，管理通知公告、友情链接、系统用户等功能，<a href="https://s4.ax1x.com/2022/02/10/HYAAw6.png" target="_blank">功能思维导图</a>

数据同步：支持全量/增量同步业务数据、实时聊天数据到Elasticsearch，<a href="https://s4.ax1x.com/2022/02/10/HYkxFU.png" target="_blank">功能思维导图</a>

在线聊天：支持私聊、群聊、推送在线用户、上线时提示离线消息、保存联系人等功能，<a href="https://s4.ax1x.com/2022/02/10/HYkbyn.png" target="_blank">功能思维导图</a>

**博客端**

顶部菜单：首页、技术、算法、架构、职场、生活、归档，单击任一选项展示对应分类的文章列表，<a href="https://s4.ax1x.com/2022/02/10/HYAkex.png" target="_blank">功能思维导图</a>

文章列表：点击顶部菜单选项或搜索文章时则会返回对应的文章列表，用户可对文章点赞/取消点赞等，<a href="https://s4.ax1x.com/2022/02/10/HYkqLq.png" target="_blank">功能思维导图</a>

左右边栏：左侧展示博主资料、通知公告列表，右侧展示阅读最多、点赞最多、最近评论等功能，<a href="https://s4.ax1x.com/2022/02/10/HYkjoT.png" target="_blank">功能思维导图</a>

文章详情：用户点击文章列表页的文章超链接后进入文章详情，可对文章进行评论等功能，<a href="https://s4.ax1x.com/2022/02/10/HYkWeP.png" target="_blank">功能思维导图</a>

搜索文章：基于Elasticsearch实现的文章搜索功能，支持分词处理、按照相似度返回文章列表，<a href="https://s4.ax1x.com/2022/02/10/HYAiO1.png" target="_blank">功能思维导图</a>

在线聊天：同管理端的在线聊天，支持私聊、群聊等功能，<a href="https://s4.ax1x.com/2022/02/10/HYkbyn.png" target="_blank">功能思维导图</a>

社交登陆：提供QQ登陆、微信登陆两种社交登陆方式，用户登录后才能进行聊天、评论、点赞等功能，<a href="https://s4.ax1x.com/2022/02/10/HYAPyR.png" target="_blank">功能思维导图</a>



# 项目结构

```
spartacus
 ├── spartacus-common -- 公共依赖
 ├── spartacus-discovery -- 服务注册/发现
 ├── spartacus-gateway -- 网关/资源服务器
 ├── spartacus-auth -- 认证/授权服务器
 ├── spartacus-monitor -- 监控服务
 ├── spartacus-article -- 文章管理
 ├── spartacus-comment -- 评论管理
 ├── spartacus-resource -- 资源管理
 ├── spartacus-system -- 系统功能
 ├── spartacus-chat -- 聊天模块
 ├── spartacus-datasyner -- 数据同步
 ├── spartacus-friday -- 管理前端
 └── spartacus-sunday -- 博客前端
```



# 技术选型

#### 后端
| 技术                 | 说明                       | 网站                                                         |
| :------------------- | :------------------------- | :----------------------------------------------------------- |
| Spring Boot          | Mvc框架/Web容器            | https://spring.io/projects/spring-boot                       |
| Spring Cloud         | 一系列框架的集合           | https://spring.io/projects/spring-cloud                      |
| Spring Security      | 安全框架                   | https://spring.io/projects/spring-security                   |
| Spring Cloud Gateway | 网关                       | https://spring.io/projects/spring-cloud-gateway              |
| Ribbon               | 负载均衡                   | https://github.com/Netflix/ribbon                            |
| Sentinel             | 流量哨兵（限流/熔断/降级） | https://github.com/alibaba/Sentinel                          |
| Eureka               | 服务注册/发现              | https://github.com/Netflix/eureka                            |
| MyBatis              | ORM框架                    | https://github.com/mybatis/mybatis-3                         |
| Dynamic-datasource   | 多数据源                   | https://github.com/baomidou/dynamic-datasource-spring-boot-starter |
| PageHelper           | 物理分页插件               | https://github.com/pagehelper/Mybatis-PageHelper             |
| Apollo               | 配置中心                   | https://github.com/apolloconfig/apollo                       |
| Sleuth               | 链路追踪                   | https://github.com/spring-cloud/spring-cloud-sleuth          |
| Zipkin               | 链路追踪信息收集与展示     | https://github.com/openzipkin/zipkin                         |
| Hikari               | 光速数据库连接池           | https://github.com/brettwooldridge/HikariCP                  |
| Xxl-job              | 分布式定时任务             | https://github.com/xuxueli/xxl-job                           |
| Openfeign            | 服务间相互调用框架         | https://github.com/spring-cloud/spring-cloud-openfeign       |
| Websocket            | 全双工通信协议             | https://datatracker.ietf.org/doc/html/rfc6455                |
| Psutil               | Python3.x版本系统监控框架  | https://pypi.org/project/psutil/                             |
| Flask                | Python3.x版本Web框架       | https://pypi.org/project/Flask/                              |
| APScheduler          | Python3.x版本定时任务框架  | https://apscheduler.readthedocs.io/en/3.x/                   |



#### 前端

| 技术                 | 说明                       | 网站&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; |
| :------------------- | :----------------------------------------------------------- | :----------------------------------------------------------- |
| Bootstrap            | 前端响应式框架                                               | https://www.bootcss.com/                                     |
| Fontawesome          | 字体图标框架                                                 | https://fontawesome.com/                                     |
| Jquery               | 快速、简洁的JS框架                                           | https://jquery.com/                                          |
| Layer                | 弹窗组件                                                     | https://github.com/sentsin/layer                             |
| Laydate              | 时间选择器                                                   | https://github.com/sentsin/laydate                           |
| Summernote           | 富文本编辑器                                                 | https://summernote.org/                                      |
| wangEditor | 富文本编辑器 | https://www.wangeditor.com/ |
| Animate              | 动画框架                                                     | https://animate.style/                                       |
| Stompjs              | Stomp协议JS库                                                | https://stomp-js.github.io/                                  |
| Sockjs               | 升级版Websocket框架                                          | https://github.com/sockjs/sockjs-client                      |
| Axios                | 前端Http工具                                                 | https://github.com/axios/axios                               |
| Bootstrap-select     | Bootstrap选择器                                              | http://silviomoreto.github.io/bootstrap-select               |
| Bootstrap-pagination | Bootstrap分页插件                                            | https://github.com/thinksea/bootstrap-pagination             |
| Webuploader          | 文件上传工具                                                 | https://fex.baidu.com/webuploader/                           |



#### 中间件&基础设施

| 技术           | 说明                   | 网站&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; |
| :------------- | :--------------------- | :----------------------------------------------------------- |
| Redis          | 缓存                   | https://redis.io/                                            |
| Maxwell        | 数据同步中间件         | http://maxwells-daemon.io/                                   |
| Rabbitmq       | 消息队列/Stomp消息代理 | https://www.rabbitmq.com/                                    |
| KeepAlived     | 保证服务高可用         | https://www.keepalived.org/                                  |
| Haproxy        | 负载均衡               | https://www.haproxy.com/                                     |
| Elasticsearch  | 搜索引擎               | https://www.elastic.co/cn/elasticsearch/                     |
| Logstash       | 数据传输管道           | https://www.elastic.co/cn/logstash/                          |
| Kibana         | 数据可视化             | https://www.elastic.co/cn/kibana/                            |
| Mysql          | 关系数据库             | https://www.mysql.com/                                       |
| CentOS         | 服务器操作系统         | https://www.centos.org/                                      |
| Maven          | 依赖管理               | https://maven.apache.org/                                    |
| Docker         | 构建镜像/运行容器      | https://www.docker.com/                                      |
| Docker Compose | 容器编排               | https://github.com/docker/compose                            |



# 公众号

关注不迷路，微信扫描下方二维码或搜索关键字“**spartacus**”，关注「**spartacus**」公众号，时刻收听**spartacus**更新通知！

在公众号后台回复“**加群**”，即可加入「**spartacus**」扯淡交流群！

![mp_qrcode](https://s4.ax1x.com/2022/02/10/HYkXwV.jpg)


# 许可证

```
Copyright [2022] [xlvchao]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```