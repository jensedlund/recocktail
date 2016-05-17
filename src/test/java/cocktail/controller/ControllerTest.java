package cocktail.controller;

import cocktail.db_access.DbAdapterImpl;
import cocktail.db_access.Driver;
import cocktail.snippet.SetOperation;
import cocktail.snippet.SnippetInfo;
import cocktail.snippet.SnippetSet;
import cocktail.storage.SnippetStorageImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * @author Marcus Vidén Ulrika, Goloconda Fahlén, Jan Eriksson
 * @version 1.0
 * @since 2016-04-27
 */
public class ControllerTest {
  private SnippetSet snippetSet;
  private SnippetInfo snippetInfo;
  private DbAdapterImpl impl;


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

  @Test
  public void writeEditSnippet() throws Exception {
    boolean returnBool = false;
    String path = "./src/test/resources/test.zip";
    SnippetSet returnSnippetSet = Controller.getInstance().writeEditSnippet(path);
    if (returnSnippetSet.getSnippetCollection().size() > 0) {
      returnBool = true;
    }
    Assert.assertEquals(returnBool, true);
  }

  @Test
  public void executeSetOperation() throws Exception {
    //TODO Denna test får skrivas om när metoden har implementerats i controller
    boolean testBool = true;
    SnippetSet testSnippetSet = Controller.getInstance().executeSetOperation(snippetSet, SetOperation.COMPLEMENT);
    List<Integer> listToDelete = new ArrayList<>();
    for(SnippetInfo info : testSnippetSet.getSnippetCollection()){
      listToDelete.add(info.getSnippetID());
    }
    if (testSnippetSet.equals(snippetSet)) {
      testBool = false;
    }
    Assert.assertEquals(testBool, true);
  }

  @Test
  public void searchSnippetSet() throws Exception {
    List<String> tagNames = new ArrayList<>();
    tagNames.add("test1");
    SnippetSet snippetSet = Controller.getInstance().searchSnippetSet(tagNames, 0.0, false);
    SortedSet<SnippetInfo> testCollection = snippetSet.getSnippetCollection();
    boolean testBool = false;
    if (testCollection.size() != 0) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);
  }


  @Test
  public void getComplementaryTags() throws Exception {
    List<String> testComplementary;
    testComplementary = Controller.getInstance().getComplementaryTags("test1");
    boolean testBool = false;
    if (testComplementary.size() != 0) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);

  }

  @Test
  public void getZippedFiles() throws Exception {
    //TODO denna mteod vet jag inte hur jag ska kunna vaildera i nuläget. Den ger ett fel i testet eftersom det blir error i koden.
    boolean testBool = false;
    String path = Controller.getInstance().getZippedFiles(snippetSet);

    if (path.length() > 1) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);
  }

  @Test
  public void updateTagName() throws Exception {
    String oldTagName = "TagName";
    String newTagName = "testTagName";
    int oldTagID = Driver.getTagID(oldTagName);
    Controller.getInstance().updateTagName(newTagName, oldTagName);
    int newTagID = Driver.getTagID(newTagName);
    Controller.getInstance().updateTagName(oldTagName, newTagName);

    Assert.assertEquals(oldTagID, newTagID);
  }

  @Test
  public void updateUserName() throws Exception {
    String oldUserName = "userName";
    String newUserName = "newUserName";
    int oldUserID = Driver.getUserID(oldUserName);
    Controller.getInstance().updateUserName(newUserName, oldUserName);
    int newUserID = Driver.getUserID(newUserName);
    Driver.updateUserInfo(oldUserName, newUserName);

    Assert.assertEquals(oldUserID, newUserID);

  }

  @Test
  public void removeSnippetFromSet() throws Exception {
    boolean testBool = false;
    SnippetInfo testSnippetInfo = snippetSet.getSnippetCollection().first();
    SnippetSet testSnippetSet = Controller.getInstance().removeSnippetFromSet(testSnippetInfo.getSnippetID(), snippetSet);
    if (!testSnippetSet.getSnippetCollection().contains(testSnippetInfo)) {
      testBool = true;
    }

    Assert.assertEquals(testBool, true);
  }


  @Test
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

 /* @Test
  public void deleteUsedZip() throws Exception {
//TODO denna metod borde kanske returnera en boolean som bekräftelse. För att kunna testa metoden behöver metoden implementeras.
    boolean testBool = Controller.getInstance().deleteUsedZip("zipNaame");
    Assert.assertEquals(testBool,true);
  }
  */

  @Test
  public void getSingelSourceFileAndItsSnippets() throws Exception {
    boolean testBool = false;
    String path = Controller.getInstance().getSingelSourceFileAndItsSnippets(5).toString();
    if (path.length() > 1) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);
  }


  @Before
  public void setUp() throws Exception {
    impl = new DbAdapterImpl();
    List<String> tagNames = new ArrayList<>();
    tagNames.add("test1");
    snippetSet = impl.search(tagNames, 1.0, false);


  }



  @After
  public void tearDown() throws Exception {

  }


}