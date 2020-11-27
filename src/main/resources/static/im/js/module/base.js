'use strict';
var YX = function(accid) {
  this.accid = accid;
  this.initModule();
  this.cache = new Cache();
  this.mysdk = new SDKBridge(this, this.cache);
  if (window.nim) {
    this.myNetcall = new NetcallBridge(this);
  }
  this.firstLoadSysMsg = true;
  this.totalUnread = 0;
};
YX.fn = YX.prototype;

YX.fn.initModule = function() {
  this.initBase();
  this.message();
  this.notification();
  this.personCard();
  this.sysMsg();
  this.session();
  this.friend();
  this.team();
  this.cloudMsg();
  this.submitCustomer()
  this.brandData()
};
YX.fn.initBase = function() {
  // 初始化节点事件
  this.$mask = $('#mask');
  //左上角信息
  this.$userPic = $('#userPic');
  this.$userName = $('#userName');
  //右边面板内容节点
  this.$rightPanel = $('#rightPanel');
  this.$chatTitle = $('#chatTitle');
  this.$submitCustomer = $('#submitCustomer');
  this.$chatContent = $('#chatContent');
  this.$nickName = $('#nickName')
  //切换面板
  this.$switchPanel = $('#switchPanel');
  this.$switchPanel.on('click', 'a', this.switchPanel.bind(this));
  //切换的面板
  this.$sessionsWrap = $('#sessionsWrap');
  this.$friendsWrap = $('#friendsWrap');
  this.$teamsWrap = $('#teamsWrap');
  this.$panelItems = $('#left-panel .item');
  //2面板中间的圆点
  this.$chatVernier = $('#chatVernier span');
  $('#left-panel .item .list').on('scroll', this.doPoint.bind(this));
  //登出
  this.logoutEvt();
  //多端登陆
  this.multiportEvt();
};
/**
 * 同步完成后 UI显示  本demo这里显示最近会话列表 跟消息中心 新系统通知技术计数
 * @see session.js
 */
YX.fn.initUI = function() {
  this.buildSessions();
  this.showSysMsgCount();
};
/**
 * 初始化个人信息回调 显示左上角信息
 */
YX.fn.showMe = function() {
  var user = this.cache.getUserById(userUID);
  this.$userName.text(user.nick);
  this.$userPic.attr('src', getAvatar(user.avatar)!='null'?getAvatar(user.avatar):'http://static-huiju-new.kuaidao.cn/lark/Lark20200911-112905.png');
  setCookie('nickName', user.nick);
  setCookie('avatar', user.avatar);
};
/*********************************************
 * SDK初始化结束后，拉取需要用到的用户信息，群组信息
 *********************************************/
YX.fn.initInfo = function(obj, team) {
  this.lockPerson = true;
  this.lockTeam = true;
  var array = Object.keys(obj),
    teamArray = [];
  for (var i = team.length - 1; i >= 0; i--) {
    if (!this.cache.hasTeam(team[i])) {
      teamArray.push(team[i]);
    }
  }
  if (teamArray.length > 0) {
    this.mysdk.getLocalTeams(teamArray, this.cbInitLocalTeamInfo.bind(this));
  } else {
    this.lockTeam = false;
  }
  this.mysdk.getUsers(array, this.cbInitInfo.bind(this));
};
YX.fn.cbInitInfo = function(error, data) {
  if (!error) {
    this.cache.setPersonlist(data);
    this.lockPerson = false;
    if (this.lockTeam === false) {
      this.initUI();
    }
  } else {
    alert('获取用户信息失败');
  }
};

YX.fn.cbInitLocalTeamInfo = function(err, data) {
  if (!err) {
    this.cache.addTeamMap(data.teams);
    this.lockTeam = false;
    if (this.lockPerson === false) {
      this.initUI();
    }
  } else {
    alert('获取本地群组失败');
  }
};

/*********************************
 * 点击左边面板，打开聊天框
 *********************************/
