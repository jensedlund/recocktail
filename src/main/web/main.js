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
 * @since 2016-04-21
 */

//
// var serverUrl = "http://130.237.67.145:4567";
var serverUrl = "http://localhost:4567";


// Global variables
var context;
var activeSnippetSet = new SnippetSet();
var newSnippetSet = new SnippetSet();
var loadedSoundSets = [];
var runningSoundSets = [];

window.onload = init;

function init() {
    // Fix up prefixing
    window.AudioContext = window.AudioContext || window.webkitAudioContext;
    context = new AudioContext();

    // Get active sets from storge unit in backend
    // getActiveSets(updateSnippetSetList, "setA");
    // getActiveSets(updateSnippetSetList, "setB");
    // getActiveSets(updateSnippetSetList, "snippetSets");
    populateSetLists();
    populateSetOpList(["Union","Intersect","Complement","illegal"]);
    // Get all tags from database to use in autocomplete
    getAllTags(populateAllTagsList);
    rangeSlider();
}

function populateSetLists() {
    getActiveSets(updateSnippetSetList, "setA");
    getActiveSets(updateSnippetSetList, "setB");
    getActiveSets(updateSnippetSetList, "snippetSets");
}

// Collect sound parameters for playback if and only if the provided set
// is selected in drop down.
function collectSoundParams(soundSet) {
    var selectedIndex = document.getElementById("soundSets").selectedIndex;
    if (soundSet == loadedSoundSets[selectedIndex]) {
        soundSet.gain = parseFloat(document.getElementById("gain").value);
        soundSet.gainVar = parseFloat(document.getElementById("gainVar").value);
        soundSet.balance = parseFloat(document.getElementById("balance").value);
        soundSet.delay = document.getElementById("delay").value;
        soundSet.delayVar = document.getElementById("delayVar").value;
    }
}

// Kick off selected soundSet
function startSound() {
    var selectedIndex = document.getElementById("soundSets").selectedIndex;
    var weighted = document.getElementById("weighted").checked;
    var selectedSet = loadedSoundSets[selectedIndex];
    selectedSet.startPlaback(weighted,collectSoundParams);
    runningSoundSets.push(selectedSet);
    updateMixSetLists();
}

// Stop selected soundSet
function stopSound() {
    var selectedIndex = document.getElementById("soundSets").selectedIndex;
    var selectedSet = loadedSoundSets[selectedIndex];
    selectedSet.stopPlayback();
    runningSoundSets.splice(selectedIndex,1);
    updateMixSetLists();
}

// Get all tags from database. Send respons to callback.
function getAllTags(callback) {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function(){
        if(xhttp.readyState == 4 && xhttp.status == 200) {
            callback(xhttp.response);
        }
    };
    xhttp.open("GET", serverUrl + "/getAllTags", true);
    xhttp.send();
}

// Get all sets from StorageUnit in backend. Send response to callback.
function getActiveSets(callback,cbArg) {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function(){
        if(xhttp.readyState == 4 && xhttp.status == 200) {
            callback(JSON.parse(xhttp.response),cbArg);
            console.log(xhttp.response)
        }
    };
    xhttp.open("GET", serverUrl + "/getActiveSets", true);
    xhttp.send();
}

// Get a specific set by name. Send response to callback and also set activeSnippetSet to response.
function getSet(setName, callback) {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function(){
        if(xhttp.readyState == 4 && xhttp.status == 200) {
            console.log(setName);
            var snippetSet = new SnippetSet();
            snippetSet.populateFromJson(JSON.parse(xhttp.response));
            activeSnippetSet = snippetSet;
            callback(snippetSet);
        }
    };
    xhttp.open("GET", serverUrl + "/getSet/" + setName, true);
    xhttp.send();
}

// Take an expected array of tags and populate allTagsList
function populateAllTagsList(response) {
    clearDataList("allTagsList");
    var allTags = JSON.parse(response);
    allTags.sort();
    var list = document.getElementById("allTagsList");
    allTags.forEach(function(item) {
        var option = document.createElement('option');
        option.value = item;
        list.appendChild(option);
    });
}

