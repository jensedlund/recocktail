package cocktail.controller;

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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cocktail.archive_handler.ArchiveHandler;
import cocktail.db_access.DbAdapter;
import cocktail.db_access.DbAdapterImpl;
import cocktail.snippet.SetOperation;
import cocktail.snippet.SnippetSet;
import cocktail.storage.SnippetStorageImpl;

/**
 * ControllerInterface is a layer that delegates and pass on information to other parts of the program.
 * This interface functions as a spider in the web.
 */
public class Controller implements ControllerInterface {

//  private SnippetStorage snippetStorage;
  private DbAdapter dbAdapter;
  private ArchiveHandler archiveHandler;
  private static Controller controller;


  private Controller() {
    dbAdapter = new DbAdapterImpl();
    archiveHandler = new ArchiveHandler();
  }

  public static Controller getInstance() {
    if (controller == null) {
      controller = new Controller();
      return controller;
    } else {
      return controller;
    }
  }

  @Override
  public void storeSet(SnippetSet snippetSet) {
    SnippetStorageImpl.getInstance().addSet(snippetSet);
  }

  @Override
  public SnippetSet writeEditSnippet(String filePath) {
    return archiveHandler.unzip(filePath);
  }

  @Override
  public SnippetSet executeSetOperation(SnippetSet setA, SnippetSet setB, SetOperation setOperation) {
    return setA.setOperation(setB, setOperation);
  }

  @Override
  public SnippetSet executeSetOperation(String setAName, String setBName, String setOperation) {
    SnippetSet setA = SnippetStorageImpl.getInstance().getSet(setAName);
    SnippetSet setB = SnippetStorageImpl.getInstance().getSet(setBName);
    SnippetSet result;
    try {
      // Protect aginst setOperation string not matching a operation.
      SetOperation operation = SetOperation.valueOf(setOperation.toUpperCase());

      // Get new set, add to Storage.
      result = setA.setOperation(setB, operation);
      SnippetStorageImpl.getInstance().addSet(result);
    } catch (IllegalArgumentException e) {
      // If set operation dones not exist, just return setA.
      result = setA;
    }
    return result;
  }

  @Override
  public SnippetSet searchSnippetSet(String[] tagNames, boolean exclusive) {
   SnippetSet set = dbAdapter.search(tagNames, exclusive);
       storeSet(set);
    return set;
  }

  @Override
  public SnippetSet searchSnippetSet(List<String> tagNames, double lengthMaxFilter, boolean exclusive) {
    SnippetSet snippetSet= dbAdapter.search(tagNames, lengthMaxFilter, exclusive);
    storeSet(snippetSet);
    return snippetSet;
  }

  @Override
  public List<String> getAssociatedTags(String tagName) {
    return dbAdapter.getAssociatedTags(tagName);
  }

  @Override
  public String getZippedFiles(SnippetSet snippetSet) {
    return archiveHandler.zip(snippetSet);
  }

  @Override
  public void updateTagName(String newTagName, String oldTagName) {
    dbAdapter.updateTagName(newTagName, oldTagName);
  }

  @Override
  public void updateUserName(String newUserName, String oldUserName) {
    dbAdapter.updateUserName(newUserName, oldUserName);

  }

  @Override
  public SnippetSet removeSnippetFromSet(int snippetID, SnippetSet snippetSet) {
    snippetSet.removeSnippet(snippetID);
    String log = "Snippet " + snippetID + "was removed from set";
    SnippetStorageImpl.getInstance().setLogNote(log, snippetSet);
    return snippetSet;
  }

  @Override
  public SnippetSet addSnippetToSet(int snippetID, SnippetSet snippetSet) {
    snippetSet.addSnippet(dbAdapter.readSnippetInfo(snippetID));
    String log = "Snippet " + snippetID + " added to the snippet set";
    SnippetStorageImpl.getInstance().setLogNote(log, snippetSet);
    return snippetSet;
  }

  @Override
  public SnippetSet getStoredSet(String setName) {
    return SnippetStorageImpl.getInstance().getSet(setName);
  }

  @Override
  public SnippetSet getCurrentSet() {
//    return snippetStorage.getLatestSet();
    return SnippetStorageImpl.getInstance().getLatestSet();
  }

  @Override
  public Set<String> getCompleteSetOfTagNames() {
    Set<String> tagSet = new HashSet<>();
    tagSet.addAll(dbAdapter.getAllTags());
    return tagSet;
  }

  @Override
  public boolean deleteUsedZip(String filePath) {
    return archiveHandler.deleteUsedZip(filePath);
  }

  @Override
  public SnippetSet getSingelSourceFileAndItsSnippets(int fileID) {
    return archiveHandler.getSingleFile(fileID);
  }

  @Override
  public List<String> getAllSavedSetsName() {
    List<String> returnList = new ArrayList<>();
    Set<String> set =  SnippetStorageImpl.getInstance().getAllSetNames();
    returnList.addAll(set);
   return  returnList;
  }

  @Override
  public boolean deleteSavedSets(List<String> savedSetNames) {
    boolean returnBool = false;
    int test = SnippetStorageImpl.getInstance().getAllSetNames().size();
    SnippetStorageImpl.getInstance().deleteSavedSets(savedSetNames);
    if(test >SnippetStorageImpl.getInstance().getAllSetNames().size() ){
      returnBool = true;
    }
    return returnBool;
  }

  @Override
  public boolean deleteSnippetAsAdmin(int snippetID) {
   return dbAdapter.deleteSnippetAsAdmin(snippetID);
  }

  @Override
  public void deleteAllTagsNotInUse() {
    dbAdapter.removeAllUnusedTags();
  }

}
