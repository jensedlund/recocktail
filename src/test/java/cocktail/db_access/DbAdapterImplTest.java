package cocktail.db_access;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import cocktail.snippet.FileInfo;
import cocktail.snippet.SnippetInfo;
import cocktail.snippet.SnippetSet;

/**
 ** Copyright 2016 Jens Edlund, Joakim Gustafson, Jonas Beskow, Ulrika Goloconda Fahlen, Jan Eriksson, Marcus Viden
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
 */
public class DbAdapterImplTest {

  private DbAdapter adapter;
  private List<Integer> listToDelete;
  private SnippetInfo snippetInfo;
  private FileInfo fileInfo;
  private int snippetID;
  private String testTag;
  private String userName;
  private int fileID;

  @Before
  public void setUp() throws Exception {
    adapter = new DbAdapterImpl();
    Driver.connectToMySql();
    if (listToDelete == null) {
      listToDelete = new ArrayList<>();
    }
    if (testTag == null) {
      testTag = "tagname";
    }
  }

  @After
  public void tearDown() throws Exception {
    for (int i : listToDelete) {
      adapter.deleteSnippet(i);
    }
  }

  @Test
  public void connectToMySql() throws Exception {
    boolean b = Driver.connectToMySql();
    Assert.assertEquals(b, true);
  }

  @Test
  public void writeSnippet() throws Exception {
    boolean testBool = false;
    writeTestSnippet();
    if (snippetID > -1) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);
  }

  @Test
  public void editSnippet() throws Exception {
    writeTestSnippet();
    snippetInfo.setKbSize(33);
    snippetInfo.setUserName("Arne");
    adapter.editSnippet(snippetInfo, fileInfo, snippetID);
    SnippetInfo snippetInfo1 = adapter.readSnippetInfo(snippetID);
    Assert.assertEquals(snippetInfo1.getUserName(), "Arne");

  }

  @Test
  public void deleteSnippet() throws Exception {
    boolean testBool = adapter.deleteSnippet(snippetID);

    Assert.assertEquals(testBool, false);
  }

  @Test
  public void readSnippet() throws Exception {
    boolean returnBool = false;

    SnippetInfo snippetInfo2 = adapter.readSnippetInfo(snippetID);
    if (snippetInfo2.getSnippetID() == snippetID) {
      returnBool = true;
    }

    Assert.assertEquals(returnBool, true);

  }

  @Test
  public void readSnippetInfo() throws Exception {

    SnippetInfo snippetInfo1 = adapter.readSnippetInfo(snippetID);
    Assert.assertEquals(snippetID, snippetInfo1.getSnippetID());

  }

  @Test
  public void getNumberOfFiles() throws Exception {
    boolean testBool = false;
    int numFilese = adapter.getNumberOfFiles();
    if (numFilese > 0) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);
  }

  @Test
  public void getTotalFileSizeKb() throws Exception {
    boolean testBool = false;
    int totalKb = adapter.getTotalFileSizeKb();
    if (totalKb > 0) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);
  }

  @Test
  public void getMinFileSizeKb() throws Exception {
    boolean testBool = false;
    int minFilelKb = adapter.getMinFileSizeKb();
    if (minFilelKb < 3000) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);

  }

  @Test
  public void getMaxFileSizeKb() throws Exception {
    boolean testBool = false;
    int maxFilelKb = adapter.getMaxFileSizeKb();
    if (maxFilelKb > 0) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);
  }

  @Test
  public void getMinFileLenSec() throws Exception {
    boolean testBool = false;
    double minFileLen = adapter.getMinFileLenSec();
    if (minFileLen < 3000) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);
  }

  @Test
  public void getMaxFileLenSec() throws Exception {
    boolean testBool = false;
    double maxLen = adapter.getMaxFileLenSec();
    if (maxLen > 0) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);
  }

  @Test
  public void getNumSnippets() throws Exception {
    boolean testBool = false;
    int numSnippets = adapter.getNumSnippets();
    if (numSnippets > 0) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);
  }

  @Test
  public void getMinSnippetLenSec() throws Exception {
    boolean testBool = false;
    double imLenSec = adapter.getMinSnippetLenSec();
    if (imLenSec < 20) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);
  }

  @Test
  public void getMaxSnippetLenSec() throws Exception {
    boolean testBool = false;
    double maxLenSec = adapter.getMaxSnippetLenSec();
    if (maxLenSec > 0) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);
  }

  @Test
  public void getAllTags() throws Exception {
    boolean testBool = false;
    List<String> tags = adapter.getAllTags();
    if (tags.contains(testTag)) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);
  }

//  @Test
  public void getOccuranceOfTag() throws Exception {
    boolean testBool = false;
    int testInt = adapter.getOccurrenceOfTag(".demo-sea-bird");
    if (testInt > 0) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);
  }

//  @Test
  public void getComplementaryTags() throws Exception {
    boolean testBool = false;
    List<String> compTags = adapter.getAssociatedTags(".demo-sea-bird");
    if (compTags.size() > 0) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);
  }

