package cocktail.snippet;

import cocktail.db_access.DbAdapter;
import cocktail.db_access.DbAdapterImpl;
import cocktail.stream_io.StreamingService;
import cocktail.stream_io.XmlStreamer;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Marcus Vidén Ulrika, Goloconda Fahlén, Jan Eriksson
 * @version 1.0
 * @since 2016-04-28
 */
public class SnippetSetTest {
  SnippetSet snippetSet;
  @Before
  public void setUp() throws Exception {
    DbAdapter impl = new DbAdapterImpl();
    List<String> tagNames = new ArrayList<>();
    tagNames.add("test1");
    snippetSet = impl.search(tagNames, 1.0, false);
  }

  @After
  public void tearDown() throws Exception {

  }

  @Test
  public void toStream() throws Exception {
    //TODO detta test behöver skrivas om, filen som nu är angiven finns inte
    StreamingService streamingService = new XmlStreamer<>();
    File file = new File("uri");
   //boolean test = snippetSet.toStream(streamingService,file);
    //Assert.assertEquals(test,true);
  }


  @Test
  public void getOperationLog() throws Exception {
    boolean testBool = false;
    List<String> test = snippetSet.getOperationLog();
    if (test != null) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);
  }


  @Test
  public void getMaxLenSec() throws Exception {
    double expected = snippetSet.getSnippetCollection().last().getLengthSec();
   double actual = snippetSet.getMaxLenSec();
    Assert.assertEquals(expected,actual,0.0);

  }

  @Test
  public void getMinLenSec() throws Exception {
    double expected = snippetSet.getSnippetCollection().first().getLengthSec();
    double actual = snippetSet.getMinLenSec();
    Assert.assertEquals(expected,actual,0.0);
  }

  @Test
  public void getAvgLenSec() throws Exception {
    boolean testBool = false;
    double avg = snippetSet.getAvgLenSec();
    if(avg != 0){
      testBool = true;
    }
    Assert.assertEquals(testBool,true);
  }

  @Test
  public void getNumSnippets() throws Exception {
    int actual = snippetSet.getSnippetCollection().size();
    int expected = snippetSet.getNumSnippets();
    Assert.assertEquals(actual, expected);
  }

  @Test
  public void getTagsInSet() throws Exception {
    boolean testBool = false;
    Set<String> tags = snippetSet.getTagsInSet();
    if (tags.size() > 0) {
      testBool = true;
    }
    Assert.assertEquals(testBool,true);
  }

  @Test
  public void setOperation() throws Exception {
    //TODO detta test behöver skrivas om när metoderna som anropas har implementerats. Just nu returnerar den null
   /* boolean testBool = false;
    SnippetSet otherSnippetSet = new SnippetSet();
    otherSnippetSet = snippetSet;
    SnippetInfo si = otherSnippetSet.getSnippetCollection().first();
    otherSnippetSet.getSnippetCollection().remove(si);
    SnippetSet testSnippetSet = snippetSet.setOperation(otherSnippetSet, SetOperation.COMPLEMENT);
    if (!otherSnippetSet.equals(snippetSet) && !otherSnippetSet.equals(testSnippetSet)) {
      testBool = true;
    }
    Assert.assertEquals(testBool, true);
    */
  }

}