var homePageVM=new Vue({
  	el: '#app',
  	data: function() {
  		var validatePass = (rule, value, callback) => {
            if (value !== this.modifyForm.newPassword) {
                callback(new Error('确认密码与新密码不一致，请重新输入'));
            } else {
                callback();
            }
        };
  		
	    return { 
	    	formLabelWidth:'120px',
	      	isCollapse: false,//侧导航是否展开
	      	isActive:true,
	      	dialogModifyPwdVisible:false,//修改密码dialog 是否显示
	      	dialogLogoutVisible:false,//退出登录 dialog
	      	modifyForm:{
	      		'oldPassword':'',
	      		'newPassword':'',
	      		'confirmPwd':''
	      	},
		   	items:menuList
		     	/*{ifreamUrl:'a.html',index:'1-1',name:"数据演示1"},
		     	{ifreamUrl:'b.html',index:'1-2',name:"数据演示2"}*/
		       ,
		    user:user,//用戶信息
		    defaultOpeneds:["0"],//默认打开菜单索引
		    initUrl:showMenuUrl,//默认展示页面
	        defaultActive:'0-0',//默认激活菜单
		   	modifyFormRules:{
		   		oldPassword:[
		   		    { required: true, message: '当前密码不能为空',trigger:'blur'},
		   		    { min: 6, max: 30, message: '长度在 6 到 30个字符' },
		   		    {pattern:/^[0-9a-zA-Z]*$/,message:'只允许输入字母/数字',trigger:'blur'}	
		   		  ],
		   		newPassword:[
		   			{ required: true, message: '新密码不能为空',trigger:'blur'},
		   		    { min: 6, max: 30, message: '长度在 6 到 30个字符', trigger: 'blur' },
		   		    {pattern:/^[0-9a-zA-Z]*$/,message:'只允许输入字母/数字',trigger:'blur'}	
		   		 ],
		   		 confirmPwd:[
		   			{ required: true, message: '确认密码不能为空',trigger:'blur'},
		   		    { min: 6, max: 30, message: '长度在 6 到 30个字符', trigger: 'blur' },
		   		    { validator: validatePass, trigger: 'blur' }
		   		 ]
		   		
		   	},
		   	isLogin:false,//坐席是否登录
		   	isTrClient:false,//天润坐席是否登录
		   	isQimoClient:false,//七陌坐席是否登录
	    	callTitle:'呼叫中心',
	    	dialogLoginClientVisible:false,//登录坐席dialog 
	    	dialogLogoutClientVisible:false,
	    	loginClientForm:{
	    		clientType:1,
	    		cno:'',
	    		bindPhone:'',
	    		bindPhoneType:1,
	    	    loginClient:''
	    		
	    	},
	    	clientTypeOptions: [{
                value: 1,
                label: '登录天润呼叫中心'
            }, {
                value: 2,
                label: '登录七陌呼叫中心'
            }],
            bindPhoneTypeOptions: [{
                value: 1,
                label: '普通电话'
            }, {
                value: 2,
                label: '手机外显'
            }],
            trClientFormRules:{//登录坐席校验规则
            	
            	cno:[
            		 { required: true, message: '坐席号不能为空'},
            	],
	    	    bindPhone:[
	    	    	{ required: true, message: '绑定电话不能为空'},
	    	    ]
            	
            },
            qimoClientFormRules:{
            	loginClient:[
            		{ required: true, message: '登录坐席不能为空'},
            	]
            },
            enterpriseId:enterpriseId,
            token:token,
            dialogOutboundVisible:false,//外呼dialog
            outboundInputPhone:'',//外呼时手机号
            accountId:accountId,//登陆者ID
	    }
	},
 	methods: {
	    handleOpen(key, keyPath) {
	      	// console.log(key, keyPath);
	    },
	    handleClose(key, keyPath) {
	      	// console.log(key, keyPath);
	    },
	    toogleClick(){
	      	if(this.isCollapse){
	        	this.isCollapse=false
	        	this.isActive=true
	      	}else{
	        	this.isCollapse=true
	        	this.isActive=false
	      	}          
	    },
	    menuClick:function(ifreamUrl){
	     	this.$refs.iframeBox.src=ifreamUrl //给ifream的src赋值
	   	},  
	   	handleCommand(command) {//点击下拉菜单
	        if(command=='modifyPwd'){//修改密码
	        	this.modifyPwd();
	        }else if(command=='logout'){//退出
	        	this.logout();
	        }
	     },
	     modifyPwd(){//修改密码
	    	 //重置表单
	    	 this.dialogModifyPwdVisible=true;
	    
	     },
	     logout(){
	    	 this.dialogLogoutVisible=true;
	     },
	     cancelModifyForm(formName){//取消修改密码
         	this.$refs[formName].resetFields();
         	 homePageVM.dialogModifyPwdVisible = false;
         },
         saveModifyForm(formName){//保存
        	 this.$refs[formName].validate((valid) => {
                 if (valid) {
                    var param=this.modifyForm;
                   axios.post('/user/userManager/updatePwd', param)
                   .then(function (response) {
                       var resData = response.data;
                       if(resData.code=='0'){
                    	   homePageVM.$message({message:'请使用新密码重新登录',type:'success'});
                    	   homePageVM.cancelModifyForm(formName);
                    	   
                    	   setTimeout(function(){//去登录页
                    		   homePageVM.gotoHomePage();
                    	   },2000);
                       }else{
                    	   homePageVM.$message(resData.msg);
                           console.error(resData);
                       }
                   
                   })
                   .catch(function (error) {
                        console.log(error);
                        homePageVM.$message({
                            message: "系统出错",
                            type: 'error'
                        }); 
                   });
                    
                 } else {
                   return false;
                 }
               });
         },
         confirmLogout(){//确认退出系统
        	 location.href="/index/logout";
        },
        gotoHomePage(){//首页跳转
        	location.href='/login';
        },
		messageCount(){
	    	console.log("请求消息中心未读条数")
			var param = {};
			axios.post("/messagecenter/unreadCount", param).then(function (response) {
				var data = response.data.data
				var annCount = 0;
				var str = "";
				if(data&&data!=0){
					annCount = data;
					str = "&nbsp;(&nbsp;"+(annCount)+"&nbsp;)"
				}
				document.getElementById("messageCount").innerHTML= str;
			})
		},
		openMessageCenter(){
	    	console.log("跳转消息中心")
	    	var dataUrl = "/messagecenter/messageCenter";
			$("#iframeBox").attr({
				"src":dataUrl //设置ifream地址
			});
        },
        closeModifyPwdDialog(){
         	this.$refs.modifyForm.resetFields();
        },
        openLoginClientDialog(){//打开登录坐席dialog
        		if(this.isQimoClient){
        			this.loginClientForm.clientType=2;
        			this.dialogLogoutClientVisible  = true;
        		}else if(this.isTrClient){
        			this.loginClientForm.clientType=1;
        			this.dialogLogoutClientVisible  = true;
        		}else{

            		this.loginClientForm.clientType=1;//设置默认选中天润坐席
            		this.dialogLoginClientVisible = true;
        		}
        	
        },
        cancelLoginClientForm(){
        	this.dialogLoginClientVisible = false;
        },
        changeClientType(selectedValue){
        	this.$refs.loginClientForm.resetFields();
        	this.loginClientForm.clientType=selectedValue;
        },
        loginClient(formName){
       	 this.$refs[formName].validate((valid) => {
             if (valid) {
            	 var clientType = this.loginClientForm.clientType;
            	 if(clientType==1){//天润坐席登录
             		this.loginTrClient();
             	}else if(clientType==2){
             		this.loginQimoClient();
             		
             	}
             	
             } else {
               return false;
             }
           });
        	
        	
        },
        loginQimoClient(){//七陌登录
        	var loginClient = this.loginClientForm.loginClient;
        	var bindType = this.loginClientForm.bindPhoneType;
        	var param={};
        	param.bindType = bindType+"";
        	param.loginName = loginClient;
        	 axios.post('/client/client/qimoLogin',param)
             .then(function (response) {
                 var data =  response.data;
                 if(data.code=='0'){
                     var resData = data.data;
                     homePageVM.$message({message:"登录成功",type:'success'});
                     homePageVM.callTitle="呼叫中心（七陌ON）";
                     homePageVM.dialogLoginClientVisible =false;
                     homePageVM.isQimoClient=true;
                     homePageVM.isTrClient=false;
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
        },
        loginTrClient(){//天润登录
        	var loginType = "2";
			var enterpriseId = this.enterpriseId;
			var cno = this.loginClientForm.cno;
			var bindPhone = this.loginClientForm.bindPhone;
			var bindType = this.loginClientForm.bindPhoneType;
			var token = this.token;
			var params = {};
			params.bindTel = bindPhone;
			params.bindType = bindType;
			params.loginStatus = 1;
			bindType = params.bindType;
			if (bindType == 2) {
				// alert("bitch");
				$.get("/client/client/login/" + cno);
				bindType = 1;
				params.bindType = 1;
			} else {
				$.post("/client/client/destroy/"+ cno);
			}
			
			var cticloud_url = "api-2.cticloud.cn";
        	
			var agentSign;
			var url = window.location.protocol
					+ "//"
					+ cticloud_url
					+ "/interface/v10/agentLogin/authenticateJsonp?validateType="
					+ loginType;

			url += "&enterpriseId=" + enterpriseId + "&cno=" + cno;
			agentSign = enterpriseId;
			console.info("tr_client login url"+ url);
			var timestamp = Date.parse(new Date()) / 1000;
			agentSign = agentSign + timestamp;
			
			if (token != '') {
				agentSign = agentSign + token;
			} else {
				alert('座席号或密码不正确');
				return false;
			}
			
			agentSign = hex_md5(agentSign);
			url += "&timestamp=" + timestamp + "&sign=" + agentSign;
			console.log(url);
			
			$.ajax({
				type : 'get',
				url : url,
				dataType : 'jsonp',
				jsonp : 'callback',
				success : function(r) {
					console.log(r);
					var d = eval("(" + r + ")");
					if (d.result == "0") {
						params.enterpriseId = d.enterpriseId;
						params.cno = d.cno;
						params.sessionKey = d.sessionKey;
						params.qids = d.qids;
						/*
						 * document.getElementById("toolbar").contentWindow.TOOLBAR.login(params,
						 * cbLogin);//执行登陆 ccic2里面的js类
						 */
						// 执行登陆
						TOOLBAR.login(params, function(token){
							 if (token.code == 0) {
			                    //座席登录成功
								 homePageVM.$message({message:'登录成功',type:'success'});
								 homePageVM.callTitle="呼叫中心（天润ON）";
			                     homePageVM.dialogLoginClientVisible =false;
			                     homePageVM.isQimoClient=false;
			                     homePageVM.isTrClient=true;
			                     
			                     var recordParam = {};
			                     recordParam.clientType=homePageVM.loginClientForm.clientType;
			                     recordParam.bindPhone= bindPhone;
			                     recordParam.cno= cno;
			                     //记录坐席登录
			                     axios.post('/client/client/clientLoginRecord',recordParam)
			                     .then(function (response) {
			                     })
			                     .catch(function (error) {
			                        console.log(error);
			                     })
			                     .then(function () {
			                       // always executed
			                     });
			                } else {
			                    //座席登录失败，失败原因： + result.msg
			                	var _msg = "登录失败！座席号或密码不正确";
			                	homePageVM.$message({message:_msg,type:'error'});
			                	return;
			                }
							
						});
			
						
						// ccic2里面的js类
						agentNumber = params.cno;
						sessionStorage.setItem("enterpriseId",params.enterpriseId, 7);
						sessionStorage.setItem("cno", params.cno, 7);
						sessionStorage.setItem("bindTel", params.bindTel, 7);
						sessionStorage.setItem("bindType", params.bindType, 7);
					} else {
						homePageVM.$message.error({message:d.description,type:'error'});
					}
				},
				error : function(r) {
					alert(r);
				}
			})
			
			
        },
        logoutClient(formName){//坐席退出
        	if(this.isQimoClient){
        		 axios.post('/client/client/qimoLogout',{})
                 .then(function (response) {
                     var data =  response.data;
                     if(data.code=='0'){
                    	 homePageVM.dialogLogoutClientVisible =false;
                         homePageVM.$message({message:"退出成功",type:'success'});
                         homePageVM.callTitle="呼叫中心";
                         homePageVM.isQimoClient=false;
                         homePageVM.isTrClient=false;
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
        	}else if(this.isTrClient){//天润
        		 var params = {};
         	    params.logoutMode = 1;
         	    params.removeBinding = 1;
         	    CTILink.Agent.logout(params, function(result) {
         	        if (result.code == 0) {
         	      	    homePageVM.dialogLogoutClientVisible =false;
                        homePageVM.isQimoClient=false;
                        homePageVM.isTrClient=false;
                        homePageVM.callTitle="呼叫中心";
                        homePageVM.$message({message:"退出成功",type:'success'});
         	        }else{
         	        	homePageVM.$message({message:"退出失败",type:'error'});
         	        }
         	    });
        		
        	}
        },
        openOutboundDialog(){//打开主动外呼diaolog 
        	this.dialogOutboundVisible = true;
        },
        clickOutbound(){//外呼
        	if(!this.isQimoClient && !this.isTrClient ){
        		   this.$message({message:"请登录呼叫中心",type:'warning'});
        		   return ;
        	}
        	var outboundInputPhone = this.outboundInputPhone;
        	console.log("outboundInputPhone:"+outboundInputPhone);
        	if(this.isTrClient){//天润呼叫
        		var param = {};
        		param.tel=outboundInputPhone;
        		param.userField={
        				'accountId':''
        		}
        		TOOLBAR.previewOutcall(param,function(token){
        			console.info("直接外呼");
        			if(token.code=='0'){
        				homePageVM.$message({message:"外呼中",type:'success'});
        			}else{
        				console.error(token);
        				homePageVM.$message({message:"外呼失败",type:'error'});
        			}
        		});
        	}else if(this.isQimoClient){//七陌呼叫
        		
        	}
        }
         
  	},
   	created() {  
   		document.body.removeChild(document.getElementById('Loading'));
		this.messageCount();
		if(isUpdatePassword=="1"){
			this.dialogModifyPwdVisible=true;
		}
	}
})
// 点击导航赋值ifream的src值
$(function () { 
	var mainBoxH=$(".elMain").height()-4;
	// 设置ifream高度
	$("#iframeBox").height(mainBoxH)
	$(document).on('click','.menu',function(){
	    // console.log(0)
	    // console.log($(this).attr("data-url"))
	    var dataUrl=$(this).attr("data-url");
	    $("#iframeBox").attr({
	      "src":dataUrl //设置ifream地址
	    });  
	})
});