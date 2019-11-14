var oLink =  document.getElementById("skinCss");
var oLinkIndex =  document.getElementById("skinCssIndex");
var homePageVM = new Vue({
	el: '#app',
	data: function () {
		var validatePass = (rule, value, callback) => {
			if (value !== this.modifyForm.newPassword) {
				callback(new Error('确认密码与新密码不一致，请重新输入'));
			} else {
				callback();
			}
		};

		return {
			formLabelWidth: '130px',
			formLabelWidth105: '105px',
			isCollapse: false,//侧导航是否展开
			isActive: true,
			dialogModifyPwdVisible: false,//修改密码dialog 是否显示
			dialogLogoutVisible: false,//退出登录 dialog
			skinVal: getCookieVal("skinVal") ? getCookieVal("skinVal") : 1,//1蓝色 //2白色 皮肤切换
			skinStatus: getCookieVal("skinVal")==2 ? true : false,
			modifyForm: {
				'oldPassword': '',
				'newPassword': '',
				'confirmPwd': ''
			},
			hasBuyPackage: hasBuyPackage,
			items: menuList
			/*{ifreamUrl:'a.html',index:'1-1',name:"数据演示1"},
			{ifreamUrl:'b.html',index:'1-2',name:"数据演示2"}*/
			,
			user: user,//用戶信息
			defaultOpeneds: showBtnIndex,//默认打开菜单索引
			initUrl: showMenuUrl,//默认展示页面
			// defaultActive:'0-0',//默认激活菜单
			defaultActive: defaultActive,//默认激活菜单
			modifyFormRules: {
				oldPassword: [
					{ required: true, message: '当前密码不能为空', trigger: 'blur' },
					{ min: 6, max: 30, message: '长度在 6 到 30个字符' },
					{ pattern: /^[0-9a-zA-Z]*$/, message: '只允许输入字母/数字', trigger: 'blur' }
				],
				newPassword: [
					{ required: true, message: '新密码不能为空', trigger: 'blur' },
					{ min: 6, max: 30, message: '长度在 6 到 30个字符', trigger: 'blur' },
					{ pattern: /^[0-9a-zA-Z]*$/, message: '只允许输入字母/数字', trigger: 'blur' }
				],
				confirmPwd: [
					{ required: true, message: '确认密码不能为空', trigger: 'blur' },
					{ min: 6, max: 30, message: '长度在 6 到 30个字符', trigger: 'blur' },
					{ validator: validatePass, trigger: 'blur' }
				]

			},
			isLogin: false,//坐席是否登录
			isTrClient: false,//天润坐席是否登录
			isQimoClient: false,//七陌坐席是否登录
			isHeliClient: false,//合力坐席是否登录
			callTitle: '',
			dialogLoginClientVisible: false,//登录坐席dialog 
			dialogLogoutClientVisible: false,
			iframeWin: {},
			loginClientForm: {
				clientType: 1,
				cno: '',
				bindPhone: '',
				bindPhoneType: 2,
				loginClient: ''

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
			}],
			bindPhoneTypeOptions: [
				{
					value: 2,
					label: '手机外显'
				}, {
					value: 1,
					label: '普通电话'
				}],
			trClientFormRules: {//登录坐席校验规则
				clientType: [
					{ required: true, message: '选择呼叫中心不能为空' }
				],
				cno: [
					{ required: true, message: '坐席号不能为空' },
					{
						validator: function (rule, value, callback) {
							if (!/^[0-9]*$/.test(value)) {
								callback(new Error("只可以输入数字,不超过10位"));
							} else {
								callback();
							}

						}, trigger: 'blur'
					},
				],
				bindPhone: [
					{ required: true, message: '绑定电话不能为空' },
					{
						validator: function (rule, value, callback) {
							if (!/^[0-9]*$/.test(value)) {
								callback(new Error("只可以输入数字,不超过11位"));
							} else {
								callback();
							}

						}, trigger: 'blur'
					},
				],
				bindPhoneType: [
					{ required: true, message: '绑定类型不能为空' }
				]

			},
			qimoClientFormRules: {
				clientType: [
					{ required: true, message: '选择呼叫中心不能为空' }
				],
				loginClient: [
					{ required: true, message: '登录坐席不能为空' },
					{
						validator: function (rule, value, callback) {
							if (!/^[0-9]*$/.test(value)) {
								callback(new Error("只可以输入数字,不超过10位"));
							} else {
								callback();
							}

						}, trigger: 'blur'
					},
				],
				bindPhoneType: [
					{ required: true, message: '绑定类型不能为空' }
				]
			},
			heliClientFormRules: {//登录坐席校验规则
				clientType: [
					{ required: true, message: '选择呼叫中心不能为空' }
				],
				cno: [
					{ required: true, message: '坐席号不能为空' },
					{
						validator: function (rule, value, callback) {
							if (!/^[0-9]*$/.test(value)) {
								callback(new Error("只可以输入数字,不超过10位"));
							} else {
								callback();
							}

						}, trigger: 'blur'
					},
				]
			},
			/*  clientRules:'trClientFormRules',*/
			enterpriseId: enterpriseId,
			token: token,
			dialogOutboundVisible: false,//外呼dialog
			outboundInputPhone: '',//外呼时手机号
			accountId: accountId,//登陆者ID
			outboundDialogMin: false,//外呼dialog 是否最小化
			tmOutboundCallDialogVisible: false,//电销页面外呼 dialog 
			consoleBtnVisible: isShowConsoleBtn,//控制台按鈕是否可見
			accountType: accountType,
		}
	},
	methods: {
		// 输入数字控制方法
		cnoNumber() {
			this.loginClientForm.cno = this.loginClientForm.cno.replace(/[^\.\d]/g, '');
			this.loginClientForm.cno = this.loginClientForm.cno.replace('.', '');
		},
		bindPhoneNumber() {
			this.loginClientForm.bindPhone = this.loginClientForm.bindPhone.replace(/[^\.\d]/g, '');
			this.loginClientForm.bindPhone = this.loginClientForm.bindPhone.replace('.', '');
		},
		handleMin() {
			this.dialogLoginClientVisible = false;
		},
		handleOutMin() {
			this.dialogLogoutClientVisible = false;
		},
		handleOpen(key, keyPath) {
			// console.log(key, keyPath);
		},
		handleClose(key, keyPath) {
			// console.log(key, keyPath);
		},
		toogleClick() {
			if (this.isCollapse) {
				this.isCollapse = false
				this.isActive = true
			} else {
				this.isCollapse = true
				this.isActive = false
			}
		},
		//切换皮肤
		changeSkin() {
			 this.skinStatus = !this.skinStatus;
			 if(this.skinStatus==false){
				 this.skinVal=1;
			 }else if(this.skinStatus==true){
				this.skinVal=2;
			 }
			 setSessionStore("skinVal", this.skinVal);
			 document.cookie="skinVal="+this.skinVal+"; expires=Thu, 18 Dec 2043 12:00:00 GMT";
			 console.log(getCookieVal("skinVal"),"222");
			 this.iframeWin.postMessage({
				cmd: 'getFormJson',
				params: {
					success:true,
					data:this.skinVal
				}
			}, '*')
			// index切换皮肤
			oLink['href'] = "/css/common/merchant_base" + getCookieVal("skinVal") + ".css";
			oLinkIndex['href'] = "/css/custom/cheranthomepage/index" + getCookieVal("skinVal") + ".css";			
		},
		menuClick: function (ifreamUrl) {
			$(".menu.is-active").removeClass("is-active")
			this.$refs.iframeBox.src = ifreamUrl //给ifream的src赋值
			window.sessionStorage.clear(); // 点击侧边栏-清除所有cookie
		},
		handleCommand(command) {//点击下拉菜单
			if (command == 'modifyPwd') {//用户信息
				this.modifyPwd();
			} else if (command == 'logout') {//退出
				this.logout();
			}
		},
		modifyPwd() {//用户信息
			//重置表单
			//  this.dialogModifyPwdVisible=true;
			// document.location.href = '/merchant/userManager/userInfo';
			var dataUrl = "/merchant/userManager/userInfo";
			$("#iframeBox").attr({
				"src": dataUrl //设置ifream地址
			});
		},
		gotoCloudCall() {//云呼叫
			var dataUrl = "/merchant/call/package/index";
			$("#iframeBox").attr({
				"src": dataUrl //设置ifream地址
			});
		},
		logout() {
			this.dialogLogoutVisible = true;
			this.$refs.loginClientForm.resetFields();
		},
		cancelModifyForm(formName) {//取消修改密码
			this.$refs[formName].resetFields();
			homePageVM.dialogModifyPwdVisible = false;
		},
		saveModifyForm(formName) {//保存
			this.$refs[formName].validate((valid) => {
				if (valid) {
					var param = this.modifyForm;
					axios.post('/user/userManager/updatePwd', param)
						.then(function (response) {
							var resData = response.data;
							if (resData.code == '0') {
								homePageVM.$message({ message: '请使用新密码重新登录', type: 'success' });
								homePageVM.cancelModifyForm(formName);

								setTimeout(function () {//去登录页
									homePageVM.gotoHomePage();
								}, 2000);
							} else {
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
		confirmLogout() {//确认退出系统
			window.sessionStorage.clear();//清除缓存
			setSessionStore("skinVal", this.skinVal);
			location.href = "/index/logout";
			document.cookie="skinVal="+this.skinVal+"; expires=Thu, 18 Dec 2043 12:00:00 GMT";
			
		},
		gotoHomePage() {//首页跳转
			location.href = '/login';
		},
		messageCount() {
			console.log("请求消息中心未读条数")
			var param = {};
			axios.post("/messagecenter/unreadCount", param).then(function (response) {
				var data = response.data.data
				var annCount = 0;
				var str = "";
				if (data && data != 0) {
					annCount = data;
					str = '<em class="messageCount">' + annCount + '</em>'
				}
				document.getElementById("messageCount").innerHTML = str;
			})
		},
		openMessageCenter() {
			console.log("跳转消息中心")
			var dataUrl = "/merchant/messcenter/topage";
			$("#iframeBox").attr({
				"src": dataUrl //设置ifream地址
			});
		},
		closeModifyPwdDialog() {
			this.$refs.modifyForm.resetFields();
		},
		openLoginClientDialog() {//打开登录坐席dialog
			console.log(this.isQimoClient, this.isTrClient, this.isTrClient);

			if (this.isQimoClient) {
				this.loginClientForm.clientType = 2;
				this.dialogLogoutClientVisible = true;
			} else if (this.isTrClient) {
				this.loginClientForm.clientType = 1;
				this.dialogLogoutClientVisible = true;
			} else if (this.isHeliClient) {
				this.loginClientForm.clientType = 3;
				this.dialogLogoutClientVisible = true;
			} else {
				if (this.$refs.loginClientForm !== undefined) {
					this.$refs.loginClientForm.resetFields();
					this.$refs.loginClientForm.clearValidate(function () {

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
		cancelLoginClientForm() {
			this.dialogLoginClientVisible = false;
			if (this.$refs.loginClientForm !== undefined) {
				this.$refs.loginClientForm.clearValidate();
			}

			this.loginClientForm.clientType = 1;//设置默认选中天润坐席
			this.loginClientForm.bindPhoneType = 2;
			this.loginClientForm.cno = '';
			this.loginClientForm.bindPhone = '';
			this.loginClientForm.loginClient = '';

		},
		changeClientType(selectedValue) {
			this.$refs.loginClientForm.resetFields();
			this.$refs.loginClientForm.clearValidate();
			this.loginClientForm.clientType = selectedValue;
			if (selectedValue == 2) {//七陌
				this.loginClientForm.bindPhoneType = 1;
			} else if (selectedValue == 1 || selectedValue == 3) {//天润 合力
				this.loginClientForm.bindPhoneType = 2;

			}
		},
		loginClient(formName) {
			this.$refs[formName].validate((valid) => {
				if (valid) {
					var clientType = this.loginClientForm.clientType;
					if (clientType == 1) {//天润坐席登录
						this.loginTrClient();
					} else if (clientType == 2) {
						this.loginQimoClient();
					} else if (clientType == 3) {
						this.loginHeliClient();
					}

				} else {
					return false;
				}
			});


		},
		loginHeliClient() {//合力 登录
			var bindType = this.loginClientForm.bindPhoneType;
			if (bindType == 1) {
				this.$message({ message: "合力不支持普通电话模式登录！", type: 'warning' });
				return;
			}
			var cno = this.loginClientForm.cno;
			//验证坐席是否属于自己
			if (!this.validHeliClientNo(cno)) {
				return;
			}
			var param = {};
			param.bindType = bindType + "";
			param.clientNo = cno;
			param.accountType = homePageVM.accountType;
			param.clientType = homePageVM.loginClientForm.clientType;
			axios.post('/client/heliClient/login', param)
				.then(function (response) {
					var data = response.data;

					if (data.code == '0') {
						var resData = data.data;
						homePageVM.$message({ message: "登录成功", type: 'success' });
						homePageVM.callTitle = "呼叫中心（合力ON）";
						homePageVM.dialogLoginClientVisible = false;
						homePageVM.isHeliClient = true;
						homePageVM.isTrClient = false;
						homePageVM.isQimoClient = false;
						//sessionStorage.setItem("loginClient","qimo");
						//sessionStorage.setItem("accountId",homePageVM.accountId);
						var clientInfo = {};
						clientInfo.loginClientType = "heli";
						clientInfo.clientNo = homePageVM.loginClientForm.cno;
						clientInfo.clientType = homePageVM.loginClientForm.clientType;
						clientInfo.bindType = homePageVM.loginClientForm.bindPhoneType;
						localStorage.setItem("clientInfo", JSON.stringify(clientInfo));

					} else {
						console.error(data);
						homePageVM.$message({ message: "合力坐席登录：" + data.msg, type: 'error' });
					}
				})
				.catch(function (error) {
					console.log(error);
				})
				.then(function () {
					// always executed
				});


		},
		loginQimoClient() {//七陌登录
			var loginClient = this.loginClientForm.loginClient;
			var bindType = this.loginClientForm.bindPhoneType;

			/*if(bindType==1){
				this.$message({message:"七陌不支持普通电话模式登录！",type:'warning'});
				return;
			}*/
			var param = {};
			// param.bindType = bindType + "";
			// param.loginName = loginClient;
			// param.accountType = homePageVM.accountType;
			param.accountType = '3';
			// param.clientType = homePageVM.loginClientForm.clientType;
			axios.post('/merchant/merchantClient/clientLogin', param)
				.then(function (response) {
					var data = response.data;

					if (data.code == '0') {
						var resData = data.data;
						homePageVM.$message({ message: "登录成功", type: 'success', duration: 1500 });
						homePageVM.callTitle = "呼叫中心（七陌ON）";
						homePageVM.dialogLoginClientVisible = false;
						homePageVM.isQimoClient = true;
						homePageVM.isTrClient = false;
						homePageVM.isHeliClient = false;
						//sessionStorage.setItem("loginClient","qimo");
						//sessionStorage.setItem("accountId",homePageVM.accountId);
						var clientInfo = {};
						clientInfo.loginClientType = "qimo";
						clientInfo.loginClient = homePageVM.loginClientForm.loginClient
						clientInfo.clientType = homePageVM.loginClientForm.clientType;
						clientInfo.bindType = homePageVM.loginClientForm.bindPhoneType;
						// localStorage.setItem("clientInfo", JSON.stringify(clientInfo));

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

					} else {
						console.error(data);
						homePageVM.$message({ message: data.msg, type: 'error' });
					}
				})
				.catch(function (error) {
					console.log(error);
				})
				.then(function () {
					// always executed
				});
		},
		loginTrClient() {//天润登录
			var cno = this.loginClientForm.cno;
			//验证坐席是否属于自己
			if (!this.validClientNo(cno)) {
				return;
			}
			var bindType = this.loginClientForm.bindPhoneType;
			if (bindType == 1) {
				//tr 普通登陸
				var whiteList = [
					915157337459918, 915157338827184, 915157339205499, 915157339500788
					, 915157339774915, 915390540464159, 115450274113949, 115450274246648
					, 115450529284536, 115451100381743, 915319637549027, 915319637639747
					, 915319638079196, 915320465997397, 915320621197701, 214991403833604
					, 115410338236403, 115565818271983, 115622112138370, 1154293728744181760
					, 1149232865020612608, 1149232925418590208, 1149232985145479168, 1149233047493808128
					, 1149636656941375488, 1149232583238885376
				];
				var curOrgId = this.user.orgId;
				var isLimit = false;
				for (var i = 0; i < whiteList.length; i++) {
					if (curOrgId == whiteList[i]) {
						isLimit = true;
						break;
					}
				}
				if (!isLimit) {
					//限制普通電話登陸
					this.$message({ message: "天润不支持普通电话模式登录！", type: 'warning' });
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
				$.post("/client/client/destroy/" + cno);
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
			console.info("tr_client login url" + url);
			var timestamp = Date.parse(new Date()) / 1000;
			agentSign = agentSign + timestamp;

			if (token != '') {
				agentSign = agentSign + token;
			} else {
				var _msg = "登录失败！token不可以为空";
				homePageVM.$message({ message: _msg, type: 'error' });
				return false;
			}

			agentSign = hex_md5(agentSign);
			url += "&timestamp=" + timestamp + "&sign=" + agentSign;
			console.log(url);

			$.ajax({
				type: 'get',
				url: url,
				dataType: 'jsonp',
				jsonp: 'callback',
				success: function (r) {
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
						TOOLBAR.login(params, function (token) {

							if (token.code == 0) {
								//座席登录成功
								homePageVM.$message({ message: '登录成功', type: 'success' });
								homePageVM.callTitle = "呼叫中心（天润ON）";
								homePageVM.dialogLoginClientVisible = false;
								homePageVM.isQimoClient = false;
								homePageVM.isTrClient = true;
								homePageVM.isHeliClient = false;
								//sessionStorage.setItem("loginClient","tr");
								// sessionStorage.setItem("accountId",homePageVM.accountId);

								var clientInfo = {};
								clientInfo.loginClientType = "tr";
								clientInfo.clientType = homePageVM.loginClientForm.clientType;
								clientInfo.bindTel = homePageVM.loginClientForm.bindPhone;
								clientInfo.bindType = homePageVM.loginClientForm.bindPhoneType;
								clientInfo.cno = homePageVM.loginClientForm.cno;
								clientInfo.enterpriseId = homePageVM.enterpriseId;
								clientInfo.token = homePageVM.token;
								localStorage.setItem("clientInfo", JSON.stringify(clientInfo));
								console.info("clientInfo" + JSON.stringify(clientInfo));
								var recordParam = {};
								recordParam.clientType = homePageVM.loginClientForm.clientType;
								recordParam.bindPhone = bindPhone;
								recordParam.cno = cno;
								recordParam.accountType = homePageVM.accountType;
								//记录坐席登录
								axios.post('/client/client/clientLoginRecord', recordParam)
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
								homePageVM.$message({ message: _msg, type: 'error' });
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
						homePageVM.$message({ message: _msg, type: 'error' });
						console.error(r);
					}
				},
				error: function (r) {
					alert(r);
				}
			})


		},
		logoutClient(formName) {//坐席退出
			if (this.isQimoClient) {
				axios.post('/client/client/qimoLogout', {})
					.then(function (response) {
						var data = response.data;
						if (data.code == '0') {
							homePageVM.dialogLogoutClientVisible = false;
							homePageVM.$message({ message: "退出成功", type: 'success' });
							homePageVM.callTitle = "呼叫中心";
							homePageVM.isQimoClient = false;
							homePageVM.isTrClient = false;
							homePageVM.isHeliClient = false;
							// sessionStorage.removeItem("loginClient");
							// sessionStorage.removeItem("accountId");
							localStorage.removeItem("clientInfo");


						} else {
							homePageVM.$message({ message: data.msg, type: 'error' });
						}
					})
					.catch(function (error) {
						console.log(error);
					})
					.then(function () {
						// always executed
					});
			} else if (this.isTrClient) {//天润
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
			} else if (this.isHeliClient) {
				this.heliClientLogout();
			}
		},
		heliClientLogout() {
			var param = {};
			param.clientNo = this.loginClientForm.cno;
			axios.post('/client/heliClient/logout', param)
				.then(function (response) {
					var data = response.data;
					if (data.code == '0') {
						homePageVM.dialogLogoutClientVisible = false;
						homePageVM.$message({ message: "退出成功", type: 'success' });
						homePageVM.callTitle = "呼叫中心";
						homePageVM.isQimoClient = false;
						homePageVM.isTrClient = false;
						homePageVM.isHeliClient = false;
						// sessionStorage.removeItem("loginClient");
						// sessionStorage.removeItem("accountId");
						localStorage.removeItem("clientInfo");

						homePageVM.loginClientForm.clientType = 1;//设置默认选中天润坐席
						homePageVM.loginClientForm.bindPhoneType = 2;
						homePageVM.loginClientForm.cno = '';
						homePageVM.loginClientForm.bindPhone = '';
						homePageVM.loginClientForm.loginClient = '';
					} else {
						homePageVM.$message({ message: data.msg, type: 'error' });
					}
				})
				.catch(function (error) {
					console.log(error);
				})
				.then(function () {
					// always executed
				});
		},
		trClientLogout() {
			var cno = homePageVM.loginClientForm.cno;
			var param = { cno: cno };

			axios.post('/client/client/trClientLogout', param)
				.then(function (response) {
					var resData = response.data;
					console.info(resData);
					if (resData.code == '0') {
						homePageVM.dialogLogoutClientVisible = false;
						homePageVM.isQimoClient = false;
						homePageVM.isTrClient = false;
						homePageVM.isHeliClient = false;
						homePageVM.callTitle = "呼叫中心";
						// sessionStorage.removeItem("loginClient");
						//  sessionStorage.removeItem("accountId");
						localStorage.removeItem("clientInfo");
						homePageVM.$message({ message: "退出成功", type: 'success' });

						homePageVM.loginClientForm.clientType = 1;//设置默认选中天润坐席
						homePageVM.loginClientForm.bindPhoneType = 2;
						homePageVM.loginClientForm.cno = '';
						homePageVM.loginClientForm.bindPhone = '';
						homePageVM.loginClientForm.loginClient = '';
						//homePageVM.$refs.loginClientForm.clearValidate();
					} else {
						homePageVM.$message({ message: '退出失败：' + resData.msg, type: 'error' });
						console.error(resData);
					}

				})
				.catch(function (error) {
					console.log(error);
				}).then(function () {

				});


		},
		openOutboundDialog() {//打开主动外呼diaolog 
			this.dialogOutboundVisible = true;
			if (this.outboundDialogMin) {//代表上次点击的是最小化
				$("#outboundCallTimeDiv").show();
			} else {
				$('#outboundCallTime').html("");
				$('#outboundPhoneLocaleArea').html("");
				this.outboundInputPhone = "";
			}
			this.outboundDialogMin = false;

		},
		closeOutboundDialog() {
			//清除计时器
			clearTimer();
			this.dialogOutboundVisible = false;
			//this.outboundDialogMin=false;
			if (this.outboundDialogMin) {//如果是最小化就不执行这个代码
				return;
			} else {
				$('#outboundCallTime').html("");
			}
		},
		clickOutbound() {//外呼
			var outboundInputPhone = this.outboundInputPhone;

			this.outboundCall(outboundInputPhone, 1);
		},
		logoutMin() {//退出最小化
			this.dialogLogoutClientVisible = false;
		},
		outboundMin() {//外呼最小化
			this.dialogOutboundVisible = false;
			this.outboundDialogMin = true;
		},
		outboundCall(outboundInputPhone, callSource, clueId) {//外呼
			axios.post('/merchant/seatManager/queryListBySubMerchant')
				.then(function (response) {
					if (response.data.data) {
						outboundCallPhone(outboundInputPhone, callSource, clueId, null);
					} else {
						homePageVM.$message({ message: '此账户未绑定坐席', type: 'warning' });
					}
				})
				.catch(function (error) {
					console.log(error);
				})
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
		closeTmOutboundDialog() {//关闭电销外呼dialog
			this.tmOutboundCallDialogVisible = false;
			clearTimer();//清除定时器
		},
		postBack() {//接通成功后的回调函数
			//console.info("postBack");
		},
		openConsolePage() {//点击控制台button 事件
			this.defaultActive = null;
			$('.menu').css("color", "rgb(255, 255, 255)");
			var dataUrl = "/console/console/index?type=1";
			$("#iframeBox").attr({
				"src": dataUrl //设置ifream地址
			});

		},
		openCostList() {//打开充值记录页面
			$(".menu.is-active").removeClass("is-active")
			console.log($(".menu .name").html())
			var a = $(".menu");
			a.each(function () {
				console.log($(this).text())
				if ($(this).text() == "充值记录") {
					$(this).addClass("is-active")
				}
			});
			var dataUrl = "/merchant/merchantRechargeRecordBusiness/initRechargeRecordBusiness";
			$("#iframeBox").attr({
				"src": dataUrl //设置ifream地址
			});
		},
		openPaymentOnline() {//打开在线充值页面
			$(".menu.is-active").removeClass("is-active")
			console.log($(".menu .name").html())
			var a = $(".menu");
			a.each(function () {
				console.log($(this).text())
				if ($(this).text() == "在线充值") {
					$(this).addClass("is-active")
				}
			});
			var dataUrl = "/merchant/merchantOnlineRecharge/initOnlineRecharge";
			$("#iframeBox").attr({
				"src": dataUrl //设置ifream地址
			});
		},
		validClientNo(cno) {//验证坐席是否属于自己
			var isPass = false;
			$.ajax({
				type: "POST",
				url: "/client/client/queryClientInfoByCno",
				data: JSON.stringify({ clientNo: cno }),
				dataType: 'json',
				async: false, //设置为同步请求
				contentType: "application/json",
				success: function (data) {
					console.info(data);
					if (data.code == 0) {
						isPass = data.data;
						if (!isPass) {
							homePageVM.$message({ message: "登陆失败，该坐席号不属于您的归属部门", type: 'error' });
						}
					} else {
						homePageVM.$message({ message: data.msg + "(验证坐席号归属部门)", type: 'error' });
					}
				},
				error: function () {

				}
			})

			return isPass;

		},
		validHeliClientNo(cno) {//验证合力坐席是否属于自己
			var isPass = false;
			$.ajax({
				type: "POST",
				url: "/client/heliClient/queryClientInfoByCno",
				data: JSON.stringify({ clientNo: cno }),
				dataType: 'json',
				async: false, //设置为同步请求
				contentType: "application/json",
				success: function (data) {
					console.info(data);
					if (data.code == 0) {
						isPass = data.data;
						if (!isPass) {
							homePageVM.$message({ message: "登陆失败，该坐席号不属于您的归属部门", type: 'error' });
						}
					} else {
						homePageVM.$message({ message: data.msg + "(验证坐席号归属部门)", type: 'error' });
					}
				},
				error: function () {

				}
			})

			return isPass;

		},
	

	},
	handleMessage (event) {
		// 根据上面制定的结构来解析iframe内部发回来的数据
		const data = event.data
		switch (data.cmd) {
		  case 'returnFormJson':
			// 业务逻辑
			break
		  case 'returnHeight':
			// 业务逻辑
			break
		}
	  },
	created() {
		if(getCookieVal("skinVal")){
			oLink['href'] = "/css/common/merchant_base" + getCookieVal("skinVal") + ".css";
			oLinkIndex['href'] = "/css/custom/cheranthomepage/index" + getCookieVal("skinVal") + ".css";
		}else{
			oLink['href'] = "/css/common/merchant_base1.css";
			oLinkIndex['href'] = "/css/custom/cheranthomepage/index1.css";
		}		
		if (this.hasBuyPackage) {
			this.loginQimoClient();
		}
		document.body.removeChild(document.getElementById('Loading'));
		this.messageCount();

		if (isUpdatePassword == "1") {
			this.dialogModifyPwdVisible = true;
		}
		console.log(document.cookie,"3333");
		
	},
	mounted () {
		// 在外部vue的window上添加postMessage的监听，并且绑定处理函数handleMessage
		window.addEventListener('message', this.handleMessage);
		this.iframeWin = this.$refs.iframeBox.contentWindow;
	  },
	computed: {
		clientRules: function () {
			var clientType = this.loginClientForm.clientType;
			var ruleName = "";
			if (clientType == 1) {
				return this.trClientFormRules;
			} else if (clientType == 2) {
				return this.qimoClientFormRules;
			} else if (clientType == 3) {
				return this.heliClientFormRules;
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
	$(document).on('click', '.menu', function () {
		// console.log(0)
		// console.log($(this).attr("data-url"))
		var dataUrl = $(this).attr("data-url");
		$("#iframeBox").attr({
			"src": dataUrl //设置ifream地址
		});
	})
});