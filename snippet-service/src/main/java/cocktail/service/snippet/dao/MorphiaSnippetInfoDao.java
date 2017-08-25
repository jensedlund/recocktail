package cocktail.service.snippet.dao;

import cocktail.service.snippet.entity.SnippetInfo;
import cocktail.service.snippet.entity.TagCloudEntry;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.dao.BasicDAO;
import org.mongodb.morphia.dao.DAO;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.QueryResults;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.mongodb.morphia.aggregation.Accumulator.accumulator;
import static org.mongodb.morphia.aggregation.Group.grouping;

/**
 * Created by jerikss3 on 2017-05-23.
 */
public class MorphiaSnippetInfoDao implements GenericDao<SnippetInfo> {

  private final DAO<SnippetInfo, ObjectId> dao;
  private final DAO<TagCloudEntry, String> tagsDao;
  private final Morphia morphia;
  private final Datastore datastore;



  public MorphiaSnippetInfoDao() {
    morphia = new Morphia();
    morphia.mapPackage("cocktail.service.snippet.entity");

    datastore = morphia.createDatastore(new MongoClient(), "morphia_example");
    datastore.ensureIndexes();

    dao = new BasicDAO<SnippetInfo, ObjectId>(SnippetInfo.class, datastore);
    tagsDao = new BasicDAO<TagCloudEntry, String>(TagCloudEntry.class, datastore);
  }

  public void dropDatabase() {
    datastore.getDB().dropDatabase();
  }

  public void updateTags() {

   datastore.createAggregation(SnippetInfo.class)
        .unwind("tags")
        .group("tags", grouping("count", accumulator("$sum", 1)))
        .out("tags", TagCloudEntry.class);
  }

  public List<TagCloudEntry> fetchTags() {
    QueryResults<TagCloudEntry> queryResults = tagsDao.find();
    return queryResults.asList();

  }

  @Override
  public String add(SnippetInfo snippetInfo) {
    Key<SnippetInfo> result = dao.save(snippetInfo);
    updateTags();
    return result.getId().toString();

  }

  @Override
  public Optional<SnippetInfo> get(String snippetId) {
    SnippetInfo snippetInfo = dao.get(new ObjectId(snippetId));
    return Optional.ofNullable(snippetInfo);
  }

  @Override
  public String update(SnippetInfo snippetInfo) {
    Key<SnippetInfo> result = dao.save(snippetInfo);
    updateTags();
    return result.getId().toString();
  }

  @Override
  public void delete(String snippetId) {
    dao.deleteById(new ObjectId(snippetId));
    updateTags();
  }

  @Override
  public List<SnippetInfo> getAll() {
    QueryResults<SnippetInfo> queryResults = dao.find();
    return queryResults.asList();
  }

  @Override
  public List<SnippetInfo> search(String key, String value) {
    Query<SnippetInfo> query = dao.createQuery();
    List<SnippetInfo> queryResult = query.field(key).contains(value).asList();
    return queryResult;
  }
}