// Can be a bit pointless due to
function clearDataList(parentListId) {
    var parentList = document.getElementById(parentListId);
    var optionArray = parentList.children;
    if (optionArray.length > 0) {
        optionArray.forEach(function(item) {
            parentList.removeChild(item);
        });
    }
}

// Post a database search and populate som fields with the result
function search() {
    var tags = document.getElementById("tags").value;
    var maxLen = document.getElementById("length").value;
    var exclusiveSearch = document.getElementById("exclusiveSearch").checked;
    var postBody = {
        tagNames: tags,
        maxLength: maxLen,
        exclusive: exclusiveSearch
    };

    $.ajax({
               url: serverUrl + "/search",
               contentType: 'application/json; charset=utf-8',
               type: 'POST',
               data: postBody,
               dataType: 'json',
               async: true,
               success: function (data) {
                   // Result is put into active snippet set
                   var snippetSet = new SnippetSet();
                   snippetSet.populateFromJson(data);
                   activeSnippetSet = snippetSet;
                   updateSnippetSetStats(snippetSet);

                   // Update the list since new snippetSet are
                   // expected to be available after search
                   // getActiveSets(updateSnippetSetList);
                   populateSetLists();
               },
               error: function (xhr, status) {
                   console.log(status);
                   console.log(xhr.responseText);
               }
           });
}

// Remove snippet from set
function removeSnippetFromSet() {
    var setName = document.getElementById("setInfoName").value;
    var snippetId = document.getElementById("snippetInfoId").value;
    var postBody = {
        snippetSetName: setName,
        snippetId: snippetId,
    };

    $.ajax({
               url: serverUrl + "/removeSnippet",
               contentType: 'application/json; charset=utf-8',
               type: 'POST',
               data: postBody,
               dataType: 'json',
               async: true,
               success: function (data) {
                   // Result is put into active snippet set
                   var snippetSet = new SnippetSet();
                   snippetSet.populateFromJson(data);
                   activeSnippetSet = snippetSet;
                   updateSnippetSetStats(snippetSet);

                   // Update the list since new snippetSet are
                   // expected to be available after search
                   getActiveSets(updateSnippetSetList);
               },
               error: function (xhr, status) {
                   console.log(status);
                   console.log(xhr.responseText);
               }
           });
}

// Execute a set operation
function setOperation() {
    var setNameA = document.getElementById("setA").value;
    var setNameB = document.getElementById("setB").value;
    var setOpName = document.getElementById("setOp").value;
    var postBody = {
        setA: setNameA,
        setB: setNameB,
        operation: setOpName,
    };

    $.ajax({
               url: serverUrl + "/setOperation",
               contentType: 'application/json; charset=utf-8',
               type: 'POST',
               data: postBody,
               dataType: 'json',
               async: true,
               success: function (data) {
                   // Result is put into active snippet set
                   var snippetSet = new SnippetSet();
                   snippetSet.populateFromJson(data);
                   activeSnippetSet = snippetSet;
                   updateSnippetSetStats(snippetSet);
                   
                   // Update the list since new snippetSet are
                   // expected to be available
                   // getActiveSets(updateSnippetSetList);
                   populateSetLists();
               },
               error: function (xhr, status) {
                   console.log(status);
                   console.log(xhr.responseText);
               }
           });
}

// Rename the set in BE and FE...
function renameSet() {
    var setNameInput = document.getElementById("setInfoName").value;
    var postBody = {
        setName: activeSnippetSet.setName,
        newSetName: setNameInput,
    };

    $.ajax({
               url: serverUrl + "/renameSet",
               contentType: 'application/json; charset=utf-8',
               type: 'POST',
               data: postBody,
               dataType: 'json',
               async: true,
               success: function (data) {
                   // Result is put into active snippet set
                   var snippetSet = new SnippetSet();
                   snippetSet.populateFromJson(data);
                   activeSnippetSet = snippetSet;
                   updateSnippetSetStats(snippetSet);

                   // Update the list since new snippetSet are
                   // expected to be available
                   // getActiveSets(updateSnippetSetList);
                   populateSetLists();
               },
               error: function (xhr, status) {
                   console.log(status);
                   console.log(xhr.responseText);
               }
           });
}

