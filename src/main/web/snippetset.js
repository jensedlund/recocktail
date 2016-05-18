/**
 * Created by Janne on 09/05/16.
 */


function DateStrings() {
    var actualDate = new Date();
    this.day = actualDate.getDate();
    this.month = actualDate.getMonth() + 1;
    this.year = actualDate.getFullYear();
}

// Todo fixa blob
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
    this.userName = "Admin";
    this.multiples = 0;
    this.fileBlob = null;
}

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

SnippetSet.prototype.getSnippet = function(id) {
    return this.snippetCollection.find(function(snippetInfo){return snippetInfo.snippetId});
};

SnippetSet.prototype.uploadNewSnippets = function() {
    if(!this.zip) {
        this.zip = new JSZip();
    }
    var that = this;
    this.addBlobsToZip();
    this.addXmlToZip().then(function() {
        that.zipAndPost();
    });
};

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

SnippetSet.prototype.addXmlToZip = function() {
    if(!this.zip) {
        this.zip = new JSZip();
    }

    var localThis = this;

    return new Promise(function(resolve, reject) {
        $.ajax({
                   url: serverUrl + "/getSnippetSetXml",
                   contentType: 'application/json; charset=utf-8',
                   type: 'POST',
                   data: JSON.stringify(localThis),
                   dataType: 'json',
                   async: true,
                   success: function (filename) {
                       var fileUrl = serverUrl + "/tmp/" + filename;
                       $.get( fileUrl )
                           .done(function( data ) {
                               localThis.zip.file(filename,data);
                               resolve(localThis.zip);
                           });
                   },
                   error: function (xhr, status) {
                       reject(xhr);
                   }
               });
    });
};

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
