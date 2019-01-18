   var orgVM =  new Vue({
        el: '#organizationManage',
        data: function() {
            return {
            	expandedKeys:expandedKeys,//树形结构  默认展开的
                dataTree: orgData,//组织结构tree
                dataTable:[],//
                staffNumTable:[],//组织人员table
                multipleSelection: [],//选择行
                defaultProps: {
                    children: 'children',
                    label: 'label'
                  },
                dialogFormVisible: false,
                dialogStaffNumVisible:false,
                form: {//添加组织机构dialog
                    name: '',
                    remark: '',
                    parentName:'',
                    id:''
                },
                staffNumSearch:{//组内组成搜索框
                	name:'',
                	userName:'',
                	phone:''
                },
                oldName:'',//旧的组织机构，更新时使用 
                formLabelWidth: '120px',
                pager:{//组织列表pager
                    total: 0,
                    currentPage: 1,
                    pageSize: 20,
                },
                pagerStaffNum:{//组内成员pager
                    total: 0,
                    currentPage: 1,
                    pageSize: 20,
                },
                selectedNode:null,//当前的orgParentId
                selectedOrgId:'',//组织列表中，点击的是个组织 
                rules:{
                	 name: [
                         { required: true, message: '组织名称不能为空',trigger:'blur'},
                         { validator:function(rule,value,callback){
                        	 var id = orgVM.form.id;
                        	 var name = orgVM.oldName;
                        	 if(id && value==name){
                        		 callback();
                        	 }
                               var param = {'name':value};
                        	    axios.post('/organization/organization/queryOrgByParam',param)
                                .then(function (response) {
                                    var data =  response.data
                                    if(data.code=='0'){
                                        var resData = data.data;
                                        if(resData){
                                        	callback(new Error("组织名称已存在"));
                                        }else{
                                        	callback();
                                        }
                                        
                                    }else{
                                    	 callback(new Error("查询组织名称是否唯一报错"));
                                    }
                                })
                                .catch(function (error) {
                                  console.log(error);
                                })
                                .then(function () {
                                  // always executed
                                });
                               
                        },trigger: 'blur'}
                     ]
                },
                inputOrgName:'',//搜索框 组织名称
                multipleSelection:[],//选择的列
            }             
        },
        methods: {
            toggleSelection(rows) {
                if (rows) {
                    rows.forEach(row => {
                        this.$refs.multipleTable.toggleRowSelection(row);
                    });
                } else {
                  this.$refs.multipleTable.clearSelection();
                }
            },
            handleSelectionChange(val) {
                this.multipleSelection = val;
            },
            deleteFun() {//点击删除组织机构
            	var rows = this.multipleSelection;
            	if(rows.length==0){
                    this.$message({
                       message: '请选择一条数据进行修改',
                       type: 'warning'
                     });
                    return;
                }
                var rowIds = [];
                for(var i=0;i<rows.length;i++){
                    rowIds.push(rows[i].id)
                }
                var param  = {idList:rowIds}
                axios.post('/organization/organization/queryOrgStaffByParentId',param)
                .then(function (response) {
                    var data =  response.data;
                    var msgInfo = "";
                    var btnText="";
                    var isDelete=false;
                    if(data.code=='0'){
                        var resData = data.data;
                        if(resData){
                            msgInfo='本组织已经关联到用户，需要将组织下用户转移后，方可进行组织的删除操作！';
                            btnText="知道了"
                        }else{
                           msgInfo='删除组织后此组织信息将不存在，确认删除吗';
                           btnText="确认";
                           isDelete=true;
                        }
                        
                        orgVM.$alert(msgInfo, '消息提醒', {
                            confirmButtonText: btnText,
                            showClose:true,
                             callback: action => {
                                   if(isDelete & action=='confirm'){
                                       //删除
                                	   axios.post('/organization/organization/delete',param)
                                       .then(function (response) {
                                    	   console.info(response);
                                           var data =  response.data;
                                           if(data.code=='0'){
                                               var resData = data.data;
                                               var delText="";
                                               if(resData){
                                            	   delText="删除成功";
                                               }else{
                                                   delText="删除失败"
                                               }
                                               
                                             orgVM.$message({message:delText,type:'success',duration:2000,onClose:function(){
                                            	 orgVM.initOrgTree();
                                                 orgVM.getQuery();
                                       	    }});
                                               
                                           }else{
                                        	   orgVM.$message({
                                                   message: "接口调用失败",
                                                   type: 'warning'
                                                 }); 
                                           }
                                       })
                                       .catch(function (error) {
                                         console.log(error);
                                         orgVM.$message({
                                             message: "系统错误",
                                             type: 'error'
                                           }); 
                                       })
                                       .then(function () {
                                    	   
                                       });
                                       
                                   }
                                 
                            } 
                        });

                        
                        
                        
                    }else{
                        
                    }
                    
                  
                }).catch(function (error) {
                  console.log(error);
                })
                .then(function () {
                  // always executed
                });
            
            },
            clickOrgNode(data,node,obj){//点击左侧节点
            	this.selectedNode = data;
            	this.form.parentName=data.label;
            	this.getQuery();
            },
            getQuery(){
                this.initTableData();
            },
            initTableData(){// 查询 table 需要数据
                var pageSize = this.pager.pageSize;
                var pageNum = this.pager.currentPage;
                var param = {};
                var  parentId = this.selectedNode.id;
              
                param.parentId = parentId;
                param.name = this.inputOrgName;
                axios.post('/organization/organization/queryOrgDataByParam?pageNum='+pageNum+"&pageSize="+pageSize,param)
                    .then(function (response) {
                        var data =  response.data
                        if(data.code=='0'){
                        	var resData = data.data;
                        	orgVM.dataTable= resData.data;
                            //3.分页组件
                            orgVM.pager.total= resData.total;
                            orgVM.pager.currentPage = resData.currentPage;
                            orgVM.pager.pageSize = resData.pageSize;
                        }else{
                        	 orgVM.$message({
                                 message: "接口调用失败",
                                 type: 'error'
                               }); 
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
            addChildOrg(){
            	this.form.id='';
            	var curData = this.selectedNode;
            	if(!curData){
            		this.$message({
                        message: '请选择一个上级',
                        type: 'warning'
                      });
                     return;
            	}
            
            	this.dialogFormVisible = true;
            },
            submitForm(formName) {
                this.$refs[formName].validate((valid) => {
                  if (valid) {
                     var param=this.form;
                     param.parentId=this.selectedNode.id;
                 
                     
                    axios.post('/organization/organization/saveOrUpdate', param)
                    .then(function (response) {
                    	var resData = response.data;
                    	if(resData.code=='0'){
                    	    orgVM.$message({message:'操作成功',type:'success',duration:2000,onClose:function(){
                    	        orgVM.initOrgTree();
                         	    orgVM.getQuery();
                    	    }});
                    	    orgVM.cancelForm(formName);
                    	    orgVM.dialogFormVisible = false;
                    	   
                    	}else{
                    		orgVM.$message('操作失败');
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
              cancelForm(formName) {
                  this.$refs[formName].resetFields();
                  this.dialogFormVisible = false;
              },
              initOrgTree(){//刷新根节点tree
            	  axios.post('/organization/organization/query',{})
                  .then(function (response) {
                      var data =  response.data
                      if(data.code=='0'){
                          orgVM.dataTree= data.data;
                          
                      }
                      
                     
                  })
                  .catch(function (error) {
                    console.log(error);
                  })
                  .then(function () {
                    // always executed
                  }); 
            	  
              },
              handleSelectionChange(val) {//选择节点的事件
                  this.multipleSelection = val;
              },
              modifyChildOrg(){//点击修改按钮时
            	  var rows = this.multipleSelection;
                  if(rows.length!=1){
                      this.$message({
                         message: '请选择一条数据进行修改',
                         type: 'warning'
                       });
                      return;
                  }
                 var parentName =  orgVM.form.parentName;
                  var param={id:rows[0].id};
                  //根据id获取数据
                  axios.post('/organization/organization/queryOrgById',param)
                  .then(function (response) {
                      var data =  response.data;
                      if(data.code=='0'){
                          orgVM.form= data.data;
                          orgVM.form.parentName=parentName;
                          //把当前的值存在临时变量里，当修改时，旧值和新值对比
                          orgVM.oldName = data.data.name;
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
              closeAddOrgDialog(){//close 添加组织弹框
            	  this.$refs['ruleForm'].resetFields();
              },
              openStaffNumTable(orgId){//打开组内成员弹框
            	   this.dialogStaffNumVisible = true;
            	   this.selectedOrgId=orgId;
            	   
            	   
            	   var param = this.staffNumSearch;
            	   param.orgId = orgId;
            	   param.pageSize = this.pagerStaffNum.pageSize;
                   param.pageNum = this.pagerStaffNum.currentPage;
           	       axios.post('/organization/organization/listOrgUserInfo',param)
                     .then(function (response) {
                       var data =  response.data
                       if(data.code=='0'){
                       	var resData = data.data;
                       	orgVM.staffNumTable= resData.data;
                        //3.分页组件
                       	orgVM.pagerStaffNum.total= resData.total;
                       	orgVM.pagerStaffNum.currentPage = resData.currentPage;
                       	orgVM.pagerStaffNum.pageSize = resData.pageSize;
                           
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
              initStaffNumTable(){
            	  
            	  this.openStaffNumTable(this.selectedOrgId); 
              },
              closeStaffNumDialog(){//
            	this.$refs['staffNumSearch'].resetFields();
            	this.selectedOrgId='';//把当前的组织ID设为''
              }
              
              
              
              
        },
        created(){
        	this.clickOrgNode(this.dataTree[0],null,null);
        	
        }//created方法 结束
    })