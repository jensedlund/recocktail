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
 * @since 2016-03-25
 */

package cocktail.db_access;

import java.util.List;

import cocktail.snippet.FileInfo;
import cocktail.snippet.SnippetInfo;
import cocktail.snippet.SnippetSet;
import java.util.List;
import java.util.Map;

/**
 * Class that provide the program with information from database and uploads information to the database.
 */
public interface DbAdapter {
  /**
   * Insert information and a file into the database.
   *
   * @param fileInfo object with a file and information about the file
   * @param snippetInfo object with information about the snippet
   * @return The id of the snippet that is generated in the database
   */
  int writeSnippet(FileInfo fileInfo, SnippetInfo snippetInfo);

  /**
   *Updates the information in by executing a delete-insert operation.
   *
   * @param snippetInfo object with information about the snippet
   * @param fileInfo object with a file and information about the file
   * @param snippetID the ID of the snippet will stay the same after editing
   * @return boolean
   */
  boolean editSnippet(SnippetInfo snippetInfo, FileInfo fileInfo, int snippetID);

  /**
   * Deletes one snippet from database.
   *
   * @param snippetID
   * @return boolean
   */
  boolean deleteSnippet(int snippetID);

  /**
   * Returns the file as a byte array. Method don't read all the information, just the file.
   *
   * @param snippetID int
   * @return byte[]
   */
  byte[] readSnippet(int snippetID);

  /**
   * Returns an object with all the information about one snippet. The object dose not include the file.
   *
   * @param snippetID
   * @return snippetInfo
   */
  SnippetInfo readSnippetInfo(int snippetID);

  /**
   * Returns the complete number of files that is stored in the database
   *
   * @return number of files as an int
   */
  Integer getNumberOfFiles();

  /**
   *Returns the total kb size of the files stored in the database.
   *
   * @return int
   */
  Integer getTotalFileSizeKb();

  /**
   * Returns the size of the smallest file in the database.
   * @return int
   */
  Integer getMinFileSizeKb();

  /**
   * Returns the kb size of the largest file stored in the database.
   * @return int
   */
  Integer getMaxFileSizeKb();

  /**
   * Returns the length in second of the shortest file in the database.
   * @return double
   */
  Double getMinFileLenSec();

  /**
   * Returns the length in second of the longest file in the database.
   * @return double
   */
  Double getMaxFileLenSec();

  /**
   * Returns the total amout of snippets stored in the database.
   * @return double
   */
  Integer getNumSnippets();

  /**
   * Returns the length in second of the shortest snippet in the database.
   *
   * @return double
   */
  Double getMinSnippetLenSec();

  /**
   * Returns the length in second of the longest snippet in the database.
   * @return double
   */
  Double getMaxSnippetLenSec();

  /**
   * Returns a list of all the tags stored in the database.
   * @return List<String> list of tags
   */
  List<String> getAllTags();

  /**
   * Returns a list of all users in the database
   * @return List<String> list of users
   */
  List<String> getAllUsers();

  /**
   * Returns the number of times one specific tag is occurring in the database.
   * @param tag a tagname as a String
   * @return int number of occurrence
   */
  Integer getOccurrenceOfTag(String tag);

  /**
   * Returns a list of tags that is usually connected to the tag that is passed as an argument.
   * There could bee duplicates in the list. The number of times one tag is occurring in the list
   * tells how often the tag is associated together with the tag in question.
   *
   * @param tag tagName as a String
   * @return
   */
  List<String> getAssociatedTags(String tag);

  /**
   * Method search for a set of snippets based on the array of tags passed as arguments. Exclusive search is used when the user
   * only wants snippets that is marked with all of the tags in the passed array.
   * Returns an object of class SnippetSet that includes a collection of the snippets.
   * @param tagArray array of tag names
   * @param exclusive boolean
   * @return snippetSet an object that includes a collection of snippets.
   */
  SnippetSet search(String[] tagArray, boolean exclusive);

  /**
   * Overloaded method search for a set of snippets based on the list of tags passed as arguments. Exclusive search is used when the user
   * only wants snippets that is marked with all of the tags in the passed array. LengthMaxFilter is used to filter out snippets that is
   * longer than the variable.
   * Returns an object of class SnippetSet that includes a collection of the snippets.
   *
   * @param tagArray list of tag names
   * @param lengthMaxFilter double
   * @param exclusiv boolean
   * @return snippetSet an object that includes a collection of snippets.
   */
  SnippetSet search(List<String> tagArray, double lengthMaxFilter, boolean exclusiv);

