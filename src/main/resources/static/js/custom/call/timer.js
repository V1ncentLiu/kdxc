/**
 * 计时
 * @param id 
 * @param noticeSecs 时间
 * @param timeUnit 单位  1：秒 2:分钟
 * @returns
 */
var outboundCallIntervalTimer;  
function intervalTimer(id,noticeSecs,timeUnit) {
	var ele_timer = document.getElementById(id);	
	var n_sec = 0; //秒
	var n_min = 0; //分
	var n_hour = 0; //时
	outboundCallIntervalTimer =  setInterval(function () {		 
		var str_sec = n_sec;
		var str_min = n_min;
		var str_hour = n_hour;
		if ( n_sec < 10) {
		str_sec = "0" + n_sec;
		}
		if ( n_min < 10 ) {
		str_min = "0" + n_min;
		}

		if ( n_hour < 10 ) {
		str_hour = "0" + n_hour;
		}
		//分钟计时
		if(timeUnit==2){
			if(n_min>=noticeSecs){
				ele_timer.style.color='red';
			 }else{
				 ele_timer.style.color='black';
			 }
		}
		
	  
		var time = str_hour + "小时" + str_min + "分" + str_sec+"秒";
		// ele_timer.value = time;
		ele_timer.innerHTML = time;
		n_sec++;
		if (n_sec > 59){
			n_sec = 0;
			n_min++;
		}
		if (n_min > 59) {
			n_min = 0;
			n_hour++;
		} 	 
 	}, 1000);
}
//清除
function clearTimer(){
    clearInterval(outboundCallIntervalTimer);
}