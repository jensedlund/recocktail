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
 * @since 2016-05-09
 */

// A class mirroring the date format provided by backend

function DateStrings() {
    var actualDate = new Date();
    this.day = actualDate.getDate();
    this.month = actualDate.getMonth() + 1;
    this.year = actualDate.getFullYear();
}

// This class is equivalent in data to the backend class SnippetInfo but with an additional
// fileBlob field for storing binary data for a file,
// Used to populate an array in SnippetSet.
function SnippetInfo() {
    this.snippetID = 0;
    this.fileID = 0;
    this.fileName = "";
    this.tagNames = [];
    this.kbSize = 0;
    this.startTime = 0;
    this.lengthSec = 0;
    this.creationDate = new DateStrings();
    this.lastModified = new DateStrings();
    this.userID = 0;
    this.userName = "Unknown";
    this.multiples = 0;
    this.fileBlob = null;
}

// This class has the same fields as the backend class SnippetSet, but with different methods.
// There is an additional fields, zip, which is used as refrence to a JSZip object.
//
// When a SnippetSet is delivered from backend (in the form of a JSON object), use the method
// populateFromJson. This use case is for searching and uploading snippet sets from database.
//
// A snippet set can be populated from SnippetInfo object at a time with addSnippet
// Usually when creating new snippet to upload to database.
function SnippetSet() {
    this.snippetCollection = [];
    this.operationLog = [];
    this.maxLenSec = 0;
    this.minLenSec = 0;
    this.avgLenSec = 0;
    this.numSnippets = 0;
    this.creationDate = new DateStrings();
    this.setName = "Unknown-"
                   + this.creationDate.year + "-"
                   + this.creationDate.month + "-"
                   + this.creationDate.day;
    this.tagsInSet = [];
    this.zip = null;
}

// Set all fields from the proved JSON.
SnippetSet.prototype.populateFromJson = function(jsonSet) {
    this.snippetCollection = jsonSet.snippetCollection;
    this.operationLog = jsonSet.operationLog;
    this.setName = jsonSet.setName;
    this.maxLenSec = jsonSet.maxLenSec;
    this.minLenSec = jsonSet.minLenSec;
    this.avgLenSec = jsonSet.avgLenSec;
    this.numSnippets = jsonSet.numSnippets;
    this.creationDate = jsonSet.creationDate;
    this.tagsInSet = jsonSet.tagsInSet;
};

SnippetSet.prototype.addSnippet = function(snippetInfo) {
    this.snippetCollection.push(snippetInfo);
    for (var i = 0; i < snippetInfo.tagNames.length; i++) {
        this.tagsInSet.push(snippetInfo.tagNames[i]);
    }
    this.numSnippets++;
};

// Get a snippet by snippetID.
SnippetSet.prototype.getSnippet = function(id) {
    return this.snippetCollection.find(function(snippetInfo){return snippetInfo.snippetId});
};

// Add soundfiles(blobs) to zip, add xml file generated from SnippetSet, then post file to backend.
SnippetSet.prototype.uploadNewSnippets = function() {
    if(!this.zip) {
        this.zip = new JSZip();
    }
    // This will be lost in the "then" call of the promise.
    var that = this;
    this.addBlobsToZip();
    // When xml is ready compress zip and post it to backend.
    this.addXmlToZip().then(function() {
        that.zipAndPost();
    });
};

// Adds sound files to Zip.
SnippetSet.prototype.addBlobsToZip = function() {
    if(!this.zip) {
        this.zip = new JSZip();
    }
    var firstTagInSet = this.tagsInSet[0];
    var snippetFolder = this.zip.folder("snippets/" + firstTagInSet);
    this.snippetCollection.forEach(function(element) {
        snippetFolder.file(element.fileName, element.fileBlob, {base64 : true});
    });
};

// Send this SnippetSet to backend and get a xml back
SnippetSet.prototype.addXmlToZip = function() {
    if(!this.zip) {
        this.zip = new JSZip();
    }
    // This will be lost in the "then" call of the promise.
    var that = this;

    return new Promise(function(resolve, reject) {
        $.ajax({
                   url: serverUrl + "/getSnippetSetXml",
                   contentType: 'application/json; charset=utf-8',
                   type: 'POST',
                   data: JSON.stringify(that),
                   dataType: 'json',
                   async: true,
                   success: function (filename) {
                       var fileUrl = serverUrl + "/tmp/" + filename;
                       $.get( fileUrl )
                           .done(function( data ) {
                               that.zip.file(filename,data);
                               resolve(that.zip);
                           });
                   },
                   error: function (xhr, status) {
                       reject(xhr);
                   }
               });
    });
};

// Compress zip and post it to backend.
SnippetSet.prototype.zipAndPost = function() {
    this.zip.generateAsync({type: "blob", compression: "DEFLATE"})
        .then(function (content) {
            $.ajax({
                       type: 'POST',
                       url: serverUrl + "/writeSnippet",
                       data: content,
                       processData: false,
                       contentType: false
                   }).done(function(data) {
            })
        });
};
