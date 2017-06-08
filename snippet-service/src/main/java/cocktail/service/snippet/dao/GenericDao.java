package cocktail.service.snippet.dao;

import java.util.List;
import java.util.Optional;

/**
 * Created by jerikss3 on 2017-05-22.
 */
public interface GenericDao<T> {
  String add(T entity);
  Optional<T> get(String entityId);
  String update(T entity);
  void delete(String entityId);
  List<T> getAll();
  List<T> search(String key, String value);
}
