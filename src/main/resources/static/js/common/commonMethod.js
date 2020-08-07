function setSessionStore (name, content) {//存sessionStorage
    if (!name) return
    if (typeof content !== 'string') {
    content = JSON.stringify(content)
    }
    window.sessionStorage.setItem(name, content)
}
function getSessionStore (name) {//取sessionStorage
    if (!name) return;
    var content = window.sessionStorage.getItem(name);
    if (typeof content == 'string') {
        content = JSON.parse(content)
    }
    return content;
}
function removeSessionStore (name) {//移除sessionStorage
    if (!name) return
    return window.sessionStorage.removeItem(name)
}
function openCostList(){
    homePageVM.openCostList()
}
function openPaymentOnline(){
    homePageVM.openPaymentOnline()
}
//获取cookie
function getCookieVal(name){
    //可以搜索RegExp和match进行学习
    var arr,reg = new RegExp("(^| )" + name + "=([^;]*)(;|$)");
    if (arr = window.parent.document.cookie.match(reg)) {
        return unescape(arr[2]);
    } else {
        return null;
    }
}
function setLocalStore (name, content) {//存localstorage
    if (!name) return
    if (typeof content !== 'string') {
    content = JSON.stringify(content)
    }
    window.localStorage.setItem(name, content)
}
function getLocalStore (name) {//取localstorage
    if (!name) return;
    var content = window.localStorage.getItem(name);
    if (typeof content == 'string') {
        content = JSON.parse(content)
    }
    return content;
}
function getQueryString(name){//获取url地址栏参数的方法
    var reg = new RegExp("(^|&)"+ name +"=([^&]*)(&|$)");
    var r = window.location.search.substr(1).match(reg);
    if(r!=null)return  unescape(r[2]); return null;
}
// 餐盟页面不显示面包屑
var canment = window.localStorage.getItem('union');
if(canment=="unionStorage"){
    var elBreadcrumb=document.getElementsByClassName('elBreadcrumb');
    if(elBreadcrumb.length!=0){
        elBreadcrumb[0].style.display="none";
    }
}

function getWeb() {
    var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
    if ((userAgent.indexOf('Trident') > -1 && userAgent.indexOf("rv:11.0"))||((userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1))) {
        return false;
    } //判断是否IE浏览器
    else{
        return true;
    }
}
function getWebIE(){
   var userAgent = navigator.userAgent; //取得浏览器的userAgent字符串
   if ((userAgent.indexOf('Trident') > -1 && userAgent.indexOf("rv:11.0"))||(userAgent.indexOf("compatible") > -1 && userAgent.indexOf("MSIE") > -1)) {
    return true;
  } //判断是否IE浏览器
  else{
    return false;
  }
}