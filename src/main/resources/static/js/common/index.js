 new Vue({
  el: '#app',
  data: function() {
    return { 
      isCollapse: true,//侧导航是否展开
      
    }
  },
  methods: {
    handleOpen(key, keyPath) {
      // console.log(key, keyPath);
    },
    handleClose(key, keyPath) {
      // console.log(key, keyPath);
    },
    toogleClick(){
      if(this.isCollapse){
        this.isCollapse=false
      }else{
        this.isCollapse=true
      }          
    },
    menuClick(){//菜单点击事件
      console.log(0)     
      
    }
  }
})
// 点击导航赋值ifream的src值
$(function () { 
  var mainBoxH=$(".mainBoxId").height()-40;
  // 设置ifream高度
  $("#iframeBox").height(mainBoxH)
  $(document).on('click','.menu',function(){
    console.log(0)
    console.log($(this).attr("data-url"))
    var dataUrl=$(this).attr("data-url");
    $("#iframeBox").attr({
      "src":dataUrl //设置ifream地址
    });  
  })
});