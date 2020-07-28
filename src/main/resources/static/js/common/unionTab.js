var unionTabTemplate='<ul class="menuTab">'
                    +  '<li @click="gotoInfo(item.url)" :class="{active:item.isActive}" v-for="item in param.dataUrlArr"><span>{{item.name}}</span></li>'
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
        gotoInfo(val){//跳转未读消息
            window.location.href=val; 
        },
    }
});


