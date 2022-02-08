# 一、COS

1、创建索引

```
PUT /cos_resource_index

注：
删除索引
DELETE /cos_resource_index

删除索引下的所有数据
POST /cos_resource_index/_delete_by_query
{
 "query": {
   "match_all" :{}
 }
}

查询所有数据
POST /cos_resource_index/_search
{
    "query": {
        "match_all": {}
    },
    "sort": [
        {
            "last_modified": {
                "order": "desc"
            }
        }
    ]
}
```

2、创建mapping

```
PUT /cos_resource_index/_mapping
{
    "properties": {
        "id": {
            "index": "true",
            "type": "long"
        },
        "parent_id": {
            "index": "true",
            "type": "long"
        },
        "file_name": {
            "index": "true",
            "type": "text",
            "analyzer": "ik_max_word"
        },
        "key": {
            "index": "true",
            "type": "keyword"
        },
        "tags": {
            "index": "true",
            "type": "text",
            "analyzer": "ik_max_word"
        },
        "content_type": {
            "index": "true",
            "type": "keyword"
        },
        "cos_type": {
            "index": "true",
            "type": "integer"
        },
        "root_dir_path": {
            "index": "true",
            "type": "keyword"
        },
        "parent_dir_path": {
            "index": "true",
            "type": "keyword"
        },
        "region": {
            "index": "true",
            "type": "keyword"
        },
        "bucket_name": {
            "index": "true",
            "type": "keyword"
        },
        "status": {
            "index": "true",
            "type": "integer"
        },
        "acl_flag": {
            "index": "true",
            "type": "integer"
        },
        "last_modified": {
            "index": "true",
            "type": "date",
            "format": "yyyy-MM-dd HH:mm:ss"
        }
    }
}
```

3、验证

```
PUT /cos_resource_index/_doc/246482648847446016
{
  "id": 246482648847446016,
  "parent_id": 246482641276727296,
  "key": "image/1111/hhh/好吧.JPG",
  "acl_flag": 2,
  "bucket_name": "tret-1251733385",
  "file_name": "好吧.JPG",
  "region": "ap-chengdu",
  "status": 0,
  "tags": "表情包",
  "cos_type": 0,
  "content_type": "application/octet-stream",
  "parent_dir_path": "image/1111/hhh/",
  "root_dir_path": "image/",
  "last_modified": "2021-04-02 20:59:57"  #必须给格式化的字符串类型
}

GET /cos_resource_index/_doc/246482648847446016
```



# 二、article

1、创建索引

```
PUT /article_index

注：
删除索引
DELETE /article_index

删除索引下的所有数据
POST /article_index/_delete_by_query
{
 "query": {
   "match_all" :{}
 }
}

查询所有数据
POST /article_index/_search
{
    "query": {
        "match_all": {}
    },
    "sort": [
        {
            "publish_time": {
                "order": "desc"
            }
        }
    ]
}
```

2、创建mapping

```
PUT /article_index/_mapping
{
    "properties": {
        "id": {
            "index": "true",
            "type": "long"
        },
        "title": {
            "index": "true",
            "type": "text",
            "analyzer": "ik_max_word"
        },
        "author": {
            "index": "true",
            "type": "keyword"
        },
        "cname": {
            "index": "true",
            "type": "keyword"
        },
        "from_where": {
            "index": "true",
            "type": "keyword"
        },
        "labels": {
            "index": "true",
            "type": "text",
            "analyzer": "ik_max_word"
        },
        "publish_time": {
            "index": "true",
            "type": "date",
            "format": "yyyy-MM-dd HH:mm:ss"
        },
        "like_number": {
            "index": "true",
            "type": "integer"
        },
        "comment_number": {
            "index": "true",
            "type": "integer"
        },
        "scan_number": {
            "index": "true",
            "type": "integer"
        },
        "status": {
            "index": "true",
            "type": "integer"
        },
        "is_top": {
            "index": "true",
            "type": "integer"
        },
        "month_day": {
            "index": "true",
            "type": "keyword"
        },
        "year": {
            "index": "true",
            "type": "keyword"
        },
        "pictures": {
            "index": "true",
            "type": "keyword"
        },
        "brief": {
            "index": "true",
            "type": "text",
            "analyzer": "ik_max_word"
        },
        "content": {
            "index": "true",
            "type": "text",
            "analyzer": "ik_max_word"
        }
    }
}
```

3、验证

