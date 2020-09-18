var brandSelectionLayer
YX.fn.brandData = function () {
    // $('#brandSelection').on("click", this.showBrandHtml.bind(this));
    // window.addEventListener('message', function(result){
    //   var  data=result.data?JSON.parse(result.data):''
    //   console.log(data);
    //   // layer.close(brandSelectionLayer)
    // })
}

YX.fn.showBrandHtml = function () {
    brandSelectionLayer=layer.open({
        type: 2,
        title: '选品牌',
        shadeClose: true,
        shade: 0.4,
        area: ['750px', '425px'],
        content: '../../../im/selectionBrand.html' //iframe的url
      });
     
}