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
        System.out.println("Körs detta innan i ArchiveHandlerTest");
        List<String> tagNames = new ArrayList<>();
        tagNames.add("duvhök");
        snippetSet = Controller.getInstance().searchSnippetSet(tagNames, 0.0, false);
        System.out.println(snippetSet);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void zip() throws Exception {
        boolean testBool = false;
        System.out.println(snippetSet);
         filePath = archiveHandler.zip(snippetSet);
        System.out.println(filePath);
        if(filePath.length()>3){
            testBool = true;
        }
        Assert.assertEquals(testBool,true);
    }

    @Test
    public void unzip() throws Exception {
        boolean testBool = false;
        String path = archiveHandler.zip(snippetSet);
        SnippetSet snippetSet = archiveHandler.unzip(path);
        if (snippetSet != null) {
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

    public void getSingleFile1() throws Exception {

        boolean testBool = false;
        SnippetSet snippetSet = archiveHandler.getSingleFile(119);
        if (snippetSet.getSnippetCollection().size() > 0) {
            testBool = true;
        }
        Assert.assertEquals(testBool, true);
    }
}