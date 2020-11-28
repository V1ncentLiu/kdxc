(function webpackUniversalModuleDefinition(root, factory) {
	if(typeof exports === 'object' && typeof module === 'object')
		module.exports = factory();
	else if(typeof define === 'function' && define.amd)
		define([], factory);
	else if(typeof exports === 'object')
		exports["NIMUIKit"] = factory();
	else
		root["NIMUIKit"] = factory();
})(this, function() {
return /******/ (function(modules) { // webpackBootstrap
/******/ 	// The module cache
/******/ 	var installedModules = {};
			
/******/ 	// The require function
/******/ 	function __webpack_require__(moduleId) {

/******/ 		// Check if module is in cache
/******/ 		if(installedModules[moduleId])
/******/ 			return installedModules[moduleId].exports;

/******/ 		// Create a new module (and put it into the cache)
/******/ 		var module = installedModules[moduleId] = {
/******/ 			exports: {},
/******/ 			id: moduleId,
/******/ 			loaded: false
/******/ 		};

/******/ 		// Execute the module function
/******/ 		modules[moduleId].call(module.exports, module, module.exports, __webpack_require__);

/******/ 		// Flag the module as loaded
/******/ 		module.loaded = true;

/******/ 		// Return the exports of the module
/******/ 		return module.exports;
/******/ 	}


/******/ 	// expose the modules object (__webpack_modules__)
/******/ 	__webpack_require__.m = modules;

/******/ 	// expose the module cache
/******/ 	__webpack_require__.c = installedModules;

/******/ 	// __webpack_public_path__
/******/ 	__webpack_require__.p = "";

/******/ 	// Load entry module and return exports
/******/ 	return __webpack_require__(0);
/******/ })
/************************************************************************/
/******/ ([
/* 0 */
/***/ function(module, exports, __webpack_require__) {

	/**
	 * ------------------------------------------------------------
	 * NIM_UI      云信web UI库
	 * ------------------------------------------------------------
	 */

	'use strict';

	var uiKit = {};

	/**
	 * list
	 */

	uiKit.SessionList = __webpack_require__(1);
	uiKit.FriendList = __webpack_require__(3);
	uiKit.TeamList = __webpack_require__(4);


	module.exports = uiKit;

/***/ },
/* 1 */
/***/ function(module, exports, __webpack_require__) {

	/**
	 * ------------------------------------------------------------
	 * SesstionList      会话面板UI
	 * ------------------------------------------------------------
	 */

	'use strict';
	var util = __webpack_require__(2);

	/**
	 * 会话列表控件
	 * @param {Object} options 控件初始化参数
	 * @property {String||Node}  parent 父节点
	 * @property {String} clazz 样式名称
	 * @property {Function} onclickitem 点击列表回调
	 * @property {Function} onclickavatar 点击列表头像回调
	 * @property {Object} data 消息数据 data.sessions 消息数据 
	 * @property {Function} infoprovider 由上层来提供显示内容
	 */
	var SessionList = function(options){
		var parent = options.parent,
			data = options.data,
			cbClickList = options.onclickitem||function(account,type){console.log('account:'+account+'---type:'+type);},
			cbClickPortrait = options.onclickavatar||function(account,type){console.log('account:'+account+'---type:'+type);};
		this._body = document.createElement('ul');
		this._body.className = options.clazz||"m-panel" +" j-session";	
		this.provider = options.infoprovider;
		util.addEvent(this._body,'click',function(e){
			var self = this,
				evt = e||window.event,
				account,
				scene,
	            target = evt.srcElement||evt.target;
	        while(self!==target){
	        	if (target.tagName.toLowerCase() === "img") {
	                // var item = target.parentNode.parentNode;
	                // account = item.getAttribute("data-account");
	                // scene = item.getAttribute("data-scene");
	                // cbClickPortrait(account,scene);
	                return;
	            }else if(target.tagName.toLowerCase() === "li"){
					let clientHeight=document.documentElement.clientHeight
					layer.close(chatWindow)
					if(chatWindow){
						layer.close(chatWindow)
						$('#rightPanel').css({
							'display':'none'
						})
						chatWindow=''
					}

					setTimeout(()=>{
						$('#rightPanel').css({
							'display':'block'
						})
						let layerWidth='800px'
						chatWindow=layer.open({
							type: 1, //Layer提供了5种层类型。可传入的值有：0（信息框，默认）1（页面层）2（iframe层）3（加                    
									//   载层）4（tips层）,
							title: ' ',   //标题
							// area: ['800px', '720px'],   //宽高
							area: [layerWidth, clientHeight*0.7+'px'],   //宽高
							shade: 0,   //遮罩透明度
							skin: 'sessionListFrame',
							content: $("#rightPanel"),//支持获取DOM元素
							scrollbar: false ,//屏蔽浏览器滚动条
							maxmin: true,
							success:function(){
								$('.sessionListFrame .layui-layer-title').css({
									'backgroundColor': '#ECEDF2',
									'borderBottom':'none',
									'height':'24px',
									'paddingRight':'40px'
								})
								sessionListNum=0
								isSessionListNum=false
							},
							min:function(){
								$('.sessionListFrame .layui-layer-title').css({
									'height':'42px'
								})
								$('.sessionListFrame .layui-layer-title').css({
									'height':'42px'
								})
								$('.sessionListFrame').css({
									'left':'400px'
								})
								var str=['<div style="display: flex;align-items: center;">',
										'<img style="border-radius: 50%;display: inline-block;width: 20px;height: 20px;" src="'+$('#headImg')[0].src+'"/>',
										'<span style="margin:0 10px;">'+showLittleBit($('#nickName').text(),5)+'</span>',
										// sessionListNum>0?'<span style="text-align: center;font-weight:700;line-height:16px;display: inline-block;width: 16px;height: 16px;background-color: red;border-radius: 50%;color: #fff;">'+sessionListNum+'</span>':'',
									'</div>'].join("")
								$('.sessionListFrame .layui-layer-title').html(str)
								isSessionListNum=true
								//如果是已经存在的会话记录, 会将此会话未读数置为 0, 开发者会收到onupdatesession回调,之后此会话在收到消息之后不会更新未读数
								nim.setCurrSession()
							},
							restore:function(){
								sessionListNum=0
								isSessionListNum=false
								$('.sessionListFrame .layui-layer-title').css({
									'backgroundColor': '#ECEDF2',
									'borderBottom':'none',
									'height':'24px',
								})
								$('.sessionListFrame .layui-layer-title').html('')
								console.log(yunXin.crtSession+'当前窗口会话id')
								nim.setCurrSession(yunXin.crtSession)
							},
							cancel:function(){
								$('#rightPanel').css({
									'display':'none'
								})
								//如果是已经存在的会话记录, 会将此会话未读数置为 0, 开发者会收到onupdatesession回调,之后此会话在收到消息之后不会更新未读数
								nim.setCurrSession()
							}
						});
						 account = target.getAttribute("data-account");
						scene = target.getAttribute("data-scene");
						cbClickList(account,scene);
					},300)
	                return;
				}
				
	            target = target.parentNode;
	        }    
		});
		this.update(data);
		if(!!parent){
			this.inject(parent);
		}
	};
	/** --------------------------public------------------------------ */

	/**
	 * 插入控件
	 * @param  {Node｜String} node 插入控件的节点
	 * @return {Void}      
	 */
	SessionList.prototype.inject = function(node){
	    var injectNode = util.getNode(node);
		injectNode.innerHTML = "";
		injectNode.appendChild(this._body);
	};

	// 获取后端接口数据合并ws推送的数据
	function sessionsListConcat1(sessions){
		console.log("获取后端接口数据合并ws推送的数据",sessions)
		if(!sessions){
			return [];
		}
	    var newIdList=[]
	    var newSessionList=[]

	    for(var i=0;i<sessions.length;i++){
	      if(sessions[i].scene=='p2p'){
	        newIdList.push(sessions[i].to)
	      }
	    }
	    console.log(newIdList,111111)
	    var params = {idList: newIdList};
	    $.ajax({
	        url : '/im/brandAndIssubmit',
	        type:'POST',
	        data:JSON.stringify(params),
	        async:false,
	        contentType: "application/json; charset=utf-8",
	        dataType:'json',
	        success: function(result){
	          console.log(result,'左侧请求后端接口数据');
	          if(result.code=='0'){
	            var data=result.data
	            for(let i=0;i<sessions.length;i++){
	              for(let j=0;j<data.length;j++){
	                  if(sessions[i].to==data[j].imId&&sessions[i].scene=='p2p'){
	                      sessions[i].accountId=data[j].accountId
	                      sessions[i].brandName=data[j].brandName
	                      sessions[i].clueId=data[j].clueId
	                      sessions[i].isSubmit=data[j].isSubmit
	                      sessions[i].nickName=data[j].nickName
	                      // 新增优惠券
	                      sessions[i].hasCoupon=data[j].hasCoupon
	                      newSessionList.push(sessions[i])
	                  }
	              }
	            }
	          }else{
	            console.log(result.code)
	          }
	        },
	        error:function(request){
	          alert(request)
	        }
	    })
	    return newSessionList
  	}

	/**
	 * 更新视图
	 * @param  {Object} data 
	 * @return {Void}   
	 */
	SessionList.prototype.update = function(data){
		var newSession=sessionsListConcat1(data.sessions)
		console.log(data,'渲染左测列表数据');
		sessionListObj=newSession;
		var html = '',
			i,
			unreadNum=0,
	        str,
	        info,
			sessions = newSession;
		if (sessions.length === 0) {
			html += '<p class="empty">暂无最近联系人哦</p>';
		}else{
			for (i = 0;i<sessions.length;i++) {
				
				info = this.provider(sessions[i],"session");
	            if(!info){
	                continue;
				}
	            var account = info.account
	            var personSubscribes = data.personSubscribes
	            var multiPortStatus = ''
	            // 开启了订阅配置
	            if (info.scene === 'p2p' && window.CONFIG && window.CONFIG.openSubscription) {
	                multiPortStatus = '离线'
	                if (personSubscribes[account] && personSubscribes[account][1]) {
	                    multiPortStatus = (personSubscribes[account][1].multiPortStatus) || '离线'
	                }
	            }
	            if (multiPortStatus !== '') {
	                var infoText = '[' + multiPortStatus + '] ' + info.text
	            } else {
	                infoText = info.text
				}
				unreadNum+=Number(info.unread)
	            str = ['<li class="panel_item '+(info.crtSession===info.target?'active':'')+'" data-scene="' + info.scene + '" data-account="' + info.account + '">',
								'<div class="panel_avatar">',
								'<img class="panel_image" src="'+(info.avatar==='null'?'https://static-huiju-new.kuaidao.cn/lark/Lark20200911-182905.png':info.avatar)+'"/>',
								'</div>',
	                            '<div class="panel_text">',
	                                '<p class="panel_multi-row">',
										'<span class="panel_nick">',
										sessions[i].isSubmit&&sessions[i].brandName?(sessions[i].nickName?sessions[i].nickName: info.nick).slice(0,6):(sessions[i].nickName?sessions[i].nickName: info.nick),
										// info.nick ,
										sessions[i].hasCoupon==1?'<img style="width: 13px;height: 10px;display:inline-block;" src="/im/images/newImages/coupon.png" />':'',
										 sessions[i].isSubmit?'<b class="panel_customer">我的客户</b> ':'',
										 sessions[i].brandName?'<b class="panel_brand">'+(sessions[i].isSubmit?sessions[i].brandName.slice(0,4):sessions[i].brandName.slice(0,9))+'</b>':'',
										 '</span>',
										// '<img class="" src="" />',
	                                    '<b class="panel_time">' + info.time + '</b>',
	                                '</p>',
	                                '<p class="panel_multi-row">',
	                                    '<span class="panel_lastMsg">' + infoText + '</span>',
	                                    info.unread ? (info.unread>99?'<b class="panel_count"  style="padding:0 2px">' +99+'+' + '</b>':'<b class="panel_count">' + info.unread + '</b>'):'',
	                                '</p>',
	                            '</div>',
	                        '</li>'].join("");
				html += str;
			}    
		}
		var str=['<div style="display: flex;align-items: center;">',
					'<img style="border-radius: 50%;display: inline-block;width: 20px;height: 20px;" src="'+$('#userPic')[0].src+'"/>',
					'<span style="margin:0 10px;">我的会话</span>',
					unreadNum>0?'<span style="text-align: center;line-height:24px;font-size:12px;display: inline-block;width: 24px;height: 24px;background-color: red;border-radius: 50%;color: #fff;">'+(unreadNum>10?10+'+':unreadNum)+'</span>':'',
				'</div>'].join("")
		if(!isLayerOpen){
			$('.mainHtmlLayer .layui-layer-title').html(str)
		}
		dataNum=unreadNum
		this._body.innerHTML = html;
	};

	/**
	 * 控件销毁
	 * @return {void} 
	 */
	SessionList.prototype.destory = function(){
		//预留
	};


	module.exports = SessionList;

/***/ },
/* 2 */
/***/ function(module, exports) {

	/**
	 * ------------------------------------------------------------
	 * util     工具库
	 * ------------------------------------------------------------
	 */

	'use strict';
	// var CONST  = require("./const.js");

	var util = {
		getNode: function(ipt,node){
			if(this.isString(ipt)){
				node = node||document;
				return node.querySelector(ipt);	
			}else if(this.isElement(ipt)){
				return ipt;
			}else{
				console.error("输入参数必须为node||String");
			}
		},
		getNodes: function(string){
			return document.querySelectorAll(string);
		},
		isString: function(data){
	        return typeof(data)==='string';
	    },
	    isElement:function(obj){
	    	return !!(obj && obj.nodeType === 1);
	    },
	    isArray:Array.isArray|| function(obj) {
			return Object.prototype.toString.call(obj) === '[object Array]';
	  	},

		addEvent: function(node,type,callback){
			if(window.addEventListener){
				node.addEventListener(type,callback,false);
			}else{
				node.attachEvent("on"+type,callback);
			}
		},

		hasClass: function(elem, cls){
		    cls = cls || '';
		    if(cls.replace(/\s/g, '').length === 0){
		    	return false;
		    }
		    return new RegExp(' ' + cls + ' ').test(' ' + elem.className + ' ');
		},

		addClass: function(elem, cls){
			if(!elem){
				return;
			}
		    if(!this.hasClass(elem, cls)){
		        elem.className += ' ' + cls;
		    }
		},
		removeClass: function(elem, cls){
			if(!elem){
				return;
			}
		    if(this.hasClass(elem, cls)){
		        var newClass = ' ' + elem.className.replace(/[\t\r\n]/g, '') + ' ';
		        while(newClass.indexOf(' ' + cls + ' ') >= 0){
		            newClass = newClass.replace(' ' + cls + ' ', ' ');
		        }
		        elem.className = newClass.replace(/^\s+|\s+$/g, '');
		    }
		},
		safeHtml: (function(){
		    var reg = /<br\/?>$/,
		        map = {
		            r:/<|>|\&|\r|\n|\s|\'|\"/g,
		            '<':'&lt;','>':'&gt;','&':'&amp;',' ':'&nbsp;',
		            '"':'&quot;',"'":'&#39;','\n':'<br/>','\r':''
		        };
		    return function(content){
		        content = _$encode(map,content);
		        return content.replace(reg,'<br/><br/>');
		    };
		})()
	};
	var _$encode = function(_map,_content){
	    _content = ''+_content;
	    if (!_map||!_content){
	        return _content||'';
	    }
	    return _content.replace(_map.r,function($1){
	        var _result = _map[!_map.i?$1.toLowerCase():$1];
	        return _result!=null?_result:$1;
	    });
	};

	module.exports = util;

/***/ },
/* 3 */
/***/ function(module, exports, __webpack_require__) {

	/**
	 * ------------------------------------------------------------
	 * FriendList      好友列表UI
	 * ------------------------------------------------------------
	 */

	'use strict';
	var util = __webpack_require__(2);

	var ACCOUNT;
	/**
	 * 好友列表控件
	 * @param {Object} options 控件初始化参数
	 * @property {String||Node}  parent 父节点
	 * @property {String} clazz 样式名称
	 * @property {Function} onclickitem 点击列表回调
	 * @property {Function} onclickavatar 点击列表头像回调
	 * @property {Object} data 消息数据 data.friends 好友数据 data.friends  data.userinfo 用户信息 data.account 当前用户账号
	 */
	var FriendList = function(options){
		var parent = options.parent,
			data = options.data,
			cbClickList = options.onclickitem||function(account,type){console.log('account:'+account+'---type:'+type);},
			cbClickPortrait = options.onclickavatar||function(account,type){console.log('account:'+account+'---type:'+type);};
		ACCOUNT = options.account;
		this.provider = options.infoprovider;
		this._body = document.createElement('ul');
		this._body.className = options.clazz||"m-panel" +" j-friend";	
		var that=this
		util.addEvent(this._body,'click',function(e){
			var self = this,
				evt = e||window.event,
				account,
				scene,
	            target = evt.srcElement||evt.target;
	        while(self!==target){
	        	if (target.tagName.toLowerCase() === "img") {
	                // var item = target.parentNode.parentNode;
	                // account = item.getAttribute("data-account");
	                // scene = item.getAttribute("data-scene");
	                // cbClickPortrait(account,scene);
	                return;
	            }else if(target.tagName.toLowerCase() === "li"){
					if(chatWindow){
						layer.close(chatWindow)
						$('#rightPanel').css({
							'display':'none'
						})
						chatWindow=''
					}
					setTimeout(()=>{
						$('#rightPanel').css({
							'display':'block'
						})

						chatWindow=layer.open({
							type: 1, //Layer提供了5种层类型。可传入的值有：0（信息框，默认）1（页面层）2（iframe层）3（加                    
									//   载层）4（tips层）,
							title: ' ',   //标题
							// area: ['800px', '720px'],   //宽高
							area: ['800px', '720px'],   //宽高
							shade: 0,   //遮罩透明度
							skin: 'sessionListFrame',
							// content: $("#chatBox"),//支持获取DOM元素
							content: $("#rightPanel"),//支持获取DOM元素
							// btn: ['确定', '取消'], //按钮组
							scrollbar: false ,//屏蔽浏览器滚动条
							maxmin: true,
							success:function(){
								$('.sessionListFrame .layui-layer-title').css({
									'backgroundColor': '#ECEDF2',
									'borderBottom':'none',
									'height':'24px',
									'paddingRight':'40px'
								})
								sessionListNum=0
								isSessionListNum=false
							},
							min:function(){
								$('.sessionListFrame .layui-layer-title').css({
									'height':'42px'
								})
								$('.sessionListFrame .layui-layer-title').css({
									'height':'42px'
								})
								var str=['<div style="display: flex;align-items: center;">',
										'<img style="border-radius: 50%;display: inline-block;width: 20px;height: 20px;" src="'+$('#headImg')[0].src+'"/>',
										'<span style="margin:0 10px;">'+showLittleBit($('#nickName').text(),5)+'</span>',
										// sessionListNum>0?'<span style="text-align: center;font-weight:700;line-height:16px;display: inline-block;width: 16px;height: 16px;background-color: red;border-radius: 50%;color: #fff;">'+sessionListNum+'</span>':'',
									'</div>'].join("")
								$('.sessionListFrame .layui-layer-title').html(str)
								isSessionListNum=true
							},
							restore:function(){
								sessionListNum=0
								isSessionListNum=false
								$('.sessionListFrame .layui-layer-title').css({
									'backgroundColor': '#ECEDF2',
									'borderBottom':'none',
									'height':'24px',
								})
								$('.sessionListFrame .layui-layer-title').html('')
							},
							cancel:function(){
								$('#rightPanel').css({
									'display':'none'
								})
							}
						});
						 account = target.getAttribute("data-account");
						scene = target.getAttribute("data-scene");
						console.log('打开friend聊天框',account,scene);
						cbClickList(account,scene);
					},500)
	                return;
	            }
	            target = target.parentNode;
	        }    
		});
		this.update(data);
		if(!!parent){
			this.inject(parent);
		}
	};
	/** --------------------------public------------------------------ */


	FriendList.prototype.formatDate=function(shijianchuo){
		function add0(m){return m<10?'0'+m:m }
			//shijianchuo是整数，否则要parseInt转换
			var time = new Date(shijianchuo);
			var y = time.getFullYear();
			var m = time.getMonth()+1;
			var d = time.getDate();
			var h = time.getHours();
			var mm = time.getMinutes();
			var s = time.getSeconds();
			return y+'-'+add0(m)+'-'+add0(d)+' '+add0(h)+':'+add0(mm)+':'+add0(s);
	}

	/**
	 * 插入控件
	 * @param  {Node｜String} node 插入控件的节点
	 * @return {Void}      
	 */
	FriendList.prototype.inject = function(node){
		var injectNode = util.getNode(node);
		injectNode.innerHTML = "";
		injectNode.appendChild(this._body);
	};

	/**
	 * 更新视图
	 * @param  {Object} data 
	 * @return {Void}   
	 */
	FriendList.prototype.update = function(data){
		var html="",
			list = data.friends,
			str,
			info;
			if (list.length === 0) {
				html += '<p class="empty">暂无提交客户</p>';
			}else{
				for (var i = 0; i < list.length; i++) {
					info = this.provider(list[i],"friend");
						var account = list[i].account
						var personSubscribes = data.personSubscribes
						var multiPortStatus = ''
						// 开启了订阅配置
						if (window.CONFIG && window.CONFIG.openSubscription) {
							multiPortStatus = '[离线]'
							if (personSubscribes[account] && personSubscribes[account][1]) {
							multiPortStatus = (personSubscribes[account][1].multiPortStatus) || '离线'
							multiPortStatus = '[' + multiPortStatus + ']'
							}
						}
						if (multiPortStatus !== '') {
							var infoText = multiPortStatus+ info.nick
						} else {
							infoText = info.nick
						}
							
						html += ['<li class="panel_item '+(info.crtSession===info.target?'active':'')+'" data-scene="p2p" data-account="' + info.account + '">',
									'<div class="panel_avatar"><img class="panel_image" src="'+list[i].icon+'"/></div>',
									'<div class="panel_text">',
									'<p class="panel_multi-row">',
										'<span class="panel_nick">',
										list[i].cusName ,
										list[i].brandName?'<b class="panel_brand">'+list[i].brandName+'</b>':'',
										'</span>',
										list[i].customerStatus?'<b class="panel_time">[' +list[i].customerStatus + ']</b>':'',
									'</p>',
									list[i].createTime?'<span class="panel_lastMsg">提交时间：' +this.formatDate(list[i].createTime)+ '</span>':'',
									'</div>',
								'</li>'].join("");
				}
			}
		this._body.innerHTML = html;
	};

	/**
	 * 控件销毁
	 * @return {void} 
	 */
	FriendList.prototype.destory = function(){
		//预留
	};




	module.exports = FriendList;

/***/ },
/* 4 */
/***/ function(module, exports, __webpack_require__) {

	/**
	 * ------------------------------------------------------------
	 * TeamList      群组列表UI
	 * ------------------------------------------------------------
	 */

	'use strict';
	var util = __webpack_require__(2);

	/**
	 * 群组列表控件
	 * @param {Object} options 控件初始化参数
	 * @property {String||Node}  parent 父节点
	 * @property {String} clazz 样式名称
	 * @property {Function} onclickitem 点击列表回调
	 * @property {Function} onclickavatar 点击列表头像回调
	 * @property {Object} data 消息数据 data.teams 群组数据
	 */
	var TeamList = function(options){
		var that = this,
	        parent = options.parent,
			data = options.data,
			cbClickList = options.onclickitem||function(account,type){console.log('account:'+account+'---type:'+type);},
			cbClickPortrait = options.onclickavatar||function(account,type){console.log('account:'+account+'---type:'+type);};

		this._body = document.createElement('ul');
		this._body.className = (options.clazz||"m-panel") +" j-team";	
	    this.provider = options.infoprovider;
		util.addEvent(this._body,'click',function(e){
			var self = this,
				evt = e||window.event,
				account,
				type,
	            target = evt.srcElement||evt.target;
	        while(self!==target){
	        	if (target.tagName.toLowerCase() === "img") {
	                var item = target.parentNode.parentNode;
	                account = item.getAttribute("data-account");
	                type = item.getAttribute("data-type");
	                cbClickPortrait(account,type);
	                return;
	            }else if(target.tagName.toLowerCase() === "li"){
					console.log(33333);
	        	 	account = target.getAttribute("data-account");
	                type = target.getAttribute("data-type");
	                util.removeClass(util.getNode(".j-team li.active",that._body),'active');
	                util.addClass(target,"active");
					cbClickList(account,type);
					
	                return;
	            }
	            target = target.parentNode;
	        }    
		});
		this.update(data);
		if(!!parent){
			this.inject(parent);
		}
	};

	/** --------------------------public------------------------------ */

	/**
	 * 插入控件
	 * @param  {Node｜String} node 插入控件的节点
	 * @return {Void}      
	 */
	TeamList.prototype.inject = function(node){
		var injectNode = util.getNode(node);
	    injectNode.innerHTML = "";
	    injectNode.appendChild(this._body);
	};

	/**
	 * 更新视图
	 * @param  {Object} data 
	 * @return {Void}   
	 */
	TeamList.prototype.update = function(data){
		var tmp1 = '<div class="panel_team"><div class="panel_team-title">讨论组</div><ul class="j-normalTeam">',
	        tmp2 = '<div class=" panel_team"><div class="panel_team-title">高级群</div><ul class="j-advanceTeam">',
	        flag1 = false,
	        flag2 = false,
	        html = '',
	        info,
	        teams = data.teams;
	        if (teams && teams.length > 0) {
	            for (var i = 0, l = teams.length; i < l; ++i) {
					info = this.provider(teams[i],"team");
	                if (info.type === 'normal') {
	                    flag1 = true;
	                    tmp1 += ['<li class="panel_item '+(info.crtSession===info.target?'active':'')+'" data-gtype="normal" data-type="team" data-account="' + info.teamId + '">',
	                                '<div class="panel_avatar"><img class="panel_image" src="'+info.avatar+'"/></div>',
	                                '<div class="panel_text">',
	                                    '<p class="panel_single-row">'+info.nick+'</p>',
	                                '</div>',
	                            '</li>'].join("");
	                } else if (info.type === 'advanced') {
	                    flag2 = true;
	                    tmp2 += ['<li class="panel_item '+(info.crtSession===info.target?'active':'')+'" data-gtype="advanced" data-type="team" data-account="' + info.teamId + '">',
	                                '<div class="panel_avatar"><img class="panel_image" src="'+info.avatar+'"/></div>',
	                                '<div class="panel_text">',
	                                    '<p class="panel_single-row">'+info.nick+'</p>',
	                                '</div>',
	                            '</li>'].join("");
	                }
	            }
	            tmp1 += '</ul></div>';
	            tmp2 += '</ul></div>';
	            if (flag1 && flag2) {
	                html = tmp2 + tmp1;
	            } else if (flag1 && !flag2) {
	                html = tmp1;
	            } else if (!flag1 && flag2) {
	                html = tmp2;
	            } else {
	                html = '<p>暂时还没有群哦</p>';
	            }
	        } else {
	            html = '<p>暂时还没有群哦</p>';
	        }
		this._body.innerHTML = html;
	};

	/**
	 * 控件销毁
	 * @return {void} 
	 */
	TeamList.prototype.destory = function(){
		//预留
	};




	module.exports = TeamList;

/***/ }
/******/ ])
});
;