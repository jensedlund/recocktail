/*
 * Copyright 2016 Jens Edlund, Joakim Gustafson, Jonas Beskow, Ulrika Goloconda Fahlen, Jan Eriksson, Marcus Viden
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @version 1.0
 * @since 2016-03-16
 */

package cocktail;


import cocktail.restful_web.RestfulService;
import cocktail.service.snippet.controller.GetSnippet;
import cocktail.service.snippet.dao.GenericDao;
import cocktail.service.snippet.dao.MorphiaSnippetInfoDao;
import cocktail.service.snippet.entity.SnippetInfo;
import spark.Spark;

public class CocktailBackend {
  public static void main(String[] args) {

    GenericDao<SnippetInfo> snippetInfoDao = new MorphiaSnippetInfoDao();
    GetSnippet getSnippet = new GetSnippet(snippetInfoDao);

    Spark.get("/snippets", getSnippet::handle);

  }

  public static void initApplication(){
    RestfulService.runSpark();
  }
}

