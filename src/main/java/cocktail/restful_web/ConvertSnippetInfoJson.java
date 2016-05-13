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

public class ConvertSnippetInfoJson {
 /*   private int snippetID;
    private int fileID;
    private String fileName;

    private List<String> tagNames;

    private int kbSize;
    private double startTime;
    private double lengthSec;

    private LocalDate creationDate;
    private LocalDate lastModified;

    private int userID;
    private String userName;

    private int multiples;
    */

    public static JsonObject convertToJsonObject(SnippetInfo snippetInfo){
        JsonArray tagNames = new JsonArray();



        for(String s : snippetInfo.getTagNames()){
            tagNames.add(s);
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("snippetID", snippetInfo.getSnippetID());
        jsonObject.addProperty("fileID", snippetInfo.getFileID());
        jsonObject.addProperty("fileName", snippetInfo.getFileName());
        jsonObject.add("tagNames", tagNames);
        jsonObject.addProperty("kbSize", snippetInfo.getKbSize());
        jsonObject.addProperty("startTime", snippetInfo.getStartTime());
        jsonObject.addProperty("lengthSec", snippetInfo.getLengthSec());
        jsonObject.addProperty("userID", snippetInfo.getUserID());
        jsonObject.addProperty("userName", snippetInfo.getUserName());
        jsonObject.addProperty("creationDate", snippetInfo.getCreationDate().toString());
        jsonObject.addProperty("lastModified", snippetInfo.getLastModified().toString());

        return jsonObject;

    }
}
