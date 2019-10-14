var myCallRecordVm = new Vue({
  el: '#myCallRecordVm',
  data: {
    audioShow: false,
    isShow: false,
    isDXZDisabled: false,
    formLabelWidth: '120px',
    pager: {//组织列表pager
      total: 0,
      currentPage: 1,
      pageSize: 20,
    },
    totalTalkTime: 0,
    callRecordData: [],
    callStatus: [
      {
        value: "-1",
        label: '全部'
      },
      {
        value: "0",
        label: '未接通'
      }, {
        value: "1",
        label: '已接通'
      }],
    callTypeList: [
      {
        value: "-1",
        label: '全部'
      }, {
        value: "1",
        label: '呼入'
      }, {
        value: "2",
        label: '外呼'
      }],
    searchForm: {
      bindAccount: '',
      seatNumber: '',
      seatPhone: '',
      startTime: '',
      endTime: '',
    },
    userInfoList: userInfoList,//绑定账户
    totalMerchantCost:totalMerchantCost,
    isActive1:true,
    isActive2:false,
    isActive3:false,
    isActive4:false,
  },
  methods: {
    transCusPhone(row) {
      var text = "";
      if (row.clueId && (row.phase == 7 || row.phase == 8 || (roleCode == 'DXZJ' && orgId != (row.teleGorupId + "")))) {
        text = "***"
      } else {
        text = row.customerPhone;
      }
      return text;
    },

    // _initData() {
    //   this.callRecordData = [{
    //     costMonth: "客户姓名",
    //     bindAccountName: "客户电话",
    //     seatNumber: "电话归属地",
    //     seatPhone: "热线号码",
    //     monthRent: "拨打账户",
    //     cost: "坐席电话",
    //     callNumber: "开始服务时间",
    //   },
    //   {
    //     costMonth: "客户姓名",
    //     bindAccountName: "客户电话",
    //     seatNumber: "电话归属地",
    //     seatPhone: "热线号码",
    //     monthRent: "拨打账户",
    //     cost: "坐席电话",
    //     callNumber: "开始服务时间",
    //   }]

    // },
    initCallRecordData() {
      // this._initData();
       var startTime = this.searchForm.startTime;
       var endTime = this.searchForm.endTime;
       var startTimestamp = Date.parse(new Date(startTime));
       if(endTime){
      	 var endTimestamp = new Date(endTime);
      		 if(startTimestamp> endTimestamp){
      			 this.$message({
                       message: '开始时间必须小于结束时间',
                       type: 'warning'
                     });
                   return;
               }

       }
       var callStatus = this.searchForm.callStatus;
       if(callStatus=='-1'){
      	 this.searchForm.callStatus='';
       }
       var callType = this.searchForm.callType;
       if(callType=='-1'){
      	 this.searchForm.callType='';
       }
       var param = this.searchForm;
       var accountId =this.searchForm.accountId;
       if(accountId){
      	 var accountIdArr = new Array();
      	 accountIdArr.push(accountId);
      	 param.accountIdList=accountIdArr;
       }else{
      	 param.accountIdList=[];
       }
      	 param.beginCostTime=new Date(this.searchForm.startTime);
      	 param.endCostTime=new Date(this.searchForm.endTime);
      	 param.pageNum=this.pager.currentPage;
      	 param.pageSize=this.pager.pageSize;
      	 axios.post('/merchant/bussinessCallCost/getBussinessCallCostList',param)
           .then(function (response) {
          	 var data =  response.data;
               if(data.code=='0'){
               	var resData = data.data;
               	var callRecordData = resData.data;
               	var callRecordDataData = callRecordData.data;
                //  for(var i=0;i<callRecordDataData.length;i++){
                //    callRecordDataData[i].customerPhone=myCallRecordVm.transCusPhone(callRecordDataData[i]);
                //  }
               	myCallRecordVm.callRecordData= callRecordData.data;
                //3.分页组件
               	myCallRecordVm.pager.total= callRecordData.total;
               	myCallRecordVm.pager.currentPage = callRecordData.currentPage;
               	myCallRecordVm.pager.pageSize = callRecordData.pageSize;

               }else{
              	 myCallRecordVm.$message({message:data.msg,type:'error'});
               	 console.error(data);
               }

           })
           .catch(function (error) {
                console.log(error);
           }).then(function(){
           });
    },
    resetForm(formName) {
      if (this.$refs[formName]) {
        this.$refs[formName].resetFields();
        this.$set(this.searchForm,'startTime','');
        this.$set(this.searchForm,'endTime','');
      }
    },
    getCallTypeText(row, column, value, index) {
      var valText = "";
      if (value == "1") {
        valText = "呼入";
      } else if (value == "3") {
        valText = "外呼";
      }

      return valText;
    }
    , getCallStatusText(row, column, value, index) {
      var valText = "";
      if (value == "1" || value == "33" || value == "52") {
        valText = "接通";
      } else {
        valText = "未接通";
      }

      return valText;
    },
    getStartTimeText(row, column, value, index) {
      var valText = "";
      if (value) {
        valText = moment.unix(Number(value)).format("YYYY-MM-DD HH:mm:ss");
      }
      return valText;
    },
    getTalkTimeText(row, column, value, index) {
      var valText = "";
      if (value) {
        valText = this.formatSeconds(value);
      }
      return valText;
    },
    formatSeconds(s) {
      var t = "";
      var hour = Math.floor(s / 3600);
      var min = Math.floor(s / 60) % 60;
      var sec = s % 60;
      if (hour < 10) {
        t += "0";
      }
      t += hour + ":";
      if (min < 10) {
        t += "0";
      }
      t += min + ":";
      if (sec < 10) {
        t += "0";
      }
      t += sec;
      // t += sec.toFixed(2);
      return t;
    },
    fomatSeconds2(s) {
      var t = "";
      var hour = Math.floor(s / 3600);
      var min = Math.floor(s / 60) % 60;
      var sec = s % 60;
      if (hour < 10) {
        t += "0";
      }
      t += hour + "小时"
      if (min < 10) {
        t += "0";
      }
      t += min + "分钟";
      if (sec < 10) {
        t += "0";
      }
      t += sec + "秒";
      return t;
    },

    searchYesterday() {
      this.isActive1=true;
      this.isActive2=false;
      this.isActive3=false;
      this.isActive4=false;
      var today = new Date();
      today.setTime(today.getTime() - 24 * 60 * 60 * 1000);
      var startTime = today.getFullYear() + "-" + (today.getMonth() + 1) + "-" + today.getDate() + " 00:00:00";
      var endTime = today.getFullYear() + "-" + (today.getMonth() + 1) + "-" + today.getDate() + " 23:59:59";
      this.searchForm.startTime = startTime;
      this.searchForm.endTime = endTime;
      this.initCallRecordData();
    },
    searchWeek() {
      this.isActive2=true;
      this.isActive1=false;
      this.isActive3=false;
      this.isActive4=false;
      var a = new Date();
      var year = a.getFullYear();
      var month = a.getMonth();
      var date = a.getDate();
      var b = new Date(year, month, date);
      var c = b.valueOf() - 6 * 24 * 60 * 60 * 1000;
      var d = b.valueOf();
      var startTime = new Date(c);
      var endTime = new Date(d);
      this.searchForm.startTime = startTime.getFullYear() + "-" + (startTime.getMonth() + 1) + "-" + startTime.getDate() + " 00:00:00";
      this.searchForm.endTime = endTime.getFullYear() + "-" + (endTime.getMonth() + 1) + "-" + endTime.getDate() + " 23:59:59";
      this.initCallRecordData();
    },
    searchMonth() {
      this.isActive3=true;
      this.isActive1=false;
      this.isActive2=false;
      this.isActive4=false;
      var a = new Date();
      var year = a.getFullYear();
      var month = a.getMonth();
      var date = a.getDate();
      var b = new Date(year, month, date);
      var f = new Date(year, month, 01);
      var c = f.valueOf();
      //  var d = b.valueOf()+1*24*60*60*1000;
      var startTime = new Date(c);
      // var endTime = new Date(d);
      this.searchForm.startTime = startTime.getFullYear() + "-" + (startTime.getMonth() + 1) + "-" + startTime.getDate() + " 00:00:00";
      this.searchForm.endTime = year + "-" + (month + 1) + "-" + date + " 23:59:59";
      this.initCallRecordData();
    },
    searchMonthHistory(){
      this.isActive4=true;
      this.isActive1=false;
      this.isActive2=false;
      this.isActive3=false;
      var a = new Date();
      var year = a.getFullYear();
      var month = a.getMonth();
      var date = a.getDate();
      var b = new Date(year, month, date);
      var f = new Date(year, 01, 01);
      var c = f.valueOf();
      //  var d = b.valueOf()+1*24*60*60*1000;
      var startTime = new Date(c);
      // var endTime = new Date(d);
      this.searchForm.startTime = startTime.getFullYear() + "-" + startTime.getMonth() + "-" + startTime.getDate() + " 00:00:00";
      this.searchForm.endTime = year + "-" + (month + 1) + "-" + date + " 23:59:59";
      this.initCallRecordData();
    },
    downloadAudio(id, url, callSource) {
      if (roleCode == 'ZCBWY') {
        this.$message({
          message: '您没有下载权限',
          type: 'warning'
        });
        return;
      }
      var param = {};
      param.id = id;
      axios.post('/call/callRecord/getRecordFile', param)
        .then(function (response) {
          var data = response.data;
          if (data.code == '0') {
            var url = data.data;
            if (url) {
              var fileName = url.split('?')[0];
              var fileNameArr = fileName.split("/");
              if (callSource == '3') {
                var decodeUrl = encodeURI(url);
                url = "/client/heliClient/downloadHeliClientAudio?url=" + decodeUrl;
              }
              var x = new XMLHttpRequest();
              x.open("GET", url, true);
              x.responseType = 'blob';
              x.onload = function (e) { download(x.response, fileNameArr[fileNameArr.length - 1], 'audio/*'); }
              x.send();

            }

          } else {
            myCallRecordVm.$message({ message: data.msg, type: 'error' });
            console.error(data);
          }

        })
        .catch(function (error) {
          console.log(error);
        }).then(function () {
        });

    },
    switchSoundBtn(id, url, callSource) {
      // debugger
      // this.audioShow=true;
      if (callSource == '2') {
        // switchSound(id,url);
        window.parent.open(url)
        return;
      }
      var newWindow = window.open();
      var param = {};
      param.id = id;
      axios.post('/call/callRecord/getRecordFile', param)
        .then(function (response) {
          var data = response.data
          if (data.code == '0') {
            var url = data.data;
            // switchSound(id,url);
            newWindow.location.href = url;
          } else {
            console.error(data);
            myCallRecordVm.$message({ message: data.msg, type: 'error' });
          }

        })
        .catch(function (error) {
          console.log(error);
        }).then(function () {
        });
    },
    toogleClick() {
      if (this.isShow) {
        this.isShow = false
      } else {
        this.isShow = true
      }
    },
    // clearTeleGroupList(selectedValue) {
    //   this.teleGroupList = [];
    //   this.tmList = [];
    //   this.searchForm.accountId = '';
    //   this.searchForm.teleGroupId = '';
    // }


  },
  created() {
    var a = new Date();
    var year = a.getFullYear();
    var month = a.getMonth();
    var date = a.getDate();
    this.searchForm.startTime = year + "-" + (month + 1) + "-" + date + " 00:00:00";
    this.searchForm.endTime = year + "-" + (month + 1) + "-" + date + " 23:59:59";
    //初始资源类别数据
    // param = {};
    // param.groupCode = "clueCategory";
    // axios.post('/dictionary/DictionaryItem/dicItemsByGroupCode', param).then(function (response) {
    //   myCallRecordVm.categoryArr = response.data.data;
    // });
    //电销总监电销组筛选按钮不可点击
    this.initCallRecordData();
  },
  mounted() {
    document.getElementById('myCallRecordVm').style.display = 'block';
  },
  filters: {
    fomatSeconds2(s) {
      var t = "";
      var hour = Math.floor(s / 3600);
      var min = Math.floor(s / 60) % 60;
      var sec = s % 60;
      if (hour < 10) {
        t += "0";
      }
      t += hour + "小时"
      if (min < 10) {
        t += "0";
      }
      t += min + "分钟";
      if (sec < 10) {
        t += "0";
      }
      t += sec + "秒";
      return t;
    }
  }

})