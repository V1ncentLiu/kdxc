/*add by gouruiling from H5 AUDIO*/

//H5 audio对象
var myAudio = new Audio("");

//当前正在播放的录音按钮id
var soundId;

//id 录音id ；url  录音播放文件地址
function switchSound(id,url){
	if(myAudio.paused){
		//暂停中,则播放
		myAudio.src = url;
		myAudio.currentTime = 0;
        myAudio.play();
		$("#"+id).removeClass("sound-trigger--type-default").addClass("sound-trigger--type-default-2");
        $("#"+id).find("i").removeClass("fa-play").addClass("fa-stop");
		soundId = id;
	}else{
		//播放中.判断是否是自身触发
		if(soundId==id){
			//自身触发，则暂停播放
			stopSound();	
		}else{
			//其他按钮触发，则更换播放地址，重新播放
			myAudio.src = url;
			myAudio.currentTime = 0;
			myAudio.play();
			$("#"+soundId).removeClass("sound-trigger--type-default-2").addClass("sound-trigger--type-default");
            $("#"+soundId).find("i").removeClass("fa-stop").addClass("fa-play");
			$("#"+id).removeClass("sound-trigger--type-default").addClass("sound-trigger--type-default-2");
            $("#"+id).find("i").removeClass("fa-play").addClass("fa-stop");
			soundId = id;
		}
		
	}
}

//监听播放结束事件，播放结束则还原按钮状态		
myAudio.addEventListener('ended', function () {  
			$("#"+soundId).removeClass("sound-trigger--type-default-2").addClass("sound-trigger--type-default");
    		$("#"+soundId).find("i").removeClass("fa-stop").addClass("fa-play");
}, false);


//停止播放
function stopSound(){
	$("#"+soundId).removeClass("sound-trigger--type-default-2").addClass("sound-trigger--type-default");
    $("#"+soundId).find("i").removeClass("fa-stop").addClass("fa-play");
	myAudio.pause();
	soundId = null;
}