/**
 * Created by Janne on 18/05/16.
 */

function SoundSet(context) {
    this.name = "";
    this.soundArray = [];
    this.processedSoundArray = [];
    this.gain = 0;
    this.gainVar = 0;
    this.delay = 0;
    this.delayVar = 0;
    this.balance = 0;
    this.balanceVar = 0;
    this.playing = false;
    this.context = context;
}

SoundSet.prototype.populateFromZip = function (zipBlob) {

    // Local references that to this due to this-scope being lost in function calls.
    var that = this;
    
    return new Promise(function(resolve, reject) {
        JSZip.loadAsync(zipBlob)
            .then(function (zip) {
                zip.forEach(function (relativePath, zipEntry) {
                    var reWav = new RegExp("wav$");
                    if (zipEntry.name == "SnippetSet.xml") {
                        zipEntry.async("String")
                            .then(function success(content) {
                                var parser = new DOMParser();
                                var xmlFileTest = parser.parseFromString(content, "text/xml");
                                that.name = xmlFileTest
                                    .getElementsByTagName("setName")[0]
                                    .childNodes[0]
                                    .nodeValue;
                                updateSoundSetList();
                            });
                    } else if (reWav.test(zipEntry.name)) {
                        zipEntry.async("arraybuffer")
                            .then(function (content) {
                                that.context.decodeAudioData(content).then(function (decodedData) {
                                    that.soundArray.push(decodedData);
                                })
                            });
                    }
                });
                console.log("Funka!");
                resolve(that);
            }, function (e) {
                $fileContent = $("<div>", {
                    "class": "alert alert-danger",
                    text: "Error reading " + f.name + " : " + e.message
                });
                reject(e);
            });
    });
};


// todo move sound process settings to callback
// todo create gain, pan etc on context creation
// todo check if elemtnt exist
SoundSet.prototype.shootSound = function () {
    if (this.playing) {

        var localContext = this.context;
        var bufferList = this.processedSoundArray;

        var source = localContext.createBufferSource();
        var soundIndex = Math.floor((Math.random() * bufferList.length));
        source.buffer = bufferList[soundIndex];

        var gainBox = localContext.createGain();
        var gain = parseFloat(document.getElementById("gain").value);
        var gainVar = parseFloat(document.getElementById("gainVar").value);
        var gainSum = gain + (Math.random() * gainVar);
        console.log("Gainsum " + gainSum + " Gain " + gain);
        gainBox.gain.value = gainSum;
        source.connect(gainBox);

        var balanceBox = localContext.createStereoPanner();

        // var panVal = (Math.round(Math.random() * 2)) - 1.0;
        var panVal = parseFloat(document.getElementById("balance").value);
        balanceBox.pan.value = panVal;
        gainBox.connect(balanceBox);
        balanceBox.connect(localContext.destination);

        source.start(0);

        var delay = document.getElementById("delay").value;
        // var gaussGen = new PolarDistribution();
        // console.log("Pan value " + panVal + " Gain " + gainSum);
        // console.log("Gaussian " + gaussGen.getGaussian(1, 0.25).toFixed(2));
        setTimeout(shootSound.bind(null), delay);
    }
};

SoundSet.prototype.stopPlayback = function () {
    this.playing = false;
};

SoundSet.prototype.startPlaback = function (weightedTime) {

    // Create processedSoundArray for playback, by either duplicate for
    // equal play time (weightedTime), or just copy.
    if (weightedTime) {
        this.soundArray.sort(function(a,b) {return a.duration - b.duration;});
        this.processedSoundArray = [];
        var minDuration = bufferList[0].duration;

        for (var i = 0; i < bufferList.length; i++) {
            var localSound = this.soundArray[i];
            var bufferDuration = localSound.duration;
            var weight = Math.round(bufferDuration / minDuration);
            for (var j = 0; j < weight; j++) {
                this.processedSoundArray.push(localSound);
            }
        }
    } else {
        this.processedSoundArray = this.soundArray;
    }
    this.playing = true;
    this.shootSound();
};

