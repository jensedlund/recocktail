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
 * @since 2016-04-05
 */

package cocktail.snippet;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 Copyright 2016 Jens Edlund, Joakim Gustafson, Jonas Beskow, Ulrika Goloconda Fahlen, Jan Eriksson, Marcus Viden
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



public class SnippetInfo implements Comparable<SnippetInfo> {


  private int snippetID;
  private int fileID;
  private String fileName;

  private List<String> tagNames;
  private List<Integer> tagIDs;

  private int kbSize;
  private double startTime;
  private double lengthSec;

  private LocalDate creationDate;
  private LocalDate lastModified;

  private int userID;
  private String userName;

  private int multiples;

  public SnippetInfo(int snippetID, int sourceID, String fileName, List<String> tagNames,
                     double startTime, double lengthSec, int kbSize, LocalDate creationDate,
                     LocalDate lastModified, int userID, String userName) {

    this.snippetID = snippetID;
    this.fileID = sourceID;
    this.fileName = fileName;

    this.tagNames = tagNames;

    this.kbSize = kbSize;
    this.startTime = startTime;
    this.lengthSec = lengthSec;

    this.creationDate = creationDate;
    this.lastModified = lastModified;

    this.userName = userName;
    this.userID = userID;

    this.multiples = 1;
  }

  public SnippetInfo(String fileName, List<String> tagNames,
                     double startTime, double lengthSec, int kbSize, LocalDate creationDate,
                     LocalDate lastModified, String userName) {

    this.fileName = fileName;

    this.tagNames = tagNames;


    this.kbSize = kbSize;
    this.startTime = startTime;
    this.lengthSec = lengthSec;

    this.creationDate = creationDate;
    this.lastModified = lastModified;

    this.userName = userName;

    this.multiples = 1;
  }

  public SnippetInfo(){}

  public void setTagIDs(List<Integer> tagIDs){
    this.tagIDs = tagIDs;
  }

  public List<Integer> getTagIDs(){
    return tagIDs;
  }
  public String getUserName(){
  return userName;
}

  public int getUserID() {
    return userID;
  }

  public void setUserID(int userID) {
    this.userID = userID;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }

  public int getSnippetID() {
    return snippetID;
  }

  public void setSnippetID(int snippetID) {
    this.snippetID = snippetID;
  }

  public int getFileID() {
    return fileID;
  }

  public void setFileID(int fileID) {
    this.fileID = fileID;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public List<String> getTagNames() {
    return tagNames;
  }

  public void setTagNames(List<String> tagNames) {
    this.tagNames = tagNames;
  }

  public int getKbSize() {
    return kbSize;
  }

  public void setKbSize(int kbSize) {
    this.kbSize = kbSize;
  }

  public double getStartTime() {
    return startTime;
  }

  public void setStartTime(double startTime) {
    this.startTime = startTime;
  }

  public double getLengthSec() {
    return lengthSec;
  }

  public void setLengthSec(double lengthSec) {
    this.lengthSec = lengthSec;
  }

  public LocalDate getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(LocalDate creationDate) {
    this.creationDate = creationDate;
  }

  public LocalDate getLastModified() {
    return lastModified;
  }

  public void setLastModified(LocalDate lastModified) {
    this.lastModified = lastModified;
  }

  public int getMultiples() {
    return multiples;
  }

  public void setMultiples(int multiples) {
    this.multiples = multiples;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SnippetInfo that = (SnippetInfo) o;
    return snippetID == that.snippetID &&
           fileID == that.fileID &&
           kbSize == that.kbSize &&
           Double.compare(that.startTime, startTime) == 0 &&
           Double.compare(that.lengthSec, lengthSec) == 0 &&
           userID == that.userID &&
           multiples == that.multiples &&
           Objects.equals(fileName, that.fileName) &&
           Objects.equals(tagNames, that.tagNames) &&
           Objects.equals(creationDate, that.creationDate) &&
           Objects.equals(lastModified, that.lastModified) &&
           Objects.equals(userName, that.userName);
  }

  @Override
  public int hashCode() {
    return Objects.hash(snippetID, fileID, fileName, tagNames, kbSize, startTime, lengthSec,
                        creationDate, lastModified, userID, userName, multiples);
  }



  @Override
  public String toString() {
    return "SnippetInfo{" +
           "snippetID=" + snippetID +
           ", fileID=" + fileID +
           ", fileName='" + fileName + '\'' +
           ", tagNames=" + tagNames +
           ", kbSize=" + kbSize +
           ", startTime=" + startTime +
           ", lengthSec=" + lengthSec +
           ", creationDate=" + creationDate +
           ", lastModified=" + lastModified +
           ", userID=" + userID +
           ", userName='" + userName + '\'' +
           ", multiples=" + multiples +
           '}';
  }


  @Override
  public int compareTo(SnippetInfo that) {

    if (this.lengthSec < that.lengthSec) {
      return -1;
    } else if (this.lengthSec > that.lengthSec) {
      return 1;
    }

    if (this.fileID < that.fileID) {
      return -1;
    } else if (this.fileID > that.fileID) {
      return 1;
    }

    if (this.multiples < that.multiples) {
      return -1;
    } else if (this.multiples > that.multiples) {
      return 1;
    }

    // Todo implement sparse vector spaces clustering
    // Todo http://stackoverflow.com/questions/1539745/clustering-huge-vector-space
    String thisTagsString = this.tagNames.stream().sorted().collect(Collectors.toList()).toString();
    String thatTagsString = that.tagNames.stream().sorted().collect(Collectors.toList()).toString();

    if (thisTagsString.compareTo(thatTagsString) < 0) {
      return -1;
    } else if (thisTagsString.compareTo(thatTagsString) > 0) {
      return 1;
    }

    if (this.snippetID < that.snippetID) {
      return -1;
    } else if (this.snippetID > that.snippetID) {
      return 1;
    }

//    if (this.fileName.compareTo(that.fileName) < 0) {
//      return -1;
//    } else if (this.fileName.compareTo(that.fileName) > 0) {
//      return 1;
//    }
//
//    if (this.kbSize < that.kbSize) {
//      return -1;
//    } else if (this.kbSize > that.kbSize) {
//      return 1;
//    }
//
//    if (this.startTime < that.startTime) {
//      return -1;
//    } else if (this.startTime > that.startTime) {
//      return 1;
//    }
//
//    if (this.creationDate.compareTo(that.creationDate) < 0) {
//      return -1;
//    } else if (this.creationDate.compareTo(that.creationDate) > 0) {
//      return 1;
//    }
    return 0;
  }
}
