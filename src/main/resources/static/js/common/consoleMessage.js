var consoleMessageTempate=
    '<div v-show="param.newsBox">'
    +'<div class="title">'   
    +'<div class="leftbox f-fl"><img th:src="@{/images/icon_remind.png}" alt="" class="f-fl">消息提醒</div>'
    +'<div class="rightbox f-fr">进入消息中心<img th:src="@{/images/icon_arrow.png}" alt="" class="f-fr"></div>'
    +'</div>'
    +'<ul class="news">'
    +'<li v-for="item in param.newsData" :key="item.id"><em></em>{{ item.content }}</li>'
    +'</ul>'  
    +'</div>' 
Vue.component('dialog-console',{
    template:consoleMessageTempate,
    props:['param'],
    computed: {
        data(){
            console.log("newsData:"+this.param)
            return this.param.newsData
        }
    },
    watch: {},
    methods:{}
});



