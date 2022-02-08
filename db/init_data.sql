-- 初始化系统用户（用户名、密码均是admin）
delete from spartacus.tb_UserConnection;
INSERT INTO spartacus.tb_UserConnection (userId, providerId, providerUserId, status, roles, `rank`, displayName, profileUrl, imageUrl, accessToken, secret, refreshToken, expireTime, addTime) VALUES ('admin', 'spartacus', 'admin', 0, 'ROLE_SUPER_ADMIN,ROLE_ACTUATOR,ROLE_ADMIN,ROLE_USER', null, 'admin', null, 'https://pic.imgdb.cn/item/61fdcc4d2ab3f51d911d7c95.jpg', null, '$2a$10$uzHnffPdPa2.0MCP3/DJ/ubOO1AhyAcNoqimlFAjsK7bRnPl7JP/u', null, null, '2021-12-26 21:00:00');

-- 初始化系统用户序列号
delete from spartacus.tb_user_sequence;
INSERT INTO spartacus.tb_user_sequence (user_id, user_sequence) VALUES ('admin', 0);

-- 初始化文章分类
delete from spartacus.tb_category;
INSERT INTO spartacus.tb_category (id, cname) VALUES (474048000000201, '技术');
INSERT INTO spartacus.tb_category (id, cname) VALUES (474048000000202, '算法');
INSERT INTO spartacus.tb_category (id, cname) VALUES (474048000000203, '架构');
INSERT INTO spartacus.tb_category (id, cname) VALUES (474048000000204, '职场');
INSERT INTO spartacus.tb_category (id, cname) VALUES (474048000000205, '生活');

-- 初始化资源分类目录
-- acl_flag：1是私有读写，2是公有读私有写，3是公有读写（必须与云端设置的一致）
-- bucket_name云端必须存在、region必须与云端保持一致
delete from spartacus.tb_cos_resource;
insert into spartacus.tb_cos_resource (id, parent_id, `key`, acl_flag, bucket_name, file_name, region, status, tags, cos_type, content_type, parent_dir_path, root_dir_path, last_modified) values (246482637636071421, null, 'image/', 2, 'tret-1251733385', 'image', 'ap-chengdu', 0, '', 1, null, null, null, '2021-12-26 21:00:00');
insert into spartacus.tb_cos_resource (id, parent_id, `key`, acl_flag, bucket_name, file_name, region, status, tags, cos_type, content_type, parent_dir_path, root_dir_path, last_modified) values (246482637636071422, null, 'video/', 2, 'tret-1251733385', 'video', 'ap-chengdu', 0, '', 1, null, null, null, '2021-12-26 21:00:00');
insert into spartacus.tb_cos_resource (id, parent_id, `key`, acl_flag, bucket_name, file_name, region, status, tags, cos_type, content_type, parent_dir_path, root_dir_path, last_modified) values (246482637636071423, null, 'other/', 2, 'tret-1251733385', 'other', 'ap-chengdu', 0, '', 1, null, null, null, '2021-12-26 21:00:00');
insert into spartacus.tb_cos_resource (id, parent_id, `key`, acl_flag, bucket_name, file_name, region, status, tags, cos_type, content_type, parent_dir_path, root_dir_path, last_modified) values (246482637636071424, null, 'recycle/', 2, 'tret-1251733385', 'recycle', 'ap-chengdu', 0, '', 1, null, null, null, '2021-12-26 21:00:00');

-- 初始化博主基本信息
delete from spartacus.tb_blog_profile;
insert into spartacus.tb_blog_profile (id, id_code, head_img, nickname, title, github, age, experience, add_time) values (111111111111111111, 'profile', 'https://tret-1251733385.cos.ap-chengdu.myqcloud.com/logo/2022-01-06/347302081005764608-超人.png', 'spartacus', '技术专家', 'https://github.com/xlvchao', '18', '10', '2022-01-06 19:55:52');
insert into spartacus.tb_blog_profile (id, id_code, head_img, nickname, title, github, age, experience, add_time) values (222222222222222222, 'head_img', 'https://tret-1251733385.cos.ap-chengdu.myqcloud.com/logo/2022-01-06/347302081005764608-超人.png', null, null, null, null, null, '2022-01-06 19:57:06');

