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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DbAdapterImpl implements DbAdapter {

  public DbAdapterImpl() {
    Driver.connectToMySql();
  }

  @Override
  public int writeSnippet(FileInfo fileInfo, SnippetInfo info) {
    return Driver.writeSnippet(fileInfo, info);
  }


  @Override
  public boolean editSnippet(SnippetInfo snippetInfo, FileInfo fileInfo, int snippetID) {

    return Driver.deleteInsertSnippetInfo(fileInfo,snippetInfo, snippetID);
  }

  @Override
  public boolean deleteSnippet(int snippetID) {
    boolean returnBool = false;
    returnBool = Driver.deleteSnippet(snippetID);
    return returnBool;
  }

  @Override
  public byte[] readSnippet(int snippetID) {
    return Driver.readSnippet(snippetID);
  }

  @Override
  public SnippetInfo readSnippetInfo(int snippetID) {
    return Driver.readSnippetInf(snippetID);
  }


  @Override
  public Integer getNumberOfFiles() {
    return Driver.getTotlNumberOfFiles();

  }

  @Override
  public Integer getTotalFileSizeKb() {
    return Driver.getTotalFileSizeKb();
  }

  @Override
  public Integer getMinFileSizeKb() {
    return Driver.getMinFileSizeKb();
  }

  @Override
  public Integer getMaxFileSizeKb() {
    return Driver.getMaxFileSizeKb();
  }

  @Override
  public Double getMinFileLenSec() {
    return Driver.getMinFileLenSec();
  }

  @Override
  public Double getMaxFileLenSec() {
    return Driver.getMaxFileLenSec();

  }

  @Override
  public Integer getNumSnippets() {
  return Driver.getNumberOfSnippets();
  }

  @Override
  public Double getMinSnippetLenSec() {
    return Driver.getMinSnippetLenSec();
  }

  @Override
  public Double getMaxSnippetLenSec() {
    return Driver.getMaxSnippetLenSec();
  }

  @Override
  public List<String> getAllTags() {
    return Driver.getAllTagNames();
  }

  @Override
  public Integer getOccuranceOfTag(String tag) {
    return Driver.getOccuranceOfTag(tag);
  }

  @Override
  public ArrayList<String> getComplementaryTags(String tag) {
    return Driver.getComplementaryTags(tag);
  }

  @Override
  public SnippetSet search(String[] tagArray, boolean exclusive) {
    List<String> tagList = Arrays.stream(tagArray).collect(Collectors.toList());

    return search(tagList,3600,exclusive);
  }

  @Override
  public SnippetSet search(List<String> tagArray, double lengthMaxFilter, boolean exclusiv) {
    SnippetSet snippetSet = new SnippetSet();
    List<Integer> snippetIDList;
    if (!exclusiv) {
      snippetIDList = Driver.searchSnippetIDs(tagArray, lengthMaxFilter);
      for (int i : snippetIDList) {
        snippetSet.getSnippetCollection().add(Driver.readSnippetInf(i));
      }
    } else {
      for (String s : tagArray) {
        snippetIDList = Driver.searchSnippetIDs(s, lengthMaxFilter);

        for (int i : snippetIDList) {
          snippetSet.getSnippetCollection().add(Driver.readSnippetInf(i));
        }
      }
    }
    return snippetSet;
  }

  @Override
  public boolean isSnippetPartOfLongerFile(int snippetID) {
    return Driver.isSnippetPartOfLongerFile(snippetID);
  }


  @Override
  public void updateTagName(String newTagName, String oldTagName) {
    Driver.updateTagInfo(newTagName, oldTagName);
  }

  @Override
  public void updateUserName(String newUserName, String oldUserName) {
    Driver.updateUserInfo(newUserName, oldUserName);
  }

  @Override
  public SnippetSet getAllSnippetFromFile(int fileID) {

    return Driver.getAllSnippetFromFile(fileID);
  }

  @Override
  public SnippetSet createSnippetSetFromIds(List<Integer> snippetIDs) {
    SnippetSet snippetSet = new SnippetSet();
    for(int i = 0; i< snippetIDs.size(); i++) {
      SnippetInfo temp = Driver.readSnippetInf(snippetIDs.get(i));
      System.out.println("temp " + temp);
      snippetSet.addSnippet(temp);

    }
    return snippetSet;
  }

  @Override
  public int writeSnippet(SnippetInfo snippetInfo, int fileID) {
    return Driver.writeSnippet(snippetInfo,fileID);
  }

  @Override
  public List<SnippetInfo> createSnippetListFromSnippetID(List<Integer> snippetIDList) {
    List<SnippetInfo> snippetInfos = new ArrayList<>();
    for (int i : snippetIDList) {
      snippetInfos.add(Driver.readSnippetInf(i));

    }
    return snippetInfos;
  }

  @Override
  public int getFileIdFromSnippetId(int snippetID){
    return Driver.getFileIDFromSnippetID(snippetID);
  }

  @Override
  public String getFileNameFromSnippetId(int snippetID) {
   return Driver.getFileNameFromSnippetId(snippetID);
  }

  @Override
  public boolean removeUnusedTag(String tagName) {
   return Driver.deleteUnusedTag(tagName);
  }

  @Override
  public boolean removeAllUnusedTags() {
    boolean returBool = false;
    List<String> allTags = getAllTags();
    int nrOfTags = allTags.size();
    for(String s : allTags){
      removeUnusedTag(s);
    }

    if(nrOfTags>getAllTags().size()){
      returBool = true;
    }
    return returBool;
  }

  @Override
  public int writeSnippetAsAdmin(SnippetInfo snippetInfo, FileInfo fileInfo) {
    return Driver.writeSnippetAsAdmin(snippetInfo,fileInfo);
  }

  @Override
  public boolean editSnippetAsAdmin(SnippetInfo snippetInfo, FileInfo fileInfo, int snippetID) {

    return Driver.deleteInsertAsAdmin(snippetInfo,fileInfo,snippetID);
  }

  @Override
  public boolean deleteSnippetAsAdmin(int snippetID) {
    return Driver.deleteSnippetAdm(snippetID);
  }
}
