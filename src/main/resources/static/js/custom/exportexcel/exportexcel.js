function exportFunnew(falgNew,param){
    var methodStr;
    if(falgNew==1){ // 沟通情况
        methodStr = 'findCommunicateRecords';
    }else{ // 资源记录
        methodStr = 'findClues';
    }
    axios.post('/exetend/distributionedTaskManager/'+methodStr,param,{responseType:'blob'})
    .then(function (response) {
        var data =  response.data;
        var fileName = response.headers.filename;
        saveAs(data,decodeURI(fileName));
        homePageVM.$notify({
            type: 'success',
            title: '提示',
            message: '下载完成',
            position: 'bottom-right',
            duration: 0
        });
    }).catch(function (error) {
        console.log(error);
        homePageVM.$notify.error({
            title: '提示',
            message: '下载失败',
            position: 'bottom-right',
            duration: 0
        });
    }).then(function(){
    });
}