YX.fn.openChatBox = function(account, scene) {
  // 切换聊天对象取消之前的语音播放以及录音
  YX.fn.stopPlayAudio()
  YX.fn.cancelRecordAudio()
  var info;
  var infoSession
  var that = this;
  this.mysdk.setCurrSession(scene, account);
  this.crtSession = scene + '-' + account;
  this.crtSessionType = scene;
  this.crtSessionAccount = account;
  //隐藏其他窗口
  $('#teamInfoContainer') && $('#teamInfoContainer').addClass('hide');
  this.$devices && this.$devices.addClass('hide');
  this.$cloudMsgContainer && this.$cloudMsgContainer.addClass('hide');
  //退群的特殊UI
  this.$rightPanel.find('.u-chat-notice').addClass('hide');
  this.$rightPanel.find('.chat-mask').addClass('hide');
  this.$rightPanel.removeClass('hide');
  this.$messageText.val('');
  $('#showTeamMsgReceipt').hide()

  // 让netcall.js感知到打开聊天框的操作，做一些UI层的控制
  this.myNetcall && this.myNetcall.whenOpenChatBox(scene, account);
  if (this.WB) this.WB.checkSession();
  // 是否是群管理员
  this.cache.isCurSessionTeamManager = false;
  //根据帐号跟消息类型获取消息数据
  if (scene == 'p2p') {
    info = this.cache.getUserById(account);
    infoSession=this.cache.getSessions()
    if (info.account == userUID) {
      this.$nickName.text('我的手机');
      // this.$chatTitle.find('img').attr('src', 'images/myPhone.png');
    } else {
      // if (window.CONFIG.openSubscription) {
      //   console.log(info,'聊天框渲染');
      //   var multiPortStatus = this.cache.getMultiPortStatus(account);
      //   if (multiPortStatus) {
      //     // this.$nickName.text(
      //     //   this.getNick(account) + ' [' + multiPortStatus + ']'
      //     // );
      //   } else {
      //     this.$nickName.text(this.getNick(account) + ' [离线]');
      //   }
      // } else {
        var multiPortStatus = this.cache.getMultiPortStatus(account);
        
        if(multiPortStatus){
          var str=`<b></b> `+'在线'
          $('#isOnline').html(str)
          $('#isOnline b').css({
            'backgroundColor':'#4AB65C'
          })
        }else{
          var str=`<b></b> `+'离线'
          $('#isOnline').html(str)
          $('#isOnline b').css({
            'backgroundColor':'#8F97A0'
          })
        }
        // 聊天框名字
        this.$nickName.text(info.nick);
        // }
        // 聊天图片
        this.$chatTitle.find('img').attr('src', info.avatar!='null'?getAvatar(info.avatar):'https://static-huiju-new.kuaidao.cn/lark/Lark20200911-182905.png');
        // 渲染聊天框信息
        this.getUserInfo(account)
        $('#submitCustomer').css({
          'display':'block'
        })
        $('#editCustomer').css({
          'display':'none'
        })
        console.log('下面打印的是将clueId显示隐藏按钮')
        $("#submitCustomer").attr('data-imId',info.account);
        for(var i=0;i<infoSession.length;i++){
          if(infoSession[i].to==info.account&&infoSession[i].clueId){
            console.log(infoSession[i].clueId,'将clueId显示隐藏按钮');
            // 将data-imId作为标签属性
            // if(infoSession[i].clueId){
            if(infoSession[i].isSubmit==1){//1是已提交
              $('#submitCustomer').css({
                'display':'none'
              })
              $('#editCustomer').css({
                'display':'block'
              })
              $('#editCustomer2').attr({
                  'href':'/tele/clueMyCustomerInfo/customerEditInfo?clueId='+infoSession[i].clueId
              })
            }
          }else{
            var newFriendList=localStorage.getItem('friendLists')
            if(newFriendList){
              newFriendList=JSON.parse(newFriendList)
              for(var j=0;j<newFriendList.length;j++){
                if(newFriendList[j].account==account){
                  $('#submitCustomer').css({
                    'display':'none'
                  })
                  $('#editCustomer').css({
                    'display':'block'
                  })
                  $('#editCustomer2').attr({
                      'href':'/tele/clueMyCustomerInfo/customerEditInfo?clueId='+newFriendList[j].clueId
                  })
                }
              } 
            }
          }
        }
      // }
    }
    // 群资料入口隐藏
    this.$teamInfo && this.$teamInfo.addClass('hide');
  }else {
    //群聊
    info = this.cache.getTeamById(account);
    this.$teamInfo && this.$teamInfo.removeClass('hide');

    if (info) {
      if (info.avatar) {
        this.$chatTitle
          .find('img')
          .attr('src', info.avatar + '?imageView&thumbnail=80x80&quality=85');
      } else {
        this.$chatTitle.find('img').attr('src', 'images/' + info.type + '.png');
      }
      this.$nickName.text(info.name);
    } else {
      this.$rightPanel.find('.u-chat-notice').removeClass('hide');
      this.$rightPanel.find('.chat-mask').removeClass('hide');
      this.$chatTitle.find('img').attr('src', 'images/normal.png');
      this.$nickName.text(account);
    }
    this.getTeamMembers(account, function() {
      that.cache.isCurSessionTeamManager = that.cache.isTeamManager(
        userUID,
        that.crtSessionAccount
      );
    });
    this.crtSessionTeamType = info ? info.type : 'normal';
    if (this.crtSessionTeamType === 'advanced') {
      $('#showTeamMsgReceipt').show();
    }
  }
  this.doPoint();
  // 根据或取聊天记录
  this.getHistoryMsgs(scene, account);
};

