$(function(){
	documentReady();
	//坐席重登录
	var clientInfo = localStorage.getItem("clientInfo");
	if(clientInfo){
		var clientInfoObj = JSON.parse(clientInfo);
		var loginClientType = clientInfoObj.loginClientType;
		if(loginClientType=="tr"){
			homePageVM.loginClientForm.clientType = clientInfoObj.clientType;
			homePageVM.loginClientForm.bindPhone = clientInfoObj.bindTel;
			homePageVM.loginClientForm.bindType = clientInfoObj.bindType;
			homePageVM.loginClientForm.cno = clientInfoObj.cno;
			homePageVM.enterpriseId = clientInfoObj.enterpriseId;
			homePageVM.token = clientInfoObj.token;
			homePageVM.loginTrClient();
		}else if(loginClientType == "qimo"){
			clientInfo.loginClientForm.clientType = clientInfoObj.clientType;
			clientInfo.loginClientForm.loginClient = clientInfoObj.loginClient;
            clientInfo.loginClientForm.bindType = clientInfoObj.bindPhoneType;
            homePageVM.loginQimoClient();
		}
	}
	
});



/**
 * 外呼
 * @param outboundInputPhone  客户手机号
 * @param callSource  来源 1:首页外呼 2:电销管理外呼 3.话务 外呼
 * @param clueId    线索id
 * @param callback 回调函数
 * @returns
 */ 
function outboundCallPhone(outboundInputPhone,callSource,clueId,callback){
	if(callSource==3){//话务和电销用一样的逻辑
		callSource=2;
	}
	
	stopSound();//停止播放录音
	clearTimer();//清除定时器
	if(!homePageVM.isQimoClient && !homePageVM.isTrClient ){
		   homePageVM.$message({message:"请登录呼叫中心",type:'warning'});
		   return ;
 	}
 	
 	 if(!/^[0-9]*$/.test(outboundInputPhone)){
			 homePageVM.$message({message:"只可以输入数字,不超过11位",type:'warning'});
		     return ; 
 	  }
 	
 	sessionStorage.setItem("callSource",callSource);//1:表示 首页头部外呼 2：表示 电销管理外呼

 	
 	if(homePageVM.isTrClient){//天润呼叫
 		var bindType = homePageVM.loginClientForm.bindPhoneType;
 		if(bindType==2){//abx外呼
 			axbOutboundCall(outboundInputPhone,callSource,clueId);
 			return;
 		}
 		priviewOutbound(outboundInputPhone,callSource,clueId,callback);
 		
 	}else if(homePageVM.isQimoClient){//七陌呼叫
 		var param = {};
 		param.customerPhoneNumber = outboundInputPhone;
 		if(clueId){
 			param.clueId = clueId;
 		}
 		param.userId= homePageVM.accountId;
 		param.accountType = homePageVM.accountType;
 		 axios.post('/client/client/qimoOutboundCall',param)
          .then(function (response) {
              var data =  response.data;
              if(data.code=='0'){
             	  var resData = data.data;
             	  if(resData.Succeed){
             		//10分钟后红色字体显示
             		  intervalTimer("outboundCallTime",10,2);
             		  homePageVM.$message({message:"外呼中",type:'success'});
             		  if (typeof callback === 'function') {
     		            callback();
     		          }
      				
             	  }else{
               		  homePageVM.$message({message:resData.Message,type:'error'});
             	  }
              }else{
             		homePageVM.$message({message:data.msg,type:'error'});
              }
          })
          .catch(function (error) {
             console.log(error);
          })
          .then(function () {
            // always executed
          });
 	}
	
} 

//abx外呼
function axbOutboundCall(outboundInputPhone,callSource,clueId){
	console.log("axb outbound call");
	var axbParam = {};
		axbParam.clueId = clueId;
		axbParam.customerPhone = outboundInputPhone;
		axbParam.accountType = homePageVM.accountType;
		 axios.post('/client/client/axbOutCall',axbParam)
      .then(function (response) {
          var data =  response.data;
          if(data.code=='0'){
     		  //10分钟后红色字体显示
     		  intervalTimer("outboundCallTime",10,1);
     		  homePageVM.$message({message:"外呼中",type:'success'});
          }else{
         		homePageVM.$message({message:data.msg,type:'error'});
          }
      })
      .catch(function (error) {
         console.log(error);
      })
      .then(function () {
        // always executed
      });
		
}
//tian run priview call
function priviewOutbound(outboundInputPhone,callSource,clueId,callback){
    	var param = {};
	    param.tel=outboundInputPhone;
		var userField ={};
		userField.accountId=homePageVM.accountId;
		if(clueId){
			userField.clueId = clueId;
		}
		userField.accountType = homePageVM.accountType;
		param.userField=userField;
	
		
		TOOLBAR.previewOutcall(param,function(token){
			if(token.code=='0'){
				homePageVM.$message({message:"外呼中",type:'success'});
				if(callSource==1){
					//清空 显示时间
					//$("#outboundCallStartTime").html("");
					$('#outboundCallTime').html("");
				}else if(callSource==2){//电销页面外呼
					homePageVM.tmOutboundCallDialogVisible =true;
					$("#tmOutboundCallTime").html("");
				}
				if (typeof callback === 'function') {
	            callback();
	        }
				
			}else{
				console.error(token);
				homePageVM.$message({message:"外呼失败",type:'error'});
			}
		});
}



/* var myCallRecordVm = new Vue({
    el: '#myCallRecordVm',
    data: {
    	isLogin:false,
    	callTitle:'呼叫中心'
    },
    methods:{
    	
    }, 
    created(){
        
    },


}) */