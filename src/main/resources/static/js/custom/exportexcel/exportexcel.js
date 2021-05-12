function distributeExportFun(falgNew,param){
    var methodStr;
    if(falgNew==1  || falgNew==3 || falgNew==4){ // 沟通情况
        methodStr = 'findCommunicateRecords';
    }else{ // 资源记录
        methodStr = 'findClues';
    }
    axios.post('/exetend/distributionedTaskManager/'+methodStr,param,{responseType:'blob'})
    .then(function (response) {
        var data =  response.data;
        var fileName = response.headers.filename;
        saveAs(data,decodeURI(fileName));
        if(falgNew==1){ //导出电销沟通情况
            homePageVM.$notify({
                type: 'success',
                title: '提示',
                message: '电销资源沟通记录下载完成',
                position: 'bottom-right',
                duration: 0
            });
            localStorage.removeItem("distributeExport1");
        }else if(falgNew == 3){ // 导出话务沟通情况
            homePageVM.$notify({
                type: 'success',
                title: '提示',
                message: '话务资源沟通记录下载完成',
                position: 'bottom-right',
                duration: 0
            });
            localStorage.removeItem("distributeExport3");
        }else if(falgNew == 4){ // 导出话务沟通情况
            homePageVM.$notify({
                type: 'success',
                title: '提示',
                message: '顾问/经纪跟进记录 下载完成',
                position: 'bottom-right',
                duration: 0
            });
            localStorage.removeItem("distributeExport4");
        }else{ // 资源记录
            homePageVM.$notify({
                type: 'success',
                title: '提示',
                message: '资源情况下载完成',
                position: 'bottom-right',
                duration: 0
            });
            localStorage.removeItem("distributeExport2");
        }        
    }).catch(function (error) {
        console.log(error);
        if(falgNew==1){ // 沟通情况
            homePageVM.$notify.error({
                title: '提示',
                message: error + ',资源沟通记录下载失败',
                position: 'bottom-right'
            });
        }else{ // 资源记录
            homePageVM.$notify.error({
                title: '提示',
                message: error + ',资源情况下载失败',
                position: 'bottom-right'
            });
        }        
        localStorage.removeItem("distributeExport1");
        localStorage.removeItem("distributeExport2");
        localStorage.removeItem("distributeExport3");
        localStorage.removeItem("distributeExport4");
    }).then(function(){
    });
}

function abandonExport(param) {
    axios.post('/abandonsource/queryListExport',param,{responseType:'blob'})
        .then(function (response) {
            var data =  response.data;
            var fileName = response.headers.filename;
            saveAs(data,decodeURI(fileName));
            homePageVM.$notify({
                type: 'success',
                title: '提示',
                message: '导出下载完成',
                position: 'bottom-right',
                duration: 0
            });
            localStorage.removeItem("abandonExport");
        }).catch(function (error) {
        console.log(error);
        homePageVM.$notify.error({
            title: '提示',
            message: error + ',导出失败',
            position: 'bottom-right'
        });
        localStorage.removeItem("abandonExport");
    }).then(function(){
    });
}