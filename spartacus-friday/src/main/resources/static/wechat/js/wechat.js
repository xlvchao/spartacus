
var base_domain = getConfig('base_domain');

//底部扩展键
$(function() {
    $('#doc-dropdown-js').dropdown({justify: '#doc-dropdown-justify-js'});
});


//tab for three icon
$(document).ready(function(){
  	$(".sidestrip_icon a").click(function(){
      $(".sidestrip_icon a").eq($(this).index()).addClass("cur").siblings().removeClass('cur');
	  $(".middle").hide().eq($(this).index()).show();
    });
});

//input box focus
$(document).ready(function(){
	$('#input_box').focus();
	$('.windows_input').css('background','#fff');
	$('#input_box').css('background','#fff');
});

////////////////////////////////////////////////////////////
var stompClient = null;
function initConnect() {
	var jwt = JSON.parse(window.localStorage.getItem('spartacus-jwt') || '{}');
	var accessToken = jwt.access_token;
	var localProviderUserId = jwt.providerUserId;

	//检查token是否有效
	if(checkToken(accessToken)==true) {
		//初始化群聊记录
		queryRecentChatMessages("", "spartacus");

		//初始化私聊记录
		initPrivateChatContacts(localProviderUserId);

		//开始创建连接
		stompClient = new StompJs.Client({
			connectHeaders: {
				accessToken: accessToken
			},
			debug: function (str) {
				console.log(str);
			},
			reconnectDelay: 3000,
			heartbeatIncoming: 4000,
			heartbeatOutgoing: 4000,
		});

		stompClient.webSocketFactory = function () {
			return new SockJS(base_domain+'/spartacus-chat/endpoint');
		};

		stompClient.onConnect = function (frame) {
			console.log("onConnect-frame:" + frame)

			//订阅自己
			stompClient.subscribe('/queue/'+localProviderUserId, function (response) {
				console.log("收到信息：");
				var message = JSON.parse(response.body);
				console.log(message);
				showChatMessage(message);
				response.ack();
			}, {ack:'client'});

			layer.msg("开始聊天吧！");
		};

		stompClient.onStompError = function (frame) {
			console.log('Broker reported error: ' + frame.headers['message']);
			console.log('Additional details: ' + frame.body);
			layer.msg('连接失败，请登录后再试试吧！', {
				time: 1800
			}, function(){
				parent.layer.close(parent.global_chat_window_iframe_index);
			});

		};

		stompClient.activate();

	} else {
		layer.msg('连接失败，请登录后再试试吧！', {
			time: 1800
		}, function(){
			parent.layer.close(parent.global_chat_window_iframe_index);
		});
	}
}

//检查token是否有效
function checkToken(accessToken) {
	var valid = false;
	if(!$.isEmptyObject(accessToken)) {
		$.ajax({
			url: base_domain+"/spartacus-gateway/jwt/decode/"+accessToken,
			type: "GET",
			dataType: "json",
			async: false,
			headers: {
				"clientId":"spartacus-friday"
			},
			success: function (result) {
				console.log(result);
				if(result.code == 200) {
					valid = true;
				}
			}
		});
	}
	return valid;
}

//关闭/刷新页面、关闭浏览器时，断开链接
window.onbeforeunload = function(){
	if(stompClient != null && stompClient.connected) {
		stompClient.deactivate();
	}
}

//生成message
function genMessage(from,to,content,fromProviderId,fromProviderUserId,fromNickname,fromHeadimage,toProviderId,toProviderUserId,toNickname,toHeadimage){
	var message = {};
	message['from']=from;
	message['to']=to;
	message['content']=content;

	message['fromProviderId']=fromProviderId;
	message['fromProviderUserId']=fromProviderUserId;
	message['fromNickname']=fromNickname;
	message['fromHeadimage']=fromHeadimage;

	message['toProviderId']=toProviderId;
	message['toProviderUserId']=toProviderUserId;
	message['toNickname']=toNickname;
	message['toHeadimage']=toHeadimage;

	return message;
}

