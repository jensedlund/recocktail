package cocktail.service.snippet.dao;

import cocktail.service.snippet.entity.SnippetInfo;
import cocktail.service.snippet.entity.SnippetSet;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.Assert.assertTrue;

/**
 * Created by jerikss3 on 2017-05-31.
 */
public class MorphiaSnippetSetTest {

  private final GenericDao<SnippetSet> snippetSetDao = new MorphiaSnippetSetDao();
  private final GenericDao<SnippetInfo> snippetInfoDao = new MorphiaSnippetInfoDao();

  @Test
  public void testPersistSnippetSet() throws Exception {
    List<SnippetInfo> snippetInfoList = snippetInfoDao.getAll();
    SortedSet<SnippetInfo> snippetInfoSortedSet = new TreeSet<>(snippetInfoList);
    SnippetSet snippetSet = new SnippetSet(snippetInfoSortedSet);
    snippetSetDao.add(snippetSet);

    Optional<SnippetSet> snippetSetOptional = snippetSetDao.get(snippetSet.getSnippetId().toHexString());
    System.out.println(snippetSetOptional.get());

    assertTrue(true);
  }

}
