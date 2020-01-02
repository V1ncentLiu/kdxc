var mainDivVM = new Vue({
    el: '#mainDiv',
    data: {
        dashboardSale:dashboardSale,
        btnDisabled: false,
        notVisitButtonAble: false,
        editRebutNoVisitDialog:false,//编辑驳回未到访弹窗
        showVisitId:false,
        editRebutNoVisit:{},
        multipleSelection: [],
        notVisitFlagDialogVisible: false,//标记未到访弹窗
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
        visitedNum:'',//当月首访数
        activeName9:'',
        hisDatas:[],
        notVisitData:[],
        showTabNotVisitData:false,
        editableTabs:[],
        showHisVisitRecordDialog:false,
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
        //赠送类型
        giveTypeList:giveTypeList,
        //待处理邀约来访记录
        editableTabsValue: 0, //tabs标签
        editableTabs: [],
        suppWrap: true,
        tabIndex: 2,
        shouAddVisitButton:false,
        updateVisitRecordDialogVisible:false,
        addVisitRecordDialogVisible:false,
        notVisitdDialogVisible:false,
        notVisitdDialogVisible1:false,
        notVisitFlagDialogVisible:false,
        showVisitAduitDialogVisible:false,
        dialogFormSigningVisible:false,
        dialogUpdateFormSigningVisible:false,
        updateRejectNotVisitButtonAble:false,
        existsSign:false,
        curSignStatus:"",
        isAllMoney: false,
        payTypeSelect:false,
        notEditRebutSign:false,
        formLabelWidth:"150px",
        formLabelWidth2:"98px",
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
        visitArr:[{value:-1,name:'待处理'},{value:0,name:'未到访'},{value:1,name:'已到访'}],
        visitTypeArr:[{value:1,name:'预约来访'},{value:2,name:'慕名来访'},{value:3,name:'临时来访'}],
        option2s: [{value: 1, label: '一次性全款'},{value: 2, label: '先付定金'}],
        option3s:[],
        payTypeArr:[{value: "1", label: '全款'},{value: "2", label: '定金'},{value: "3", label: '追加定金'},{value: "4", label: '尾款'}],
        payTypeArr1:[{value: "1", label: '全款'},{value: "2", label: '定金'},{value: "3", label: '追加定金'},{value: "4", label: '尾款'}],
        vistitStoreTypeArr:[],
        signStoreTypeArr: [],
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
            clueId: "",
            str: "",
            clueIds: [],
            notVisitReason: "",
            createUserName:"",
            notVisitTime:""
        },
        editRebutNoVisit:{
            id: "",
            rejectTime: "",
            rejectUser: "",
            rejectReason: "",
            notVisitTime: "",
            notVisitReason: ""

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
            arrVisitCity:"",
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
            arrVisitCity:"",
            isSign:1,
            // visitPeopleNum:"",
            notSignReason:"",
            rebutTime:"",
            rebutReason:""
        },
        formSigning: {
        	giveType:"",
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
            isRemoteSign:'',

            visitTime:"",
            arrVisitCity:"",
            visitType:"",
            visitShopType:"",
            visitNum:"",

            payType:1,
            payMode:'',
            payModes:[],
            amountReceived: '',
            amountBalance: '',
            makeUpTime:'',
            payTime: new Date(),
            amountPerformance:'',
            payName:''
        },
        updateFormSigning: {
        	giveType:"",
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
            isRemoteSign:0,

            visitDetailRecordId:"",
            visitTime: "",
            arrVisitCity: "",
            visitType: "",
            visitShopType: "",
            visitNum: "",

            payDetailId:"",
            payType:'1',
            payName:'',
            payMode:'',
            payModes:[],
            amountReceived: '',
            amountBalance: '',
            makeUpTime:'',
            payTime: '',
            amountPerformance:'',
            remarks: '', //备注
            signRejectRecordList:[]//签约单驳回
        },
        pager:{
            total: 0,
            currentPage: 1,
            pageSize: 20,
        },
        rulesVisit:{
            companyid:[
                { required: true, message: '请选择考察公司', trigger: 'change' }
            ],
            vistitTime:[
                { required: true, message: '请选择到访时间', trigger: 'change' }
            ],
            projectId:[
                { required: true, message: '请选择考察项目', trigger: 'change' }
            ],
            vistitStoreType:[
                { required: true, message: '请选择到访店铺类型', trigger: 'change' }
            ],
            isSign:[
                { required: true, message: '请选择是否签约', trigger: 'change' }
            ],
            signProvince: [
                { required: true, message: '请选择签约省份', trigger: 'change' }
            ],
            // signDistrict:[
            //     { required: true, message: '请选择签约区/县', trigger: 'change' }
            // ],
            customerName: [
                { required: true, message: '请输入客户姓名', trigger: 'blur' }
            ],
            // signCity: [
            //     { required: true, message: '请选择签约城市', trigger: 'change' }
            // ],
            arrVisitCity: [ //请填写来访城市
                { required: true, message: '请填写到访城市', trigger: 'change' }
            ],
            signShopType: [
                { required: true, message: '请选择签约店型', trigger: 'change' }
            ],
            visitPeopleNum: [ //请填写到访人数
                { required: true, message: '请填写到访人数', trigger: 'blur' },
                { validator:function(rule,value,callback){
                        var reg =  /^[0-9]+[0-9]*]*$/ ;
                        if(value ==null || value =="null" || value ==""  ){
                            callback(new Error("请填写到访人数"));
                            mainDivVM.addVisitRecord.visitPeopleNum =null;

                        }else  if(value !=null && value !="null" && value !="" && !reg.test(value) ){
                            callback(new Error("请输入正整数"));
                            mainDivVM.addVisitRecord.visitPeopleNum =null;
                        }else{
                            callback();
                        }

                    }, trigger: 'change'}
            ]
        },
        editRebutNoVisitRules:{
            notVisitReason: [
                {required: true, message: '请输入未到访原因', trigger: 'blur'}
            ],
        },
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
            // signDictrict: [
            //     { required: true, message: '请选择签约区/县', trigger: 'change' }
            // ],
            // signCity: [
            //     { required: true, message: '请选择签约城市', trigger: 'change' }
            // ],
            signShopType: [
                { required: true, message: '请选择签约店型', trigger: 'change' }
            ],
            amountReceivable: [
                { required: true, message: '请输入应收金额', trigger: 'blur' },
                { validator:function(rule,value,callback){
                        if(value===0){
                            callback();
                        }
                        var reg =  /^[0-9]+[0-9]*]*$/ ;
                        if(value ==null || value =="null" || value ==""  ){
                            callback(new Error("请输入应收金额"));
                            mainDivVM.formSigning.amountReceivable =null;

                        }else  if(value !=null && value !="null" && value !="" && !reg.test(value) ){
                            callback(new Error("请输入正整数"));
                            mainDivVM.formSigning.amountReceivable =null;
                        }else{
                            callback();
                        }
                    }, trigger: 'change'}
            ],
            firstToll: [
                { required: true, message: '请输入路费（报首次）', trigger: 'blur' },
                { validator:function(rule,value,callback){
                        if(value===0){
                            callback();
                        }
                        var reg =  /^[0-9]+[0-9]*]*$/ ;
                        if(value ==null || value =="null" || value ==""  ){
                            callback(new Error("请输入路费（报首次）"));
                            mainDivVM.formSigning.firstToll =null;

                        }else  if(value !=null && value !="null" && value !="" && !reg.test(value) ){
                            callback(new Error("请输入正整数"));
                            mainDivVM.formSigning.firstToll =null;
                        }else{
                            callback();
                        }
                    }, trigger: 'change'}
            ],
            preferentialAmount: [
                { required: true, message: '请输入优惠金额', trigger: 'blur' },
                { validator:function(rule,value,callback){
                        if(value===0){
                            callback();
                        }
                        var reg =  /^[0-9]+[0-9]*]*$/ ;
                        if(value ==null || value =="null" || value ==""  ){
                            callback(new Error("请输入优惠金额"));
                            mainDivVM.formSigning.preferentialAmount =null;

                        }else  if(value !=null && value !="null" && value !="" && !reg.test(value) ){
                            callback(new Error("请输入正整数"));
                            mainDivVM.formSigning.preferentialAmount =null;
                        }else{
                            callback();
                        }

                    }, trigger: 'change'}
            ],
            signType: [
                { required: true, message: '请选择签约类型', trigger: 'change' }
            ],
            giveAmount: [
                { required: true, message: '请输入赠送金额', trigger: 'blur' },
                { validator:function(rule,value,callback){
                        if(value===0){
                            callback();
                        }
                        var reg =  /^[0-9]+[0-9]*]*$/ ;
                        if(value ==null || value =="null" || value ==""  ){
                            callback(new Error("请输入赠送金额"));
                            mainDivVM.formSigning.giveAmount =null;

                        }else  if(value !=null && value !="null" && value !="" && !reg.test(value) ){
                            callback(new Error("请输入正整数"));
                            mainDivVM.formSigning.giveAmount =null;
                        }else{
                            callback();
                        }
                    }, trigger: 'change'}
            ],
            // payType: [
            //  { required: true, message: '请选择付款类型', trigger: 'change' }
            // ],
            payMode: [
                { required: true, message: '请选择付款方式', trigger: 'change' }
            ],
            payModes: [
                { required: true,validator:function(rule,value,callback){
                        if(value.length==0){
                            callback(new Error("请选择付款方式"));
                        }else{
                            callback();
                        }
                    }, trigger: 'change' }
            ],
            payName: [
                {required: true, message: '请输入付款人姓名', trigger: 'blur'}
            ],
            payTime: [
                { type: 'date', required: true, message: '请选择时间', trigger: 'change' }
            ],
            amountReceived: [
                { required: true, message: '请输入实收金额', trigger: 'blur' },
                { validator:function(rule,value,callback){
                        if(value===0){
                            callback();
                        }
                        var reg =  /^[0-9]+[0-9]*]*$/ ;
                        if(value ==null || value =="null" || value ==""  ){
                            callback(new Error("请输入实收金额"));
                            mainDivVM.formSigning.amountReceived =null;

                        }else  if(value !=null && value !="null" && value !="" && !reg.test(value) ){
                            callback(new Error("请输入正整数"));
                            mainDivVM.formSigning.amountReceived =null;
                        }else{
                            callback();
                        }
                    }, trigger: 'change'}
            ],
            amountBalance: [
                {required: true, validator:function(rule,value,callback){
                        if(value===0){
                            callback();
                        }
                        var reg =  /^[0-9]+[0-9]*]*$/ ;
                        if(value ==null || value =="null" || value ==""  ){
                            callback(new Error("请输入余款金额"));
                            // mainDivVM.formSigning.amountBalance =null;
                            return;

                        }else  if(value !=null && value !="null" && value !="" && !reg.test(value) ){
                            callback(new Error("请输入正整数"));
                            // mainDivVM.formSigning.amountBalance =null;
                            return;
                        }else{
                            callback();
                        }
                    }, trigger: 'change'}
            ],
            makeUpTime: [
                {required: true, message: '请选择预计余款补齐时间', trigger: 'change' }
            ],
            // performanceAmount: [
            //  { required: true, message: '请输入业绩金额', trigger: 'blur' }
            // ],
            isRemoteSign: [ //是否远程签约
                { required: true, message: '请选择是否远程签约', trigger: 'change' }
            ],
            visitTime: [ //请选择到访时间
                { required: true, message: '请选择到访时间', trigger: 'change' }
            ],
            arrVisitCity: [ //请填写来访城市
                { required: true, message: '请填写到访城市', trigger: 'change' }
            ],
            visitType: [ //请选择到访类型
                { required: true, message: '请选择到访类型', trigger: 'change' }
            ],
            visitShopType: [ //请选择到访店铺类型
                { required: true, message: '请选择到访店铺类型', trigger: 'change' }
            ],
            visitNum: [ //请填写到访人数
                { required: true, message: '请填写到访人数', trigger: 'blur' },
                { validator:function(rule,value,callback){
                        var reg =  /^[0-9]+[0-9]*]*$/ ;
                        if(value ==null || value =="null" || value ==""  ){
                            callback(new Error("请填写到访人数"));
                            mainDivVM.formSigning.visitNum =null;

                        }else  if(value !=null && value !="null" && value !="" && !reg.test(value) ){
                            callback(new Error("请输入正整数"));
                            mainDivVM.formSigning.visitNum =null;
                        }else{
                            callback();
                        }
                    }, trigger: 'change'}
            ],
            visitPeopleNum: [ //请填写到访人数
                 { required: true, message: '请填写到访人数', trigger: 'blur' },
                 { validator:function(rule,value,callback){
                     var reg =  /^[0-9]+[0-9]*]*$/ ;
                     if(value ==null || value =="null" || value ==""  ){
                         callback(new Error("请填写到访人数"));
                        mainDivVM.addVisitRecord.visitPeopleNum =null;

                     }else  if(value !=null && value !="null" && value !="" && !reg.test(value) ){
                           callback(new Error("请输入正整数"));
                           mainDivVM.addVisitRecord.visitPeopleNum =null;
                     }else{
                         callback();
                     }

                }, trigger: 'change'}
              ]
        },
    },
    
    methods: {
        getShopType(value){
            var param = {};
            param.id = value;
            axios.post('/busVisitRecord/getShortTypeByProjectId', param).then(function (response) {
                console.log("###"+response.data.data);
                mainDivVM.vistitStoreTypeArr= response.data.data;
                mainDivVM.signStoreTypeArr= response.data.data;
                mainDivVM.addVisitRecord.vistitStoreType =''
                mainDivVM.updateVisitRecord.vistitStoreType =''
                mainDivVM.formSigning.signShopType =''
                mainDivVM.formSigning.visitShopType =''
                mainDivVM.updateFormSigning.signShopType =''
                mainDivVM.updateFormSigning.visitShopType =''
            });
        },
    	number3() {//添加签约单业绩金额
            this.formSigning.performanceAmount = this.formSigning.performanceAmount.replace(/[^\.\d]/g, '');
            this.formSigning.performanceAmount = this.formSigning.performanceAmount.replace('.', '');
        },
        number4() {//编辑签约单业绩金额
            this.updateFormSigning.performanceAmount = this.updateFormSigning.performanceAmount.replace(/[^\.\d]/g, '');
            this.updateFormSigning.performanceAmount = this.updateFormSigning.performanceAmount.replace('.', '');
        },
    	formSigningAmountPerformance() {
            var aone = parseFloat(this.formSigning.amountReceived);
            var atwo = parseFloat(this.formSigning.firstToll);
            if (isNaN(aone)) aone = 0
            if (isNaN(atwo)) atwo = 0
            this.formSigning.performanceAmount = (aone + atwo) + ""
        },
      updateFormSigningAmountPerformance(){
        var aone = parseFloat(this.updateFormSigning.amountReceived);
        var atwo = parseFloat(this.updateFormSigning.firstToll);
        if(isNaN(aone)) aone = 0
        if(isNaN(atwo)) atwo = 0
        this.updateFormSigning.performanceAmount = (aone + atwo) + ""
      },
      formatNum(value) {
          if(!value&&value!==0) return 0;

          let str = value.toString();
          let reg = str.indexOf(".") > -1 ? /(\d)(?=(\d{3})+\.)/g : /(\d)(?=(?:\d{3})+$)/g;
          return str.replace(reg,"$1,");
        },
      saveNotVisit() {
        var param = this.notVisitFlag;
        // 设置 clueid
        if (mainDivVM.notVisitButtonAble == true) {
          return;
        }
        this.$refs['notVisitFlag'].validate((valid) => {
          if (valid) {
            mainDivVM.notVisitButtonAble = true;
            axios.post("/aggregation/businessMyCustomer/notVisit", param)
            .then(function (response) {
              if (response.data.code == 0) {
                mainDivVM.$message({
                  type: 'success', message: '标记成功!', duration: 2000, onClose: function () {
                    mainDivVM.pager.pageNum = 1
                    mainDivVM.initList();
                    mainDivVM.notVisitFlagDialogVisible = false;
                    mainDivVM.notVisitButtonAble = false;
                  }
                });
              } else {
                mainDivVM.$message.error(response.data.msg);
                mainDivVM.notVisitButtonAble = false;
              }
            }).catch(function (error) {
              mainDivVM.notVisitButtonAble = false;
              console.log(error);
            });
          } else {
            return false;
          }
        });
      },
      handleSelectionChange(val) {
        this.multipleSelection = val;
      },
      notVisit() {//标记未到访
        this.resetForm("notVisitFlag")
        var rows = mainDivVM.multipleSelection;
        if (rows.length == 0) {
          this.$message({message: '请选择数据', type: 'warning'});
          return false;
        } else {
          var names = "";
          var rowIds = [];
          for (var i = 0; i < rows.length; i++) {
            // if(rows[i].visitStatus==0||rows[i].visitStatus==1||rows[i].visitStatus==2||rows[i].visitStatus==3){
            //     this.$message({message: '勾选数据中，包含了已到访或是已标记的客户', type: 'warning'});
            //     return false;
            // }
            names = names + '【' + rows[i].cusName + '】';
            rowIds.push(rows[i].clueId);
          }
          var str = '请填写客户姓名' + names + '未到访的原因?';
          this.notVisitFlag.clueIds = rowIds;
          this.notVisitFlag.str = str;
          this.notVisitFlag.notVisitReason = "";
          this.notVisitFlag.notVisitUser="";
          this.notVisitFlag.notVisitTime="";
          this.notVisitFlagDialogVisible = true;
        }
      },
      initList() {
        var param = this.queryForm;
        param.pageSize = this.pager.pageSize;
        param.pageNum = this.pager.currentPage;
        axios.post('/aggregation/businessMyCustomer/queryPage', param).then(function (response) {
          if (null === response || response.data == null || response.data.code != '0') {
            if (response.data.code != '0') {
              mainDivVM.$message({message: response.data.msg, type: 'warning'});
            }
            return false;
          } else {
            mainDivVM.tableData = response.data.data.data;
            mainDivVM.pager.currentPage = response.data.data.currentPage;
            mainDivVM.pager.total = response.data.data.total;
            mainDivVM.pager.pageSize = response.data.data.pageSize;
            console.log(mainDivVM.tableData)

          }
        })
      },
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
        // 待处理邀约来访记录
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
                resVal='已到访';
                var visitNum = row.auditVisitNum;
                if(visitNum == 1){
                    resVal = '首次到访';
                }else if(visitNum == 2){
                    resVal = '2次到访';
                }else if(visitNum == 3){
                    resVal = '3次到访';
                } else if(visitNum == 4){
                    resVal = '4次到访';
                }else if(visitNum == 5){
                    resVal = '5次到访';
                }
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
        selectIsRemoteSign (pa1) { //是否远程签约event
            if( pa1==1 ){
                this.suppWrap = false;
            }else{
                this.suppWrap = true;
            }
        },
        suppShow(){ //补充到访记录展开
            this.suppWrap = true;
            this.shouAddVisitButton = false;
            var param = {};
            param.id = mainDivVM.formSigning.clueId;
            axios.post('/businesssign/visitEcho',param).then(function (response) {
                var echoData = response.data.data;
                mainDivVM.formSigning.arrVisitCity = echoData.arrVisitCity;
                if(echoData.visitNum>0){
                    mainDivVM.formSigning.visitNum=echoData.visitNum;
                }
                mainDivVM.formSigning.visitType=1;;
                mainDivVM.formSigning.visitTime=new Date();
            });
        },
        suppHide(){
            this.suppWrap = false;
            this.shouAddVisitButton = true;
            //清空补充到访记录数据
            this.formSigning.arrVisitCity = '';
            this.formSigning.visitNum='';
            this.formSigning.visitShopType='';
            this.formSigning.visitTime='';
            this.formSigning.visitType='';
        },

        suppUpdateShow() { //补充到访记录展开
            this.suppWrap = true;
            this.shouAddVisitButton = false;
            var param = {};
            console.log(mainDivVM.updateFormSigning.visitDetailRecordId)
            if(!mainDivVM.updateFormSigning.visitDetailRecordId){
                return false;
            }
            param.id = mainDivVM.updateFormSigning.visitDetailRecordId;
            //TODO
            axios.post('/busVisitRecord/one', param).then(function (response) {
                var echoData = response.data.data;
                console.log("============================")
                console.log(echoData)
                if(echoData.arrVisitCity){
                    mainDivVM.updateFormSigning.arrVisitCity = echoData.arrVisitCity;
                }
                if (echoData.visitPeopleNum > 0) {
                    mainDivVM.updateFormSigning.visitNum = echoData.visitPeopleNum;
                }
                mainDivVM.updateFormSigning.visitShopType = echoData.vistitStoreType;
                mainDivVM.updateFormSigning.visitType = echoData.visitType;
                mainDivVM.updateFormSigning.visitTime = echoData.vistitTime;
            });
        },
        suppUpdateHide() {
            this.suppWrap = false;
            this.shouAddVisitButton = true;
            //清空补充到访记录数据
            this.updateFormSigning.arrVisitCity = '';
            this.updateFormSigning.visitNum = '';
            this.updateFormSigning.visitShopType = '';
            this.updateFormSigning.visitTime = '';
            this.updateFormSigning.visitType = '';
        },

        changePayType(val){
            if(val==3){
                this.isAllMoney = true;
            }else{
                this.isAllMoney = false;
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
            //window.location.href='/bus/BusinessCustomer/viewCustomerInfo?clueId='+row.clueId;
          var clueId=row.clueId;
          window.location.href = '/bus/BusinessCustomer/editCustomerInfo?clueId=' + clueId;
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

        changeUpdateVisitProvince(selVal){
            mainDivVM.updateVisitRecord.signCity = '';
            mainDivVM.updateVisitRecord.signDistrict='';
            mainDivVM.districtArr=[]
            this.currentProvince(selVal);
        },
        changeUpdateVisitCity(selVal){
            mainDivVM.updateVisitRecord.signDistrict='';
            this. currentCity(selVal);
        },
        changeVisitProvince(selVal){
            mainDivVM.addVisitRecord.signCity = '';
            mainDivVM.addVisitRecord.signDistrict='';
            mainDivVM.districtArr=[]
            this.currentProvince(selVal);
        },
        changeVisitCity(selVal){
            mainDivVM.addVisitRecord.signDistrict=''
            this. currentCity(selVal);
        },

        changeUpdateSignProvince(selVal){
            mainDivVM.updateFormSigning.signCity = '';
            mainDivVM.updateFormSigning.signDictrict='';
            mainDivVM.districtArr=[]
            this.currentProvince(selVal);
        },
        changeUpdateSignCity(selVal){
            mainDivVM.updateFormSigning.signDictrict='';
            this. currentCity(selVal);
        },
        changeSignProvince(selVal){
            mainDivVM.formSigning.signCity = '';
            mainDivVM.formSigning.signDictrict='';
            mainDivVM.districtArr=[]
            this.currentProvince(selVal);
        },
        changeSignCity(selVal){
            mainDivVM.formSigning.signDictrict='';
            this. currentCity(selVal);
        },
        saveVisitRecord(){
            var param = this.addVisitRecord;
            param.rebutTime = new Date(param.rebutTime)
            param.createTime = new Date(param.createTime)
            // 设置 clueid
            this.$refs['addVisitRecord'].validate((valid) => {
                if (valid) {
                    this.btnDisabled = true;
                    axios.post("/busVisitRecord/insert", param)
                        .then(function (response) {
                            if (response.data.code == 0) {
                                mainDivVM.$message({type: 'success', message: '来访记录保存成功!',duration:2000,onClose:function(){
                                        mainDivVM.pager.pageNum = 1
                                        mainDivVM.initList();
                                        mainDivVM.addVisitRecordDialogVisible = false;
                                        mainDivVM.addVisitRecord.clueId = "";
                                        mainDivVM.btnDisabled = false;
                                    }});
                            } else {
                                mainDivVM.$message.error(response.data.msg);
                                mainDivVM.btnDisabled = false;
                            }
                        }).catch(function (error) {mainDivVM.btnDisabled = false; console.log(error);});
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
                    param.arrVisitCity=this.updateVisitRecord.arrVisitCity;
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
                    this.btnDisabled = true;
                    axios.post("/busVisitRecord/update", param)
                        .then(function (response) {
                            if (response.data.code == 0) {
                                mainDivVM.$message({type: 'success', message: '来访提交成功!',duration:2000,onClose:function(){
                                        mainDivVM.pager.pageNum = 1
                                        mainDivVM.initList();
                                        mainDivVM.updateVisitRecordDialogVisible = false;
                                        mainDivVM.updateVisitRecord.clueId = "";
                                        mainDivVM.updateVisitRecord.id = "";
                                        mainDivVM.btnDisabled = false;
                                    }});
                            } else {
                                mainDivVM.$message.error(response.data.msg);
                                mainDivVM.btnDisabled = false;
                            }
                        }).catch(function (error) {mainDivVM.btnDisabled = false; console.log(error);});
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
            //关闭未到访原因弹窗
            this.notVisitdDialogVisible=false;
           // 打开编辑到访记录
            this.resetForm('addVisitRecord')
            var param ={};
            if(row.clueId){
                param.id = row.clueId;
            }else{
                param.id = mainDivVM.notVisitFlag.clueId;
                row.clueId = mainDivVM.notVisitFlag.clueId;
            }


            if(row.signAuditStatus==0||row.signAuditStatus==1||row.signAuditStatus==2){
                mainDivVM.existsSign =true;
                mainDivVM.curSignStatus = row.signAuditStatus;
            }

            axios.post('/busVisitRecord/echo',param).then(function (response) {
                mainDivVM.addVisitRecord = response.data.data;
                mainDivVM.addVisitRecord.clueId=row.clueId;
                 mainDivVM.addVisitRecord.vistitTime = new Date()
                mainDivVM.addVisitRecordDialogVisible = true;
                mainDivVM.vistitStoreTypeArr= response.data.data.vistitStoreTypeArr;
                mainDivVM.currentProvince(mainDivVM.addVisitRecord.signProvince)
                mainDivVM.currentCity(mainDivVM.addVisitRecord.signCity)
                mainDivVM.showNotSignReason(mainDivVM.addVisitRecord.isSign);
            });
        },
        showNotVisitReason(row){
            var param={};
            param.id = row.visitId;
            mainDivVM.notVisitFlag.clueId = row.clueId;
            axios.post('/busVisitRecord/one',param).then(function (response) {
                mainDivVM.notVisitdDialogVisible = true;
                console.log( response.data.data)
                mainDivVM.notVisitFlag.str = response.data.data.notVisitReason
                mainDivVM.notVisitFlag.createUserName = response.data.data.createUserName
                mainDivVM.notVisitFlag.notVisitTime = response.data.data.createTime
            });
        },
        showNotVisitReason1(row){
            var param={};
            param.id = row.visitId;
            mainDivVM.notVisitFlag.clueId = row.clueId;
            axios.post('/busVisitRecord/one',param).then(function (response) {
                mainDivVM.notVisitdDialogVisible1 = true;
                console.log( response.data.data)
                mainDivVM.notVisitFlag.str = response.data.data.notVisitReason
                mainDivVM.notVisitFlag.createUserName = response.data.data.createUserName
                mainDivVM.notVisitFlag.notVisitTime = response.data.data.createTime
            });
        },
        //查看驳回未到访
        rejectNotVisit(row){
            this.editRebutNoVisitDialog = true;
            var param = {};
            param.id = row.id
            axios.post('/busVisitRecord/one', param).then(function (response) {
                if (null === response || response.data == null || response.data.code != '0') {
                    if (response.data.code != '0') {
                        mainDivVM.$message({message: response.data.msg, type: 'warning'});
                    }
                    return false;
                } else {
                    mainDivVM.editRebutNoVisit.id = response.data.data.id;
                    mainDivVM.editRebutNoVisit.rejectTime = response.data.data.rebutTime;
                    mainDivVM.editRebutNoVisit.rejectUser = response.data.data.auditPersonName;
                    mainDivVM.editRebutNoVisit.rejectReason = response.data.data.rebutReason;
                    mainDivVM.editRebutNoVisit.notVisitTime = response.data.data.createTime;
                    mainDivVM.editRebutNoVisit.notVisitReason = response.data.data.notVisitReason;
                }

            })
        },
        //编辑驳回未到访
        updateRejectNotVisit(formName) {
            var notVisitReason = mainDivVM.editRebutNoVisit.notVisitReason;
            var param = {};
            param.id = mainDivVM.editRebutNoVisit.id;
            param.notVisitReason = notVisitReason;
            this.$refs[formName].validate((valid) => {
                if (mainDivVM.updateRejectNotVisitButtonAble == true) {
                    return false;
                }
                if (valid) {
                    mainDivVM.updateRejectNotVisitButtonAble = true;
                    axios.post("/busVisitRecord/update", param)
                        .then(function (response) {
                            if (response.data.code == 0) {
                                mainDivVM.$message({
                                    type: 'success', message: '未到访原因更新成功!', duration: 1000, onClose: function () {
                                        mainDivVM.editRebutNoVisitDialog = false;
                                        mainDivVM.initList();
                                        mainDivVM.updateRejectNotVisitButtonAble = false;
                                    }
                                });
                            } else {
                                mainDivVM.$message.error(response.data.msg);
                                mainDivVM.updateRejectNotVisitButtonAble = false;
                            }
                        }).catch(function (error) {
                        mainDivVM.updateRejectNotVisitButtonAble = false;
                        console.log(error);
                    });
                } else {
                    return false;
                }
            });

        },
        //关闭编辑驳回未到访
        closeEditRebutNoVisitDialog(){
            // 隐藏
            this.editRebutNoVisitDialog = false;
            this.resetForm("editRebutNoVisit")
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
                    var dataList = response.data.data;
                    mainDivVM.setVisitRecordData(dataList);
                }

            })
        },
        // handleClick(tab, event){
        //     mainDivVM.setVisitRecordData( mainDivVM.hisDatas[tab.name]);
        // },
        // showHistory(row){
        //     var param = {};
        //     mainDivVM.editableTabs = [];
        //     this.showHisVisitRecordDialog = true;
        //     this.activeName9 = "0"; // 通过名称设置默认显示
        //     param.clueId = row.clueId
        //     axios.post('/busVisitRecord/queryHisList',param).then(function (response) {
        //         // 动态生成tab
        //         var tabs = [];
        //         for(var i = 0 ; i < response.data.length ; i++){
        //             var tab = {};
        //             tab.title =  response.data[i][0].createUserName+"-客户到访记录";
        //             tab.name = ""+i;
        //             tabs.push(tab)
        //         }
        //         mainDivVM.editableTabs = tabs;
        //         mainDivVM.hisDatas = response.data;
        //         mainDivVM.setVisitRecordData( mainDivVM.hisDatas[0]);
        //     });
        // },
        handleClick(tab, event){
            console.log(tab)
            if(tab.label=='未到访记录'){
                this.showTabNotVisitData = true;
                mainDivVM.notVisitData =  mainDivVM.hisDatas[tab.name];
            }else{
                this.showTabNotVisitData = false;
                mainDivVM.setVisitRecordData( mainDivVM.hisDatas[tab.name]);
            }
        },
        showHistory(row){
            var param = {};
            mainDivVM.editableTabs = [];
            this.showHisVisitRecordDialog = true;
            this.activeName = "0"; // 通过名称设置默认显示
            param.clueId = row.clueId
            axios.post('/busVisitRecord/queryHisList',param).then(function (response) {
                // 动态生成tab
                var tabs = [];
                for(var i = 0 ; i < response.data.length ; i++){
                    var tab = {};
                    if(response.data[i][0]){
                        if(response.data[i][0].isVisit==0){
                            tab.title =  "未到访记录";
                        }else{
                            tab.title =  response.data[i][0].createUserName+"-客户到访记录";
                        }
                        tab.name = ""+i;
                        tabs.push(tab)
                    }
                }
                mainDivVM.editableTabs = tabs;
                mainDivVM.hisDatas = response.data;
                if(mainDivVM.hisDatas[0][0]){
                    if(response.data[0][0].isVisit==0){
                        mainDivVM.notVisitData =  mainDivVM.hisDatas[0];
                        this.showTabNotVisitData = true;
                    }else{
                        mainDivVM.setVisitRecordData( mainDivVM.hisDatas[0]);
                        mainDivVM.showTabNotVisitData = false;
                    }
                }
            });
        },
        setVisitRecordData(dataList){
            console.log(dataList);
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
            console.log(mainDivVM.showVisitAduitDatas)
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
            this.resetForm('formSigning')
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
                    param.arrVisitCity=this.updateVisitRecord.arrVisitCity;
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
            this.resetForm('formSigning')
            this.isAllMoney = false;
            var param = {};
            param.id = row.clueId;
            axios.post('/businesssign/echo',param).then(function (response) {
                mainDivVM.formSigning = response.data.data;
                mainDivVM.formSigning.clueId=row.clueId;
                mainDivVM.dialogFormSigningVisible = true;
                mainDivVM.currentProvince(mainDivVM.formSigning.signProvince)
                mainDivVM.currentCity(mainDivVM.formSigning.signCity)
                mainDivVM.formSigning.payName = response.data.data.customerName;
                mainDivVM.signStoreTypeArr= response.data.data.vistitStoreTypeArr;
            //  设置默认时间
            //     mainDivVM.formSigning.payTime = new Date()
            //     mainDivVM.formSigning.isRemoteSign = 0;
            //     mainDivVM.formSigning.visitTime = new Date();
            });
        },
        showSignDetail(row){

        },
        editRebutSign(row){
            var param ={};
            console.log(row)
            param.id = row.signId
            param.signId = row.signId
            param.clueId = row.clueId
            axios.post('/businesssign/one',param).then(function (response) {
                console.log(response)
                if(null===response||response.data==null||response.data.code!='0'){
                    if(response.data.code!='0'){
                        mainDivVM.$message({message: response.data.msg, type: 'warning'});
                    }
                    return false;
                }else{
                    // if(response.data.data.length==0){
                    //     mainDivVM.$message({message:'没有签约单', type: 'warning'});
                    //     return false;
                    // }
                    //填装数据
                    // mainDivVM.editableTabs = []
                    // for(var i = 0 ; i < response.data.data.length ;i++){
                    //     console.log(response.data.data[i])
                    //     var data = {};
                    //     data.updateFormSigning=response.data.data[i];
                    //     data.title='（'+ data.updateFormSigning.signNo +'）签约单';
                    //     data.name=""+i;
                    //     mainDivVM.editableTabs.push(data);
                    // }

                    //设置显示数据
                    mainDivVM.signStoreTypeArr= response.data.data.vistitStoreTypeArr;
                    mainDivVM.updateFormSigning =response.data.data;
                    if(response.data.data.payName == null || response.data.data.payName == ''){
                        mainDivVM.updateFormSigning.payName=mainDivVM.updateFormSigning.customerName;
                    }
                    if( mainDivVM.updateFormSigning){
                        if(mainDivVM.updateFormSigning.payType > 2){
                            if(mainDivVM.updateFormSigning.payType == 3){
                                mainDivVM.isAllMoney = true;
                            }else{
                                mainDivVM.isAllMoney = false;
                            }
                           // mainDivVM.notEditRebutSign = true;
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
                        if(mainDivVM.updateFormSigning.giveType !=-1){
                        	mainDivVM.updateFormSigning.giveType = mainDivVM.updateFormSigning.giveType+"";
                        }else{
                        	mainDivVM.updateFormSigning.giveType = null;
                        }

                        mainDivVM.currentProvince(mainDivVM.updateFormSigning.signProvince)
                        mainDivVM.currentCity(mainDivVM.updateFormSigning.signCity)
                        var modeArr = mainDivVM.updateFormSigning.payMode.split(",");
                        mainDivVM.updateFormSigning.payModes = mainDivVM.tansPayModeValueToName(modeArr);
                    }

                    mainDivVM.suppUpdateHide() // 默认隐藏 到访记录关联
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
            // this.formSigning.amountBalance = null;
            // this.formSigning.makeUpTime = '';
            if( pa1=='1' ){
                this.formSigning.payType = '1';
                this.isAllMoney = false;
            }else{
                this.formSigning.payType = '2';
                this.isAllMoney = true;
            }
        },
        selectUpdateSigningType(pa1) { //是不是全款
            // this.updateFormSigning.amountBalance = '';
            // this.updateFormSigning.makeUpTime = '';
            if( pa1=='1' ){
                this.updateFormSigning.payType = '1';
                this.isAllMoney = false;
            }else{
                this.updateFormSigning.payType = '2';
                this.isAllMoney = true;
            }
        },

        tansPayModeNameToValue(payModes){
            var payModeArr = [];
            for(var i = 0 ; i < payModes.length;i++){
                for(var j = 0 ; j < mainDivVM.option3s.length ; j++){
                    if(payModes[i]== mainDivVM.option3s[j].name){
                        payModeArr.push( mainDivVM.option3s[j].value);
                    }
                }
            }
            return payModeArr.join(",");
        },

        tansPayModeValueToName(payModes){
            var payModeArr = [];
            for(var i = 0 ; i < payModes.length;i++){
                for(var j = 0 ; j < mainDivVM.option3s.length ; j++){
                    if(payModes[i]== mainDivVM.option3s[j].value){
                        payModeArr.push( mainDivVM.option3s[j].name);
                    }
                }
            }
            return payModeArr;
        },

        submitForm(formName) {
            if( this.formSigning.visitTime){
              this.formSigning.visitTime = new Date( this.formSigning.visitTime);
             }
            if( this.formSigning.payTime){
                this.formSigning.payTime = new Date( this.formSigning.payTime);
            }
            this.formSigning.amountPerformance = this.formSigning.performanceAmount;
            this.formSigning.payMode = mainDivVM.tansPayModeNameToValue(this.formSigning.payModes);
            var param = this.formSigning;
            console.log(param)
            // 设置 clueid
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    this.btnDisabled = true;
                    axios.post("/businesssign/insert", param)
                        .then(function (response) {
                            if (response.data.code == 0) {
                                mainDivVM.$message({type: 'success', message: '签约单保存成功!',duration:2000,onClose:function(){
                                        mainDivVM.pager.pageNum = 1
                                        mainDivVM.initList();
                                        mainDivVM.dialogFormSigningVisible = false;
                                        mainDivVM.formSigning.clueId = "";
                                        mainDivVM.btnDisabled = false;
                                    }});
                            } else {
                                mainDivVM.$message.error(response.data.msg);
                                mainDivVM.btnDisabled = false;
                            }
                        }).catch(function (error) {mainDivVM.btnDisabled = false; console.log(error);});
                } else {return false;}
            });
        },

        submitUpdateForm(formName) {
            this.updateFormSigning.amountPerformance = this.updateFormSigning.performanceAmount;
            var param = this.updateFormSigning;
            console.log(param)
            if(param.makeUpTime){
                param.makeUpTime = new Date(param.makeUpTime)
            }
            if(param.visitTime){
                param.visitTime = new Date(param.visitTime);
            }
           if(param.payTime){
               param.payTime = new Date( param.payTime)
           }
           if(param.giveType ==null || param.giveType ==""){
        	   param.giveType=-1;
           }
           this.updateFormSigning.payMode = mainDivVM.tansPayModeNameToValue(this.updateFormSigning.payModes);
           // 设置 clueid
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    this.btnDisabled = true;
                    axios.post("/businesssign/update", param)
                        .then(function (response) {
                            if (response.data.code == 0) {
                                mainDivVM.$message({type: 'success', message: '签约单更新成功!',duration:2000,onClose:function(){
                                        mainDivVM.pager.pageNum = 1
                                        mainDivVM.initList();
                                        mainDivVM.dialogUpdateFormSigningVisible = false;
                                        mainDivVM.btnDisabled = false;
                                    }});
                            } else {
                                mainDivVM.$message.error(response.data.msg);
                                mainDivVM.btnDisabled = false;
                            }
                        }).catch(function (error) {mainDivVM.btnDisabled = false; console.log(error);});
                } else {return false;}
            });
        },

        resetForm(formName) {
            if( mainDivVM.$refs[formName]) {
                mainDivVM.$refs[formName].resetFields();
            }
        },
        initList(){
            var param = {};
            param.pageSize = this.pager.pageSize;
            param.pageNum =  this.pager.currentPage;
            // axios.post('/aggregation/businessMyCustomer/queryPage',param).then(function (response) {
            axios.post('/console/console/listPendingInviteCustomer',param).then(function (response) {
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
                    console.log(  mainDivVM.tableData)
                }
            })
        },
        number(){//添加签约单余款
　　　      this.formSigning.amountBalance=this.formSigning.amountBalance.replace(/[^\.\d]/g,'');
            this.formSigning.amountBalance=this.formSigning.amountBalance.replace('.','');
    　　},
        number2(){//编辑签约单余款
　　　      this.updateFormSigning.amountBalance=this.updateFormSigning.amountBalance.replace(/[^\.\d]/g,'');
            this.updateFormSigning.amountBalance=this.updateFormSigning.amountBalance.replace('.','');
    　　},
    },
    created(){
        // 工作台
        this.initBoard();
        // 待处理邀约来访记录
        this.initList();
        //  到访店铺类型 vistitStoreTypeArr
        // var param={};
        // param.groupCode="vistitStoreType";
        // axios.post('/dictionary/DictionaryItem/dicItemsByGroupCode',param).then(function (response) {
        //     mainDivVM.vistitStoreTypeArr=response.data.data;
        // });

        param = {};
        param.groupCode = "payMode";
        axios.post('/dictionary/DictionaryItem/dicItemsByGroupCode', param).then(function (response) {
            mainDivVM.option3s = response.data.data;
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
        $(".el-progress__text").css("font-size","35px");
        $(".el-progress__text").eq(0).css("color","#697df5");
        $(".el-progress__text").eq(1).css("color","#a978f5");
        $(".el-progress__text").eq(2).css("color","#32c3bf");
    }
});