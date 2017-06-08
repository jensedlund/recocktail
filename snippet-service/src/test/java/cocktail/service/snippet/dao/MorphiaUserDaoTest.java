package cocktail.service.snippet.dao;

import cocktail.service.snippet.entity.SnippetInfo;
import cocktail.service.snippet.entity.User;
import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;

import javax.jws.soap.SOAPBinding;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * Created by jerikss3 on 2017-05-23.
 */

public class MorphiaUserDaoTest {

  private final GenericDao<User> userDao = new MorphiaUserDao();
  private List<User> userList = new ArrayList<>();

  private void createSomeUsers() {
    User dummyUser = new User(ObjectId.get(), "NoOne");
    for(int i = 1; i < 5; i++) {
      User user = new User("User" + i);
      userList.add(user);
    }
  }

  private void populateDatabase() {
    for(User user : userList) {
      userDao.add(user);
    }
  }

  @Before
  public void setUp() {
    System.out.println("Running before method");
    createSomeUsers();
  }

  @Test
  public void testAdd() throws Exception {
    User user = userList.get(0);
    String returnedId = userDao.add(user);
    assertTrue("Nothing returned from add operation", !returnedId.isEmpty());
  }

  @Test
  public void testGet() throws Exception {
    populateDatabase();
    User user = userList.get(0);
    String returnedId = user.getId().toHexString();

    Optional<User> fetchedUserOpt = userDao.get(returnedId);
    assertTrue("Nothing fetched with get with id " + returnedId, fetchedUserOpt.isPresent());

    User fetchedUser = fetchedUserOpt.get();
    assertEquals("Retrived object is not equal to saved", user, fetchedUser);

    user = new User("Sam User");

    userDao.add(user);
    returnedId = user.getId().toHexString();

    fetchedUserOpt = userDao.get(returnedId);
    assertTrue("Nothing fetched with get with id " + returnedId, fetchedUserOpt.isPresent());

    fetchedUser = fetchedUserOpt.get();
    assertEquals("Retrived object is not equal to saved", user, fetchedUser);
  }

  @Test
  public void testGetAll() {
    List<User> userList = userDao.getAll();
    assertNotNull("Empty list from getAll", userList);
    assertTrue("Empty list from getAll", userList.size() > 0);
    for (User user : userList) {
      System.out.println(user);
    }
  }

  @Test
  public void testUpdate() {
    populateDatabase();
    User user = userList.get(0);
    user.setName("New name");
    String returnedId = userDao.update(user);

    Optional<User> fetchedUserOpt = userDao.get(returnedId);
    assertTrue("Nothing fetched with get with id " + returnedId, fetchedUserOpt.isPresent());

    User fetchedUser = fetchedUserOpt.get();
    assertEquals("Retrived object is not equal to saved", user, fetchedUser);
  }

  @Test
  public void testDelete() {
    populateDatabase();
    User user = userList.get(0);
    String idToDelete = user.getId().toHexString();
    userDao.delete(idToDelete);
    Optional<User> fetchedUserOpt = userDao.get(idToDelete);
    assertFalse("Not deleted entity with id " + idToDelete, fetchedUserOpt.isPresent());
    userDao.delete(idToDelete);
  }

  @Test
  public void testSearchString() {
    populateDatabase();
    List<User> userList = userDao.search("name","2");
    for(User si : userList) {
      System.out.println(si);
    }

    assertTrue("Search should return at least 3 results", userList.size() > 2);
  }
}
