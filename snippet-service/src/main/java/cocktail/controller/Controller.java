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

import java.util.List;
import java.util.Set;

import cocktail.service.snippet.entity.SetOperation;
import cocktail.service.snippet.entity.SnippetSet;

/**
 * ControllerInterface is a layer that delegates and pass on information to other parts of the program.
 * This interface functions as a spider in the web.
 */
public class Controller implements ControllerInterface {

  @Override
  public void cacheSnippetStorage(String cacheId) {

  }

  @Override
  public void retrieveCachedSnippetStorage(String cacheId) {

  }

  @Override
  public void storeSet(SnippetSet snippetSet) {

  }

  @Override
  public SnippetSet writeEditSnippet(String filePath) {
    return null;
  }

  @Override
  public SnippetSet executeSetOperation(SnippetSet setA, SnippetSet setB, SetOperation setOperation) {
    return null;
  }

  @Override
  public SnippetSet executeSetOperation(String setA, String setB, String setOperation) {
    return null;
  }

  @Override
  public SnippetSet searchSnippetSet(String[] tagNames, boolean exclusive) {
    return null;
  }

  @Override
  public SnippetSet searchSnippetSet(List<String> tagNames, double lengthMaxFilter, boolean exclusive) {
    return null;
  }

  @Override
  public List<String> getAssociatedTags(String tagName) {
    return null;
  }

  @Override
  public String getZippedFiles(SnippetSet snippetSet) {
    return null;
  }

  @Override
  public void updateTagName(String newTagName, String oldTagName) {

  }

  @Override
  public void updateUserName(String newUserName, String oldUserName) {

  }

  @Override
  public SnippetSet removeSnippetFromSet(int snippetID, SnippetSet snippetSet) {
    return null;
  }

  @Override
  public void removeSet(String setName) {

  }

  @Override
  public SnippetSet addSnippetToSet(int snippetID, SnippetSet snippetSet) {
    return null;
  }

  @Override
  public SnippetSet getStoredSet(String setID) {
    return null;
  }

  @Override
  public SnippetSet renameStoredSet(String oldSetName, String newSetName) {
    return null;
  }

  @Override
  public SnippetSet getCurrentSet() {
    return null;
  }

  @Override
  public Set<String> getCompleteSetOfTagNames() {
    return null;
  }

  @Override
  public boolean deleteUsedZip(String filePath) {
    return false;
  }

  @Override
  public SnippetSet getSingleSourceFileAndItsSnippets(int fileID) {
    return null;
  }

  @Override
  public List<String> getAllSavedSetsName() {
    return null;
  }

  @Override
  public List<String> getAllUserNames() {
    return null;
  }

  @Override
  public boolean deleteSavedSets(List<String> savedSetNames) {
    return false;
  }

  @Override
  public boolean deleteSnippetAsAdmin(int snippetID) {
    return false;
  }

  @Override
  public void deleteAllTagsNotInUse() {

  }

  @Override
  public SnippetSet getAllSnippetsFromUserName(String userNam) {
    return null;
  }
}
