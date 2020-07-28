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
          isRoleCodeDX:false,
          isRoleCodeSW:false,
	    	  formLabelWidth:'130px',
            formLabelWidth105:'105px',
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
		    defaultOpeneds:showBtnIndex,//默认打开菜单索引
		    initUrl:showMenuUrl,//默认展示页面
	       // defaultActive:'0-0',//默认激活菜单
	        defaultActive:defaultActive,//默认激活菜单
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
            isHeliClient:false,//合力坐席是否登录
            isKeTianClient:false,//科天坐席是否登录
		   	isRongLianClient:false,//容联坐席是否登录
	    	callTitle:'呼叫中心',
	    	dialogLoginClientVisible:false,//登录坐席dialog 
	    	dialogLogoutClientVisible:false,
        isShowTip:false,//默认不显示七陌里的天翼云app呼叫红色提示
        isShowPhoneNumber:false,//默认不显示新增绑定手机号
	    	loginClientForm:{
	    		clientType:1,//呼叫中心默认1是天润
	    		cno:'',
	    		bindPhone:'',
	    		bindPhoneType:2,
	    	  loginClient:'',//登录坐席
          callPhoneType:1,//呼叫方式，默认是1话机呼叫	 
          mobilePhoneNumber:'',//绑定手机号   		
	    	},
	    	clientTypeOptions: [{
                value: 1,
                label: '登录天润呼叫中心'
            }, {
                value: 2,
                label: '登录七陌呼叫中心'
            }, {
                value: 3,
                label: '登录合力呼叫中心'
            }, 
            {
                value: 4,
                label: '登录科天呼叫中心'
            },
            {
                value: 5,
                label: '登录容联呼叫中心'
            }],
            bindPhoneTypeOptions: [
                	{
    					  value: 2,
    						label: '手机外显'
    				},{
    						value: 1,
    						label: '普通电话'
		       }],
           bindPhoneTypeOptions1: [{
                value: 1,
                label: '普通电话'
           }],
           callPhoneTypeOptions: [
                  {
                value: 1,
                label: '话机呼叫'
            },{
                value: 2,
                label: '天翼云呼app呼叫'
           },{
                value: 3,
                label: '绑定手机号呼叫'
           }],
            trClientFormRules:{//登录坐席校验规则
            	clientType:[
                    { required: true, message: '请选择呼叫中心',trigger: 'change'}
                ],
            	cno:[
            		 { required: true, message: '坐席号不能为空',trigger: 'blur'},
            		 {validator:function(rule,value,callback){
            			 if(!/^[0-9]*$/.test(value)){
          					  callback(new Error("只可以输入数字,不超过10位"));     
          	        	  }else{
          	        		  callback();
          	        	  }
            			 
            		 },trigger:'blur'},
            	],
	    	    bindPhone:[
	    	    	{ required: true, message: '绑定电话不能为空',trigger: 'blur'},
	    	    	 {validator:function(rule,value,callback){
            			 if(!/^[0-9]*$/.test(value)){
          					  callback(new Error("只可以输入数字,不超过11位"));     
          	        	  }else{
          	        		  callback();
          	        	  }
            			 
            		 },trigger:'blur'},
	    	    ],
                bindPhoneType:[
                    { required: true, message: '请选择绑定类型',trigger: 'change'}
                ]
            	
            },
            qimoClientFormRules:{
                clientType:[
                    { required: true, message: '请选择呼叫中心',trigger: 'change'}
                ],
            	loginClient:[
            		{ required: true, message: '登录坐席不能为空',trigger: 'blur'},
            		{validator:function(rule,value,callback){
           			 if(!/^[0-9]*$/.test(value)){
         					  callback(new Error("只可以输入数字,不超过10位"));     
         	        	  }else{
         	        		  callback();
         	        	  }
           			 
           		 },trigger:'blur'},
            	],
                bindPhoneType:[
                    { required: true, message: '请选择绑定类型',trigger: 'change'}
                ],
                callPhoneType:[
                    { required: true, message: '请选择呼叫方式',trigger: 'change'}
                ],
                mobilePhoneNumber:[
                    { required: true, message: '绑定手机号不能为空',trigger: 'blur'},
                    {validator:function(rule,value,callback){
                        if(!/^[0-9]*$/.test(value)){
                            callback(new Error("只可以输入数字,不超过11位"));     
                        }else{
                            callback();
                        }
                   
                    },trigger:'blur'},
                ]
            },
			      heliClientFormRules:{//登录坐席校验规则
            	  clientType:[
                    { required: true, message: '请选择呼叫中心',trigger: 'change'}
                ],
            	  cno:[
            		    { required: true, message: '坐席号不能为空', trigger: 'blur'},
            		    {validator:function(rule,value,callback){
            			      if(!/^[0-9]*$/.test(value)){
          					        callback(new Error("只可以输入数字,不超过10位"));     
          	        	  }else{
          	        		    callback();
          	        	  }
            			 
            		    },trigger:'blur'},
            	  ],
                bindPhoneType:[
                    { required: true, message: '请选择绑定类型',trigger: 'change'}
                ],

            },
            ktClientFormRules:{
                clientType:[
                    { required: true, message: '请选择呼叫中心',trigger: 'change'}
                ],
                loginClient:[
                    { required: true, message: '登录坐席不能为空',trigger: 'blur'},
                    {validator:function(rule,value,callback){
                        if(!/^[0-9]*$/.test(value)){
                            callback(new Error("只可以输入数字,不超过10位"));     
                        }else{
                            callback();
                        }
                 
                    },trigger:'blur'},
                ],
                bindPhoneType:[
                    { required: true, message: '请选择绑定类型',trigger: 'change'}
                ],
            },
            rlClientFormRules:{//登录坐席校验规则
                clientType:[
                    { required: true, message: '请选择呼叫中心',trigger: 'change'}
                ],
                loginClient:[
                    { required: true, message: '登录坐席不能为空',trigger: 'blur'},
                    {validator:function(rule,value,callback){
                        if(!/^[0-9]*$/.test(value)){
                            callback(new Error("只可以输入数字,不超过10位"));     
                        }else{
                            callback();
                        }                     
                    },trigger:'blur'},
                ],
            },
          /*  clientRules:'trClientFormRules',*/
            enterpriseId:enterpriseId,
            token:token,
            dialogOutboundVisible:false,//外呼dialog
            outboundInputPhone:'',//外呼时手机号
            accountId:accountId,//登陆者ID
            outboundDialogMin:false,//外呼dialog 是否最小化
            tmOutboundCallDialogVisible:false,//电销页面外呼 dialog 
            consoleBtnVisible:isShowConsoleBtn,//控制台按鈕是否可見
            accountType:accountType,
			webRTCCallback:{//科天登录回调
				handle:(event,msg)=>{
					if(event === 'check'){
						if(!msg){
							this.$message({message:"初始化失败，请检查麦克风！",type:'error'});
						}
					}
					if (event === 'invite') {
						console.info("invite");
					}
				}
			},
          ringOffdialogVisible:false,//默认不显示挂断弹窗
          isRingOff:false,//默认不显示挂断按钮
    			ketianInBoundPhone:'',//科天来电手机号

          isDataBase:false,//默认不展示资料库按钮
          dataBasedialogVisible:false,//资料库弹窗默认不显示
          searchDatabaseKeyword:'',//搜索关键词
          dataBaseInvestMoneyArr:[],//投资金额list
          dataBaseInvestAreaArr:[],//投资区域list
          dataBaseCategoryArr:[],//意向品类list
          dataBaseInvestMoneyVal:"0",//默认是不限
          dataBaseInvestAreaVal: "0",//默认是不限
          dataBaseCategoryVal: "0",//默认是不限
          dataBaseResultArr:[],//搜索结果
          isshowsearchTip:false,//默认暂无搜索结果
          issearchResult:false,
          dataBaseUrl:dataBaseUrl,//搜索接口地
          unionTipdialogVisible:false,
          isTipbgShow:false,//默认不显示提示框图片2
          isCurrent:true,//首页按钮默认高亮
	    }
	},
 	methods: {
      dataBaseClick1(val){//投资金额
        // console.log(val)
        this.dataBaseInvestMoneyVal=val;
      },
      dataBaseClick2(val){//投资区域
        // console.log(val)
        this.dataBaseInvestAreaVal=val;
      },
      dataBaseClick3(val){//意向品类
        // console.log(val)
        this.dataBaseCategoryVal=val;
      },
      // 获取搜索条件
      searchDataList1() {//投资金额
          var param = {};
          param.groupCode = "dataBaseInvestMoney";
          axios.post('/dictionary/DictionaryItem/dicItemsByGroupCode', param).then(function (response) {
            // console.log(response)
            homePageVM.dataBaseInvestMoneyArr = response.data.data;
          });
      },
      searchDataList2() {//投资区域
          var param = {};
          param.groupCode = "dataBaseInvestArea";
          axios.post('/dictionary/DictionaryItem/dicItemsByGroupCode', param).then(function (response) {
            // console.log(response)
            homePageVM.dataBaseInvestAreaArr = response.data.data;
          });
      },
      searchDataList3() {//意向品类
          var param = {};
          param.groupCode = "dataBaseCategory";
          axios.post('/dictionary/DictionaryItem/dicItemsByGroupCode', param).then(function (response) {
            // console.log(response)
            homePageVM.dataBaseCategoryArr = response.data.data;
          });
      },
      searchDatabaseFun(){
          var keyword=this.searchDatabaseKeyword;
          var join_fee=this.dataBaseInvestMoneyVal;
          var join_area=this.dataBaseInvestAreaVal;
          var category_name=this.dataBaseCategoryVal;
          axios.get(dataBaseUrl+'?keyword='+keyword+'&join_fee='+join_fee+'&join_area='+join_area+'&category_name='+category_name)
          .then(function (response) {
              // console.log(response)
              var result =  response.data;
              if(result.code==0){
                  if(result.data.list&&result.data.list.length>0){
                      homePageVM.issearchResult=true;
                      homePageVM.isshowsearchTip=false;
                      homePageVM.dataBaseResultArr=result.data.list; 
                  }else{
                      homePageVM.issearchResult=false;
                      homePageVM.isshowsearchTip=true;
                  }
                                    
              }else{
                  homePageVM.$message.error(result.msg);
              }                    
          })
          .catch(function (error) {
               console.log(error);
          });
      },
      opendataBase(){
          this.dataBasedialogVisible=true
      },
      // 切换七陌里的呼叫方式
      changeCallPhoneType(val){

          if(val==2){
            // 如果是天翼云呼叫app
            this.isShowTip=true;
            this.isShowPhoneNumber=false;
          }else if(val==3){
            this.isShowTip=false;
            // 如果是绑定手机号
            this.isShowPhoneNumber=true;
          }else{
            this.isShowTip=false;
            this.isShowPhoneNumber=false;
          }
      },
        // 输入数字控制方法
        cnoNumber(){
　　　    this.loginClientForm.cno=this.loginClientForm.cno.replace(/[^\.\d]/g,'');
          this.loginClientForm.cno=this.loginClientForm.cno.replace('.','');
    　  },
        bindPhoneNumber(){
　　　    this.loginClientForm.bindPhone=this.loginClientForm.bindPhone.replace(/[^\.\d]/g,'');
          this.loginClientForm.bindPhone=this.loginClientForm.bindPhone.replace('.','');
    　  },
        mobilePhoneNumber(){
　　　    this.loginClientForm.mobilePhoneNumber=this.loginClientForm.mobilePhoneNumber.replace(/[^\.\d]/g,'');
          this.loginClientForm.mobilePhoneNumber=this.loginClientForm.mobilePhoneNumber.replace('.','');
    　  },
 		handleMin(){
 			this.dialogLoginClientVisible = false;
 		},
 		handleOutMin(){
 			this.dialogLogoutClientVisible = false;
 		},
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
          window.sessionStorage.clear(); // 点击侧边栏-清除所有cookie
	   	}, 
      menuClickCm:function(ifreamUrl){//餐盟菜单点击
          window.sessionStorage.clear(); // 点击侧边栏-清除所有cookie
          // 餐盟首页index去掉高亮
          this.isCurrent=false;
      },  
	   	handleCommand(command) {//点击下拉菜单
	        if(command=='modifyPwd'){//修改密码
	        	this.modifyPwd();
	        }else if(command=='logout'){//退出
	        	this.logout();
	        }else{
                this.gotoUserInfo();
            }
	     },
         gotoUserInfo(){
            var dataUrl = "/merchant/userManager/userInfo";
            $("#iframeBox").attr({
                "src":dataUrl //设置ifream地址
            });
         },
	     modifyPwd(){//修改密码
	    	 //重置表单
	    	 this.dialogModifyPwdVisible=true;
	    
	     },
	     logout(){
	    	 this.dialogLogoutVisible=true;
	    	 this.$refs.loginClientForm.resetFields();
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
             window.sessionStorage.clear();//清除缓存
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
					str = '<em class="messageCount">'+annCount+'</em>'
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
        		}else if(this.isHeliClient){
        			this.loginClientForm.clientType=3;
        			this.dialogLogoutClientVisible  = true;
                }else if(this.isKeTianClient){
                    this.loginClientForm.clientType=4;
                    this.dialogLogoutClientVisible  = true;
                }else if(this.isRongLianClient){
                    this.loginClientForm.clientType=5;
                    this.dialogLogoutClientVisible  = true;
        		}else{
        			 if (this.$refs.loginClientForm !==undefined) {
        				  this.$refs.loginClientForm.resetFields();
        				 this.$refs.loginClientForm.clearValidate(function(){
        					 
        				 });
        			 }
        		/*	if(!this.dialogLoginClientVisible){
        				this.loginClientForm.clientType=1;//设置默认选中天润坐席
                		this.loginClientForm.bindPhoneType=1;
                		this.loginClientForm.cno='';
                		this.loginClientForm.bindPhone='';
                		this.loginClientForm.loginClient='';
                		
        			}*/
        			this.dialogLoginClientVisible = true;
        		}
        	
        },
        cancelLoginClientForm(){
        	this.dialogLoginClientVisible = false;
        	if (this.$refs.loginClientForm !==undefined) {
				  this.$refs.loginClientForm.clearValidate();
			 }
        	
        	this.loginClientForm.clientType=1;//设置默认选中天润坐席
    		this.loginClientForm.bindPhoneType=2;
    		this.loginClientForm.cno='';
    		this.loginClientForm.bindPhone='';
    		this.loginClientForm.loginClient='';
        	
        },
        changeClientType(selectedValue){
          this.isShowTip=false;
          this.isShowPhoneNumber=false;
        	this.$refs.loginClientForm.resetFields();
        	this.$refs.loginClientForm.clearValidate();
        	this.loginClientForm.clientType=selectedValue;
            // bindPhoneType绑定类型 1是手机外显2是普通电话
        	if(selectedValue==2 || selectedValue==4){//七陌、科天
              this.loginClientForm.bindPhoneType = 1;
        		  this.loginClientForm.callPhoneType = 1;//呼叫方式是1
    			}else if (selectedValue==1 || selectedValue ==3){//天润 合力
            		this.loginClientForm.bindPhoneType = 2;
    			}else if (selectedValue==5){//容联
                this.loginClientForm.bindPhoneType = "";
          }
          // 登录坐席是空
          this.loginClientForm.loginClient="";
          // 坐席号是空
          this.loginClientForm.cno="";
          // 坐席号是空
          this.loginClientForm.bindPhone="";
        },
        loginClient(formName){
       	 this.$refs[formName].validate((valid) => {
             if (valid) {
            	 var clientType = this.loginClientForm.clientType;
            	 if(clientType==1){//天润坐席登录
             		this.loginTrClient();
             	}else if(clientType==2){
             		this.loginQimoClient();
             	}else if(clientType==3){
             		this.loginHeliClient();
             	}else if(clientType==4){//科天坐席登录
                    this.loginKeTianClient();
                }else if(clientType==5){//容联坐席登录
                    this.loginRongLianClient();
                }
             	
             } else {
               return false;
             }
           });
        	
        	
        },
        loginHeliClient(){//合力 登录
        	var bindType = this.loginClientForm.bindPhoneType;
        	if(bindType==1){
    		     this.$message({message:"合力不支持普通电话模式登录！",type:'warning'});
    		     return;
        	}
        	var cno = this.loginClientForm.cno;
        	//验证坐席是否属于自己
        	if(!this.validHeliClientNo(cno)){
        		return;
        	}
        	var param = {};
        	param.bindType = bindType+"";
        	param.clientNo = cno;
        	param.accountType = homePageVM.accountType;
        	param.clientType = homePageVM.loginClientForm.clientType;
	       	 axios.post('/client/heliClient/login',param)
	         .then(function (response) {
	             var data =  response.data;
	             
	             if(data.code=='0'){
	                 var resData = data.data;
	                 homePageVM.$message({message:"登录成功",type:'success'});
	                 homePageVM.callTitle="呼叫中心（合力ON）";
	                 homePageVM.dialogLoginClientVisible =false;
	                 homePageVM.isHeliClient=true;
	                 homePageVM.isTrClient=false;
	                 homePageVM.isQimoClient = false;
	                 homePageVM.isRongLianClient = false;
	                 homePageVM.isKeTianClient = false;
	                 //sessionStorage.setItem("loginClient","qimo");
	                 //sessionStorage.setItem("accountId",homePageVM.accountId);
	                 var clientInfo={};
	                 clientInfo.loginClientType="heli";
	                 clientInfo.clientNo = homePageVM.loginClientForm.cno;
	                 clientInfo.clientType = homePageVM.loginClientForm.clientType;
	                 clientInfo.bindType = homePageVM.loginClientForm.bindPhoneType;
	                 localStorage.setItem("clientInfo",JSON.stringify(clientInfo));
	                 
	             }else{
	            	 console.error(data);
	                 homePageVM.$message({message:"合力坐席登录："+data.msg,type:'error'});
	             }
	         })
	         .catch(function (error) {
	            console.log(error);
	         })
	         .then(function () {
	           // always executed
	         });
        	
        	
        },
        loginQimoClient(){//七陌登录
        	var loginClient = this.loginClientForm.loginClient;
        	var bindType = this.loginClientForm.bindPhoneType;
          var callType = this.loginClientForm.callPhoneType;//新增呼叫方式
          var bindPhone = this.loginClientForm.mobilePhoneNumber;//新增呼叫方式

			/*if(bindType==1){
				this.$message({message:"七陌不支持普通电话模式登录！",type:'warning'});
				return;
			}*/
        	var param={};
        	param.bindType = bindType+"";
        	param.loginName = loginClient;
        	param.accountType = homePageVM.accountType;
        	param.clientType = homePageVM.loginClientForm.clientType;
          // 呼叫方式
          param.callType = homePageVM.loginClientForm.callPhoneType;
          // 绑定手机号
          if(bindPhone&&param.callType==3){//3是选择绑定手机号呼叫
            param.bindPhone=homePageVM.loginClientForm.mobilePhoneNumber;
          }else{
            param.bindPhone="";
          }

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
                     homePageVM.isHeliClient=false;
                     homePageVM.isKeTianClient=false;
                     homePageVM.isRongLianClient=false;
                     //sessionStorage.setItem("loginClient","qimo");
                     //sessionStorage.setItem("accountId",homePageVM.accountId);
                     var clientInfo={};
                     clientInfo.loginClientType="qimo";
                     clientInfo.loginClient = homePageVM.loginClientForm.loginClient
                     clientInfo.clientType = homePageVM.loginClientForm.clientType;
                     clientInfo.bindType = homePageVM.loginClientForm.bindPhoneType;
                     // 呼叫方式
                     clientInfo.callType=homePageVM.loginClientForm.callPhoneType;
                     // 绑定手机号
                     clientInfo.bindPhone=homePageVM.loginClientForm.mobilePhoneNumber;

                     localStorage.setItem("clientInfo",JSON.stringify(clientInfo));
                     
                    /* var recordParam = {};
                     recordParam.clientType=homePageVM.loginClientForm.clientType;
                     recordParam.bindPhone= bindPhone;
                     recordParam.cno= loginClient;
                     recordParam.accountType=homePageVM.accountType;
                     //记录坐席登录
                     axios.post('/client/client/clientLoginRecord',recordParam)
                     .then(function (response) {
                     })
                     .catch(function (error) {
                        console.log(error);
                     })
                     .then(function () {
                       // always executed
                     });*/
                     
                 }else{
                	 console.error(data);
                     homePageVM.$message({message:"登录失败:"+data.msg,type:'error'});
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
        	var cno = this.loginClientForm.cno;
        	//验证坐席是否属于自己
        	if(!this.validClientNo(cno)){
        		return;
        	}
        	var bindType = this.loginClientForm.bindPhoneType;
        	if(bindType==1){
        		//tr 普通登陸
        		var whiteList = [
              	   915157337459918,915157338827184,915157339205499,915157339500788
              	   ,915157339774915,915390540464159,115450274113949,115450274246648
              	   ,115450529284536,115451100381743,915319637549027,915319637639747
              	   ,915319638079196,915320465997397,915320621197701,214991403833604
              	   ,115410338236403,115565818271983,115622112138370,1154293728744181760
              	   ,1149232865020612608,1149232925418590208,1149232985145479168,1149233047493808128
              	   ,1149636656941375488,1149232583238885376,214991401620051
              	   ];
             	var  curOrgId = this.user.orgId;
             	var isLimit = false;
            	for(var i=0;i<whiteList.length;i++){
            		if(curOrgId==whiteList[i]){
            			isLimit = true;
            			break;
            		}
            	}
        		if(!isLimit){
        			//限制普通電話登陸
        		    this.$message({message:"天润不支持普通电话模式登录！",type:'warning'});
        			return;
        			
        		}
            	
        	}
        	var loginType = "2";
			var enterpriseId = this.enterpriseId;
			var bindPhone = this.loginClientForm.bindPhone;
			
			var token = this.token;
			var params = {};
			params.bindTel = bindPhone;
			params.bindType = bindType;
			params.loginStatus = 1;
			bindType = params.bindType;
			if (bindType == 2) {
				// alert("bitch");
				$.get("/client/client/login/" + cno);
				//TODO 暂时注释 dev
				//bindType = 1;
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
				var _msg = "登录失败！token不可以为空";
            	homePageVM.$message({message:_msg,type:'error'});
				return false;
			}
			
			agentSign = hex_md5(agentSign);
			url += "&timestamp=" + timestamp + "&sign=" + agentSign;
			// console.log(url);
			
			$.ajax({
				type : 'get',
				url : url,
				dataType : 'jsonp',
				jsonp : 'callback',
				success : function(r) {
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
			                     homePageVM.isHeliClient=false;
			                     homePageVM.isKeTianClient=false;
			                     homePageVM.isRongLianClient = false;
			                     //sessionStorage.setItem("loginClient","tr");
			                    // sessionStorage.setItem("accountId",homePageVM.accountId);
			                     
			                     var clientInfo={};
			                     clientInfo.loginClientType="tr";
			                     clientInfo.clientType = homePageVM.loginClientForm.clientType;
			                     clientInfo.bindTel=homePageVM.loginClientForm.bindPhone;
			                     clientInfo.bindType = homePageVM.loginClientForm.bindPhoneType;
			                     clientInfo.cno=homePageVM.loginClientForm.cno;
			                     clientInfo.enterpriseId = homePageVM.enterpriseId;
			                     clientInfo.token=homePageVM.token;
			                     localStorage.setItem("clientInfo",JSON.stringify(clientInfo));
			                     console.info("clientInfo"+JSON.stringify(clientInfo));
			                     var recordParam = {};
			                     recordParam.clientType=homePageVM.loginClientForm.clientType;
			                     recordParam.bindPhone= bindPhone;
			                     recordParam.cno= cno;
			                     recordParam.accountType=homePageVM.accountType;
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
			                	var _msg = "登录失败！座席号或绑定电话不正确";
			                	homePageVM.$message({message:_msg,type:'error'});
			                	console.error(token);
			                	return;
			                }
							
						});
			
						
						// ccic2里面的js类
						agentNumber = params.cno;
						//sessionStorage.setItem("enterpriseId",params.enterpriseId, 7);
						//sessionStorage.setItem("cno", params.cno, 7);
						//sessionStorage.setItem("bindTel", params.bindTel, 7);
						//sessionStorage.setItem("bindType", params.bindType, 7);
					} else {
						//homePageVM.$message.error({message:d.description,type:'error'});
						var _msg = "登录失败！座席号或绑定电话不正确";
	                	homePageVM.$message({message:_msg,type:'error'});
	                	console.error(r);
					}
				},
				error : function(r) {
					alert(r);
				}
			})
			
			
        },
        loginKeTianClient(){//科天登录
			var bindType = this.loginClientForm.bindPhoneType;
			if(bindType==1){
				this.$message({message:"科天不支持普通电话模式登录！",type:'warning'});
				return;
			}
			var loginClient = this.loginClientForm.loginClient;
			var param = {};
			param.loginName = loginClient;
			axios.post('/client/ketianClient/queryClientByLoginName',param)
				.then(function (response) {
					var data =  response.data;
					if(data.code=='0'){
						var resData = data.data;
						if(!resData){
							homePageVM.$message({message:"登陆失败，该坐席号不属于您",type:'error'});
						}
						var requestUrl = "https://tky.ketianyun.com";
						var userName = resData.userName;
						var password = resData.pwd;
						//注册
						var configuration = {};
						configuration.baseUrl = requestUrl;
						configuration.username = userName;
						configuration.password = password;
						configuration.enableWebRTC = "true";
						configuration.stateEventListener = homePageVM.ketianStateEventListener;
						CtiAgentBar.init(configuration, homePageVM.initCallback,homePageVM.webRTCCallback).then((res) => {
							if(res.code === 200){
								var clientNo = resData.clientExtNo;
								//登录
								CtiAgentBar.login(clientNo);
								//就绪
								//CtiAgentBar.ready();
								var clientType = homePageVM.loginClientForm.clientType;
								//记录登录信息
								homePageVM.ketianClientLoginRecord({"loginName":loginClient,"accountType":homePageVM.accountType,"clientType":clientType});
								homePageVM.isRingOff = true;
							}else{
								homePageVM.$message({message:"注册失败-"+res.msg,type:'error'});
							}
						});
					}else{
						console.error(data);
						homePageVM.$message({message:"查询坐席是否属于自己:"+data.msg,type:'error'});
					}
				})
				.catch(function (error) {
					console.log(error);
				})
				.then(function () {
					// always executed
				});

        },
		ketianConnection(){
			CtiAgentBar.webRTCAnswer();
		},
		ketianHangup(){
			CtiAgentBar.hangup();
		},
		ketianStateEventListener(data){
        	console.info(data);
			switch (data.event) {
				case "CB_CONNECT":
					if (data.data.code === 200) {
						//连接成功
						console.info();
					}
					break;
				case "CB_LOGIN":
					if (data.data.code === 200) {
						//登录成功
						console.info("登录成功");
						CtiAgentBar.ready();
					} else {
						// console.log(data.data.message);
						this.$message({message:"坐席登录失败-"+data.data.message,type:'error'});
					}
					break;
				case "CB_LOGOUT":
				//登出成功
				case "CB_READY":
					if (data.data.code === 200) {
						//就绪成功
						console.info("就绪成功");
					}else{
						console.error("就绪%o",data);
						this.$message({message:"坐席就绪失败-"+data.data.message,type:'error'});
					}
					break;
				case "CB_BUSY":
					if (data.data.code === 200) {
						//成功置忙
					} else {
						console.log(data.data.message);
					}
					break;
				case "CB_REST":
					if (data.data.code === 200) {
						//成功设置为小休
					} else {
						console.log(data.data.message);
					}
					break;
				case "CB_PROGRESS":
					if (data.data.code === 200) {
						//成功设置为后处理
					} else {
						console.log(data.data.message);
					}
					break;
				case "CB_RINGING":
					//振铃事件
					var resData = data.data.data;
					console.log(resData); //弹屏数据,具体参数如下
					if(resData.dir=="INBOUND"){
						//显示接听弹窗
						this.ringOffdialogVisible =true;
						this.ketianInBoundPhone = resData.ani+" ("+resData.area+")";
					}else{
						CtiAgentBar.webRTCAnswer();
					}

					break;
				case "CB_ANSWERING":
					console.info("answering");
					//接听事件
					break;
				case "CB_REALTIME":
					//被叫应答
					console.log('-------> 被叫应答 <-------');
					break;
				case "CB_HANGUP":
					//坐席挂机
					console.log('-------> 挂机放音 <-------');
					break;
				case "CB_HOLD":
					//保持
					break;
				case "CB_UNHOLD":
					//取消保持(取回)
					break;
				case "CB_MAKECALL":
					//发起呼叫回调
					if (data.data.code !== 200) {
						//发起呼叫失败
						console.log(data.data.message);
					}
					break;
				case "CB_LISTENCALL":
					//监听回调
					if (data.data.code !== 200) {
						console.log(data.data.message);
					}
					break;
				case "CB_THREEWAY":
					//三方回调
					if(data.data.code === 200){
						//success
					}
					break;
				case "CB_KICKOUT":
					this.$message({message:"您的坐席已在其他地方登陆，本地已强制下线！",type:'error'});
					break;

			}
		},
		ketianClientLoginRecord(param){
			axios.post('/client/ketianClient/clientLoginRecord',param)
				.then(function (response) {
					var data = response.data;
					if(data.code!=0){
						homePageVM.$message({message:"登录坐席失败-"+data.msg,type:'error'});
						return ;
					}
					homePageVM.$message({message:"登录成功",type:'success'});
					homePageVM.callTitle="呼叫中心（科天ON）";
					homePageVM.dialogLoginClientVisible =false;
					homePageVM.isQimoClient=false;
					homePageVM.isTrClient=false;
					homePageVM.isHeliClient=false;
					homePageVM.isKeTianClient=true;
					homePageVM.isRongLianClient = false;

					var clientInfo={};
					clientInfo.loginClientType="ketian";
					clientInfo.clientType = homePageVM.loginClientForm.clientType;
					clientInfo.bindType = homePageVM.loginClientForm.bindPhoneType;
					clientInfo.loginClient = homePageVM.loginClientForm.loginClient;
					localStorage.setItem("clientInfo",JSON.stringify(clientInfo));
					console.info("clientInfo"+JSON.stringify(clientInfo));

				})
				.catch(function (error) {
					console.log(error);
				});
		},
		initCallback(data) {//科天登录初始化
			console.log("data:==>" + JSON.stringify(data));
			if (data.code === 200) {
				console.log(data.msg);
			} else {
				CtiAgentBar.destroy();
			}
		},

      loginRongLianClient(){//容联登录
          var loginClient = this.loginClientForm.loginClient;
          var param={};
          param.loginName = loginClient;
          param.accountType = homePageVM.accountType;
          param.clientType = homePageVM.loginClientForm.clientType;
           axios.post('/client/ronglianClient/login',param)
             .then(function (response) {
                 var data =  response.data;                 
                 if(data.code=='0'){
                    var resData = data.data;
                    homePageVM.$message({message:"登录成功",type:'success'});
                    homePageVM.callTitle="呼叫中心（容联ON）";
                    homePageVM.dialogLoginClientVisible =false;
                    homePageVM.isQimoClient=false;
                    homePageVM.isTrClient=false;
                    homePageVM.isHeliClient=false;
                    homePageVM.isKeTianClient=false;
                    homePageVM.isRongLianClient=true;
                    var clientInfo={};
                    clientInfo.loginClientType="ronglian";
                    clientInfo.loginClient = homePageVM.loginClientForm.loginClient
                    clientInfo.clientType = homePageVM.loginClientForm.clientType;
                    clientInfo.acountType = homePageVM.accountType;
                    localStorage.setItem("clientInfo",JSON.stringify(clientInfo));                     
                }else{
                    console.error(data);
                    homePageVM.$message({message:"登录失败:"+data.msg,type:'error'});
                }
             })
             .catch(function (error) {
                console.log(error);
             })
             .then(function () {
               // always executed
             });
        },
        logoutClient(formName){//坐席退出          
        	if(this.isQimoClient){// 七陌退出
        		 axios.post('/client/client/qimoLogout',{})
                 .then(function (response) {
                     var data =  response.data;
                     if(data.code=='0'){
                    	 homePageVM.dialogLogoutClientVisible =false;
                         homePageVM.$message({message:"退出成功",type:'success'});
                         homePageVM.callTitle="呼叫中心";
                         homePageVM.isQimoClient=false;
                         homePageVM.isTrClient=false;
                         homePageVM.isHeliClient=false;
                         homePageVM.isKeTianClient=false;
                         homePageVM.isRongLianClient=false;
                     	// sessionStorage.removeItem("loginClient");
                     	// sessionStorage.removeItem("accountId");
                         localStorage.removeItem("clientInfo");

                         homePageVM.$message({message:"退出成功",type:'success'});

                         homePageVM.loginClientForm.clientType=1;//设置默认选中天润坐席
                         homePageVM.loginClientForm.bindPhoneType=2;//默认绑定类型是手机外显
                         homePageVM.loginClientForm.cno='';
                         homePageVM.loginClientForm.bindPhone='';
                         homePageVM.loginClientForm.loginClient='';
                         //homePageVM.$refs.loginClientForm.clearValidate();                       
                         
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
        		/* var params = {};
         	    params.logoutMode = 1;
         	    params.removeBinding = 1;
         	    CTILink.Agent.logout(params, function(result) {
         	    	console.info("logout:%o",result);
         	        if (result.code == 0) {
         	      	    homePageVM.dialogLogoutClientVisible =false;
                        homePageVM.isQimoClient=false;
                        homePageVM.isTrClient=false;
                        homePageVM.callTitle="呼叫中心";
                       // sessionStorage.removeItem("loginClient");
                      //  sessionStorage.removeItem("accountId");
                        localStorage.removeItem("clientInfo");
                        homePageVM.$message({message:"退出成功",type:'success'});
                        
                        homePageVM.loginClientForm.clientType=1;//设置默认选中天润坐席
                        homePageVM.loginClientForm.bindPhoneType=1;
                        homePageVM.loginClientForm.cno='';
                        homePageVM.loginClientForm.bindPhone='';
                        homePageVM.loginClientForm.loginClient='';
                        //homePageVM.$refs.loginClientForm.clearValidate();
                        
         	        }else{
         	        	console.info(result);
         	        	homePageVM.$message({message:"退出失败",type:'error'});
         	        }
         	    });*/
        		
         	   this.trClientLogout();
        	}else if(this.isHeliClient){//合力退出
        		this.heliClientLogout();
        	}else if(this.isKeTianClient){//科天退出
                this.KeTianClientLogout();
                homePageVM.isRingOff = false;//退出之后不显示挂断按钮
            }else if(this.isRongLianClient){//容联退出
                var loginClient = this.loginClientForm.loginClient;
                var param={};
                param.loginName = loginClient;
                axios.post('/client/ronglianClient/logout',param)
                 .then(function (response) {
                     var data =  response.data;
                     if(data.code=='0'){
                       homePageVM.dialogLogoutClientVisible =false;
                         homePageVM.$message({message:"退出成功",type:'success'});
                         homePageVM.callTitle="呼叫中心";
                         homePageVM.isQimoClient=false;
                         homePageVM.isTrClient=false;
                         homePageVM.isHeliClient=false;
                         homePageVM.isKeTianClient=false;
                         homePageVM.isRongLianClient=false;
                      // sessionStorage.removeItem("loginClient");
                      // sessionStorage.removeItem("accountId");
                         localStorage.removeItem("clientInfo");
                      
                         
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
        },
        heliClientLogout(){
         var param = {};
         param.clientNo = this.loginClientForm.cno;
   		   axios.post('/client/heliClient/logout',param)
         .then(function (response) {
             var data =  response.data;
             if(data.code=='0'){
            	 homePageVM.dialogLogoutClientVisible =false;
                 homePageVM.$message({message:"退出成功",type:'success'});
                 homePageVM.callTitle="呼叫中心";
                 homePageVM.isQimoClient=false;
                 homePageVM.isTrClient=false;
                 homePageVM.isHeliClient=false;
                 homePageVM.isKeTianClient = false;
                 homePageVM.isRongLianClient = false;
             	// sessionStorage.removeItem("loginClient");
             	// sessionStorage.removeItem("accountId");
                 localStorage.removeItem("clientInfo");
                 
                 homePageVM.loginClientForm.clientType=1;//设置默认选中天润坐席
                 homePageVM.loginClientForm.bindPhoneType=2;
                 homePageVM.loginClientForm.cno='';
                 homePageVM.loginClientForm.bindPhone='';
                 homePageVM.loginClientForm.loginClient='';
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
        trClientLogout(){
        	 var cno = homePageVM.loginClientForm.cno;
        	 var param = {cno:cno};
        	 
        	 axios.post('/client/client/trClientLogout', param)
             .then(function (response) {
                 var resData = response.data;
                 console.info(resData);
                 if(resData.code=='0'){
                	 homePageVM.dialogLogoutClientVisible =false;
                	 homePageVM.isQimoClient=false;
                     homePageVM.isTrClient=false;
                     homePageVM.isHeliClient=false;
                     homePageVM.isKeTianClient =false;
                     homePageVM.isRongLianClient = false;
                     homePageVM.callTitle="呼叫中心";
                    // sessionStorage.removeItem("loginClient");
                   //  sessionStorage.removeItem("accountId");
                     localStorage.removeItem("clientInfo");
                     homePageVM.$message({message:"退出成功",type:'success'});
                     
                     homePageVM.loginClientForm.clientType=1;//设置默认选中天润坐席
                     homePageVM.loginClientForm.bindPhoneType=2;
                     homePageVM.loginClientForm.cno='';
                     homePageVM.loginClientForm.bindPhone='';
                     homePageVM.loginClientForm.loginClient='';
                     //homePageVM.$refs.loginClientForm.clearValidate();
                 }else{
                	 homePageVM.$message({message:'退出失败：'+resData.msg,type:'error'});
              	     console.error(resData);
                 }
             
             })
             .catch(function (error) {
                  console.log(error);
             }).then(function(){
          	      
             });
        	 
        	 
        },
        KeTianClientLogout(){
			CtiAgentBar.logout();
			homePageVM.isRingOff = true;

			homePageVM.dialogLogoutClientVisible =false;
			homePageVM.$message({message:"退出成功",type:'success'});
			homePageVM.callTitle="呼叫中心";
			this.setLoginClientMark();
			localStorage.removeItem("clientInfo");

			homePageVM.loginClientForm.clientType=1;//设置默认选中天润坐席
			homePageVM.loginClientForm.bindPhoneType=2;
			homePageVM.loginClientForm.cno='';
			homePageVM.loginClientForm.bindPhone='';
			homePageVM.loginClientForm.loginClient='';
        },
		setLoginClientMark(){
			homePageVM.isQimoClient=false;
			homePageVM.isTrClient=false;
			homePageVM.isHeliClient=false;
			homePageVM.isKeTianClient = false;
			homePageVM.isRongLianClient = false;
		},
        RongLianClientLogout(){

        },
        openOutboundDialog(){//打开主动外呼diaolog 
        	this.dialogOutboundVisible = true;
        	if(this.outboundDialogMin){//代表上次点击的是最小化
        		$("#outboundCallTimeDiv").show();
        	}else{
        		$('#outboundCallTime').html("");
        		$('#outboundPhoneLocaleArea').html("");
        		this.outboundInputPhone="";
        	}
        	this.outboundDialogMin = false;
        	
        },
        closeOutboundDialog(){
        	//清除计时器
        	clearTimer();
        	this.dialogOutboundVisible = false;
        	//this.outboundDialogMin=false;
        	if(this.outboundDialogMin){//如果是最小化就不执行这个代码
        		return;
        	}else{
        		$('#outboundCallTime').html("");
        	}
        },
        clickOutbound(){//外呼
        	var outboundInputPhone = this.outboundInputPhone;
        	
        	this.outboundCall(outboundInputPhone,1);
        },
        logoutMin(){//退出最小化
        	this.dialogLogoutClientVisible =false;
        },
    	outboundMin(){//外呼最小化
    		this.dialogOutboundVisible = false;
    		this.outboundDialogMin=true;
    	},
    	async outboundCall(outboundInputPhone,callSource,clueId){//外呼
    		var isReturn =false;
        	await axios.post('/call/callRecord/missedCalPhone?phone='+outboundInputPhone, {})
            .then(function (response) {
            	if(response.data !=""){
            		homePageVM.$message({
                        message: response.data,
                        type: 'warning'
                    });
            		isReturn = true; ;
            	}
               ;
            });
        	if(isReturn){
        		return;
        	}
    		outboundCallPhone(outboundInputPhone,callSource,clueId,null);
    		//stopSound();//停止播放录音
    		/*clearTimer();//清除定时器
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
    	 		var bindType = this.loginClientForm.bindPhoneType;
    	 		if(bindType==2){//abx外呼
    	 			this.axbOutboundCall(outboundInputPhone,callSource,clueId);
    	 			return;
    	 		}
    	 		//预览外呼
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
    	 	}*/
    		
    		
    	},
    	closeTmOutboundDialog(){//关闭电销外呼dialog
    		this.tmOutboundCallDialogVisible = false;
    		clearTimer();//清除定时器
    	},
    	postBack(){//接通成功后的回调函数
    		//console.info("postBack");
    	},
    	openConsolePage(){//点击控制台button 事件
      		this.defaultActive= null;
      		$('.menu').css("color","rgb(255, 255, 255)");
          var dataUrl=""
          if(user){
            var roleCode=user.roleCode;
            console.log(roleCode);
            if(roleCode=="DXCYGW"){//电销顾问
              this.isRoleCodeDX=true;//电销顾问
              dataUrl = "/console/console/index?sourceType=1";
            }else if(roleCode=="SWJL"){
              this.isRoleCodeSW=true;//商务经理
              dataUrl = "/console/console/index?sourceType=1";
            }else{
              dataUrl = "/console/console/index?type=1";
            }
          }
      		// var dataUrl = "/console/console/index?type=1";
    			$("#iframeBox").attr({
    				"src":dataUrl //设置ifream地址
    			});
          // 给餐盟首页加高亮
          this.isCurrent=true;
          // 给左侧餐盟菜单取消高亮
          // this.$el.querySelector('.elAsideCm .el-menu-item.is-active').classList.remove("is-active");
    	},
    	validClientNo(cno){//验证坐席是否属于自己
    			var isPass =false;
    			$.ajax({  
    				type: "POST",  
    				url: "/client/client/queryClientInfoByCno",          
    				data: JSON.stringify({clientNo:cno}),   
    				dataType: 'json',     
    				async: false, //设置为同步请求
    				contentType:"application/json",
    				success: function(data){  
    					console.info(data);
    					if(data.code==0){
    						isPass= data.data;
    						if(!isPass){
    							homePageVM.$message({message:"登陆失败，该坐席号不属于您的归属部门",type:'error'});
    						}
    					}else{
    						homePageVM.$message({message:data.msg+"(验证坐席号归属部门)",type:'error'});
    					}
    				},  
    				error: function() {     
    				     
    				}
    			})
    			
    			return isPass;
    		
    	},
    	validHeliClientNo(cno){//验证合力坐席是否属于自己
			var isPass =false;
			$.ajax({  
				type: "POST",  
				url: "/client/heliClient/queryClientInfoByCno",          
				data: JSON.stringify({clientNo:cno}),   
				dataType: 'json',     
				async: false, //设置为同步请求
				contentType:"application/json",
				success: function(data){  
					console.info(data);
					if(data.code==0){
						isPass= data.data;
						if(!isPass){
							homePageVM.$message({message:"登陆失败，该坐席号不属于您的归属部门",type:'error'});
						}
					}else{
						homePageVM.$message({message:data.msg+"(验证坐席号归属部门)",type:'error'});
					}
				},  
				error: function() {     
				     
				}
			})
			
			return isPass;
		
	},
  showTipbg2(){//展示图片2和关闭按钮
    this.isTipbgShow=true;
    this.$el.querySelector('.unionTipdialog .el-dialog__headerbtn').style.display="block";
  },
  closeUnionTipdialog(){//关闭提示框
    this.unionTipdialogVisible=false;
  },
  closeDataBasedialog(){//关闭资料库清空搜索框和搜索结果,默认显示不限
    this.searchDatabaseKeyword="";
    this.dataBaseInvestMoneyVal="0";
    this.dataBaseInvestAreaVal="0";
    this.dataBaseCategoryVal="0";
    this.isshowsearchTip=false;
    this.issearchResult=false;
  }

         
	},
 	created() {
   		document.body.removeChild(document.getElementById('Loading'));
  		this.messageCount();
  		if(isUpdatePassword=="1"){
  			this.dialogModifyPwdVisible=true;
  		}
      // 判断是否展示资料库按钮
      if(isShowDataBase){
        this.isDataBase=true;
      }
      this.searchDataList1();//资料库投资金额list
      this.searchDataList2();//资料库投资区域list
      this.searchDataList3();//资料库意向品类list
      // 通过用户信息判断餐盟菜单显示
      if(user){
        var roleCode=user.roleCode;
        console.log(roleCode);
        if(roleCode=="DXCYGW"){//电销顾问
          this.isRoleCodeDX=true;//电销顾问
        }else if(roleCode=="SWJL"){
          this.isRoleCodeSW=true;//商务经理
        }
      }
      // 首次登陆显示
      if(showIKonwFlag==1){
        this.unionTipdialogVisible=true;
      }
	},
	computed: {
	    clientRules:function() {
	    	var clientType = this.loginClientForm.clientType;
	    	var ruleName="";
	        if(clientType==1){
	    	  return this.trClientFormRules;
	    	}else if(clientType==2){
	    		 return this.qimoClientFormRules;
	    	}else if(clientType==3){
	    		 return this.heliClientFormRules;
	    	}else if(clientType==4){//科天
                 return this.ktClientFormRules;
            }else if(clientType==5){//容联
                 return this.rlClientFormRules;
            }
	    }
	 }
})
// 点击导航赋值ifream的src值
$(function () { 
	//初始化 天润坐席 相关参数
	//documentReady();
	
	// var mainBoxH=$(".elMain").height()-4;
	// 设置ifream高度
	// $("#iframeBox").height(mainBoxH)
	$(document).on('click','.menu',function(){
	    // console.log(0)
	    // console.log($(this).attr("data-url"))
	    var dataUrl=$(this).attr("data-url");
	    $("#iframeBox").attr({
	      "src":dataUrl //设置ifream地址
	    });  
	})
});