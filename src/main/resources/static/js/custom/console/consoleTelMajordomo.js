// 查询 模块
var mainDivVM = new Vue({
    el: '#mainDiv',
    data: {
        dataTable:[],
        pager:{
            total: 0,
            currentPage: 1,
            pageSize: 20,
        },
        repeatPhonesDialog:false,
        repeatPhonesTable:[],
        dailogTitleType:'',
        multipleSelection:[],
        allocationVisible: false,
        allocationForm: {
            saleId:''
        },
        allocationFormRules: {
            saleId: [
                { required: true, message: '请选择电销顾问', trigger: 'blur' }
            ]
        },
        formLabelWidth: '150px',
        saleOptions:saleList,
        // 工作台
        activeName:'1',
        activeName2:'1',
        activeName3:'1',
        activeName4:'1',
        activeName5:'1',
        assignClueNum:'',//待分配资源数
        receiveClueNum:'',//今日接受资源
        todaygetClueNum:'',//今日领取资源数
        todayAppiontmentNum:'',//今日邀约数
        tomorrowArriveTime:'',//预计明日到访数
        workDay:'',//工作天数
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
        }
        
    },
    methods: {
        gotoPendingAllocation(){//跳转待分配资源列表
            window.location.href="/clue/pendingAllocation/initAppiontmentList"; 
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
            // 待分配资源数
            param={};
            axios.post('/console/console/countTeleDircortoerUnAssignClueNum',param).then(function (response) {
                console.log(response.data)                
                mainDivVM.assignClueNum=response.data.data;
            });
            // 今日接受资源
            param={};
            axios.post('/console/console/countTeleDircortoerReceiveClueNum',param).then(function (response) {
                console.log(response.data)
                mainDivVM.receiveClueNum=response.data.data;
            });
            // 今日领取资源数
            param={};
            axios.post('/console/console/countTeleDircortoerGetClueNum',param).then(function (response) {
                console.log(response.data)
                mainDivVM.todaygetClueNum=response.data.data;
            });
            // 今日邀约数
            param={};
            axios.post('/console/console/countTeleDirectorTodayAppiontmentNum',param).then(function (response) {
                console.log(response.data)
                mainDivVM.todayAppiontmentNum=response.data.data;
            });
            // 预计明日到访数
            param={};
            axios.post('/console/console/countTeleDirecotorTomorrowArriveTime',param).then(function (response) {
                console.log(response.data)
                mainDivVM.tomorrowArriveTime=response.data.data;
            });   
            // 工作天数
            param={};
            axios.post('/console/console/getWorkDay',param).then(function (response) {
                console.log('工作天数')                
                console.log(response.data)                
                mainDivVM.workDay=response.data.data;
            });    
        },
        // 表格        
        handleSelectionChange(val) {//选择行
            this.multipleSelection = val;
        },
        searchTable() {
            var param ={};
            param.pageSize = this.pager.pageSize;
            param.pageNum = this.pager.currentPage;
            console.info(param);
            // axios.post('/clue/pendingAllocation/list', param)
            axios.post('/console/console/listUnAssignClue', param)
            .then(function (response) {
                var result =  response.data;
                console.info('待分配资源');
                console.info(result);
                var table=result.data;
                var data= table.data;
                for(var i=0;i<data.length;i++){
                    data[i].category=mainDivVM.transformCategory(data[i].category);
                    data[i].type=mainDivVM.transformType(data[i].type);
                    data[i].createTime=mainDivVM.dateFormat(data[i].createTime);
                    data[i].messageTime=mainDivVM.dateFormat(data[i].messageTime);
                }
                mainDivVM.dataTable= data;
                mainDivVM.pager.total =  table.total;
                mainDivVM.pager.currentPage = table.currentPage;
            })
            .catch(function (error) {
                 console.log(error);
            });            
        },
        //日期数据格式化方法
        dateFormat( cellValue) {
            if (cellValue == undefined) {
                return "";
            }
            return moment(cellValue).format("YYYY-MM-DD HH:mm");
        },
        //查看重复手机号资源
        repeatPhonesClick(row) {
            console.log(row);
            this.repeatPhonesDialog=true;
            this.dailogTitleType=row.phone;
            var param ={};
            param.id = row.clueId;
            param.cusPhone = row.phone;
            console.info(param);
            axios.post('/clue/appiontment/repeatPhonelist', param)
            .then(function (response) {
                var result =  response.data;
                console.info(result);
                var table=result.data;
                mainDivVM.repeatPhonesTable= table.data;
                mainDivVM.repeatPhonesDialog=true;
            }).catch(function (error) {
                console.log(error);
            });                
        },
        //类别转换方法
        transformCategory(cellValue) {
          var text="";
          if(clueCategoryList){
              for(var i=0;i<clueCategoryList.length;i++){
                    if(cellValue==clueCategoryList[i].value){
                        text=clueCategoryList[i].name;
                    }
                }
          }
           return text;
        },
        //类行转换方法
        transformType(cellValue) {
          var text="";
          if(clueTypeList){
              for(var i=0;i<clueTypeList.length;i++){
                    if(cellValue==clueTypeList[i].value){
                        text=clueTypeList[i].name;
                    }
                }
          }
           return text;
        },
        // 快速分配资源
        //打开分配资源
        toAllocationClue() {
            var rows = this.multipleSelection;
            if(rows.length==0){
                this.$message({
                    message: '请选择数据进行分配',
                    type: 'warning'
                });
               return;
            }
            this.allocationVisible = true;
        },
        // 提交分配资源
        allocationClue(formName){//分配资源
            this.$refs[formName].validate((valid) => {
                if (valid) {
                    var rows = this.multipleSelection;
                    var rowIds = [];
                    for(var i=0;i<rows.length;i++){
                        var curRow = rows[i];
                        rowIds.push(curRow.id);
                    }
                    //分配
                    var param  = {};
                    param.idList=rowIds;
                    param.teleSaleId=this.allocationForm.saleId;
                    axios.post('/clue/pendingAllocation/allocationClue',param)
                    .then(function (response) {
                        var data =  response.data;
                        if(data.code=='0'){
                            mainDivVM.$message({message:'分配成功',type:'success',duration:1000,onClose:function(){
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
                    console.log('数据未通过校验！');
                    return false;
                }
            });
        },        
    },
    created(){
        // 工作台
        this.initBoard();
        // 表格
        this.searchTable();

    },
    mounted(){
        document.getElementById('mainDiv').style.display = 'block';
    }
});