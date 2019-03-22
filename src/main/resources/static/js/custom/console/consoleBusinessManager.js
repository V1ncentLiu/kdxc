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
        visitedNum:'',//当月到访数   
        signedNum:'',//当月签约数
        secondVisitedNum:'',//当月二次到访数
        secondSignedNum:'',//当月二次来访签约数
        unPaymentNum:'',//未收齐尾款笔数
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
        // 待处理邀约来访客户
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
        rules:{},
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
            amountPerformance:''
        },
        pager:{
            total: 0,
            currentPage: 1,
            pageSize: 20,
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
        },
        // 待处理邀约来访客户
        initList(){
            var param ={};
            param.pageSize = this.pager.pageSize;
            param.pageNum =  this.pager.currentPage;
            axios.post('/aggregation/businessMyCustomer/queryPage',param).then(function (response) {
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
        showCustomerDetail(row, column){ // 客户详情
            window.location.href='/bus/BusinessCustomer/viewCustomerInfo?clueId='+row.clueId;
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
        editCustomerDetail(row, column){ // 客户维护
            window.location.href='/bus/BusinessCustomer/editCustomerInfo?clueId='+row.clueId;
        },
        showNotVisitReason(row){// 展示未到访原因            
            var param={};
            param.id = row.clueId;
            axios.post('/aggregation/businessMyCustomer/notVisitReason',param).then(function (response) {
                mainDivVM.notVisitdDialogVisible = true;
                mainDivVM.notVisitFlag.str = response.data.data.notVisitReason
            });
        },
        editSign(row){//添加签约单
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
        setAddPerformanceAmount(value){
            this.formSigning.performanceAmount = this.formSigning.amountReceived
        },
        changePayType(val){
            if(val==3){
                this.isAllMoney = true;
            }else{
                this.isAllMoney = false;
            }
        },
    },
    created(){        
        // 工作台
        this.initBoard();
        // 待处理邀约来访客户
        this.initList();
    },
    mounted(){
        document.getElementById('mainDiv').style.display = 'block';
    }
});