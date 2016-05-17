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
 * @since 2016-04-18
 */

package cocktail.restful_web;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import cocktail.controller.Controller;
import cocktail.snippet.SnippetSet;
import cocktail.stream_io.XmlStreamer;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static spark.Spark.externalStaticFileLocation;
import static spark.Spark.get;
import static spark.Spark.post;

public class RestfulService {

  static {

    externalStaticFileLocation("/home/zen/Documents/School/LiA/jensGIT/recocktail/src/main/web");
//    externalStaticFileLocation("/root/github/recocktail/src/main/web");
//    staticFiles.location("/web");;
  }

  public static void runSpark() {
    Controller controller = Controller.getInstance();



    get("/getAllTags", (request, response) -> controller.getCompleteSetOfTagNames(),
        (src) -> {
          Gson gson = new Gson();
          return gson.toJson(src);
        });

    get("/getZipUrl", (request, response) -> {
      new File("src/main/web/tmp").mkdirs();
      String zipFileName = "src/main/web/tmp/download.zip";
      SnippetSet snippetSet = controller.getCurrentSet();
      String tmpZipName = controller.getZippedFiles(snippetSet);
      Files.move(Paths.get(tmpZipName), Paths.get(zipFileName), REPLACE_EXISTING);
      Gson gson = new Gson();
      return gson.toJson(zipFileName);
    });

    post("/writeSnippet", (request, response) -> {
      String fileName = "src/main/web/tmp/arkiv.zip";
      FileOutputStream fos = new FileOutputStream(fileName);
      fos.write(request.bodyAsBytes());
      fos.close();
      controller.writeEditSnippet(fileName);
      return true;
    });

    post("/search", (request, response) -> {

      Map<String, String> reqBodyMap = RestfulHelper.mapFromRequestBody(request);
      Set<String> existingKeys = reqBodyMap.keySet();

      double maxLength = 0;
      boolean exclusiveSearch = false;

      if (existingKeys.contains("maxLength")) {
        maxLength = Double.parseDouble(reqBodyMap.get("maxLength"));
      }

      if (existingKeys.contains("exclusive")) {
        exclusiveSearch = Boolean.parseBoolean(reqBodyMap.get("exclusive"));
      }

      if (existingKeys.contains("tagNames")) {
        List<String> tagList =
            Arrays.asList(reqBodyMap.get("tagNames").split("\\+"))
                .stream()
                .collect(Collectors.toList());
        SnippetSet snippetSet = controller.searchSnippetSet(tagList, maxLength, exclusiveSearch);
        System.out.println("Snippet set: " + snippetSet);
        Gson gson = new Gson();
        return gson.toJson(snippetSet);
      } else {
        return true;
      }
    });

    post("/getSnippetSetXml", ((request, response) -> {

      Gson gson = new Gson();
      XmlStreamer<SnippetSet> xmlStreamer = new XmlStreamer<>();

      SnippetSet snippetSet = gson.fromJson(request.body(), SnippetSet.class);
      String fileName = "SnippetSet.xml";
      String filePath = "src/main/web/tmp/" + fileName;
      File file = new File(filePath);
      xmlStreamer.toStream(SnippetSet.class, snippetSet, file);
      return gson.toJson(fileName);
    }));

    get("/getSets", (request, response) -> {
      List<String> setList = controller.getAllSavedSetsName();
      Gson gson = new Gson();
      return gson.toJson(setList);
    });
  }
}



