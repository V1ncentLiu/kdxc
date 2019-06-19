/**
 * Created by jiashiran on 2017/2/16.
 */
function documentReady(){
    //devin unlogin()
	//移除打电话来源标识
	//sessionStorage.removeItem("callSource");
	//移除坐席登录表示 
	//sessionStorage.removeItem("loginClient");
	//移除账号
	//sessionStorage.removeItem("accountId");
	
    CTILink.setup(
        {
            webrtc: true, // 是否加载软电话，对IE浏览器无效
            debug: true // 是否打开debug
        },
        function () {
            log("加载完成");
            //注册事件处理方法
            /** 状态事件 */
            CTILink.event("status", handleState);  //当前座席状态
            CTILink.event("previewOutcallRinging", handleState);  //外呼接通
            CTILink.event("previewOutcallBridge", handleState);  //外呼接通
            CTILink.event("consultStart", handleState);  //咨询开始
            CTILink.event("consultLink", handleState);  //咨询接听
            CTILink.event("consultError", handleState);  //咨询失败
            CTILink.event("unconsult", handleState);  //咨询挂断/接回/取消
            CTILink.event("consultThreeway", handleState);  //咨询三方
            CTILink.event("consultThreewayUnlink", handleState);  //咨询三方挂断
            CTILink.event("consultTransfer", handleState);  //咨询转移
            CTILink.event("rasr",handleRasr);//实时语音识别
            /** 其他事件 */
            CTILink.event("ringing", handleRinging); //弹屏
            CTILink.event("kickout", handleKickout);  //被踢下线
            CTILink.event("sipDisconnected", handleSipDisconnected);  //软电话断线
            CTILink.event("login", handleLogin);
            CTILink.event("extenState", extenState);//分机状态
            //自动登录
            //TOOLBAR.login();
        });
}

