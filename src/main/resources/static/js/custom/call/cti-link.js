/*!
 * CTILink JavaScript Client
 * http://sdk.cticloud.cn
 *
 * Since: 16/11/21 21:18
 */
(function(){
	var sdk_version = "10"; //版本号
	var module_version = "0.0.1";
	var CTILINK_ROOT = typeof CTILINK_ROOT != "undefined" ?  CTILINK_ROOT : (function(){
		var prot = location.protocol || "http:",
			uri = "//agent-gateway-2.cticloud.cn/js/agent/",
			scripts = document.getElementsByTagName("script"),
			re = new RegExp("(\\w+:)?(\/\/.*)v" + sdk_version + "/(cti-link.min.js|cti-link.js)");
		for (var i = 0; i < scripts.length; i++) {
			var match = scripts[i].src.match(re);
			if (match) {
				prot = (match[1] || prot);
				uri = match[2];
				break;
			}
		}
		return prot + uri;
	})();
	// <JasobNoObfs>
	// Please refer to http://json.org/js
	if(!this.JSON){this.JSON={};}(function(){function f(n){return n<10?'0'+n:n;}if(typeof Date.prototype.toJSON!=='function'){Date.prototype.toJSON=function(key){return isFinite(this.valueOf())?this.getUTCFullYear()+'-'+f(this.getUTCMonth()+1)+'-'+f(this.getUTCDate())+'T'+f(this.getUTCHours())+':'+f(this.getUTCMinutes())+':'+f(this.getUTCSeconds())+'Z':null;};String.prototype.toJSON=Number.prototype.toJSON=Boolean.prototype.toJSON=function(key){return this.valueOf();};}var cx=/[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,escapable=/[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g,gap,indent,meta={'\b':'\\b','\t':'\\t','\n':'\\n','\f':'\\f','\r':'\\r','"':'\\"','\\':'\\\\'},rep;function quote(string){escapable.lastIndex=0;return escapable.test(string)?'"'+string.replace(escapable,function(a){var c=meta[a];return typeof c==='string'?c:'\\u'+('0000'+a.charCodeAt(0).toString(16)).slice(-4);})+'"':'"'+string+'"';}function str(key,holder){var i,k,v,length,mind=gap,partial,value=holder[key];if(value&&typeof value==='object'&&typeof value.toJSON==='function'){value=value.toJSON(key);}if(typeof rep==='function'){value=rep.call(holder,key,value);}switch(typeof value){case'string':return quote(value);case'number':return isFinite(value)?String(value):'null';case'boolean':case'null':return String(value);case'object':if(!value){return'null';}gap+=indent;partial=[];if(Object.prototype.toString.apply(value)==='[object Array]'){length=value.length;for(i=0;i<length;i+=1){partial[i]=str(i,value)||'null';}v=partial.length===0?'[]':gap?'[\n'+gap+partial.join(',\n'+gap)+'\n'+mind+']':'['+partial.join(',')+']';gap=mind;return v;}if(rep&&typeof rep==='object'){length=rep.length;for(i=0;i<length;i+=1){k=rep[i];if(typeof k==='string'){v=str(k,value);if(v){partial.push(quote(k)+(gap?': ':':')+v);}}}}else{for(k in value){if(Object.hasOwnProperty.call(value,k)){v=str(k,value);if(v){partial.push(quote(k)+(gap?': ':':')+v);}}}}v=partial.length===0?'{}':gap?'{\n'+gap+partial.join(',\n'+gap)+'\n'+mind+'}':'{'+partial.join(',')+'}';gap=mind;return v;}}if(typeof JSON.stringify!=='function'){JSON.stringify=function(value,replacer,space){var i;gap='';indent='';if(typeof space==='number'){for(i=0;i<space;i+=1){indent+=' ';}}else if(typeof space==='string'){indent=space;}rep=replacer;if(replacer&&typeof replacer!=='function'&&(typeof replacer!=='object'||typeof replacer.length!=='number')){throw new Error('JSON.stringify');}return str('',{'':value});};}if(typeof JSON.parse!=='function'){JSON.parse=function(text,reviver){var j;function walk(holder,key){var k,v,value=holder[key];if(value&&typeof value==='object'){for(k in value){if(Object.hasOwnProperty.call(value,k)){v=walk(value,k);if(v!==undefined){value[k]=v;}else{delete value[k];}}}}return reviver.call(holder,key,value);}text=String(text);cx.lastIndex=0;if(cx.test(text)){text=text.replace(cx,function(a){return'\\u'+('0000'+a.charCodeAt(0).toString(16)).slice(-4);});}if(/^[\],:{}\s]*$/.test(text.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g,'@').replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g,']').replace(/(?:^|:|,)(?:\s*\[)+/g,''))){j=eval('('+text+')');return typeof reviver==='function'?walk({'':j},''):j;}throw new SyntaxError('JSON.parse');};}}());
	// </JasobNoObfs>

	/** 公共方法 */
	var isIE = (function(){
		var browser = {};
		return function(ver,c){
			var key = ver ?  ( c ? "is"+c+"IE"+ver : "isIE"+ver ) : "isIE";
			var v = browser[key];
			if( typeof(v)  != "undefined"){
				return v;
			}
			if( !ver){
				v = (navigator.userAgent.indexOf('MSIE') !== -1 || navigator.appVersion.indexOf('Trident/') > 0) ;
			}else {
				var match = navigator.userAgent.match(/(?:MSIE |Trident\/.*; rv:|Edge\/)(\d+)/);
				if(match){
					var v1 = parseInt(match[1]) ;
					v = c ?  ( c == 'lt' ?  v1 < ver  :  ( c == 'gt' ?  v1 >  ver : undefined ) ) : v1== ver ;
				}else	if(ver <= 9){
					var b = document.createElement('b');
					b.innerHTML = '<!--[if '+(c ? c : '')+' IE '  + ver + ']><i></i><![endif]-->';
					v =  b.getElementsByTagName('i').length === 1;
				}else{
					v=undefined;
				}
			}
			browser[key] =v;
			return v;
		};
	}());
	//随机数字
	var randomNumber = function (n) {
		return Math.floor(Math.random() * n + 5);
	};
	//随机数字+字母
	var randomString = function (len) {
		var rdmString = "";
		//toSting接受的参数表示进制，默认为10进制。36进制为0-9 a-z
		for (; rdmString.length < len; rdmString += Math.random().toString(36).substr(2));
		return rdmString.substr(0, len);
	};
	//生成sessionId
	var generateSessionId = function () {
		GLOBAL.sessionId = randomString(10);
		debug("generateSessionId | sessionId=" + GLOBAL.sessionId, 'blue');
		return GLOBAL.sessionId;
	};
	var debug = function (message, color) {
		message = (new Date())+ " | " + message;
		if (GLOBAL.connected) {  //如果是连接的
			//回调
			Event.callback("debug", message);
		}
		//发送到AgentGateway
		if (GLOBAL.connected && UserBasic.getDebug()) {
			WebSocketClient.debug({message: message});
		}
		//输出到控制台
		if (GLOBAL.debug) {
			var _ref;
			if (typeof color !== 'undefined') {
				color = 'color:' + color;
			}
			return typeof window !== 'undefined' && window !== null ? (_ref = window.console) != null ? _ref.log('%c' + message, color) : void 0 : void 0;
		}
	};
	var isFunction = function (arg) {
		return typeof arg === 'function';
	};
	var isNumber = function (arg) {
		return typeof arg === 'number';
	};
	var isObject = function (arg) {
		return typeof arg === 'object' && arg !== null;
	};
	var isUndefined = function (arg) {
		return arg === void 0;
	};
	/** 内部全局变量 */
	var GLOBAL = {
		ready: false,
		debug: true,  // 是否开启debug
		webrtc: false,  // 是否是webrtc
		connecting : false, //建立连接中
		connected: false,  // 是否建立websocket连接
		connectionCloseCount: 0,  // 连接断开次数
		sessionId : '',
		lastPingTime: null, // 上次ping时间
		pingstId: '',
		logout: false,
		connectInterval: 1000,  //建立连接的时间间隔, 默认1秒
		latency: 0, // 网络时延
		pingValue: false, // 检测断线
		randoms: 0
	};
	/**
	 * 加载
	 */
	var script = [
		{"url":"v" + sdk_version + "/sockjs-1.1.0.js","version":module_version},
		{"url":"v" + sdk_version + "/stomp.js","version":module_version}
	];
	var flashBridgeScript = [
		{"url":"v" + sdk_version + "/flashbridge/swfobject.js","version":module_version},
		{"url":"v" + sdk_version + "/flashbridge/web_socket.js","version":module_version}
	];
	var sipScript = [
		{"url":"v" + sdk_version + "/sipjs/adapter-latest.js","version":module_version},
		{"url":"v" + sdk_version + "/sipjs/sip-0.7.7.js","version":module_version}
	];
	var sipAudio = [
		{"id":"ringtone","url":"v" + sdk_version + "/sipjs/sounds/ringtone.wav","version":module_version},
		{"id":"ringbacktone","url":"v" + sdk_version + "/sipjs/sounds/ringbacktone.wav","version":module_version},
		{"id":"dtmfTone","url":"v" + sdk_version + "/sipjs/sounds/dtmf.wav","version":module_version},
		{"id":"startTone","url":"v" + sdk_version + "/sipjs/sounds/start.wav","version":module_version},
		{"id":"hangupTone","url":"v" + sdk_version + "/sipjs/sounds/hangup.wav","version":module_version}
	];
	CTILink.setup = function(params, callback) {
		debug("CTILink.setup | " + JSON.stringify(params), 'blue');
		if (params.webrtc && !isIE()) {
			debug("CTILink.setup | load sip js&audio", 'blue');
			if (GLOBAL.ready) {
				GLOBAL.callback = callback;
				loadScript(sipScript, 0);
			} else {
				loadScript(sipScript, 0, callback);
			}
			loadAudio(sipAudio, 0);
		} else {
			if (GLOBAL.ready) {
				callback();
			} else {
				GLOBAL.callback = callback;
			}
		}
		if (params.debug) {
			GLOBAL.debug = true;
		}
		if (params.connectInterval && isNumber(params.connectInterval)
			&& params.connectInterval > 500) {
			GLOBAL.connectInterval = params.connectInterval;
		}
	};
	var loadAudio = function (jss, i) {
		if (i == jss.length) {
			var oBody = document.getElementsByTagName('body').item(0);
			var exist = document.getElementById("audio");
            if(exist) {
				return;
            }
			var oAudio = document.createElement("audio");
			oAudio.id = "audio_remote";
			oAudio.autoplay = "autoplay";
			oAudio.style = "display: none";
			oBody.appendChild(oAudio);
			debug("loadAudio | sip语音文件加载完成");
			return;
		}
		var oHead = document.getElementsByTagName('head').item(0);
		var exist = document.getElementById(jss[i].id);
        if(exist) {
			return;
        }
		var oScript = document.createElement("audio");
		oScript.id = jss[i].id;
		if (jss[i].id == "ringtone" || jss[i].id == "ringbacktone") {
			oScript.loop = "loop";
		}
		oScript.src = CTILINK_ROOT + jss[i].url;
		oHead.appendChild(oScript);
		oScript.addEventListener("canplaythrough" ,function() {
			loadAudio(jss, i + 1);
		});
	};
	var loadScript = function (jss, i, callback) {
		if (i == jss.length) {
			if (isFunction(GLOBAL.callback)) {
				GLOBAL.callback();
				debug("loadScript | js文件加载完成");
			} else if (isFunction(callback)) {
				callback();
				debug("loadScript | js文件加载完成");
			}
			GLOBAL.ready = true;
			return;
		}
		var oHead = document.getElementsByTagName('head').item(0);
		var oScript = document.createElement("script");
		oScript.type = "text/javascript";
		oScript.src = CTILINK_ROOT + jss[i].url+"?version="+jss[i].version;
		oScript.charset = "UTF-8";
		oHead.appendChild(oScript);
		if(oScript.readyState) { // IE
			oScript.onreadystatechange = function() {
				if(oScript.readyState == "loaded" || oScript.readyState == "complete") {
					oScript.onreadystatechange = null;
					loadScript(jss, i + 1, callback);
				}
			};
		} else { // Other
			oScript.onload = function() {
				loadScript(jss, i + 1, callback);
			};
		}
	};
	//IE浏览器, 加载flash支持
	if (isIE()) {
		debug("Flash | IE load flash-bridge", 'blue');
		window.WEB_SOCKET_SWF_LOCATION = CTILINK_ROOT + "v" + sdk_version + "/flashbridge/WebSocketMainInsecure.swf?time=" + (new Date()).getTime();
		window.WEB_SOCKET_DEBUG = GLOBAL.debug;
		//加载flash
		script = flashBridgeScript.concat(script);
	}
	loadScript(script, 0);
	/** User对象, 存储企业座席等 */
	var User = function (params) {
		var _sessionKey = params.sessionKey;
		var _enterpriseId = params.enterpriseId; //企业ID
		var _cno = params.cno; //座席号
		var _bindType = params.bindType; //绑定电话类型
		var _bindTel = params.bindTel; //绑定电话
		var _qids = params.qids; //队列ID
		var _loginStatus = params.loginStatus; //登录状态
		var _pauseDescription = params.pauseDescription;
		var _webSocketUrl = params.webSocketUrl;
		//软电话
		var _sipIp = params.sipIp; //软电话IP
		var _sipPwd = params.sipPwd; //软电话密码
		var _webrtcSocketUrl = params.webrtcSocketUrl;
		var _webrtcStunServer = params.webrtcStunServer;
		var _debug = params.debug;

		this.setSessionKey = function (sessionKey) {
			_sessionKey = sessionKey;
		};
		this.getSessionKey = function () {
			return _sessionKey;
		};
		this.getEnterpriseId = function () {//企业ID
			return _enterpriseId;
		};
		this.setEnterpriseId = function (enterpriseId) {//企业ID
			_enterpriseId = enterpriseId;
		};
		this.setCno = function (cno) {
			_cno = cno;
		};
		this.getCno = function () {//座席号
			return _cno;
		};
		this.setBindType = function (bindType) {//绑定电话类型
			_bindType = bindType;
		};
		this.getBindType = function () {//绑定电话类型
			return _bindType;
		};
		this.setBindTel = function (bindTel) {//绑定电话
			_bindTel = bindTel;
		};
		this.getBindTel = function () {//绑定电话类型
			return _bindTel;
		};
		this.getQids = function () {// 企业ID
			return _qids;
		};
		this.setQids = function (qids) {// 企业ID
			_qids = qids;
		};
		this.setLoginStatus = function (loginStatus) {//登陆状态
			_loginStatus = loginStatus;
		};
		this.getLoginStatus = function () {//登陆状态
			return _loginStatus;
		};
		this.setPauseDescription = function (pauseDescription) {//
			_pauseDescription = pauseDescription;
		};
		this.getPauseDescription = function () {//
			return _pauseDescription;
		};
		this.getWebSocketUrl = function () {//websocketurl
			return _webSocketUrl;
		};
		this.setWebSocketUrl = function (webSocketUrl) {//websocketurl
			_webSocketUrl = webSocketUrl;
		};
		this.setSipIp = function (sipIp) {//软电话IP
			_sipIp = sipIp;
		};
		this.getSipIp = function () {//软电话IP
			return _sipIp;
		};
		this.setSipPwd = function (sipPwd) {//软电话密码
			_sipPwd = sipPwd;
		};
		this.getSipPwd = function () {//软电话密码
			return _sipPwd;
		};
		this.getWebrtcSocketUrl = function () {//webrtc
			return _webrtcSocketUrl;
		};
		this.setWebrtcSocketUrl = function (webrtcSocketUrl) {//webrtc
			_webrtcSocketUrl = webrtcSocketUrl;
		};
		this.getWebrtcStunServer = function () {//stun
			return _webrtcStunServer;
		};
		this.setWebrtcStunServer = function (webrtcStunServer) {//stun
			_webrtcStunServer = webrtcStunServer;
		};
		this.setDebug = function (debug) {
			_debug = debug;
		};
		this.getDebug = function () {
			return _debug;
		};
	};

	var UserBasic;
	/** 基础方法 */
	var WebSocketClient = (function () {
		function WebSocketClient() {}
		//回掉方法, 执行回掉, 对外调用
		var eventListeners = {};
		var responseListeners = {};
		//定义事件的handler, 提供给Agent/Event/Session
		var responseHandlers = {};
		var eventHandlers = {};
		WebSocketClient.addEventListener = function (type, listener) {
			if (isUndefined(listener))
				return;

			if (!isFunction(listener))
				throw TypeError('listener must be a function');

			if (!eventListeners)
				eventListeners = {};

			//一个event只能有一个callback
			eventListeners[type] = listener;
		};
		WebSocketClient.removeEventListener = function (type) {
			if (eventListeners && eventListeners[type])
				delete eventListeners[type];
		};
		//不对外
		var getEventListener = function (type) {
			var listener;
			if (eventListeners)
				listener = eventListeners[type];

			return listener;
		};
		WebSocketClient.addResponseListener = function (type, listener) {
			if (isUndefined(listener))
				return;

			if (!isFunction(listener))
				throw TypeError('listener must be a function');

			if (!responseListeners)
				responseListeners = {};

			//一个action只能有一个callback
			responseListeners[type] = listener;
		};
		WebSocketClient.removeResponseListener = function (type) {
			if (responseListeners && responseListeners[type])
				delete responseListeners[type];
		};
		//不对外
		var getResponseListener = function (type) {
			var listener;
			if (responseListeners)
				listener = responseListeners[type];

			return listener;
		};
		//注册handler, 分别提供给 Agent和Event
		WebSocketClient.registerResponseHandler = function (type, handler) {
			responseHandlers[type] = handler;
		};

		WebSocketClient.registerEventHandler = function (type, handler) {
			eventHandlers[type] = handler;
		};
		/**
		 * 建立连接
		 * @param options
		 */
		WebSocketClient.connect = function (options) {
			if (GLOBAL.connecting) {
				debug("连接正在建立,请稍后再试");
				return;
			}
			GLOBAL.connecting = true;
			debug("连接建立中...");
			setTimeout(function() {
				GLOBAL.connecting = false;
			}, GLOBAL.connectInterval);
			var socketUrl = (isUndefined(UserBasic.getWebSocketUrl()) || UserBasic.getWebSocketUrl() == '')
				? CTILINK_ROOT.substring(0, CTILINK_ROOT.indexOf('/', 10))  : UserBasic.getWebSocketUrl();
			if (socketUrl.toLocaleLowerCase().indexOf("http") == -1) {
				socketUrl = (location.protocol || "http:") + "//" + socketUrl;
			}
			//判断SockJs是否加载完成
			if (typeof SockJS  == 'undefined') {
				debug("WebSocketClient.connect | SockJS is undefined", 'red');
				return;
			}
			//判断Stomp是否加载完成
			if (typeof Stomp  == 'undefined') {
				debug("WebSocketClient.connect | Stomp is undefined", 'red');
				return;
			}
			var socket = new SockJS(socketUrl + '/agent?sessionKey=' + UserBasic.getSessionKey()
				, {}, {sessionId: generateSessionId});
			WebSocketClient.stompClient = Stomp.over(socket);
			WebSocketClient.stompClient.debug = null; //关闭日志
			WebSocketClient.stompClient.connect({}, function () {
				if (GLOBAL.connected) {
					debug("连接还未中断");
					return;
				}
				debug("连接建立成功");
				GLOBAL.connected = true;
				GLOBAL.connecting = false;
				if (typeof options.OnOpen === "function") {
					options.OnOpen();
				}

				// 订阅属于自己的消息
				WebSocketClient.stompClient.subscribe('/user/agent', function (data) {
					var r = eval("(0 || " + data.body + ")");  //0 || 兼容IE低版本
					if (typeof options.OnMessage === "function") {
						options.OnMessage(r);
					}

					WebSocketClient.processToken(r);
				});
				if (!isUndefined(UserBasic.getQids())
					&& UserBasic.getQids() != '') {
					// 订阅队列的消息
					WebSocketClient.stompClient.subscribe('/queue/' + UserBasic.getQids(),
						function (data) {
							var r = eval("(0 || " + data.body + ")");
							if (typeof options.OnMessage === "function") {
								options.OnMessage(r);
							}

							WebSocketClient.processToken(r);
						});
				}
				// 订阅企业全局的消息
				WebSocketClient.stompClient.subscribe('/enterprise/'
					+ UserBasic.getEnterpriseId(), function (data) {
					var r = eval("(0 || " + data.body + ")");

					if (typeof options.OnMessage === "function") {
						options.OnMessage(r);
					}

					WebSocketClient.processToken(r);
				});

				WebSocketClient.login();
			}, function (error) {
				if (typeof options.OnClose === "function") {
					options.OnClose();
				}
				debug(error)
			});
		};
		/**
		 * 关闭连接
		 */
		WebSocketClient.disconnect = function () {
			WebSocketClient.stompClient.disconnect();
			GLOBAL.connected = false;
		};
		/**
		 * 向服务器发送消息
		 *
		 * @param token
		 * @param headers
		 */
		WebSocketClient.sendToken = function (token, headers) {
			WebSocketClient.stompClient.send("/app/agent/handle/" + token.type,
				headers == null ? {} : headers, JSON.stringify(token));
		};

		WebSocketClient.processToken = function (token) {
			var key;
			var callback;
			if (token.type == 'response') {  //action的响应
				for(key in responseHandlers) {
					responseHandlers[key](token);
				}
				var response = getResponseListener(token.reqType);
				if (typeof response === 'function') {
					response(token);
				}
			} else if (token.type == 'event') { //事件
				if (token.name == 'kickout') {
					//断线重连被踢下线, 不处理
					if (GLOBAL.sessionId == token.sessionId) {
						return;
					}
				} else if (token.name == 'debug') {
					if (token.debug == '1') {
						UserBasic.setDebug(true);
					} else {
						UserBasic.setDebug(false);
					}
					return;
				} else if (token.name == 'status') {
					if (token.enterpriseId != UserBasic.getEnterpriseId()
						|| token.cno != UserBasic.getCno()) {  //不是当前座席的事件
						callback = getEventListener('queueStatus');
						if (typeof callback === 'function') {
							callback(token);
						}
						return;
					}
				}
				for(key in eventHandlers) {
					eventHandlers[key](token);
				}
				callback = getEventListener(token.name);
				if (typeof callback === 'function') {
					callback(token);
				}
			}
		};

		WebSocketClient.login = function () {
			// 连接成功后登录座席
			WebSocketClient.sendToken({
				type: "login",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				loginStatus: UserBasic.getLoginStatus(),
				bindTel: UserBasic.getBindTel(),
				bindType: UserBasic.getBindType(),
				pauseDescription: UserBasic.getPauseDescription(),
				loginType: 1

			});
		};
		WebSocketClient.debug = function (params) {
			WebSocketClient.sendToken({
				type: "debug",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				message: params.message
			});
		};
		return WebSocketClient;
	})();

	var SipPhone = (function () {
		var sipPhone;
		var currentSession;
		function SipPhone(){}
		var eventHandlers = [];
		SipPhone.registerEventHandler = function (type, handler) {
			eventHandlers[type] = handler;
		};
		SipPhone.sipRegister = function() {
			sipPhone = new SIP.UA({
				uri: 'sip:' + UserBasic.getEnterpriseId()
				+ UserBasic.getBindTel() + '@' + UserBasic.getSipIp(),
				wsServers: [UserBasic.getWebrtcSocketUrl()],
				authorizationUser: UserBasic.getEnterpriseId() + UserBasic.getBindTel(),
				password: UserBasic.getSipPwd(),
				register: true,
				stunServers: "",
				turnServers: UserBasic.getWebrtcStunServer(),
				traceSip: true,
				wsServerMaxReconnection: 3,
				wsServerReconnectionTimeout: 4,
				iceCheckingTimeout: 1000,
				hackIpInContact: true
			});
			sipPhone.on('disconnected', function() {
				for(var key in eventHandlers) {
					eventHandlers[key]({
						name:'disconnected'
					});
				}
			});
			sipPhone.on('invite', function (session) {
				for(var key in eventHandlers) {
					eventHandlers[key]({
						name:'invite'
					});
				}
				var autoAnswer = '0';
				if(session.transaction.request.headers['X-Asterisk-Call-Type'] != undefined){
					var callType = session.transaction.request.headers['X-Asterisk-Call-Type'][0].raw;
					if (callType == '3' || callType == '4' || callType== '5') {
						autoAnswer = '1';
					}
				}
				if(autoAnswer == '1'){
					setTimeout(function() {
						try{
							startTone.play();
						} catch (e) {

						}
						session.accept({
							media: {
								constraints: {
									audio: true,
									video: false
								},
								render: {
									remote: document.getElementById("audio_remote")
								}
							}
						});
					}, 500);
				}else{
					startRingTone();
				}
				currentSession = session;
				session.on('connecting', function (session) {
					stopRingTone();
				});
				session.on('accepted', function (session) {
					stopRingTone();
				});
				session.on('failed', function (session) {
					for(var key in eventHandlers) {
						eventHandlers[key]({
							name:'failed'
						});
					}
					stopRingTone();
					currentSession = undefined;
				});
				session.on('cancel', function (session) {
					for(var key in eventHandlers) {
						eventHandlers[key]({
							name:'cancel'
						});
					}
					stopRingTone();
					currentSession = undefined;
				});
				session.on('bye', function (session) {
					for(var key in eventHandlers) {
						eventHandlers[key]({
							name:'bye'
						});
					}
					currentSession = undefined;
					try{
						hangupTone.play();
					} catch (e) {

					}
				});
			});
		};
		SipPhone.isRegistered = function () {
			return sipPhone.isRegistered();
		};
		SipPhone.sipUnRegister = function (){
			sipPhone.stop();
		};
		SipPhone.sipCall = function (number){
			sipPhone.invite(number, document.getElementById("audio_remote"));
		};
		SipPhone.sipAnswer = function() {
			currentSession.accept({
				media: {
					render: {
						remote: {
							audio: document.getElementById("audio_remote")
						}
					}
				}
			});
		};
		SipPhone.sipHangup = function () {
			currentSession.terminate();
		};
		SipPhone.sendDTMF = function(value){
			if(currentSession != undefined){
				currentSession.dtmf(value);
				try{
					dtmfTone.play();
				} catch (e) {

				}
			}
		};
		var startRingTone = function () {
			try {
				ringtone.play();
			} catch (e) {
			}
		};
		var stopRingTone = function () {
			try {
				ringtone.pause();
			} catch (e) {
			}
		};
		var startRingbackTone = function() {
			try {
				ringbacktone.play();
			} catch (e) {
			}
		};
		var stopRingbackTone = function() {
			try {
				ringbacktone.pause();
			} catch (e) {
			}
		};
		return SipPhone;
	})();

	var Session = (function () {
		function Session() {}
		var alive = false;
		Session.init = function (uniqueId, callType, customerNumber) {
			debug("session.init | session建立, uniqueId:" + uniqueId + ", callType:" + callType +
				", customerNumber:" + customerNumber, 'blue');
			Session.uniqueId = uniqueId;
			Session.callType = callType;
			Session.customerNumber = customerNumber;
			alive = true;
		};
		Session.terminate = function () {
			debug("session.terminate | session销毁", 'blue');
			alive = false;
		};
		Session.isSessionAlive = function () {
			return alive;
		};
		Session.unlink = function (params, callback) {//挂断
			debug("session.unlink | 挂断," + JSON.stringify(params), 'blue');
			if (!this.isSessionAlive()) {
				debug("session.unlink | 挂断失败, session已经销毁", 'blue');
				return;
			}
			WebSocketClient.addResponseListener("unlink", callback);
			WebSocketClient.sendToken({
				type: "unlink",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno()
			});
		};
		Session.previewOutcallCancel = function (params, callback) {//外呼取消
			debug("session.previewOutcallCancel | 外呼取消," + JSON.stringify(params), 'blue');
			if (!this.isSessionAlive()) {
				debug("session.previewOutcallCancel | 外呼取消失败, session已经销毁", 'blue');
				return;
			}
			WebSocketClient.addResponseListener("previewOutcallCancel", callback);
			WebSocketClient.sendToken({
				type: "previewOutcallCancel",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno()
			});
		};
		Session.hold = function (params, callback) {//保持
			debug("session.hold | 保持," + JSON.stringify(params), 'blue');
			if (!this.isSessionAlive()) {
				debug("session.hold | 保持失败, session已经销毁", 'blue');
				return;
			}
			WebSocketClient.addResponseListener("hold", callback);
			WebSocketClient.sendToken({
				type: "hold",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno()
			});
		};
		Session.unhold = function (params, callback) {//保持取消
			debug("session.unhold | 保持接回," + JSON.stringify(params), 'blue');
			if (!this.isSessionAlive()) {
				debug("session.unhold | 保持接回失败, session已经销毁", 'blue');
				return;
			}
			WebSocketClient.addResponseListener("unhold", callback);
			WebSocketClient.sendToken({
				type: "unhold",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno()
			});
		};
		Session.consult = function (params, callback) {//咨询
			debug("session.consult | 咨询," + JSON.stringify(params), 'blue');
			if (!this.isSessionAlive()) {
				debug("session.consult | 咨询失败, session已经销毁", 'blue');
				return;
			}
			WebSocketClient.addResponseListener("consult", callback);
			WebSocketClient.sendToken({
				type: "consult",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				consultObject: params.consultObject,
				objectType: params.objectType
			});
		};
		Session.consultCancel = function (params, callback) {//咨询取消
			debug("session.consultCancel | 咨询取消," + JSON.stringify(params), 'blue');
			if (!this.isSessionAlive()) {
				debug("session.consultCancel | 咨询取消失败, session已经销毁", 'blue');
				return;
			}
			WebSocketClient.addResponseListener("consultCancel", callback);
			WebSocketClient.sendToken({
				type: "consultCancel",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno()
			});
		};
		Session.consultTransfer = function (params, callback) {//咨询转接
			debug("session.consultTransfer | 咨询转移," + JSON.stringify(params), 'blue');
			if (!this.isSessionAlive()) {
				debug("session.consultTransfer | 咨询转移失败, session已经销毁", 'blue');
				return;
			}
			if (params == undefined) {
				params = {};
			}
			if (params.limitTimeSecond == undefined) {
				params.limitTimeSecond = "";
			}
			if (params.limitTimeAlertSecond == undefined) {
				params.limitTimeAlertSecond = "";
			}
			if (params.limitTimeFile == undefined) {
				params.limitTimeFile = "";
			}
			WebSocketClient.addResponseListener("consultTransfer", callback);
			WebSocketClient.sendToken({
				type: "consultTransfer",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				limitTimeSecond: params.limitTimeSecond,
				limitTimeAlertSecond: params.limitTimeAlertSecond,
				limitTimeFile: params.limitTimeFile
			});
		};
		Session.consultThreeway = function (params, callback) {//咨询三方
			debug("session.consultThreeway | 咨询三方," + JSON.stringify(params), 'blue');
			if (!this.isSessionAlive()) {
				debug("session.consultThreeway | 咨询三方失败, session已经销毁", 'blue');
				return;
			}
			WebSocketClient.addResponseListener("consultThreeway", callback);
			WebSocketClient.sendToken({
				type: "consultThreeway",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno()
			});
		};
		Session.unconsult = function (params, callback) {//咨询接回
			debug("session.unconsult | 咨询接回," + JSON.stringify(params), 'blue');
			if (!this.isSessionAlive()) {
				debug("session.unconsult | 咨询接回失败, session已经销毁", 'blue');
				return;
			}
			WebSocketClient.addResponseListener("unconsult", callback);
			WebSocketClient.sendToken({
				type: "unconsult",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno()
			});
		};
		Session.transfer = function (params, callback) {//转移
			debug("session.transfer | 转移," + JSON.stringify(params), 'blue');
			if (!this.isSessionAlive()) {
				debug("session.transfer | 转移失败, session已经销毁", 'blue');
				return;
			}
			WebSocketClient.addResponseListener("transfer", callback);
			WebSocketClient.sendToken({
				type: "transfer",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				transferObject: params.transferObject,
				objectType: params.objectType
			});
		};
		Session.interact = function (params, callback) {//交互
			debug("session.interact | 交互," + JSON.stringify(params), 'blue');
			if (!this.isSessionAlive()) {
				debug("session.interact | 交互失败, session已经销毁", 'blue');
				return;
			}
			WebSocketClient.addResponseListener("interact", callback);
			WebSocketClient.sendToken({
				type: "interact",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				interactObject: params.interactObject
			});
		};
		Session.investigation = function (params, callback) {//满意度调查
			debug("session.investigation | 满意度调查," + JSON.stringify(params), 'blue');
			if (!this.isSessionAlive()) {
				debug("session.investigation | 满意度调查失败, session已经销毁", 'blue');
				return;
			}
			WebSocketClient.addResponseListener("investigation", callback);
			WebSocketClient.sendToken({
				type: "investigation",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno()
			});
		};
		Session.refuse = function (params, callback) {//拒接
			debug("session.refuse | 拒接," + JSON.stringify(params), 'blue');
			if (!this.isSessionAlive()) {
				debug("session.refuse | 拒接失败, session已经销毁", 'blue');
				return;
			}
			WebSocketClient.addResponseListener("refuse", callback);
			WebSocketClient.sendToken({
				type: "refuse",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno()
			});
		};
		Session.mute = function (params, callback) {
			debug("session.mute | 静音," + JSON.stringify(params), 'blue');
			if (!this.isSessionAlive()) {
				debug("session.mute | 静音失败, session已经销毁", 'blue');
				return;
			}
			WebSocketClient.addResponseListener("mute", callback);
			WebSocketClient.sendToken({
				type: "mute",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				direction : params.direction
			});
		};

		Session.setUserData = function (params, callback) {
			debug("session.setUserData | 设置随路数据," + JSON.stringify(params), 'blue');
			if (!this.isSessionAlive()) {
				debug("session.setUserData | 设置随路数据失败, session已经销毁", 'blue');
				return;
			}
			WebSocketClient.addResponseListener("setUserData", callback);
			WebSocketClient.sendToken({
				type: "setUserData",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				userData: params.userData,
				direction: params.direction
			});
		};

		Session.getUserData = function (params, callback) {
			debug("session.getUserData | 获取随路数据," + JSON.stringify(params), 'blue');
			if (!this.isSessionAlive()) {
				debug("session.getUserData | 获取随路数据失败, session已经销毁", 'blue');
				return;
			}
			WebSocketClient.addResponseListener("getUserData", callback);
			WebSocketClient.sendToken({
				type: "getUserData",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				keys: params.keys,
				encryptKeys: params.encryptKeys ,
				encryption: params.encryption ,
				direction: params.direction
			});
		};

		Session.dtmf = function (params, callback) {
			debug("session.dtmf | 发送dtmf," + JSON.stringify(params), 'blue');
			if (!this.isSessionAlive()) {
				debug("session.dtmf | 发送dtmf失败, session已经销毁", 'blue');
				return;
			}
			if (params.digits == undefined || isNaN(params.digits)) {
				debug("session.dtmf | 发送dtmf失败, 参数digits格式不正确", 'blue');
				return;
			}
			if (params.direction != 1 && params.direction != 2) {
				debug("session.dtmf | 发送dtmf失败, 参数direction格式不正确", 'blue');
				return;
			}
			if (params.duration == undefined || isNaN(params.duration)) {
				params.duration = 100;
			}
			if (params.duration < 100 || params.duration > 500) {
				debug("session.dtmf | 发送dtmf失败, 参数duration取值不正确", 'blue');
				return;
			}
			if (params.gap == undefined || isNaN(params.gap)) {
				params.gap = 250;
			}
			if (params.gap < 250 || params.gap > 1000) {
				debug("session.dtmf | 发送dtmf失败, 参数gap取值不正确", 'blue');
				return;
			}
			WebSocketClient.addResponseListener("dtmf", callback);
			WebSocketClient.sendToken({
				type: "dtmf",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				digits: params.digits,
				direction: params.direction,
				duration: params.duration,
				gap: params.gap
			});
		};
		Session.controlPlayback = function (params, callback) {//录音回放
			debug("session.controlPlayback | 录音回放," + JSON.stringify(params), 'blue');
			if (!this.isSessionAlive()) {
				debug("session.controlPlayback | 录音回放失败, session已经销毁", 'blue');
				return;
			}
			WebSocketClient.addResponseListener("controlPlayback", callback);
			WebSocketClient.sendToken({
				type: "controlPlayback",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				action: params.action,
				playUrl: params.playUrl,
				skipMs: params.skipMs
			});
		};
		return Session;
	})(WebSocketClient);

	var SipSession = (function () {
		function SipSession() {}
		var alive = false;
		SipSession.init = function () {
			debug("sipSession.init | sipSession建立", 'blue');
			alive = true;
		};
		SipSession.terminate = function () {
			debug("sipSession.terminate | sipSession销毁", 'blue');
			alive = false;
		};
		SipSession.isSessionAlive = function () {
			return alive;
		};
		SipSession.sipLink = function() {
			debug("sipSession.sipLink | sip接听", 'blue');
			if (!this.isSessionAlive()) {
				debug("sipSession.sipLink | sip接听失败, session已经销毁", 'blue');
				return;
			}
			SipPhone.sipAnswer();
		};
		SipSession.sipUnlink = function () {
			debug("sipSession.sipUnlink | sip挂断", 'blue');
			if (!this.isSessionAlive()) {
				debug("sipSession.sipUnlink | sip挂断失败, session已经销毁", 'blue');
				return;
			}
			SipPhone.sipHangup();
		};
		SipSession.sipDTMF = function (params) {
			debug("sipSession.sipDTMF | sipDTMF", 'blue');
			if (!this.isSessionAlive()) {
				debug("sipSession.sipDTMF | sipDTMF失败, session已经销毁", 'blue');
				return;
			}
			SipPhone.sendDTMF(params);
		};

		return SipSession;
	})(SipPhone);

	var Event = (function () {
		function Event(){}
		var events = {};
		var eventHandler = function (token) {
			if (token.name == 'status') {
				//sessionInit
				if (token.deviceStatus == 3  //响铃
					|| token.deviceStatus == 4) { //通话中
					if (!Session.isSessionAlive()) {
						Session.init(token.uniqueId, token.callType, token.customerNumber);
						if (token.deviceStatus == 3) {
							if (isFunction(events['sessionInit'])) {
								events['sessionInit']();
							}
						}

					}
				}
				//sessionTerminate
				if (token.deviceStatus == -1 || token.deviceStatus == 0
					|| token.deviceStatus == 1) {
					if (Session.isSessionAlive()) {
						Session.terminate();
						if (isFunction(events['sessionTerminate'])) {
							events['sessionTerminate']();
						}
					}
				}
			} else if (token.name == 'kickout') {  //踢下线事件, 座席退出
				var params = {};
				params.logoutMode = 0;  //被踢下线
				Agent.logout(params);

			}
		};
		var sipEventHandler = function(token) {
			switch (token.name) {
				case 'invite':
					if (!SipSession.isSessionAlive()) {
						SipSession.init();
						if (isFunction(events['sipSessionInit'])) {
							events['sipSessionInit']();
						}
					}
					break;
				case 'disconnected':
					debug("Event.sipEventHandler | sipDisconnected", 'blue');
					if (isFunction(events['sipDisconnected'])) {
						events['sipDisconnected']();
					}
					break;
				case 'failed':
				case 'cancel':
				case 'bye':
					if (SipSession.isSessionAlive()) {
						SipSession.terminate();
						if (isFunction(events['sipSessionTerminate'])) {
							events['sipSessionTerminate']();
						}
					}
					break;
			}
		};
		Event.addListener = function (type, listener) {
			if (isUndefined(listener))
				return;

			if (!isFunction(listener))
				throw TypeError('listener must be a function');

			if (!events)
				events = {};

			//一个action只能有一个callback
			events[type] = listener;
			WebSocketClient.addEventListener(type, listener);
		};
		Event.removeListener = function (type, listener) {
			if (events && events[type])
				delete events[type];
			WebSocketClient.removeEventListener(type);
		};
		Event.callback = function(type, token) {
			if (isFunction(events[type])) {
				events[type](token);
			}
		};
		WebSocketClient.registerEventHandler("event", eventHandler);
		SipPhone.registerEventHandler("event",sipEventHandler);
		return Event;
	})(WebSocketClient, SipPhone, Session, SipSession);

	var Monitor = (function () {
		function Monitor() {}
		Monitor.threeway = function (params, callback) {////三方通话
			debug("Monitor.threeway | 监控三方," + JSON.stringify(params), 'blue');
			WebSocketClient.addResponseListener("threeway", callback);
			WebSocketClient.sendToken({
				type: "threeway",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				threewayedCno: params.threewayedCno,
				objectType: 1,
				threewayObject: UserBasic.getCno()
			});
		};
		Monitor.unthreeway = function (params, callback) {//三方通话挂断
			debug("Monitor.unthreeway | 监控三方挂断," + JSON.stringify(params), 'blue');
			WebSocketClient.addResponseListener("unthreeway", callback);
			WebSocketClient.sendToken({
				type: "unthreeway",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				threewayedCno: params.threewayedCno
			});
		};
		Monitor.spy = function (params, callback) {//监听
			debug("Monitor.spy | 监控监听," + JSON.stringify(params), 'blue');
			WebSocketClient.addResponseListener("spy", callback);
			WebSocketClient.sendToken({
				type: "spy",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				spiedCno: params.spiedCno,
				objectType: 1,
				spyObject: UserBasic.getCno()
			});
		};
		Monitor.unspy = function (params, callback) {//监听取消
			debug("Monitor.unspy | 监控挂断," + JSON.stringify(params), 'blue');
			WebSocketClient.addResponseListener("unspy", callback);
			WebSocketClient.sendToken({
				type: "unspy",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				spiedCno: params.spiedCno
			});
		};
		Monitor.pickup = function (params, callback) {//抢线
			debug("Monitor.pickup | 监控抢线," + JSON.stringify(params), 'blue');
			WebSocketClient.addResponseListener("pickup", callback);
			WebSocketClient.sendToken({
				type: "pickup",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				pickupCno: params.pickupCno
			});
		};
		Monitor.whisper = function (params, callback) {//耳语
			debug("Monitor.whisper | 监控耳语," + JSON.stringify(params), 'blue');
			WebSocketClient.addResponseListener("whisper", callback);
			WebSocketClient.sendToken({
				type: "whisper",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				whisperedCno: params.whisperedCno,
				objectType: 1,
				whisperObject: UserBasic.getCno()
			});
		};
		Monitor.unwhisper = function (params, callback) {//耳语取消
			debug("Monitor.unwhisper | 监控耳语挂断," + JSON.stringify(params), 'blue');
			WebSocketClient.addResponseListener("unwhisper", callback);
			WebSocketClient.sendToken({
				type: "unwhisper",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				whisperedCno: params.whisperedCno
			});
		};
		Monitor.barge = function (params, callback) {//强插
			debug("Monitor.barge | 监控强插," + JSON.stringify(params), 'blue');
			WebSocketClient.addResponseListener("barge", callback);
			WebSocketClient.sendToken({
				type: "barge",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				bargedCno: params.bargedCno,
				objectType: 1,
				bargeObject: UserBasic.getCno()
			});
		};
		Monitor.setPause = function (params, callback) {//监控置忙
			debug("Monitor.setPause | 监控置忙," + JSON.stringify(params), 'blue');
			WebSocketClient.addResponseListener("setPause", callback);
			WebSocketClient.sendToken({
				type: "setPause",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				monitorCno: UserBasic.getCno(),
				monitoredCno: params.monitoredCno
			});
		};
		Monitor.setUnpause = function (params, callback) {//监控置闲
			debug("Monitor.setUnpause | 监控置闲," + JSON.stringify(params), 'blue');
			WebSocketClient.addResponseListener("setUnpause", callback);
			WebSocketClient.sendToken({
				type: "setUnpause",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				monitorCno: UserBasic.getCno(),
				monitoredCno: params.monitoredCno
			});
		};
		Monitor.disconnect = function (params, callback) {//强拆
			debug("Monitor.disconnect | 监控强拆," + JSON.stringify(params), 'blue');
			WebSocketClient.addResponseListener("disconnect", callback);
			WebSocketClient.sendToken({
				type: "disconnect",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				disconnectedCno: params.disconnectedCno,
				objectType: 1,
				disconnectObject: UserBasic.getCno()
			});
		};
		return Monitor;
	})(WebSocketClient);

	//Agent 操作封装, 响应处理
	var Agent = (function () {
		function Agent() {}
		var close = function () {
			debug("Agent.close | 断开连接", 'blue');
			if (GLOBAL.pingstId != '' && GLOBAL.pingstId != 'undefined') {
				clearTimeout(GLOBAL.pingstId);
				GLOBAL.pingstId = '';
			}
			if (WebSocketClient != null) {
				WebSocketClient.disconnect();
			}
		};
		var ping = function (options) {
			var lEcho = true;
			if (options) {
				if (options.echo) {
					lEcho = true;
				}
			}
			debug("Agent.ping | {type:ping, sessionKey:" + UserBasic.getSessionKey() + "}", 'blue');
			WebSocketClient.sendToken({
					type: "ping",
					sessionKey: UserBasic.getSessionKey(),
					echo: lEcho
				},
				options
			);
			GLOBAL.lastPingTime = new Date().getTime();
		};
		var autoReconnect = function () {
			if (GLOBAL.connectionCloseCount >= 11) {
				debug("Agent.autoReconnect | 连接失败,请稍后再试或联系管理员");
			} else {
				debug("Agent.autoReconnect | 系统正在第" + GLOBAL.connectionCloseCount + "次尝试连接...");

				autoReconnectsitId = setInterval(function() {
					GLOBAL.randoms = GLOBAL.randoms - 1;
					if (GLOBAL.randoms == 0) {
						//noCare
						UserBasic.setLoginStatus('-1');
						GLOBAL.connected = false;
						var params = {};
						params.enterpriseId = UserBasic.getEnterpriseId();
						params.cno = UserBasic.getCno();
						params.loginStatus = UserBasic.getLoginStatus();
						params.pauseDescription = UserBasic.getPauseDescription();
						params.bindTel = UserBasic.getBindTel();
						params.bindType = UserBasic.getBindType();
						params.qids = UserBasic.getQids();
						params.sessionKey = UserBasic.getSessionKey();
						params.webSocketUrl = UserBasic.getWebSocketUrl();
						Agent.login(params);
						clearTimeout(autoReconnectsitId);
					}
					if (GLOBAL.connectionCloseCount == 10) {
						debug("Agent.autoReconnect | 自动重连尝试已经达到最大次数(10次),请手动重连或联系管理员");
						clearTimeout(autoReconnectsitId);
					}
				}, 1000);
			}
		};
		var responseHandler = function (token) {
			switch (token.reqType) {
				case 'login':
					if (token.code == '0') {
						if (token.isAgentDebug == '1') {
							GLOBAL.isDebug = true;
						}
						if (GLOBAL.pingstId != '' && GLOBAL.pingstId != 'undefined') {
							clearTimeout(GLOBAL.pingstId);
							GLOBAL.pingstId = '';
						}
						ping();

						//如果是软电话， 而且不是断线重连
						if (token.bindType == 3 && token.loginStatus != -1) {
							//sipregister
							UserBasic.setSipIp(token.sipIp);
							UserBasic.setSipPwd(token.sipPwd);
							UserBasic.setWebrtcSocketUrl(token.webrtcSocketUrl);
							UserBasic.setWebrtcStunServer(eval(token.webrtcStunServer));

							SipPhone.sipRegister();

							//检查是否注册成功, 20次
							var st = 1;
							var objMsg = {};
							var setSipIntervalId = setInterval(function(){
								st++;
								if(SipPhone.isRegistered()){
									clearInterval(setSipIntervalId);
									objMsg.code = '0';
									objMsg.msg = '软电话注册成功';
									Event.callback("login", objMsg);
								}
								if(st == 20){
									clearInterval(setSipIntervalId);
									objMsg.code = '-1';
									objMsg.msg = '软电话注册失败';
									Event.callback("login", objMsg);
									debug("软电话注册失败, 请退出重新登录");
								}
							},500);
						}
					} else {
						close();
					}
					break;
				case 'kickout':
				case 'logout':
					//软电话
					if (UserBasic.getBindType() == 3) {
						SipPhone.sipUnRegister();
					}
					close();
					GLOBAL.logout = true;
					break;
				case 'changeBindTel':
					UserBasic.setBindTel(token.bindTel);
					UserBasic.setBindType(token.bindType);
					break;
				case 'ping':
					GLOBAL.pingValue = true;
					//每次都清空
					if (GLOBAL.pingstId != '' && GLOBAL.pingstId != 'undefined') {
						clearTimeout(GLOBAL.pingstId);
						GLOBAL.pingstId = '';
					}
					GLOBAL.pingstId = setTimeout(function () {
						ping();
						//ping time to 30s  by fengwei 20131018
					}, 30000);
					GLOBAL.latency = (new Date() -GLOBAL.lastPing);
					break;
			}
		};

		window.onbeforeunload = function () {
			debug("Agent window.onbeforeunload | window关闭", 'blue');
			if (GLOBAL.connected && !GLOBAL.logout) {
				if (UserBasic.getBindType() == 3) {
					SipPhone.sipUnRegister();
				}
				var params = {};
				params.logoutMode = 1;
				Agent.logout(params);
			}
		};
		Agent.login = function (params, callback) {
			debug("Agent.login | 登录," + JSON.stringify(params), 'blue');
			UserBasic = new User(params);
			if (GLOBAL.connected) {// 改成false 保证多个连接
				WebSocketClient.sendToken({
					type: "login",
					enterpriseId: UserBasic.getEnterpriseId(),
					cno: UserBasic.getCno(),
					loginStatus: UserBasic.getLoginStatus(),
					pauseDescription: UserBasic.getPauseDescription(),
					bindTel: UserBasic.getBindTel(),
					bindType: UserBasic.getBindType(),
					loginType: 1
				});
			} else {
				var options = {};
				// OnOpen callback
				options.OnOpen = function (token) {
					GLOBAL.logout = false;
					debug("Agent.OnOpen | 连接成功");
					GLOBAL.connectionCloseCount = 0;
					GLOBAL.randoms = randomNumber('5');
					var objMsg = {};
					objMsg.type = "event";
					objMsg.name = "breakLine";
					objMsg.enterpriseId = UserBasic.getEnterpriseId();
					objMsg.cno = UserBasic.getCno();
					objMsg.msg = 'open';
					objMsg.code = '0';
					objMsg.randoms = GLOBAL.randoms;
					GLOBAL.randoms = 0;
					Event.callback("breakLine", objMsg);
				};
				// OnMessage callback debug用
				options.OnMessage = function (token) {
					if (token.type == 'response') {  //action的响应
						debug("Agent.OnMessage | 收到响应, " + JSON.stringify(token), 'green');
					} else if (token.type == 'event') { //事件
						debug("Agent.OnMessage | 收到事件, " + JSON.stringify(token), 'red');
					} else {
						debug("Agent.OnMessage | 收到其他, " + JSON.stringify(token));
					}
				};
				// OnClose callback
				options.OnClose = function (token) {
					if (GLOBAL.logout == false) {
						GLOBAL.connectionCloseCount = GLOBAL.connectionCloseCount + 1;
						GLOBAL.randoms = randomNumber('5');
						var objMsg = {};
						objMsg.type = "event";
						objMsg.name = "breakLine";
						objMsg.enterpriseId = UserBasic.getEnterpriseId();
						objMsg.cno = UserBasic.getCno();
						objMsg.msg = 'close';
						objMsg.attempts = GLOBAL.connectionCloseCount;
						objMsg.randoms = GLOBAL.randoms;
						objMsg.code = '-1';
						Event.callback("breakLine", objMsg);
						debug("Agent.OnClose | 连接断开");
						autoReconnect();
					}
				};
				WebSocketClient.addResponseListener("login", callback);
				// 注册handler, 建立WebSocket连接
				WebSocketClient.registerResponseHandler("agent", responseHandler);
				WebSocketClient.connect(options);
			}
		};
		Agent.logout = function (params, callback) {//退出
			debug("Agent.logout | 登出," + JSON.stringify(params), 'blue');
			WebSocketClient.addResponseListener("logout", callback);
			if (params.removeBinding != 0 && params.removeBinding != 1) {
				params.removeBinding = 0;
			}
			if (params.logoutMode == 0) {
				params.removeBinding = 0;
			}
			if (params.removeBinding == 1 || params.logoutMode == 1) {
				WebSocketClient.sendToken({
					type: "logout",
					enterpriseId: UserBasic.getEnterpriseId(),
					cno: UserBasic.getCno(),
					removeBinding: params.removeBinding,
					logoutType: 1
				});
			} else {
				close();
			}
		};
		Agent.queueStatus = function (params, callback) {//获取Q数据  座席和队列
			debug("Agent.queueStatus | 队列状态," + JSON.stringify(params), 'blue');
			WebSocketClient.addResponseListener("queueStatus", callback);
			if (params.qnos == undefined) {
				params.qnos = '';
			}
			if (params.fields == undefined) {
				params.fields = '';
			}
			WebSocketClient.sendToken({
				type: "queueStatus",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				qnos: params.qnos,
				fields: params.fields
			});
		};
		Agent.pause = function (params, callback) {//置忙
			debug("Agent.pause | 置忙," + JSON.stringify(params), 'blue');
			WebSocketClient.addResponseListener("pause", callback);
			if (isNaN(params.pauseType)) {
				params.pauseType = 1;
			}
			if (params.pauseDescription == undefined || params.pauseDescription == '') {
				params.pauseDescription = '置忙';
			}
			WebSocketClient.sendToken({
				type: "pause",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				pauseType: params.pauseType,
				pauseDescription: params.pauseDescription
			});
		};
		Agent.unpause = function (params, callback) {//置闲
			debug("Agent.unpause | 置闲" + JSON.stringify(params), 'blue');
			WebSocketClient.addResponseListener("unpause", callback);
			WebSocketClient.sendToken({
				type: "unpause",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno()
			});
		};
		Agent.status = function (params, callback) {//获取座席状态
			debug("Agent.status | 座席状态," + JSON.stringify(params), 'blue');
			WebSocketClient.addResponseListener("status", callback);
			WebSocketClient.sendToken({
				type: "status",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				monitoredCno: params.monitoredCno
			});
		};
		Agent.previewOutcall = function (params, callback) {////外呼
			debug("Agent.previewOutcall | 预览外呼," + JSON.stringify(params), 'blue');
			WebSocketClient.addResponseListener("previewOutcall", callback);
			if (params.tel == undefined || params.tel.length == 0) {
				debug("Agent.previewOutcall | 预览外呼, Error invalid tel");
				return;
			}
			//除去空格等特殊字符和中横线
			params.tel = params.tel.replace(/\s+/g, "");
			params.tel = params.tel.replace(/-/g, "");

			if (isNaN(params.timeout) || params.timeout > 60
				|| params.timeout < 5) {
				params.timeout = 30;
			}
			if (isNaN(params.dialTelTimeout) || params.dialTelTimeout > 60
				|| params.dialTelTimeout < 5) {
				params.dialTelTimeout = 45;
			}
			if (params.obClid == undefined) {
				params.obClid = '';
			}
			if (params.requestUniqueId == undefined) {
				params.requestUniqueId = '';
			}
			if (params.userField == undefined) {
				params.userField = {};
			}
			WebSocketClient.sendToken({
				type: "previewOutcall",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				previewOutcallTel: params.tel,
				timeout: params.timeout,
				dialTelTimeout: params.dialTelTimeout,
				obClidLeftNumber: params.obClid,
				userField: params.userField,
				requestUniqueId: params.requestUniqueId
			});
		};
		Agent.directCallStart = function (params, callback) {//
			debug("Agent.directCallStart | 主叫外呼," + JSON.stringify(params), 'blue');
			WebSocketClient.addResponseListener("directCallStart", callback);
			if (isNaN(params.tel)) {
				debug("Agent.directCallStart | 主叫外呼, Error invalid tel");
				return;
			}
			WebSocketClient.sendToken({
				type: "directCallStart",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				customerNumber: params.tel
			});
		};
		Agent.changeBindTel = function (params, callback) {//更改绑定电话
			debug("Agent.changeBindTel | 修改绑定电话," + JSON.stringify(params), 'blue');
			WebSocketClient.addResponseListener("changeBindTel", callback);
			if (isNaN(params.bindTel)) {
				debug("Agent.changeBindTel | 修改绑定电话, Error invalid bindTel");
				return;
			}
			if (isNaN(params.bindType)) {
				debug("Agent.changeBindTel | 修改绑定电话, Error invalid bindType");
				return;
			}
			WebSocketClient.sendToken({
				type: "changeBindTel",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				bindTel: params.bindTel,
				bindType: params.bindType
			});
		};
		Agent.setCdrTag = function (params, callback) {
			debug("Agent.setCdrTag | 设置tag," + JSON.stringify(params), 'blue');
			WebSocketClient.addResponseListener("setCdrTag", callback);
			WebSocketClient.sendToken({
				type: "setCdrTag",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				uniqueId: params.uniqueId,
				callType: params.callType,
				key: params.key,
				value: params.value
			});
		};
		Agent.getCdrTag = function (params, callback) {
			debug("Agent.getCdrTag | 获取tag," + JSON.stringify(params), 'blue');
			WebSocketClient.addResponseListener("getCdrTag", callback);
			if (params.key == undefined) {
				params.key = '';
			}
			WebSocketClient.sendToken({
				type: "getCdrTag",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				uniqueId: params.uniqueId,
				key: params.key
			});
		};
		Agent.sipCall = function (params, callback) {
			debug("Agent.sipCall | 软电话外呼," + JSON.stringify(params), 'blue');
			WebSocketClient.addResponseListener("sipCall", callback);
			SipPhone.sipCall(params.tel);
		};
		Agent.prolongWrapup = function (params, callback) {
			debug("Agent.prolongWrapup | 延长整理时间," + JSON.stringify(params), 'blue');
			WebSocketClient.addResponseListener("prolongWrapup", callback);
			if (isNaN(params.wrapupTime)) {
				debug("Agent.prolongWrapup | 延长整理时间, Error invalid wrapupTime");
				return;
			}
			if (params.wrapupTime < 30 || params.wrapupTime > 600) {
				debug("Agent.prolongWrapup | 延长整理时间, Error invalid wrapupTime");
				return;
			}
			WebSocketClient.sendToken({
				type: "prolongWrapup",
				enterpriseId: UserBasic.getEnterpriseId(),
				cno: UserBasic.getCno(),
				wrapupTime: params.wrapupTime
			});
		};
		return Agent;
	})(WebSocketClient, SipPhone, Event);

	CTILink.Session = Session;
	CTILink.SipSession = SipSession;
	CTILink.Monitor = Monitor;
	CTILink.Agent = Agent;

	CTILink.event = function (type, callback) {
		Event.addListener(type, callback);
	};

	return CTILink;
})(CTILink=(typeof CTILink == 'undefined' ? {} : CTILink));