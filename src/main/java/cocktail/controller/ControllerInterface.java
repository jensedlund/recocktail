package cocktail.controller;

import java.util.List;
import java.util.Set;

import cocktail.snippet.SetOperation;
import cocktail.snippet.SnippetSet;

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


/**
 * ControllerInterface is a layer that delegates and pass on information to other parts of the program.
 * This interface functions as a spider in the web.
 */
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
   * Method executes operation ont the SnippetSets that is passed as argument.
   *
   * @param setA Set A of type SnippetSet
   * @param setB Set B of type SnippetSet
   * @param setOperation Enum constant for the specific op.
   * @return snippetSet object of class SnippetSet.
   */
  SnippetSet executeSetOperation(SnippetSet setA, SnippetSet setB, SetOperation setOperation);

 /**
   * Method executes operation ont the SnippetSets that is passed as argument.
   *
   * @param setA Name of Set A.
   * @param setB Name of Set B.
   * @param setOperation String name for the specific op.
   * @return snippetSet object of class SnippetSet.
   */
  SnippetSet executeSetOperation(String setA, String setB, String setOperation);

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
   * Method creates a zip file from a SnippetSet object. Returns a string that represent a
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
   * Method removes one snippetInfo from the SnippetSet passed as argument.
   * Returns a new snippetSet that not includes the unwanted snippetInfo.
   *
   * @param snippetID int
   * @param snippetSet
   * @return snippetSet new SnippetSet
   */
  SnippetSet removeSnippetFromSet(int snippetID, SnippetSet snippetSet);

  /**
   * Method removes one snippetInfo from the SnippetSet passed as argument.
   * Returns a new snippetSet that not includes the unwanted snippetInfo.
   *
   * @param setName
   */
  void removeSet(String setName);

  /**
   * Method reads one snippetInfo based on snippetID that is passed as argument and add the snippetInfo
   * to the collection of snippetInfos that is embedded in the snippetSet object. Returns a new snippetSet.
   * @param snippetID int
   * @param snippetSet object of class SnippetSet
   * @return snippetSet a new object
   */
  SnippetSet addSnippetToSet(int snippetID, SnippetSet snippetSet);

  /**
   * Returns a SnippetSet from storage. The id of the set is passed as argument.
   * @param setID String
   * @return SnippetSet
   */
  SnippetSet getStoredSet(String setID);

  /**
   * Renames a SnippetSet.
   *
   * @param oldSetName Original name.
   * @param newSetName New name.
   * @return A reference to the renamed set.
   */
  SnippetSet renameStoredSet(String oldSetName, String newSetName);

  /**
   * Returns the SnippetSet from SnippetStorage that is marked as currentSet.
   * @return SnippetSet
   */
  SnippetSet getCurrentSet();

  /**
   * Returns a collection of tag names. All tags in the database are included.
   * @return set of strings
   */
  Set<String> getCompleteSetOfTagNames();

  /**
   * Method deletes one zip file that is no longer in use.
   *
   * @param filePath String
   * @return boolean
   */
  boolean deleteUsedZip(String filePath);

  /**
   * Method returns one file and all the snippets that is associated whit that file as an object of class
   * SnippetSet. The snippetSet object includes a collection of SnippetInfo representing the marked snippet on the file.
   *
   * @param fileID int
   * @return snippetSet
   */
  SnippetSet getSingelSourceFileAndItsSnippets(int fileID);

  /**
   * Returns a list of all the stored sets.
   *
   * @return list of Strings
   */
  List<String> getAllSavedSetsName();

  /**
   * Method deletes saved sets, the sets are specified by the list of names that is passed as argument.
   *
   * @param savedSetNames list of strings
   * @return boolean
   */
  boolean deleteSavedSets(List<String> savedSetNames);

  /**
   *  Method deletes one snippet in the database. The method is used when the user name is equal to the admin user name
   *  and one of the tags are a protected tag. Protected tag starts whit a (dot) [.].
   *
   * @param snippetID int
   * @return boolean
   */
  boolean deleteSnippetAsAdmin(int snippetID);

  /**
   * Method deletes all tags in the database that are not connected to any snippets.
   *
   */
  void deleteAllTagsNotInUse();
}
