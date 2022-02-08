var auth_domain = getConfig('auth_domain');
var base_domain = getConfig('base_domain');


//左侧栏
function loadBlogProfile(){
    $.ajax({
        type : "GET",
        url : base_domain+"/spartacus-system/system/loadBlogProfile",
        async : true,
        dataType:"json",
        headers: {
            "clientId":"spartacus-sunday",
            "clientCurrentId":getClientCurrentId()
        },
        success : function(result) {
            if (result.code==200) {
                $("#headImg").attr("src", result.data.headImg);
                $("#nickname").text(result.data.nickname);
                $("#title").text(result.data.title);
                $("#age").text(result.data.age);
                $("#experience").text(result.data.experience);
                $("#github").attr("href", result.data.github);
            } else {
                parent.layer.msg("博主资料加载失败！", {
                    icon: 2,
                    time:1000
                });
            }
        },
        error: function (xhr, status, error) {
            layer.msg("博主资料加载失败！", {
                icon: 2,
                time:1000
            });
        }
    });
}

function getNotices() {
    $.ajax({
        type : "POST",
        url : base_domain+"/spartacus-system/system/findNoticeByCriteria",
        async : true,
        data : { //使用Json格式进行透传
            "isForSite" : 1
        },
        dataType:"json",
        headers: {
            "clientId":"spartacus-sunday",
            "clientCurrentId":getClientCurrentId()
        },
        success : function(result) {
            if (result.code==200) {
                $("#noticeContainer").children().remove();
                var records = result.data;
                for(var i = 0; i < records.length; i++) {
                    var id = records[i]['id'];
                    var originContent = records[i]['content'];
                    var pureContent = records[i]['content'].replace(/<\/?.+?>/g, ""); //纯净文本
                    var createTime = records[i]['createTime'];

                    global_notices[id] = originContent;
                    $("#noticeContainer").append("<div class='sidebox__user'><div class='sidebox__user-title'style='width:85%;'><h5><p style='overflow: hidden;-webkit-line-clamp: 2;display:-webkit-box;-webkit-box-orient: vertical;'><a href='#'style='line-height: 16px;'notice-id="+id+" onclick='viewNotice(this)'>"+pureContent+"</a></p></h5></div><button class='sidebox__user-btn'type='button'><i class='fa fa-info'notice-id="+id+" onclick='viewNotice(this)'></i></button></div>");
                }
            } else {
                layer.msg("通知公告加载失败！", {
                    icon: 2,
                    time:1000
                });
            }
        },
        error: function (xhr, status, error) {
            layer.msg("通知公告加载失败！", {
                icon: 2,
                time:1000
            });
        }
    });
}

function viewNotice(ele) {
    var noticeId= $(ele).attr('notice-id');
    swal({
        content: {
            element: "span",
            attributes: {
                innerHTML: global_notices[noticeId],
                style: "float:left;text-align:left;"
            },
        },
    });
}