YX.fn.getUserInfo = function(accountId) {
  var that=this
  console.log(accountId,'聊天面板id');
    var params = {
      id:accountId
    };
    $.ajax({
      url:'/customerInfo/customerInfoByIm',
      type: 'POST',
      data: JSON.stringify(params),
      contentType:"application/json",
      success: function(data) {
        console.log(data,'个人信息请求后端接口');
        if(data.data){
          var data=data.data
          // 新增券
          var couponInfoStr=[,
            data.coupon.couponName?'<span>'+data.coupon.couponName+'</span>':'',
            data.coupon.discountAmount?'<span>'+data.coupon.discountAmount+'元</span>|':'',
            data.coupon.expiredTime?'<span>'+data.coupon.expiredTime+'到期</span>|':'',
            ].join("");
          // var couponInfoStr='<img src=""><span>'+data.coupon.couponName+'</span><span>'+data.coupon.discountAmount+'元</span><span>'+data.coupon.expiredTime+'到期</span>';
          if(data.coupon){
            $('#couponInfo').html('<img src="images/myPhone.png}">'+couponInfoStr);
          }else{
            $('#couponInfo').html("");
          }          

          var str=[,
            data.createDateStr?'<span>注册时间：'+data.createDateStr+'</span>':'',
            data.age?'<span>'+data.age+'</span>|':'',
            data.sex?'<span>'+data.sex+'</span>|':'',
            data.brandInvestment?'<span>'+data.brandInvestment+'</span>|':'',
            data.phoneNumber?'<span>电话：'+data.phoneNumber+'</span>':'',
            ].join("");
            if(data.cusName){
              that.$nickName.text(data.cusName);
            }
        //   var str=`
        //   <span>注册时间：`+data.createDateStr+`</span>
        //   <span>`+data.age+`</span>
        //   <span>`+data.sex+`</span>
        //   <span>`+data.brandInvestment+`</span>
        //   <span> `+data.phoneNumber+`</span>
        // `
          $('#otherInformation').html(str)
          // 给提交客户弹窗赋值姓名、手机号，并清空其他内容
          // var name=data.cusName?data.cusName:'';
          var phone=data.phoneNumber?data.phoneNumber:'';  
          setTimeout(function(){
            $("#submitCustomerFormName").val(that.$nickName.text());
          },0) 
          $("#submitCustomerFormIphone").val(phone);
          $("#submitCustomerFormIphoneTwo").val("");
          $("#submitCustomerFormWechat").val("");
          $("#submitCustomerBeizhu").val("");
        
        }else{
            $('#otherInformation').html('')
            $('#couponInfo').html('')
        }
      },
      error: function() {
        
      }
    })
}



/**
 * 切换左边面板上方tab
 */
YX.fn.switchPanel = function(evt) {
  var node;
  if (evt.target.tagName.toLowerCase() === 'span') {
    node = evt.target.parentNode;
  } else {
    node = evt.target;
  }
  $('.panel_tab').removeClass('cur');
  $(node).addClass('cur');
  var type = $(node).data('type');
  this.$chatVernier.addClass('hide');
  console.log(type,'切换tab');
  $('.item[data-type="' + type + '"]')
    .removeClass('hide')
    .siblings('.item')
    .addClass('hide');
  if (type === 'sessions') {
    this.buildSessions();
  } else if (type === 'friends') {
    this.buildFriends();
  } else {
    this.buildTeams();
  }
  
};
/**
 * 导航圆点显示
 */
YX.fn.doPoint = function() {
  var $container;
  var that = this;
  if (!this.$sessionsWrap.is('.hide')) {
    $container = this.$sessionsWrap;
  } else if (!this.$friendsWrap.is('.hide')) {
    $container = this.$friendsWrap;
  } else {
    $container = this.$teamsWrap;
  }
  var $li = $container.find('.m-panel li');
  var $active = $li.map(function() {
    $(this).removeClass('active');
    if ($(this).attr('data-account') == that.crtSessionAccount) {
      $(this).addClass('active');
      return this;
    }
  });
  if ($active.length) {
    var top = $active.offset().top - $container.offset().top + 60;
    this.$chatVernier.css('top', top).removeClass('hide');
  } else {
    this.$chatVernier.addClass('hide');
  }
};
//获取好友备注名或者昵称
YX.fn.getNick = function(account) {
  // 使用util中的工具方法
  return getNick(account, this.cache);
};

