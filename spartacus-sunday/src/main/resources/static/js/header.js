var auth_domain = getConfig('auth_domain');
var base_domain = getConfig('base_domain');

function chatTips() {
    layer.tips('快来和大家一起聊天吧！', '#chatButton', {
        tips : [4, '#000000'],
        time : 1000
    });
}

function unLoginTips() {
    layer.tips('亲，您还没有登录', '#loginButton', {
        tips : [2, '#000000'],
        time : 1000
    });
}

function loginTips() {
    var jwt = JSON.parse(localStorage.getItem('social-jwt') || '[]');
    layer.tips('Hello，'+jwt.nickname+'!', '#loginButton', {
        tips : [2, '#000000'],
        time : 1000
    });
}

function setAttrsWhenJwtValid() {
    //设置头像
    var jwt = JSON.parse(localStorage.getItem('social-jwt') || '[]');
    $("#loginButton").attr("src", jwt.headimage);
    $("#loginButton").attr("onmouseover", "loginTips()");

    //设置菜单
    $("#qqLoginBtn").css("display", "none");
    $("#wxLoginBtn").css("display", "none");
    $("#logoutBtn").css("display", "block");
}

function setAttrsWhenJwtInvalid() {
    //设置头像
    $("#loginButton").attr("src", "img/login.png");
    $("#loginButton").attr("onmouseover", "unLoginTips()");

    //设置菜单
    $("#qqLoginBtn").css("display", "block");
    $("#wxLoginBtn").css("display", "block");
    $("#logoutBtn").css("display", "none");
    $("#loginButton").attr("onmouseover", "unLoginTips()");
}

/**
 * 弹出社交登陆窗口
 */
function openLoginWindow(url, name, newWinWidth, newWinHeight) {
    var winWidth=screen.width;  //获取屏幕宽度
    var winHeight=screen.height;  //获取屏幕高度
    //获取新窗口距离屏幕左侧的位置
    var left=(winWidth-newWinWidth)/2;
    //获取新窗口距离屏幕顶部的位置
    var top=(winHeight-newWinHeight)/2 - 40;
    //name:弹出窗口的名字,可不填，用''代替
    //toolbar=no 是否显示工具栏。yes为显示
    //menubar=no 是否显示菜单栏。yes为显示
    //location=no 是否显示地址字段。yes为显示
    //status=no 是否添加状态栏。yes为可以
    window.open(url,name,'width='+newWinWidth+',height='+newWinHeight+',left='+left+',top='+top+',toolbar=no,menubar=no,location=no,status=no');

    // 通过监听，实现父子页面之间通信（父：前端页面，子：小窗）
    window.addEventListener('message', function (event) {
        if (event.origin !== auth_domain) return;

        // 保存token
        localStorage.setItem('social-jwt', JSON.stringify(event.data.data));

        //设置元素属性
        setAttrsWhenJwtValid();

        //定时检查token
        global_handler = window.setInterval(function () {
            if(!checkJwtValid()) {
                setAttrsWhenJwtInvalid();
                window.clearInterval(global_handler);
            }
        }, 10000);

    }, false)
}

/**
 * 创建聊天窗口
 */
