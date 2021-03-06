package cocktail.db_access;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 ** Copyright 2016 Jens Edlund, Joakim Gustafson, Jonas Beskow, Ulrika Goloconda Fahlen, Jan Eriksson, Marcus Viden
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

/**
 * DbAccessHandler is used to read information from a textfile where password, username, ip-adress and the name of the database is stored.
 * The textfile is not added to git repository.
 */
public class DbAccessHandler {
    private static Map<String, String> accessMap;

    private static void getInfoFromFile() {
        accessMap = new HashMap<>();

        try {
            String[] temp = new String[2];
            FileReader fileReader = new FileReader("./src/main/java/cocktail/db_access/creds.txt");
            BufferedReader bR = new BufferedReader(fileReader);
            String str;
            int i = 0;
            while ((str = bR.readLine()) != null) {
                temp = str.split(":");
                accessMap.put(temp[0], temp[1]);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getAccessInfo(String string) {
        String returnString = null;
        if (accessMap == null) {
            getInfoFromFile();
            returnString = accessMap.get(string);

        } else {
            returnString = accessMap.get(string);
        }
        return returnString;
    }
}