var TOOLBAR = (function () {
    function toolbar() {
    }
    /** 动作 */
    toolbar.login = function (params, callback) { //登录
        CTILink.Agent.login(params, function(token) {
            if (token.msg == "软电话注册失败") {
                var obj = {};
                obj.logoutMode = 1;
                obj.removeBinding = '0';
                TOOLBAR.logout(obj, window.parent.cbLogout);//退出
            }
            if (typeof callback === 'function') {
                callback(token);
            }
        });
    };
    toolbar.logout = function (params, callback) { //登出
        CTILink.Agent.logout(params, function (token) {
            if (token.code == "0") {
                //typeButton.buttonDisabled();
                $("#statusImg").css({"backgroundPosition": "0px 0px"});
                $('#status').text("离线");
                clearInterval(document.getElementById('cnoTime').setIntervalCnoTimeId);
                document.getElementById('cnoTime').innerHTML = "";
                $("#infos").hide();
            }

            if (typeof callback === 'function') {
                callback(token);
            }
        });
    };
    toolbar.queueStatus = function (params, callback) { //队列状态
        CTILink.Agent.queueStatus(params, callback);
    };
    toolbar.previewOutcall = function (params, callback) { //外呼
        CTILink.Agent.previewOutcall(params, callback);
    };
    toolbar.pause = function (params, callback) { //置忙
        CTILink.Agent.pause(params, callback);
    };
    toolbar.unpause = function () { //置闲
        CTILink.Agent.unpause({});
    };
    toolbar.sipCall = function(params) {
        CTILink.Agent.sipCall(params);
    };

    /** 通话动作 */
    toolbar.previewOutcallCancel = function () { //外呼取消
        if (CTILink.Session.isSessionAlive()) {
            CTILink.Session.previewOutcallCancel();
        } else {
            log("外呼取消失败，session不存在或已经销毁");
        }
    };
    toolbar.refuse = function () { //拒接
        if (CTILink.Session.isSessionAlive()) {
            CTILink.Session.refuse();
        } else {
            log("拒接失败，session不存在或已经销毁");
        }
    };
    toolbar.unlink = function () { //挂断
        if (CTILink.Session.isSessionAlive()) {
            CTILink.Session.unlink();
        } else {
            log("挂断失败，session不存在或已经销毁");
        }
    };
    toolbar.hold = function () { //保持
        if (CTILink.Session.isSessionAlive()) {
            CTILink.Session.hold();
        } else {
            log("保持失败，session不存在或已经销毁");
        }
    };
    toolbar.unhold = function () { //保持接回
        if (CTILink.Session.isSessionAlive()) {
            CTILink.Session.unhold();
        } else {
            log("保持接回失败，session不存在或已经销毁");
        }
    };
    toolbar.investigation = function () { //满意度调查
        if (CTILink.Session.isSessionAlive()) {
            CTILink.Session.investigation();
        } else {
            log("满意度调查失败，session不存在或已经销毁");
        }
    };
    toolbar.consult = function (params) { //咨询
        if (CTILink.Session.isSessionAlive()) {
            CTILink.Session.consult(params);
        } else {
            log("咨询失败，session不存在或已经销毁");
        }
    };
    toolbar.consultCancel = function () { //咨询取消
        if (CTILink.Session.isSessionAlive()) {
            CTILink.Session.consultCancel();
        } else {
            log("咨询取消失败，session不存在或已经销毁");
        }
    };
    toolbar.unconsult = function () { //咨询接回
        if (CTILink.Session.isSessionAlive()) {
            CTILink.Session.unconsult();
        } else {
            log("咨询接回失败，session不存在或已经销毁");
        }
    };
    toolbar.consultThreeway = function () { //咨询三方
        if (CTILink.Session.isSessionAlive()) {
            CTILink.Session.consultThreeway();
        } else {
            log("咨询三方失败，session不存在或已经销毁");
        }
    };
    toolbar.consultTransfer = function () { //咨询转移
        if (CTILink.Session.isSessionAlive()) {
            CTILink.Session.consultTransfer();
        } else {
            log("咨询转移失败，session不存在或已经销毁");
        }
    };
    toolbar.transfer = function (params) { //转移
        if (CTILink.Session.isSessionAlive()) {
            CTILink.Session.transfer(params);
        } else {
            log("转移失败，session不存在或已经销毁");
        }
    };
    toolbar.interact = function (params) { //交互
        if (CTILink.Session.isSessionAlive()) {
            CTILink.Session.interact(params);
        } else {
            log("交互失败，session不存在或已经销毁");
        }
    };
    toolbar.mute = function (params) {//静音
        if (CTILink.Session.isSessionAlive()) {
            CTILink.Session.mute(params);
        } else {
            log("静音失败，session不存在或已经销毁");
        }

    }
    toolbar.getUserData = function (params) { //获取随路数据
        if (CTILink.Session.isSessionAlive()) {
            CTILink.Session.getUserData(params);
        } else {
            log("获取随路数据失败，session不存在或已经销毁");
        }
    };
    /** 监控 */
    toolbar.spy = function(params) {
        CTILink.Monitor.spy(params);
    };
    toolbar.unspy = function(params) {
        CTILink.Monitor.unspy(params);
    };
    toolbar.threeway = function(params) {
        CTILink.Monitor.threeway(params);
    };
    toolbar.unthreeway = function(params) {
        CTILink.Monitor.unthreeway(params);
    };
    toolbar.whisper = function(params) {
        CTILink.Monitor.whisper(params);
    };
    toolbar.unwhisper = function(params) {
        CTILink.Monitor.unwhisper(params);
    };
    toolbar.pickup = function(params) {
        CTILink.Monitor.pickup(params);
    };
    toolbar.barge = function(params) {
        CTILink.Monitor.barge(params);
    };
    toolbar.disconnect = function(params) {
        CTILink.Monitor.disconnect(params);
    };
    toolbar.setPause = function(params) {
        CTILink.Monitor.setPause(params);
    };
    toolbar.setUnpause = function(params) {
        CTILink.Monitor.setUnpause(params);
    };
    //软电话已中断
    toolbar.cbSipDisconnected = function (token) {
        var obj = {};
        obj.logoutMode = 1;
        obj.removeBinding = '0';
        TOOLBAR.logout(obj, window.parent.cbLogout);
        alert("软电话已中断");
    };


    toolbar.sipLink = function () { //软电话接听
        if (CTILink.SipSession.isSessionAlive()) {
            CTILink.SipSession.sipLink();
        } else {
            log("软电话接听失败，sipSession不存在或已经销毁");
        }
    };
    toolbar.sipUnlink = function () { //软电话挂断
        if (TILink.SipSession.isSessionAlive()) {
            CTILink.SipSession.sipUnlink();
        } else {
            log("软电话挂断失败，sipSession不存在或已经销毁");
        }
    };
    toolbar.sipDTMF = function (params) { //软电话dtmf
        if (CTILink.SipSession.isSessionAlive()) {
            CTILink.SipSession.sipDTMF(params);
        } else {
            log("sipDTMF失败，sipSession不存在或已经销毁");
        }
    };
    toolbar.prolongWrapup = function () {//置忙延时
        var params = {};
        params.wrapupTime= wrapupTime;
        CTILink.Agent.prolongWrapup(params)
    };
    toolbar.controlPlayback = function (params) {//置忙延时
        CTILink.Session.controlPlayback(params)
    };
    return toolbar;
})();

