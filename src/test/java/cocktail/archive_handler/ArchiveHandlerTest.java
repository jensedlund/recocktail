package cocktail.archive_handler;

import cocktail.controller.Controller;
import cocktail.snippet.SnippetSet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Marcus Vidén Ulrika, Goloconda Fahlén, Jan Eriksson
 * @version 1.0
 * @since 2016-05-09
 */
public class ArchiveHandlerTest {
    SnippetSet snippetSet;
    ArchiveHandler archiveHandler = new ArchiveHandler();
    String filePath;
    @Before
    public void setUp() throws Exception {
        List<String> tagNames = new ArrayList<>();
        tagNames.add("test1");
        snippetSet = Controller.getInstance().searchSnippetSet(tagNames, 0.0, false);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void zip1() throws Exception {
        boolean testBool = false;
        filePath = archiveHandler.zip(snippetSet);
        if(filePath.length()>3){
            testBool = true;
        }
        Assert.assertEquals(testBool,true);
    }


    @Test
    public void zip2() throws Exception {
        boolean testBool = false;
        filePath = archiveHandler.zip(null);
        if(filePath.length()==0){
            testBool = true;
        }
        Assert.assertEquals(testBool,true);
    }

    @Test
    public void zip3() throws Exception {
        boolean testBool = false;
        SnippetSet testSet = new SnippetSet();
        filePath = archiveHandler.zip(testSet);
        if(filePath.length()==0){
            testBool = true;
        }
        Assert.assertEquals(testBool,true);
    }

    @Test
    public void unzip1() throws Exception {
        boolean testBool = false;
        String path = archiveHandler.zip(snippetSet);
        SnippetSet snippetSet = archiveHandler.unzip(path);
        if (snippetSet != null) {
            testBool = true;
        }
        Assert.assertEquals(testBool, true);
    }

    @Test
    public void unzip2() throws Exception {
        boolean testBool = false;
        SnippetSet snippetSet = archiveHandler.unzip(null);
        if (snippetSet.getSnippetCollection().size()==0) {
            testBool = true;
        }
        Assert.assertEquals(testBool, true);
    }


    @Test
    public void unzip3() throws Exception {
        boolean testBool = false;
        SnippetSet snippetSet = archiveHandler.unzip("");
        if (snippetSet.getSnippetCollection().size()==0) {
            testBool = true;
        }
        Assert.assertEquals(testBool, true);
    }


    @Test
    public void unzip4() throws Exception {
        boolean testBool = false;
        SnippetSet snippetSet = archiveHandler.unzip("någonSomInteÄrEnSökväg.zip");
        if (snippetSet.getSnippetCollection().size()==0) {
            testBool = true;
        }
        Assert.assertEquals(testBool, true);
    }


    @Test
    public void deleteUsedZip() throws Exception {
        String setName = "";
        boolean test = archiveHandler.deleteUsedZip(setName);
        Assert.assertEquals(test, false);
    }

    @Test
    public void getSingelFile1() throws Exception {
        boolean testBool = false;
        SnippetSet snippetSet = archiveHandler.getSingleFile(161);
        if (snippetSet.getSnippetCollection().size() > 0) {
            testBool = true;
        }
        Assert.assertEquals(testBool, true);
    }

    @Test
    public void getSingelFile2() throws Exception {
        boolean testBool = false;
        SnippetSet snippetSet = archiveHandler.getSingleFile(-1);
        System.out.println(snippetSet);
        if (snippetSet.getSnippetCollection().size() == 0) {
            testBool = true;
        }
        Assert.assertEquals(testBool, true);
    }
}