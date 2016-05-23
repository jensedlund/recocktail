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

  void storeSet(SnippetSet snippetSet);

  SnippetSet writeEditSnippet(String filePath);

  SnippetSet executeSetOperation(SnippetSet snippetSet, SetOperation setOperation);

  SnippetSet searchSnippetSet(String[] tagNames, boolean exclusive);

  SnippetSet searchSnippetSet(List<String> tagNames, double lengthMaxFilter, boolean exclusive);

  List<String> getComplementaryTags(String tagName);

  String getZippedFiles(SnippetSet snippetSet);

  void updateTagName(String newTagName, String oldTagName);

  void updateUserName(String newTagName, String oldTagName);

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
