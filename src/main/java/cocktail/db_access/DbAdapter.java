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

import cocktail.snippet.FileInfo;
import cocktail.snippet.SnippetInfo;
import cocktail.snippet.SnippetSet;

import java.util.List;

/**
 * Class that provide the programm with information from database and uppload information to the database.
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
   * @param snippetID
   * @return byte[]
   */
  byte[] readSnippet(int snippetID);

  /**
   * Returns an object with all the information about one snippet. The object dose not include the file.
   *
   * @param snippetID
   * @return
   */
  SnippetInfo readSnippetInfo(int snippetID);

  /**
   * Returns the complete number of files that is stored in the database
   *
   * @return number of files as Integer
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
   * Returns the number of times one specific tag is occurring in the database.
   * @param tag a tagname as a String
   * @return int number of occurrence
   */
  Integer getOccurrenceOfTag(String tag);

  /**
   * Returns a list of tagas that is usually connected to the tag that is passed as an argument.
   * There could bee duplicates in the list. The number of times one tag is occurring in the list
   * tells how often the tag is associated together with the tag in question.
   *
   * @param tag tagName as a String
   * @return
   */
  List<String> getAssociatedTags(String tag);

  SnippetSet search(String[] tagArray, boolean exclusive);

  SnippetSet search(List<String> tagArray, double lengthMaxFilter, boolean exclusiv);

  boolean isSnippetPartOfLongerFile(int snippetID);

  void updateTagName(String newTagName, String oldTagName);

  void updateUserName(String newUserName, String oldUserName);

  SnippetSet getAllSnippetFromFile(int fileID);

  SnippetSet createSnippetSetFromIds(List<Integer> snippetIDs);

  int writeSnippet(SnippetInfo snippetInfo,int fileID);

  List<SnippetInfo> createSnippetListFromSnippetID(List<Integer> snippetIDList);

  int getFileIdFromSnippetId(int snippetID);

  String getFileNameFromSnippetId(int snippetID);

  boolean removeUnusedTag(String tagName);

  boolean removeAllUnusedTags();

  int writeSnippetAsAdmin(SnippetInfo snippetInfo, FileInfo fileInfo);

  boolean editSnippetAsAdmin(SnippetInfo snippetInfo,FileInfo fileInfo, int snippetID);

  boolean deleteSnippetAsAdmin(int SnippetID);

}



