package cocktail.db_access;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
public class DbAccessHandler {


    public static String getPassword(){
        String[] password = new String[2];
        try {
            FileReader fileReader = new FileReader("./src/main/java/cocktail/db_access/recocktailCreds.txt");
            BufferedReader bR = new BufferedReader(fileReader);
            String str;
            int i = 0;
            while ((str = bR.readLine()) != null){
                if(str.contains("password")) {
                    password = str.split(".*\\:");
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return password[1];
    }



    public static String getUserName(){
        String[] userName = new String[2];
        try {
            FileReader fileReader = new FileReader("./src/main/java/cocktail/db_access/recocktailCreds.txt");
            BufferedReader bR = new BufferedReader(fileReader);
            String str;
            int i = 0;
            while ((str = bR.readLine()) != null){
                if(str.contains("username")) {
                    userName = str.split(".*\\:");
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return userName[1];
    }


    public static String getIp(){
        String[] ip = new String[2];
        try {
            FileReader fileReader = new FileReader("./src/main/java/cocktail/db_access/recocktailCreds.txt");
            BufferedReader bR = new BufferedReader(fileReader);
            String str;
            int i = 0;
            while ((str = bR.readLine()) != null){
                if(str.contains("ip")) {
                    ip = str.split(".*\\:");
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return ip[1];
    }


    public static String getDbName(){
        String[] name = new String[2];
        try {
            FileReader fileReader = new FileReader("./src/main/java/cocktail/db_access/recocktailCreds.txt");
            BufferedReader bR = new BufferedReader(fileReader);
            String str;
            int i = 0;
            while ((str = bR.readLine()) != null){
                if(str.contains("dbname")) {
                    name = str.split(".*\\:");
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
        return name[1];
    }
}