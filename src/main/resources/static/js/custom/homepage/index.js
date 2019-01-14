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
		   	modifyFormRules:{
		   		oldPassword:[
		   		    { required: true, message: '当前密码不能为空',trigger:'blur'},
		   		    { min: 6, max: 30, message: '长度在 6 到 30个字符', trigger: 'blur' },
		   		    {pattern:'/^[0-9a-zA-Z]*$/',message:'只允许输入字母/数字',trigger:'blur'}	
		   		  ],
		   		newPassword:[
		   			{ required: true, message: '新密码不能为空',trigger:'blur'},
		   		    { min: 6, max: 30, message: '长度在 6 到 30个字符', trigger: 'blur' },
		   		    {pattern:'/^[0-9a-zA-Z]*$/',message:'只允许输入字母/数字',trigger:'blur'}	
		   		 ],
		   		 confirmPwd:[
		   			{ required: true, message: '确认密码不能为空',trigger:'blur'},
		   		    { min: 6, max: 30, message: '长度在 6 到 30个字符', trigger: 'blur' },
		   		    { validator: validatePass, trigger: 'blur' }
		   		 ]
		   		
		   	}
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
	    	console.log(this)
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
	    	 this.dialogModifyPwdVisible=true;
	     },
	     logout(){
	    	 this.dialogLogoutVisible=true;
	     },
	     cancelModifyForm(formName){
         	this.$refs[formName].resetFields();
         	 homePageVM.dialogModifyPwdVisible = false;
         },
         saveModifyForm(formName){//保存
        	 this.$refs[formName].validate((valid) => {
                 if (valid) {
                    var param=this.form;
                   axios.post('/user/userManager/updatePassword', param)
                   .then(function (response) {
                       var resData = response.data;
                       if(resData.code=='0'){
                    	   homePageVM.$message('请使用新密码重新登录的提示框');
                    	   homePageVM.cancelForm(formName);
                    	   
                    	   setTimeout('gotoHomePage',2000);
                       }else{
                    	   homePageVM.$message(resData.msg);
                           console.error(resData);
                       }
                   
                   })
                   .catch(function (error) {
                        console.log(error);
                   });
                    
                 } else {
                   return false;
                 }
               });
         },
         confirmLogout(){//确认退出系统
        	 location.href="/logout";
        },
		messageCount(){
	    	console.log("请求消息中心未读条数")
			var param = {};
			axios.post("/messagecenter/unreadCount", param).then(function (response) {
				var data = response.data.data
				var annCount = 0;
				if(data&&data!=0){
					annCount = data;
				}
				document.getElementById("messageCount").innerHTML="&nbsp;(&nbsp;"+(annCount)+"&nbsp;)"
			})
		},
		openMessageCenter(){
	    	console.log("跳转消息中心")
	    	var dataUrl = "/messagecenter/messageCenter";
			$("#iframeBox").attr({
				"src":dataUrl //设置ifream地址
			});
        }
  	},
   	created() {
  		console.log("created method")
   		document.body.removeChild(document.getElementById('Loading'))
		this.messageCount();
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