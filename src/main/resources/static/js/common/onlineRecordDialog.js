var onlineRecordDialogTempate=
    '<el-dialog title="在线聊天记录"  :visible.sync="param.onlineRecordDialogVisible" width="1000px">'
    +'<p class="marginB10"><span>客户姓名：</span><span style="margin-right:100px;"></span><span>在线客服：</span><span></span></p>'   
    +'<p class="marginB10">聊天内容：</p>'
    +'<div class="borderbox padding10">'
    +'<ul>'
    +'<li v-for="item in param.newsData" :key="item.id">'
    +'<p class="padding10"><span>在线客服</span><span>{{ item.messageTime }}</span></p>'
    +'<p class="padding10"><span>{{ item.message }}</span></p>'
    +'</li>'
    +'</ul>'
    +'<div><p class="f-tac">加载更多内容</p></div>'
    +'</div>'
    +'</el-dialog>'

Vue.component('dialog-onlinerecord',{
    template:onlineRecordDialogTempate,
    props:['param'],
    computed: {
        data(){
            debugger
            console.log("newsData:"+this.param)
            return this.param.newsData
        }
    },
    watch: {
    },
    methods:{
        // initData(){
        //     var param = {};
        //     param.clueId = this.param.clueId;
        //     param.pageNum = 1;
        //     param.pageSize = 10;
        //     console.log("param================")
        //     console.log(param)
        //     axios.post('/log/imLog/queryIMLogRecord', param).then(function (response) {
        //         console.log("response==============")
        //         console.log(response)
        //         var recordData=response.data;
        //         if(recordData){
        //             var data=recordData.data.data;                    
        //             this.onlinerecordData=data;
        //             for(var i=0;i<data.length;i++){
        //                this.messageTime=data[0].messageTime; 
        //             }
                    
        //         }

        //     });

        // }
    }
});



