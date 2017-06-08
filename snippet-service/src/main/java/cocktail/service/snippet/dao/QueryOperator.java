package cocktail.service.snippet.dao;

import cocktail.service.snippet.entity.SnippetInfo;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;

/**
 * Created by jerikss3 on 2017-05-24.
 */
public enum QueryOperator {
  EQUALS("eq"),
  GREATER_THAN("gt"),
  LESS_THAN("lt");

  private final String op;

  private QueryOperator(String op) {
    this.op = op;
  }

  public String getOp() {
    return op;
  }
}
