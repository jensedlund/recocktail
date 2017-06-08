package cocktail.service.snippet.dao;

import cocktail.service.snippet.entity.SnippetInfo;
import cocktail.service.snippet.entity.User;
import com.mongodb.MongoClient;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.dao.DAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.QueryResults;

import java.util.List;
import java.util.Optional;

/**
 * Created by jerikss3 on 2017-05-23.
 */
public class MorphiaUserDao implements GenericDao<User> {

  private final DAO<User, ObjectId> dao;
  private final Morphia morphia;
  private final Datastore datastore;


  public MorphiaUserDao() {
    morphia = new Morphia();
    morphia.mapPackage("cocktail.service.snippet.entity");

    datastore = morphia.createDatastore(new MongoClient(), "morphia_example");
    datastore.ensureIndexes();

    dao = new BasicDAO<User, ObjectId>(User.class, datastore);
  }

  @Override
  public String add(User user) {
    Key<User> result = dao.save(user);
    return result.getId().toString();
  }

  @Override
  public Optional<User> get(String snippetId) {
    User user = dao.get(new ObjectId(snippetId));
    return Optional.ofNullable(user);
  }

  @Override
  public String update(User user) {
    Key<User> result = dao.save(user);
    return result.getId().toString();
  }

  @Override
  public void delete(String snippetId) {
    dao.deleteById(new ObjectId(snippetId));
  }

  @Override
  public List<User> getAll() {
    QueryResults<User> queryResults = dao.find();
    return queryResults.asList();
  }

  @Override
  public List<User> search(String key, String value) {
    Query<User> query = dao.createQuery();
    List<User> queryResult = query.field(key).contains(value).asList();
    return queryResult;
  }
}
