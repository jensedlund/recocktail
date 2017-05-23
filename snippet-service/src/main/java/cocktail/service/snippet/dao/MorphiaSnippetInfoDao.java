package cocktail.service.snippet.dao;

import cocktail.service.snippet.entity.SnippetInfo;
import com.mongodb.MongoClient;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.dao.DAO;

import java.util.List;
import java.util.Optional;

/**
 * Created by jerikss3 on 2017-05-23.
 */
public class MorphiaSnippetInfoDao implements GenericDao<SnippetInfo> {

  private final DAO<SnippetInfo, ObjectId> dao;
  private final Morphia morphia;
  private final Datastore datastore;


  public MorphiaSnippetInfoDao() {
    morphia = new Morphia();
    morphia.mapPackage("cocktail.service.snippet.entity");

    datastore = morphia.createDatastore(new MongoClient(), "morphia_example");
    datastore.ensureIndexes();

    dao = new BasicDAO<SnippetInfo, ObjectId>(SnippetInfo.class, datastore);
  }

  @Override
  public String add(SnippetInfo snippetInfo) {
    Key<SnippetInfo> result = dao.save(snippetInfo);
    return result.getId().toString();
  }

  @Override
  public Optional<SnippetInfo> get(String snippetId) {
    SnippetInfo snippetInfo = dao.get(new ObjectId(snippetId));
    return Optional.of(snippetInfo);
  }

  @Override
  public String update(SnippetInfo snippetInfo) {
    return null;
  }

  @Override
  public boolean delete(String snippetId) {
    return false;
  }

  @Override
  public List<SnippetInfo> getAll() {
    return null;
  }

  @Override
  public List<SnippetInfo> search(String key, String value) {
    return null;
  }
}