// Get zip url and send to parseZip
// todo broken return string
function getZip() {
    var snippetSelector = document.getElementById("snippetSets");
    var setName = snippetSelector.item(snippetSelector.selectedIndex).value;
    $.ajax({
               url: serverUrl + "/getZipUrl/" + setName,
               contentType: 'application/json; charset=utf-8',
               type: 'GET',
               async: true,
               success: function (data) {
                   var fileUrl = data;
                   parseZip("tmp/download.zip");
               },
               error: function (xhr, status) {
                   console.log(status);
                   console.log(xhr.responseText);
               }
           });
}

// Create a new soundset and populate it from the zip.
function parseZip(zipFileUrl) {
    var newSoundSet = new SoundSet(context);
    loadedSoundSets.push(newSoundSet);

    // To get a binary file is a bit tricky. A convencience method
    // provided by JSZip developers are used here.
    JSZipUtils.getBinaryContent(zipFileUrl, function(err, data) {
        if (err) {
            console.log(err);
        }
        newSoundSet.populateFromZip(data);
    });
}

function updateSoundSetList() {
    $("#soundSets").empty();
    document.getElementById("soundstart").disabled = true;
    document.getElementById("soundstop").disabled = true;

    var soundSelectorStrings = ["soundSets"];
    // var soundSelectorStrings = ["soundSets", "leftSet", "rightSet"];
    for (var i = 0; i < soundSelectorStrings.length; i++) {
        var soundSelector = document.getElementById(soundSelectorStrings[i]);
        for (var j = 0; j < loadedSoundSets.length; j++) {
            var option = document.createElement("option");
            var setName = loadedSoundSets[j].name;
            option.text = setName;
            option.value = setName;
            soundSelector.add(option);
            document.getElementById("soundstart").disabled = false;
            document.getElementById("soundstop").disabled = false;
        }
    }
}

function updateMixSetLists() {
    var soundSelectorStrings = ["leftSet", "rightSet"];
    for (var i = 0; i < soundSelectorStrings.length; i++) {
        $("#" + soundSelectorStrings[i]).empty();
        var soundSelector = document.getElementById(soundSelectorStrings[i]);
        for (var j = 0; j < runningSoundSets.length; j++) {
            var option = document.createElement("option");
            var setName = runningSoundSets[j].name;
            option.text = setName;
            option.value = setName;
            soundSelector.add(option);
        }
    }
}

function updateSnippetSetList(setNames, listId) {
    if (listId === undefined) {
        listId = "snippetSets";
    }

    $("#" + listId).empty();

    var snippetSelector = document.getElementById(listId);
    for (var i = 0; i < setNames.length; i++) {
        var option = document.createElement("option");
        var setName = setNames[i];
        console.log(setName);
        option.text = setName;
        option.value = setName;
        snippetSelector.add(option);
    }
}

function populateSetOpList(legalOps) {
    $("#setOp").empty();

    var opSelector = document.getElementById("setOp");
    for (var i = 0; i < legalOps.length; i++) {
        var option = document.createElement("option");
        var opName = legalOps[i];
        console.log(opName);
        option.text = opName;
        option.value = opName;
        opSelector.add(option);
    }
}

function updateSnippetSetStats(snippetSet) {
    $("#setInfoName")[0].value = snippetSet.setName;
    $("#setInfoNum")[0].value = snippetSet.numSnippets;
    $("#setInfoTags")[0].value = snippetSet.tagsInSet.toString();
    $("#snippetsInSet").empty();
    var snippetInfoSelector = document.getElementById("snippetsInSet");
    for (var i = 0; i < snippetSet.snippetCollection.length; i++) {
        var option = document.createElement("option");
        var setName = snippetSet.snippetCollection[i].snippetID;
        option.text = setName;
        option.value = setName;
        snippetInfoSelector.add(option);
    }
    updateSnippetStats(0);
}

