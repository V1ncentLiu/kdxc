/**
 * Created by jiashiran on 2017/2/9.
 */

function initlogin(token) {//登录
    var _toolbar = $("#toolbar").contents();
    _toolbar.find("#_cno").html(token.cno)
    _toolbar.find("#_bindTel").html(token.bindTel)
    console.log("initlogin token",token)
    _toolbar.find("#_statusInfo").html("空闲")
    __bindTel = parent.getCookie("bindType");
    //console.log("bindType:",__bindTel)
    if(__bindTel == 2 || __bindTel == 3){
        if(token.extenState == "Reachable"){
            _toolbar.find("#_extenState").html("分机状态：可达").css('color', '#45a231');
        }else if(token.extenState == "Unreachable"){
            _toolbar.find("#_extenState").html("分机状态：不可达").css('color', '#FF0000');
        } else {
       	    _toolbar.find("#_extenState").html("分机状态：未知");
	    }
    }
    _toolbar.find("#infos").show()
    _toolbar.find("#yczl").hide();

}

function barClick(id) {
    toggle(id)
}

function init(id) {
   /* i = id.indexOf("-")
    if(i > 0){
        //console.log(id)
        if(version == "A"){
            $("#"+id).show()
        }
        id = id.substring(i+1,id.length)
        $("#"+id).hide()
    }else{
        //console.log(id)
        $("#"+id).show()
        $("#no-"+id).hide()
    }*/
}

function toggle(id) {
    i = id.indexOf("-")
    if(i > 0){
        id = id.substring(i+1,id.length)
    }
    //console.log(id)
    $("#"+id).toggle()
    if(version == "A"){
        $("#no-"+id).toggle()
    }else {
        $("#no-"+id).hide()
    }
    triggerEvent(id)
}

//点击事件处理
function triggerEvent(id) {
    switch(id){
        case "busy":{//点击置忙触发事件
            var params = {};
            params.pauseType = 1;
            params.pauseDescription = "置忙";
            TOOLBAR.pause(params);
            break;
        }
        case "little_rest":{//小休
            var params = {};
            params.pauseType = 1;
            params.pauseDescription = "小休";
            TOOLBAR.pause(params);
            break;
        }
        case "idle":{//置闲
            TOOLBAR.unpause();
            break;
        }
        case "have_meals":{//用餐
            var params = {};
            params.pauseType = 1;
            params.pauseDescription = "用餐";
            TOOLBAR.pause(params);
            break;
        }
        case "train":{//培训
            var params = {};
            params.pauseType = 1;
            params.pauseDescription = "培训";
            TOOLBAR.pause(params);
            break;
        }
        case "rest":{//休息
            var params = {};
            params.pauseType = 1;
            params.pauseDescription = "休息";
            TOOLBAR.pause(params);
            break;
        }
        case "calling":{//呼叫
            parent.showDialog(1);
            return;
        }
        case "keep":{//保持
            TOOLBAR.hold();
            break;
        }
        case "keep_back":{//保持接回
            TOOLBAR.unhold();
            break;
        }
        case "consulting":{//咨询
            parent.showDialog(2);
            break;
        }
        case "transfer":{//转接
            parent.showDialog(3);
            break;
        }
        case "consulting_back":{//咨询接回
            TOOLBAR.unconsult();
            callingState()//咨询接回 回到通话状态
            break;
        }
        case "three_party_consultation":{//咨询三方
            TOOLBAR.consultThreeway();
            break;
        }
        case "satisfaction":{//满意度
            TOOLBAR.investigation();
            break;
        }
        case "answer":{//接听
            TOOLBAR.sipLink();
            break;
        }
        case "refuse":{//拒接
            TOOLBAR.refuse();
            //TOOLBAR.previewOutcallCancel();//外呼取消
            break;
        }
        case "hangUp":{//挂断
            TOOLBAR.unlink();
            break;
        }
        case "mute":{//静音
            var params = {};
            params.direction = 'in';
            TOOLBAR.mute(params);
            init("no-mute_button");
            init("cancel_mute_button");
            break;
        }
        case "cancel_mute":{//取消静音
            var params = {};
            params.direction = 'in';
            TOOLBAR.mute(params);
            init("mute_button");
            init("no-cancel_mute_button");
            break;
        }
    }
}

