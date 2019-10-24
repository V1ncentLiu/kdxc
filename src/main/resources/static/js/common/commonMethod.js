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