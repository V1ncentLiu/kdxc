var onlineRecordDialogTempate=
    '<el-dialog title="在线聊天记录"  :visible.sync="param.onlineRecordDialogVisible" width="1000px">'
    +'<p><span>客户姓名：</span><span style="margin-right:100px;">{{customerName}}</span><span>在线客服：</span><span>{{serviceName}}</span></p>'   
    +'<p>聊天内容：</p>'
    +'<div class="borderbox">'
    +'<ul>'
    +'<li></li>'
    +'</ul>'
    +'<div><p class="f-tac">加载更多内容</p></div>'
    +'</div>'
    +'</el-dialog>'

Vue.component('dialog-onlinerecord',{
    template:onlineRecordDialogTempate,
    props:['param'],
    data:()=>{
        return {
            customerName:'123',
            serviceName:'456',
        }
    },
    computed: {},
    watch: {
    },
    methods:{
        initData(){
            var customerName=this.customerName;
            var serviceName=this.serviceName;
            console.log(customerName)
            console.log(serviceName)
        }
    },
    created() {
        this.initData()
    }
});