/*YX.fn.getLastCanShowMsgForInfoProvider = function(lastMsg){
    if (!lastMsg) return;
    var sessionId = lastMsg.sessionId;
    var msgs = this.cache.getMsgs(sessionId);
    var msg;
    for(var i = msgs.length - 1; i >= 0; i--) {
        msg = msgs[i];
        if(msg && (!msg.attach || msg.attach.netcallType === undefined)) {
            break;
        }
    }
    return msg;
}*/
/**
 * 列表想内容提供方法（用于ui组件）
 * @param  {Object} data 数据
 * @param  {String} type 类型
 * @return {Object} info 需要呈现的数据
 */
YX.fn.infoProvider = function(data, type) {
  var info = {};
  switch (type) {
    case 'session':
      var msg = data.lastMsg;
      if (!msg) {
        return;
      }
      var scene = msg.scene;
      info.scene = scene;
      info.account = msg.target;
      info.target = msg.scene + '-' + msg.target;
      info.time = transTime2(msg.time);
      info.crtSession = this.crtSession;
      info.unread = data.unread > 99 ? '99+' : data.unread;
      info.text = buildSessionMsg(msg);
      if (scene === 'p2p') {
        //点对点
        if (msg.target === userUID) {
          info.nick = '我的手机';
          info.avatar = 'images/myPhone.png';
        } else {
          var userInfo = this.cache.getUserById(msg.target);
          info.nick = this.getNick(msg.target);
          info.avatar = getAvatar(userInfo.avatar);
        }
      } else {
        //群组
        var teamInfo = this.cache.getTeamById(msg.target);
        if (teamInfo) {
          info.nick = teamInfo.name;
          if (teamInfo.avatar) {
            info.avatar =
              teamInfo.avatar + '?imageView&thumbnail=40x40&quality=85';
          } else {
            info.avatar = 'images/' + teamInfo.type + '.png';
          }
        } else {
          info.nick = msg.target;
          info.avatar = 'images/normal.png';
        }
      }
      break;
    case 'friend':
      info.target = 'p2p-' + data.account;
      info.account = data.account;
      info.nick = this.getNick(info.account);
      info.avatar = getAvatar(data.avatar);
      info.crtSession = this.crtSession;
      break;
    case 'team':
      info.type = data.type;
      info.nick = data.name;
      info.target = 'team-' + data.teamId;
      info.teamId = data.teamId;
      if (data.avatar) {
        info.avatar = data.avatar + '?imageView&thumbnail=40x40&quality=85';
      } else {
        info.avatar =
          info.type === 'normal' ? 'images/normal.png' : 'images/advanced.png';
      }
      info.crtSession = this.crtSession;
      break;
  }
  return info;
};
/**********************************
 * 登出
 **********************************/
YX.fn.logoutEvt = function() {
  this.$logout = $('#logout');
  this.$logoutDialog = $('#logoutDialog');
  this.$logout.on('click', this.showLogoutDialog.bind(this));
  this.$logoutDialog.delegate(
    '.j-close',
    'click',
    this.hideLogoutDialog.bind(this)
  );
  this.$logoutDialog.delegate('.j-ok', 'click', this.doLogout.bind(this));
};
YX.fn.doLogout = function() {
  delCookie('uid');
  delCookie('sdktoken');
  delCookie('avatar');
  delCookie('nickName');
  window.location.href = './index.html';
};

YX.fn.showLogoutDialog = function() {
  this.$logoutDialog.removeClass('hide');
  this.$mask.removeClass('hide');
};

YX.fn.hideLogoutDialog = function() {
  this.$logoutDialog.addClass('hide');
  this.$mask.addClass('hide');
};

/**********************************************
 * 多端登录管理
 ********************************************/
