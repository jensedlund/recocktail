package cocktail.restful_web;


/**
 Copyright 2016 Jens Edlund, Joakim Gustafson, Jonas Beskow, Ulrika Goloconda Fahlen, Jan Eriksson, Marcus Viden
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import spark.Request;

public abstract class RestfulHelper {

  private RestfulHelper() {}

  public static String[] extractStrings(String string) {
    String[] returnList = string.split("&");
    return returnList;
  }

  public static Map<String, String> createMapFromRequest(String[] listOfPairs) {
    Map<String, String> returnMap = new HashMap<>();
    String[] tempArray = null;
    for (String s : listOfPairs) {
      tempArray = s.split("=");
      returnMap.put(tempArray[0], tempArray[1]);
    }
    return returnMap;
  }

  public static Map<String, String> mapFromRequestBody(Request request) {
    Map<String, String> reqBodyMap =
        Arrays.asList(request.body().split("&"))
            .stream()
            .map(str -> str.split("="))
            .filter(str -> str.length > 1)
            .collect(Collectors.toMap(str -> str[0], str -> str[1]));
    return reqBodyMap;
  }

}