//中间-文章列表
function findByCategory() {
    if(global_currentPage > global_totalPages) {
        parent.layer.msg("已经加载完啦", function(){});
        return;
    }

    $.ajax({
        type : "POST",
        url : base_domain+"/spartacus-article/article/findByCategory",
        async : true,
        data : { //使用Json格式进行透传
            "cname" : localStorage.getItem("sunday-curr-cname"),
            "currentPage" : global_currentPage,
            "pageSize" : global_pageSize
        },
        dataType:"json",
        headers: {
            "clientId":"spartacus-sunday",
            "clientCurrentId":getClientCurrentId()
        },
        success : function(result) {
            if (result.code==0) {
                var articles = result.data.records;
                if(!global_hasLoadHistory) {
                    if(!$.isEmptyObject(articles)) {
                        $("#postContainer").children().remove();
                    }
                }

                for(var i = 0; i < articles.length; i++) {
                    var id = articles[i]['id'];
                    var author = articles[i]['author'];
                    var brief = articles[i]['brief'];
                    var cname = articles[i]['cname'];
                    var likeNumber = articles[i]['likeNumber'];
                    var commentNumber = articles[i]['commentNumber'];
                    var fromWhere = articles[i]['fromWhere'];
                    var isTop = articles[i]['isTop'];
                    var labels = articles[i]['labels'];
                    var monthDay = articles[i]['monthDay'];
                    var pictures = [];
                    if(!isNullOrEmpty(articles[i].pictures)) {
                        pictures = articles[i].pictures.split(";");
                    }
                    var publishTime = articles[i]['publishTime'];
                    var scanNumber = articles[i]['scanNumber'];
                    var status = articles[i]['status'];
                    var title = articles[i]['title'];
                    var year = articles[i]['year'];

                    var labelHtml = '';
                    if(labels != null && labels !='') {
                        var labelArray = labels.split(',');
                        for(var j = 0; j < labelArray.length; j++) {
                            labelHtml += "<a style='cursor: default;' href='#'>"+labelArray[j]+"</a>";
                        }
                    }

                    if(isTop==1) {
                        $("#postContainer").append("<div class='post'><div class='post__head'><a target='_blank' href='article.html?articleId="+id+"' article-id="+id+"><h2 class='post__title'><font color='#fa7268'>[置顶]</font>"+title+"</h2></a><div class='post__dropdown'><a class='dropdown-toggle post__dropdown-btn'href='#'role='button'id='dropdownMenu_"+id+"'data-toggle='dropdown'aria-haspopup='true'aria-expanded='false'><i class='icon ion-md-more'></i></a><ul class='dropdown-menu dropdown-menu-right post__dropdown-menu' aria-labelledby='dropdownMenu_"+id+"'><li><a class='copy-link' href='javascript:void(0);' article-id='"+id+"'>复制链接</a></li><li><a href='javascript:void(0);' onclick='share(this)' article-id='"+id+"'>分享至朋友圈</a></li></ul></div></div><div class='post__wrap'><div class='post__company'><i class='icon ion-ios-person-add'></i><span><font style='vertical-align: inherit;'><font style='vertical-align: inherit;'>"+author+"</font></font></span></div><div class='post__location'><i class='icon ion-ios-time'></i><span><font style='vertical-align: inherit;'><font style='vertical-align: inherit;'>"+millisecondsToLocalDate(publishTime)+"</font></font></span></div></div><div class='post__description'><p style='overflow: hidden;-webkit-line-clamp: 3;display:-webkit-box;-webkit-box-orient: vertical;'>"+brief+"</p><a target='_blank' href='article.html?articleId="+id+"' article-id="+id+">查看更多</a></div><div class='post__tags'>"+labelHtml+"</div><div class='post__stats'><div><a id='btn_like_"+id+"' class='post__likes' href='javascript:void(0);' onclick='like(this)' article-id="+id+"><i id='icon_like_"+id+"' class='icon ion-ios-heart'></i><span id='like_number_"+id+"'>"+likeNumber+"</span></a><a style='cursor: default;' class='post__comments' data-toggle='collapse'href='#collapse_"+id+"'role='button'aria-expanded='false'aria-controls='collapse_"+id+"'><i class='icon ion-ios-text'></i><span>"+commentNumber+"</span></a></div><div class='post__views'><i class='icon ion-ios-eye'></i><span>"+scanNumber+"</span></div></div></div>");
                    } else {
                        $("#postContainer").append("<div class='post'><div class='post__head'><a target='_blank' href='article.html?articleId="+id+"' article-id="+id+"><h2 class='post__title'>"+title+"</h2></a><div class='post__dropdown'><a class='dropdown-toggle post__dropdown-btn'href='#'role='button'id='dropdownMenu_"+id+"'data-toggle='dropdown'aria-haspopup='true'aria-expanded='false'><i class='icon ion-md-more'></i></a><ul class='dropdown-menu dropdown-menu-right post__dropdown-menu' aria-labelledby='dropdownMenu_"+id+"'><li><a class='copy-link' href='javascript:void(0);' article-id='"+id+"'>复制链接</a></li><li><a href='javascript:void(0);' onclick='share(this)' article-id='"+id+"'>分享至朋友圈</a></li></ul></div></div><div class='post__wrap'><div class='post__company'><i class='icon ion-ios-person-add'></i><span><font style='vertical-align: inherit;'><font style='vertical-align: inherit;'>"+author+"</font></font></span></div><div class='post__location'><i class='icon ion-ios-time'></i><span><font style='vertical-align: inherit;'><font style='vertical-align: inherit;'>"+millisecondsToLocalDate(publishTime)+"</font></font></span></div></div><div class='post__description'><p style='overflow: hidden;-webkit-line-clamp: 3;display:-webkit-box;-webkit-box-orient: vertical;'>"+brief+"</p><a target='_blank' href='article.html?articleId="+id+"' article-id="+id+">查看更多</a></div><div class='post__tags'>"+labelHtml+"</div><div class='post__stats'><div><a id='btn_like_"+id+"' class='post__likes' href='javascript:void(0);' onclick='like(this)' article-id="+id+"><i id='icon_like_"+id+"' class='icon ion-ios-heart'></i><span id='like_number_"+id+"'>"+likeNumber+"</span></a><a style='cursor: default;' class='post__comments' data-toggle='collapse'href='#collapse_"+id+"'role='button'aria-expanded='false'aria-controls='collapse_"+id+"'><i class='icon ion-ios-text'></i><span>"+commentNumber+"</span></a></div><div class='post__views'><i class='icon ion-ios-eye'></i><span>"+scanNumber+"</span></div></div></div>");
                    }

                }

                checkMulLikeMember(articles);

                global_totalPages = result.data.totalPages;
                global_currentPage += 1;
                global_hasLoadHistory = true;

            } else {
                parent.layer.msg("文章列表加载失败！", {
                    icon: 2,
                    time:1000
                });
            }
        },
        error: function (xhr, status, error) {
            layer.msg("文章列表加载失败！", {
                icon: 2,
                time:1000
            });
        }
    });
}

