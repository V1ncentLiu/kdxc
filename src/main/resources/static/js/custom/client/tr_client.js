var clientVm = new Vue({
     el: '#trClientManage',
     data: {
    	 formLabelWidth:'120px',
         clientData:[],
         multipleSelection:[],
         dialogFormVisible:false,
         pager:{//组织列表pager
             total: 0,
             currentPage: 1,
             pageSize: 20,
         },
         addOrModifyDialogTitle:'',//添加或修改坐席title
         submitClientUrl:'',//保存或更新时url
         confirmBtnDisabled:false,//提交按钮 禁用
         orgDialogVisible:false,
         dialogBatchVisible:false,
         fileList:[],//上传文件列表
         orgTreeData:[],
         orgList:orgList,//组织机构list,
         uploadClientData:[],
         uploadNum:0,
         uploadPreDialogVisible:false,//预览
         uploadBtnText:'',
         uploadBtnDisabled:false,
         uploadErrorDialogVisible:false,//上传失败dialog
         uploadErrorData:[],//上传失败
         form:{//坐席form
        	 id:'',
        	 clientNo:'',
        	 bindPhone:'',
        	 displayPhone:'',
        	 orgId:'',
        	 callbackPhone:'',
        	 orgName:''
         },
         searchForm:{//搜索form
        	 orgId:'',
        	 clientNo:'',
        	 bindPhone:'',
        	 displayPhone:''
         },
         rules:{
        	 clientNo:[
        		 { required: true, message: '坐席编号不能为空'},
        		 {validator:function(rule,value,callback){
        			 if(Number.isInteger(Number(value)) && Number(value) > 0){                
               	        callback();
               	      }else{                 
               	        callback(new Error("只可以输入正整数,不超过50位"));               
               	      } 
        			 
        		 },trigger:'blur'},
        		 {validator:function(rule,value,callback){
        		
        			  var param = {clientNo:value};
        			   axios.post('/client/client/queryTrClientByParam', param)
                       .then(function (response) {
                           var resData = response.data;
                           if(resData.code=='0'){
                        	   var data = resData.data;
                        	   if(data){
                        		   if(data.id==clientVm.form.id){
                        			   callback();
                        		   }else{
                        			   callback(new Error("此坐席编号已存在，请修改后提交"));
                        		   }
                        		   
                        	   }else{
                        		   callback();
                        	   }
                               
                           }else{
                        	   clientVm.$message({message:'查询失败',type:'error'});
                               console.error(resData);
                           }
                       
                       })
                       .catch(function (error) {
                            console.log(error);
                       }).then(function(){
                       });
        			 
        		 },trigger:'blur'}
        	 ],
        	 bindPhone:[
        		 { required: true, message: '绑定电话号码不能为空'},
        		 {validator:function(rule,value,callback){
        			 if(Number.isInteger(Number(value)) && Number(value) > 0){                
               	        callback();
               	      }else{                 
               	        callback(new Error("只可以输入正整数,不超过11位"));               
               	      } 
        			 
        		 },trigger:'blur'},
        		 
        	 ],
        	 displayPhone:[
        		 { required: true, message: '外显手机号不能为空'},
        		 {validator:function(rule,value,callback){
        			 if(Number.isInteger(Number(value)) && Number(value) > 0){                
               	        callback();
               	      }else{                 
               	        callback(new Error("只可以输入正整数,不超过11位"));               
               	      } 
        			 
        		 },trigger:'blur'},
        		 
        	 ],
        	 orgName:[
        		 { required: true, message: '所在组织不能为空'},
        		 
        	 ],
        	 callbackPhone:[
        		 {validator:function(rule,value,callback){
        			 if(value){
        				  if(!/^[0-9]*$/.test(value)){
        					  callback(new Error("只可以输入正整数数字"));     
        	        	  }else{
        	        		  callback();
        	        	  }
        			 }else{
        				 callback();
        			 }
        			
        			 
        		 },trigger:'blur'},
        		 
        	 ]
         }
      },
     methods: {
    	 handleSelectionChange(val) {
             this.multipleSelection = val;
         },
         addClientDialog(){//添加坐席弹窗
        	 this.addOrModifyDialogTitle='添加坐席';
        	 this.dialogFormVisible=true;
        	 this.submitClientUrl ='saveTrClient';
        	 this.form.orgId='';
        	 if (this.$refs['clientForm']!==undefined) {
         		this.$refs['clientForm'].resetFields();
         	}
         },
         deleteClientDialog(){//删除坐席弹窗
        	 var rows = this.multipleSelection;
             if(rows.length<1){
                 this.$message({
                    message: '请选择数据进行删除',
                    type: 'warning'
                  });
                 return;
             }
             var rowNames = [];//字段名称
             var rowIds = [];//字段ID
             for(var i=0;i<rows.length;i++){
                 var curRow = rows[i];
                 rowIds.push(curRow.id);
                 rowNames.push("["+curRow.clientNo+"]");
             }
             var clientNos = rowNames.join(" ");
             
             
             this.$confirm('确定要删除 '+clientNos+'坐席吗？', '提示', {
            	 closeOnClickModal:false,
                 confirmButtonText: '确定',
                 cancelButtonText: '取消',
                 type: 'warning'
               }).then(() => {
            	  var param  = {idList:rowIds};
        	      axios.post('/client/client/deleteTrClient', param)
                  .then(function (response) {
                      var resData = response.data;
                      if(resData.code=='0'){
                    		clientVm.$message({message:'刪除成功',type:'success',duration:2000,onClose:function(){
                    			clientVm.initClientData();
                    	    }});  ;                     
                      }else{
                   	     clientVm.$message({message:'删除失败',type:'error'});
                          console.error(resData);
                      }
                  
                  })
                  .catch(function (error) {
                       console.log(error);
                  }).then(function(){
               	      
                  });
        	   
                 
                 
               }).catch(() => {
                 this.$message({
                   type: 'info',
                   message: '已取消删除'
                 });          
               });
             
             
             
             
         },
         modifyClientDialog(){
        	 this.form.orgId='';
        	 this.form.id='';
        	 var rows = this.multipleSelection;
             if(rows.length!=1){
                 this.$message({
                    message: '请选择一条数据进行修改',
                    type: 'warning'
                  });
                 return;
             }
           
        	 
        	 this.addOrModifyDialogTitle='修改坐席';
        	 this.dialogFormVisible=true;
        	 this.submitClientUrl ='updateTrClient';
        	 if (this.$refs['clientForm']!==undefined) {
          		this.$refs['clientForm'].resetFields();
          	}
        	 
        	
        	 var param={id:rows[0].id};
             axios.post('/client/client/queryTrClientById', param)
             .then(function (response) {
                 var resData = response.data;
                 if(resData.code=='0'){
              	    clientVm.form = resData.data;                     
                 }else{
              	     clientVm.$message({message:'查询失败',type:'error'});
                     console.error(resData);
                 }
             
             })
             .catch(function (error) {
                  console.log(error);
             }).then(function(){
          	   clientVm.confirmBtnDisabled=false;//启用提交按钮
             });
        	 
         },
         cancelForm(formName){
        	 this.$refs[formName].resetFields();
         	 this.dialogFormVisible = false;
         },
         saveClient(formName){//保存坐席
        	 this.$refs[formName].validate((valid) => {
                 if (valid) {
                 	//fieldMenuVM.$refs.confirmBtn.disabled=true;
                 	clientVm.confirmBtnDisabled=true;//禁用提交按钮
                    var param=this.form;
                   axios.post('/client/client/'+this.submitClientUrl, param)
                   .then(function (response) {
                       var resData = response.data;
                       if(resData.code=='0'){
                    	   clientVm.cancelForm(formName);
                    	   clientVm.$message({message:'操作成功',type:'success',duration:2000,onClose:function(){
                    		   clientVm.initClientData();
                     	    }});
                           
                       }else{
                    	   clientVm.$message({message:'操作失败',type:'error'});
                           console.error(resData);
                       }
                   
                   })
                   .catch(function (error) {
                        console.log(error);
                   }).then(function(){
                	   clientVm.confirmBtnDisabled=false;//启用提交按钮
                   });
                    
                 } else {
                   return false;
                 }
               });
         },
         closeAddClientDialog(){
        	 
         },
         selectOrg(){//选择组织
        	
        	 this.$nextTick(() => {
        		 clientVm.$refs.orgTree.setCheckedKeys([]);//清空tree
        		});
        	 this.orgDialogVisible=true;
        	 axios.post('/organization/organization/query',{})
             .then(function (response) {
                 var resData = response.data;
                 if(resData.code=='0'){
              	   clientVm.orgTreeData=resData.data;
                     
                 }else{
              	   clientVm.$message({message:'查询组织结构树失败',type:'error'});
                     console.error(resData);
                 }
             
             })
             .catch(function (error) {
                  console.log(error);
             }).then(function(){
            	var selectedOrgId =  clientVm.form.orgId;
            	if(selectedOrgId){
            		var array = new Array();
            		array.push(selectedOrgId);
            		clientVm.$refs.orgTree.setCheckedKeys(array);
            	}else{
            		clientVm.$refs.orgTree.setCheckedKeys([]);//清空tree
            	}
             });
         },
         selecedNode(checkedNodes,checkedKeys,halfCheckedNodes,halfCheckedKeys){//选择组织
        	 this.form.orgId=checkedNodes.id;
        	 this.form.orgName=checkedNodes.label;
        	 this.orgDialogVisible=false;
        	 this.$refs.orgTree.setCheckedKeys([]);//清空tree
         },
         initClientData(){
        	 var param = this.searchForm;
        	 param.pageNum=this.pager.currentPage;
        	 param.pageSize=this.pager.pageSize;
        	 axios.post('/client/client/listTrClientPage',param)
             .then(function (response) {
            	 var data =  response.data
                 if(data.code=='0'){
                 	var resData = data.data;
                 	clientVm.clientData= resData.data;
                  //3.分页组件
                 	clientVm.pager.total= resData.total;
                 	clientVm.pager.currentPage = resData.currentPage;
                 	clientVm.pager.pageSize = resData.pageSize;
                     
                 }else{
                	 clientVm.$message({message:'初始化坐席列表错误',type:'error'});
                 	 console.error(data);
                 }
             
             })
             .catch(function (error) {
                  console.log(error);
             }).then(function(){
             });
         },
         addBatchClient(){
        	 this.dialogBatchVisible=true;
        	 this.fileList=[];
         },
         submitUpload() {//提交文件
         	var fileList = this.fileList;
         	if(!fileList || fileList.length!=1){
         		this.$message.error({message:'未选中文件',type:'error'});
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
                   this.$message.error('文件格式错误');
                   return false;
               }
           },
           uploadSuccess(response, file, fileList){//上传成功后
         	  if(response.code=='0'){
         		  //清空文件里列表
             	  this.$refs.upload.clearFiles();
                   this.dialogBatchVisible = false;
                   /*this.$message({message:'上传成功',type:'success',duration:2000,onClose:function(){
           	         fieldVM.getQuery();
           	      }});*/
                   this.uploadClientData = response.data.data;
                   this.uploadNum = response.data.num;
                   this.uploadBtnText='导入';
                   this.uploadBtnDisabled=false;
                   this.uploadPreDialogVisible=true;
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
           submitUploadClient(){//上传坐席
    
               this.$confirm('', '确认导入数据', {
            	   showCancelButton:false,
            	   closeOnClickModal:false,
                   confirmButtonText: '确认',
                   type: 'warning',
                   center: true
                 }).then(() => {
                	 this.uploadBtnDisabled=true;
                	 this.uploadBtnText='导入中';
                	 
              	   axios.post('/client/client/submitTrClientData',{})
                     .then(function (response) {
                    	 var data =  response.data;
                         if(data.code=='0'){
                            var resData =  data.data;
                            if(resData && resData.length!=0){
                            	clientVm.uploadPreDialogVisible = false;
                            	clientVm.uploadErrorData = resData;
                            	clientVm.uploadErrorDialogVisible = true;
                            }else{
                            	 clientVm.uploadPreDialogVisible = false;
                            	 clientVm.$message({message:'导入成功',type:'success',duration:2000,onClose:function(){
                          		     clientVm.initClientData();
                           	    }});
                            }
                             
                         }else{
                        	 clientVm.$message({message:'系统错误',type:'error'});
                         	 console.error(data);
                         }
                     
                     })
                     .catch(function (error) {
                          console.log(error);
                     }).then(function(){
                    	 clientVm.initClientData
                     });
              	   
                	 
                  
                 }).catch(() => {
                 });
        	   
    
           },
           clickMeKown(){//点击我知道了
        	   this.uploadErrorData=[];
        	   this.uploadErrorDialogVisible=false;
        	   this.initClientData();
           },
           updateCallbackPhone(id,callbackPhone){
        	   if(!/^[0-9]*$/.test(callbackPhone)){
        		   clientVm.$message({message:'只可以输入正整数数字',type:'warning'});
         	        return false;
        	    }
        	    
        	   var param={};
        	   param.id=id;
        	   param.callbackPhone=callbackPhone;
               axios.post('/client/client/updateCallbackPhone', param)
               .then(function (response) {
                   var resData = response.data;
                   if(resData.code=='0'){
                	   if(resData.data){
                		   clientVm.$message({message:'操作成功',type:'success',duration:2000,onClose:function(){
                    		   clientVm.initClientData();
                     	    }});
                	   }else{
                		   clientVm.$message({message:'操作失败',type:'error'});
                	   }
                	 
                       
                   }else{
                	   clientVm.$message({message:'系统错误',type:'error'});
                       console.error(resData);
                   }
               
               })
               .catch(function (error) {
                    console.log(error);
               }).then(function(){
            	   
               });
           }
         
     },
     created(){
          this.initClientData();
     },
     mounted(){
      	document.getElementById('trClientManage').style.display = 'block';
      },
})