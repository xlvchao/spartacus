/**
 * 加载配置信息
 */
function getConfig(key) {
    let value = '';
    $.ajaxSettings.async = false;
    $.getJSON("config/config.json", function (data, status) {
        if( status=='success') {
            value = data[key];
        }else{
            parent.layer.msg("无法读取config.json", {
                icon: 2,
                time:1000
            });
        }
    });
    return value;
}