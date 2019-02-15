function devinTest(params){
	console.info("alert success");
    CTILink.Agent.login(params, function(result) {
 	   console.info(result);
         if (result.code == 0) {
             //座席登录成功
         	console.info("...........sucess");
         } else {
             //座席登录失败，失败原因： + result.msg
         }
     });
	
	
}

