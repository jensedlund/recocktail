/*
 * Copyright 2016 Jens Edlund, Joakim Gustafson, Jonas Beskow, Ulrika Goloconda Fahlen, Jan Eriksson, Marcus Viden
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @version 1.0
 * @since 2016-05-18
 */

function SoundSet(context) {
    this.name = "";
    this.soundArray = [];
    this.processedSoundArray = [];
    this.gain = 0.2;
    this.gainVar = 0.2;
    this.delay = 50;
    this.delayVar = 10;
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

SoundSet.prototype.shootSound = function (soundParamFunc) {
    if (this.playing) {

        var localContext = this.context;
        var bufferList = this.processedSoundArray;

        var source = localContext.createBufferSource();
        var source = localContext.createBufferSource();
        var gainBox = localContext.createGain();
        var balanceBox = localContext.createStereoPanner();

        source.connect(gainBox);
        gainBox.connect(balanceBox);
        balanceBox.connect(localContext.destination);

        soundParamFunc(this);

        var soundIndex = Math.floor((Math.random() * bufferList.length));
        source.buffer = bufferList[soundIndex];

        var gainSum = this.gain + (Math.random() * this.gainVar);
        console.log("Gainsum " + this.gainSum + " Gain " + this.gain);
        gainBox.gain.value = gainSum;

        // var panVal = (Math.round(Math.random() * 2)) - 1.0;
        balanceBox.pan.value = this.balance;
        gainBox.connect(balanceBox);

        source.start(0);

        // var gaussGen = new PolarDistribution();
        // console.log("Pan value " + panVal + " Gain " + gainSum);
        // console.log("Gaussian " + gaussGen.getGaussian(1, 0.25).toFixed(2));
        var delaySum = this.delay;
        setTimeout(this.shootSound.bind(this,soundParamFunc), delaySum);
        // setTimeout(this.shootSound, delay);
    }
};

SoundSet.prototype.startPlaback = function (weightedTime, soundParamFunc) {
    if (!this.playing) {
        // Create processedSoundArray for playback, by either duplicate for
        // equal play time (weightedTime), or just copy.
        if (weightedTime) {
            this.soundArray.sort(function (a, b) {
                return a.duration - b.duration;
            });
            this.processedSoundArray = [];
            var minDuration = this.soundArray[0].duration;

            for (var i = 0; i < this.soundArray.length; i++) {
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
        this.shootSound(soundParamFunc);
    }
};

SoundSet.prototype.stopPlayback = function () {
    this.playing = false;
};