var global_chat_window_iframe_index = null;
var global_chat_window_iframe_left = null;
var global_chat_window_iframe_top = null;
var global_chat_window_open_timesramp = null;
function openChatWindow() {
    if(global_chat_window_iframe_index != null) {
        layer.restore(global_chat_window_iframe_index);
        $('.layui-layer-iframe').css("top", global_chat_window_iframe_top);
        $('.layui-layer-iframe').css("left", global_chat_window_iframe_left);
    } else {
        global_chat_window_iframe_index = layer.open({
            type: 2,
            scrollbar: false,
            minStack: false,
            title: ['spartacus', 'font-size:20px;height:60px;background: linear-gradient(180deg,#E1E1E1 0,#E1E1E1 1%,hsla(0,0%,100%,0))'],
            shadeClose: false,
            maxmin: true, //开启最大化最小化按钮
            area: ['50%', '100%'],
            offset: 'rt',
            shade: [0.4, '#393D49'],
            anim: 6,
            content: 'wechat.html',
            id: 'wechat-window',
            success: function(layero, index){ //层弹出后的成功回调方法
                global_chat_window_iframe_left=$(layero).css('left');
                global_chat_window_iframe_top=$(layero).css('top');
            },
            cancel: function(index, layero){ //右上角关闭按钮触发的回调
                global_chat_window_iframe_index = null;
            },
            end: function(){ //层销毁后触发的回调
                global_chat_window_iframe_index = null;
            },
            min: function (layero, index){ //右上角最小化按钮触发的回调：设置堆叠位置
                var left=$('#index_id').width()-180;
                var top=$('#index_id').height()-60;

                $('.layui-layer-iframe').css("top", top+'px');
                $('.layui-layer-iframe').css("left", left+'px');
            },
            restore: function (layero, index){ //右上角回复按钮触发的回调：设置堆叠位置
                $('.layui-layer-iframe').css("top", global_chat_window_iframe_top);
                $('.layui-layer-iframe').css("left", global_chat_window_iframe_left);
            }
        });

        //缓存当前聊天窗口创建的时间戳
        global_chat_window_open_timesramp = new Date().getTime();
        localStorage.setItem("openChatWindowIndexTimeStamp", global_chat_window_open_timesramp);
    }
}
//监听多个tab页面，如果在别的页面打开了聊天窗口，则关闭当前页面聊天窗口
window.onstorage = (e) => {
    if(localStorage.getItem("openChatWindowIndexTimeStamp")!=global_chat_window_open_timesramp) {
        layer.close(global_chat_window_iframe_index);
        console.log(localStorage.getItem("openChatWindowIndexTimeStamp"));
    }
}

// 点击遮罩时触发：最小化聊天窗口、设置堆叠位置
$(document).on('click', '.layui-layer-shade', function() {
    //最小化聊天窗口
    layer.min(global_chat_window_iframe_index);

    //设置堆叠位置
    var left=$('#index_id').width()-180;
    var top=$('#index_id').height()-60;

    $('.layui-layer-iframe').css("top", top+'px');
    $('.layui-layer-iframe').css("left", left+'px');
});


//监听搜索框回车动作
$('#search_input_id').keypress(function(e){
    if(e.keyCode==13) {
        e.preventDefault(); //禁止提交表单
        beforeSearch();
    }
});

function beforeSearch() {
    var searchContent = $("#search_input_id").val();
    if(!$.trim(searchContent)){ // "",null,undefined
        parent.layer.msg('搜索内容不能为空！', function () {});
        $("#search_input_id").val("");
        $("#search_input_id").focus();
        return;
    } else {
        $('#main').load('search.html');
    }
}

$(document).ready(function() {
    //1、设置社交登陆按钮单击事件
    $('#qqLoginHref').attr("href", "javascript:openLoginWindow('"+auth_domain+"/auth/qq', '', 750, 450);");
    $('#wxLoginHref').attr("href", "javascript:openLoginWindow('"+auth_domain+"/auth/weixin', '', 850, 500);");

    //2、打开网页立即检查token是否有效
    var global_handler = null;
    if(checkJwtValid()) {
        //设置元素属性
        setAttrsWhenJwtValid();

        //定时检查token
        global_handler = window.setInterval(function () {
            if(!checkJwtValid()) {
                setAttrsWhenJwtInvalid();
                window.clearInterval(global_handler);
            }
        }, 10000);

    } else {
        //设置元素属性
        setAttrsWhenJwtInvalid();
    }

    //3、index页面顶部菜单选项点击事件
    $('.index-top-menu').click(function () {
        $('.index-top-menu').removeClass('top-menu-bg-color');
        $(this).addClass('top-menu-bg-color');

        localStorage.setItem("sunday-last-cname", localStorage.getItem("sunday-curr-cname"));
        localStorage.setItem("sunday-curr-cname", $(this).text() == '首页' ? '' : $(this).text());

        localStorage.setItem("sunday-last-page", 'index.html');

        if(localStorage.getItem("sunday-curr-cname") == '归档') {
            $('#main').load('archive.html');
        } else {
            global_currentPage=1;
            global_pageSize=10;
            global_totalPages=1;
            global_hasLoadHistory=false; //标志是否加载过
            $('#main').load('articles.html');
        }
    });

    //4、article页面顶部菜单选项点击事件
    $('.article-top-menu').click(function () {
        localStorage.setItem("menuId", $(this).attr('id'));

        localStorage.setItem("sunday-last-cname", '');
        localStorage.setItem("sunday-curr-cname", $(this).text() == '首页' ? '' : $(this).text());

        localStorage.setItem("sunday-last-page", 'article.html');
    });

});
