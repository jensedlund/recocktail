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

package cocktail.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import cocktail.service.snippet.entity.SnippetSet;

/**
 * A SnippetStorage object handles generated snippet sets by name so that there is a history of
 * created sets that all can be retrieved.
 */
public class SnippetStorageImpl implements SnippetStorage {

//  private LinkedList<SnippetSet> workingSetsByUser;
  private HashMap<String, LinkedList<SnippetSet>> workingSetsByUser;
  private Map<String, File> savedSets;
  private static SnippetSet latestSet;
  private static SnippetStorageImpl instance;
  private static Set<String> setNames;
  private static Set<String> userNames;
  private static final String defaultUser = "Unknown";

  static {
    setNames = new HashSet<>();
    userNames = new HashSet<>();
  }

  public SnippetStorageImpl() {
    LinkedList<SnippetSet> defaultUserSets = new LinkedList<>();
    workingSetsByUser = new HashMap<>();
    userNames.add(defaultUser);
    workingSetsByUser.put(defaultUser,defaultUserSets);
    savedSets = new HashMap<>();
  }

  public static SnippetStorageImpl getInstance() {
    if (instance == null) {
      instance = new SnippetStorageImpl();
      return instance;
    } else {
      return instance;
    }
  }

  private void userCheck(String userName) {
    if (!userNames.contains(userName)) {
      userNames.add(userName);
      LinkedList<SnippetSet> newUserSets = new LinkedList<>();
      workingSetsByUser.put(userName, newUserSets);
    }
  }

  @Override
  public SnippetSet getSet(String setName) {
    return getSet(defaultUser, setName);
  }

  @Override
  public SnippetSet getSet(String userName, String setName) {
    LinkedList<SnippetSet> userSetList = workingSetsByUser.get(userName);
    for (SnippetSet snippetSet : userSetList) {
      if (snippetSet.getSetName().equals(setName)) {
        return snippetSet;
      }
    }
    return null;
  }

  @Override
  public SnippetSet getLatestSet() {
    return latestSet;
  }

  @Override
  public boolean addSet(SnippetSet snippetSet) {
    return addSet(defaultUser, snippetSet);
  }

  @Override
  public boolean addSet(String userName, SnippetSet snippetSet) {
    boolean returnBool = false;
    latestSet = snippetSet;
    userCheck(userName);
    LinkedList<SnippetSet> userSetList = workingSetsByUser.get(userName);
    userSetList.addFirst(snippetSet);
    if (setNames.contains(snippetSet.getSetName())) {
      return returnBool;
    } else {
      setNames.add(snippetSet.getSetName());
      returnBool = true;
      return returnBool;
    }
  }

  /**
   *
   * @param snippetSetName Name of set to remove.
   * @return True if a set was removed. Otherwise false.
   */
  @Override
  public boolean removeSet(String snippetSetName) {
    return removeSet(defaultUser, snippetSetName);
  }

  @Override
  public boolean removeSet(String userName, String snippetSetName) {
    userCheck(userName);
    if(setNames.contains(snippetSetName)) {
      setNames.remove(snippetSetName);
      LinkedList<SnippetSet> userSetList = workingSetsByUser.get(userName);
      SnippetSet snippetSet = getSet(userName, snippetSetName);
      userSetList.remove(snippetSet);
      return true;
    } else {
      return false;
    }
  }

  /**
   * Rename a set without changing it.
   *
   * @param oldSetName
   * @param newSetName
   * @return
   */
  @Override
  public SnippetSet renameSet(String oldSetName, String newSetName) {
    return renameSet(defaultUser,oldSetName,newSetName);
  }

  @Override
  public SnippetSet renameSet(String userName, String oldSetName, String newSetName) {
    if(setNames.contains(oldSetName) && !setNames.contains(newSetName)) {
      SnippetSet snippetSet = getSet(userName, oldSetName);
      snippetSet.setSetName(newSetName);
      setNames.remove(oldSetName);
      setNames.add(newSetName);
      return snippetSet;
    } else {
      return null;
    }
  }

  @Override
  public Set<String> getAllSetNames() {
    return setNames;
  }

  @Override
  public void deleteSavedSets(List<String> savedSetNames) {
    deleteSavedSets(defaultUser, savedSetNames);
  }

  @Override
  public void deleteSavedSets(String userName, List<String> savedSetNames) {
    LinkedList<SnippetSet> userSetList = workingSetsByUser.get(userName);
    userSetList.removeAll(savedSetNames);
  }

  @Override
  public List<String> getWorkingSetNames() {
    return getWorkingSetNames(defaultUser);
  }

  @Override
  public List<String> getWorkingSetNames(String userName) {
    LinkedList<SnippetSet> userSetList = workingSetsByUser.get(userName);
    return userSetList.stream().map(s -> s.getSetName()).collect(Collectors.toList());
  }

  @Override
  public String[] storedContextIds() {
    return (String[]) savedSets.keySet().toArray();
  }



  @Override
  public boolean restoreContext(String id) {
    String dirName = "./src/main/resources/sets/";
    File storageArea = new File(dirName);
    if (!storageArea.exists()) {
      storageArea.mkdir();
    }
    try {
      System.out.println("Will load your working set " + id + " from file...");
      ObjectInputStream
          oi =
          new ObjectInputStream(new FileInputStream(dirName + "/" + id + ".sav"));
      Object snippetSetsIn = oi.readObject();
//      workingSetsByUser = (LinkedList<SnippetSet>) snippetSetsIn;
      workingSetsByUser = (HashMap<String, LinkedList<SnippetSet>>) snippetSetsIn;
      oi.close();
    } catch (Exception exc) {
      exc.printStackTrace();
      return false;
    }
    return true;
  }

  @Override
  public boolean storeContext(String id) {
    String dirName = "./src/main/resources/sets/";
    File storageArea = new File(dirName);
    if (!storageArea.exists()) {
      storageArea.mkdir();
    }

    try {
      ObjectOutputStream
          oos =
          new ObjectOutputStream(new FileOutputStream(dirName + "/" + id + ".sav"));
      try {
        oos.writeObject(workingSetsByUser);
        System.out.println("Saving your working set.");
      } catch (IOException e) {
        e.printStackTrace();
        return false;
      }
      oos.close();
    } catch (IOException ioe) {
      ioe.printStackTrace();
      return false;
    }
    //    File outPutFile = new File(dirName + "/" + id + ".sav");

//    XmlStreamer<LinkedList<SnippetSet>> xmlStreamer = new XmlStreamer<>();
//    xmlStreamer.toStream(workingSetsByUser.getClass(), workingSetsByUser, outPutFile);
    return true;
  }

  @Override
  public void setLogNote(String log, SnippetSet snippetSet) {
    snippetSet.getOperationLog().add(log);
  }
}
