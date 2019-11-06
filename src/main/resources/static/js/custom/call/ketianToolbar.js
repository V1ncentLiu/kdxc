
var KetianClientToolBar= window.NameSpace || {};
KetianClientToolBar.ketianClient = new function (){
	var self=this;
	self.hangup=function(){
        CtiAgentBar.hangup();
	};

	//播放挂机铃声
        self.playHangupMedia= function () {
        var hangupAudio = document.getElementById("hangupMediaAudioId")
        if(!hangupAudio){
            hangupAudio = document.createElement('audio');
            hangupAudio.id = 'hangupMediaAudioId';
            hangupAudio.hidden = true;
            hangupAudio.src = 'custom/wav/hangup.wav'
            document.body.appendChild(hangupAudio);
        }
        hangupAudio.play();
    };
    //播放来电振铃
    self.playRingMedia = function (){
        const _this = this;
        _this.stopPlayRingMedia();
        var ringAudio = document.createElement('audio');
        ringAudio.id = 'ringMediaAudioId';
        ringAudio.hidden = true;
        ringAudio.src = 'custom/wav/ring.wav'
        ringAudio.loop = 'loop';
        document.body.appendChild(ringAudio);
        ringAudio.play();
    };
    //停止播放来电振铃
    self.stopPlayRingMedia = function () {
        const _this = this;
        var ringAudio = document.getElementById("ringMediaAudioId");
        if (ringAudio) {
            document.body.removeChild(ringAudio);
        }
    };

};