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
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cocktail.controller.Controller;
import cocktail.snippet.SnippetSet;
import cocktail.stream_io.XmlStreamer;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

/**
 * REST service to handle frontend to backend calls.
 * Based on Spark Framework. The built in Jetty server
 * is used for web.
 */
public class RestfulService {

  static {
    // Static web resources
    boolean localhost = true;
    if (localhost) {
      String projectDir = System.getProperty("user.dir");
      String staticDir = "/src/main/web";
      staticFiles.externalLocation(projectDir + staticDir);
    } else {
      staticFiles.location("/public");
    }

  }

  /**
   * REST resolution is handled in this method.
   * Gson is used extensively to convert java objects to JSON objects
   * for convenience in frontend.
   */
  public static void runSpark() {
    Controller controller = Controller.getInstance();

    // Return all tags that exist in database
    get("/getAllTags", (request, response) -> controller.getCompleteSetOfTagNames(),
        (src) -> {
          Gson gson = new Gson();
          return gson.toJson(src);
        });

    // Move generated zip to stic file location and return the URL.
    get("/getZipUrl/:name", (request, response) -> {
      String setName = request.params(":name");
      SnippetSet snippetSet = controller.getStoredSet(setName);

      new File("src/main/web/tmp").mkdirs();
      String zipFileName = "src/main/web/tmp/download.zip";
      String tmpZipName = controller.getZippedFiles(snippetSet);
      Files.move(Paths.get(tmpZipName), Paths.get(zipFileName), REPLACE_EXISTING);
      controller.deleteUsedZip(tmpZipName);
      Gson gson = new Gson();
      return gson.toJson(new String("tmp/download.zip"));
    });

    // Get a zip file as binary stream from frontend
    // Store it to a static location and pass the path to
    // controller for processing.
    post("/writeSnippet", (request, response) -> {
      String fileName = "src/main/web/tmp/arkiv.zip";
      FileOutputStream fos = new FileOutputStream(fileName);
      fos.write(request.bodyAsBytes());
      fos.close();
      controller.writeEditSnippet(fileName);
      return true;
    });

    // Search in database and return a snippetset from the result.
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
        List<String> tagList = new ArrayList<>();
        for(String s : reqBodyMap.get("tagNames").split("\\+")) {
          tagList.add(URLDecoder.decode(s, "UTF-8"));
        }

        SnippetSet snippetSet = controller.searchSnippetSet(tagList, maxLength, exclusiveSearch);
        snippetSet.updateDerivedFields();
        Gson gson = new Gson();
        return gson.toJson(snippetSet);
      } else {
        return true;
      }
    });


    // Remove snippet from a set.
//    todo
    post("/removeSnippet", (request, response) -> {
      Map<String, String> reqBodyMap = RestfulHelper.mapFromRequestBody(request);
      Set<String> existingKeys = reqBodyMap.keySet();

      int snippetId;
      String snippetSetName;

      if (existingKeys.contains("snippetId") && existingKeys.contains("snippetSetName")) {
        snippetId = Integer.parseInt(reqBodyMap.get("snippetId"));
        snippetSetName = reqBodyMap.get("snippetSetName");
        SnippetSet snippetSet = controller.getStoredSet(snippetSetName);
        snippetSet.removeSnippet(snippetId);
        Gson gson = new Gson();
        return gson.toJson(snippetSet);
      } else {
        return true;
      }
    });

    // Pass a snippetset from frontend and return a xml file URL
    post("/getSnippetSetXml", ((request, response) -> {
      new File("src/main/web/tmp").mkdirs();
      Gson gson = new Gson();
      XmlStreamer<SnippetSet> xmlStreamer = new XmlStreamer<>();

      SnippetSet snippetSet = gson.fromJson(request.body(), SnippetSet.class);
      String fileName = "SnippetSet.xml";
      String filePath = "src/main/web/tmp/" + fileName;
      File file = new File(filePath);
      xmlStreamer.toStream(SnippetSet.class, snippetSet, file);
      return gson.toJson(fileName);
    }));

    // Return a list of all sets in StorgaeUnit.
    get("/getActiveSets", (request, response) -> {
      List<String> setList = controller.getAllSavedSetsName();
      Gson gson = new Gson();
      return gson.toJson(setList);
    });

    // Get a specific set from StorageUnit and return as JSON.
    get("/getSet/:name", (request, response) -> {
      String setName = request.params(":name");
      SnippetSet snippetSet = controller.getStoredSet(setName);
      Gson gson = new Gson();
      return gson.toJson(snippetSet);
    });

  }
}



