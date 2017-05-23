package cocktail.service.snippet.dao;

import cocktail.service.snippet.entity.SnippetInfo;

import java.util.List;
import java.util.Optional;

/**
 * Created by jerikss3 on 2017-05-22.
 */
public interface GenericDao<T> {
  String add(T snippetInfo);
  Optional<T> get(String snippetId);
  String update(T snippetInfo);
  boolean delete(String snippetId);
  List<T> getAll();
  List<T> search(String key, String value);
}
