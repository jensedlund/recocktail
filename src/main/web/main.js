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
// var serverUrl = "http://130.237.67.145:4567";
var serverUrl = "http://localhost:4567";

var globalSnippetSets = [];
var globalActiveSnippetSet = new SnippetSet();
var zipForUpload = new JSZip();
var zipForDownload = new JSZip();
var snippetZipDir = zipForUpload.folder("snippets");
var newSnippetSetGlobal = new SnippetSet();
var localContext = new AudioContext();
var loadedSoundSets = [];


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

function getActiveSets(callback) {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function(){
        if(xhttp.readyState == 4 && xhttp.status == 200) {
            // callback(xhttp.response);
            console.log(xhttp.response)
        }
    };
    xhttp.open("GET", serverUrl + "/getActiveSets", true);
    xhttp.send();
}

function getSet(setName, callback) {
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function(){
        if(xhttp.readyState == 4 && xhttp.status == 200) {
            // callback(xhttp.response);
            console.log(JSON.parse(xhttp.response));
        }
    };
    xhttp.open("GET", serverUrl + "/getSet/" + setName, true);
    xhttp.send();
}

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

function clearDataList(parentListId) {
    var parentList = document.getElementById(parentListId);
    var optionArray = parentList.children;
    if (optionArray.length > 0) {
        optionArray.forEach(function(item) {
            parentList.removeChild(item);
        });
    }
}

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
                   // console.log(data);
                   populateSetInfo(data);
                   var snippetSet = new SnippetSet();
                   snippetSet.populateFromJson(data);
                   globalSnippetSets.push(snippetSet);
               },
               error: function (xhr, status) {
                   console.log(status);
                   console.log(xhr.responseText);
               }
           });
}

function getXmlFromSet(snippetSet) {
    $.ajax({
               url: serverUrl + "/getSnippetSetXml",
               contentType: 'application/json; charset=utf-8',
               type: 'POST',
               data: JSON.stringify(snippetSet),
               dataType: 'json',
               async: true,
               success: function (data) {
                   var fileUrl = serverUrl + "/" + data;
                   addServerFileToZip(data,fileUrl);
               },
               error: function (xhr, status) {
                   console.log(status);
                   console.log(xhr.responseText);
               }
           });
}

function getZip() {
    $.ajax({
               url: serverUrl + "/getZipUrl",
               contentType: 'application/json; charset=utf-8',
               type: 'GET',
               async: true,
               success: function (data) {
                   console.log(data);
                   var fileUrl = data;
                   console.log(fileUrl);
                   parseZip("tmp/download.zip");
               },
               error: function (xhr, status) {
                   console.log(status);
                   console.log(xhr.responseText);
               }
           });
}

// var xmlFileTest;

function parseZip(zipFileUrl) {
    // var localContext = new AudioContext();
    var newSoundSet = {};
    loadedSoundSets.push(newSoundSet);
    newSoundSet.files = [];

    JSZipUtils.getBinaryContent(zipFileUrl, function(err, data) {
        if(err) {
            console.log(err);
        }
        JSZip.loadAsync(data)
            .then(function(zip) {
                zip.forEach(function (relativePath, zipEntry) {
                    var reWav = new RegExp("wav$");
                    if (zipEntry.name == "SnippetSet.xml") {
                        zipEntry.async("String")
                            .then(function success(content) {
                                var parser = new DOMParser();
                                var xmlFileTest = parser.parseFromString(content,"text/xml");
                                newSoundSet.label = xmlFileTest
                                    .getElementsByTagName("setName")[0]
                                    .childNodes[0]
                                    .nodeValue;

                                var soundSelector = document.getElementById("soundSets");
                                var option = document.createElement("option");
                                option.text = newSoundSet.label;
                                option.value = newSoundSet.label;
                                soundSelector.add(option);
                            });
                        
                    } else if (reWav.test(zipEntry.name)) {

                        zipEntry.async("arraybuffer")
                            .then(function (content) {
                                localContext.decodeAudioData(content).then(function(decodedData) {
                                    newSoundSet.files.push(decodedData);
                            })
                        });
                    }
                });
                console.log("Funka!");
            }, function (e) {
                $fileContent = $("<div>", {
                    "class" : "alert alert-danger",
                    text : "Error reading " + f.name + " : " + e.message
                });
            });
    });
}

function addServerFileToZip(name,url) {
    $.ajax({
               url: url,
               type: 'get',
               success: function (data) {
                   console.log(data);
                   zipForUpload.file(name, data);
               },
               error: function (xhr, status) {
                   console.log(status);
                   console.log(xhr.responseText);
               }
           });
}

var snippetSetTest;
function populateSetInfo (response) {
    // snippetSetTest1 = JSON.parse(response.responseText);
    snippetSetTest = response;
    // getXmlFromSet(snippetSetTest);
    // var newSet = JSON.parse(response.responseText);
    var snippetInfoArr = response.snippetCollection;
    
    var snippTab = document.getElementById("snippetSetTab");
    snippetInfoArr.forEach(function (item) {
        var row = snippTab.insertRow(0);
        var cellLen = row.insertCell(0);
        var cellName = row.insertCell(1);
        var cellTags = row.insertCell(2);
        cellLen.innerHTML = item.lengthSec;
        cellName.innerHTML = item.fileName;
        cellTags.innerHTML = item.tagNames;
    });

    // var soundSelector = document.getElementById("snippetSets");
    // var option = document.createElement("option");
    // option.text = newSoundSet.label;
    // option.value = newSoundSet.label;
    // soundSelector.add(option);
}

function getComplementaryTags() {
    console.log(document.getElementById("searchInput").innerHTML);
    var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function(){
        if(xhttp.readyState == 4 && xhttp.status == 200){
            console.log("I ifsatsen comp " + xhttp.responseText);
            document.getElementById("complTags").value = xhttp.responseText;
        }
    };
    xhttp.open("GET", serverUrl + "/searchByTagName/complementary", true);
    xhttp.send();
}

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
        newSnippetSetGlobal.addSnippet(newSnippetInfo);

        // Update table
        addSnippetToTable(newSnippetInfo);
    };
}
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
        snippetZipDir.file(files[0].name, event.target.result, {base64 : true});

        localContext.decodeAudioData(event.target.result).then(function(decodedData) {
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