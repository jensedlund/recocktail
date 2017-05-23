package cocktail.service.snippet.dao;

import cocktail.service.snippet.entity.SnippetInfo;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by jerikss3 on 2017-05-23.
 */

public class MorphiaSnippetSetDaoTest {

  private final GenericDao<SnippetInfo> snippetInfoGenericDao = new MorphiaSnippetInfoDao();
  private List<SnippetInfo> aListOfSnippetInfos = new ArrayList<>();

  private void createSomeSnippetInfos() {
    List<String> tagList = new ArrayList<>();
    for(int i = 1; i < 5; i++) {
      tagList.add(String.valueOf(i));
      SnippetInfo snippetInfo = new SnippetInfo(
          null,
          i,
          tagList,
          2*i,
          1.1*i,
          1.2*i,
          LocalDate.now(),
          LocalDate.now(),
          null
      );
      aListOfSnippetInfos.add(snippetInfo);
    }
  }

  private void populateDatabase() {
    for(SnippetInfo snippetInfo : aListOfSnippetInfos) {
      snippetInfoGenericDao.add(snippetInfo);
    }
  }

  @Before
  public void setUp() {
    System.out.println("Running before method");
    createSomeSnippetInfos();
  }

  @Test
  public void testAdd() throws Exception {
    SnippetInfo snippetInfo = aListOfSnippetInfos.get(0);
    String returnedId = snippetInfoGenericDao.add(snippetInfo);
    assertTrue("Nothing returned from add operation", !returnedId.isEmpty());


  }

  @Test
  public void testGet() throws Exception {
    populateDatabase();
    SnippetInfo snippetInfo = aListOfSnippetInfos.get(0);
    String returnedId = snippetInfo.getSnippetId().toHexString();

    Optional<SnippetInfo> fetchedSnippetInfoOpt = snippetInfoGenericDao.get(returnedId);
    assertTrue("Nothing fetched with get with id " + returnedId, fetchedSnippetInfoOpt.isPresent());

    SnippetInfo fetchedSnippetInfo = fetchedSnippetInfoOpt.get();
    assertEquals("Retrived object is not equal to saved", snippetInfo, fetchedSnippetInfo);
  }

  @Test
  public void testGetAll() {
    List<SnippetInfo> snippetInfoList = snippetInfoGenericDao.getAll();
    assertNotNull("Empty list from getAll", snippetInfoList);
    assertTrue("Empty list from getAll", snippetInfoList.size() > 0);
    for (SnippetInfo snippetInfo : snippetInfoList) {
      System.out.println(snippetInfo);
    }
  }
}
