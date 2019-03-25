var mainDivVM = new Vue({
    el: '#mainDiv',
    data: {
        // 工作台
        activeName:'1',
        activeName2:'1',
        activeName3:'1',
        activeName4:'1',
        activeName5:'1',
        unAssignNum:'',//待分配任务数
        secondVisitedNum:'',//当月二次到访数
        secondSignedNum:'',//当月二次来访签约数
        direcotorTomorrowArriveTime:'',//预计明日到访数
        //公告        
        items: [ 
            // {content:'系统将于2018年12月5日晚上12:00进行系统升级，请各位同事及时处理工作。系统预计在12:20分恢复正常使用,感谢配合!',id:1},
            // {content:'公告2公告2公告2公告2公告2公告2公告2',id:2},
            // {content:'公告3公告3公告3公告3公告3公告3公告3',id:3}
        ], 
        //未读消息             
        consoleNewsParam:{
            newsBox:false,
            newsData: [
                // {content:'系统将于2018年12月5日晚上12:00进行系统升级，请各位同事及时处理工作。系统预计在12:20分恢复正常使用,感谢配合!',id:1},
                // {content:'公告2公告2公告2公告2公告2公告2公告2',id:2},
                // {content:'公告3公告3公告3公告3公告3公告3公告3',id:3}
            ]
        },
        // 待分配邀约来访客户
        dataTable:[],
        multipleSelection:[],
        allocationForm: {
            saleId:'',
            remark:'',
            message:''
        },
        allocationVisible: false,
        formLabelWidth: '150px',
        allocationFormRules: {
            saleId: [
                { required: true, message: '请选择商务经理', trigger: 'change' }
            ]
        },
        saleOptions:busSaleList,
        // 待审签约记录
        dataTable4:[],
        pager:{//
            total: 0,
            currentPage: 1,
            pageSize: 20,
        },
        dialogFormVisible:false,//驳回
        recordDialog:false,//付款明细
        reasonDialog:false,//驳回原因
        paymentDetailsShow:false,//判断全款付款明细表格是否显示
        paymentDetailsShow2:false,//判断定金付款明细表格是否显示
        paymentDetailsShow3:false,//判断二次定金付款明细表格是否显示
        paymentDetailsShow4:false,//判断尾款付款明细表格是否显示
        signRecordArrTitle:'',
        dialogForm:{//输入驳回原因
            reason:''
        },
        dialogRules:{
            reason: [
                { required: true, message: '请输入驳回原因', trigger: 'blur' }
            ]
        },
        rebutReason:'',//驳回原因
        recordTable:[],//全款付款明细表格
        recordTable2:[],//定金付款明细表格
        recordTable3:[],//二次定金付款明细表格
        recordTable4:[],//尾款付款明细表格
        // 待审批到访记录
        dataTable2:[],
        dialogFormVisibleVisit:false,
        // 待审批未到访记录
        dataTable3:[],
        dialogFormVisibleUnVisit:false,

    },
    methods: {
        gotoBusAllocation(){//跳转待分配来访客户
            window.location.href="/business/busAllocation/initAppiontmentList"; 
        },
        gotoVisitRecord(){//跳转客户到访记录
            window.location.href="/visit/visitRecord/visitRecordPage"; 
        },
        gotoVisitRecord(){//跳转客户未到访记录
            window.location.href="/visit/visitRecord/visitRecordPage"; 
        },
        gotoSignRecord(){//跳转客户签约记录
            window.location.href="/sign/signRecord/signRecordPage"; 
        },
        // 工作台
        handleClick(tab, event) {
            console.log(tab, event);
        },
        initBoard(){
            var param={};
            // 公告
            param={};
            axios.post('/console/console/queryAnnReceiveNoPage',param).then(function (response) {
                console.log('公告')
                console.log(response.data)
                mainDivVM.items=response.data.data;
            });
            // 未读消息
            param={};
            axios.post('/console/console/queryBussReceiveNoPage',param).then(function (response) {
                console.log('未读消息')
                console.log(response.data)
                if(response.data){
                    if (response.data.data) {
                        if(response.data.data.length!=0){
                            mainDivVM.consoleNewsParam.newsData=response.data.data;
                            mainDivVM.consoleNewsParam.newsBox=true
                        }else{
                            mainDivVM.consoleNewsParam.newsBox=false
                        }
                    }else{
                        mainDivVM.consoleNewsParam.newsBox=false
                    }
                }else{
                    mainDivVM.consoleNewsParam.newsBox=false
                }                
            }); 
            // 待分配任务数 当月二次到访数 当月二次到访签约数
            param={};
            axios.post('/console/console/countBusinessDirectorCurMonthNum',param).then(function (response) {
                console.log('待分配任务数 当月二次到访数 当月二次到访签约数')                
                console.log(response.data)                
                mainDivVM.unAssignNum=response.data.data.unAssignNum;
                mainDivVM.secondVisitedNum=response.data.data.secondVisitedNum;
                mainDivVM.secondSignedNum=response.data.data.secondSignedNum;
            }); 
            // 预计明日到访数
            param={};
            axios.post('/console/console/countBusiDirecotorTomorrowArriveTime',param).then(function (response) {
                console.log('预计明日到访数')                
                console.log(response.data)                
                mainDivVM.direcotorTomorrowArriveTime=response.data.data;
            });   
        },
        // 待分配邀约来访客户
        searchTable() {
            var param ={};     
            axios.post('/console/console/pendingVisitListNoPage', param)
            .then(function (response) {
                console.log('待分配邀约来访客户')
                console.log(response.data)                
                mainDivVM.dataTable= response.data.data;
            })
            .catch(function (error) {
                 console.log(error);
            });            
        },
        showCustomerDetail(row, column){ // 客户详情
            window.location.href='/bus/BusinessCustomer/viewCustomerInfo?clueId='+row.clueId;
        },
        //选择行
        handleSelectionChange(val) {
            this.multipleSelection = val;
        },
        //日期数据格式化方法
        dateFormat( row, column, cellValue, index) {
            if (cellValue == undefined) {
                return "";
            }
            return moment(cellValue).format("YYYY-MM-DD HH:mm:ss");
        },
        //品尝项目转换方法
        transformProject(row, column, cellValue, index) {
           var text="";
            if(projectList&&cellValue){
               var array=cellValue.split(",");
                for(var i=0;i<array.length;i++){
                    for(var j=0;j<projectList.length;j++){
                        if(array[i]==projectList[j].id){
                            if(i==0){
                                text=projectList[j].projectName;
                            }else{
                                text=text+","+projectList[j].projectName;
                            }
                        }
                    }
                }
            }
            return text;
        },
        //选址情况转换方法
        transformAddress(row, column, cellValue, index) {
            var text="";
            if(optionAddressList){
                for(var i=0;i<optionAddressList.length;i++){
                    if(cellValue==optionAddressList[i].value){
                        text=optionAddressList[i].name;
                    }
                }
            }
            return text;
        },
        //投资金额转换方法
        transformUssm(row, column, cellValue, index) {
            var text="";
            if(ussmList){
                for(var i=0;i<ussmList.length;i++){
                    if(cellValue==ussmList[i].value){
                        text=ussmList[i].name;
                    }
                }
            }
            return text;
        },
        //意向项目转换方法
        transformPurProject(row, column, cellValue, index) {
            var text="";
            if(cellValue){
                if(projectList){
                    for(var i=0;i<projectList.length;i++){
                        if(cellValue==projectList[i].value){
                            text=projectList[i].name;
                        }
                    }
                }
            }else{
                text=row.purOutProject;
            }
            return text;
        },
        //店铺面积转换方法
        transformArea(row, column, cellValue, index) {
            var text="";
            if(storefrontAreaList){
                for(var i=0;i<storefrontAreaList.length;i++){
                    if(cellValue==storefrontAreaList[i].value){
                        text=storefrontAreaList[i].name;
                    }
                }
            }
            return text;
        },
        //打开分发资源
        toAllocationClue() {
            this.allocationForm.message="";
            this.allocationForm.saleId = '';
            this.allocationForm.remark = '';
            var rows = this.multipleSelection;
            if(rows.length==0){
                this.$message({
                    message: '请选择一条数据进行分发',
                    type: 'warning'
                });
                return;
            }
            var text="";
            for(var i=0;i<rows.length;i++){
                var curRow = rows[i];
                if(curRow.busSaleId){
                    text+="当前选择“"+curRow.cusName+"”已有之前服务商务经理"+curRow.busSaleName+",";
                }
            }
            if(text!=""){
                text+="请确认后分发客户。";
                this.allocationForm.message=text;
            }
           
           this.allocationVisible = true;
        },
        // 提交分发资源
        allocationClue(formName){//分发资源                   
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    var rows = this.multipleSelection;
                    var idList = [];
                    var clueIdList = [];
                    for(var i=0;i<rows.length;i++){
                        var curRow = rows[i];
                        idList.push(curRow.id);
                        clueIdList.push(curRow.clueId);
                    }
                    //分发
                    var param  = {};
                    param.type=1;
                    param.idList=idList;
                    param.clueIdList=clueIdList;
                    param.busSaleId=this.allocationForm.saleId;
                    param.remark=this.allocationForm.remark;
                    axios.post('/business/busAllocation/busAllocationClue',param)
                    .then(function (response) {
                        var data =  response.data;
                        if(data.code=='0'){
                            mainDivVM.$message({message:'分发成功',type:'success',duration:1000,onClose:function(){
                                mainDivVM.allocationVisible = false;
                                mainDivVM.searchTable();
                            }});
                        }else{
                            mainDivVM.$message({
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
                } else {
                    console.log('error submit!!');
                    return false;
                }
            });
        },
        allocationClueCancel(formName){//分发资源取消
            this.$refs[formName].resetFields();
            this.allocationVisible = false;
        },
        // 待审签约记录
        initSignRecordData(){
            var param = {};
            // axios.post('/sign/signRecord/listSignRecord',param)
            axios.post('/console/console/listSignRecord',param)
            .then(function (response) {
                var data =  response.data
                console.log('待审签约记录')
                console.log(data)
                if(data.code=='0'){
                    var resData = data.data;
                    mainDivVM.dataTable4= resData;                     
                }else{
                    mainDivVM.$message({message:data.msg,type:'error'});
                    console.error(data);
                }             
            })
            .catch(function (error) {
                console.log(error);
            }).then(function(){
            });            
        },
        pass(){//待审签约记录审核通过
            var rows = this.multipleSelection;
            if(rows.length<1){
                this.$message({
                   message: '请选择数据',
                   type: 'warning'
                 });
                return;
            }
            
            var title = "";
            var idArr = new Array();
            var isPass = true;
            for(var i=0;i<rows.length;i++){
                var curRow = rows[i];
                if(curRow.status!=1){
                    this.$message({message:'只允许审核待审核的数据',type:'warning'});
                    isPass=false;
                    break;
                }
                title += "【"+curRow.serialNum+""+curRow.customerName+"】 ";
                idArr.push(curRow.id);
            }
            if(!isPass){
                return;
            }
            
            this.$confirm('确定要将此 '+title+' 签约单审核通过吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                var param={};
                param.idList = idArr;
               
                axios.post('/sign/signRecord/passAuditSignOrder', param)
                .then(function (response) {
                    var resData = response.data;
                    if(resData.code=='0'){
                        mainDivVM.dialogFormVisible = false;
                        mainDivVM.$message({message:'操作成功',type:'success',duration:2000,onClose:function(){
                            mainDivVM.initSignRecordData();
                        }});
                    }else{
                        mainDivVM.$message({message:resData.msg,type:'error'});
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
                    message: '已取消审核'
                });          
            });
        },
        OpenRejectSignRecordDialog(){//待审签约记录驳回
            this.signRecordArrTitle='';
            this.dialogForm.reason='';
            var rows = this.multipleSelection;
            if(rows.length<1){
                this.$message({
                   message: '请选择数据',
                   type: 'warning'
                 });
                return;
            }
            var title = "";
            var isPass = true;
            for(var i=0;i<rows.length;i++){
                var curRow = rows[i];
                if(curRow.status!=1){
                    this.$message({message:'只允许审核待审核的数据',type:'warning'});
                    isPass=false;
                    break;
                }
                title += "【"+curRow.serialNum+""+this.getCustomerName(curRow.customerName)+"】 ";
            }
            
            if(!isPass){
                return;
            }
            
            this.signRecordArrTitle=title;
            this.dialogFormVisible=true;
        },
        recordClick(row) {//查看明细
            this.paymentDetailsShow=false;
            this.paymentDetailsShow2=false;
            this.paymentDetailsShow3=false;
            this.paymentDetailsShow4=false;
            var param = {};
            param.idList = [row.id];
            axios.post('/sign/signRecord/listPayDetailNoPage', param)
            .then(function (response) {
                var resData = response.data;
                if(resData.code=='0'){
                    debugger;
                    var payDetailData = resData.data;
                    if(payDetailData[1]){
                        mainDivVM.paymentDetailsShow=true;
                        mainDivVM.recordTable= payDetailData[1];
                    }
                    if(payDetailData[2]){
                        mainDivVM.paymentDetailsShow2=true;
                        console.info(payDetailData[2]);
                         mainDivVM.recordTable2= payDetailData[2];
                    }
                    if(payDetailData[3]){
                          mainDivVM.paymentDetailsShow3=true;
                          mainDivVM.recordTable3= payDetailData[3];
                    }
                    if(payDetailData[4]){
                        mainDivVM.paymentDetailsShow4=true;
                        mainDivVM.recordTable4= payDetailData[4];
                    }
                    
                }else{
                    mainDivVM.$message({message:resData.msg,type:'error'});
                    console.error(resData);
                }
                mainDivVM.recordDialog=true;
            
            })
            .catch(function (error) {
                 console.log(error);
            }).then(function(){
            });           
        },
        reasonClick(row) {//待审签约记录驳回原因
            this.rebutReason = row.rebutReason;
            this.reasonDialog=true;
        },
        getSignTypeText(row, column, value, index){
            var valText="";
            if(value==1){
                valText="一次性全款";
            }else if(value==2){
                valText="先交订金";
            }
            return valText;
        },
        getSignShopTypeText(row, column, value, index){
            var valText="";
            if(value==1){
                valText="创业店";
            }else if(value==2){
                valText="标准店";
            }else if(value==3){
                valText="白金店";
            }else if(value==4){
                valText="旗舰店";
            }else if(value==5){
                valText="区域代理";
            }
            return valText;
        },
        getPayModeText(row, column, value, index){
            var valText="";
            if(value==1){
                valText="现金";
            }else if(value==2){
                valText="POS";
            }else if(value==3){
                valText="转账";
            }
            return valText;
        },
        getStatusText(row, column, value, index){
               var valText="";
               if(value==0){
                   valText="驳回";
               }
               else if(value==1){
                   valText="审核中";
               }else if(value==2){
                   valText="有效";
               }
               return valText;
        },
        getTimeText(row, column, value, index){
            var valText="";
            if(value){
                valText =  moment(value).format("YYYY-MM-DD HH:mm:ss");
            }
            return valText;
        },
        getCustomerName(val){
            if(!val){
                return "";
            }
            return val;
        },
        submitDialogForm(formName) { //提交驳回原因
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    var param ={};  
                    var rows = this.multipleSelection;
                    var idArr = new Array();
                    var isPass = true;
                    for(var i=0;i<rows.length;i++){
                        var curRow = rows[i];
                        if(curRow.status!=1){
                            this.$message({message:'只允许审核待审核的数据',type:'warning'});
                            isPass=false;
                            break;
                        }
                       idArr.push(curRow.id);
                    }
                    if(!isPass){
                        return;
                    }
                    
                    
                    param.idList = idArr;
                    param.rebutReason = this.dialogForm.reason;
                    axios.post('/sign/signRecord/rejectSignOrder', param)
                    .then(function (response) {
                        var resData = response.data;
                        if(resData.code=='0'){
                            mainDivVM.dialogForm.reason='';
                            mainDivVM.dialogFormVisible = false;
                             mainDivVM.$message({message:'操作成功',type:'success',duration:2000,onClose:function(){
                                 mainDivVM.initSignRecordData();
                            }});
                        }else{
                            mainDivVM.$message({message:resData.msg,type:'error'});
                            console.error(resData);
                        }
                    
                    })
                    .catch(function (error) {
                         console.log(error);
                    }).then(function(){
                    });

                    
                } else {
                    console.log('error submit!!');
                    return false;
                }
            });
        },
        // 待审批到访记录
        initCustomerVisitRecord(){//初始列表 
            var param = {};
            // axios.post('/visit/visitRecord/listVisitRecord',param)
            param.isVisit=1;
            axios.post('/console/console/listVisitRecord',param)            
            .then(function (response) {
                var data =  response.data
                console.log('待审批到访记录')
                console.log(data)
                if(data.code=='0'){
                    var resData = data.data;
                    mainDivVM.dataTable2= resData;                    
                }else{
                    mainDivVM.$message({message:data.msg,type:'error'});
                    console.error(data);
                }             
            })
            .catch(function (error) {
                console.log(error);
            }).then(function(){
            });            
        },
        getVisitTypeText(row, column, value, index){
            var valText="";
            if(value==1){
                valText="预约来访";
            }else if(value==2){
                valText="慕名来访";
            }else if(value==3){
                valText="临时来访";
            }
            return valText;
        },
        getVisitNumText(row, column, value, index){
            var valText="";
            if(value==1){
                valText="首次到访";
            }else if(value==2){
                valText="二次来访";
            }else if(value>=3){
                valText="多次来访";
            }
            return valText;
        },
        OpenRejectVisitRecordDialog(){//待审批到访记录驳回
           this.signRecordArrTitle='';
           var rows = this.multipleSelection;
           if(rows.length<1){
               this.$message({
                  message: '请选择数据',
                  type: 'warning'
                });
               return;
           }
           var title = "";
           for(var i=0;i<rows.length;i++){
               var curRow = rows[i];
               title += "【"+curRow.serialNum+""+curRow.customerName+"】";
           }
           this.signRecordArrTitle=title;
           
           this.dialogFormVisibleVisit=true;
        },
        passVisit(){//到访审核通过
            var rows = this.multipleSelection;
            if(rows.length<1){
                this.$message({
                   message: '请选择数据',
                   type: 'warning'
                 });
                return;
            }            
            var title = "";
            var idArr = new Array();
            var isPass = true;
            for(var i=0;i<rows.length;i++){
                var curRow = rows[i];
                if(curRow.status!=1){
                    this.$message({message:'只允许审核待审核的数据',type:'warning'});
                    isPass=false;
                    break;
                }
                title += "【"+curRow.serialNum+""+curRow.customerName+"】 ";
                idArr.push(curRow.id);
            }
            if(!isPass){
                return;
            }
            
            this.$confirm('确定要将此 '+title+' 到访记录定为有效吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                var param={};
                param.idList = idArr;
               
                axios.post('/visit/visitRecord/passAuditSignOrder', param)
                .then(function (response) {
                    var resData = response.data;
                    if(resData.code=='0'){
                        mainDivVM.dialogFormVisibleVisit = false;
                        mainDivVM.$message({message:'操作成功',type:'success',duration:2000,onClose:function(){
                            mainDivVM.initCustomerVisitRecord();
                        }});
                    }else{
                        mainDivVM.$message({message:resData.msg,type:'error'});
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
                    message: '已取消审核'
                });          
            });
        },
        submitDialogFormVisit(formName) {//到访提交
            this.$refs[formName].validate((valid) => {
                if (valid) {                  
                    var param ={}; 
                    var rows = this.multipleSelection;
                    var idArr = new Array();
                    for(var i=0;i<rows.length;i++){
                        var curRow = rows[i];
                       idArr.push(curRow.id);
                    }
                    param.idList = idArr;
                    param.rebutReason = this.dialogForm.reason;
                    axios.post('/visit/visitRecord/rejectVisitRecord', param)
                    .then(function (response) {
                        var resData = response.data;
                        if(resData.code=='0'){
                            mainDivVM.dialogForm.reason='';
                            mainDivVM.dialogFormVisibleVisit = false;
                            mainDivVM.$message({message:'操作成功',type:'success',duration:2000,onClose:function(){
                                mainDivVM.initCustomerVisitRecord();
                            }});
                        }else{
                            mainDivVM.$message({message:resData.msg,type:'error'});
                            console.error(resData);
                        }                    
                    })
                    .catch(function (error) {
                         console.log(error);
                    }).then(function(){
                    });
                } else {
                    return false;
                }
            });
        },
        // 待审批未到访记录
        initCustomerUnVisitRecord(){//初始列表 
            var param = {};
            // axios.post('/visit/visitRecord/listVisitRecord',param)
            param.isVisit=0;
            axios.post('/console/console/listVisitRecord',param)            
            .then(function (response) {
                var data =  response.data
                console.log('待审批未到访记录')
                console.log(data)
                if(data.code=='0'){
                    var resData = data.data;
                    mainDivVM.dataTable3= resData;                    
                }else{
                    mainDivVM.$message({message:data.msg,type:'error'});
                    console.error(data);
                }             
            })
            .catch(function (error) {
                console.log(error);
            }).then(function(){
            });            
        },
        OpenRejectUnVisitRecordDialog(){//待审批未到访记录驳回
           this.signRecordArrTitle='';
           var rows = this.multipleSelection;
           if(rows.length<1){
               this.$message({
                  message: '请选择数据',
                  type: 'warning'
                });
               return;
           }
           var title = "";
           for(var i=0;i<rows.length;i++){
               var curRow = rows[i];
               title += "【"+curRow.serialNum+""+curRow.customerName+"】";
           }
           this.signRecordArrTitle=title;
           
           this.dialogFormVisibleUnVisit=true;
        },
        passUnVisit(){//未到访审核通过
            var rows = this.multipleSelection;
            if(rows.length<1){
                this.$message({
                   message: '请选择数据',
                   type: 'warning'
                 });
                return;
            }            
            var title = "";
            var idArr = new Array();
            var isPass = true;
            for(var i=0;i<rows.length;i++){
                var curRow = rows[i];
                if(curRow.status!=1){
                    this.$message({message:'只允许审核待审核的数据',type:'warning'});
                    isPass=false;
                    break;
                }
                title += "【"+curRow.serialNum+""+curRow.customerName+"】 ";
                idArr.push(curRow.id);
            }
            if(!isPass){
                return;
            }
            
            this.$confirm('确定要将此 '+title+' 未到访记录定为有效吗？', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                var param={};
                param.idList = idArr;
               
                axios.post('/visit/visitRecord/passAuditSignOrder', param)
                .then(function (response) {
                    var resData = response.data;
                    if(resData.code=='0'){
                        mainDivVM.dialogFormVisibleUnVisit = false;
                        mainDivVM.$message({message:'操作成功',type:'success',duration:2000,onClose:function(){
                            mainDivVM.initCustomerVisitRecord();
                        }});
                    }else{
                        mainDivVM.$message({message:resData.msg,type:'error'});
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
                    message: '已取消审核'
                });          
            });
        },
        submitDialogFormVisit(formName) {//未到访提交
            this.$refs[formName].validate((valid) => {
                if (valid) {                  
                    var param ={}; 
                    var rows = this.multipleSelection;
                    var idArr = new Array();
                    for(var i=0;i<rows.length;i++){
                        var curRow = rows[i];
                       idArr.push(curRow.id);
                    }
                    param.idList = idArr;
                    param.rebutReason = this.dialogForm.reason;
                    axios.post('/visit/visitRecord/rejectVisitRecord', param)
                    .then(function (response) {
                        var resData = response.data;
                        if(resData.code=='0'){
                            mainDivVM.dialogForm.reason='';
                            mainDivVM.dialogFormVisibleUnVisit = false;
                            mainDivVM.$message({message:'操作成功',type:'success',duration:2000,onClose:function(){
                                mainDivVM.initCustomerUnVisitRecord();
                            }});
                        }else{
                            mainDivVM.$message({message:resData.msg,type:'error'});
                            console.error(resData);
                        }                    
                    })
                    .catch(function (error) {
                         console.log(error);
                    }).then(function(){
                    });
                } else {
                    return false;
                }
            });
        },

    },
    created(){
        // 工作台
        this.initBoard();
        // 待分配邀约来访客户
        this.searchTable();
        // 待审签约记录
        this.initSignRecordData();
        // 待审批到访记录
        this.initCustomerVisitRecord();
        // 待审未批到访记录
        this.initCustomerUnVisitRecord();
    },
    mounted(){
        document.getElementById('mainDiv').style.display = 'block';
    }
});