//发送聊天信息
function sendMessage(content) {
	if(stompClient == null || !stompClient.connected) {
		layer.msg('您掉线了，请重启对话框！');
		return;
	}

	var jwt = JSON.parse(window.localStorage.getItem('spartacus-jwt') || '{}');
	if(jwt==null || jwt=='{}') {
		layer.msg('您还没有登录，请先登录！');
		return;
	}


	var accessToken = jwt.access_token;
	
	var from = jwt.providerUserId;
	var to = $("#user_list li.user_active").attr('providerUserId');
	
	var fromNickname = jwt.nickname;
	var fromHeadimage = jwt.headimage;
	var fromProviderId = jwt.providerId;
	var fromProviderUserId = jwt.providerUserId;

	var toNickname = $("#user_list li.user_active").attr('nickname');
	var toHeadimage = $("#user_list li.user_active").attr('headimage');
	var toProviderId = $("#user_list li.user_active").attr('providerId');
	var toProviderUserId = $("#user_list li.user_active").attr('providerUserId');

	var message = genMessage(from,to,content,fromProviderId,fromProviderUserId,fromNickname,fromHeadimage,toProviderId,toProviderUserId,toNickname,toHeadimage);

	if("spartacus" === to) {
		stompClient.publish({
			destination: '/send/sendGroupChatMessage',
			body: JSON.stringify(message),
			headers: { accessToken:accessToken },
		});
	} else {
		stompClient.publish({
			destination: '/send/sendPrivateChatMessage',
			body: JSON.stringify(message),
			headers: { accessToken:accessToken },
		});
	}

}

//获取纯文本方法
function removeHTMLTag(str) {
	str = str.replace(/<\/?[^>]*>/g, ''); //去除HTML tag
	//str = str.replace(/[ | ]*\n/g, '\n'); //去除行尾空白
	//str = str.replace(/\n[\s| | ]*\r/g,'\n'); //去除多余空行
	//str = str.replace(/&nbsp;/ig, ''); //去掉&nbsp;
	//str = str.replace(/\s/g, ''); //将空格去掉
	return str;
}