//对应状态
function bellToRing(type,token) { //响铃
    switch (type){
        case 1:{//呼入座席响铃

            break;
        }
        case 2:{//外呼座席响铃
            //第三方代码
            top.CTI_ID = token.uniqueId;//获取录音编号

            break;
        }
        case 3:{//外呼客户响铃

            break;
        }
        case 4:{//呼叫中

            break;
        }
    }
    ///console.log("响铃,type:" + type)
    init('no-idle_button')
    init('no-busy_button')
    init('no-little_rest_button')
    init('no-have_meals_button')
    init('no-train_button')
    init('no-little_rest_button')
    init('no-calling_button')
    init('no-keep_button')
    init('no-keep_back_button')
    init('no-consulting_button')
    init('no-transfer_button')
    init('no-consulting_back_button')
    init('no-three_party_consultation_button')
    init('no-satisfaction_button')
    init('answer_button')
    init('refuse_button')
    init('no-hangUp_button')
    timer()
}

function calling(type,token) {//通话状态
    switch (type){
        case 1:{//外呼座席接通，呼叫客户中
            outboundState()
            return
        }
        case 2:{//呼入座席接听

        }
        case 3:{//外呼客户接听 客户和座席通话
            callingTrState(token);
            return
        }
        case 4:{ //保持开始
            hold()
            return
        }
        case 5:{ //保持结束
            callingState()
            return
        }
        case 6:{ //咨询开始
            //consultingState()
            return
        }
        case 7:{ //咨询成功
            consultingState()
            return
        }
        case 8:{ //被咨询转接或转移的通话

        }
        case 9:{ //busyTransfer

        }
        case 10:{ //consultThreeway
            three_party_consultation_buttonState()
            return
        }
        default:{
            console.log("no type")
            return
        }
    }
    timer()
}

//天润 接通
function callingTrState(token){
	
	var callSource = sessionStorage.getItem("callSource");
	if(callSource==1){//首页头部外呼
		$("#outboundCallStartTime").val(token.stateStartTime);
		//10分钟后红色字体显示
		intervalTimer("outboundCallTime",10,2);
	}else if(callSource==2){
		$("#tmOutboundCallStartTime").val(token.stateStartTime);
		intervalTimer("tmOutboundCallTime",10,2);
	}
	
	
}

function idleState() {//置闲状态
    //console.log("置闲")
    init('no-idle_button')
    init('busy_button')
    init('little_rest_button')
    init('little_rest_button')
    init('have_meals_button')
    init('train_button')
    init('little_rest_button')
    init('calling_button')
    init('no-keep_button')
    init('no-keep_back_button')
    init('no-consulting_button')
    init('no-transfer_button')
    init('no-consulting_back_button')
    init('no-three_party_consultation_button')
    init('no-satisfaction_button')
    init('no-answer_button')
    init('no-refuse_button')
    init('no-hangUp_button')
    init("no-mute_button");
    init("no-cancel_mute_button");
    timer()
    $("#_phone").html("");
    $("#nologin").hide();
    $("#prolongWrapup").hide();
    check = true;
}

function busyState(token) {//置忙状态
    init('idle_button')
    init('busy_button')
    init('little_rest_button')
    init('little_rest_button')
    init('have_meals_button')
    init('train_button')
    init('little_rest_button')
    init('calling_button')
    init('no-keep_button')
    init('no-keep_back_button')
    init('no-consulting_button')
    init('no-transfer_button')
    init('no-consulting_back_button')
    init('no-three_party_consultation_button')
    init('no-satisfaction_button')
    init('no-answer_button')
    init('no-refuse_button')
    init('no-hangUp_button')
    //置忙、小休、用餐、培训、休息
    //console.log(token.pauseDescription)
    if(token.pauseDescription=="小休"){
        init('no-little_rest_button')
        $('#status').text("小休")
        $("#_statusInfo").html("小休")
    }else if(token.pauseDescription=="用餐"){
        init('no-have_meals_button')
        $('#status').text("用餐");
        $("#_statusInfo").html("用餐")
    }else if(token.pauseDescription=="置忙" || token.pauseDescription == "管理置忙"){
        init('no-busy_button')
        $('#status').text("置忙");
        $("#_statusInfo").html("置忙")
    }else if(token.pauseDescription=="培训"){
        init('no-train_button')
        $('#status').text("培训");
        $("#_statusInfo").html("培训")
    }else if(token.pauseDescription=="休息"){
        init('no-little_rest_button')
        $('#status').text("休息");
        $("#_statusInfo").html("休息")
    }
    timer()
    $("#prolongWrapup").hide();
    check = true;
}

