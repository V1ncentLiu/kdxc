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
			homePageVM.loginClientForm.bindPhoneType = clientInfoObj.bindType;
			homePageVM.loginClientForm.cno = clientInfoObj.cno;
			homePageVM.enterpriseId = clientInfoObj.enterpriseId;
			homePageVM.token = clientInfoObj.token;
			homePageVM.loginTrClient();
		}else if(loginClientType == "qimo"){
			homePageVM.loginClientForm.clientType = clientInfoObj.clientType;
			homePageVM.loginClientForm.loginClient = clientInfoObj.loginClient;
			homePageVM.loginClientForm.bindPhoneType = clientInfoObj.bindType;
			// 新增呼叫方式
			homePageVM.loginClientForm.callPhoneType = clientInfoObj.callType;
			// 新增绑定手机号
			if(clientInfoObj.bindPhone){
				homePageVM.loginClientForm.callPhoneType = clientInfoObj.bindPhone;
			}else{
				homePageVM.loginClientForm.callPhoneType="";
			}			
            homePageVM.loginQimoClient();
		}else if(loginClientType == "heli"){
			homePageVM.loginClientForm.clientType = clientInfoObj.clientType;
			homePageVM.loginClientForm.cno = clientInfoObj.clientNo;
			homePageVM.loginClientForm.bindPhoneType = clientInfoObj.bindType;
            homePageVM.loginHeliClient();
		}else if(loginClientType=="ketian"){
			homePageVM.loginClientForm.clientType = clientInfoObj.clientType;
			homePageVM.loginClientForm.loginClient = clientInfoObj.loginClient;
			homePageVM.loginClientForm.bindType = clientInfoObj.bindPhoneType;
			homePageVM.loginKeTianClient();
		}else if(loginClientType=="ronglian"){
      homePageVM.loginClientForm.clientType = clientInfoObj.clientType;
      homePageVM.loginClientForm.loginClient = clientInfoObj.loginClient;
      homePageVM.acountType = clientInfoObj.acountType;
      homePageVM.loginRongLianClient();
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

	var callSource1 = callSource;
	if(callSource==3){//话务和电销用一样的逻辑
		callSource=2;
	}
	stopSound();//停止播放录音
	clearTimer();//清除定时器
	if(!homePageVM.isQimoClient && !homePageVM.isTrClient && !homePageVM.isHeliClient && !homePageVM.isKeTianClient && !homePageVM.isRongLianClient){
		   homePageVM.$message({message:"请登录呼叫中心",type:'warning'});
		   return ;
 	}
 	
	if(!/^[0-9]*$/.test(outboundInputPhone)){
			homePageVM.$message({message:"只可以输入数字,不超过11位",type:'warning'});
			return ;
	}
 	
 	sessionStorage.setItem("callSource",callSource);//1:表示 首页头部外呼 2：表示 电销管理外呼
	// 记录拨打时间
	recodeCallTime(callSource1,clueId)

 	if(homePageVM.isTrClient){//天润呼叫
 		var bindType = homePageVM.loginClientForm.bindPhoneType;
 		if(bindType==2){//abx外呼
 			axbOutboundCall(outboundInputPhone,callSource,clueId,callback);
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
 		param.bindType = homePageVM.loginClientForm.bindPhoneType;
 		homePageVM.$message({message:"外呼中",type:'success'});
 		 axios.post('/client/client/qimoOutboundCall',param)
          .then(function (response) {
              var data =  response.data;
              if(data.code=='0'){
             	  var resData = data.data;
             	  if(resData.Succeed){
             		  if(callSource==1){
             			 homePageVM.dialogOutboundVisible =true;
             			 $("#outboundCallTime").html("");
    					 $('#outboundPhoneLocaleArea').html("");
    				 	 intervalTimer("outboundCallTime",10,2);//10分钟后红色字体显示
    					 getPhoneLocale(outboundInputPhone,callSource);
             		  }else if(callSource==2) {
             			 homePageVM.tmOutboundCallDialogVisible =true;
    					 $("#tmOutboundCallTime").html("");
    					 $('#tmOutboundPhoneLocaleArea').html("");
    					 intervalTimer("tmOutboundCallTime",10,2);
    					 //查询手机号归属地
    					 getPhoneLocale(outboundInputPhone,callSource);
             		  }
             	
         		   
         		    if (typeof callback === 'function') {
 		               callback();
 		             }
      				
             	  }else{
             	  	console.error(data);
             		  clearTimer();//清除定时器
					  var  qimoResMsg = resData.Message;
					  if(qimoResMsg.indexOf("404") != -1){
						  homePageVM.$message({message:"呼叫失败，绑定类型错误",type:'error'});
					  }else{
						  homePageVM.$message({message:"外呼失败【"+resData.Message+"】",type:'error'});
					  }
             	  }
              }else{
				  console.error(data);
            	   clearTimer();//清除定时器
             		homePageVM.$message({message:"外呼失败【"+data.msg+"】",type:'error'});
              }
          })
          .catch(function (error) {
             console.log(error);
          })
          .then(function () {
            // always executed
          });
 	}else if(homePageVM.isHeliClient){
 		heliClientOutbound(outboundInputPhone,callSource,clueId,callback);
 	}else if(homePageVM.isKeTianClient){
 		ketianClientOutbound(outboundInputPhone,callSource,clueId,callback);
	}else if(homePageVM.isRongLianClient){
 		var param = {};
 		param.customerPhone = outboundInputPhone;
 		if(clueId){
 			param.clueId = clueId;
 		}
 		param.accountType = homePageVM.accountType;
 		homePageVM.$message({message:"外呼中",type:'success'});
 		 axios.post('/client/ronglianClient/outbound',param)
          .then(function (response) {
              var data =  response.data;
              if(data.code=='0'){
             	  var resData = data.data;
             		  if(callSource==1){
             			 homePageVM.dialogOutboundVisible =true;
             			 $("#outboundCallTime").html("");
    					 $('#outboundPhoneLocaleArea').html("");
    				 	 intervalTimer("outboundCallTime",10,2);//10分钟后红色字体显示
    					 getPhoneLocale(outboundInputPhone,callSource);
             		  }else if(callSource==2) {
             			 homePageVM.tmOutboundCallDialogVisible =true;
    					 $("#tmOutboundCallTime").html("");
    					 $('#tmOutboundPhoneLocaleArea').html("");
    					 intervalTimer("tmOutboundCallTime",10,2);
    					 //查询手机号归属地
    					 getPhoneLocale(outboundInputPhone,callSource);
             		  }
             	
         		   
         		    if (typeof callback === 'function') {
 		               callback();
 		             }
              }else{
				  console.error(data);
            	   clearTimer();//清除定时器
             		homePageVM.$message({message:"外呼失败【"+data.msg+"】",type:'error'});
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

function recodeCallTime(callSource,clueId){
	 var param = {};
	 param.callSource = callSource;
	 param.clueId = clueId;
	 axios.post('/call/callRecord/recodeCallTime',param).then(function (response) {
		 console.log("拨打时间记录");
	 }).catch(function (error) {
		 console.log(error);
	 }).then(function () {
 	 });
}


//abx外呼
function axbOutboundCall(outboundInputPhone,callSource,clueId,callback){
	console.log("axb outbound call");
	homePageVM.$message({message:"外呼中",type:'success'});
	 if(callSource==1){
	     homePageVM.dialogOutboundVisible =true;
		 $('#outboundPhoneLocaleArea').html("");
		 getPhoneLocale(outboundInputPhone,callSource);
	  }else if(callSource==2) {
		 homePageVM.tmOutboundCallDialogVisible =true;
		 $('#tmOutboundPhoneLocaleArea').html("");
		 //查询手机号归属地
		 getPhoneLocale(outboundInputPhone,callSource);
	}
	
	var axbParam = {};
	axbParam.clueId = clueId;
	axbParam.customerPhone = outboundInputPhone;
	axbParam.accountType = homePageVM.accountType;
	  axios.post('/client/client/axbOutCall',axbParam)
      .then(function (response) {
          var data =  response.data;
          if(data.code=='0'){
     		  //10分钟后红色字体显示
     		 // intervalTimer("outboundCallTime",1,2);
     		  
     		   if (typeof callback === 'function') {
 	            callback();
     		   }
     		   
          }else{
        	  clearTimer();//清除定时器
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
		var orgId =homePageVM.user.orgId;
		userField.orgId = orgId;
		userField.accountType = homePageVM.accountType;
		param.userField=userField;
	
		
		TOOLBAR.previewOutcall(param,function(token){
			if(token.code=='0'){
				homePageVM.$message({message:"外呼中",type:'success'});
				if(callSource==1){
					//清空 显示时间
					//$("#outboundCallStartTime").html("");
					$('#outboundCallTime').html("");
					$("#outboundPhoneLocaleArea").html();
					//查询手机号归属地
					getPhoneLocale(outboundInputPhone,callSource);
				}else if(callSource==2){//电销页面外呼
					homePageVM.tmOutboundCallDialogVisible =true;
					$("#tmOutboundCallTime").html("");
					$('#tmOutboundPhoneLocaleArea').html("");
					//查询手机号归属地
					getPhoneLocale(outboundInputPhone,callSource);
				}
				if (typeof callback === 'function') {
		            callback();
		        }
				
			}else{
				console.error(token);
				homePageVM.$message({message:"外呼失败:"+token.msg,type:'error'});
			}
		});
}
/**
 * 查询手机号归属地
 * @param phone 客户电话
 * @param callSource 
 * @returns
 */
function getPhoneLocale(phone,callSource){
	 var param={phone:phone};
	 axios.post('/call/callRecord/queryPhoneLocale',param)
     .then(function (response) {
         var data =  response.data;
         if(data.code=='0'){
    		 var resData = data.data;
    		 if(resData){
    			 var area = resData.area;
    			 if(callSource==1){
    				 $('#outboundPhoneLocaleArea').html(area);
        		 }else if(callSource==2){
        			 $('#tmOutboundPhoneLocaleArea').html(area);
        		 } 
    		 }else{
    			 console.error(resData);
    			 homePageVM.$message({message:"查询手机归属地 "+resData.description,type:'error'});
    		 }
    		 
         }else{
        	 console.error("查询手机号归属地：%o",response);
        	 homePageVM.$message({message:"查询手机归属地  "+data.msg,type:'error'});
         }
     })
     .catch(function (error) {
        console.log(error);
     })
     .then(function () {
     });
}
//合力坐席外呼
function heliClientOutbound(outboundInputPhone,callSource,clueId,callback){
	homePageVM.$message({message:"外呼中",type:'success'});
	 if(callSource==1){
	     homePageVM.dialogOutboundVisible =true;
		 $('#outboundPhoneLocaleArea').html("");
		 getPhoneLocale(outboundInputPhone,callSource);
	  }else if(callSource==2) {
		 homePageVM.tmOutboundCallDialogVisible =true;
		 $('#tmOutboundPhoneLocaleArea').html("");
		 //查询手机号归属地
		 getPhoneLocale(outboundInputPhone,callSource);
	}
	 

		var axbParam = {};
		axbParam.clueId = clueId;
		axbParam.customerPhone = outboundInputPhone;
		axbParam.accountType = homePageVM.accountType;
		  axios.post('/client/heliClient/outbound',axbParam)
	      .then(function (response) {
	          var data =  response.data;
	          if(data.code=='0'){
	     		  //10分钟后红色字体显示
	     		 // intervalTimer("outboundCallTime",1,2);
	     		  
	        	  if(callSource==1){
          			 homePageVM.dialogOutboundVisible =true;
          			 $("#outboundCallTime").html("");
 					 //$('#outboundPhoneLocaleArea').html("");
 				 	 intervalTimer("outboundCallTime",10,2);//10分钟后红色字体显示
 					// getPhoneLocale(outboundInputPhone,callSource);
          		  }else if(callSource==2) {
          			 homePageVM.tmOutboundCallDialogVisible =true;
 					 $("#tmOutboundCallTime").html("");
 					 //$('#tmOutboundPhoneLocaleArea').html("");
 					 intervalTimer("tmOutboundCallTime",10,2);
 					 //查询手机号归属地
 					// getPhoneLocale(outboundInputPhone,callSource);
          		  }
          	
      		   
      		    if (typeof callback === 'function') {
		             callback();
		        }	     		   
	          }else{
	        	  clearTimer();//清除定时器
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

//科天外呼
function ketianClientOutbound(outboundInputPhone,callSource,clueId,callback){
	homePageVM.$message({message:"外呼中",type:'success'});
	if(callSource==1){
		homePageVM.dialogOutboundVisible =true;
		$('#outboundPhoneLocaleArea').html("");
		getPhoneLocale(outboundInputPhone,callSource);
	}else if(callSource==2) {
		homePageVM.tmOutboundCallDialogVisible =true;
		$('#tmOutboundPhoneLocaleArea').html("");
		//查询手机号归属地
		getPhoneLocale(outboundInputPhone,callSource);
	}


	var axbParam = {};
	axbParam.loginName = homePageVM.loginClientForm.loginClient;
	axbParam.orgId = homePageVM.user.orgId;
	axbParam.accountId = homePageVM.accountId;
	axbParam.clueId = clueId;
	axbParam.customerPhone = outboundInputPhone;
	axbParam.accountType = homePageVM.accountType;
	axios.post('/client/ketianClient/outbound',axbParam)
		.then(function (response) {
			var data =  response.data;
			if(data.code=='0'){
				//10分钟后红色字体显示
				// intervalTimer("outboundCallTime",1,2);

				if(callSource==1){
					homePageVM.dialogOutboundVisible =true;
					$("#outboundCallTime").html("");
					intervalTimer("outboundCallTime",10,2);//10分钟后红色字体显示
				}else if(callSource==2) {
					homePageVM.tmOutboundCallDialogVisible =true;
					$("#tmOutboundCallTime").html("");
					intervalTimer("tmOutboundCallTime",10,2);
					//查询手机号归属地
				}

				if (typeof callback === 'function') {
					callback();
				}
			}else{
				clearTimer();//清除定时器
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