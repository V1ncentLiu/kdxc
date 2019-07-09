  var fieldVM =  new Vue({
        el: '#userManage',
        data: function() {
            return {
                dialogFormVisible: false,
                dialogBatchVisible: false,//上传文件弹窗控制
                fileList:[],//上传文件列表
                pager:{
                    total: 0,
                    currentPage: 1,
                    pageSize: 20,
                },
                multipleSelection: [],
                form: {
                	id:'',
                    fieldCode:'',
                    fieldName:'',
                    displayName:'',
                    fieldType:1,
                    sortNum:'',
                    width:'',
                    isUsed:1,
                    remark:'',
                    //menuId:fieldMenu.id
                },
                addAndUpdateDialog:'',//添加或修改 字段dialog 标题
                menuId:fieldMenu.id,//该字段设置属于菜单组
                submitBrnDisabled:false,
                submitUrl:'',//提交url
                oldFieldForm:{//存放旧的form 数据，提交时比对
                	fieldCode:'',
                	fieldName:'',
                	displayName:''
                },
                paramData:{
                	id:fieldMenu.id
                },//上传文件携带的参数
                inputFieldName:'',//搜索输入框
                fieldMenu:fieldMenu,//字段菜单信息 
                formLabelWidth: '100px',
                dataTable: [],
                options: [{
                    value: 1,
                    label: '文本'
                }, {
                    value: 2,
                    label: '时间'
                }, {
                    value: 3,
                    label: '下拉列表'
                }],
                isUsedOps:[{
                    value: 1,
                    label: '是'
                }, {
                    value: 0,
                    label: '否'
                }],
                rules:{
                	fieldCode:[
                		 { required: true, message: '字段编码不能为空',trigger:'blur'},
                		 {validator:function(rule,value,callback){
                			 var id = fieldVM.form.id;
                        	 var fieldCode = fieldVM.oldFieldForm.fieldCode;
                        	 if(id && value==fieldCode){
                        		 callback();
                        	 }
                			 
                              var param = {};
                              param.fieldCode=value;
                              param.menuId=fieldVM.menuId;
                              axios.post('/customfield/customField/query',param)
                              .then(function (response) {
                                  var data =  response.data;
                                  if(data.code=='0'){
                                      var resData = data.data;
                                      if(resData){
                                          callback(new Error("此字段编码已存在，请修改后提交"));
                                      }else{
                                          callback();
                                      }
                                      
                                  }else{
                                       callback(new Error("查询字段编码是否唯一报错"));
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
                	fieldName:[
                		{required: true, message: '字段名称不能为空',trigger:'blur'},
                		 {validator:function(rule,value,callback){
                			 var id = fieldVM.form.id;
                        	 var fieldName = fieldVM.oldFieldForm.fieldName;
                        	 if(id && value==fieldName){
                        		 callback();
                        	 }
                			 
                             var param = {};
                             param.fieldName=value;
                             param.menuId=fieldVM.menuId;
                             axios.post('/customfield/customField/query',param)
                             .then(function (response) {
                                 var data =  response.data;
                                 if(data.code=='0'){
                                     var resData = data.data;
                                     if(resData){
                                         callback(new Error("此字段名称已存在，请修改后提交"));
                                     }else{
                                         callback();
                                     }
                                     
                                 }else{
                                      callback(new Error("查询字段名称是否唯一报错"));
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
                	displayName:[
                        {required: true, message: '外显名称不能为空',trigger:'blur'},
                         {validator:function(rule,value,callback){
                        	 var id = fieldVM.form.id;
                        	 var displayName = fieldVM.oldFieldForm.displayName;
                        	 if(id && value==displayName){
                        		 callback();
                        	 }
                        	 
                             var param = {};
                             param.displayName=value;
                             param.menuId=fieldVM.menuId;
                             axios.post('/customfield/customField/query',param)
                             .then(function (response) {
                                 var data =  response.data;
                                 if(data.code=='0'){
                                     var resData = data.data;
                                     if(resData){
                                         callback(new Error("此外显名称已存在，请修改后提交"));
                                     }else{
                                         callback();
                                     }
                                     
                                 }else{
                                      callback(new Error("查询外显名称是否唯一报错"));
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
                    fieldType:[
                        {required: true, message: '请选择字段类型',trigger:'blur'}
                    ],
                    sortNum:[
                    	{            
                    	    validator(rule,value,callback){ 
                    	    	if(value){
                    	    		  if(Number.isInteger(Number(value)) && Number(value) > 0){                
                              	        callback();
                              	      }else{                 
                              	        callback(new Error("只可以输入正整数,不超过5位字符"));               
                              	      } 
                    	    	}else{
                    	    		callback();
                    	    	}
                    	                
                    	    },             
                    	    trigger: 'blur',           
                    	  }
                    	
                    ],
                    width:[
                      	{            
                    	    validator(rule,value,callback){ 
                    	    	if(value){
                    	    		if(Number.isInteger(Number(value)) && Number(value) > 0 && Number(value) <1000){                
                            	        callback();
                            	      }else{                 
                            	        callback(new Error("只可以输入正整数,且不大于1000"));               
                            	      }    
                    	    	}else{
                    	    		callback();
                    	    	}
                    	               
                    	    },             
                    	    trigger: 'blur',           
                    	  }
                    ]
                
                    
                
                }
            }        	  
        },
        methods: {
            deleteFun() {
            	
                var rows = this.multipleSelection;
                if(rows.length==0){
                    this.$message({
                       message: '请选择删除数据',
                       type: 'warning'
                     });
                    return;
                }
                var rowNames = [];//字段名称
                var rowIds = [];//字段ID
                for(var i=0;i<rows.length;i++){
                    var curRow = rows[i];
                    rowIds.push(curRow.id);
                    rowNames.push("【"+curRow.fieldName+"】");
                }
                var fieldName = rowNames.join(" ");
            	
            	
                this.$confirm('确定要删除'+fieldName+' 字段吗？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                	
                    var param  = {idList:rowIds};
                    axios.post('/customfield/customField/delete',param)
                    .then(function (response) {
                        var data =  response.data;
                        if(data.code=='0'){
                        	fieldVM.$message({message:'刪除成功',type:'success',duration:2000,onClose:function(){
                        		fieldVM.getQuery();
                    	    }});  
                            
                        }else{
                            fieldVM.$message({
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
                    this.$message({
                        type: 'info',
                        message: '已取消删除'
                    });          
                });
            },
            cancelForm(formName){
            	this.$refs[formName].resetFields();
            	this.dialogFormVisible=false;
            },
            saveField(formName){//保存自定义字段
            	 this.$refs[formName].validate((valid) => {
                     if (valid) {
                    	//fieldVM.$refs.submitBtn.disabled=true; //禁用提交按钮
                    	fieldVM.submitBrnDisabled=true;
                        var param=this.form;
                        param.menuId=fieldVM.menuId;
                       axios.post('/customfield/customField/'+this.submitUrl, param)
                       .then(function (response) {
                           var resData = response.data;
                           if(resData.code=='0'){
                               fieldVM.$message({message:'操作成功',type:'success',duration:2000,onClose:function(){
                            	   	fieldVM.getQuery();
                       	        }});
                               
                               fieldVM.dialogFormVisible = false;
                           }else{
                               fieldVM.$message('操作失败');
                               console.error(resData);
                           }
                       
                       })
                       .catch(function (error) {
                            console.log(error);
                       }).then(function(){
                    	  // fieldVM.$refs.submitBtn.disabled=false; 
                    	   fieldVM.submitBrnDisabled=false;
                       });
                        
                     } else {
                       return false;
                     }
                   });
                 
            	
            },
            getQuery(){//查询table 数据
            	 var pageSize = this.pager.pageSize;
                 var pageNum = this.pager.currentPage;
                 var param = {};
                 param.menuId=this.menuId;
                 param.fieldName=this.inputFieldName;
                 axios.post('/customfield/customField/listCustomFieldPage?pageNum='+pageNum+"&pageSize="+pageSize,param)
                     .then(function (response) {
                         var data =  response.data;
                         if(data.code=='0'){
                             var resData = data.data;
                             fieldVM.dataTable= resData.data;
                             //3.分页组件
                             fieldVM.pager.total= resData.total;
                             fieldVM.pager.currentPage = resData.currentPage;
                             fieldVM.pager.pageSize = resData.pageSize;
                         }else{
                        	 fieldVM.$message('查询失败');
                             console.error(resData);
                         }
                        
                     })
                     .catch(function (error) {
                       console.log(error);
                     })
                     .then(function () {
                       // always executed
                     }); 
            },
            handleSelectionChange(val){
            	  this.multipleSelection = val;
            },
            getFieldTypeText(row, column, cellValue, index){//字段類型的文本
                   var valueText ="";
            	   if(cellValue==1){
            		   valueText="文本";
            	   }else if(cellValue==2){
            		   valueText="时间";
            	   }else if(cellValue==3){
            		   valueText="下拉列表";
            	   }
            	   return valueText;
            },
            getIsUsed(row, column, cellValue, index){//是否使用文本
            	var valueText ="";
                if(cellValue==0){
                    valueText="否";
                }else if(cellValue==1){
                    valueText="是";
                }
                return valueText;
            	
            },
            modifyFiled(){//点击编辑按钮
            	   this.form.id='';
            	   var rows = this.multipleSelection;
                   if(rows.length!=1){
                       this.$message({
                          message: '请选择一条数据进行修改',
                          type: 'warning'
                        });
                       return;
                   }
                   if (this.$refs['fieldForm']!==undefined) {
                 		this.$refs['fieldForm'].resetFields();
                   }
                   this.submitUrl='update';
                   this.addAndUpdateDialog="修改字段";
                   var param={id:rows[0].id};
                   //根据id获取数据
                   axios.post('/customfield/customField/query',param)
                   .then(function (response) {
                       var data =  response.data;
                       if(data.code=='0'){
                    	   var resData = data.data;
                           fieldVM.form= resData;
                           //把当前的值存在临时变量里，当修改时，旧值和新值对比
                           fieldVM.oldFieldForm.fieldCode=resData.fieldCode;
                           fieldVM.oldFieldForm.fieldName=resData.fieldName;
                           fieldVM.oldFieldForm.displayName=resData.displayName;
                       }else{
                    	   fieldVM.$message('查询失败');
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
                   
                   
            },
            submitUpload() {//提交文件
            	var fileList = this.fileList;
            	if(!fileList || fileList.length!=1){
            		this.$message.error({message:'请选择一个文件',type:'error'});
            		return false;
            	}
                this.$refs.upload.submit();
              },
              handleChange(files,fileList){//只允许选择一个文件
                    this.fileList = fileList.slice(-1);
              },
              beforeUpload(file){//上传之前 文件校验
                  var isTextComputer = file.type === 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';
                  if(!isTextComputer){
                	  this.fileList=[];
                      this.$message.error('文件格式错误');
                      return false;
                  }
              },
              uploadSuccess(response, file, fileList){//上传成功后
            	  if(response.code=='0' && response.data){
            		  //清空文件里列表
                	  this.$refs.upload.clearFiles();
                      this.dialogBatchVisible = false;
                      this.$message({message:'上传成功',type:'success',duration:2000,onClose:function(){
              	         fieldVM.getQuery();
              	      }});
            	  }else{
            		  console.log(response);
            		  this.$message({message:'上传失败',type:'error'});
            		  this.$refs.upload.clearFiles();
            	  }
            	
              },
              closeAddDialog(){//关闭添加自定义字段dialog
            	  this.$refs.fieldForm.resetFields();
              },
              closeUploadFileDialog(){//关闭上传文件 dialog
            	  this.$refs.upload.clearFiles();
              },
              uploadError(){//上傳失敗
            	  this.$message({message:'上传失败',type:'error'});
            	  this.$refs.upload.clearFiles();
              },
              addFieldSetting(){//添加字段dialog
            	  this.dialogFormVisible = true;
            	  this.form.id='';
            	  this.submitUrl='save';
            	  this.addAndUpdateDialog="新建字段";
            	  if (this.$refs['fieldForm']!==undefined) {
              		this.$refs['fieldForm'].resetFields();
              	  }
              }
              
              
            
            
            
            
        },
        created(){
            var localVal=localStorage.getItem('allChangePageSize')?parseInt(localStorage.getItem('allChangePageSize')):'';
            if(localVal){this.pager.pageSize = localVal;}
            
        	this.getQuery();
        },
        mounted(){
            document.getElementById('userManage').style.display = 'block';
        }
        
    })