var myCallRecordVm = new Vue({
    el: '#myCallRecordVm',
    data: {
    	formLabelWidth:'120px',
	    pager:{//组织列表pager
          total: 0,
          currentPage: 1,
          pageSize: 20,
        },
        totalTalkTime:0,
        callRecordData:[],
        callStatus:[{
            value: "1",
            label: '接通'
        }, {
            value: "0",
            label: '未接通'
        }],
        callTypeList:[{
            value: "1",
            label: '呼入'
        }, {
            value: "2",
            label: '外呼'
        }],
        searchForm:{
        	callStatus:'',
        	customerName:'',
        	customerPhone:'',
        	callType:'',
        	
        }
    },
    methods:{
    	initCallRecordData(){
    		 var param = this.searchForm;
        	 param.pageNum=this.pager.currentPage;
        	 param.pageSize=this.pager.pageSize;
        	 axios.post('/call/callRecord/listMyCallRecord',param)
             .then(function (response) {
            	 var data =  response.data
                 if(data.code=='0'){
                 	var resData = data.data;
                 	var callRecordData = resData.data;
                 	myCallRecordVm.callRecordData= callRecordData.data;
                 	myCallRecordVm.totalTalkTime = resData.totalTalkTime;
                  //3.分页组件
                 	myCallRecordVm.pager.total= callRecordData.total;
                 	myCallRecordVm.pager.currentPage = callRecordData.currentPage;
                 	myCallRecordVm.pager.pageSize = callRecordData.pageSize;
                     
                 }else{
                	 myCallRecordVm.$message({message:'初始化通话记录列表错误',type:'error'});
                 	 console.error(data);
                 }
             
             })
             .catch(function (error) {
                  console.log(error);
             }).then(function(){
             });
    	},
    	getCallTypeText(row, column, value, index){
    		var valText="";
    		if(value=="1"){
    			valText="呼入";
    		}else if(value=="2"){
    			valText="外呼";
    		}
    		
    		return valText;
    	}
    	,getCallStatusText(row, column, value, index){
    		var valText="";
    		if(value=="1" || value=="33" || value=="52"){
    			valText="接通";
    		}else{
    			valText="未接通";
    		}
    		
    		return valText;
    	},
    	getStartTimeText(row, column, value, index){
    		var valText="";
    		if(value){
    			valText =  moment.unix(value).format("YYYY-MM-DD HH:MM:SS");
    		}
    		return valText;
    	},
    	getTalkTimeText(row, column, value, index){
    		console.log(value);
    		var valText="";
    		if(value){
    			valText =  this.formatSeconds(value);
    		}
    		return valText;
    	},
    	formatSeconds(s){
    		var t="";
            var min = Math.floor(s/60) % 60;
            var sec = s % 60;
           
           if(min < 10){
            	t += "0";
            }
            t += min + ":";
           if(sec < 10){
            	t += "0";
           }
            t+=sec;
           // t += sec.toFixed(2);
		  return t;
    	},
    	fomatSeconds2(s){
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
             t += min + "分钟";
             if(sec < 10){
             	t += "0";
            }
             t += sec + "秒";
             return t;
    	}
    },
    created(){
      this.initCallRecordData();
   },
   mounted(){
     	document.getElementById('myCallRecordVm').style.display = 'block';
     },
     filters:{
    	 fomatSeconds2(s){
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
              t += min + "分钟";
              if(sec < 10){
              	t += "0";
             }
              t += sec + "秒";
              return t;
     	}
     }

})