  /**
   * Checks if the snippet in question is cut out from a larger file or if tha file is the same length as the hole snippet.
   * Returns a boolean.
   * @param snippetID int
   * @return boolean
   */
  boolean isSnippetPartOfLongerFile(int snippetID);

  /**
   * Updates a tag name in the database. Takes the new wanted name and the old name as argument.
   *
   * @param newTagName String
   * @param oldTagName String
   */
  void updateTagName(String newTagName, String oldTagName);


  /**
   * Updates one user name in the database. Takes the new wanted user name and the old name as argument.
   * @param newUserName
   * @param oldUserName
   */
  void updateUserName(String newUserName, String oldUserName);

  /**
   * Collect all the snippets that is cut out from file, specified by the argument. Returns an object of
   * class SnippetSet that inculdes a collection of snippets.
   * @param fileID int
   * @return
   */
  SnippetSet getAllSnippetFromFile(int fileID);

  /**
   * Reads snippets that matches the snippet id:s that is passed as arguments and returns
   * an object of class SnippetSet.
   * @param snippetIDs list of int
   * @return snippetSet object that includes collection of snippets
   */
  SnippetSet createSnippetSetFromIds(List<Integer> snippetIDs);

  /**
   * Method inserts one snippet into the database based on the two objects that is passed as argument.
   * Returns the snippetID that is generated in the database.
   * @param snippetInfo object with information about tags, length, size
   * @param fileID object including the file and information about the file.
   * @return snippetID int
   */
  int writeSnippet(SnippetInfo snippetInfo,int fileID);

  /**
   * Reads the snippets specified by the list of snippet id:s that is passed ass arguments. Returns a
   * list of SnippetInfo objects.
   *
   * @param snippetIDList list of int
   * @return list of SnippetInfo objects
   */
  List<SnippetInfo> createSnippetListFromSnippetID(List<Integer> snippetIDList);

  /**
   * Returns the id of the file that is related to the snippetID that is passed as argument.
   * @param snippetID int
   * @return fileID int
   */
  int getFileIdFromSnippetId(int snippetID);

  /**
   * Returns the name of the file that is related to the snippetID that is passed as argumen.
   * @param snippetID int
   * @return fileName String
   */
  String getFileNameFromSnippetId(int snippetID);

  /**
   * Method deletes one stored tag from database if the tag is not associated to any snippets.
   * @param tagName
   * @return
   */
  boolean removeUnusedTag(String tagName);

  /**
   * Method search for tags that is associated with any snippets and deletes them.
   * @return boolean true if tags was deleted
   */
  boolean removeAllUnusedTags();

  /**
   * Method inserts one snippet into the database based on the object and the id of the file that is passed as argument.
   * This method is used when the field userName in the object SnippetInfo is equal to the admin UserName and if any of the
   * tag names is a protected tag. Protected tag start with a [.]
   * Returns the snippetID that is generated in the database.
   *
   * @param snippetInfo object with information about tags, length, size
   * @param fileInfo
   * @return snippetID int
   */
  int writeSnippetAsAdmin(SnippetInfo snippetInfo, FileInfo fileInfo);

  /**
   * Method updates one snippet by executing a delete/insert operation. The new snippet is using the same snippetID as the
   * old snippet. This method is used when the field userName in the object SnippetInfo is equal to the admin UserName and if any of the
   * tag names is a protected tag. Protected tags starts with a (dot) [.]
   *
   * @param snippetInfo
   * @param fileInfo
   * @param snippetID
   * @return boolean true if the operation was executed whitout any problems
   */
  boolean editSnippetAsAdmin(SnippetInfo snippetInfo,FileInfo fileInfo, int snippetID);

  /**
   * Method deletes one snippet from database. his method is used when the field userName in the object SnippetInfo is equal to the admin UserName and if any of the
   * tag names is a protected tag. Protected tags starts with a (dot) [.]
   *
   * @param SnippetID int
   * @return boolean true if the snippet was deleted
   */
  boolean deleteSnippetAsAdmin(int SnippetID);

  /**
   * Updates one tag name and is used when the one of the two tag names are protected and the userName is equal to admin user name.
   * @param newTagName String
   * @param oldTagName String
   */
 boolean updateTagNameAsAdmin(String newTagName, String oldTagName, String userName);

  List<Integer> writeSnippets(Map<SnippetInfo, FileInfo> snippetInfoFileInfoMap);

  SnippetSet getAllSnippetsForUserName(String userName);
}



