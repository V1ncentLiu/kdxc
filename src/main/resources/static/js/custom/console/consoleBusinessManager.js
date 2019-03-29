var mainDivVM = new Vue({
    el: '#mainDiv',
    data: {
        // 工作台
        activeName:'1',
        activeName2:'1',
        activeName3:'1',
        activeName4:'1',
        activeName5:'1',
        activeName6:'1',
        activeName7:'1',
        visitedNum:'',//当月到访数   
        signedNum:'',//当月签约数
        secondVisitedNum:'',//当月二次到访数
        secondSignedNum:'',//当月二次来访签约数
        unPaymentNum:'',//未收齐尾款笔数
        workDay:'',//工作天数
        //公告  
        afficheBox:false,     
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
        // 待处理邀约来访客户
        editableTabsValue: 0, //tabs标签
        editableTabs: [],
        tabIndex: 2,
        updateVisitRecordDialogVisible:false,
        addVisitRecordDialogVisible:false,
        notVisitdDialogVisible:false,
        notVisitFlagDialogVisible:false,
        showVisitAduitDialogVisible:false,
        dialogFormSigningVisible:false,
        dialogUpdateFormSigningVisible:false,
        existsSign:false,
        curSignStatus:"",
        isAllMoney: false,
        payTypeSelect:false,
        notEditRebutSign:false,
        formLabelWidth:"150px",
        tableData:[],
        visitTableData:[],
        showVisitAduitDatas:{
            one:[],
            two:[],
            three:[],
            oneNotSignReason:"",
            twoNotSignReason:"",
            threeNotSignReason:"",
            oneRebutReason:"",
            twoRebutReason:"",
            threeRebutReason:"",
            oneShow:true,
            twoShow:false,
            threeShow:false,
            oneRebutShow:false,
            twoRebutShow:false,
            threeRebutShow:false,
            oneNotSignShow:false,
            twoNotSignShow:false,
            threeNotSignShow:false
        },
        teleketGroupArr:dzList,
        teleketSaleArr:dxgwList,
        tasteProArr:proSelect,
        signArr:[{value:0,name:'未签约'},{value:1,name:'已签约'}],
        isSignArr:[{value:0,name:'否'},{value:1,name:'是'}],
        visitArr:[{value:-1,name:'待处理'},{value:0,name:'未到访'},{value:1,name:'首次到访'},{value:2,name:'2次到访'},{value:3,name:'多次到访'}],
        visitTypeArr:[{value:1,name:'预约来访'},{value:2,name:'慕名来访'},{value:3,name:'临时来访'}],
        option2s: [{value: 1, label: '一次性全款'},{value: 2, label: '先交订金'}],
        option3s: [{value: '1', label: '现金'},{value: '2', label: 'POS'},{value: '3', label: '转账'}],
        payTypeArr:[{value: "1", label: '全款'},{value: "2", label: '定金'},{value: "3", label: '追加定金'},{value: "4", label: '尾款'}],
        payTypeArr1:[{value: "1", label: '全款'},{value: "2", label: '定金'},{value: "3", label: '追加定金'},{value: "4", label: '尾款'}],
        vistitStoreTypeArr:[],
        provinceArr:[],
        districtArr:[],
        cityArr:[],
        companyArr:[],
        projectArr:proSelect,
        multipleSelection:[],
        notVisitFlagRules:{
            notVisitReason: [
                { required: true, message: '未到访原因为必填项', trigger: 'blur' },
                { min: 1, max: 100, message: '长度在 1 到 100 个字符', trigger: 'blur' }
            ],
        },
        notVisitFlag:{
            str:"",
            clueIds:[],
            notVisitReason:""
        },
        queryForm:{
            visitStatus:"",
            isSign:"",
            teleGroupId:"",
            teleSaleId:"",
            tasteProjectId:"",
            cusName:"",
            reserveTime1:"",
            reserveTime2:"",
            createTime1:"",
            createTime2:"",
            allocateTime1:"",
            allocateTime2:"",
        },
        addVisitRecord:{
            clueId:"",
            companyid:"",
            vistitTime:"",
            customerName:"",
            projectId:"",
            visitType:1,
            vistitStoreType:"",
            signProvince:"",
            signCity:"",
            signDistrict:"",
            visitCity:"",
            isSign:1,
            visitPeopleNum:"",
            notSignReason:""
        },
        updateVisitRecord:{
            id:"",
            clueId:"",
            companyid:"",
            vistitTime:"",
            customerName:"",
            projectId:"",
            visitType:1,
            vistitStoreType:"",
            signProvince:"",
            signCity:"",
            signDistrict:"",
            visitCity:"",
            isSign:1,
            // visitPeopleNum:"",
            notSignReason:"",
            rebutTime:"",
            rebutReason:""
        },
        formSigning: {
            clueId:"",
            signNo:'',
            customerName: '',
            idCard: '',
            phone: '',
            signCompanyId:'',
            signProjectId: '',
            signProvince:'',
            signCity: '',
            signDictrict:'',
            signShopType:'',
            amountReceivable:'',
            firstToll:'',
            preferentialAmount:'',
            signType:1,
            giveAmount:'',

            payType:1,
            payMode:'',
            amountReceived: '',
            amountBalance: '',
            makeUpTime:'',
            payTime: '',
            amountPerformance:''
        },
        updateFormSigning: {
            signId:"",
            clueId:"",
            signNo:'',
            customerName: "",
            idCard: '',
            phone: '',
            signCompanyId:'',
            signProjectId: '',
            signProvince:'',
            signCity: '',
            signDictrict:'',
            signShopType:'',
            amountReceivable:'',
            firstToll:'',
            preferentialAmount:'',
            signType:"1",
            giveAmount:'',
            rebutTime:"",
            rebutReason:"",

            payDetailId:"",
            payType:'1',
            payMode:'',
            amountReceived: '',
            amountBalance: '',
            makeUpTime:'',
            payTime: '',
            amountPerformance:'',
            remarks: '' //备注
        },
        pager:{
            total: 0,
            currentPage: 1,
            pageSize: 20,
        },
        //rules
        rules: {
            customerName: [
                { required: true, message: '请输入客户姓名', trigger: 'blur' }
            ],
            idCard: [
                { required: true, message: '请输入客户身份证号', trigger: 'blur' }
            ],
            phone: [
                { required: true, message: '请输入客户联系电话', trigger: 'blur' }
            ],
            signCompanyId: [
                { required: true, message: '请选择签约餐饮公司', trigger: 'change' }
            ],
            signProjectId: [
                { required: true, message: '请选择签约项目', trigger: 'change' }
            ],
            signProvince: [
                { required: true, message: '请选择签约省份', trigger: 'change' }
            ],
            signCity: [
                { required: true, message: '请选择签约城市', trigger: 'change' }
            ],
            signDictrict: [
                { required: true, message: '请选择签约区/县', trigger: 'change' }
            ],
            signShopType: [
                { required: true, message: '请选择签约店型', trigger: 'change' }
            ],
            amountReceivable: [
                { required: true, message: '请输入应收金额', trigger: 'blur' }
            ],
            firstToll: [
                { required: true, message: '请输入路费（报首次）', trigger: 'blur' }
            ],
            preferentialAmount: [
                { required: true, message: '请输入优惠金额', trigger: 'blur' }
            ],
            signType: [
                { required: true, message: '请选择签约类型', trigger: 'change' }
            ],
            giveAmount: [
                { required: true, message: '请输入赠送金额', trigger: 'blur' }
            ],
            // payType: [
            //  { required: true, message: '请选择付款类型', trigger: 'change' }
            // ],
            payMode: [
                { required: true, message: '请选择付款方式', trigger: 'change' }
            ],
            payTime: [
                { type: 'date', required: true, message: '请选择时间', trigger: 'change' }
            ],
            amountReceived: [
                { required: true, message: '请输入实收金额', trigger: 'blur' }
            ],
            amountBalance: [
                { required: true, message: '请输入余款金额', trigger: 'blur' }
            ],
            makeUpTime: [
                { type: 'date', required: true, message: '请选择预计余款补齐时间', trigger: 'change' }
            ],
            // performanceAmount: [
            //  { required: true, message: '请输入业绩金额', trigger: 'blur' }
            // ],
            isRemoteSign: [ //是否远程签约
                { required: true, message: '请选择是否远程签约', trigger: 'change' }
            ]
        },
    },
    computed: {
        updateFormSigningAmountPerformance(){
            var aone = parseFloat(this.updateFormSigning.amountReceived);
            var atwo = parseFloat(this.updateFormSigning.firstToll);
            if(isNaN(aone)) aone = 0
            if(isNaN(atwo)) atwo = 0
            return aone + atwo
        },
        formSigningAmountPerformance(){
            var aone = parseFloat(this.formSigning.amountReceived);
            var atwo = parseFloat(this.formSigning.firstToll);
            if(isNaN(aone)) aone = 0
            if(isNaN(atwo)) atwo = 0
            return aone + atwo
        }
    },
    methods: {
        gotoBusinessMyCustomer(){//跳转我的客户
            window.location.href="/aggregation/businessMyCustomer/listPage"; 
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
                if(response.data.data&&response.data.data.length>0){
                    mainDivVM.items=response.data.data;
                    mainDivVM.afficheBox=true
                }else{
                    mainDivVM.afficheBox=false
                }
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
            // 当月到访数 当月签约数 当月二次到访数 当月二次来访签约数 未收齐尾款笔数
            param={};
            axios.post('/console/console/countCurMonthNum',param).then(function (response) {
                console.log('当月到访数 当月签约数 当月二次到访数 当月二次来访签约数 未收齐尾款笔数')                
                console.log(response.data)                
                mainDivVM.visitedNum=response.data.data.visitedNum;
                mainDivVM.signedNum=response.data.data.signedNum;
                mainDivVM.secondVisitedNum=response.data.data.secondVisitedNum;
                mainDivVM.secondSignedNum=response.data.data.secondSignedNum;
                mainDivVM.unPaymentNum=response.data.data.unPaymentNum;
            });    
            // 工作天数
            param={};
            axios.post('/console/console/getWorkDay',param).then(function (response) {
                console.log('工作天数')                
                console.log(response.data)                
                mainDivVM.workDay=response.data.data;
            });
        },
        // 待处理邀约来访客户
        tabClick(tab, event){
            this.updateFormSigning = this.editableTabs[tab.index].updateFormSigning;
        },
        formatVisitTime(row){
            function dateFtt(fmt,date)
            { //author: meizz
                var o = {
                    "M+" : date.getMonth()+1,                 //月份
                    "d+" : date.getDate(),                    //日
                    "h+" : date.getHours(),                   //小时
                    "m+" : date.getMinutes(),                 //分
                    "s+" : date.getSeconds(),                 //秒
                    "q+" : Math.floor((date.getMonth()+3)/3), //季度
                    "S"  : date.getMilliseconds()             //毫秒
                };
                if(/(y+)/.test(fmt))
                    fmt=fmt.replace(RegExp.$1, (date.getFullYear()+"").substr(4 - RegExp.$1.length));
                for(var k in o)
                    if(new RegExp("("+ k +")").test(fmt))
                        fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
                return fmt;
            }
            return dateFtt("yyyy-MM-dd hh:mm", new Date(row.vistitTime));
        },
        formatVisit(row, column){
            if(row.visitStatus==0){
                resVal='未到访（原因）';
            }else  if(row.visitStatus>0&&row.visitAuditStatus==1){
                resVal='审核中';
            }else  if(row.visitStatus>0&&row.visitAuditStatus==0){
                resVal='到访被驳回';
            }else  if(row.visitStatus>0&&row.visitAuditStatus==2){
                resVal='查看到访记录';
            }else{
                //没有状态的时候
                resVal='添加到访记录';
            }
            return resVal;
        },

        formatVisitStatus(row, column){
            var  resVal='待处理';
            if(row.visitStatus==0){
                resVal='未到访';
            }else  if(row.visitStatus==1){
                resVal='首次到访';
            }else  if(row.visitStatus==2){
                resVal='2次到访';
            }else  if(row.visitStatus==3){
                resVal='多次到访';
            }
            return resVal;
        },
        formatSignStatus(row, column){
            var  resVal='未签约';
            if(row.isSign==1){
                resVal='已签约';
            }
            return resVal;
        },
        setAddPerformanceAmount(value){
            this.formSigning.performanceAmount = this.formSigning.amountReceived
        },
        setUpdatePerformanceAmount(value){
                this.updateFormSigning.performanceAmount = this.updateFormSigning.amountReceived
        },
        handleSelectionChange(val) {
            this.multipleSelection = val;
        },
        changePayType(val){
            if(val==3){
                this.isAllMoney = true;
            }else{
                this.isAllMoney = false;
            }
        },
        notVisit(){
            var rows = mainDivVM.multipleSelection;
            if(rows.length==0){
                this.$message({message: '请选择数据', type: 'warning'});
                return false;
            }else{
                var names = "";
                var rowIds = [];
                for(var i=0;i<rows.length;i++){
                    if(rows[i].visitStatus==0||rows[i].visitStatus==1||rows[i].visitStatus==2||rows[i].visitStatus==3){
                        this.$message({message: '勾选数据中，包含了已到访或是已标记的客户', type: 'warning'});
                        return false;
                    }
                    names = names + '【'+rows[i].cusName+'】';
                    rowIds.push(rows[i].clueId);
                }
                var str = '确定要将此'+names+'标记为未到访吗?';
                this.notVisitFlag.clueIds = rowIds;
                this.notVisitFlag.str  = str;
                this.notVisitFlag.notVisitReason = "";
                this.notVisitFlagDialogVisible = true;
            }
        },
        saveNotVisit(){
            var param = this.notVisitFlag;
            // 设置 clueid
            this.$refs['notVisitFlag'].validate((valid) => {
                if (valid) {
                    axios.post("/aggregation/businessMyCustomer/notVisit", param)
                        .then(function (response) {
                            if (response.data.code == 0) {
                                mainDivVM.$message({type: 'success', message: '标记成功!',duration:2000,onClose:function(){
                                        mainDivVM.pager.pageNum = 1
                                        mainDivVM.initList();
                                        mainDivVM.notVisitFlagDialogVisible = false;
                                        mainDivVM.addVisitRecord.clueId = "";
                                    }});
                            } else {
                                mainDivVM.$message.error(response.data.msg);
                            }
                        }).catch(function (error) {console.log(error);});
                } else {return false;}
            });
        },
        showCustomerDetail(row, column){ // 客户详情
            window.location.href='/bus/BusinessCustomer/viewCustomerInfo?clueId='+row.clueId;
        },
        editCustomerDetail(row, column){ // 客户维护
            window.location.href='/bus/BusinessCustomer/editCustomerInfo?clueId='+row.clueId;
        },
        currentProvince(selVal){
            if(!selVal){
                return false;
            }
            param={};
            param.type="1";
            param.name=selVal;
            axios.post('/area/sysregion/querySysRegionByParam',param).then(function (response) {
                mainDivVM.cityArr=response.data.data.data;
            });
        },
        currentCity(selVal){
            if(!selVal){
                return false;
            }
            param={};
            param.type="2";
            param.name=selVal;
            axios.post('/area/sysregion/querySysRegionByParam',param).then(function (response) {
                mainDivVM.districtArr=response.data.data.data;
            });
        },
        saveVisitRecord(){
            var param = this.addVisitRecord;
            param.rebutTime = new Date(param.rebutTime)
            param.createTime = new Date(param.createTime)
            // 设置 clueid
            this.$refs['addVisitRecord'].validate((valid) => {
                if (valid) {
                    axios.post("/busVisitRecord/insert", param)
                        .then(function (response) {
                            if (response.data.code == 0) {
                                mainDivVM.$message({type: 'success', message: '来访记录保存成功!',duration:2000,onClose:function(){
                                        mainDivVM.pager.pageNum = 1
                                        mainDivVM.initList();
                                        mainDivVM.addVisitRecordDialogVisible = false;
                                        mainDivVM.addVisitRecord.clueId = "";
                                    }});
                            } else {
                                mainDivVM.$message.error(response.data.msg);
                            }
                        }).catch(function (error) {console.log(error);});
                } else {return false;}
            });
        },

        updateVisitRecordMethod(){
            // 设置 clueid
            this.$refs['updateVisitRecord'].validate((valid) => {
                if (valid) {
                    var param = {};
                    param.id=this.updateVisitRecord.id;
                    param.clueId=this.updateVisitRecord.clueId;
                    param.companyid=this.updateVisitRecord.companyid;
                    param.vistitTime=new Date(this.updateVisitRecord.vistitTime);
                    param.customerName=this.updateVisitRecord.customerName;
                    param.projectId=this.updateVisitRecord.projectId;
                    param.visitType=this.updateVisitRecord.visitType;
                    param.vistitStoreType=this.updateVisitRecord.vistitStoreType;
                    param.signProvince=this.updateVisitRecord.signProvince;
                    param.signCity=this.updateVisitRecord.signCity;
                    param.signDistrict=this.updateVisitRecord.signDistrict;
                    param.visitCity=this.updateVisitRecord.visitCity;
                    param.isSign=this.updateVisitRecord.isSign;
                    param.visitPeopleNum=this.updateVisitRecord.visitPeopleNum;
                    if( param.isSign==1){ // 已签约
                        param.notSignReason="";
                    }else{
                        param.notSignReason=this.updateVisitRecord.notSignReason;
                    }
                    param.notSignReason=this.updateVisitRecord.notSignReason;
                    if(param.rebutTime){
                        param.rebutTime = new Date(param.rebutTime)
                    }
                    if(param.vistitTime){
                        param.vistitTime = new Date(param.vistitTime)
                    }

                    axios.post("/busVisitRecord/update", param)
                        .then(function (response) {
                            if (response.data.code == 0) {
                                mainDivVM.$message({type: 'success', message: '来访提交成功!',duration:2000,onClose:function(){
                                        mainDivVM.pager.pageNum = 1
                                        mainDivVM.initList();
                                        mainDivVM.updateVisitRecordDialogVisible = false;
                                        mainDivVM.updateVisitRecord.clueId = "";
                                        mainDivVM.updateVisitRecord.id = "";
                                    }});
                            } else {
                                mainDivVM.$message.error(response.data.msg);
                            }
                        }).catch(function (error) {console.log(error);});
                } else {return false;}
            });
        },

        showNotSignReason(val){
            if(this.addVisitRecordDialogVisible){
                if( val==1){
                    this.addVisitRecord.notSignReason = '';
                }
            }else{
                if( val==1){
                    console.log(this.updateVisitRecord.notSignReason)
                    this.updateVisitRecord.notSignReason = '';
                }
            }

            if(val==0){
                //  将隐藏换成元素添加 和 移除 ，这样的动态就会变得好看
                document.getElementById("notSignReason").setAttribute("style","display:block")
                document.getElementById("goSignButton").setAttribute("style","visibility:hidden")
                // document.getElementById("goSignButton").setAttribute("style","display:none")
            }else if(val==1){
                document.getElementById("notSignReason").setAttribute("style","display:none")
                document.getElementById("goSignButton").setAttribute("style","visibility:visible")
                //   document.getElementById("goSignButton").setAttribute("style","display:block")
            }
        },
        openEditVisitRecord(row){
           // 打开编辑到访记录
            var param ={};
            param.id = row.clueId;
            if(row.signAuditStatus==0||row.signAuditStatus==1||row.signAuditStatus==2){
                mainDivVM.existsSign =true;
                mainDivVM.curSignStatus = row.signAuditStatus;
            }

            axios.post('/busVisitRecord/echo',param).then(function (response) {
                mainDivVM.addVisitRecord = response.data.data;
                mainDivVM.addVisitRecord.clueId=row.clueId;
                 mainDivVM.addVisitRecord.vistitTime = new Date()
                mainDivVM.addVisitRecordDialogVisible = true;
                mainDivVM.currentProvince(mainDivVM.addVisitRecord.signProvince)
                mainDivVM.currentCity(mainDivVM.addVisitRecord.signCity)
                mainDivVM.showNotSignReason(mainDivVM.addVisitRecord.isSign);
            });
        },
        showNotVisitReason(row){
            // 展示未到访原因
            var param={};
            param.id = row.clueId;
            axios.post('/aggregation/businessMyCustomer/notVisitReason',param).then(function (response) {
                mainDivVM.notVisitdDialogVisible = true;
                mainDivVM.notVisitFlag.str = response.data.data.notVisitReason
            });
        },

        showVisitRecord(row){
            // 展示到访记录
            this.showVisitAduitDialogVisible = true;
            var param = {};
            param.clueId = row.clueId
            axios.post('/busVisitRecord/queryList',param).then(function (response) {
                if(null===response||response.data==null||response.data.code!='0'){
                    if(response.data.code!='0'){
                        mainDivVM.$message({message: response.data.msg, type: 'warning'});
                    }
                    return false;
                }else{
                    //mainDivVM.showVisitAduitDatas.visitTableData =response.data.data;
                    // mainDivVM.showVisitAduitDatas.visitStatus = row.visitStatus
                    // if(response.data.data.length>0){
                    //     mainDivVM.showVisitAduitDatas.notSignReason = response.data.data[0].notSignReason
                    // }
                    var dataList = response.data.data;
                    // 首次到访
                    var first = [];
                    if(dataList.length>=1){
                        first.push(dataList[dataList.length-1])
                        mainDivVM.showVisitAduitDatas.one = first;
                        if(first[0].isSign==0){
                            mainDivVM.showVisitAduitDatas.oneNotSignReason = first[0].notSignReason;
                            mainDivVM.showVisitAduitDatas.oneNotSignShow = true;
                        }
                        if(first[0].rebutReason){
                            mainDivVM.showVisitAduitDatas.oneRebutReason = first[0].rebutReason;
                            mainDivVM.showVisitAduitDatas.oneRebutShow = true;
                        }
                    }else{
                        mainDivVM.showVisitAduitDatas.one = first;
                    }

                    // 二次到访
                    var two = [];
                    if(dataList.length>=2){
                        two.push(dataList[dataList.length-2])
                        mainDivVM.showVisitAduitDatas.two = two;
                        mainDivVM.showVisitAduitDatas.twoShow = true;
                        if(two[0].isSign==0){
                            mainDivVM.showVisitAduitDatas.twoNotSignReason = two[0].notSignReason;
                            mainDivVM.showVisitAduitDatas.twoNotSignShow = true;
                        }
                        if(two[0].rebutReason){
                            mainDivVM.showVisitAduitDatas.twoRebutReason = two[0].rebutReason;
                            mainDivVM.showVisitAduitDatas.twoRebutShow = true;
                        }
                    }else{
                        mainDivVM.showVisitAduitDatas.two = two;
                        mainDivVM.showVisitAduitDatas.twoShow = false;
                    }
                    // 多次到访
                    var three = []
                    if(dataList.length>=3){
                        three = dataList.slice(0,dataList.length-2)
                        mainDivVM.showVisitAduitDatas.three = three;
                        mainDivVM.showVisitAduitDatas.threeShow = true;
                        if(three[0].isSign==0){
                            mainDivVM.showVisitAduitDatas.threeNotSignReason = three[0].notSignReason;
                            mainDivVM.showVisitAduitDatas.threeNotSignShow = true;
                        }
                        if(three[0].rebutReason){
                            mainDivVM.showVisitAduitDatas.threeRebutReason = three[0].rebutReason;
                            mainDivVM.showVisitAduitDatas.threeRebutShow = true;
                        }
                    }else{
                        mainDivVM.showVisitAduitDatas.three = three;
                        mainDivVM.showVisitAduitDatas.threeShow = false;
                    }
                //
                }

            })
        },
        toVisitRecordPage(row){
            window.location.href='/busVisitRecord/visitRecordPage?clueId='+row.clueId+"&visitStatus="+row.visitStatus+"&signAuditStatus="+row.signAuditStatus;
        },
        editRebutVisitRecord(row){
            var param ={};
            console.log(row)
            param.id = row.visitId
            if(row.signAuditStatus==0||row.signAuditStatus==1||row.signAuditStatus==2){
                mainDivVM.existsSign =true;
                mainDivVM.curSignStatus = row.signAuditStatus;
            }
            axios.post('/busVisitRecord/one',param).then(function (response) {
                console.log(response)
                if(null===response||response.data==null||response.data.code!='0'){
                    if(response.data.code!='0'){
                        mainDivVM.$message({message: response.data.msg, type: 'warning'});
                    }
                    return false;
                }else{
                    mainDivVM.updateVisitRecord =response.data.data;
                    mainDivVM.updateVisitRecord.clueId=row.clueId;
                    mainDivVM.updateVisitRecord.id = row.visitId
                    mainDivVM.updateVisitRecordDialogVisible = true;
                    mainDivVM.showNotSignReason(mainDivVM.updateVisitRecord.isSign);
                    mainDivVM.currentProvince(mainDivVM.updateVisitRecord.signProvince)
                    mainDivVM.currentCity(mainDivVM.updateVisitRecord.signCity)
                }
            })
        },
        goEditSign(){
            if(!mainDivVM.existsSignMessage()){
                return false;
            }
            var param = this.addVisitRecord;
            param.rebutTime = new Date(param.rebutTime)
            param.createTime = new Date(param.createTime)
            this.$refs['addVisitRecord'].validate((valid) => {
                if (valid) {
                    axios.post("/busVisitRecord/insert", param)
                        .then(function (response) {
                            if (response.data.code == 0) {
                                mainDivVM.$message({type: 'success', message: '来访记录保存成功!',duration:2000,onClose:function(){
                                        mainDivVM.initList();
                                        mainDivVM.updateVisitRecordDialogVisible = false;
                                        mainDivVM.addVisitRecordDialogVisible = false;
                                        //  弹出签约单添加弹窗
                                       // mainDivVM.formSigning.clueId = mainDivVM.addVisitRecord.clueId;
                                       // mainDivVM.dialogFormSigningVisible = true;
                                        mainDivVM.toSign(mainDivVM.addVisitRecord)
                                    }});
                            } else {
                                mainDivVM.$message.error(response.data.msg);
                            }
                        }).catch(function (error) {console.log(error);});
                } else {return false;}
            });
        },

        toSign(val){
            if(mainDivVM.curSignStatus==0&&mainDivVM.curSignStatus!==""){ // 当前签约单的状态 == 0 存在驳回签约单。
                mainDivVM.editRebutSign(val)
            }else{
                mainDivVM.editSign(val)
            }
        },
        goUpdateEditSign(){
            if(!mainDivVM.existsSignMessage()){
                return false;
            }
            // 设置 clueid
            this.$refs['updateVisitRecord'].validate((valid) => {
                if (valid) {
                    var param = {};
                    param.id=this.updateVisitRecord.id;
                    param.clueId=this.updateVisitRecord.clueId;
                    param.companyid=this.updateVisitRecord.companyid;
                    param.vistitTime=new Date(this.updateVisitRecord.vistitTime);
                    param.customerName=this.updateVisitRecord.customerName;
                    param.projectId=this.updateVisitRecord.projectId;
                    param.visitType=this.updateVisitRecord.visitType;
                    param.vistitStoreType=this.updateVisitRecord.vistitStoreType;
                    param.signProvince=this.updateVisitRecord.signProvince;
                    param.signCity=this.updateVisitRecord.signCity;
                    param.signDistrict=this.updateVisitRecord.signDistrict;
                    param.visitCity=this.updateVisitRecord.visitCity;
                    param.isSign=this.updateVisitRecord.isSign;
                    param.visitPeopleNum=this.updateVisitRecord.visitPeopleNum;
                    param.notSignReason=this.updateVisitRecord.notSignReason;
                    if( param.rebutTime){
                        param.rebutTime = new Date(param.rebutTime)
                    }
                   if( param.createTimeTime){
                       param.vistitTime = new Date(param.createTimeTime)
                   }

                    console.log(param)
                    axios.post("/busVisitRecord/update", param)
                        .then(function (response) {
                            if (response.data.code == 0) {
                                mainDivVM.$message({type: 'success', message: '来访提交成功!',duration:2000,onClose:function(){
                                        mainDivVM.initList();
                                        mainDivVM.updateVisitRecordDialogVisible = false;
                                        //  弹出签约单添加弹窗
                                        // mainDivVM.formSigning.clueId = mainDivVM.addVisitRecord.clueId;
                                        // mainDivVM.dialogFormSigningVisible = true;
                                        // mainDivVM.editSign(mainDivVM.updateVisitRecord)
                                        mainDivVM.toSign(mainDivVM.updateVisitRecord)
                                    }});
                            } else {
                                mainDivVM.$message.error(response.data.msg);
                            }
                        }).catch(function (error) {console.log(error);});
                } else {return false;}
            });
        },
        existsSignMessage(){
            if(this.existsSign){
                if(this.curSignStatus==0){
                    mainDivVM.$message.error("存在被驳回签约单");
                }else  if(this.curSignStatus==1){
                    mainDivVM.$message.error("签约单正在审核中");
                }else  if(this.curSignStatus==2){
                    mainDivVM.$message.error("签约单已经存在");
                }
            }
            if(this.curSignStatus==0){
                return true;
            }
            return false;
        },
        editSign(row){
            // this.formSigning.clueId=row.clueId;
            // this.dialogFormSigningVisible = true;
            var param = {};
            param.id = row.clueId;
            axios.post('/businesssign/echo',param).then(function (response) {
                mainDivVM.formSigning = response.data.data;
                mainDivVM.formSigning.clueId=row.clueId;
                mainDivVM.dialogFormSigningVisible = true;
                mainDivVM.currentProvince(mainDivVM.formSigning.signProvince)
                mainDivVM.currentCity(mainDivVM.formSigning.signCity)
            });
        },
        showSignDetail(row){

        },
        editRebutSign(row){
            var param ={};
            console.log(row)
            param.clueId = row.clueId
            axios.post('/businesssign/queryRebuts',param).then(function (response) {
                console.log(response)                  
                if(null===response||response.data==null||response.data.code!='0'){
                    if(response.data.code!='0'){
                        mainDivVM.$message({message: response.data.msg, type: 'warning'});
                    }
                    return false;
                }else{
                    if(response.data.data.length==0){
                        mainDivVM.$message({message:'没有签约单', type: 'warning'});
                        return false;
                    }

                    //填装数据
                    mainDivVM.editableTabs = []
                    for(var i = 0 ; i < response.data.data.length ;i++){
                        console.log(response.data.data[i])
                        var data = {};
                        data.updateFormSigning=response.data.data[i];
                        data.title='（'+ data.updateFormSigning.signNo +'）签约单';
                        data.name=""+i;
                        mainDivVM.editableTabs.push(data);
                    }

                    //设置显示数据
                    mainDivVM.updateFormSigning =response.data.data[0];
                    if( mainDivVM.updateFormSigning){
                        if(mainDivVM.updateFormSigning.payType > 2){
                            if(mainDivVM.updateFormSigning.payType == 3){
                                mainDivVM.isAllMoney = true;
                            }else{
                                mainDivVM.isAllMoney = false;
                            }
                            mainDivVM.notEditRebutSign = true;
                            mainDivVM.payTypeSelect = false;
                            mainDivVM.payTypeArr = mainDivVM.payTypeArr1.slice(2,4)
                        }else{
                            if(mainDivVM.updateFormSigning.payType == 1){
                                mainDivVM.isAllMoney = false;
                            }else{
                                mainDivVM.isAllMoney = true;
                            }
                            mainDivVM.payTypeSelect = true;
                            mainDivVM.payTypeArr = mainDivVM.payTypeArr1.slice(0,2)
                        }
                        mainDivVM.currentProvince(mainDivVM.updateFormSigning.signProvince)
                        mainDivVM.currentCity(mainDivVM.updateFormSigning.signCity)
                    }
                    mainDivVM.dialogUpdateFormSigningVisible = true;
                }
            })
        },
        toSignPage(row){  // 进入签约单明细  能够 新增付款
            window.location.href='/businesssign/visitRecordPage?clueId='+row.clueId+"&signId="+row.signId+"&readyOnly=0";
        },
        toReadOnlySignPage(row){  // 进入签约单明细  能够 新增付款
            window.location.href='/businesssign/visitRecordPage?clueId='+row.clueId+"&signId="+row.signId+"&readyOnly=1";
        },
        selectSigningType(pa1) { //是不是全款
            if( pa1=='1' ){
                this.formSigning.payType = '1';
                this.isAllMoney = false;
            }else{
                this.formSigning.payType = '2';
                this.isAllMoney = true;
            }
        },

        selectUpdateSigningType(pa1) { //是不是全款
            if( pa1=='1' ){
                this.updateFormSigning.payType = '1';
                this.isAllMoney = false;
            }else{
                this.updateFormSigning.payType = '2';
                this.isAllMoney = true;
            }
        },

        submitForm(formName) {
            var param = this.formSigning;
            param.amountPerformance = this.formSigningAmountPerformance;
            // 设置 clueid
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    axios.post("/businesssign/insert", param)
                        .then(function (response) {
                            if (response.data.code == 0) {
                                mainDivVM.$message({type: 'success', message: '签约单保存成功!',duration:2000,onClose:function(){
                                        mainDivVM.pager.pageNum = 1
                                        mainDivVM.initList();
                                        mainDivVM.dialogFormSigningVisible = false;
                                        mainDivVM.formSigning.clueId = "";
                                    }});
                            } else {
                                mainDivVM.$message.error(response.data.msg);
                            }
                        }).catch(function (error) {console.log(error);});
                } else {return false;}
            });
        },

        submitUpdateForm(formName) {
            var param = this.updateFormSigning;
            param.amountPerformance = this.updateFormSigningAmountPerformance;
            this.updateFormSigning.amountPerformance = this.updateFormSigningAmountPerformance;
            param.makeUpTime = new Date( param.makeUpTime)
            param.payTime = new Date( param.payTime)
            // 设置 clueid
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    axios.post("/businesssign/update", param)
                        .then(function (response) {
                            if (response.data.code == 0) {
                                mainDivVM.$message({type: 'success', message: '签约单更新成功!',duration:2000,onClose:function(){
                                        mainDivVM.pager.pageNum = 1
                                        mainDivVM.initList();
                                        mainDivVM.dialogUpdateFormSigningVisible = false;
                                    }});
                            } else {
                                mainDivVM.$message.error(response.data.msg);
                            }
                        }).catch(function (error) {console.log(error);});
                } else {return false;}
            });
        },

        resetForm(formName) {
            if( mainDivVM.$refs[formName]) {
                mainDivVM.$refs[formName].resetFields();
            }
        },
        initList(){
            var param = this.queryForm;
            param.pageSize = this.pager.pageSize;
            param.pageNum =  this.pager.currentPage;
            // axios.post('/aggregation/businessMyCustomer/queryPage',param).then(function (response) {
            axios.post('/console/console/listPendingInviteCustomer',param).then(function (response) {
                console.log('待处理邀约来访客户')
                console.log(response.data)
                if(null===response||response.data==null||response.data.code!='0'){
                    if(response.data.code!='0'){
                        mainDivVM.$message({message: response.data.msg, type: 'warning'});
                    }
                    return false;
                }else{
                    mainDivVM.tableData =response.data.data.data;
                    mainDivVM.pager.currentPage= response.data.data.currentPage;
                    mainDivVM.pager.total= response.data.data.total;
                    mainDivVM.pager.pageSize =  response.data.data.pageSize;
                }
            })
        },
    },
    created(){        
        // 工作台
        this.initBoard();
        // 待处理邀约来访客户
        this.initList();
        //  到访店铺类型 vistitStoreTypeArr
        var param={};
        param.groupCode="vistitStoreType";
        axios.post('/dictionary/DictionaryItem/dicItemsByGroupCode',param).then(function (response) {
            mainDivVM.vistitStoreTypeArr=response.data.data;
        });

        //初始化省份
        param={};
        param.type="0";
        axios.post('/area/sysregion/querySysRegionByParam',param).then(function (response) {
            mainDivVM.provinceArr=response.data.data.data;
        });

        //考察公司
        param={};
        axios.post('/aggregation/companyManager/listNoPage',param).then(function (response) {
            mainDivVM.companyArr=response.data.data;
        });
    },
    mounted(){
        document.getElementById('mainDiv').style.display = 'block';
    }
});