function outboundState() {//外呼状态
    init('no-idle_button')
    init('no-busy_button')
    init('no-little_rest_button')
    init('no-have_meals_button')
    init('no-train_button')
    init('no-little_rest_button')
    init('no-calling_button')
    init('no-keep_button')
    init('no-keep_back_button')
    init('no-consulting_button')
    init('no-transfer_button')
    init('no-consulting_back_button')
    init('no-three_party_consultation_button')
    init('no-satisfaction_button')
    init('answer_button')
    init('refuse_button')
    init('no-hangUp_button')
    timer()
}

function hold() {//保持开始
    init('no-idle_button')
    init('no-busy_button')
    init('no-little_rest_button')
    init('no-have_meals_button')
    init('no-train_button')
    init('no-little_rest_button')
    init('no-calling_button')
    init('no-keep_button')
    init('keep_back_button')
    init('no-consulting_button')
    init('no-transfer_button')
    init('no-consulting_back_button')
    init('no-three_party_consultation_button')
    init('no-satisfaction_button')
    init('no-answer_button')
    init('no-refuse_button')
    init('no-hangUp_button')
    init("no-mute_button");
    init("no-cancel_mute_button");
    timer()
}

function callingState() {//通话中
    init('no-idle_button')
    init('no-busy_button')
    init('no-little_rest_button')
    init('no-have_meals_button')
    init('no-train_button')
    init('no-little_rest_button')
    init('no-calling_button')
    init('keep_button')
    init('no-keep_back_button')
    init('consulting_button')
    init('transfer_button')
    init('no-consulting_back_button')
    init('no-three_party_consultation_button')
    init('satisfaction_button')
    init('no-answer_button')
    init('no-refuse_button')
    init('hangUp_button')
    init("mute_button")
    timer()
}

function consultingState(){//咨询中
    init('no-idle_button')
    init('no-busy_button')
    init('no-little_rest_button')
    init('no-have_meals_button')
    init('no-train_button')
    init('no-little_rest_button')
    init('no-calling_button')
    init('no-keep_button')
    init('no-keep_back_button')
    init('no-consulting_button')
    init('no-transfer_button')
    init("consulting_back_button")
    init('three_party_consultation_button')
    init('no-satisfaction_button')
    init('no-answer_button')
    init('no-refuse_button')
    init('no-hangUp_button')
    timer()
}

function three_party_consultation_buttonState() {//咨询三方
    init('no-idle_button')
    init('no-busy_button')
    init('no-little_rest_button')
    init('no-have_meals_button')
    init('no-train_button')
    init('no-little_rest_button')
    init('no-calling_button')
    init('no-keep_button')
    init('no-keep_back_button')
    init('no-consulting_button')
    init('no-transfer_button')
    init('no-consulting_back_button')
    init('no-three_party_consultation_button')
    init('no-satisfaction_button')
    init('no-answer_button')
    init('no-refuse_button')
    init('hangUp_button')
    timer()
}

function arrangementState(token) {//整理
	var callSource = sessionStorage.getItem("callSource");
	if(callSource==1){//首页头部外呼
		/*var startTime = $("#outboundCallStartTime").val();
		var endTime = token.stateStartTime;
		var talkTime = fomatSecondsToString(endTime-Number(startTime));
		
		$('#outboundCallTime').html(talkTime);
		$('#outboundCallTimeDiv').show();*/
		clearTimer();//清除定时器
		
	}else if(callSource==2){
		clearTimer();//清除定时器
	}
	
}

function fomatSecondsToString(s){
	var t="";
	var hour = Math.floor(s/3600);
	 var min = Math.floor(s/60) % 60;
     var sec = s % 60;
    if(hour<10){
    	t+="0";
    }
     t+=hour+"小时"
     if(min < 10){
     	t += "0";
     }
     t += min + "分";
     if(sec < 10){
     	t += "0";
    }
     t += sec + "秒";
     return t;
}