YX.fn.multiportEvt = function() {
  this.$devices = $('#devices');
  // 踢人 0：移动端 1：pc端
  $('#devices .mobile').on(
    'click',
    function() {
      this.mysdk.kick(0);
    }.bind(this)
  );
  $('#devices .pc').on(
    'click',
    function() {
      this.mysdk.kick(1);
    }.bind(this)
  );
  $('#backBtn2').on('click', this.hideDevices.bind(this));
  $('.m-devices').on('click', this.showDevices.bind(this));
};
//SDK回调
(YX.fn.loginPorts = function(devices) {
  var pc, mobile;
  for (var i = devices.length - 1; i >= 0; i--) {
    if (/iOS|Android|WindowsPhone/i.test(devices[i].type)) {
      mobile = devices[i];
    } else if (/Mac|PC/i.test(devices[i].type)) {
      pc = devices[i];
    }
  }
  if ((pc && pc.online) || (mobile && mobile.online)) {
    if (pc && pc.online && (mobile && mobile.online)) {
      $('.m-devices').html('正在使用云信手机版，电脑版');
      $('#devices .pc').removeClass('hide');
      $('#devices .mobile').removeClass('hide');
      this.mysdk.mobileDeviceId = mobile.deviceId;
      this.mysdk.pcDeviceId = pc.deviceId;
    } else if (pc && pc.online) {
      $('.m-devices').html('正在使用云信电脑版');
      $('#devices .pc').removeClass('hide');
      $('#devices .mobile').addClass('hide');
      this.mysdk.mobileDeviceId = false;
      this.mysdk.pcDeviceId = pc.deviceId;
    } else {
      $('.m-devices').html('正在使用云信手机版');
      $('#devices .mobile').removeClass('hide');
      $('#devices .pc').addClass('hide');
      this.mysdk.mobileDeviceId = mobile.deviceId;
      this.mysdk.pcDeviceId = false;
    }
    $('.m-devices').removeClass('hide');
    $('#left-panel .item').height(463);
    $('#chatVernier').css({ marginTop: '41px' });
  } else {
    $('.m-devices').addClass('hide');
    $('#devices').addClass('hide');
    $('#devices .pc').addClass('hide');
    $('#devices .mobile').addClass('hide');
    this.mysdk.mobileDeviceId = false;
    this.mysdk.pcDeviceId = false;
    $('#left-panel .item').height(504);
    $('#chatVernier').css({ marginTop: '0' });
  }
}),
  /**
   * 多端登录面板UI
   */
  (YX.fn.showDevices = function() {
    this.$devices.removeClass('hide');
  });
YX.fn.hideDevices = function() {
  this.$devices.addClass('hide');
};
/**
 * 语音播放
 */
YX.fn.playAudio = function() {
  // 点击音频则停止录音
  YX.fn.stopRecordAudio()
  var node = $(this);
  if (!node.hasClass('u-audio')) {
    node = node.parent()
  }
  var btn = node && node.children('.j-play');
  var sameNode = node.hasClass('play')
  YX.fn.stopPlayAudio()
  if (sameNode) {
    return
  }
  YX.fn.playAudio.$audio = {
    node: node,
    audio: document.createElement('audio')
  };
  var $audio = YX.fn.playAudio.$audio.audio
  var $source = document.createElement('source');
  var audioType = btn.attr('data-ext');
  $source.src = btn.attr('data-src');
  $source.type = 'audio/' + (audioType === 'mp3' ? 'mpeg' : audioType);
  $audio.appendChild($source)
  $audio.play()
  node.addClass('play');
  setTimeout(function() {
    node.removeClass('play');
  }, parseInt(btn.attr('data-dur')));
  if (node.hasClass('unreadAudio') && btn.attr('data-id')) {
    nim.updateLocalMsg({
      idClient: btn.attr('data-id'),
      localCustom: '{"audio": "played"}'
    });
    node.removeClass('unreadAudio');
    $audio.onended = YX.fn.autoPlayNextUnreadAudio
  }
};
YX.fn.stopPlayAudio = function() {
  if (YX.fn.playAudio.$audio.audio) {
    YX.fn.playAudio.$audio.node.removeClass('play');
    YX.fn.playAudio.$audio.audio.pause();
    YX.fn.playAudio.$audio = {
      node: null,
      audio: null
    };
  }
};
YX.fn.autoPlayNextUnreadAudio = function() {
  var parentNode = YX.fn.playAudio.$audio.node && YX.fn.playAudio.$audio.node.parent()
  if (!parentNode) {
    return
  }
  var currentMsgNode = parentNode.parent().parent().parent();
  var nextMsgNode = currentMsgNode[0].nextElementSibling
  var $nextMsgNode
  var nextAudioNode
  while (nextMsgNode) {
    $nextMsgNode = $(nextMsgNode)
    if ($nextMsgNode.hasClass('item-you')) {
      nextAudioNode = nextMsgNode.querySelector('.u-audio')
      if (nextAudioNode) {
        break
      }
    }
    nextMsgNode = nextMsgNode.nextElementSibling
  }
  if (nextMsgNode && $(nextAudioNode).hasClass('unreadAudio')) {
    nextAudioNode.click()
  }
};
// 播放音频信息使用的 audio
YX.fn.playAudio.$audio = {
  node: null,
  audio: null
}
