new Vue({
  	el: '#app',
  	data: function() {
	    return { 
	      	isCollapse: false,//侧导航是否展开
	      	isActive:true,
		   	items:[
		     	/*{ifreamUrl:'a.html',index:'1-1',name:"数据演示1"},
		     	{ifreamUrl:'b.html',index:'1-2',name:"数据演示2"}*/
		   	]
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
	        	this.isActive=true
	      	}else{
	        	this.isCollapse=true
	        	this.isActive=false
	      	}          
	    },
	    menuClick:function(ifreamUrl){
	    	console.log(this)
	     	this.$refs.iframeBox.src=ifreamUrl //给ifream的src赋值
	   	}
  	},
   	created() {  
   		document.body.removeChild(document.getElementById('Loading'))   
	}
})
// 点击导航赋值ifream的src值
$(function () { 
	var mainBoxH=$(".elMain").height()-4;
	console.log($(".elMain").height())
	// 设置ifream高度
	$("#iframeBox").height(mainBoxH)
	$(document).on('click','.menu',function(){
	    // console.log(0)
	    // console.log($(this).attr("data-url"))
	    var dataUrl=$(this).attr("data-url");
	    $("#iframeBox").attr({
	      "src":dataUrl //设置ifream地址
	    });  
	})
});