//右侧栏
function findLikeMost() {
    $.ajax({
        type : "POST",
        url : base_domain+"/spartacus-article/article/findByCriteria",
        async : true,
        data : { //使用Json格式进行透传
            "isLikeMost" : 1,
            "isReadMost" : 0
        },
        dataType:"json",
        headers: {
            "clientId":"spartacus-sunday",
            "clientCurrentId":getClientCurrentId()
        },
        success : function(result) {
            console.log(result);

            if (result.code==0) {
                $("#likeMostContainer").children().remove();
                var articles = result.data;
                for(var i = 0; i < articles.length; i++) {
                    var id = articles[i]['id'];
                    var author = articles[i]['author'];
                    var brief = articles[i]['brief'];
                    var cname = articles[i]['cname'];
                    var likeNumber = articles[i]['likeNumber'];
                    var commentNumber = articles[i]['commentNumber'];
                    var fromWhere = articles[i]['fromWhere'];
                    var isTop = articles[i]['isTop'];
                    var labels = articles[i]['labels'];
                    var monthDay = articles[i]['monthDay'];
                    var pictures = articles[i]['pictures'].split(";");
                    var publishTime = articles[i]['publishTime'];
                    var scanNumber = articles[i]['scanNumber'];
                    var status = articles[i]['status'];
                    var title = articles[i]['title'];
                    var year = articles[i]['year'];

                    $("#likeMostContainer").append("<div class='sidebox__job'><div class='sidebox__job-title'><a style='overflow: hidden; -webkit-line-clamp: 1; display: -webkit-box; -webkit-box-orient: vertical; width: 80%;' target='_blank' href='article.html?articleId="+id+"' article-id="+id+">"+title+"</a><span>"+displayTime(millisecondsToLocalDate(publishTime))+"</span></div><p class='sidebox__job-description'style='overflow: hidden;-webkit-line-clamp: 1;display:-webkit-box;-webkit-box-orient: vertical;'>"+brief+"</p></div>");
                }

            } else {
                parent.layer.msg("点赞最多加载是吧！", {
                    icon: 2,
                    time:1000
                });
            }
        },
        error: function (xhr, status, error) {
            layer.msg("点赞最多加载失败！", {
                icon: 2,
                time:1000
            });
        }
    });
}

