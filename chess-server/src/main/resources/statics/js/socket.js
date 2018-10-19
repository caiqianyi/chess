function Socket(userId){
	this.pullServer();
	this.userId = userId;
};

Socket.prototype = {
	ws: null,
	discovery: [],
	userId: null
};

Socket.prototype.pullServer = function(){
	var _this = this;
	//loading.show("更新服务器...");
	$.ajax({
		url: '/socket/discovery',
		type: "GET",
		async: false,
		success: function(response){
			_this.discovery = response;
		},
		error: function(){
		}
	});
};

Socket.prototype.nextWs = function(discovery){
	if(discovery.length < 2){
		return discovery.length -1;
	}
	var maxNum = discovery.length -1, minNum = 0;
	var i = parseInt(Math.random()*(maxNum-minNum+1)+minNum,10);
	console.info(i);
	return i;
}

Socket.prototype.connection = function(room,discovery){
	var _this = this;
	if(_this.ws){
		_this.close();
	}
	if(!discovery){
		discovery = _this.discovery.concat();
	}
	
	var inde = _this.nextWs(discovery);
	
	if(inde == -1){
		console.error('当前没有可用的服务');
		return;
	}
	
	var ws_url  = discovery[inde]+"/"+_this.userId;
	console.info(ws_url);
	_this.ws = new WebSocket(ws_url); //创建WebSocket对象
	
	_this.ws.last_health_time = -1; // 上一次心跳时间
	_this.ws.keepalive = function() {
		var time = new Date().getTime();
		if (_this.ws.last_health_time !== -1 && time - _this.ws.last_health_time > 20000) { // 不是刚开始连接并且20s
			_this.ws.close()
		} else {
			// 如果断网了，ws.send会无法发送消息出去。ws.bufferedAmount不会为0。
			if (_this.ws.bufferedAmount === 0 && _this.ws.readyState === 1) {
				_this.sendMessage({
					msgId : "10001",
					params : {
						time : new Date().getTime()
					}
			    });
				_this.ws.last_health_time = time;
			}
		}
	}
    
	if(_this.ws) {
		var reconnect = 0; //重连的时间
	    var reconnectMark = false; //是否重连过
		_this.ws.onopen = function (ws_url) {
			console.info("连接成功！");
			reconnect = 0;
	        reconnectMark = false;
	        _this.ws.receiveMessageTimer = setTimeout(function(){
	        	_this.ws.close();
	        }, 30000); // 30s没收到信息，代表服务器出问题了，关闭连接。如果收到消息了，重置该定时器。
	        if(_this.ws.readyState === 1) { // 为1表示连接处于open状态
	        	_this.ws.keepAliveTimer = setInterval(function(){
	        		_this.ws.keepalive();
	        	}, 1000)
	        }
		};
		_this.ws.onmessage = function (msg) {
			//analysisMessage(evt.data);  //解析后台传回的消息,并予以展示
			message = JSON.parse(msg.data);
			
			var msgAction = _this["action_"+message.msgId];
			if(msgAction){
				msgAction(message);
			}else{
				console.info("message nofund action",message);
			}
			 // 收到消息，重置定时器
	        clearTimeout(_this.ws.receiveMessageTimer); 
	        _this.ws.receiveMessageTimer = setTimeout(function(){
	        	_this.ws.close();
	        }, 30000); // 30s没收到信息，代表服务器出问题了，关闭连接。
		};
		_this.ws.onerror = function (evt) {
			console.info("异常!");
		};
		_this.ws.onclose = function (evt) {
			console.info("已经关闭连接!");
			
			clearTimeout(_this.ws.receiveMessageTimer);
	        clearInterval(_this.ws.keepAliveTimer);
	        if(!reconnectMark) { // 如果没有重连过，进行重连。
	          reconnect = new Date().getTime();
	          reconnectMark = true;
	        }
	        var tempWs = _this.ws; // 保存ws对象
	        if(new Date().getTime() - reconnect >= 10000) { // 10秒中重连，连不上就不连了
	        	_this.ws.close();
	        } else {
	          _this.ws = new WebSocket(ws_url); //创建WebSocket对象
        	  _this.ws.onopen = tempWs.onopen;
        	  _this.ws.onmessage = tempWs.onmessage;
        	  _this.ws.onerror = tempWs.onerror;
        	  _this.ws.onclose = tempWs.onclose;
        	  _this.ws.keepalive = tempWs.keepalive;
        	  _this.ws.last_health_time = -1;
	        }
		};
	}
};

Socket.prototype.sendMessage = function(m){
	var message = $.extend({"from":this.userId},m);
	this.ws.send(JSON.stringify(message));
};

Socket.prototype.action_10001 = function(m){
};

