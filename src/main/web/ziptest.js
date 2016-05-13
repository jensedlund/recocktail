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
 * @since 2016-04-20
 */

function create_zip() {
    var zip = new JSZip();
    zip.file("Hello.txt", "Hello World\n");
    var snippets = zip.folder("snippets");

    var oReq = new XMLHttpRequest();

    oReq.onload = function(e) {
        var soundArrBuf = oReq.response; // not responseText
        snippets.file("sound.wav", soundArrBuf, {base64: true});
        zip.generateAsync({type: "blob"})
            .then(function (content) {
                // see FileSaver.js
                saveAs(content, "example.zip");
                console.log("Händer saker här??")
            });
    }

    oReq.open("GET", 'snippets/dalripa-14/dalripa-1.wav', true);
    oReq.responseType = "arraybuffer";
    oReq.send();
}

function read_zip() {
    var f = document.getElementById("file").files[0];
    JSZip.loadAsync(f)
        .then(function(zip) {
            // var dateAfter = new Date();
            // $title.append($("<span>", {
            //     text:" (loaded in " + (dateAfter - dateBefore) + "ms)"
            // }));
            zip.forEach(function (relativePath, zipEntry) {
                console.log(zipEntry.name);

            });
            console.log("Funka!");
        }, function (e) {
            $fileContent = $("<div>", {
                "class" : "alert alert-danger",
                text : "Error reading " + f.name + " : " + e.message
            });
        });
    // var zip = new JSZip();
    // zip.loadAsync(zipDataFromXHR);
}
