function distributeExportFun(falgNew,param){
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
        if(falgNew==1){ // 沟通情况
            homePageVM.$notify({
                type: 'success',
                title: '提示',
                message: '资源沟通记录下载完成',
                position: 'bottom-right'
            });
        }else{ // 资源记录
            homePageVM.$notify({
                type: 'success',
                title: '提示',
                message: '资源情况下载完成',
                position: 'bottom-right'
            });
        }        
    }).catch(function (error) {
        console.log(error);
        homePageVM.$message.error(error);
        homePageVM.$notify.error({
            title: '提示',
            message: '下载失败',
            position: 'bottom-right'
        });
    }).then(function(){
    });
}