/*var typeButton = {
    phoneCallout: function () {//外呼
        $("#phoneCallout").show().attr("disabled", false).css({"backgroundPosition": "0px -33px"});
        $("#phoneCallout").unbind("mouseover mouseout click");
        $("#phoneCallout").bind({
            mouseover: function () {
                $(this).css({"backgroundPosition": "0px -66px"});
            },
            mouseout: function () {
                $(this).css({"backgroundPosition": "0px -33px"});
            },
            click: function () {
                var params = {};
                params.tel = $('#phoneCallText').val();
                params.callType = '3'; //3点击外呼
                TOOLBAR.previewOutcall(params);
            }
        });
    },
    phoneCallText: function () {
        $("#phoneCallText").attr("disabled", false);
        $("#phoneCallText").show();
        $("#consultInput").show();
    },
    phoneCallCancel: function () {//外呼取消
        $("#phoneCallCancel").show().attr("disabled", false).css({"backgroundPosition": "0px -33px"});
        $("#phoneCallCancel").unbind("mouseover mouseout click");
        $("#phoneCallCancel").bind({
            mouseover: function () {
                $(this).css({"backgroundPosition": "0px -66px"});
            },
            mouseout: function () {
                $(this).css({"backgroundPosition": "0px -33px"});
            },
            click: function () {
                TOOLBAR.previewOutcallCancel();//外呼取消
            }
        });
    },
    refused: function () {//拒接
        $("#refused").show().attr("disabled", false).css({"backgroundPosition": "0px -33px"});
        $("#refused").unbind("mouseover mouseout click");
        $("#refused").bind({
            mouseover: function () {
                $(this).css({"backgroundPosition": "0px -66px"});
            },
            mouseout: function () {
                $(this).css({"backgroundPosition": "0px -33px"});
            },
            click: function () {
                TOOLBAR.refuse();
            }
        });
    },
    unLink: function () {//挂断

        $("#unLink").show().attr("disabled", false).css({"backgroundPosition": "0px -33px"});
        $("#unLink").unbind("mouseover mouseout click");
        $("#unLink").bind({
            mouseover: function () {
                $(this).css({"backgroundPosition": "0px -66px"});
            },
            mouseout: function () {
                $(this).css({"backgroundPosition": "0px -33px"});
            },
            click: function () {
                TOOLBAR.unlink();
            }
        });
    },
    hold: function () {//保持
        $("#hold").show().attr("disabled", false).css({"backgroundPosition": "0px -33px"});
        $("#hold").unbind("mouseover mouseout click");
        $("#hold").bind({
            mouseover: function () {
                $(this).css({"backgroundPosition": "0px -66px"});
            },
            mouseout: function () {
                $(this).css({"backgroundPosition": "0px -33px"});
            },
            click: function () {
                TOOLBAR.hold();
            }
        });
    },
    unHold: function () {//保持接回

        $("#unHold").show().attr("disabled", false).css({"backgroundPosition": "0px -33px"});
        $("#unHold").unbind("mouseover mouseout click");
        $("#unHold").bind({
            mouseover: function () {
                $(this).css({"backgroundPosition": "0px -66px"});
            },
            mouseout: function () {
                $(this).css({"backgroundPosition": "0px -33px"});
            },
            click: function () {
                TOOLBAR.unhold();
            }
        });
    },
    investigation: function () {//满意度调查
        $("#investigation").show().attr("disabled", false).css({"backgroundPosition": "0px -33px"});
        $("#investigation").unbind("mouseover mouseout click");
        $("#investigation").bind({
            mouseover: function () {
                $(this).css({"backgroundPosition": "0px -66px"});
            },
            mouseout: function () {
                $(this).css({"backgroundPosition": "0px -33px"});
            },
            click: function () {
                TOOLBAR.investigation();
            }
        });
    },
    online: function () {//空闲
        $("#online").show().attr("disabled", false).css({"backgroundPosition": "0px -33px"});
        $("#online").unbind("mouseover mouseout click");
        $("#online").bind({
            mouseover: function () {
                $(this).css({"backgroundPosition": "0px -66px"});
            },
            mouseout: function () {
                $(this).css({"backgroundPosition": "0px -33px"});
            },
            click: function () {
                TOOLBAR.unpause();
            }
        });
    },
    pause: function () {//置忙

        $("#pause").show().attr("disabled", false).css({"backgroundPosition": "0px -33px"});
        $("#pause").unbind("mouseover mouseout click");
        $("#pause").bind({
            mouseover: function () {
                $(this).css({"backgroundPosition": "0px -66px"});
            },
            mouseout: function () {
                $(this).css({"backgroundPosition": "0px -33px"});
            },
            click: function () {
                var params = {};
                params.pauseType = 1;
                params.pauseDescription = "置忙";
                TOOLBAR.pause(params);

            }
        });
    },
    buttonDisabled: function () {//按钮恢复状态
        $("#toolbarButton input").hide();
        $("#toolbarButton input").css({"backgroundPosition": "0px 0px"});
        $("#toolbarButton input").attr("disabled", true);
        $("#toolbarButton input").unbind("mouseover mouseout click");

    },
    consult: function () {//咨询
        $("#consult").show().attr("disabled", false).css({"backgroundPosition": "0px -33px"});
        $("#consult").unbind("mouseover mouseout click");
        $("#consult").bind({
            mouseover: function () {
                $(this).css({"backgroundPosition": "0px -66px"});
            },
            mouseout: function () {
                $(this).css({"backgroundPosition": "0px -33px"});
            },
            click: function () {
                var type = $("#consultInput").val();
                var number = $("#phoneCallText").val();
                var params = {};
                params.consultObject = number;
                params.objectType = type;
                TOOLBAR.consult(params);
            }
        });
    },
    consultBack: function () {//咨询接回
        $("#consultBack").show().attr("disabled", false).css({"backgroundPosition": "0px -33px"});
        $("#consultBack").unbind("mouseover mouseout click");
        $("#consultBack").bind({
            mouseover: function () {
                $(this).css({"backgroundPosition": "0px -66px"});
            },
            mouseout: function () {
                $(this).css({"backgroundPosition": "0px -33px"});
            },
            click: function () {
                TOOLBAR.unconsult();
            }
        });
    },
    consultTransfer: function () {//咨询转接
        $("#consultTransfer").show().attr("disabled", false).css({"backgroundPosition": "0px -33px"});
        $("#consultTransfer").unbind("mouseover mouseout click");
        $("#consultTransfer").bind({
            mouseover: function () {
                $(this).css({"backgroundPosition": "0px -66px"});
            },
            mouseout: function () {
                $(this).css({"backgroundPosition": "0px -33px"});
            },
            click: function () {
                TOOLBAR.consultTransfer();
            }
        });
    },
    consultThreeway: function () {//咨询三方
        $("#consultThreeway").show().attr("disabled", false).css({"backgroundPosition": "0px -33px"});
        $("#consultThreeway").unbind("mouseover mouseout click");
        $("#consultThreeway").bind({
            mouseover: function () {
                $(this).css({"backgroundPosition": "0px -66px"});
            },
            mouseout: function () {
                $(this).css({"backgroundPosition": "0px -33px"});
            },
            click: function () {
                TOOLBAR.consultThreeway();
            }
        });
    },
    transfer: function () {//转移
        $("#transfer").show().attr("disabled", false).css({"backgroundPosition": "0px -33px"});
        $("#transfer").unbind("mouseover mouseout click");
        $("#transfer").bind({
            mouseover: function () {
                $(this).css({"backgroundPosition": "0px -66px"});
            },
            mouseout: function () {
                $(this).css({"backgroundPosition": "0px -33px"});
            },
            click: function () {
                var type = $("#consultInput").val();
                var number = $("#phoneCallText").val();
                var object = {};
                object.transferObject = number;
                object.objectType = type;
                TOOLBAR.transfer(object);
            }
        });
    },
    answer: function () {//接听 软电话功能
        if (window.parent.bindType == '3') {
            $("#answer").show().attr("disabled", false).css({"backgroundPosition": "0px -33px"});
            $("#answer").unbind("mouseover mouseout click");
            $("#answer").bind({
                mouseover: function () {
                    $(this).css({"backgroundPosition": "0px -66px"});
                },
                mouseout: function () {
                    $(this).css({"backgroundPosition": "0px -33px"});
                },
                click: function () {
                    TOOLBAR.sipLink();
                }
            });
        }

    }
}*/
function handleState(token) {
	console.log("handleState:%o",token);
    var stateShow = token.state;
    switch (token.state) {
        case '离线':
            break;
        case '失效':
            //软电话状态, 退出
            break;
        case '空闲':
           //devin idleState()
           /* typeButton.buttonDisabled();
            typeButton.phoneCallout();
            typeButton.phoneCallText();
            typeButton.pause();*/
            break;
        case '置忙':
            //console.log(token)
            busyState(token)
            /*typeButton.buttonDisabled();
            typeButton.phoneCallout();
            typeButton.phoneCallText();
            typeButton.online();*/
            break;
        case '呼叫中':
            //typeButton.buttonDisabled();
            bellToRing(4,token)
            break;
        case '响铃':
            switch(token.stateAction) {
                case 'ringingIb' : //呼入座席响铃
                   /* typeButton.buttonDisabled();
                    typeButton.answer(token);
                    typeButton.refused();*/
                    bellToRing(1,token)
                    break;
                case 'ringingAgentOb' : //外呼座席响铃
                    //typeButton.buttonDisabled();
                    ////$("#phoneCallout").hide();
                    //typeButton.phoneCallCancel();
                    //第三方代码
                    top.CTI_ID = token.uniqueId;//获取录音编号
                    bellToRing(2,token)
                    break;
                case 'ringingCustomerOb': //外呼客户响铃
                    bellToRing(3,token)
                    break;
            }
            break;
        case '通话':
            switch(token.stateAction) {
                case 'busyAgentOb' : //外呼座席接通，呼叫客户中
                   /* typeButton.buttonDisabled();
                    typeButton.unLink();*/
                   // calling(1)
                    break;
                case 'busyIb' : //呼入座席接听
                  //  calling(2)
                    break;
                case 'busyOb' : //外呼客户接听 客户和座席通话
                   /* typeButton.buttonDisabled();
                    typeButton.unLink();
                    typeButton.hold();
                    typeButton.investigation();
                    typeButton.consult();
                    typeButton.transfer();
                    $("#phoneCallText").show().attr("disabled", false);*/
                    calling(3,token)
                    console.log(token)
                    break;
                case 'hold' : //保持开始
                    /*stateShow = '保持';
                    typeButton.buttonDisabled();
                    typeButton.unLink();
                    typeButton.unHold();
                    typeButton.investigation();*/
                   // calling(4)
                    break;
                case 'unhold' : //保持结束
                    /*typeButton.buttonDisabled();
                    typeButton.unLink();
                    typeButton.hold();
                    typeButton.investigation();
                    typeButton.consult();
                    typeButton.transfer();*/
                   // calling(5)
                    break;
                case 'consultStart' : //咨询开始
                    //$("#consult").hide();
                    //typeButton.buttonDisabled();
                   // calling(6)
                    break;
                case 'consultLink' : //咨询成功
                    stateShow = '咨询';
                    /*$("#consult").hide();
                    typeButton.unLink();
                    typeButton.hold();
                    typeButton.investigation();
                    typeButton.consultBack();
                    typeButton.consultThreeway();
                    typeButton.consultTransfer();
                    typeButton.transfer();*/
                    //console.log("consultBack")
                   // calling(7)
                    break;
                case 'busyConsult' : //被咨询转接或转移的通话
                    calling(8)
                    break;
                case 'busyTransfer' :
                   // calling(9)
                    /*typeButton.buttonDisabled();
                    typeButton.unLink();
                    typeButton.hold();*/
                    break;
                case 'consultThreeway':
                    //calling(10)
                    stateShow = '咨询三方';
                    break;
                default:
                    /*typeButton.buttonDisabled();
                    typeButton.unLink();
                    typeButton.hold();
                    typeButton.investigation();
                    typeButton.consult();
                    typeButton.transfer();
                    $("#phoneCallText").show().attr("disabled", false);*/
                    //calling(3,token);
            }
            break;
        case '整理':  //整理开始（座席挂断）
            /*typeButton.buttonDisabled();
            typeButton.online();
            typeButton.pause();*/
            //console.log(token)
            arrangementState(token);
            break;
    }
  /*  //log(JSON.stringify(token)+"===================="+str);
    if (stateShow != "" && token.state != "置忙") {
        //$("#status").text(stateShow);
        $("#_statusInfo").html(stateShow)
    }
    if (stateShow == '空闲') {
        //$("#statusImg").css({"backgroundPosition": "0px -25px"});
    } else if (stateShow == '离线') {
        //$("#statusImg").css({"backgroundPosition": "0px 0px"});
        //unlogin()
    } else {
        //$("#statusImg").css({"backgroundPosition": "0px -50px"});
    }*/
}

