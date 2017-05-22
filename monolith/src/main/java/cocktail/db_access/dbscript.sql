/** Copyright 2016 Jens Edlund, Joakim Gustafson, Jonas Beskow, Ulrika Goloconda Fahlen, Jan Eriksson, Marcus Viden
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
        * @since 2016-04-18
**/

/*
The database is crated whit this script.
 */

CREATE TABLE tagInfo (
 tagID int(10) unsigned NOT NULL AUTO_INCREMENT,
 tagName varchar(50) NOT NULL,
 PRIMARY KEY (tagID)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE userInfo (
 userID int(10) unsigned NOT NULL AUTO_INCREMENT,
 userName varchar(80) NOT NULL,
 PRIMARY KEY (userID)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE fileInfo (
  fileID int(10) unsigned NOT NULL AUTO_INCREMENT,
  fileName varchar(100) NOT NULL,
  file longblob NOT NULL,
  fileSizeKb INT (10) NOT NULL,
  fileLenSec DOUBLE UNSIGNED NOT NULL,
  PRIMARY KEY (fileID)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE snippetInfo (
  snippetID int(10) unsigned NOT NULL AUTO_INCREMENT,
  fileID int(10) unsigned NOT NULL,
  sizeKb int(10) unsigned NOT NULL,
  startTime double NOT NULL,
  lenSec double unsigned NOT NULL,
  creationDate varchar(40) NOT NULL,
  lastModifiedDate varchar(40) NOT NULL,
  userID int(10) unsigned NOT NULL,
  PRIMARY KEY (snippetID),
  KEY fileID (fileID),
  CONSTRAINT snippetInfo_ibfk_1 FOREIGN KEY (fileID) REFERENCES fileInfo (fileID) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT snippetInfo_ibfk_2 FOREIGN KEY (userID) REFERENCES userInfo (userID) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;

CREATE TABLE bridgeSnippetTagTable (
 snippetID int(10) unsigned NOT NULL,
 tagID int(10) unsigned NOT NULL,
 KEY snippetID (snippetID),
 KEY tagID (tagID),
 CONSTRAINT bridgesnippettagtable_ibfk_1 FOREIGN KEY (snippetID) REFERENCES snippetInfo (snippetID) ON DELETE CASCADE ON UPDATE CASCADE,
 CONSTRAINT bridgesnippettagtable_ibfk_2 FOREIGN KEY (tagID) REFERENCES tagInfo (tagID) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
