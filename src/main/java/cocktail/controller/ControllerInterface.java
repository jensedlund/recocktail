package cocktail.controller;

import cocktail.snippet.SetOperation;
import cocktail.snippet.SnippetSet;

import java.util.List;
import java.util.Set;

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

public interface ControllerInterface {
  /**
   * Method stores one SnippetSet object in SnippetStorage
   *
   * @param snippetSet object including collection of snippetInfo and information about the set
   */
  void storeSet(SnippetSet snippetSet);

  /**
   * Method both writes and edits snippets. The String passed as argument represent a path to a zip file
   * that includes all information necessary for the task. The snippets are then returned as a collection that is
   * embedded in a SnippetSet object.
   *
   * @param filePath
   * @return
   */
  SnippetSet writeEditSnippet(String filePath);

  /**
   * Method executes operation ont the SnippetSet that is passed as argument.
   *
   * @param snippetSet object of class SnippetSet
   * @param setOperation Union, intersect or complemet
   * @return snippetSet object of class SnippetSet
   */
  SnippetSet executeSetOperation(SnippetSet snippetSet, SetOperation setOperation);

  /**
   * Overloaded method that search for snippet based on the array of tagNames that is passed as argument.
   * The argument "exclusive" decides if the user wants snippets that is marked whit all the tags in the array or if not.
   * Returns an object of class SnippetSet that includes a collection of snippets.
   *
   * @param tagNames list of Strings
   * @param exclusive boolean
   * @return snippetSet
   */
  SnippetSet searchSnippetSet(String[] tagNames, boolean exclusive);

  /**
   * Overloaded method that search for snippets based on the list of tagNames that is passed as argument.
   * The argument "exclusive" decides if the user wants snippets that is marked whit all the tags in the array or if not.
   * The argument "lengthMaxFileter" filters out snippets that is longer than tha this variable.
   * Returns an object of class SnippetSet that includes a collection of snippets.
   *
   * @param tagNames list of strings
   * @param lengthMaxFilter double
   * @param exclusive boolean
   * @return snippetSet
   */
  SnippetSet searchSnippetSet(List<String> tagNames, double lengthMaxFilter, boolean exclusive);

  /**
   * Method returns a list of tag names that is associated whit the tag name passed as argument. The list
   * could contain duplicates and the number of times one tag is occurring tells the degree of association.
   *
   * @param tagName String
   * @return listOfTagNames list of associated tag names
   */
  List<String> getAssociatedTags(String tagName);

  /**
   *Method creates a zip file from a SnippetSet object. Returns a string that represent a
   * path to a file.
   *
   * @param snippetSet
   * @return filePath String
   */
  String getZippedFiles(SnippetSet snippetSet);

  /**
   * Updates one tag name. The new and old tag name are passed as arguments.
   *
   * @param newTagName String
   * @param oldTagName String
   */
  void updateTagName(String newTagName, String oldTagName);

  /**
   * Updates one user name. The new and old user name are passed as arguments.
   * @param newUserName string
   * @param oldUserName string
   */
  void updateUserName(String newUserName, String oldUserName);

  /**
   *
   * @param snippetID
   * @param snippetSet
   * @return
   */
  SnippetSet removeSnippetFromSet(int snippetID, SnippetSet snippetSet);

  SnippetSet addSnippetToSet(int snippetID, SnippetSet snippetSet);

  SnippetSet getStoredSet(String setID);

  SnippetSet getCurrentSet();

  Set<String> getCompleteSetOfTagNames();

  boolean deleteUsedZip(String setName);

  SnippetSet getSingelSourceFileAndItsSnippets(int fileID);

  List<String> getAllSavedSetsName();

  boolean deleteSavedSets(List<String> savedSetNames);

  boolean deleteSnippetAsAdmin(int snippetID);

  void deleteAllTagsNotInUse();


}
