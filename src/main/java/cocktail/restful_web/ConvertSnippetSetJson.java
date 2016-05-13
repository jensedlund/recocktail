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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import cocktail.snippet.SnippetInfo;
import cocktail.snippet.SnippetSet;

public class ConvertSnippetSetJson {

    public static JsonObject convertToJson(SnippetSet snippetSet){
        JsonObject jsonSnippetSet = new JsonObject();
        JsonArray operationLog = new JsonArray();
        JsonArray tagsInSet = new JsonArray();
        JsonArray snippetInfoArray = new JsonArray();
//        System.out.println(snippetSet.getSnippetCollection() + " snippetcollection ");
        for(SnippetInfo si : snippetSet.getSnippetCollection()){
//            System.out.println("hur många gåner ");
            snippetInfoArray.add(ConvertSnippetInfoJson.convertToJsonObject(si));
        }
//        System.out.println(snippetInfoArray.size() + " storleken på snippetInfoArrayen ");

        for(String s : snippetSet.getOperationLog()){ //TODO denna convertering måste kunna gå att göra bättre
            operationLog.add(s);
        }

        for(String s : snippetSet.getTagsInSet()){
            tagsInSet.add(s);
        }
        jsonSnippetSet.addProperty("id", snippetSet.getSetName());
        jsonSnippetSet.add("operationLog",operationLog);
        jsonSnippetSet.addProperty("maxLenSec", snippetSet.getMaxLenSec());
        jsonSnippetSet.addProperty("minLenSec", snippetSet.getMinLenSec());
        jsonSnippetSet.addProperty("avgLenSec", snippetSet.getAvgLenSec());
        jsonSnippetSet.addProperty("numSnippets", snippetSet.getNumSnippets());
        jsonSnippetSet.add("tagsInSet", tagsInSet);
        jsonSnippetSet.add("snippetCollection", snippetInfoArray );
        return jsonSnippetSet;
    }

   /* Gson gson = new GsonBuilder().create();
    gson.toJson("Hello", System.out);
    gson.toJson(123, System.out);
    RestfulService.restTest();

    private String id;
    private SortedSet<SnippetInfo> snippetCollection;
    private List<String> operationLog;
    private double maxLenSec;
    private double minLenSec;
    private double avgLenSec;
    private int numSnippets;
    private Set<String> tagsInSet;


    Set<String> tagsInSet = new HashSet<>();
    JsonArray jsonArray = new JsonArray();
    JsonArray setArray = new JsonArray();
    for(String s : tagsInSet) {
        setArray.add(s);
    }

    jsonArray.add("TagName");

    JsonObject json = new JsonObject();
    json.addProperty("id",25);
    json.addProperty("maxLenSec",2.5);
    json.add("operationLog",jsonArray);



    SnippetSet snippetset = new Gson().fromJson(json, SnippetSet.class);
    */

}