//  @Test
  public void search() throws Exception {
    boolean testBool = false;
    List<String> tagList = new ArrayList<>();
    tagList.add(".demo-sea-bird");
    SnippetSet snippetSet = adapter.search(tagList, 0.0, false);
    if (snippetSet.getSnippetCollection().last().getTagNames().contains(".demo-sea-bird")) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);
  }

//  @Test
  public void search1() throws Exception {
    boolean testBool = false;
    List<String> tagList = new ArrayList<>();
    tagList.add(".demo-sea-bird");
    SnippetSet snippetSet = adapter.search(tagList, 0.0, true);
    if (snippetSet.getSnippetCollection().size() > 0) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);

  }

  @Test
  public void isSnippetPartOfLongerFile() throws Exception {
    boolean testBool = adapter.isSnippetPartOfLongerFile(384);
    //TODO ange en annan snippet som är en del av en längre fil när en sådan finns i databasen. ändra till true i Assert
    Assert.assertEquals(testBool, false);
  }

  @Test
  public void isSnippetPartOfLongerFile2() throws Exception {
    boolean testBool = adapter.isSnippetPartOfLongerFile(snippetID);
    Assert.assertEquals(testBool, false);
  }


  @Test
  public void getAllSnippetFromFile() throws Exception {
    boolean testBool = false;
    writeTestSnippet();
    SnippetSet snippetSet = adapter.getAllSnippetFromFile(fileID);
    if (snippetSet.getSnippetCollection().size() > 0) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);
  }

//  @Test
  public void createSnippetSetFromIds() throws Exception {
    boolean testBool = false;
    List<Integer> snippetIDs = new ArrayList<>();
    for (int i = 134; i < 144; i++) {
      snippetIDs.add(i);
    }
    SnippetSet snippetSet1 = adapter.createSnippetSetFromIds(snippetIDs);
    if (snippetSet1.getSnippetCollection().size() > 0) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);
  }

  @Test
  public void writeSnippet1() throws Exception {
    boolean testBool = false;
    FileInputStream input = new FileInputStream("src/test/resources/clap1.wav");
    byte[] b = spark.utils.IOUtils.toByteArray(input);
    ByteArrayInputStream bInput = new ByteArrayInputStream(b);
    FileInfo fileInfo = new FileInfo(bInput, "test-tr%C3%A4dpipl%C3%A4ra-5", 39, 3.0);
    List<String> tagNames = new ArrayList<>();
    tagNames.add("TagName");
    LocalDate ld1 = LocalDate.now();
    LocalDate ld2 = LocalDate.now();
    SnippetInfo snippetInfo = new SnippetInfo("sourceFileName", tagNames, 0.2, 2.0, 2, ld1, ld2, "userName");
    int snippetID = adapter.writeSnippet(fileInfo, snippetInfo);
    listToDelete.add(snippetID);
    if (snippetID > 0) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);
  }

  @Test
  public void createSnippetListFromSnippetIds() throws Exception {
    List<Integer> snippetIDList = new ArrayList<>();
   snippetIDList.add(95);
    snippetIDList.add(96);
    List<SnippetInfo> testLIst = adapter.createSnippetListFromSnippetID(snippetIDList);
    Assert.assertEquals(testLIst.size(), snippetIDList.size());
  }

//  @Test
  public void getFileNameFromSnippetId() throws Exception {
    boolean testBool = false;
    String fileName = adapter.getFileNameFromSnippetId(135);
    if (fileName.length() > 1) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);
  }

  @Test
  public void removeUnusedTag() throws Exception {
    boolean testBool = false;
    writeSnippet();
    adapter.deleteSnippet(snippetID);
    adapter.removeUnusedTag("removetest");
    if (!adapter.getAllTags().contains("removetest")) {
      testBool = true;
    }
    Assert.assertEquals(testBool,true);
  }

  @Test
  public void removeAllUnusedTags() throws Exception {
    boolean testBool = false;
    writeSnippet();
    adapter.deleteSnippet(snippetID);
    int numberOfPre = adapter.getAllTags().size();
    adapter.removeAllUnusedTags();
    int numberOfPost = adapter.getAllTags().size();

    if(numberOfPost< numberOfPre){
      testBool = true;
    }
    Assert.assertEquals(testBool,true);
  }

  public void writeTestSnippet() {
    boolean testBool = false;
    try {
      FileInputStream input = new FileInputStream("src/test/resources/clap1.wav");
      byte[] b = spark.utils.IOUtils.toByteArray(input);
      ByteArrayInputStream bInput = new ByteArrayInputStream(b);
      fileInfo = new FileInfo(bInput, "Filnamn", 39, 3.0);
      List<String> tagNames = new ArrayList<>();
      tagNames.add("TagName");
      tagNames.add("removetest");
      LocalDate ld1 = LocalDate.now();
      LocalDate ld2 = LocalDate.now();
      snippetInfo = new SnippetInfo("sourceFileName", tagNames, 0.2, 2.0, 2, ld1, ld2, "userName");
      snippetID = adapter.writeSnippet(fileInfo, snippetInfo);
      userName = snippetInfo.getUserName();
      fileID = Driver.getFileIDFromSnippetID(snippetID);
      listToDelete.add(snippetID);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}