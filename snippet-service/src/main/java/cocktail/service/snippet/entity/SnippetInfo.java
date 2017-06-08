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

package cocktail.service.snippet.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Snippet info describes a snippet, a short sound that can either be a part of a longer sound file
 * or a complete soundfile. A snippet is supposed to be unique. Snippets are cathegorized
 * by 0 or more tags.
 */
@Data @AllArgsConstructor @NoArgsConstructor
@Entity("snippets")
public class SnippetInfo implements Comparable<SnippetInfo>, Serializable {
  @Id
  private ObjectId snippetId;

  private int fileID;

  private List<String> tags;

  private int kbSize;
  private double startTime;
  private double lengthSec;

  private LocalDate creationDate;
  private LocalDate lastModified;

  @Reference(idOnly = true)
  private User user;

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


    // Todo implement sparse vector spaces clustering
    // Todo http://stackoverflow.com/questions/1539745/clustering-huge-vector-space
    String thisTagsString = this.tags.stream().sorted().collect(Collectors.toList()).toString();
    String thatTagsString = that.tags.stream().sorted().collect(Collectors.toList()).toString();

    if (thisTagsString.compareTo(thatTagsString) < 0) {
      return -1;
    } else if (thisTagsString.compareTo(thatTagsString) > 0) {
      return 1;
    }

//    if (this.snippetId < that.snippetId) {
//      return -1;
//    } else if (this.snippetId > that.snippetId) {
//      return 1;
//    }

    if (this.fileID < that.fileID) {
      return -1;
    } else if (this.fileID < that.fileID) {
      return 1;
    }

    if (this.kbSize < that.kbSize) {
      return -1;
    } else if (this.kbSize > that.kbSize) {
      return 1;
    }

    if (this.startTime < that.startTime) {
      return -1;
    } else if (this.startTime > that.startTime) {
      return 1;
    }

    if (this.creationDate.compareTo(that.creationDate) < 0) {
      return -1;
    } else if (this.creationDate.compareTo(that.creationDate) > 0) {
      return 1;
  }
    return 0;
  }

  @PostLoad
  public void avoidNullTags() {
    if(tags == null) {
      tags = new ArrayList<>();
    }
  }

  @PrePersist
  public void updateModifiedDate() {
    lastModified = LocalDate.now();
  }
}