function handleKickout (token) {
    if (token.code == "0") {
        /*typeButton.buttonDisabled();
        $("#statusImg").css({"backgroundPosition": "0px 0px"});*/
       /* $('#status').text("离线");
        clearInterval(document.getElementById('cnoTime').setIntervalCnoTimeId);
        document.getElementById('cnoTime').innerHTML = "";
        window.parent.cbKickout(token);*/
    }
}

function handleRinging() {

}

function handleSipDisconnected() {
    var obj = {};
    obj.logoutMode = 1;
    obj.removeBinding = '0';
    TOOLBAR.logout(obj, window.parent.cbLogout);//退出
}

function handleRasr(event) {
    side = event.monitorSide;
    text = event.text;
    sentenceId = event.sentenceId;
    //console.log(side,text,sentenceId)
    if(sentenceId != 0){
        if(side==1){
            $("#_rasrSeat").html("坐席侧："+text);
        }else if(side==2){
            $("#_rasrCustomer").html("客户侧："+text)
        }
    }else{//挂断
        $("#_rasrSeat").html("");
        $("#_rasrCustomer").html("");
    }
}

//软交换登录失败
function handleLogin(event) {
    console.log(event);
    if(event.msg != "软电话注册成功"){
        var obj = {};
        obj.logoutMode = 1;
        obj.removeBinding = '0';
        TOOLBAR.logout(obj, window.parent.cbLogout);//退出
    }
}

//分机状态
function extenState(event) {
    __bindTel = parent.getCookie("bindType");
    //console.log(__bindTel)
    if(__bindTel == 2){//分机登录
        if(event.state == "Reachable"){
            $("#_extenState").html("分机状态：可达").css('color', '#45a231');
        }else if(event.state == "Unreachable"){
            $("#_extenState").html("分机状态：不可达").css('color', '#FF0000');
        }
    }
}

function log (message) {
    message = (new Date())+ " | toolbarIframe | " + message;
    return typeof window !== 'undefined' && window !== null ? (_ref = window.console) != null ? _ref.log(message) : void 0 : void 0;
}
