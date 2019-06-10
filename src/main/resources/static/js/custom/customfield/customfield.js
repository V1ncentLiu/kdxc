var fieldMenuVM = new Vue({
        el: '#userManage',
        data: function() {
            return {
                dataTable:[],
                pager:{
                    total: 0,
                    currentPage: 1,
                    pageSize: 20,
                },
                multipleSelection: [],
                dialogFormVisible: false,
                form: {
                	id:'',
                    menuCode:'',
                    menuName:'',
                    beanName:''
                },
                oldMenuCode:'',//旧 菜单吗
                oldMenuName:'',//旧 菜单名称
                formLabelWidth: '150px',
                inputMenuName:'',//菜单搜索框
                addOrModifyDialogTitle:'',
                confirmBtnDisabled:false,//提交按钮 是否禁用
                submitUrl:'',//提交时url
                rules:{
                	menuCode:[
                		{ required: true, message: '菜单代码不能为空',trigger:'blur'},
                		{validator:function(rule,value,callback){
                			  var id = fieldMenuVM.form.id;
                              var name = fieldMenuVM.oldMenuCode;
                              if(id && value==name){
                                  callback();
                              }
                			
                		     var param = {'menuCode':value};
                             axios.post('/customfield/customField/isExistsFieldMenu',param)
                             .then(function (response) {
                                 var data =  response.data;
                                 if(data.code=='0'){
                                     var resData = data.data;
                                     if(resData){
                                         callback(new Error("此菜单代码已存在，请修改后提交"));
                                     }else{
                                         callback();
                                     }
                                     
                                 }else{
                                      callback(new Error("查询菜单代码是否唯一报错"));
                                 }
                             })
                             .catch(function (error) {
                               console.log(error);
                             })
                             .then(function () {
                               // always executed
                             });
                			
                		},trigger:'blur'}
                		],
                	menuName:[
                		  { required: true, message: '菜单名称不能为空',trigger:'blur'},
                		  {validator:function(rule,value,callback){
                			     var id = fieldMenuVM.form.id;
                                 var name = fieldMenuVM.oldMenuName;
                                 if(id && value==name){
                                     callback();
                                 }
                			    var param = {'menuName':value};
                                axios.post('/customfield/customField/isExistsFieldMenu',param)
                                .then(function (response) {
                                    var data =  response.data;
                                    if(data.code=='0'){
                                        var resData = data.data;
                                        if(resData){
                                            callback(new Error("此菜单名称已存在，请修改后提交"));
                                        }else{
                                            callback();
                                        }
                                        
                                    }else{
                                         callback(new Error("查询菜单名称是否唯一报错"));
                                    }
                                })
                                .catch(function (error) {
                                  console.log(error);
                                })
                                .then(function () {
                                  // always executed
                                });
                			  
                		  },trigger:'blur'}
                		
                		
                	]
                
                		
                	
                }//rules end
            }        	  
        },
        methods: {
        	cancelForm(formName) {
                this.$refs[formName].resetFields();
            	this.dialogFormVisible = false;
            },
            deleteFieldMenu() {
            	
            	   var rows = this.multipleSelection;
                   if(rows.length==0){
                       this.$message({
                          message: '请选择删除数据',
                          type: 'warning'
                        });
                       return;
                   }
                   var rowNames = [];
                   var rowIds = [];
                   for(var i=0;i<rows.length;i++){
                	   var curRow = rows[i];
                       rowIds.push(curRow.id);
                       rowNames.push("【"+curRow.menuName+"】");
                   }
                   
                   
                   var menuName = rowNames.join(" ");
                this.$confirm('确定要删除 '+menuName+' 菜单吗？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                	  //删除
                	var param  = {idList:rowIds};
                    axios.post('/customfield/customField/deleteMenu',param)
                    .then(function (response) {
                        var data =  response.data;
                        if(data.code=='0'){
                        	
                        	fieldMenuVM.$message({message:'删除成功',type:'success',duration:2000,onClose:function(){
                        		fieldMenuVM.initCustomFiledMenu();
                    	    }});
                            
                        }else{
                        	fieldMenuVM.$message({
                                message: "接口调用失败",
                                type: 'error'
                              }); 
                        }
                    })
                    .catch(function (error) {
                      console.log(error);
                    })
                    .then(function () {
                    	
                    });
                	
                	
                   
                }).catch(() => {
                       
                });
            },
            saveFieldMenu(formName){//保存自定义字段
                this.$refs[formName].validate((valid) => {
                    if (valid) {
                    	//fieldMenuVM.$refs.confirmBtn.disabled=true;
                    	fieldMenuVM.confirmBtnDisabled=true;//禁用提交按钮
                       var param=this.form;
                      axios.post('/customfield/customField/'+this.submitUrl, param)
                      .then(function (response) {
                          var resData = response.data;
                          if(resData.code=='0'){
                        	  fieldMenuVM.cancelForm(formName);
                        	  fieldMenuVM.$message({message:'操作成功',type:'success',duration:2000,onClose:function(){
                        		  fieldMenuVM.initCustomFiledMenu();
                        	    }});
                              
                          }else{
                              fieldMenuVM.$message('操作失败');
                              console.error(resData);
                          }
                      
                      })
                      .catch(function (error) {
                        fieldMenuVM.confirmBtnDisabled=false;//启用提交按钮
                        console.log(error);
                      }).then(function(){
                    		//fieldMenuVM.$refs.confirmBtn.disabled=false;
                    		fieldMenuVM.confirmBtnDisabled=false;//启用提交按钮
                      });
                       
                    } else {
                      return false;
                    }
                  });
            	

            },// method end
            handleSelectionChange(val) {
                this.multipleSelection = val;
            },
            modifyFiledMenu(){//点击修改菜单页面按钮
            	var rows = this.multipleSelection;
                if(rows.length!=1){
                    this.$message({
                       message: '请选择一条数据进行修改',
                       type: 'warning'
                     });
                    return;
                }
                if (this.$refs['customMenuForm']!==undefined) {
            		this.$refs['customMenuForm'].resetFields();
            	}
                this.submitUrl='updateMenu';
                this.addOrModifyDialogTitle="修改菜单页面";
                
                var param={id:rows[0].id};
                //根据id获取数据
                axios.post('/customfield/customField/queryFieldMenuById',param)
                .then(function (response) {
                    var data =  response.data;
                    if(data.code=='0'){
                    	fieldMenuVM.form= data.data;
                        //把当前的值存在临时变量里，当修改时，旧值和新值对比
                        fieldMenuVM.oldMenuCode = data.data.menuCode;
                        fieldMenuVM.oldMenuName = data.data.menuName;
                    }else{
                    	console.error(data);
                    }
                    
                   
                })
                .catch(function (error) {
                  console.log(error);
                })
                .then(function () {
                  // always executed
                }); 
                
                
                this.dialogFormVisible = true;
            	
            	
            },//end
            initCustomFiledMenu(){//初始化table 表格
            	 var pageSize = this.pager.pageSize;
                 var pageNum = this.pager.currentPage;
                 var param = {};
                 param.menuName=this.inputMenuName;
                 axios.post('/customfield/customField/listMenuPage?pageNum='+pageNum+"&pageSize="+pageSize,param)
                     .then(function (response) {
                         var data =  response.data;
                         if(data.code=='0'){
                             var resData = data.data;
                             fieldMenuVM.dataTable= resData.data;
                             //3.分页组件
                             fieldMenuVM.pager.total= resData.total;
                             fieldMenuVM.pager.currentPage = resData.currentPage;
                             fieldMenuVM.pager.pageSize = resData.pageSize;
                         }else{
                        	 console.error(data);
                         }
                        
                     })
                     .catch(function (error) {
                       console.log(error);
                     })
                     .then(function () {
                       // always executed
                     }); 
            },
            goToFieldSettingPage(row){
            	console.info(row);
            	var id = row.id;
            	location.href= "/customfield/customField/customFieldPage?id="+id;
            },
            closeAddCustomFieldDialog(){//关闭添加自定义字段dialog
          	  this.$refs['customMenuForm'].resetFields();
            },
            addFieldMenu(){//点击弹出新增dialog
            	this.form.id='';
            	this.dialogFormVisible = true;
            	this.submitUrl='saveMenu';
            	this.addOrModifyDialogTitle="添加菜单页面";
            	if (this.$refs['customMenuForm']!==undefined) {
            		this.$refs['customMenuForm'].resetFields();
            	}
            }
            
            
        },//methods end
        created(){
        	this.initCustomFiledMenu();
        },
        mounted(){
        	document.getElementById('userManage').style.display = 'block';
        },
      
    })