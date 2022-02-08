function genUuid() {
    var d = Date.now();
    if (typeof performance !== 'undefined' && typeof performance.now === 'function'){
        d += performance.now(); //use high-precision timer if available
    }
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
        var r = (d + Math.random() * 16) % 16 | 0;
        d = Math.floor(d / 16);
        return (c === 'x' ? r : (r & 0x3 | 0x8)).toString(16);
    });
}

//获取地址栏中的参数
function getUrlParam(name) {
	var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
	var r = window.location.search.substr(1).match(reg);
	if(r!=null) {
		return  unescape(r[2]);	
	}
	return null;
}


//时间毫秒值 转换到 yyyy-mm-dd hh:mm:ss 格式
function millisecondsToLocalDate (milliseconds) {
	Date.prototype.toLocaleString = function() {
        return this.getFullYear() + "-" + ("0" + (this.getMonth() + 1)).slice(-2) + "-" + ("0" + this.getDate()).slice(-2) + " " + ("0" + this.getHours()).slice(-2) + ":" + ("0" + this.getMinutes()).slice(-2) + ":" + ("0" + this.getSeconds()).slice(-2);
    };
    var unixTimestamp = new Date(milliseconds) ;
	return unixTimestamp.toLocaleString();
}


//转化成指定格式的时间字符串
function convertDateToGivenFormat (date, fmt) {
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符， 
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字) 
Date.prototype.Format = function () { //author: meizz 
    var o = {
        "M+": this.getMonth() + 1, //月份 
        "d+": this.getDate(), //日 
        "H+": this.getHours(), //小时 
        "m+": this.getMinutes(), //分 
        "s+": this.getSeconds(), //秒 
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度 
        "S": this.getMilliseconds() //毫秒 
    };
    if (/(y+)/.test(fmt)) fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
    if (new RegExp("(" + k + ")").test(fmt)) fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}
//调用
var _date = date.Format(fmt);
return _date;
}


// 对Date的扩展，将 Date 转化为指定格式的String
// 月(M)、日(d)、小时(h)、分(m)、秒(s)、季度(q) 可以用 1-2 个占位符，
// 年(y)可以用 1-4 个占位符，毫秒(S)只能用 1 个占位符(是 1-3 位的数字)
// 例子：
// (new Date()).Format("yyyy-MM-dd hh:mm:ss.S") ==> 2006-07-02 08:09:04.423
// (new Date()).Format("yyyy-M-d h:m:s.S")      ==> 2006-7-2 8:9:4.18
Date.prototype.Format = function (fmt) {
    var o = {
        "M+": this.getMonth() + 1,                 //月份
        "d+": this.getDate(),                    //日
        "h+": this.getHours(),                   //小时
        "m+": this.getMinutes(),                 //分
        "s+": this.getSeconds(),                 //秒
        "q+": Math.floor((this.getMonth() + 3) / 3), //季度
        "S": this.getMilliseconds()             //毫秒
    };
    if (/(y+)/.test(fmt))
        fmt = fmt.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    for (var k in o)
        if (new RegExp("(" + k + ")").test(fmt))
            fmt = fmt.replace(RegExp.$1, (RegExp.$1.length == 1) ? (o[k]) : (("00" + o[k]).substr(("" + o[k]).length)));
    return fmt;
}


//把 yyyy-mm-dd hh:mm:ss 格式的时间转换成  XXX年前/XXX月前/XXX天前/XXX小时前/XXX分钟前/刚刚发表
function displayTime(dateStr) {
    console.log("dateStr:" + dateStr)
    //将字符串转换成时间格式
    var timePublish = new Date(dateStr);
    var timeNow = new Date();
    var minute = 1000 * 60;
    var hour = minute * 60;
    var day = hour * 24;
    var month = day * 30;
    var year = month * 12;
    var diffValue = timeNow - timePublish;
    console.log("timeNow: " + timeNow )
    console.log("timePublish: " + timePublish)
    console.log("diffValue: " + diffValue)
    var diffYear = diffValue / year;
    var diffMonth = diffValue / month;
    var diffWeek = diffValue / (7 * day);
    var diffDay = diffValue / day;
    var diffHour = diffValue / hour;
    var diffMinute = diffValue / minute;
    if (diffValue <= 0) {
        result = "刚刚";
    }
    else if (diffYear > 1) {
        result = parseInt(diffYear) + "年前";
    }
    else if (diffMonth > 1) {
        result = parseInt(diffMonth) + "月前";
    }
    else if (diffWeek > 1) {
        result = parseInt(diffWeek) + "周前";
    }
    else if (diffDay > 1) {
        result = parseInt(diffDay) + "天前";
    }
    else if (diffHour > 1) {
        result = parseInt(diffHour) + "小时前";
    }
    else if (diffMinute > 1) {
        result = parseInt(diffMinute) + "分钟前";
    }
    else {
        result = "刚刚";
    }
    return result;
}


// 把时间字符串或者时间毫秒值，转化成指定的格式，调用方式如：formatDate("2010-04-30", "yyyy-MM-dd HH:mm:ss")
function formatDate(date, format) {
    if (!date) return;
    if (!format) format = "yyyy-MM-dd HH:mm:ss";
    switch(typeof date) {
        case "string":
            date = new Date(date.replace(/-/, "/"));
            break;
        case "number":
            date = new Date(date);
            break;
    }
    if (!date instanceof Date) return;
    var dict = {
        "yyyy": date.getFullYear(),
        "M": date.getMonth() + 1,
        "d": date.getDate(),
        "H": date.getHours(),
        "m": date.getMinutes(),
        "s": date.getSeconds(),
        "MM": ("" + (date.getMonth() + 101)).substr(1),
        "dd": ("" + (date.getDate() + 100)).substr(1),
        "HH": ("" + (date.getHours() + 100)).substr(1),
        "mm": ("" + (date.getMinutes() + 100)).substr(1),
        "ss": ("" + (date.getSeconds() + 100)).substr(1)
    };
    return format.replace(/(yyyy|MM?|dd?|HH?|ss?|mm?)/g, function() {
        return dict[arguments[0]];
    });
}

//判断是否为Null或者undefined或者为空字符串
function isNullOrEmpty(value) {
    //正则表达式用于判斷字符串是否全部由空格或换行符组成
    var reg = /^\s*$/
    //返回值为true表示不是空字符串
    return !(value != null && value != undefined && !reg.test(value))
}

//yyyyMMdd格式的日期字符串转成星期几
function yyyyMMddToWeek(yyyyMMdd) {
    var dateFormat = yyyyMMdd.replace(/^(\d{4})(\d{2})(\d{2})$/, "$1-$2-$3")
    var weeks = ["星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"];
    var date = new Date(Date.parse(dateFormat.replace(/-/g, "/")));
    return weeks[date.getDay()];
}

//yyyyMMdd格式的日期字符串转成XXXX年XX月XX日
function yyyyMMddToYMD(yyyyMMdd) {
    var dateFormat = yyyyMMdd.replace(/^(\d{4})(\d{2})(\d{2})$/, "$1年$2月$3日")
    return dateFormat;
}