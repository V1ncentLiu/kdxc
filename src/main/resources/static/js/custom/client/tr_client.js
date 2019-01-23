var clientVM = new Vue({
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
         orgTreeData:[],
         form:{//坐席form
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
        		 
        	 ],
        	 bindPhone:[
        		 { required: true, message: '绑定电话号码不能为空'},
        		 
        	 ],
        	 displayPhone:[
        		 { required: true, message: '外显手机号不能为空'},
        		 
        	 ],
        	 orgId:[
        		 { required: true, message: '所在组织不能为空'},
        		 
        	 ],
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
        	 if (this.$refs['clientForm']!==undefined) {
         		this.$refs['clientForm'].resetFields();
         	}
         },
         modifyClientDialog(){
        	 this.addOrModifyDialogTitle='修改坐席';
        	 this.dialogFormVisible=true;
        	 this.submitClientUrl ='updateTrClient';
        	 this.$refs['clientForm'].resetFields();
         },
         cancelForm(formName){
        	 this.$refs[formName].resetFields();
         	 this.dialogFormVisible = false;
         },
         saveClient(formName){//保存坐席
        	 this.$refs[formName].validate((valid) => {
                 if (valid) {
                 	//fieldMenuVM.$refs.confirmBtn.disabled=true;
                 	clientVM.confirmBtnDisabled=true;//禁用提交按钮
                    var param=this.form;
                   axios.post('/client/client/'+this.submitClientUrl, param)
                   .then(function (response) {
                       var resData = response.data;
                       if(resData.code=='0'){
                    	   clientVM.cancelForm(formName);
                    	   clientVM.$message({message:'操作成功',type:'success',duration:2000,onClose:function(){
                    		   //clientVM.initCustomFiledMenu();
                     	    }});
                           
                       }else{
                    	   clientVM.$message({message:'操作失败',type:'error'});
                           console.error(resData);
                       }
                   
                   })
                   .catch(function (error) {
                        console.log(error);
                   }).then(function(){
                	   clientVM.confirmBtnDisabled=false;//启用提交按钮
                   });
                    
                 } else {
                   return false;
                 }
               });
         },
         closeAddClientDialog(){
        	 
         },
         selectOrg(){//选择组织
        	 this.orgDialogVisible=true;
        	 axios.post('/organization/organization/query',{})
             .then(function (response) {
                 var resData = response.data;
                 if(resData.code=='0'){
              	   clientVM.orgTreeData=resData.data;
                     
                 }else{
              	   clientVM.$message({message:'查询组织结构树失败',type:'error'});
                     console.error(resData);
                 }
             
             })
             .catch(function (error) {
                  console.log(error);
             }).then(function(){
             });
         },
         selecedNode(checkedNodes,checkedKeys,halfCheckedNodes,halfCheckedKeys){
        	 this.form.orgId=checkedNodes.id;
        	 this.form.orgName=checkedNodes.label;
        	 this.orgDialogVisible=false;
         }
         
         
     },
     created(){
        	
     }
})