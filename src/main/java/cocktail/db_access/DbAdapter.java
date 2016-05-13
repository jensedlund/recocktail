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

public interface DbAdapter {

  int writeSnippet(FileInfo fileInfo, SnippetInfo info);

  boolean editSnippet(SnippetInfo snippetInfo, FileInfo fileInfo, int snippetID);

  boolean deleteSnippet(int snippetID);

  byte[] readSnippet(int snippetID);

  SnippetInfo readSnippetInfo(int snippetID);

  Integer getNumberOfFiles();

  Integer getTotalFileSizeKb();

  Integer getMinFileSizeKb();

  Integer getMaxFileSizeKb();


  Double getMinFileLenSec();

  Double getMaxFileLenSec();

  Integer getNumSnippets();

  Double getMinSnippetLenSec();

  Double getMaxSnippetLenSec();

  List<String> getAllTags();

  Integer getOccuranceOfTag(String tag);

  List<String> getComplementaryTags(String tag);

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



