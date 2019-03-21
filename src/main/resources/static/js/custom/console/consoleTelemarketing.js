var mainDivVM = new Vue({
    el: '#mainDiv',
    data: {
    	receiveDialog:false,
    	receiveTitle:"",
    	dailogTitleType:"",
        repeatPhonesDialog:false,//重复手机号
        formLabelWidth:"150px",
        tableData:[],
        categoryArr:[],
        typeArr:[],
        reasonArr:[],
        cusStatusArr:[],
        multipleSelection:[],
        queryForm:{
            releaseReasons:[],
            releaseReason:"",
            customerStatus:"",
            teleGorupId:"",
            teleSaleIds:[],
            project:"",
            isRepeatPhone:"",
            createTime1:"",
            createTime2:"",
            releaseTime1:"",
            releaseTime2:"",
            searchWord:"",
            phone:"",
            type:"",
            category:"",
            address:""
        },
        pager:{
            total: 0,
            currentPage: 1,
            pageSize: 20,
        },
        trackingParam:{
            trackingDialogVisible:false,
            tableData:[]
        },
        repeatPhonesTable:[],
        receiveTable:[],
        // 今日待跟进客户资源
        dataTable: [],


        // 工作台
        activeName:'1',
        activeName2:'1',
        activeName3:'1',
        activeName4:'1',
        activeName5:'1',
        receiveTodayNum:'',//今日领取资源数
        assignTodayNum:'',//今日分配资源数
        todayTalkTime:'',//今日通话时长
        //公告
        items: [
            // {content:'系统将于2018年12月5日晚上12:00进行系统升级，请各位同事及时处理工作。系统预计在12:20分恢复正常使用,感谢配合!',id:1},
            // {content:'公告2公告2公告2公告2公告2公告2公告2',id:2},
            // {content:'公告3公告3公告3公告3公告3公告3公告3',id:3}
        ],
        // 未读消息
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
        openTrackingDialog(cid){//跟进记录点击方法
            var param = {};
            param.clueId = cid;
            axios.post('/aggregation/tracking/queryList', param)
                .then(function (response) {
                    if (response.data.code == 0) {
                        mainDivVM.trackingParam.tableData = response.data.data
                        mainDivVM.trackingParam.trackingDialogVisible = true;
                    } else {
                        mainDivVM.$message.error(response.data.msg);
                    }
                }).catch(function (error) {
                console.log(error);
            });
        },
        handleSelectionChange(val) {
            this.multipleSelection = val;
        },
        resetForm(formName) {
            if( mainDivVM.$refs[formName]) {
                mainDivVM.$refs[formName].resetFields();
            }
        },
        // 快速领取新资源
        initList(){
            var param = this.queryForm;
            param.pageSize = this.pager.pageSize;
            param.pageNum =  this.pager.currentPage;
            axios.post('/aggregation/publiccustomer/queryPage',param).then(function (response) {
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
        openReceive(){//领取                
            if(this.multipleSelection.length==0){
                this.$message({message: '请选择资源', type: 'warning'});
                return false;
            }else{
            	var param={};
            	param.idList = []
            	 for(var i = 0 ; i < this.multipleSelection.length ; i++ ){
                     param.idList.push(this.multipleSelection[i].clueid)
                 }
            	  axios.post('/clue/cluereceiverecords/receiveClueByClueIds', param)
                  .then(function (response) {
                        var result =  response.data;
                        if(result.code == 0){
                        	if(result.data !=null){
                        		var data = result.data;
                        		if(data.backStatus ==3){
                        			mainDivVM.$message({message: data.backResult, type: 'warning'});
                        		}else if(data.backStatus !=0){
                        			mainDivVM.receiveDialog = true;
                        			mainDivVM.receiveTitle=data.backResult;
                        			mainDivVM.receiveTable= data.clueCustomerDTOs;
                        		}else{
                        			window.location.href="/aggregation/publiccustomer/listPage"; 
                        		}
                        	}                                
                        }else{
                        	mainDivVM.$message({message: result.msg, type: 'warning'});
                        }
                        
                  }).catch(function (error) {
                              console.log(error);
                  })           
                // 1、本组释放到公有池的资源，本组的电销人员不能再捡了
                // 2、总监领取老资源上限按照领取规则管理中设置的限制进行限制
                // 3、电销人员领取新资源上限按照领取规则管理中设置的限制进行限制
                var resourceType=1;//假数据
             /*    if(resourceType==1){
                    this.$alert('此资源（资源姓名+手机号）为组内资源，不可进行领取。', '提示', {
                      confirmButtonText: '确定',
                      callback: action => {
                        this.$message({
                          type: 'info',
                          message: `action: ${ action }`
                        });
                      }
                    });
                    return;
                }else if(resourceType==2){
                    this.$alert('今日领取超限制XX数。', '提示');
                    return;
                }else if(resourceType==3){
                    this.$alert('今日领取超限制XX数。', '提示');
                    return;
                }else{
                    //领取成功
                    this.$message({
                        message: '资源领取成功',
                        type: 'success'
                    });
                } */
            }
        },
        //展现详情
        showClueDetailInfo (row, column) {
            window.location.href='/tele/clueMyCustomerInfo/customerInfoReadOnly?clueId='+row.clueid+"&commonPool=1";
        },
        repeatPhonesClick(row) {//重复手机号按钮点击
            this.repeatPhonesDialog=true;
            this.dailogTitleType=row.phone;
            var param ={};
                param.id = row.clueId;
                  param.cusPhone = row.phone;
                    axios.post('/clue/appiontment/repeatPhonelist', param)
                      .then(function (response) {
                            var result =  response.data;
                            var table=result.data;
                            mainDivVM.repeatPhonesTable= table;
                            mainDivVM.repeatPhonesDialog=true;
                      })  .catch(function (error) {
                                  console.log(error);
            });
        },
        // 今日待跟进客户资源
        customerEidt(clueId,phone){
            //客户维护界面   
            window.location.href="/tele/clueMyCustomerInfo/customerEditInfo?clueId="+clueId; 
        },
        // 今日待跟进客户资源
        initTableData(){
            var param = {};
            // axios.post('/tele/clueMyCustomerInfo/findTeleClueInfo',param).then(function (response) {
            axios.post('/console/console/listTodayFollowClue',param).then(function (response) {
                if(!response){
                    mainDivVM.$message({
                        message: "接口调用失败",
                        type: 'error'
                      }); 
                    return ;
                }
                var resobj= response.data;
                if(!resobj){
                    mainDivVM.$message({
                        message: "接口调用失败",
                        type: 'error'
                      }); 
                    return ;
                } 
                if(resobj.code!='0'){
                    mainDivVM.$message({
                        message: "接口调用失败",
                        type: 'error'
                      }); 
                    return ;
                }
                var pageData=resobj.data;
                mainDivVM.pager.total=pageData.total;
                mainDivVM.pager.currentPage = pageData.currentPage;
                mainDivVM.pager.pageSize = pageData.pageSize;
                mainDivVM.dataTable =pageData.data;
            })
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
            // 今日领取资源数
            param={};
            axios.post('/console/console/countReceiveClueNum',param).then(function (response) {
                console.log('今日领取资源数')
                console.log(response.data)
                mainDivVM.receiveTodayNum=response.data.data;
            });
            // 今日分配资源数
            param={};
            axios.post('/console/console/countAssignClueNum',param).then(function (response) {
                console.log('今日分配资源数')
                console.log(response.data)
                mainDivVM.assignTodayNum=response.data.data;
            });
            // 今日通话时长
            param={};
            axios.post('/call/callRecord/countTodayTalkTime',param).then(function (response) {
                console.log('今日通话时长')
                console.log(response.data)
                mainDivVM.todayTalkTime=response.data.data;
            }); 
        }
    },
    created(){
        this.initList();
        //初始资源类别数据
        var  param={};
        param.groupCode="clueCategory";
        axios.post('/dictionary/DictionaryItem/dicItemsByGroupCode',param).then(function (response) {
            mainDivVM.categoryArr=response.data.data;
        });
        //初始化资源类型数据
        param={};
        param.groupCode="clueType";
        axios.post('/dictionary/DictionaryItem/dicItemsByGroupCode',param).then(function (response) {
            mainDivVM.typeArr=response.data.data;
        });
        // 初始化释放原因
        param={};
        param.groupCode="releaseReason";
        axios.post('/dictionary/DictionaryItem/dicItemsByGroupCode',param).then(function (response) {
            mainDivVM.reasonArr=response.data.data;
            if(mainDivVM.reasonArr.length==8){
                mainDivVM.reasonArr = mainDivVM.reasonArr.slice(0,7);
            }
        });
        //初始化客户状态
        param={};
        param.groupCode="customerStatus";
        axios.post('/dictionary/DictionaryItem/dicItemsByGroupCode',param).then(function (response) {
            mainDivVM.cusStatusArr=response.data.data;
        });

        // 今日待跟进客户资源
        //初始化数据列表
        this.initTableData();
        // 工作台
        this.initBoard();

    },
    mounted(){
        document.getElementById('mainDiv').style.display = 'block';
    }
});