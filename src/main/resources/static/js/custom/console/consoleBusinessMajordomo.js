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
            // 待分配任务数 当月二次到访数 当月二次到访签约数
            param={};
            axios.post('/console/console/countBusinessDirectorCurMonthNum',param).then(function (response) {
                console.log('待分配任务数 当月二次到访数 当月二次到访签约数')                
                console.log(response)                
                // mainDivVM.unAssignNum=response.data.data.unAssignNum;
                // mainDivVM.secondVisitedNum=response.data.data.secondVisitedNum;
                // mainDivVM.secondSignedNum=response.data.data.secondSignedNum;
            }); 
            // 预计明日到访数
            param={};
            axios.post('/console/console/countBusiDirecotorTomorrowArriveTime',param).then(function (response) {
                console.log('预计明日到访数')                
                console.log(response)                
                mainDivVM.direcotorTomorrowArriveTime=response.data.data;
            });   
        },
    },
    created(){
        // 工作台
        this.initBoard();
    },
    mounted(){
        document.getElementById('mainDiv').style.display = 'block';
    }
});