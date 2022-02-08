var auth_domain = getConfig('auth_domain');
var base_domain = getConfig('base_domain');

function logOut() {
    //服务端
    var jwt = JSON.parse(window.localStorage.getItem('social-jwt') || '{}');
    var access_token = jwt.access_token;

    if(!isNullOrEmpty(access_token)) {
        $.ajax({
            url: auth_domain + "/auth/logout/"+access_token,
            type: "GET",
            async: true
        });
    }

    //客户端
    window.localStorage.removeItem('social-jwt');
    $("#loginButton").attr("src", "img/login.png");
    $("#loginButton").attr("onmouseover", "unLoginTips()");
    layer.msg('退出成功！', {
        icon: 1,
        time: 1500 //（如果不配置，默认是3秒）
    }, function () {
        $("#qqLoginBtn").css("display", "block");
        $("#wxLoginBtn").css("display", "block");
        $("#logoutBtn").css("display", "none");
    });
}

function checkJwtValid() {
    var jwt = JSON.parse(window.localStorage.getItem('social-jwt') || '{}');
    var access_token = jwt.access_token;

    let valid = false;
    if(!isNullOrEmpty(access_token)) {
        $.ajax({
            url: base_domain + "/spartacus-gateway/jwt/decode/"+access_token,
            type: "GET",//方法类型
            dataType: "json",//预期服务器返回的数据类型
            async: false, //使用同步的方式,true为异步方式
            headers: {
                "clientId":"spartacus-sunday",
                "clientCurrentId":getClientCurrentId()
            },
            success: function (result) {
                if(result.code == 200) {
                    valid = true;
                } else {
                    window.localStorage.removeItem('social-jwt');
                    valid = false;
                }
            },
            error: function (xhr, status, error) {
                valid = false;
            }
        });
    }
    return valid;
}