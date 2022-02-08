CREATE DATABASE IF NOT EXISTS spartacus DEFAULT CHARACTER SET = utf8mb4;

Use spartacus;

--
-- Table structure for table `tb_UserConnection`
--
DROP TABLE IF EXISTS `tb_UserConnection`;
CREATE TABLE `tb_UserConnection` (
  `userId` varchar(255) NOT NULL,
  `providerId` varchar(255) NOT NULL,
  `providerUserId` varchar(255) NOT NULL,
  `status` int(11) DEFAULT '0',
  `roles` varchar(512) DEFAULT NULL,
  `rank` int(11) DEFAULT NULL,
  `displayName` varchar(255) DEFAULT NULL,
  `profileUrl` varchar(512) DEFAULT NULL,
  `imageUrl` varchar(512) DEFAULT NULL,
  `accessToken` varchar(512) DEFAULT NULL,
  `secret` varchar(512) DEFAULT NULL,
  `refreshToken` varchar(512) DEFAULT NULL,
  `expireTime` bigint(20) DEFAULT NULL,
  `addTime` datetime DEFAULT NULL,
  PRIMARY KEY (`userId`),
  INDEX `UserConnectionProviderId` (`providerId`),
  UNIQUE INDEX `UserConnectionProviderUserId` (`providerUserId`),
  UNIQUE INDEX `UserConnectionRank` (`userId`, `providerId`, `rank`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
comment '系统用户表';


--
-- Table structure for table `tb_access`
--
DROP TABLE IF EXISTS `tb_access`;
CREATE TABLE `tb_access` (
  `id` bigint(20) NOT NULL,
  `access_time` datetime DEFAULT NULL,
  `client_current_id` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `ip_city` varchar(255) DEFAULT NULL,
  `url` varchar(255) DEFAULT NULL,
  `client_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
comment '访问记录表';


--
-- Table structure for table `tb_access_forbid`
--
DROP TABLE IF EXISTS `tb_access_forbid`;
CREATE TABLE `tb_access_forbid` (
  `id` bigint(20) NOT NULL,
  `day_count` int(11) DEFAULT NULL,
  `forbid_type` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `ip_city` varchar(255) DEFAULT NULL,
  `month_count` int(11) DEFAULT NULL,
  `operate_time` datetime DEFAULT NULL,
  `total_count` int(11) DEFAULT NULL,
  `year_count` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
comment '访问黑名单表';


--
-- Table structure for table `tb_article`
--
DROP TABLE IF EXISTS `tb_article`;
CREATE TABLE `tb_article` (
  `id` bigint(20) NOT NULL,
  `author` varchar(255) DEFAULT NULL,
  `brief` text,
  `cname` varchar(255) DEFAULT NULL,
  `like_number` int(11) DEFAULT '0',
  `comment_number` int(11) DEFAULT '0',
  `content` text,
  `from_where` varchar(255) DEFAULT NULL,
  `is_top` int(11) DEFAULT '0',
  `labels` varchar(255) DEFAULT NULL,
  `month_day` varchar(255) DEFAULT NULL,
  `pictures` text,
  `publish_time` datetime DEFAULT NULL,
  `scan_number` int(11) DEFAULT '0',
  `status` int(11) DEFAULT '0',
  `title` varchar(255) DEFAULT NULL,
  `year` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
comment '文章表';


--
-- Table structure for table `tb_category`
--
DROP TABLE IF EXISTS `tb_category`;
CREATE TABLE `tb_category` (
  `id` bigint(20) NOT NULL,
  `cname` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `tb_category_cname_nuique_index` (`cname`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
comment '分类表';


--
-- Table structure for table `tb_comment`
--
DROP TABLE IF EXISTS `tb_comment`;
CREATE TABLE `tb_comment` (
  `id` bigint(20) NOT NULL,
  `article_id` bigint(20) NOT NULL,
  `article_title` varchar(255) DEFAULT NULL,
  `content` text NOT NULL,
  `front_id` bigint(20) DEFAULT NULL,
  `head_img` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `ip_city` varchar(255) DEFAULT NULL,
  `level` int(11) NOT NULL DEFAULT '1',
  `nickname` varchar(255) DEFAULT NULL,
  `provider_id` varchar(255) DEFAULT NULL,
  `provider_user_id` varchar(255) DEFAULT NULL,
  `publish_time` datetime NOT NULL,
  `rear_id` bigint(20) DEFAULT NULL,
  `ref_id` bigint(20) DEFAULT NULL,
  `status` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
comment '评论表';


--
-- Table structure for table `tb_comment_forbid`
--
DROP TABLE IF EXISTS `tb_comment_forbid`;
CREATE TABLE `tb_comment_forbid` (
  `id` bigint(20) NOT NULL,
  `forbid_type` int(11) DEFAULT NULL,
  `head_img` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `ip_city` varchar(255) DEFAULT NULL,
  `nickname` varchar(255) DEFAULT NULL,
  `operate_time` datetime DEFAULT NULL,
  `provider_id` varchar(255) DEFAULT NULL,
  `provider_user_id` varchar(255) DEFAULT NULL,
  `reason` text NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
comment '评论黑名单表';


--
-- Table structure for table `tb_cos_resource`
--
DROP TABLE IF EXISTS `tb_cos_resource`;
CREATE TABLE `tb_cos_resource` (
  `id` bigint(20) NOT NULL,
  `parent_id` bigint(20) DEFAULT NULL,
  `key` varchar(255) DEFAULT NULL,
  `acl_flag` int(11) DEFAULT NULL,
  `bucket_name` varchar(255) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `region` varchar(255) DEFAULT NULL,
  `status` int(11) DEFAULT NULL,
  `tags` varchar(255) DEFAULT NULL,
  `cos_type` int(11) DEFAULT NULL,
  `content_type` varchar(255) DEFAULT NULL,
  `parent_dir_path` varchar(255) DEFAULT NULL,
  `root_dir_path` varchar(255) DEFAULT NULL,
  `last_modified` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  INDEX `tb_cos_resource_cos_type_status_index` (`cos_type`,`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
comment '云端资源表';


--
-- Table structure for table `tb_net_io`
--
DROP TABLE IF EXISTS `tb_net_io`;
CREATE TABLE `tb_net_io` (
  `id` bigint(20) NOT NULL,
  `insert_date` datetime DEFAULT NULL,
  `io_in` float DEFAULT NULL,
  `io_out` float DEFAULT NULL,
  `ip` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
COMMENT='网络流量统计表';


--
-- Table structure for table `tb_socialuserinfo`
--
DROP TABLE IF EXISTS `tb_socialuserinfo`;
CREATE TABLE `tb_socialuserinfo` (
  `id` bigint(20) NOT NULL,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `head_img` varchar(255) DEFAULT NULL,
  `nickname` varchar(255) DEFAULT NULL,
  `provider_id` varchar(255) DEFAULT NULL,
  `provider_user_id` varchar(255) DEFAULT NULL,
  `province` varchar(255) DEFAULT NULL,
  `refresh_time` datetime DEFAULT NULL,
  `sex` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
COMMENT='社交用户信息表';


--
-- Table structure for table `tb_user_contact`
--
DROP TABLE IF EXISTS `tb_user_contact`;
CREATE TABLE `tb_user_contact` (
  `id` bigint(20) NOT NULL,
  `provider_user_id` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '用户的id',
  `contact_provider_user_id` varchar(50) COLLATE utf8mb4_bin NOT NULL COMMENT '联系人的id',
  PRIMARY KEY (`id`),
  INDEX `tb_user_contact_providerUserId_index` (`provider_user_id`,`contact_provider_user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
COMMENT='用户联系人表';


--
-- Table structure for table `tb_login_record`
--
DROP TABLE IF EXISTS `tb_login_record`;
CREATE TABLE `tb_login_record` (
    `id` bigint(20) NOT NULL,
    `username` varchar(32) DEFAULT NULL comment '用户名',
    `user_type` varchar(32) DEFAULT NULL comment '用户类型',
    `ip` varchar(16) DEFAULT NULL comment 'ip地址',
    `province` varchar(20) DEFAULT NULL comment '省份',
    `city` varchar(20) DEFAULT NULL comment '城市',
    `client` varchar(20) DEFAULT NULL comment '客户端',
    `login_time` datetime DEFAULT NULL comment '登录时间',
	primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
comment '登录记录表';


--
-- Table structure for table `tb_notice`
--
DROP TABLE IF EXISTS `tb_notice`;
CREATE TABLE `tb_notice` (
    `id` bigint(20) NOT NULL,
    `content` text NOT NULL comment '通知内容',
    `is_for_site` int(11) DEFAULT '0' comment '是否用于网站，1是，0不是',
    `is_for_chat` int(11) DEFAULT '0' comment '是否用于聊天，1是，0不是',
    `is_enabled` int(11) DEFAULT '0' comment '是否启用，1启用，0未启用',
    `create_time` datetime DEFAULT NULL comment '创建时间',
	primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
comment '通知通告表';


--
-- Table structure for table `tb_friend_link`
--
DROP TABLE IF EXISTS `tb_friend_link`;
CREATE TABLE `tb_friend_link` (
    `id` bigint(20) NOT NULL,
    `site_name` varchar(255) NOT NULL comment '网站名称',
    `site_address` varchar(255) NOT NULL comment '主页地址',
    `is_valid` int(11) DEFAULT '0' comment '是否有效，1有效，0无效',
    `add_time` datetime DEFAULT NULL comment '添加时间',
	primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
comment '友情链接表';


--
-- Table structure for table `tb_blog_profile`
--
DROP TABLE IF EXISTS tb_blog_profile;
CREATE TABLE `tb_blog_profile` (
    `id` bigint(20) NOT NULL,
    `id_code` varchar(10) NOT NULL comment '识别码',
    `head_img` varchar(500) NULL comment '头像',
    `nickname` varchar(30) NULL comment '昵称',
    `title` varchar(100) NULL comment '职称',
    `github` varchar(200) NULL comment 'github',
    `age` varchar(10) NULL comment '年龄',
    `experience` varchar(10) NULL comment '工作年限',
    `add_time` datetime DEFAULT NULL comment '添加时间',
	primary key (id),
    INDEX `tb_blog_profile_id_code_index` (`id_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
comment '博主基本信息表';


--
-- Table structure for table `tb_user_sequence`
--
DROP TABLE IF EXISTS tb_user_sequence;
create table `tb_user_sequence`(
    `user_id` varchar(255) NOT NULL comment '用户id',
    `user_sequence` int(11) NOT NULL comment '用户唯一序列号',
    primary key(`user_id`),
    UNIQUE INDEX `tb_user_sequence_user_sequence_index`(`user_sequence`)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
comment '用户序列表';


--
-- Table structure for table `tb_article_like`
--
DROP TABLE IF EXISTS tb_article_like;
create table `tb_article_like`(
    `article_id` bigint(20) NOT NULL comment '文章id',
    `user_sequence` int(11) NOT NULL comment '用户唯一序列号',
    `status` tinyint(1) default '0' comment '点赞状态，1点赞，0取消点赞',
    `create_time` datetime not null default now() comment '创建时间',
    `update_time` datetime not null default now() on update now() comment '修改时间',
    primary key(`article_id`, `user_sequence`),
    INDEX `tb_user_like_status_index`(`status`)
)  ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin
comment '文章点赞表';