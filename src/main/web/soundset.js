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
            }, function (e) {
                $fileContent = $("<div>", {
                    "class": "alert alert-danger",
                    text: "Error reading " + f.name + " : " + e.message
                });
            });
    });
};