//初始化聊天信息
function initChatMessages(message) {
	var chatbox = document.getElementById('chatbox');
	var office_text = document.getElementById('office_text');

	//自己
	var jwt = JSON.parse(window.localStorage.getItem('spartacus-jwt') || '{}');
	var localProviderUserId = jwt.providerUserId;

	//消息
	var code = message.code;

	var from = message.from;
	var to = message.to;
	var content = message.content;
	var sendTime = message.sendTime;

	var type = message.type;

	var fromProviderId = message.fromProviderId;
	var fromProviderUserId = message.fromProviderUserId;
	var fromNickname = message.fromNickname;
	var fromHeadimage = message.fromHeadimage;

	var toProviderId = message.toProviderId;
	var toProviderUserId = message.toProviderUserId;
	var toNickname = message.toNickname;
	var toHeadimage = message.toHeadimage;

	if(type == 2) { //私聊
		if(localProviderUserId === toProviderUserId) { //接受到的
			$('#user_message_'+fromProviderUserId).html(removeHTMLTag(content));
			$('#user_time_'+fromProviderUserId).html(displayTime(sendTime));
			$('#user_time_'+fromProviderUserId).attr("sendTime", sendTime);
			$('#chatbox_'+fromProviderUserId).append('<li class="other"><img title="'+fromNickname+'" src="'+fromHeadimage+'"><span style="font-size: 18px;word-wrap: break-word;padding-top: 0px"><div class="send_time_other">'+fromNickname+'@'+sendTime + '</div><div style="width:100%; height:5px; border-top:1px solid #eee; clear:both;"></div>'+content+'</span></li>');
		} else { //自己发的
			$('#user_message_'+toProviderUserId).html(removeHTMLTag(content));
			$('#user_time_'+toProviderUserId).html(displayTime(sendTime));
			$('#user_time_'+toProviderUserId).attr("sendTime", sendTime);
			$('#chatbox_'+toProviderUserId).append('<li class="me"><img title="'+fromNickname+'" src="'+fromHeadimage+'"><span style="font-size: 18px;word-wrap: break-word;padding-top: 0px"><div class="send_time_me">'+fromNickname+'@'+sendTime + '</div><div style="width:100%; height:5px; border-top:1px solid #76838f2e; clear:both;"></div>'+content+'</span></li>');
		}
	} else { //群聊
		if(localProviderUserId === fromProviderUserId) {
			$('#user_message_'+toProviderUserId).html(removeHTMLTag(content));
			$('#user_time_'+toProviderUserId).html(displayTime(sendTime));
			$('#user_time_'+toProviderUserId).attr("sendTime", sendTime);
			$('#chatbox_'+toProviderUserId).append('<li class="me"><img title="'+fromNickname+'" src="'+fromHeadimage+'"><span style="font-size: 18px;word-wrap: break-word;padding-top: 0px"><div class="send_time_me">'+fromNickname+'@'+sendTime + '</div><div style="width:100%; height:5px; border-top:1px solid #76838f2e; clear:both;"></div>'+content+'</span></li>');
		} else {
			$('#user_message_'+toProviderUserId).html(removeHTMLTag(content));
			$('#user_time_'+toProviderUserId).html(displayTime(sendTime));
			$('#user_time_'+toProviderUserId).attr("sendTime", sendTime);
			$('#chatbox_'+toProviderUserId).append('<li class="other"><a href="javascript:void(0)" onclick="openPrivateChatWindow(this,true)" providerId="'+fromProviderId+'" providerUserId="'+fromProviderUserId+'" nickname="'+fromNickname+'" headimage="'+fromHeadimage+'"><img title="'+fromNickname+'" src="'+fromHeadimage+'"></a><span style="font-size: 18px;word-wrap: break-word;padding-top: 0px"><div class="send_time_other">'+fromNickname+'@'+sendTime + '</div><div style="width:100%; height:5px; border-top:1px solid #eee; clear:both;"></div>'+content+'</span></li>');
		}
	}

	//滚动条拉倒底部
	office_text.scrollTop=office_text.scrollHeight;
}

