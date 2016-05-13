<!DOCTYPE html>
<!--
  ~ Copyright 2016 Jens Edlund, Joakim Gustafson, Jonas Beskow, Ulrika Goloconda Fahlen, Jan Eriksson, Marcus Viden
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~ http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  ~
  ~ @version 1.0
  ~ @since 2016-04-11
  -->

<html lang="en">
<head>
    <meta charset="UTF-8">
    <script src="../web/script.js"></script>
    <link rel="stylesheet" href="../web/style.css">

    <title>ReCocktail</title>
</head>
<body>
<div setName ="utanförWrapper">
    <div setName="wrapper">
        <noscript>Please Enable JavaScript</noscript>

        <div setName="searchDiv">
            <h2>Search ${testar}</h2>
            <p>Search snippet info by tag names. Separate tags with whitespace.</p>
            <input setName="searchInput"> </input>
        </div>

        <div setName="savedSnippetSetDiv">
            <h2>Saved snippet sets</h2>
            <ul>
                <li>Forrest sounds</li>
                <li>Jungle sounds</li>
                <li>City sounds</li>
                <li>Sounds from beach with birds</li>
                <li>Ocean waves</li>
            </ul>

        </div>



        <div setName="soundscapeDiv">

            <h2>Area for editing</h2>
            <button setName="loadButton" onclick=loadSnippetSet()>Load SnippetSet info</button>

        </div>
    </div>
    <div setName="setInfoDiv">
        <h2>Snippet set Information</h2>
        <ul>
            <li>Snippet set name: </li>
            <li>Tags:
                <ul>
                    <li>Forrest</li>
                    <li>Bird</li>
                    <li>Sounds of Spring</li>
                </ul></li>
            <li>Exclusive: yes </li>
            <li>Snippet set size: </li>
            <li>Numer of snippets: </li>
            <li>Average length: </li>
            <li>Longest snippet: </li>
            <li>Shortest snippet: </li>
            <li>Comments: </li>
        </ul>

    </div>
</div>
</body>
</html>
