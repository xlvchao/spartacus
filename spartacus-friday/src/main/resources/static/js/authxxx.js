
var base_domain = getConfig('base_domain');

function logOut() {
    //服务端
    var jwt = JSON.parse(window.localStorage.getItem('spartacus-jwt') || '{}');
    var access_token = jwt.access_token;
    if(!$.isEmptyObject(access_token)) {
        $.ajax({
            url: base_domain+"/spartacus-auth/auth/logout/"+access_token,
            type: "GET",
            async: true,
            headers: {
                "clientId":"spartacus-friday"
            }
        });
    }

    //客户端
    window.localStorage.removeItem('spartacus-jwt');
    layer.msg('退出成功！', {
        icon: 1,
        time: 1500 //（如果不配置，默认是3秒）
    }, function() {
        window.location.href=base_domain+"/spartacus-friday/login.html";
    });
}

function jumpToIndex() {
    var jwt = JSON.parse(window.localStorage.getItem('spartacus-jwt') || '{}');
    var access_token = jwt.access_token;
    if(!$.isEmptyObject(access_token)) {
        $.ajax({
            url: base_domain+"/spartacus-gateway/jwt/decode/"+access_token,
            type: "GET",
            dataType: "json",
            async: false,
            headers: {
                "clientId":"spartacus-friday"
            },
            success: function (result) {
                if(result.code == 200) {
                    var authorities = result.data.claims.authorities;
                    if(authorities.indexOf("ROLE_ADMIN") > -1) {
                        window.location.href=base_domain+"/spartacus-friday/index.html";
                    } else {
                        parent.layer.msg("您无权访问！", {
                            icon: 2,
                            time:2000
                        });
                    }
                } else {
                    parent.layer.msg(result.message, {
                        icon: 2,
                        time:2000
                    });
                }
            }
        });
    } else {
        parent.layer.msg("您无权访问！", {
            icon: 2,
            time:1000
        });
    }
}

function checkJwtValid() {
    var jwt = JSON.parse(window.localStorage.getItem('spartacus-jwt') || '{}');
    var access_token = jwt.access_token;
    if(!$.isEmptyObject(access_token)) {
        $.ajax({
            url: base_domain+"/spartacus-gateway/jwt/decode/"+access_token,
            type: "GET",//方法类型
            dataType: "json",//预期服务器返回的数据类型
            async: false, //使用同步的方式,true为异步方式
            headers: {
                "clientId":"spartacus-friday"
            },
            success: function (result) {
                if(result.code != 200) {
                    parent.layer.msg("凭证已失效，请重新登陆！", {
                        icon: 2,
                        time:2000
                    }, function () {
                        window.location.href=base_domain+"/spartacus-friday/login.html";
                    });
                }
            },
            error: function (data, status, e) {
                parent.layer.msg("凭证已失效，请重新登陆！", {
                    icon: 2,
                    time:2000
                }, function () {
                    window.location.href=base_domain+"/spartacus-friday/login.html";
                });
            }
        });
    } else {
        parent.layer.msg("凭证已失效，请重新登陆！", {
            icon: 2,
            time:2000
        }, function () {
            window.location.href=base_domain+"/spartacus-friday/login.html";
        });
    }

}