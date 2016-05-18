/**
 * Created by Janne on 18/05/16.
 */

function SoundSet(context) {
    this.name = "";
    this.soundArray = [];
    this.weightedSoundArray = [];
    this.gain = 0;
    this.gainVar = 0;
    this.delay = 0;
    this.delayVar = 0;
    this.balance = 0;
    this.balanceVar = 0;
    this.playing = false;
    this.context = context;
}

SoundSet.prototype.populateFromZip = function (zipFile) {
    
}