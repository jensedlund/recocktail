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
 * @since 2016-04-11
 * 
 */


window.onload = init;

// var context = [];
var context ;
// var otherContext;
var bufferLoader = [];

function init() {
    // Fix up prefixing
    window.AudioContext = window.AudioContext || window.webkitAudioContext;
    context = new AudioContext();

    // context[0] = new AudioContext();
    // context[1] = new AudioContext();
    // otherContext = new AudioContext();

    // Populating drop down selection.
    // var soundSelector = document.getElementById("soundSets");
    // loadedSoundSets.forEach(function (val) {
    //     var option = document.createElement("option");
    //     option.text = val.label;
    //     option.value = val.label;
    //     soundSelector.add(option);
    // });
    // document.getElementById("soundstart1").disabled = false;
    // document.getElementById("soundstart2").disabled = false;
    // document.getElementById("soundstop1").disabled = false;
    // document.getElementById("soundstop2").disabled = false;
    rangeSlider();
    getAllTags(populateAllTagsList);
}

function collectSoundParams(soundSet) {
    soundSet.gain = parseFloat(document.getElementById("gain").value);
    soundSet.gainVar = parseFloat(document.getElementById("gainVar").value);
    soundSet.balance = parseFloat(document.getElementById("balance").value);
    soundSet.delay = document.getElementById("delay").value;
    soundSet.delayVar = document.getElementById("delayVar").value;

}

var staph = [false, false];
function finishedLoading(contextVar, bufferList) {
    var snippetList = [];
    if (document.getElementById("weighted").checked) {
        bufferList.sort(function(a,b) {return a.duration - b.duration;});
        var minDuration = bufferList[0].duration;
        var maxDuration = bufferList[bufferList.length - 1].duration;
        var snippetArrWeighted = [];
        for (var i = 0; i < bufferList.length; i++) {
            var bufferDuration = bufferList[i].duration;
            var weight = Math.round(bufferDuration / minDuration);
            for (var j = 0; j < weight; j++) {
                snippetArrWeighted.push(bufferList[i]);
            }
        }
        // bufferLoader.snippetList = snippetArrWeighted;
        snippetList = snippetArrWeighted;
    } else {
        // bufferLoader.snippetList = bufferList;
        snippetList = bufferList;
    }
    shootSound(contextVar, snippetList);
}

function startSound(contextVar) {
    staph[contextVar] = false;
    var selected = document.getElementById("soundSets").selectedIndex;
    finishedLoading(contextVar, loadedSoundSets[selected].files);
}

function stopSound(contextVar) {
    staph[contextVar] = true;
}

function shootSound(contextVar, bufferList) {
    if (staph[contextVar] === false) {

        var localContext = context;

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
        setTimeout(shootSound.bind(null,contextVar,bufferList), delay);
    }
}

// Javascript verion of Java code from wikipedia on the Marsaglia polar
// method to creates a pseudo random normal (Gaussian) distribution.
function PolarDistribution() {
    this.spare = 0;
    this.isSpareReady = false;
}

PolarDistribution.prototype.getGaussian = function(mean, stdDev) {
    if (this.isSpareReady) {
        this.isSpareReady = false;
        return (this.spare * stdDev) + mean;
    } else {
        var u, v, s;
        do {
            u = Math.random() * 2 - 1;
            v = Math.random() * 2 - 1;
            s = u * u + v * v;
        } while (s >= 1 || s == 0);
        var mul = Math.sqrt(-2.0 * Math.log(s) / s);
        this.spare = v * mul;
        this.isSpareReady = true;
        return mean + (stdDev * u * mul);
    }
}