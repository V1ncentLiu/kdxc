var mainDivVM = new Vue({
    el: '#mainDiv',
    data: {
        isElBreadcrumb:true,
    	receiveDialog:false,
    	receiveTitle:"",
    	dailogTitleType:"",
        repeatPhonesDialog:false,//今日待跟进客户资源重复手机号
        repeatPhonesDialog2:false,//快速领取新资源重复手机号
        formLabelWidth:"150px",
        // 快速领取新资源
        tableData:[],        
        repeatPhonesTable:[],
        repeatPhonesTable2:[],
        repeatPhonesTable3:[],
        repeatPhonesTable4:[],
        repeatPhonesTable5:[],
        showPhoneTable:false,
        showPhoneTable2:false,
        showPhoneTable3:false,
        showPhoneTable4:false,
        showPhoneTable5:false,
        phone:'',
        phone2:'',
        phone3:'',
        phone4:'',
        phone5:'',
        categoryArr:[],
        typeArr:[],
        reasonArr:[],
        cusStatusArr:[],
        multipleSelection:[],
        pager:{
            total: 0,
            currentPage: 1,
            pageSize: 20,
        },
        trackingParam:{
            trackingDialogVisible:false,
            tableData:[]
        },
        receiveTable:[],
        // 今日待跟进客户资源
        dataTable: [],
        // 工作台
        activeName:'1',
        activeName2:'1',
        activeName3:'1',
        activeName4:'1',
        activeName5:'1',
        receiveTodayNum:dashboardTelSale.todayReceiveResources,//今日领取资源数
        assignTodayNum:dashboardTelSale.todayDistributionResources,//今日分配资源数
        todayCallDuration:dashboardTelSale.todayCallDuration,//今日通话时长
        todayTalkTimeh:'',//今日通话时长
        todayTalkTimem:'',//今日通话时长
        todayTalkTimes:'',//今日通话时长
        todayAppiontmentNum:dashboardTelSale.todayFirstInvitation,//今日邀约数
        todayFirstVisit:dashboardTelSale.todayFirstVisit,//今日首访数
        todaySign:dashboardTelSale.todaySign, //今日签约数
        monthFirstVisit:dashboardTelSale.monthFirstVisit, //当月首访数
        monthSign:dashboardTelSale.monthSign, //当月签约数
        monthAllPerformance:dashboardTelSale.monthAllPerformance, //月业绩
        monthGroupRanking:dashboardTelSale.monthGroupRanking, //月组内排名
        monthCompanyRanking:dashboardTelSale.monthCompanyRanking, //月公司排名
        monthGroupPerformanceDifference:dashboardTelSale.monthGroupPerformanceDifference, //月距离组内上一名业绩差
        monthCompanyPerformanceDifference:dashboardTelSale.monthCompanyPerformanceDifference, //月距离公司上一名业绩差
        quarterAllPerformance:dashboardTelSale.quarterAllPerformance, //季度业绩
        quarterGroupRank:dashboardTelSale.quarterGroupRank, //季度组内排名
        quarterCompanyRank:dashboardTelSale.quarterCompanyRank, //季度公司排名
        quarterGroupPerformanceDifference:dashboardTelSale.quarterGroupPerformanceDifference, //季度距离组内上一名业绩差
        quarterCompanyPerformanceDifference:dashboardTelSale.quarterCompanyPerformanceDifference, //季度距离公司上一名业绩差
        yearAllPerformance:dashboardTelSale.yearAllPerformance, //年业绩
        yearGroupRank:dashboardTelSale.yearGroupRank, //年度组内排名
        yearCompanyRank:dashboardTelSale.yearCompanyRank, //年度公司排名
        yearGroupPerformanceDifference:dashboardTelSale.yearGroupPerformanceDifference, //年距离组内上一名业绩差
        yearCompanyPerformanceDifference:dashboardTelSale.yearCompanyPerformanceDifference, //年距离公司上一名业绩差
        totalInvitation:dashboardTelSale.totalInvitation,//累计邀约数
        cumulativeNonDrinkPerformance:dashboardTelSale.cumulativeNonDrinkPerformance,//小物种非饮品
        cumulativePerformance:dashboardTelSale.cumulativePerformance,//饮品业绩
        totalPerformance:'',
        workDay:'',//工作天数
        //公告
        afficheBox:false,
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
    	async gettelNumberIcon(tel,clueId) {//小图标外呼并跳转到电销维护页面
        	var isReturn =false;
        	await axios.post('/call/callRecord/missedCalPhone?phone='+tel, {})
            .then(function (response) {
            	if(response.data !=""){
            		mainDivVM.$message({
                        message: response.data,
                        type: 'warning'
                    });
            		isReturn = true; ;
            	}
               ;
            });
        	if(isReturn){
        		return;
        	}
            //外呼手机
            var param = {};
            param.clueId = clueId;
            var sessionStorage =window.sessionStorage;
            if(sessionStorage.getItem("phoneKey"+clueId) ==null || new Date().getTime()-sessionStorage.getItem("phoneKey"+clueId)>15000) {
                sessionStorage.setItem("phoneKey" + clueId, new Date().getTime());
                window.parent.parent.outboundCallPhone(tel, 2, clueId, function (res) {
                    axios.post('/tele/clueMyCustomerInfo/updateCallTime', param).then(function (response) {
                    });
                });
                //跳转页面
                window.location.href="/tele/clueMyCustomerInfo/customerEditInfo?clueId="+clueId;
            }


        },
        gotoMyCustomer(){//跳转我的客户
            window.location.href="/tele/clueMyCustomerInfo/initmyCustomer"; 
        },
        gotoPublicCustomer(){//跳转公有池
            window.location.href="/aggregation/publiccustomer/listPage"; 
        },
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
        // 今日待跟进客户资源进入我的客户详情
        showClueDetailInfo (row, column) {
            window.location.href='/tele/clueMyCustomerInfo/customerInfoReadOnly?clueId='+row.clueId;
        },
      customerEidt(row){
        var clueId=row.clueId;
        //客户维护界面
        this.setSessionStore("storeForm", this.storeForm);
        var otherVal = {
          "currentPage": this.pager.currentPage,
          "clueId": clueId,
          "scrollTop": this.$el.querySelector('.el-table__body-wrapper').scrollTop
        }
        this.setSessionStore("otherVal", otherVal);
        window.location.href="/tele/clueMyCustomerInfo/customerEditInfo?clueId="+clueId;
      },
        // 快速领取新资源
        initList(){
            var param = {};
            // param.pageSize = this.pager.pageSize;
            // param.pageNum =  this.pager.currentPage;
            param.pageSize = 20;
            param.pageNum =  1;
            axios.post('/aggregation/publiccustomer/queryPage',param).then(function (response) {
                console.log('快速领取新资源')
                console.log(response.data)
                if(null===response||response.data==null||response.data.code!='0'){
                    if(response.data.code!='0'){
                        mainDivVM.$message({message: response.data.msg, type: 'warning'});
                    }
                    return false;
                }else{
                    mainDivVM.tableData =response.data.data.data;
                }
            })
        },
        receiveDo(param){
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
                                // window.location.href="/aggregation/publiccustomer/listPage";
                                // 领取成功刷新表格
                                mainDivVM.initList();
                            }
                        }
                    }else{
                        mainDivVM.$message({message: result.msg, type: 'warning'});
                    }

                }).catch(function (error) {
                console.log(error);
            })
        },
        openReceive(row){//领取 
            console.log(row)               
            var param={};
            param.idList = []
            // for(var i = 0 ; i < this.multipleSelection.length ; i++ ){
            //     param.idList.push(this.multipleSelection[i].clueid)
            // }
            param.idList.push(row.clueId);
            axios.post('/clue/cluereceiverecords/validateCluePhase', param)
                .then(function (response) {

                    var result =  response.data;

                    if(result.code == 0){

                        mainDivVM.receiveDo(param);

                    }else{
                        mainDivVM.$confirm(result.msg, '提示', {
                            confirmButtonText: '知道了',
                            cancelButtonText: '取消',
                            type: 'warning'
                        }).then(() => {
                            mainDivVM.initList();
                    }).catch(() => {

                        });
                        mainDivVM.loading = false;
                        return false;
                    }
                }).catch(function (error) {
                console.log(error);
            })
            // 1、本组释放到公有池的资源，本组的电销人员不能再捡了
            // 2、总监领取老资源上限按照领取规则管理中设置的限制进行限制
            // 3、电销人员领取新资源上限按照领取规则管理中设置的限制进行限制
        },
        //快速领取新资源进入公有池展现详情
        showClueDetailInfo2 (row, column) {
            window.location.href='/tele/clueMyCustomerInfo/customerInfoReadOnly?clueId='+row.clueId+"&commonPool=1";
        },
        repeatPhonesClick(row) {//今日待跟进客户资源-我的客户重复手机号按钮点击
            debugger
            this.repeatPhonesDialog=true;
            this.dailogTitleType=row.clueId;
            this.repeatPhonesTable=[];
            var param ={};
            param.id = row.clueId;
            param.cusPhone = row.phone;
            param.clueId = row.clueId;
            axios.post('/clue/appiontment/repeatPhoneMap', param).then(function (response) {
                // var result =  response.data;
                // var table=result.data;
                // mainDivVM.repeatPhonesTable=table;
                var map = response.data.data;
                if(map.phone){
                    mainDivVM.phone = map.phones[0]
                    mainDivVM.repeatPhonesTable=map.phone;
                    mainDivVM.showPhoneTable = true;
                }else{
                    mainDivVM.phone = '';
                    mainDivVM.repeatPhonesTable=[];
                    mainDivVM.showPhoneTable = false;
                }
                if(map.phone2){
                    mainDivVM.phone2 = map.phones[1]
                    mainDivVM.repeatPhonesTable2=map.phone2;
                    mainDivVM.showPhoneTable2 = true;
                }else{
                    mainDivVM.phone2 ='';
                    mainDivVM.repeatPhonesTable2=[];
                    mainDivVM.showPhoneTable2 = false;
                }
                if(map.phone3){
                    mainDivVM.phone3 = map.phones[2]
                    mainDivVM.repeatPhonesTable3=map.phone3;
                    mainDivVM.showPhoneTable3 = true;
                }else{
                    mainDivVM.phone3 = '';
                    mainDivVM.repeatPhonesTable3=[];
                    mainDivVM.showPhoneTable3 = false;
                }
                if(map.phone4){
                    mainDivVM.phone4 = map.phones[3]
                    mainDivVM.repeatPhonesTable4=map.phone4;
                    mainDivVM.showPhoneTable4 = true;
                }else{
                    mainDivVM.phone4 = ''
                    mainDivVM.repeatPhonesTable4=[];
                    mainDivVM.showPhoneTable4 = false;
                }
                if(map.phone5){
                    mainDivVM.phone5 = map.phones[4]
                    mainDivVM.repeatPhonesTable5=map.phone5;
                    mainDivVM.showPhoneTable5 = true;
                }else{
                    mainDivVM.phone5 = map.phones[4]
                    mainDivVM.repeatPhonesTable5=[];
                    mainDivVM.showPhoneTable5 = false;
                }
                mainDivVM.repeatPhonesDialog=true;
            })  .catch(function (error) {
                console.log(error);
            });
        },
        repeatPhonesClick2(row) {//重复手机号按钮点击
            this.repeatPhonesDialog2=true;
            this.dailogTitleType=row.clueid;
            var param ={};
            param.id = row.clueid;
            param.cusPhone = row.phone;
            axios.post('/clue/appiontment/repeatPhonelist', param)
                .then(function (response) {
                    var result =  response.data;
                    console.info(result);
                    var table=result.data;
                    mainDivVM.repeatPhonesTable2= table;
                    mainDivVM.repeatPhonesDialog2=true;
                })  .catch(function (error) {
                    console.log(error);
            });
        },
        // 今日待跟进客户资源
        customerEidt(row){
            //客户维护界面   
            window.location.href="/tele/clueMyCustomerInfo/customerEditInfo?clueId="+row.clueId;
        },
        // 今日待跟进客户资源
        initTableData(){
            var param = {};
            var pageSize = this.pager.pageSize;
            var pageNum = this.pager.currentPage;
            param.pageNum=pageNum;
            param.pageSize=pageSize;
            // axios.post('/tele/clueMyCustomerInfo/findTeleClueInfo',param).then(function (response) {
            axios.post('/console/console/listTodayFollowClue',param).then(function (response) {
                console.log('今日待跟进客户资源')
                console.log(response.data)
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
        dateFormat:function(row, column) {//日期数据格式化方法
            var date = row[column.property];
             if (date == undefined) {
               return "";
             }
             return moment(date).format("YYYY-MM-DD HH:mm:ss");
        },        
        handleClick(tab, event) {// 工作台
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
            // 今日领取资源数
//            param={};
//            axios.post('/console/console/countReceiveClueNum',param).then(function (response) {
//                console.log('今日领取资源数')
//                console.log(response.data)
//                mainDivVM.receiveTodayNum=response.data.data;
//            });
//            // 今日分配资源数
//            param={};
//            axios.post('/console/console/countAssignClueNum',param).then(function (response) {
//                console.log('今日分配资源数')
//                console.log(response.data)
//                mainDivVM.assignTodayNum=response.data.data;
//            });
//            // 今日通话时长
//            param={};
//            axios.post('/call/callRecord/countTodayTalkTime',param).then(function (response) {
//                mainDivVM.todayTalkTime=mainDivVM.fomatSeconds2(response.data.data);
//                mainDivVM.todayTalkTimeh=mainDivVM.fomatSecondsh(response.data.data);
//                mainDivVM.todayTalkTimem=mainDivVM.fomatSecondsm(response.data.data);
//                mainDivVM.todayTalkTimes=mainDivVM.fomatSecondss(response.data.data);
//            }); 
//            // 今日邀约数
//            param={};
//            axios.post('/console/console/countTodayAppiontmentNum',param).then(function (response) {
//                console.log('今日邀约数')
//                console.log(response.data)
//                mainDivVM.todayAppiontmentNum=response.data.data;
//            }); 
            // 工作天数
            param={};
            axios.post('/console/console/getWorkDay',param).then(function (response) {
                console.log('工作天数')                
                console.log(response.data)                
                mainDivVM.workDay=response.data.data;
            });
        },
        fomatSeconds2(s){//格式化时间
     		var t="";
     		var hour = Math.floor(s/3600);
     		 var min = Math.floor(s/60) % 60;
              var sec = s % 60;
             if(hour<10){
             	t+="0";
             }
              t+=hour+"小时"
              if(min < 10){
              	t += "0";
              }
              t += min + "分";
              if(sec < 10){
              	t += "0";
             }
              t += sec + "秒";
              return t;
     	},
        fomatSecondsh(s){//格式化时间
            var t="";
            var hour = Math.floor(s/3600);
             var min = Math.floor(s/60) % 60;
              var sec = s % 60;
             if(hour<10){
                t+="0";
             }
              t+=hour;
              
              return t;
        },
        fomatSecondsm(s){//格式化时间
            var t="";
            var hour = Math.floor(s/3600);
             var min = Math.floor(s/60) % 60;
              var sec = s % 60;
              if(min < 10){
                t += "0";
              }
              t += min;
              return t;
        },
        fomatSecondss(s){//格式化时间
            var t="";
            var hour = Math.floor(s/3600);
             var min = Math.floor(s/60) % 60;
              var sec = s % 60;
              if(sec < 10){
                t += "0";
             }
              t += sec;
              return t;
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
        this.todayTalkTimeh = this.fomatSecondsh(dashboardTelSale.todayCallDuration);
        this.todayTalkTimem = this.fomatSecondsm(dashboardTelSale.todayCallDuration);
        this.todayTalkTimes = this.fomatSecondss(dashboardTelSale.todayCallDuration);
        this.totalPerformance = dashboardTelSale.cumulativeNonDrinkPerformance*1.5+dashboardTelSale.cumulativePerformance;
        // 今日待跟进客户资源
        //初始化数据列表
        this.initTableData();        
        // 获取url地址
        var type=getQueryString("sourceType");
        console.log(type)
        if(type==1){
            // 隐藏面包屑
            this.isElBreadcrumb=false;
            
        }
    },
    mounted(){
        document.getElementById('mainDiv').style.display = 'block';
    }
});