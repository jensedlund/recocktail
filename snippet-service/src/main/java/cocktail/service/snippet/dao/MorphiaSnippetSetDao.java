package cocktail.service.snippet.dao;

import cocktail.service.snippet.entity.SnippetSet;
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
public class MorphiaSnippetSetDao implements GenericDao<SnippetSet> {

  private final DAO<SnippetSet, ObjectId> dao;
  private final Morphia morphia;
  private final Datastore datastore;


  public MorphiaSnippetSetDao() {
    morphia = new Morphia();
    morphia.mapPackage("cocktail.service.snippet.entity");

    datastore = morphia.createDatastore(new MongoClient(), "morphia_example");
    datastore.ensureIndexes();

    dao = new BasicDAO<SnippetSet, ObjectId>(SnippetSet.class, datastore);
  }

  public void dropDatabase() {
    datastore.getDB().dropDatabase();
  }

  @Override
  public String add(SnippetSet snippetSet) {
    Key<SnippetSet> result = dao.save(snippetSet);
    return result.getId().toString();
  }

  @Override
  public Optional<SnippetSet> get(String snippetId) {
    SnippetSet snippetSet = dao.get(new ObjectId(snippetId));
    return Optional.ofNullable(snippetSet);
  }

  @Override
  public String update(SnippetSet snippetSet) {
    Key<SnippetSet> result = dao.save(snippetSet);
    return result.getId().toString();
  }

  @Override
  public void delete(String snippetId) {
    dao.deleteById(new ObjectId(snippetId));
  }

  @Override
  public List<SnippetSet> getAll() {
    QueryResults<SnippetSet> queryResults = dao.find();
    return queryResults.asList();
  }

  @Override
  public List<SnippetSet> search(String key, String value) {
    Query<SnippetSet> query = dao.createQuery();
    List<SnippetSet> queryResult = query.field(key).contains(value).asList();
    return queryResult;
  }
}
