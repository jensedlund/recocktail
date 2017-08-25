package cocktail.service.snippet.controller;

import cocktail.service.snippet.dao.GenericDao;
import cocktail.service.snippet.entity.SnippetInfo;
import com.google.gson.Gson;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.List;

/**
 * Created by jerikss3 on 2017-06-02.
 */
public class GetSnippetList implements Route {

  private final GenericDao<SnippetInfo> dao;

  public GetSnippetList(GenericDao<SnippetInfo> dao) {
    this.dao = dao;
  }

  @Override
  public Object handle(Request request, Response response) throws Exception {
    List<SnippetInfo> snippetInfoList = dao.getAll();
    return snippetInfoList;
  }
}
