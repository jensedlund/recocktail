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

// This class wraps data and methods necessary to play a soundscape consisting of a number
// of snippets. A soundset is populated from a zip with sound files in wav format.
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

// Get a zip bytestream and extract sound files to populate soundArray field.
SoundSet.prototype.populateFromZip = function (zipBlob) {

    // Local references that to this due to this-scope being lost in function calls.
    var that = this;
    
    // Extract zip with JSZip
    JSZip.loadAsync(zipBlob)
        .then(function (zip) {
            zip.forEach(function (relativePath, zipEntry) {

                // Create a regexp pattern to catch files with wav ending.
                var reWav = new RegExp("wav$");

                // If xml file, get the set name from xml.
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

                    // For each wav file do an asynch load and decode it as a audio object.
                    // Push audio objects to soundArray.
                    zipEntry.async("arraybuffer")
                        .then(function (content) {
                            that.context.decodeAudioData(content).then(function (decodedData) {
                                that.soundArray.push(decodedData);
                            })
                        });
                }
            });
        }, function (e) {
            $fileContent = $("<div>", {
                "class": "alert alert-danger",
                text: "Error reading " + f.name + " : " + e.message
            });
        });
};

// Before starting playback of a soundscape, create a new processedSoundArray
// then make the first call to soundshooter method.
// weightedTime is a booleandesciding if shorter sounds should be played more often to equalize
// playtime between sounds.
// soundParamFunc is passed to shootSound. A callback that sets sound parameters.
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

// The heart of the sound generation.
// From the processed sound array, randomly selects an sound that is played in a fire and forget
// style. This method is looped as long as playing field on the object is true.
SoundSet.prototype.shootSound = function (soundParamFunc) {

    // Create a new context on this object if it does not exist.
    // Ideally there is one global object reference of AudioContext passed around because of
    // limited hardware resources allocated in browser.
    if(!this.context) {
        this.zip = new AudioContext();
    }

    // While palying true do stuff and call this method again with a delay
    if (this.playing) {
        // Some shortened vars...
        var localContext = this.context;
        var bufferList = this.processedSoundArray;

        // Create a sound source, gain and balance processor.
        var source = localContext.createBufferSource();
        var gainBox = localContext.createGain();
        var balanceBox = localContext.createStereoPanner();

        // Create a sound route by connecting the processors
        source.connect(gainBox);
        gainBox.connect(balanceBox);
        balanceBox.connect(localContext.destination);

        // Callback to set sound paramters
        if (soundParamFunc) {
            soundParamFunc(this);
        }

        //
        var soundIndex = Math.floor((Math.random() * bufferList.length));
        source.buffer = bufferList[soundIndex];

        var gainSum = this.gain + (Math.random() * this.gainVar);
        console.log("Gainsum " + this.gainSum + " Gain " + this.gain);
        gainBox.gain.value = gainSum;

        balanceBox.pan.value = this.balance;
        gainBox.connect(balanceBox);

        source.start(0);

        var delaySum = this.delay;
        setTimeout(this.shootSound.bind(this,soundParamFunc), delaySum);
    }
};

SoundSet.prototype.stopPlayback = function () {
    this.playing = false;
};