function findReadMost() {
    $.ajax({
        type : "POST",
        url : base_domain+"/spartacus-article/article/findByCriteria",
        async : true,
        data : { //使用Json格式进行透传
            "isLikeMost" : 0,
            "isReadMost" : 1
        },
        dataType:"json",
        headers: {
            "clientId":"spartacus-sunday",
            "clientCurrentId":getClientCurrentId()
        },
        success : function(result) {
            if (result.code==0) {
                $("#readMostContainer").children().remove();
                var articles = result.data;
                for(var i = 0; i < articles.length; i++) {
                    var id = articles[i]['id'];
                    var author = articles[i]['author'];
                    var brief = articles[i]['brief'];
                    var cname = articles[i]['cname'];
                    var likeNumber = articles[i]['likeNumber'];
                    var commentNumber = articles[i]['commentNumber'];
                    var fromWhere = articles[i]['fromWhere'];
                    var isTop = articles[i]['isTop'];
                    var labels = articles[i]['labels'];
                    var monthDay = articles[i]['monthDay'];
                    var pictures = articles[i]['pictures'].split(";");
                    var publishTime = articles[i]['publishTime'];
                    var scanNumber = articles[i]['scanNumber'];
                    var status = articles[i]['status'];
                    var title = articles[i]['title'];
                    var year = articles[i]['year'];

                    $("#readMostContainer").append("<div class='sidebox__job'><div class='sidebox__job-title'><a style='overflow: hidden; -webkit-line-clamp: 1; display: -webkit-box; -webkit-box-orient: vertical; width: 80%;' target='_blank' href='article.html?articleId="+id+"' article-id="+id+">"+title+"</a><span>"+displayTime(millisecondsToLocalDate(publishTime))+"</span></div><p class='sidebox__job-description'style='overflow: hidden;-webkit-line-clamp: 1;display:-webkit-box;-webkit-box-orient: vertical;'>"+brief+"</p></div>");
                }

            } else {
                parent.layer.msg("阅读最多加载是吧！", {
                    icon: 2,
                    time:1000
                });
            }
        },
        error: function (xhr, status, error) {
            layer.msg("阅读最多加载失败！", {
                icon: 2,
                time:1000
            });
        }
    });
}

function getRecentComments() {
    $.ajax({
        type : "GET",
        url : base_domain+"/spartacus-comment/comment/getRecentComments",
        async : true,
        dataType:"json",
        headers: {
            "clientId":"spartacus-sunday",
            "clientCurrentId":getClientCurrentId()
        },
        success : function(result) {
            console.log(result);

            if (result.code==0) {
                $("#recentCommentsContainer").children().remove();
                var recentComments = result.data;
                for (var i = 0; i < recentComments.length; i++) {
                    var articleId = recentComments[i]['articleId'];
                    var nickname = recentComments[i]['nickname'];
                    var headImg = recentComments[i]['headImg'];
                    var content = recentComments[i]['content'];
                    var publishTime = displayTime(millisecondsToLocalDate(recentComments[i]['publishTime']));

                    $("#recentCommentsContainer").append("<div class='sidebox__user'style='align-items: flex-start'><div style='width:15%'><a href='javascript:void(0);'class='sidebox__user-img'article-id="+articleId+"><img src='"+headImg+"'style='width:33px;height:33px'></a></div><div class='sidebox__user-title'style='width:85%'><h5><a style='width: 80%;float: left;' href='javascript:void(0);' article-id="+articleId+">"+nickname+"</a><span style='float: right;width: 20%;'>"+publishTime+"</span></h5><br><p class='sidebox__job-description'style='overflow: hidden;-webkit-line-clamp: 1;display:-webkit-box;-webkit-box-orient: vertical;'>"+content+"</p></div></div>");
                }
            } else {
                parent.layer.msg("最近评论加载失败！", {
                    icon: 2,
                    time:1000
                });
            }
        },
        error: function (xhr, status, error) {
            layer.msg("最近评论加载失败！", {
                icon: 2,
                time:1000
            });
        }
    });
}