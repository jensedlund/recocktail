package cocktail.controller;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import cocktail.archive_handler.ArchiveHandler;
import cocktail.db_access.DbAdapterImpl;
import cocktail.snippet.SnippetInfo;
import cocktail.snippet.SnippetSet;
import cocktail.storage.SnippetStorageImpl;

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

public class ControllerTest {
  private SnippetSet snippetSet;

  private SnippetInfo snippetInfo;
  private DbAdapterImpl impl;
  private ArchiveHandler archiveHandler;


  @Test
  public void storeSet() throws Exception {
    List<String> tagNames = new ArrayList<>();
    tagNames.add("clap");
    SnippetSet snippetTest = impl.search(tagNames, 0.0, false);
    String testName = snippetTest.getSetName();
    Controller.getInstance().storeSet(snippetTest);
    SnippetSet expected = SnippetStorageImpl.getInstance().getSet(testName);
    Assert.assertEquals(expected, snippetTest);
  }

//  @Test
  public void writeEditSnippet1() throws Exception {
    boolean returnBool = false;
    List<String> tagNames = new ArrayList<>();
    tagNames.add(".demo-sea-bird");
    SnippetSet snippetSet = impl.search(tagNames, 1.0, false);;
    String path = archiveHandler.zip(snippetSet);
    SnippetSet returnSnippetSet = Controller.getInstance().writeEditSnippet(path);
    if (returnSnippetSet.getSnippetCollection().size() > 0) {
      returnBool = true;
    }
    Assert.assertEquals(returnBool, true);
  }

  @Test
  public void writeEditSnippet2() throws Exception {
    boolean returnBool = false;
    SnippetSet returnSnippetSet = Controller.getInstance().writeEditSnippet("");
    if (returnSnippetSet.getSnippetCollection().size() == 0) {
      returnBool = true;
    }
    Assert.assertEquals(returnBool, true);
  }


  @Test
  public void writeEditSnippet3() throws Exception {
    boolean returnBool = false;
    SnippetSet returnSnippetSet = Controller.getInstance().writeEditSnippet("strängSomInteÄrEnSökväg.zip");
    if (returnSnippetSet.getSnippetCollection().size() == 0) {
      returnBool = true;
    }
    Assert.assertEquals(returnBool, true);
  }

  @Test
  public void executeSetOperation() throws Exception {
    //TODO Denna test får skrivas om när metoden har implementerats i controller
    /*boolean testBool = true;
    SnippetSet testSnippetSet = Controller.getInstance().executeSetOperation(snippetSet, SetOperation.COMPLEMENT);
    List<Integer> listToDelete = new ArrayList<>();
    for(SnippetInfo info : testSnippetSet.getSnippetCollection()){
      listToDelete.add(info.getSnippetID());
    }
    if (testSnippetSet.equals(snippetSet)) {
      testBool = false;
    }
    Assert.assertEquals(testBool, true);
    */
  }

//  @Test
  public void searchSnippetSet1() throws Exception {
    List<String> tagNames = new ArrayList<>();
    tagNames.add(".demo-sea-bird");
    SnippetSet snippetSet = Controller.getInstance().searchSnippetSet(tagNames, 0.0, false);
    SortedSet<SnippetInfo> testCollection = snippetSet.getSnippetCollection();
    boolean testBool = false;
    if (testCollection.size() != 0) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);

  }

  @Test
  public void searchSnippetSet2() throws Exception {
    List<String> tagNames = new ArrayList<>();
    tagNames.add("");
    SnippetSet snippetSet = Controller.getInstance().searchSnippetSet(tagNames, 0.0, false);
    SortedSet<SnippetInfo> testCollection = snippetSet.getSnippetCollection();
    boolean testBool = false;
    if (testCollection.size() == 0) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);
  }

  @Test
  public void searchSnippetSet3() throws Exception {
    List<String> tagNames = new ArrayList<>();
    tagNames.add("någotMeKonstiga&Tecken");
    SnippetSet snippetSet = Controller.getInstance().searchSnippetSet(tagNames, 0.0, false);
    SortedSet<SnippetInfo> testCollection = snippetSet.getSnippetCollection();
    boolean testBool = false;
    if (testCollection.size() == 0) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);
  }

//  @Test
  public void getComplementaryTags() throws Exception {
    List<String> testComplementary;
    testComplementary = Controller.getInstance().getAssociatedTags(".demo-sea-bird");
    boolean testBool = false;
    if (testComplementary.size() != 0) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);
  }