```
PUT /article_index/_doc/235903824002904064
{
    "id": 235903824002904064,
    "author": "spartacus",
    "brief": "为啥平安在脉脉上的风评这么差？清一色的劝退、别来。 本人平安科技技术岗，总体感觉还行。 早九晚六，不加班，6-7点很多人就走了，9点以后办公室基本没几个人了，除非发版会比较晚。 每次过节都有过节费，总计5880元/年，每个月都有福利礼包。 薪资待遇也还行，虽然base确实不高，但拿个普通c绩效年终",
    "cname": "人文",
    "like_number": 5,
    "comment_number": 5,
    "content": "<p>随便写点什么吧</p>",
    "from_where": "原创",
    "is_top": 1,
    "labels": "脉脉,风评差",
    "month_day": "05.10",
    "year": "2021",
    "pictures": "https://github.githubassets.com/images/icons/emoji/unicode/1f9d1.png?v8",
    "publish_time": "2021-03-15 15:59:08",  #必须给格式化的字符串类型
    "scan_number": 100,
    "status": 0,
    "title": "为啥平安在脉脉上的风评这么差？"
}

GET /article_index/_doc/235903824002904064
```



# 三、CHAT

1、创建索引

```
PUT /chat_message_index

注：
删除索引
DELETE /chat_message_index

删除索引下的所有数据
POST /chat_message_index/_delete_by_query
{
 "query": {
   "match_all" :{}
 }
}

查询所有数据
POST /chat_message_index/_search
{
    "query": {
        "match_all": {}
    },
    "sort": [
        {
            "sendTime": {
                "order": "desc"
            }
        }
    ]
}
```

2、创建mapping

```
PUT /chat_message_index/_mapping
{
    "properties": {
        "id": {
            "index": "true",
            "type": "long"
        },
        "belongProviderUserId": {
            "index": "true",
            "type": "text"
        },
        "chatId": {
            "index": "true",
            "type": "text"
        },
        "code": {
            "index": "true",
            "type": "integer"
        },
        "from": {
            "index": "true",
            "type": "text"
        },
        "to": {
            "index": "true",
            "type": "text"
        },
        "content": {
            "index": "true",
            "type": "keyword"
        },
        "type": {
            "index": "true",
            "type": "integer"
        },
        "fromNickname": {
            "index": "true",
            "type": "text"
        },
        "fromHeadimage": {
            "index": "true",
            "type": "text"
        },
        "fromProviderId": {
            "index": "true",
            "type": "text"
        },
        "fromProviderUserId": {
            "index": "true",
            "type": "text"
        },
        "toNickname": {
            "index": "true",
            "type": "text"
        },
        "toHeadimage": {
            "index": "true",
            "type": "text"
        },
        "toProviderId": {
            "index": "true",
            "type": "text"
        },
        "toProviderUserId": {
            "index": "true",
            "type": "text"
        },
        "sendTime": {
            "index": "true",
            "type": "date",
            "format": "yyyy-MM-dd HH:mm:ss"
        }
    }
}
```

3、验证

```
PUT /chat_message_index/_doc/246482648847446016
{
  "id": 246482648847446016,
  "belongProviderUserId":"123",
  "chatId": "123456",
  "code": 200,
  "from": "123",
  "to": "456",
  "content": "哈哈哈哈",
  "type": 2,
  "fromNickname": "张三",
  "fromHeadimage": "http://image.com/1111/hhh/123.JPG",
  "fromProviderId": "spartacus123",
  "fromProviderUserId": "123",
  "toNickname": "李四",
  "toHeadimage": "http://image.com/1111/hhh/456.JPG",
  "toProviderId": "spartacus456",
  "toProviderUserId": "456",
  "sendTime": "2021-04-02 20:59:57"  #必须给格式化的字符串类型
}

GET /chat_message_index/_doc/246482648847446016
```



# 四、login

1、创建索引

```
PUT /login_record_index

注：
删除索引
DELETE /login_record_index

删除索引下的所有数据
POST /login_record_index/_delete_by_query
{
 "query": {
   "match_all" :{}
 }
}

查询所有数据
POST /login_record_index/_search
{
    "query": {
        "match_all": {}
    },
    "sort": [
        {
            "login_time": {
                "order": "desc"
            }
        }
    ]
}
```

2、创建mapping

```
PUT /login_record_index/_mapping
{
    "properties": {
        "id": {
            "index": "true",
            "type": "long"
        },
        "username": {
            "index": "true",
            "type": "text"
        },
        "user_type": {
            "index": "true",
            "type": "text"
        },
        "ip": {
            "index": "true",
            "type": "text"
        },
        "province": {
            "index": "true",
            "type": "text"
        },
        "city": {
            "index": "true",
            "type": "text"
        },
        "client": {
            "index": "true",
            "type": "text"
        },
        "login_time": {
            "index": "true",
            "type": "date",
            "format": "yyyy-MM-dd HH:mm:ss"
        }
    }
}
```

3、验证

```
PUT /login_record_index/_doc/246482648847446016
{
  "id": 246482648847446016,
  "username":"123",
  "user_type": "qq",
  "ip": "127.0.0.1",
  "province": "广东省",
  "city": "深圳市",
  "client": "spartacus-sunday",
  "login_time": "2021-04-02 20:59:57"  #必须给格式化的字符串类型
}

GET /login_record_index/_doc/246482648847446016
```

