package cocktail.service.snippet.dao;

import cocktail.service.snippet.entity.SnippetInfo;
import cocktail.service.snippet.entity.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by jerikss3 on 2017-05-23.
 */

public class MorphiaSnippetInfoDaoTest {

  private final GenericDao<SnippetInfo> snippetInfoDao = new MorphiaSnippetInfoDao();
  private final GenericDao<User> userDao = new MorphiaUserDao();

  private List<SnippetInfo> aListOfSnippetInfos = new ArrayList<>();

  private User testUser = new User("Test user");

  private void persistUser() {
    userDao.add(testUser);
  }

  private void createSomeSnippetInfos() {
    persistUser();

    aListOfSnippetInfos = new ArrayList<>();
    List<String> tagList = new ArrayList<>();
    tagList.add("gull");
    tagList.add("wolf");
    tagList.add("bear");
    tagList.add("larch");
    for(int i = 1; i < 5; i++) {
      SnippetInfo snippetInfo = new SnippetInfo(
          null,
          i,
          tagList.subList(0,i),
          2*i,
          1.1*i,
          1.2*i,
          LocalDateTime.now(),
          LocalDate.now(),
          testUser
      );
      aListOfSnippetInfos.add(snippetInfo);
    }
  }

  private void populateDatabase() {
    for(SnippetInfo snippetInfo : aListOfSnippetInfos) {
      snippetInfoDao.add(snippetInfo);
    }
  }

  @Before
  public void setUp() {
    System.out.println("Running before method");
    createSomeSnippetInfos();
    populateDatabase();
  }

  @After
  public void tearDown() {
    System.out.println("Running after method");
    MorphiaSnippetInfoDao snippetInfoDaoRecast = (MorphiaSnippetInfoDao) snippetInfoDao;
    snippetInfoDaoRecast.dropDatabase();
  }

  @Test
  public void testAdd() throws Exception {
    SnippetInfo snippetInfo = aListOfSnippetInfos.get(0);
    String returnedId = snippetInfoDao.add(snippetInfo);
    assertTrue("Nothing returned from add operation", !returnedId.isEmpty());
    Optional<SnippetInfo> snippetInfoOptional = snippetInfoDao.get(returnedId);
    assertTrue(snippetInfoOptional.isPresent());
    System.out.println(snippetInfoOptional.get());
  }

  @Test
  public void testGet() throws Exception {
    SnippetInfo snippetInfo = aListOfSnippetInfos.get(0);
    String returnedId = snippetInfo.getSnippetId().toHexString();

    Optional<SnippetInfo> fetchedSnippetInfoOpt = snippetInfoDao.get(returnedId);
    assertTrue("Nothing fetched with get with id " + returnedId, fetchedSnippetInfoOpt.isPresent());

    SnippetInfo fetchedSnippetInfo = fetchedSnippetInfoOpt.get();
    assertEquals("Retrived object is not equal to saved", snippetInfo, fetchedSnippetInfo);

    snippetInfo = new SnippetInfo(
        null,
        5,
        new ArrayList<>(),
        2,
        1.1,
        1.2,
        LocalDateTime.now(),
        LocalDate.now(),
        null
    );

    snippetInfoDao.add(snippetInfo);
    returnedId = snippetInfo.getSnippetId().toHexString();

    fetchedSnippetInfoOpt = snippetInfoDao.get(returnedId);
    assertTrue("Nothing fetched with get with id " + returnedId, fetchedSnippetInfoOpt.isPresent());

    fetchedSnippetInfo = fetchedSnippetInfoOpt.get();
    assertEquals("Retrived object is not equal to saved", snippetInfo, fetchedSnippetInfo);

  }

  @Test
  public void testGetAll() {
    List<SnippetInfo> snippetInfoList = snippetInfoDao.getAll();
    assertNotNull("Empty list from getAll", snippetInfoList);
    assertTrue("Empty list from getAll", snippetInfoList.size() > 0);
    for (SnippetInfo snippetInfo : snippetInfoList) {
      System.out.println(snippetInfo);
    }
  }

  @Test
  public void testUpdate() {
    SnippetInfo snippetInfo = aListOfSnippetInfos.get(0);
    snippetInfo.setLastModified(LocalDate.of(2017,12,12));
    String returnedId = snippetInfoDao.update(snippetInfo);

    Optional<SnippetInfo> fetchedSnippetInfoOpt = snippetInfoDao.get(returnedId);
    assertTrue("Nothing fetched with get with id " + returnedId, fetchedSnippetInfoOpt.isPresent());

    SnippetInfo fetchedSnippetInfo = fetchedSnippetInfoOpt.get();
    assertEquals("Retrived object is not equal to saved", snippetInfo, fetchedSnippetInfo);
  }

  @Test
  public void testDelete() {
    SnippetInfo snippetInfo = aListOfSnippetInfos.get(0);
    String idToDelete = snippetInfo.getSnippetId().toHexString();
    snippetInfoDao.delete(idToDelete);
    Optional<SnippetInfo> fetchedSnippetInfoOpt = snippetInfoDao.get(idToDelete);
    assertFalse("Not deleted entity with id " + idToDelete, fetchedSnippetInfoOpt.isPresent());
    snippetInfoDao.delete(idToDelete);
  }

  @Test
  public void testSearchString() {
    List<SnippetInfo> snippetInfoList = snippetInfoDao.search("tags","wolf");
    for(SnippetInfo si : snippetInfoList) {
      System.out.println(si);
    }

    assertTrue("Search should return at least 3 results", snippetInfoList.size() > 2);
  }
}
