/*
* 会话模块 
*/

'use strict'

YX.fn.session = function () {
    $('#searchListInput').on('input',this.searchListInputOnmouse.bind(this)())
    $('#inputSearchImg').on('click',this.inputSearchImgClick.bind(this))
}
/**
 * 最近联系人显示
 * @return {void}
 */
YX.fn.buildSessions = function(id) {
    // var data = {
    //     sessions:this.cache.getSessions(),
    //     personSubscribes: this.cache.getPersonSubscribes()
    // }
    // console.log(data,'sessions列表数据');
    // if(!this.sessions){
    //     var options = {
    //         data:data,
    //         onclickavatar:this.showInfo.bind(this),
    //         onclickitem:this.openChatBox.bind(this),
    //         infoprovider:this.infoProvider.bind(this),

    //     } 
    //     this.sessions = new NIMUIKit.SessionList(options)
    //     this.sessions.inject($('#sessions').get(0))
    // }else{
    //     this.sessions.update(data)
    // }
    var searchListInputVal=$('#searchListInput').val()
    this.fuzzyQuerySearch(searchListInputVal)
    //导航上加未读示例  
    this.showUnread()         		
    this.doPoint()
    //已读回执处理
    this.markMsgRead(id)
    var $node = $(".m-unread .u-unread")
    $node.on('mouseenter', function () {
        $node.text('×')
    })
    $node.on('mouseleave', function () {
        $node.text(this.totalUnread)
    }.bind(this))
    $node.on('click', function (event) {
        this.nim.resetAllSessionUnread()
        event.preventDefault()
    }.bind(this))
}

YX.fn.searchListInputOnmouse = function () {
    let timer = null
    var that=this
    return function() {
          if (timer) clearTimeout(timer)
          timer = setTimeout(() => {
                if(!$('#searchListInput').val()){
                    $("#inputSearchImg").attr('src','../../../im/images/newImages/fada@3x.png');
                }else{
                    $("#inputSearchImg").attr('src','../../../im/images/newImages/qx@3x.png');
                }
              that.fuzzyQuerySearch($('#searchListInput').val())
          }, 800)
    }
}




YX.fn.fuzzyQuerySearch = function (value) {
    var allData=this.cache.getSessions()||[]
    console.log(allData,'聊天列表');
    var newArray=[]
    for(let i=0;i<allData.length;i++){
        if(allData[i].lastMsg.fromNick.indexOf(value)>-1){
            newArray.push(allData[i])
        }
    }
    var data = {
        sessions:value?newArray:allData,
        personSubscribes: this.cache.getPersonSubscribes()
    }
    if(!this.sessions){
        var options = {
            data:data,
            onclickavatar:this.showInfo.bind(this),
            onclickitem:this.openChatBox.bind(this),
            infoprovider:this.infoProvider.bind(this),

        } 
        this.sessions = new NIMUIKit.SessionList(options)
        this.sessions.inject($('#sessions').get(0))
    }else{
        this.sessions.update(data)
    }
}
YX.fn.inputSearchImgClick = function () {
    $("#searchListInput").val('');
    $("#inputSearchImg").attr('src','../../../im/images/newImages/fada@3x.png');
    this.fuzzyQuerySearch()
}

 // 导航上加未读数
YX.fn.showUnread = function () {
    var counts = $("#sessions .panel_count")
    this.totalUnread = 0
    if(counts.length!==0){
        if(this.totalUnread !=="99+"){
            for (var i = counts.length - 1; i >= 0; i--) {
                if($(counts[i]).text()==="99+"){
                    this.totalUnread = "99+"
                    break
                }
                this.totalUnread +=parseInt($(counts[i]).text(),10)
            }
        }
    }
    var $node = $(".m-unread .u-unread")
    $node.text(this.totalUnread)
    this.totalUnread?$node.removeClass("hide"):$node.addClass("hide")
}