function updateSnippetStats(selected) {
    var snippetInfo = activeSnippetSet.snippetCollection[selected];
    $("#snippetInfoId")[0].value = snippetInfo.snippetID;
    $("#snippetInfoTags")[0].value = snippetInfo.tagNames.toString();
    $("#snippetInfoFile")[0].value = snippetInfo.fileName;
    $("#snippetInfoStart")[0].value = snippetInfo.startTime;
    $("#snippetInfoDuration")[0].value = snippetInfo.lengthSec;
}

// unused atm
function getRelatedTags() {
    console.log(document.getElementById("searchInput").innerHTML);
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function(){
        if(xhttp.readyState == 4 && xhttp.status == 200){
            document.getElementById("complTags").value = xhttp.responseText;
        }
    };
    xhttp.open("GET", serverUrl + "/searchByTagName/complementary", true);
    xhttp.send();
}

// Collect data into a new SnippetInfo object
// Put object in new
function newSnippet() {
    var fileButton = document.getElementById("newFile");

    // FileList object
    var files = fileButton.files;
    var newSnippetInfo = new SnippetInfo();
    var tagsInput = document.getElementById("newTags");
    var tagList = tagsInput.value.split(" ");

    // Populate simple properties
    newSnippetInfo.tagNames = tagList;
    newSnippetInfo.fileName = encodeURI(files[0].name);

    // Read in the file as array buffer
    var fileReader = new FileReader();
    fileReader.readAsArrayBuffer(files[0]);
    fileReader.onloadend = function(event) {

        newSnippetInfo.fileBlob =  event.target.result;

        newSnippetInfo.kbSize = parseInt(files[0].size/1024);

        var startTime = document.getElementById("newStart");
        var duration = document.getElementById("newDuration");

        newSnippetInfo.lengthSec = parseFloat(duration.value);
        newSnippetInfo.startTime = parseFloat(startTime.value);
        newSnippetSet.addSnippet(newSnippetInfo);

        // Update table
        addSnippetToTable(newSnippetInfo);
    };
}

// When a new file is selected, populate fields
// to edit or leave unchanged for newSnippet call.
function fileSelectionUpdate() {

    var fileButton = document.getElementById("newFile");

    var files = fileButton.files; // FileList object

    // files is a FileList of File objects. List some properties.
    var output = [];
    for (var i = 0, f; f = files[i]; i++) {
        output.push('<li><strong>', encodeURI(f.name), '</strong> (', f.type || 'n/a', ') - ',
                    f.size, ' bytes, last modified: ',
                    f.lastModifiedDate ? f.lastModifiedDate.toLocaleDateString() : 'n/a',
                    '</li>');
    }

    document.getElementById('fileInfo').innerHTML = '<ul>' + output.join('') + '</ul>';

    var fileReader = new FileReader();
    fileReader.readAsArrayBuffer(files[0]);
    fileReader.onloadend = function(event) {
        context.decodeAudioData(event.target.result).then(function(decodedData) {
            var startTime = document.getElementById("newStart");
            var duration = document.getElementById("newDuration");
            startTime.value = 0;
            duration.value = decodedData.duration;
        });
    }
}

function addSnippetToTable(snippetInfo) {
    var snippTab = document.getElementById("newSnippetsTable");
    var row = snippTab.insertRow(1);
    var cellFile = row.insertCell(0);
    var cellStart = row.insertCell(1);
    var cellEnd = row.insertCell(2);
    var cellTags = row.insertCell(3);
    console.log(snippetInfo.startTime);
    console.log(snippetInfo.lengthSec);
    cellStart.innerHTML = snippetInfo.startTime.toFixed(2);
    cellEnd.innerHTML = snippetInfo.lengthSec.toFixed(2);
    cellFile.innerHTML = snippetInfo.fileName;
    cellTags.innerHTML = snippetInfo.tagNames;
}