//显示聊天消息
function showChatMessage(message) {
	var chatbox = document.getElementById('chatbox');
	var office_text = document.getElementById('office_text');

	//自己
	var jwt = JSON.parse(window.localStorage.getItem('spartacus-jwt') || '{}');
	var localProviderUserId = jwt.providerUserId;

	//消息
	var code = message.code;

	var from = message.from;
	var to = message.to;
	var content = message.content;
	var sendTime = message.sendTime;

	var type = message.type;

	var fromProviderId = message.fromProviderId;
	var fromProviderUserId = message.fromProviderUserId;
	var fromNickname = message.fromNickname;
	var fromHeadimage = message.fromHeadimage;

	var toProviderId = message.toProviderId;
	var toProviderUserId = message.toProviderUserId;
	var toNickname = message.toNickname;
	var toHeadimage = message.toHeadimage;

	//显示消息
	if(code === 200) {
		if(type == 2) { //私聊
			if(localProviderUserId === toProviderUserId) { //接受到的
				openPrivateChatWindow(null, false, fromProviderId, fromProviderUserId, fromNickname, fromHeadimage);
				$('#user_message_'+fromProviderUserId).html(removeHTMLTag(content));
				$('#user_time_'+fromProviderUserId).html(displayTime(sendTime));
				$('#user_time_'+fromProviderUserId).attr("sendTime", sendTime);
				$('#chatbox_'+fromProviderUserId).append('<li class="other"><img title="'+fromNickname+'" src="'+fromHeadimage+'"><span style="font-size: 18px;word-wrap: break-word;padding-top: 0px"><div class="send_time_other">'+fromNickname+'@'+sendTime + '</div><div style="width:100%; height:5px; border-top:1px solid #eee; clear:both;"></div>'+content+'</span></li>');

				//来新消息时，左侧列表中，将发送者置顶（群聊下方第一个）
				$('#spartacus').after($('#'+from));

			} else { //自己发的
				openPrivateChatWindow(null, true, toProviderId, toProviderUserId, toNickname, toHeadimage);
				$('#user_message_'+toProviderUserId).html(removeHTMLTag(content));
				$('#user_time_'+toProviderUserId).html(displayTime(sendTime));
				$('#user_time_'+toProviderUserId).attr("sendTime", sendTime);
				$('#chatbox_'+toProviderUserId).append('<li class="me"><img title="'+fromNickname+'" src="'+fromHeadimage+'"><span style="font-size: 18px;word-wrap: break-word;padding-top: 0px"><div class="send_time_me">'+fromNickname+'@'+sendTime + '</div><div style="width:100%; height:5px; border-top:1px solid #76838f2e; clear:both;"></div>'+content+'</span></li>');
			}
		} else { //群聊
			//新消息提示
			if(!$("#"+toProviderUserId).hasClass("user_active")) {
				$("#"+toProviderUserId).removeClass("user_active_new_msg");
				sleep(1).then(() => {
					$("#"+toProviderUserId).addClass("user_active_new_msg");
				})
			}

			if(localProviderUserId === fromProviderUserId) { //自己发的
				$('#user_message_'+toProviderUserId).html(removeHTMLTag(content));
				$('#user_time_'+toProviderUserId).html(displayTime(sendTime));
				$('#user_time_'+toProviderUserId).attr("sendTime", sendTime);
				$('#chatbox_'+toProviderUserId).append('<li class="me"><img title="'+fromNickname+'" src="'+fromHeadimage+'"><span style="font-size: 18px;word-wrap: break-word;padding-top: 0px"><div class="send_time_me">'+fromNickname+'@'+sendTime + '</div><div style="width:100%; height:5px; border-top:1px solid #76838f2e; clear:both;"></div>'+content+'</span></li>');
			} else { //接收到的
				$('#user_message_'+toProviderUserId).html(removeHTMLTag(content));
				$('#user_time_'+toProviderUserId).html(displayTime(sendTime));
				$('#user_time_'+toProviderUserId).attr("sendTime", sendTime);
				$('#chatbox_'+toProviderUserId).append('<li class="other"><a href="javascript:void(0)" onclick="openPrivateChatWindow(this,true)" providerId="'+fromProviderId+'" providerUserId="'+fromProviderUserId+'" nickname="'+fromNickname+'" headimage="'+fromHeadimage+'"><img title="'+fromNickname+'" src="'+fromHeadimage+'"></a><span style="font-size: 18px;word-wrap: break-word;padding-top: 0px"><div class="send_time_other">'+fromNickname+'@'+sendTime + '</div><div style="width:100%; height:5px; border-top:1px solid #eee; clear:both;"></div>'+content+'</span></li>');
			}
		}

		office_text.scrollTop=office_text.scrollHeight; //滚动条拉倒底部

		//客户端消费后，异步保存信息
		asyncSaveMessage(message);

	} else {
		layer.msg('您的凭证已失效，请重新登录！');
	}

}

//1分钟刷新一次聊天列表最新消息的时间
window.setInterval("refreshTime();",1*60*1000);
function refreshTime() {
	$(".user_time").each(function () {
		$(this).html(displayTime($(this).attr('sendTime')));
	});
}

//睡眠（单位：毫秒）
function sleep (time) {
	return new Promise((resolve) => setTimeout(resolve, time));
}

//左侧聊天列表点击响应事件
function userListItemClick() {
	$("#user_list li").on('click',function() {
		//选中
		$(".user_active").removeClass("user_active");
		$(this).removeClass("user_active_new_msg");
		$(this).addClass("user_active");

		//改变标题
		var index = parent.layer.getFrameIndex(window.name); //获取窗口索引
		parent.layer.title($(this).find(".user_name").html(), index);

		//展示对应的聊天内容
		var chatId = $(this).attr('providerUserId');
		$('.chatbox').css('display', 'none');
		$('#chatbox_'+chatId).css('display', 'block');

		//滚动条拉倒底部
		var office_text = document.getElementById('office_text');
		office_text.scrollTop=office_text.scrollHeight;
	});
}

