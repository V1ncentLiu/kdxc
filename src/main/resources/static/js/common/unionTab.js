var unionTabTemplate='<ul class="menuTab">'
                    +  '<li @click="gotoInfo(item.url,item.type)" :class="{active:item.isActive}" v-for="item in param.dataUrlArr"><span>{{item.name}}</span></li>'
                    +  '</ul>'

Vue.component('table-uniontabtemplate',{
    template:unionTabTemplate,
    props:['param'],
    computed: {
        data(){
            console.log(123)
            return this.param.dataUrlArr
        }
    },
    watch: {},
    methods:{
        gotoInfo(val,type){//跳转tab页面
            window.location.href=val; 
            // 清session
            if(type=="hJtype"){
                window.sessionStorage.clear(); // 点击tab同点击侧边栏-清除所有session
            }
        },
    }
});


