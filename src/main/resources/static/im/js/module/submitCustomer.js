YX.fn.submitCustomer = function () {
    // 常用语  选品牌 提交客户 鼠标hover
    $('#commonWords').on("mouseenter", this.mouseenterHover.bind(this, 'commonWords'));
    $('#brandSelection').on("mouseenter", this.mouseenterHover.bind(this, 'brandSelection'));
    $('#submitCustomer').on("mouseenter", this.mouseenterHover.bind(this, 'submitCustomer'));
    $('#cloudMsg').on("mouseenter", this.mouseenterHover.bind(this, 'cloudMsg'));
    
    
    // 常用语  选品牌 提交客户 鼠标离开
    $('#commonWords').on("mouseleave", this.mouseleaveHover.bind(this, 'commonWords'));
    $('#brandSelection').on("mouseleave", this.mouseleaveHover.bind(this, 'brandSelection'));
    $('#submitCustomer').on("mouseleave", this.mouseleaveHover.bind(this, 'submitCustomer'));
    $('#cloudMsg').on("mouseleave", this.mouseleaveHover.bind(this, 'cloudMsg'));

    // 点击提交客户 弹出框
    $('#submitCustomer').on("click", this.submitCustomerClick.bind(this));

    // 提交客户弹框取消按钮
    $('#generalFormLastCancel').on("click", this.generalFormLastClick.bind(this,'cancel'));
    
    // 提交客户弹框提交按钮
    $('#generalFormLastSubmit').on("click", this.generalFormLastClick.bind(this,'submit'));
    
    
    $('#commonWordsList li').on("click",this.commonWordsListClick.bind(this));

}

YX.fn.commonWordsListClick = function () {
    var e = e||window.event;
    var tar=$(event.target)
    if(tar[0].localName=='li'){
        var txt=tar.html()
        $('#messageText').val(txt)
        $('#commonWordsList').css({
            'display': 'none'
        })
        $('#messageText').focus()
    }

}

YX.fn.generalFormLastClick = function (type) {
    if(type=='submit'){
        // alert('表单提交');
        var that = this;
        var name=$("#submitCustomerFormName").val();
        var customerId="";
        var phone=$("#submitCustomerFormIphone").val();
        var phone2=$("#submitCustomerFormIphoneTwo").val();
        var wechat=$("#submitCustomerFormWechat").val();
        var remark=$("#submitCustomerBeizhu").val();
        var teleSaleId="";
        var params = {
            name: name,
            customerId: customerId,
            phone: phone,
            phone2: phone2,
            wechat: wechat,
            remark: remark,
            teleSaleId: teleSaleId
        };
        $.ajax({
          url: '/im/submit',
          type: 'POST',
          data: params,
          contentType: 'application/json',
          success: function(data) {
            if (data.res === 200) {
              console.log('保存成功');
              
            } else {
               console.log('保存失败'); 
            }
          },
          error: function() {
             console.log('请求失败，请重试');
          }
        });
    }
}

YX.fn.submitCustomerClick = function () {
    var submitCustomerLayer
    submitCustomerLayer=layer.open({
        type: 1,
        title: '提交客户',   //标题
        area: ['375px', '430px'],   //宽高
        shade: 0.3,   //遮罩透明度
        content: $("#submitCustomerFormBar"),//支持获取DOM元素
        // btn: ['确定', '取消'], //按钮组
        scrollbar: false,//屏蔽浏览器滚动条
        // maxmin: true,
        // offset: [document.documentElement.clientHeight-800+'px', document.documentElement.clientWidth-400+'px']
    })
    $("#generalFormLastCancel").click(function(){
        layer.close(submitCustomerLayer)
    });
    // 回显客户信息
    var that = this;
    var params = {
            id: ""
    };
    $.ajax({
        url: '/custservice/customerInfo/customerInfoByIm',
        type: 'POST',
        data: params,
        contentType: 'application/json',
        success: function(data) {
            if (data.res === 200) {
                var data=data.data;
                var phoneNumber=data.phoneNumber;
                var phoneNumber=data.phoneNumber;
                console.log('回显客户信息');
                $("#submitCustomerFormName").val('1');      
                $("#submitCustomerFormIphone").val(phoneNumber);
              
            } else {
                console.log('回显客户信息失败');
            }
        },
        error: function() {
            console.log('请求失败，请重试');
        }
    });

}
YX.fn.mouseenterHover = function (type) {
    switch (type) {
        case 'commonWords':
            $('#commonWords img').attr("src", "../../../im/images/newImages/icon_yy@3x.png");
            $('#commonWords p').css({
                'color': '#026ADD'
            })
            $('#commonWordsList').css({
                'display': 'block'
            })
            break;
        case 'brandSelection':
            $('#brandSelection img').attr("src", "../../../im/images/newImages/icon_xpp拷贝@3x.png");
            $('#brandSelection p').css({
                'color': '#026ADD'
            })
            break;
        case 'submitCustomer':
            $('#submitCustomer img').attr("src", "../../../im/images/newImages/icon_tj拷贝@3x.png");
            $('#submitCustomer p').css({
                'color': '#026ADD'
            })
            break;
        case 'cloudMsg':
            $('#cloudMsg img').attr("src", "../../../im/images/newImages/icon_lt拷贝@3x.png");
            $('#cloudMsg p').css({
                'color': '#026ADD'
            })
            break;
        default: return ''

    }

};

YX.fn.mouseleaveHover = function (type) {
    switch (type) {
        case 'commonWords':
            $('#commonWords img').attr("src", "../../../im/images/newImages/icon_yycopy@3x.png");
            $('#commonWords p').css({
                'color': '#666',
            })
            $('#commonWordsList').css({
                'display': 'none'
            })
            break;
        case 'brandSelection':
            $('#brandSelection img').attr("src", "../../../im/images/newImages/icon_xpp@3x.png");
            $('#brandSelection p').css({
                'color': '#666'
            })
            break;
        case 'submitCustomer':
            $('#submitCustomer img').attr("src", "../../../im/images/newImages/icon_tj@3x.png");
            $('#submitCustomer p').css({
                'color': '#666'
            })
            break;
        case 'cloudMsg':
            $('#cloudMsg img').attr("src", "../../../im/images/newImages/icon_lt@3x.png");
            $('#cloudMsg p').css({
                'color': '#666'
            })
            break;
        default: return ''

    }

};