//初始化私聊列表和对话窗口
function initPrivateChatWindow(providerId, providerUserId, nickname, headimage) {
	//聊天列表-新增
	$("#spartacus").after('<li id="'+providerUserId+'" providerId="'+providerId+'" providerUserId="'+providerUserId+'" nickname="'+nickname+'" headimage="'+headimage+'"><div class="user_head"><img src="'+headimage+'"/></div><div class="user_text"><p class="user_name">'+nickname+'</p><p class="user_message" id="user_message_'+providerUserId+'">快来聊天吧！</p></div><div class="user_time" id="user_time_'+providerUserId+'">下午2：54</div></li>')

	//聊天窗口-新增
	$("#chatbox_spartacus").after('<ul class="content chatbox" id="chatbox_'+providerUserId+'" style="display: none"></ul>');

	//刷新左侧聊天列表点击响应事件
	userListItemClick();
}

//切换或创建私聊列表和聊天窗口
function openPrivateChatWindow(e, initiative, providerId, providerUserId, nickname, headimage) {
	//自己
	var jwt = JSON.parse(window.localStorage.getItem('spartacus-jwt') || '{}');
	var localProviderUserId = jwt.providerUserId;

	//群聊里点击对方头像
	if(e != null) {
		providerId = $(e).attr("providerId");
		providerUserId = $(e).attr("providerUserId");
		nickname = $(e).attr("nickname");
		headimage = $(e).attr("headimage");
	}

	//已存在，说明之前私聊过，那么聊天列表里已有，直接打开
	if($("#"+providerUserId).length > 0) {

		//接受到消息 or 主动发起私聊
		if(initiative==true) {  //主动发起的
			//聊天列表
			$(".user_active").removeClass("user_active");
			$("#"+providerUserId).removeClass("user_active_new_msg");
			$("#"+providerUserId).addClass("user_active");

			//改变标题
			var index = parent.layer.getFrameIndex(window.name);
			parent.layer.title(nickname, index);

			//聊天窗口
			$('.chatbox').css('display', 'none');
			$('#chatbox_'+providerUserId).css('display', 'block');
		} else { //被动来消息了
			if(!$("#"+providerUserId).hasClass("user_active")) {
				$("#"+providerUserId).removeClass("user_active_new_msg");
				sleep(1).then(() => {
					$("#"+providerUserId).addClass("user_active_new_msg");
				})
			}
		}

	} else {
		//聊天列表-新增
		$("#spartacus").after('<li id="'+providerUserId+'" providerId="'+providerId+'" providerUserId="'+providerUserId+'" nickname="'+nickname+'" headimage="'+headimage+'"><div class="user_head"><img src="'+headimage+'"/></div><div class="user_text"><p class="user_name">'+nickname+'</p><p class="user_message" id="user_message_'+providerUserId+'">快来聊天吧！</p></div><div class="user_time" sendTime="2021-09-24 14:54:00" id="user_time_'+providerUserId+'">下午2：54</div></li>')

		//聊天窗口-新增
		$("#chatbox_spartacus").after('<ul class="content chatbox" id="chatbox_'+providerUserId+'" style="display: none"></ul>');

		//主动发起私聊 or 被动接受消息
		if(initiative===true) { //主动发起私聊
			//聊天列表
			$(".user_active").removeClass("user_active");
			$("#"+providerUserId).addClass("user_active");

			//改变标题
			var index = parent.layer.getFrameIndex(window.name);
			parent.layer.title(nickname, index);

			//聊天窗口
			$('.chatbox').css('display', 'none');
			$('#chatbox_'+providerUserId).css('display', 'block');

		} else { //被动接受消息
			$("#"+providerUserId).removeClass("user_active_new_msg");
			sleep(1).then(() => {
				$("#"+providerUserId).addClass("user_active_new_msg");
			})
		}

		//刷新左侧聊天列表点击响应事件
		userListItemClick();
	}

	//异步保存私聊联系人
	asyncSaveContact(localProviderUserId, providerUserId);
}

