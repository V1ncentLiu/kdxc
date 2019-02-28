$(function(){
	documentReady();
});



/**
 * 外呼
 * @param outboundInputPhone  客户手机号
 * @param callSource  来源 1:首页外呼 2:电销管理外呼
 * @param clueId    线索id
 * @param callback 回调函数
 * @returns
 *//*
function outboundCallPhone(outboundInputPhone,callSource,clueId,callback){
	stopSound();//停止播放录音
	if(!homePageVM.isQimoClient && !homePageVM.isTrClient ){
		   homePageVM.$message({message:"请登录呼叫中心",type:'warning'});
		   return ;
 	}
 	
 	 if(!/^[0-9]*$/.test(outboundInputPhone)){
			 homePageVM.$message({message:"只可以输入数字,不超过11位",type:'warning'});
		     return ; 
 	  }
 	
 	sessionStorage.setItem("callSource",callSource);//1:表示 首页头部外呼 2：表示 电销管理外呼

 	var param = {};
 	if(homePageVM.isTrClient){//天润呼叫
 		param.tel=outboundInputPhone;
 		var userField ={};
 		userField.accountId=homePageVM.accountId;
 		if(clueId){
 			userField.clueId = clueId;
 		}
 		param.userField=userField;
 	
 		
 		TOOLBAR.previewOutcall(param,function(token){
 			if(token.code=='0'){
 				homePageVM.$message({message:"外呼中",type:'success'});
 				clearTimer();//清除定时器
 				if(callSource==1){
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
 	}else if(homePageVM.isQimoClient){//七陌呼叫
 		param.customerPhoneNumber = outboundInputPhone;
 		if(clueId){
 			param.clueId = clueId;
 		}
 		param.userId= homePageVM.accountId;
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
	
}*/


/*var myCallRecordVm = new Vue({
    el: '#myCallRecordVm',
    data: {
    	isLogin:false,
    	callTitle:'呼叫中心'
    },
    methods:{
    	
    }, 
    created(){
        
    },





})*/