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

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.JAXBException;

import cocktail.stream_io.StreamingService;


/**
 * SnippetSet contains a sorted set of snippetInfo and related stats.
 * A specific SnippetSet is defined by its setName field.
 * The getters for derived fields also work as setters in that they also update the
 * field based on the current state of the collection of SnippetInfo.
 */
public class SnippetSet {

  private SortedSet<SnippetInfo> snippetCollection;
  private List<String> operationLog;
  private String setName;
  private double maxLenSec;
  private double minLenSec;
  private double avgLenSec;
  private int numSnippets;
  private LocalDate creationDate;
  private Set<String> tagsInSet;


  /**
   * Create new empty SnippetSet object.
   */
  public SnippetSet() {
    snippetCollection = new TreeSet<>();
    operationLog = new ArrayList<>();
    tagsInSet = new TreeSet<>();
    creationDate = LocalDate.now();
    setName = createUniqueSetName();
  }


  /**
   * Create new SnippetSet object populated with provided collection of SnippetInfo.
   * @param snippetCollection A sorted set collection of SnippetInfo objects.
   */
  public SnippetSet(SortedSet<SnippetInfo> snippetCollection) {
    this.snippetCollection = snippetCollection;
    operationLog = new ArrayList<>();
    tagsInSet = new TreeSet<>();
    creationDate = LocalDate.now();
    setName = createUniqueSetName();
    updateDerivedFields();
  }


  /**
   * Updates the values of the fields derived from SnippetInfo collection
   * by calling the getters for those fields.
   */
  public void updateDerivedFields() {
    // Execute getters to set the fields.
    getAvgLenSec();
    getMaxLenSec();
    getMinLenSec();
    getNumSnippets();
    getTagsInSet();
  }

  /**
   * Create a semi-unique name created from current date and time.
   * @return The new name string.
   */
  public String createUniqueSetName(){
    String str = "" + creationDate.toString() + " " + LocalTime.now();
    str = str.replace(':','-');
    str = str.replace(' ', '-');
    str = str.replace('.','-');
    return str;
  }

  /**
   * Removes a Snippet from the collection of SnippetInfo.
   * @param snippetId the numeric, unique, id of the snippet to remove.
   */
  public void removeSnippet(int snippetId) {
    SnippetInfo storedTemp = null;
    Iterator itr = snippetCollection.iterator();
    while (itr.hasNext()) {
      SnippetInfo temp = (SnippetInfo) itr.next();
      if (temp.getSnippetID() == snippetId) {
        storedTemp = temp;
      }
    }
    snippetCollection.remove(storedTemp);
    updateDerivedFields();
  }

  /**
   * Add a new SnippetInfo obejct to the snippet collection.
   * @param snippetInfo Snippet object to add.
   */
  public void addSnippet(SnippetInfo snippetInfo) {
    snippetCollection.add(snippetInfo);
    updateDerivedFields();
  }

  /**
   * Stream out this object as an xml file using the provided streaming service.
   * @param streamingService the Service/object to use for xml generation.
   * @param file Filehandle for the target file.
   * @return Return true if sucessful.
   */
  public boolean toStream(StreamingService streamingService, File file) {
    boolean returnBool = false;
    try {
      streamingService.toStream(SnippetSet.class, this, file);
      returnBool = true;
      return returnBool;
    } catch (JAXBException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    return returnBool;
  }


  public SortedSet<SnippetInfo> getSnippetCollection() {
    return snippetCollection;
  }

  public List<String> getOperationLog() {
    return operationLog;
  }

  public void setOperationLog(List<String> operationLog) {
    this.operationLog = operationLog;
  }

  public double getMaxLenSec() {
    if (snippetCollection.size() > 0) {
      maxLenSec = snippetCollection.last().getLengthSec();
      return maxLenSec;
    } else {
      return 0.0;
    }

  }


  public double getMinLenSec() {
    if(snippetCollection.size()>0) {
      minLenSec = snippetCollection.first().getLengthSec();
      return minLenSec;
    } else {
      return 0.0;
    }
  }

  public double getAvgLenSec() {

    Iterator itr = snippetCollection.iterator();
    double totalLenSec = 0;
    while (itr.hasNext()) {
      SnippetInfo temp = (SnippetInfo) itr.next();
      totalLenSec += temp.getLengthSec();
    }
    avgLenSec = totalLenSec / getNumSnippets();

    return avgLenSec;
  }

  public int getNumSnippets() {
    numSnippets = snippetCollection.size();
    return numSnippets;
  }

  public Set<String> getTagsInSet() {
    Iterator itr = snippetCollection.iterator();
    while (itr.hasNext()) {
      SnippetInfo temp = (SnippetInfo) itr.next();
      tagsInSet.addAll(temp.getTagNames());
    }
    return tagsInSet;
  }

  public void setSetName(String setName) {
    this.setName = setName;
  }

  public String getSetName() {
    return setName;
  }

  // todo implement
  public SnippetSet setOperation(SnippetSet otherSet, SetOperation operation) {
    operation.calculate(snippetCollection, otherSet.getSnippetCollection());
    //todo new snippet set
    return null;
  }

  public void setSnippetCollection(SortedSet<SnippetInfo> snippetCollection) {
    this.snippetCollection = snippetCollection;
  }

  public void setMaxLenSec(double maxLenSec) {
    this.maxLenSec = maxLenSec;
  }

  public void setMinLenSec(double minLenSec) {
    this.minLenSec = minLenSec;
  }

  public void setAvgLenSec(double avgLenSec) {
    this.avgLenSec = avgLenSec;
  }

  public void setNumSnippets(int numSnippets) {
    this.numSnippets = numSnippets;
  }

  public void setCreationDate(LocalDate creationDate) {
    this.creationDate = creationDate;
  }

  public void setTagsInSet(Set<String> tagsInSet) {
    this.tagsInSet = tagsInSet;
  }

  @Override
  public String toString() {
    return "SnippetSet{" +
           "setName=" + setName +
           ", operationLog=" + operationLog +
           ", maxLenSec=" + getMaxLenSec() +
           ", minLenSec=" + getMinLenSec() +
           ", avgLenSec=" + getAvgLenSec() +
           ", numSnippets=" + getNumSnippets() +
           ", tagsInSet=" + getTagsInSet() +
           "snippetCollection=" + snippetCollection +
           '}';
  }
}