//  @Test
  public void getZippedFiles1() throws Exception {
    boolean testBool = false;
    String path = Controller.getInstance().getZippedFiles(snippetSet);

    if (path.length() > 1) {
      testBool = true;
    }
    System.out.println(path);
    Controller.getInstance().deleteUsedZip(path);
    Assert.assertEquals(testBool, true);
  }

  @Test
  public void getZippedFiles2() throws Exception {
    boolean testBool = false;
    String path = Controller.getInstance().getZippedFiles(null);
    System.out.println(path);
    if (path.length() == 0) {
      testBool = true;
    }
    System.out.println(path);
    Controller.getInstance().deleteUsedZip(path);
    Assert.assertEquals(testBool, true);
  }

  @Test
  public void getZippedFiles3() throws Exception {
    boolean testBool = false;
    SnippetSet test = new SnippetSet();
    String path = Controller.getInstance().getZippedFiles(test);
    if (path.length() == 0) {
      testBool = true;
    }
    System.out.println(path);
    Controller.getInstance().deleteUsedZip(path);
    Assert.assertEquals(testBool, true);
  }

  @Test
  public void updateUserName() throws Exception {
    boolean testBool = false;
    String oldUserName = "userName";
    String newUserName = "newUserName";
    Controller.getInstance().updateUserName(newUserName, oldUserName);
    if(impl.getAllUsers().contains(newUserName)){
      testBool = true;
    }
    Controller.getInstance().updateUserName(oldUserName,newUserName);

    Assert.assertEquals(testBool, true);

  }

//  @Test
  public void removeSnippetFromSet() throws Exception {
    boolean testBool = false;

    SnippetInfo testSnippetInfo = snippetSet.getSnippetCollection().first();
    SnippetSet testSnippetSet = Controller.getInstance().removeSnippetFromSet(testSnippetInfo.getSnippetID(), snippetSet);
    if (!testSnippetSet.getSnippetCollection().contains(testSnippetInfo)) {
      testBool = true;
    }

    Assert.assertEquals(testBool, true);
  }


//  @Test
  public void addSnippetToSet() throws Exception {
    boolean testBool = false;
    System.out.println(snippetSet.getSnippetCollection().size());
    SnippetInfo testSnippetInfo = snippetSet.getSnippetCollection().first();
    snippetSet.removeSnippet(testSnippetInfo.getSnippetID());
    snippetSet.addSnippet(testSnippetInfo);

    if (snippetSet.getSnippetCollection().contains(testSnippetInfo)) {
      testBool = true;
    }

    Assert.assertEquals(testBool, true);
  }


  @Test
  public void getStoredSet() throws Exception {
    boolean testBool = false;
    List<String> tagNames = new ArrayList<>();
    tagNames.add("clap");

    SnippetSet snippetTest = Controller.getInstance().searchSnippetSet(tagNames, 0.0, false);
    String setName = snippetTest.getSetName();
    Controller.getInstance().storeSet(snippetTest);
    SnippetSet snippetSet2 = Controller.getInstance().getStoredSet(setName);
    if (snippetSet2.getSetName().equals(setName)) {
      testBool = true;
    }

    Assert.assertEquals(testBool, true);
  }

  @Test
  public void getAllSavedSetsName() throws Exception {
    boolean testBool = false;
    Controller.getInstance().storeSet(snippetSet);
    List<String> allSetNames = Controller.getInstance().getAllSavedSetsName();
    if(allSetNames.size() > 0){
      testBool = true;
    }
    Assert.assertEquals(testBool,true);
  }


  @Test
  public void getCompleteSetOfTagNames() throws Exception {
    boolean testBool = false;
    Set<String> testTagList;
    testTagList = Controller.getInstance().getCompleteSetOfTagNames();
    if(testTagList.size()>0){
      testBool = true;
    }

    Assert.assertEquals(testBool,true);
  }

//  @Test
  public void deleteUsedZip() throws Exception {
    String filePath = archiveHandler.zip(snippetSet);
    boolean testBool = Controller.getInstance().deleteUsedZip(filePath);
    Assert.assertEquals(testBool,true);
  }


//  @Test
  public void getSingleSourceFileAndItsSnippets1() throws Exception {
    boolean testBool = false;
    SnippetSet snippetSet = Controller.getInstance().getSingleSourceFileAndItsSnippets(1343);
    if (snippetSet.getSnippetCollection().size() > 0) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);
  }


  @Test
  public void getSingleSourceFileAndItsSnippets2() throws Exception {
    boolean testBool = false;
    String path = Controller.getInstance().getSingleSourceFileAndItsSnippets(-1).toString();
    if (path.length() < 1) {
      testBool = false;
    }
    Assert.assertEquals(testBool, false);
  }


  @Before
  public void setUp() throws Exception {
    archiveHandler = new ArchiveHandler();
    impl = new DbAdapterImpl();
    List<String> tagNames = new ArrayList<>();
    tagNames.add(".demo-sea-bird");
    snippetSet = impl.search(tagNames, 1.0, false);


  }

  @After
  public void tearDown() throws Exception {

  }
}