//初始化私聊记录
function initPrivateChatContacts(localProviderUserId) {
	var jwt = JSON.parse(window.localStorage.getItem('spartacus-jwt') || '{}');

	$.ajax({
		url: base_domain+"/spartacus-chat/queryContacts",
		type: "POST",//方法类型
		dataType: "json",//预期服务器返回的数据类型
		async: false, //使用同步的方式,true为异步方式
		headers: {
			"clientId":"spartacus-friday",
			"Authorization":"bearer " + jwt.access_token//自定义请求头
		},
		data : {
			"providerUserId" : localProviderUserId
		},
		success: function (result) {
			if(result.code == 0) {
				console.log("联系人查询成功！");
				let contacts = result.data;
				contacts.forEach((contact) => {
					initPrivateChatWindow(contact.providerId, contact.providerUserId, contact.nickname, contact.headimage);
					queryRecentChatMessages(localProviderUserId, contact.providerUserId);
				});
			}
		}
	});
}

//查询最近100条聊天记录
function queryRecentChatMessages(fromProviderUserId, toProviderUserId) {
	var jwt = JSON.parse(window.localStorage.getItem('spartacus-jwt') || '{}');
	var belongProviderUserId = jwt.providerUserId;

	$.ajax({
		url: base_domain+"/spartacus-chat/searchMessage",
		type: "POST",//方法类型
		dataType: "json",//预期服务器返回的数据类型
		async: false, //使用同步的方式,true为异步方式
		headers: {
			"clientId":"spartacus-friday",
			"Authorization":"bearer " + jwt.access_token//自定义请求头
		},
		data : {
			"belongProviderUserId" : belongProviderUserId,
			"fromProviderUserId" : fromProviderUserId,
			"toProviderUserId" : toProviderUserId,
		},
		success: function (result) {
			if(result.code == 0) {
				console.log("查询最近聊天记录成功！");
				const messages = result.data.records;
				for(let i = messages.length-1; i >= 0; i--){
					initChatMessages(messages[i]);
				}
			}
		}
	});
}

//异步保存私聊联系人
async function asyncSaveContact(providerUserId, contactProviderUserId) {
	var jwt = JSON.parse(window.localStorage.getItem('spartacus-jwt') || '{}');

	$.ajax({
		url: base_domain+"/spartacus-chat/saveContact",
		type: "POST",//方法类型
		dataType: "json",//预期服务器返回的数据类型
		async: true, //使用同步的方式,true为异步方式
		headers: {
			"clientId":"spartacus-friday",
			"Authorization":"bearer " + jwt.access_token//自定义请求头
		},
		data : {
			"providerUserId" : providerUserId,
			"contactProviderUserId" : contactProviderUserId
		},
		success: function (result) {
			if(result.code == 0) {
				console.log("联系人保存成功！");
			}
		}
	});
}

//客户端消费后，异步保存信息
async function asyncSaveMessage(message) {
	var jwt = JSON.parse(window.localStorage.getItem('spartacus-jwt') || '{}');
	var localProviderUserId = jwt.providerUserId;

	message['belongProviderUserId'] = localProviderUserId;

	$.ajax({
		url: base_domain+"/spartacus-chat/saveMessage",
		type: "POST",//方法类型
		dataType: "json",//预期服务器返回的数据类型
		async: true, //使用同步的方式,true为异步方式
		headers: {
			"clientId":"spartacus-friday",
			"Authorization":"bearer " + jwt.access_token//自定义请求头
		},
		data : message,
		success: function (result) {
			if(result.code == 0) {
				console.log("消息保存成功！");
			}
		}
	});
}

//自动上线
$(function () {
	layer.msg("正在初始化，稍等...");
	userListItemClick();
	initConnect();
});
///////////////////////////////////////////