function prolongWrapup() {//置忙延时
    TOOLBAR.prolongWrapup();
    $("#prolongWrapup").hide();
    setTimeout("check = true;",30000);
}

function unlogin() {
    init('no-idle_button')
    init('no-busy_button')
    init('no-little_rest_button')
    init('no-have_meals_button')
    init('no-train_button')
    init('no-little_rest_button')
    init('no-calling_button')
    init('no-keep_button')
    init('no-keep_back_button')
    init('no-consulting_button')
    init('no-transfer_button')
    init('no-consulting_back_button')
    init('no-three_party_consultation_button')
    init('no-satisfaction_button')
    init('no-answer_button')
    init('no-refuse_button')
    init('no-hangUp_button')
    init("no-mute_button");
    init("no-cancel_mute_button");
    $("#infos").hide();
    $("#nologin").show();
}

//计时器
var timerInterval = null;
var check = true;
function timer(zl){
    $("#timer").html("00:00:00")
    var reg = /^\d$/, sleep = 1000, sum = 0;
    if(timerInterval != null){
        clearInterval(timerInterval);
    }
    timerInterval = setInterval(function () {
            sum++;
            var d = new Date("1111/1/1,00:00:00");
            d.setSeconds(sum);
            var h = d.getHours();
            h = reg.test(h) ? "0" + h + ":" : h + ":"
            var m = d.getMinutes();
            m = reg.test(m) ? "0" + m + ":" : m + ":"
            var s = d.getSeconds();
            s = reg.test(s) ? "0" + s : s;
            if(zl != undefined){//整理计时
                if(check && 30-s > 0 && 30-s <= 10){
                    console.log("延长整理");
                    $("#prolongWrapup").show();
                    check = false;
                    //zl = undefined;
                }
            }
            $("#timer").html(h + m + s)
        }, sleep);
}

//呼叫动作开始
function outbound() {//外呼
    var num = $(window.parent.document).contents().find("#phone_num").val()
    var params = {};
    if(num == ''  || num.length < 5){
        alert("请输入正确号码！")
        return;
    }
    params.tel = num;
    params.callType = '3'; //3点击外呼
    parent.dialogHide(1);
    TOOLBAR.previewOutcall(params);
    timer()
    $("#_phone").html("通话号码："+num);
}

function consultation() {//咨询
    var number = $(window.parent.document).contents().find("#phone_num").val()
    var type =   $(window.parent.document).contents().find("#consultInput").val()
    if(number == '' || number.length < 5){
        alert("请输入正确号码！")
        return;
    }
    var params = {};
    params.consultObject = number;
    params.objectType = type;
    parent.dialogHide(2);
    TOOLBAR.consult(params);
    timer()
    $("#_phone").html("通话号码："+number);
}

function transfer() {//转接
    var number = $(window.parent.document).contents().find("#phone_num").val();
    var type =   $(window.parent.document).contents().find("#consultInput").val();
    console.log('number=',number,'type=',type);
    if(number == ''){
        alert("请输入正确号码！");
        return;
    }
    var object = {};
    if (type == '3') {
    	var ivrid = $(window.parent.document).contents().find("#ivrRootInput").val();
    	var ivrPath = $(window.parent.document).contents().find("#ivrChildInput").val();
    	if (ivrid == 'pleaseSelect' || ivrPath == 'pleaseSelect') {
    		alert("请选择IVR列表！")
            return;
    	}
    	object.transferObject = ivrid + "," + ivrPath;
        object.objectType = type;
    }
    else {
    	object.transferObject = number;
        object.objectType = type;
    }
    
    parent.dialogHide(3);
    TOOLBAR.transfer(object);
    timer()
    $("#_phone").html("通话号码："+ number);
}

//获取cookie
function getCookie(cname) {
 var name = cname + "=";
 var ca = document.cookie.split(';');
 for(var i=0; i<ca.length; i++) {
  var c = ca[i];
  while (c.charAt(0)==' ') c = c.substring(1);
  if (c.indexOf(name) != -1) return c.substring(name.length, c.length);
 }
 return "";
}




