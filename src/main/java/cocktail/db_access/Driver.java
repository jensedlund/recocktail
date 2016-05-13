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
 * @since 2016-03-25
 */

package cocktail.db_access;

import cocktail.snippet.FileInfo;
import cocktail.snippet.SnippetInfo;
import cocktail.snippet.SnippetSet;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.sql.*;
import java.util.*;

public class Driver {
  private static Connection myConnection;
  private static Statement myStatment;
  private static String _adminUserName;
  static {
    _adminUserName = "Admin";
  }

  public static void setAdminUserName(String adminUserName){
    _adminUserName = adminUserName;
  }

  public static String getAdminUserName(){
    return _adminUserName;
  }
  public static boolean connectToMySql() {
    boolean returnBool = false;

    try {
      myConnection = DriverManager.getConnection("jdbc:mysql://130.237.67.145:3306/recocktail?aoutoReconnect=true&useSSL=false", DbAccessHandler.getUserName(), DbAccessHandler.getPassword());
      myStatment = myConnection.createStatement();
      returnBool = true;

    } catch (Exception e) {
      System.out.println(myConnection);
      e.printStackTrace();

    }
    return returnBool;
  }

  private static List<String> removeUnwantedCharacters(List<String> tagNames){
    List<String> newTagList = new ArrayList<>();
   for(String s : tagNames) {
   newTagList.add(s.replaceAll("[^\\.\\_\\-åäö\\w]",""));
   }
    return newTagList;
  }

  private static void tagsToLowerCase(SnippetInfo snippetInfo){
    List<String> tempTagNames = new ArrayList<>();

    int size = snippetInfo.getTagNames().size();
   for (int i = 0; i < size; i++ ) {
     String temp = snippetInfo.getTagNames().get(i).toLowerCase();
     tempTagNames.add(temp);
   }
   snippetInfo.getTagNames().clear();
    snippetInfo.getTagNames().addAll(tempTagNames);
  }

  private static void removeProtectedTags(SnippetInfo snippetInfo){
    List<String> listToDel = new ArrayList<>();
    for(String s : snippetInfo.getTagNames()){
      if(s.charAt(0)=='.'){
        listToDel.add(s);
      }
    }
    snippetInfo.getTagNames().removeAll(listToDel);

  }

  public static boolean isCallProtectedAdmin(SnippetInfo snippetInfo){
    boolean isAdmin;
    if(snippetInfo.getUserName().equals(_adminUserName)){
      isAdmin = true;
    } else {
      isAdmin = false;
    }
    return isAdmin;
  }

  public static int writeSnippet(SnippetInfo snippetInfo, int fileID) {
    int returnInt = 0;
    if (!isCallProtectedAdmin(snippetInfo)) {
      removeProtectedTags(snippetInfo);
      tagsToLowerCase(snippetInfo);
      List<String> newTagList = removeUnwantedCharacters(snippetInfo.getTagNames());
      snippetInfo.getTagNames().clear();
      snippetInfo.getTagNames().addAll(newTagList);
      snippetInfo.setFileID(fileID);
      insertIntoUserInfo(snippetInfo);
      insertIntoSnippetinfo(snippetInfo, fileID);
      insertIntoTagInfo(snippetInfo);
      insertIntoBrigeTable(snippetInfo);
      returnInt = snippetInfo.getSnippetID();
      return returnInt;
    }
    return returnInt;
  }

  public static boolean isSnippetADuplicate(SnippetInfo snippetInfo, FileInfo fileInfo){
    return false;
  }

  public static boolean isFileInDb(FileInfo fileInfo){
return false;
  }
  public static int writeSnippet(FileInfo fileInfo, SnippetInfo snippetInfo) {
    int returnInt = 0;
    if(isCallProtectedAdmin(snippetInfo)){
      returnInt = writeSnippetAsAdmin(snippetInfo,fileInfo);
    } else {
      removeProtectedTags(snippetInfo);
      tagsToLowerCase(snippetInfo);
      List<String> newTagList = removeUnwantedCharacters(snippetInfo.getTagNames());
      snippetInfo.getTagNames().clear();
      snippetInfo.getTagNames().addAll(newTagList);

      insertIntoFileInfo(snippetInfo, fileInfo);

      insertIntoUserInfo(snippetInfo);

      insertIntoSnippetinfo(snippetInfo, fileInfo.getFileID());

      insertIntoTagInfo(snippetInfo);

      insertIntoBrigeTable(snippetInfo);

      returnInt = snippetInfo.getSnippetID();
    }
    return returnInt;
  }


  public static boolean insertIntoFileInfo(SnippetInfo snippetInfo, FileInfo fileInfo) {
    boolean returnBool = false;
//TODO här ska en koll göras ju
    if (!getAllFileNames().contains(snippetInfo.getFileName())) {
      try {
        PreparedStatement ps = myConnection.prepareStatement
            ("INSERT INTO fileInfo (file,fileName,fileSizeKb,fileLenSec) VALUES(?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
        ps.setBinaryStream(1, fileInfo.getInputStream());
        ps.setString(2, snippetInfo.getFileName());
        ps.setInt(3, fileInfo.getFileSizeKb());
        ps.setDouble(4, fileInfo.getFileLenSec());
        ps.executeUpdate();
        ResultSet tableKeys = ps.getGeneratedKeys();
        if (tableKeys.next()) {
          fileInfo.setFileID(tableKeys.getInt(1));
        }
        ps.close();
        tableKeys.close();
        fileInfo.getInputStream().close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      fileInfo.setFileID(getFileIDFromFileName(snippetInfo.getFileName()));
      returnBool = true;
    }
    return returnBool;
  }


  public static int getFileIDFromFileName(String fileName) {
    int fileID = 0;

    try {
      String sql = "SELECT fileID FROM fileInfo WHERE fileName =?";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ps.setString(1, fileName);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        fileID = rs.getInt("fileID");
      }
      ps.close();
      rs.close();
      return fileID;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return fileID;
  }


  public static List<String> getAllUsers() {
    List<String> userLIst = new ArrayList<>();
    try {
      String spl = "SELECT userName FROM userInfo";
      PreparedStatement ps = myConnection.prepareStatement(spl);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        userLIst.add(rs.getString("userName"));
      }
      ps.close();
      rs.close();
      return userLIst;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }


  private static boolean insertIntoUserInfo(SnippetInfo snippetInfo) {
    boolean returnBool = false;
    List<String> userList = getAllUsers();
    if (!userList.contains(snippetInfo.getUserName())) {

      try {
        PreparedStatement ps = myConnection.prepareStatement("INSERT INTO userInfo (userName) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, snippetInfo.getUserName());
        ps.executeUpdate();
        ResultSet tableKeys = ps.getGeneratedKeys();
        tableKeys.next();
        snippetInfo.setUserID(tableKeys.getInt(1));
        ps.close();
        tableKeys.close();
        returnBool = true;
      } catch (Exception e) {
        e.printStackTrace();
      }
    } else {
      try {
        String sql = "SELECT userID FROM userInfo WHERE userName=?";
        PreparedStatement ps = myConnection.prepareStatement(sql);
        ps.setString(1, snippetInfo.getUserName());
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
          snippetInfo.setUserID(rs.getInt("userID"));
        }
        ps.close();
        rs.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return returnBool;
  }

  public static boolean insertIntoBrigeTable(SnippetInfo snippetInfo) {
    boolean returBool = false;
    for (int i = 0; i < snippetInfo.getTagIDs().size(); i++) {
      try {
        PreparedStatement ps = myConnection.prepareStatement("INSERT INTO bridgeSnippetTagTable(snippetID,tagID) VALUES(?,?)");
        ps.setInt(1, snippetInfo.getSnippetID());
        ps.setInt(2, snippetInfo.getTagIDs().get(i));
        ps.executeUpdate();
        ps.close();
        returBool = true;
      } catch (Exception e) {
        returBool = false;
        e.printStackTrace();
      }
    }
    return returBool;
  }

  public static boolean insertIntoTagInfo(SnippetInfo snippetInfo) {
    boolean returnBool = false;
    List<Integer> tagIDs = new ArrayList<>();
    List<String> tagNames = snippetInfo.getTagNames();

    for (int i = 0; i < tagNames.size(); i++) {
      if (!getAllTagNames().contains(tagNames.get(i))) {
        try {
          PreparedStatement ps = myConnection.prepareStatement("INSERT INTO tagInfo (tagName) VALUES(?)", Statement.RETURN_GENERATED_KEYS);
          ps.setString(1, tagNames.get(i));
          ps.executeUpdate();
          ResultSet tableKeys = ps.getGeneratedKeys();
          tableKeys.next();
          tagIDs.add(tableKeys.getInt(1));
          ps.close();
          tableKeys.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      } else {
        tagIDs.add(getTagID(tagNames.get(i)));
      }
    }
    snippetInfo.setTagIDs(tagIDs);
    return returnBool;
  }

  private static boolean insertIntoSnippetinfo(SnippetInfo snippetInfo, int fileID) {
    removeProtectedTags(snippetInfo);
    boolean returnBool = false;
    java.sql.Date date = java.sql.Date.valueOf(snippetInfo.getCreationDate());
    java.sql.Date date2 = java.sql.Date.valueOf(snippetInfo.getLastModified());
    try {
      PreparedStatement ps = myConnection.prepareStatement
          ("INSERT INTO snippetInfo (fileID,sizeKb,startTime,lenSec,creationDate,lastModifiedDate,userID) VALUES(?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
      ps.setInt(1, fileID);
      ps.setInt(2, snippetInfo.getKbSize());
      ps.setDouble(3, snippetInfo.getStartTime());
      ps.setDouble(4, snippetInfo.getLengthSec());
      ps.setDate(5, date);
      ps.setDate(6, date2);
      ps.setInt(7, snippetInfo.getUserID());
      ps.executeUpdate();
      ResultSet tableKeys = ps.getGeneratedKeys();
      tableKeys.next();
      snippetInfo.setSnippetID(tableKeys.getInt(1));
      ps.close();
      tableKeys.close();
    } catch (Exception ex) {
      ex.printStackTrace();
    }

    return returnBool;
  }

  private static List<String> getAllFileNames() {
    List<String> returnList = new ArrayList<>();
    try {
      String sql = "SELECT fileName FROM fileInfo";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        returnList.add(rs.getString("fileName"));
      }
      ps.close();
      rs.close();
      return returnList;

    } catch (Exception e) {
      e.printStackTrace();
    }
    return returnList;
  }

  public static List<String> getAllTagNames() {
    List<String> tempListOfTagNames = new ArrayList<>();
    try {
      String sql = "SELECT tagName FROM tagInfo";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {

        tempListOfTagNames.add(rs.getString("tagName"));
      }

      ps.close();
      rs.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    return tempListOfTagNames;
  }

  public static int getTagID(String tagName) {
    int returnInt = 0;
    try {
      String sql = ("SELECT tagID FROM tagInfo WHERE tagName=?");
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ps.setString(1, tagName);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        returnInt = rs.getInt("tagID");
      }
      ps.close();
      rs.close();
      return returnInt;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return returnInt;
  }

  private static boolean updateFileInfo(FileInfo fileInfo) {
    boolean returnBool = false;
    try {
      String sql = "UPDATE fileInfo SET fileName=?,fileSizeKb=?,fileLenSec=?,file=? WHERE fileID=?";
      PreparedStatement psFileInfo = myConnection.prepareStatement(sql);
      psFileInfo.setString(1, fileInfo.getFileName());
      psFileInfo.setInt(2, fileInfo.getFileSizeKb());
      psFileInfo.setDouble(3, fileInfo.getFileLenSec());
      psFileInfo.setBinaryStream(4, fileInfo.getInputStream());
      psFileInfo.setInt(5, fileInfo.getFileID());
      psFileInfo.executeUpdate();
      returnBool = true;
      psFileInfo.close();
      fileInfo.getInputStream().close();
      return returnBool;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return returnBool;
  }

  public static boolean deleteInsertSnippetInfo(FileInfo fileInfo, SnippetInfo snippetInfo, int snippetID) {
    boolean returnBool = false;
    if (isCallProtectedAdmin(snippetInfo)) {
      returnBool = deleteInsertAsAdmin(snippetInfo, fileInfo, snippetID);
    } else {
      removeProtectedTags(snippetInfo);
      deleteSnippet(snippetID);
      snippetInfo.setSnippetID(snippetID);
      java.sql.Date dateCreate = java.sql.Date.valueOf(snippetInfo.getCreationDate());
      java.sql.Date dateModified = java.sql.Date.valueOf(snippetInfo.getLastModified());
      insertIntoFileInfo(snippetInfo, fileInfo);
      insertIntoUserInfo(snippetInfo);
      try {
        String sql = "INSERT INTO snippetInfo (fileID,sizeKb,startTime,lenSec,creationDate,lastModifiedDate,userID, snippetID) VALUES(?,?,?,?,?,?,?,?)";
        PreparedStatement ps = myConnection.prepareStatement(sql);
        ps.setInt(1, fileInfo.getFileID());
        ps.setInt(2, snippetInfo.getKbSize());
        ps.setDouble(3, snippetInfo.getStartTime());
        ps.setDouble(4, snippetInfo.getLengthSec());
        ps.setDate(5, dateCreate);
        ps.setDate(6, dateModified);
        ps.setInt(7, snippetInfo.getUserID());
        ps.setInt(8, snippetID);
        ps.executeUpdate();
        insertIntoTagInfo(snippetInfo);
        insertIntoBrigeTable(snippetInfo);
        ps.close();
        returnBool = true;
        return returnBool;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return returnBool;
  }

  public static boolean deleteInsertAsAdmin(SnippetInfo snippetInfo, FileInfo fileInfo, int snippetID) {
    boolean returnBool = false;
    if(snippetInfo.getUserName().equals(_adminUserName)){
     deleteSnippetAdm(snippetInfo.getSnippetID());
      snippetInfo.setSnippetID(snippetID);
      java.sql.Date dateCreate = java.sql.Date.valueOf(snippetInfo.getCreationDate());
      java.sql.Date dateModified = java.sql.Date.valueOf(snippetInfo.getLastModified());
      insertIntoFileInfo(snippetInfo, fileInfo);
      insertIntoUserInfo(snippetInfo);
      try {
        String sql = "INSERT INTO snippetInfo (fileID,sizeKb,startTime,lenSec,creationDate,lastModifiedDate,userID, snippetID) VALUES(?,?,?,?,?,?,?,?)";
        PreparedStatement ps = myConnection.prepareStatement(sql);
        ps.setInt(1, fileInfo.getFileID());
        ps.setInt(2, snippetInfo.getKbSize());
        ps.setDouble(3, snippetInfo.getStartTime());
        ps.setDouble(4, snippetInfo.getLengthSec());
        ps.setDate(5, dateCreate);
        ps.setDate(6, dateModified);
        ps.setInt(7, snippetInfo.getUserID());
        ps.setInt(8, snippetID);
        ps.executeUpdate();
        insertIntoTagInfo(snippetInfo);
        insertIntoBrigeTable(snippetInfo);
        ps.close();
        returnBool = true;
        return returnBool;
      } catch (Exception e) {
        e.printStackTrace();
      }

    }
    return returnBool;
  }


  public static boolean deleteSnippetAdm(int snippetID) {
    boolean returnBool;
    String userName = getUserNameForSnippet(snippetID);
    if(userName.equals(_adminUserName)) {
      int fileID = getFileID(snippetID);
      List<Integer> tagIdsSnippet = getTagIDsForSnippetID(snippetID);

      deleteFromBridgeTable(snippetID);
      deleteFromSnippetInfo(snippetID);
      if (!isFileInUse(fileID)) {
        deleteFromFileInfo(fileID);
      }
      for (int i : tagIdsSnippet) {
        if (!isTagInUse(i)) {
          deleteFromTagInfo(i);
        }
      }
      returnBool = true;
    } else {
      returnBool = false;
    }
    return returnBool;
  }

  private static boolean updateTagInfo(SnippetInfo snippetInfo) {
    boolean returnBool = false;
    for (String s : snippetInfo.getTagNames()) {
      int tagID = getTagID(s);
      try {
        String sql = "UPDATE tagInfo SET tagName=? WHERE tagID=?";
        PreparedStatement ps = myConnection.prepareStatement(sql);
        ps.setString(1, s);
        ps.setInt(2, tagID);
        ps.close();
        returnBool = true;
        return true;
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    return returnBool;
  }

  public static int getFileID(int snippetID) {
    int returnInt = 0;
    try {
      String sql = "SELECT fileID FROM snippetInfo WHERE snippetID=?";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ps.setInt(1, snippetID);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        returnInt = rs.getInt("fileID");
      }
      ps.close();
      rs.close();
      return returnInt;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return returnInt;
  }

  public static List<Integer> getTagIDsForSnippetID(int snippetID) {
    List<Integer> tagIDList = new ArrayList<>();

    try {
      String sql = "SELECT tagID from bridgeSnippetTagTable WHERE snippetID=?";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ps.setInt(1, snippetID);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        tagIDList.add(rs.getInt(1));
      }
      ps.close();
      rs.close();
      return tagIDList;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return tagIDList;
  }

  public static List<String> getTagNamesFromIDs(List<Integer> tagIDs) {
    List<String> tagNames = new ArrayList<>();
    try {
      for (int i = 0; i < tagIDs.size(); i++) {
        String sql = "SELECT tagName from tagInfo WHERE tagID=?";
        PreparedStatement ps = myConnection.prepareStatement(sql);
        ps.setInt(1, tagIDs.get(i));
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
          tagNames.add(rs.getString("tagName"));
        }
        ps.close();
        rs.close();
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return tagNames;
  }

  public static List<Integer> getAllFileIDs() {
    List<Integer> returnList = new ArrayList<>();
    try {
      String sql = "SELECT fileID FROM fileInfo";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        returnList.add(rs.getInt("fileID"));
      }
      ps.close();
      rs.close();
      return returnList;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private static boolean deleteFromBridgeTable(int snippetID) {
    boolean returnBool = false;
    int fileID = getFileID(snippetID);
    try {
      String sql = "DELETE FROM bridgeSnippetTagTable WHERE snippetID=?";
      PreparedStatement psBridgeTable = myConnection.prepareStatement(sql);
      psBridgeTable.setInt(1, snippetID);
      int rs = psBridgeTable.executeUpdate();
      psBridgeTable.close();
      returnBool = true;
      return returnBool;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return returnBool;
  }

  private static boolean deleteFromSnippetInfo(int snippetID) {
    boolean returnBool = false;
    try {
      String sql = "DELETE FROM snippetInfo WHERE snippetID=?";
      PreparedStatement psSnippetInfo = myConnection.prepareStatement(sql);
      psSnippetInfo.setInt(1, snippetID);
      int i = psSnippetInfo.executeUpdate();
      psSnippetInfo.close();

      returnBool = true;
      return returnBool;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return returnBool;
  }

  public static int getFileIDFromSnippetInfoTable(int snippetID) {
    int returnInt = 0;
    try {
      String sql = "SELECT fileID FROM snippetInfo WHERE snippetID=?";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ps.setInt(1, snippetID);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        returnInt = rs.getInt("fileID");
      }
      return returnInt;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return returnInt;
  }

  public static List<Integer> getAllSnippetIDs() {
    List<Integer> snippetIDs = new ArrayList<>();
    try {
      String sql = "SELECT snippetID FROM snippetInfo";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        snippetIDs.add(rs.getInt("snippetID"));
      }
      return snippetIDs;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return snippetIDs;
  }

  private static boolean isFileInUse(int fileID) {
    boolean returnBool = false;
    List<Integer> snippetIDs = getAllSnippetIDs();
    for (int sID : snippetIDs) {
      int existingFileID = getFileIDFromSnippetInfoTable(sID);
      if (existingFileID == fileID) {
        returnBool = true;
        return returnBool;
      }
    }
    return returnBool;
  }

  private static boolean deleteFromFileInfo(int fileID) {
    boolean returnBool = false;
    try {
      String sql = "DELETE FROM fileInfo WHERE fileID=?";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ps.setInt(1, fileID);
      int nrEfectedRow = ps.executeUpdate();
      returnBool = true;
      return returnBool;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return returnBool;
  }

  public static boolean isTagInUse(int tagID) {
    boolean returnBool = false;
    try {
      String sql = ("SELECT snippetID FROM bridgeSnippetTagTable WHERE tagID=?");
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ps.setInt(1, tagID);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        int snippetID = rs.getInt("snippetID");
      }
      returnBool = true;
      return returnBool;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return returnBool;
  }

  private static boolean deleteFromTagInfo(int tagID) {
    boolean returnBool = false;
    try {
      String sql = ("DELETE FROM tagInfo WHERE tagID=?");
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ps.setInt(1, tagID);
      int nrEffectedRow = ps.executeUpdate();
      returnBool = true;
      return returnBool;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return returnBool;
  }

  public static boolean isSnippetProtectedSample(int snippetID){
    boolean isProtected = false;
    List<Integer> id = new ArrayList<>();
    id.add(snippetID);
    List<String> tags = getTagNamesFromIDs(id);
    for(String tag : tags){
      if(tag.charAt(0)=='.'){
        isProtected = true;
      }
    }
    return isProtected;
  }

  public static boolean deleteSnippet(int snippetID) {
    boolean returnBool;
    String userName = getUserNameForSnippet(snippetID);
    if(!isSnippetProtectedSample(snippetID)|| userName.equals(_adminUserName)){
      int fileID = getFileID(snippetID);
      List<Integer> tagIdsSnippet = getTagIDsForSnippetID(snippetID);

      deleteFromBridgeTable(snippetID);
      deleteFromSnippetInfo(snippetID);
      if (!isFileInUse(fileID)) {
        deleteFromFileInfo(fileID);
      }
      for (int i : tagIdsSnippet) {
        if (!isTagInUse(i)) {
          deleteFromTagInfo(i);
        }
      }
      returnBool = true;
    } else {
      returnBool = false;
    }
    return returnBool;
  }

  public static byte[] readSnippet(int snippetID) {
    int sourceID = getFileID(snippetID);
    byte[] buffer = null;
    try {
      String sql = "SELECT file FROM fileInfo WHERE fileID=?";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ps.setInt(1, sourceID);
      ResultSet rs = ps.executeQuery();
      InputStream input;
      if (rs.next()) {
        input = rs.getBinaryStream("file");
        buffer = new byte[input.available()];
        input.read(buffer);
        System.out.println(buffer.length);
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    return buffer;
  }

  private static String getUserName(int userID) {
    String userName = null;
    try {
      String sql = "SELECT userName FROM userInfo WHERE userID=?";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ps.setInt(1, userID);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        userName = rs.getString("userName");
      }
      return userName;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return userName;
  }

  public static SnippetInfo readSnippetInf(int snippetID) {
    SnippetInfo snippetInfo = new SnippetInfo();
    snippetInfo.setSnippetID(snippetID);
    snippetInfo.setFileID(getFileID(snippetID));
    List<Integer> tagIDs = getTagIDsForSnippetID(snippetID);
    List<String> tagNames = getTagNamesFromIDs(tagIDs);
    snippetInfo.setTagNames(tagNames);

    try {
      String sql = "SELECT fileName FROM fileInfo WHERE fileID=?";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ps.setInt(1, snippetInfo.getFileID());
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        snippetInfo.setFileName(rs.getString("fileName"));
      }
      sql = "SELECT sizeKb,startTime,lenSec,creationDate,lastModifiedDate,userID FROM snippetInfo WHERE snippetID=?";
      ps = myConnection.prepareStatement(sql);
      ps.setInt(1, snippetID);
      rs = ps.executeQuery();
      if (rs.next()) {
        snippetInfo.setKbSize(rs.getInt("sizeKb"));
        snippetInfo.setLengthSec(rs.getDouble("lenSec"));
        snippetInfo.setCreationDate(rs.getDate("creationDate").toLocalDate());
        snippetInfo.setLastModified(rs.getDate("lastModifiedDate").toLocalDate());
        snippetInfo.setUserID(rs.getInt("userID"));
      }
      snippetInfo.setUserName(getUserName(snippetInfo.getUserID()));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return snippetInfo;
  }

  public static int getTotlNumberOfFiles() {
    int numberOfFiles = 0;
    try {
      String sql = "SELECT fileID FROM fileInfo";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        numberOfFiles++;
      }

      return numberOfFiles;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return numberOfFiles;
  }

  @NotNull
  public static Integer getTotalFileSizeKb() {
    int total = 0;
    try {
      String sql = "SELECT fileSizeKb FROM fileInfo";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        total += rs.getInt("fileSizeKb");

      }
      return total;

    } catch (Exception e) {
      e.printStackTrace();
    }
    return total;
  }

  public static Integer getMinFileSizeKb() {
    int min = 1000;
    int temp = 0;
    try {
      String sql = "SELECT fileSizeKb FROM fileInfo";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        temp = rs.getInt("fileSizeKb");
        if (temp < min) {
          min = temp;
        }
      }
      return min;

    } catch (Exception e) {
      e.printStackTrace();
    }
    return min;
  }

  public static Integer getMaxFileSizeKb() {
    int max = 0;
    int temp = 0;
    try {
      String sql = "SELECT fileSizeKb FROM fileInfo";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        temp = rs.getInt("fileSizeKb");
        if (temp > max) {
          max = temp;
        }
      }
      return max;

    } catch (Exception e) {
      e.printStackTrace();
    }
    return max;

  }

  public static double getMinFileLenSec() {
    double min = 0;
    double temp = 0;
    try {
      String sql = "SELECT fileLenSec FROM fileInfo";
      PreparedStatement ps = myConnection.prepareCall(sql);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        temp = rs.getDouble("fileLenSec");
        if (temp < min) {
          min = temp;
        }
      }
      return min;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }

  public static List<Integer> searchSnippetIDs(List<String> tagArray, double lengthMaxFilter) {
    List<String> tagLowerCas = new ArrayList<>();
    for(String tag : tagArray){
      tagLowerCas.add(tag.toLowerCase());
    }
    List<Integer> snippetIdList = new ArrayList<>();
    List<Integer> trimmedIdList = new ArrayList<>();
    List<Integer> tagIdList = new ArrayList<>();

    try {
      for (String tag : tagLowerCas) {
        String sql = "SELECT tagID FROM tagInfo WHERE tagName=?";
        PreparedStatement ps = myConnection.prepareStatement(sql);
        ps.setString(1, tag);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
          tagIdList.add(rs.getInt("tagID"));
        }
      }
      snippetIdList = getSnippetIDForTagID(tagIdList);
      trimmedIdList = fileterSnippetIDByLenght(snippetIdList, lengthMaxFilter);
      return trimmedIdList;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return snippetIdList;
  }

  private static List<Integer> fileterSnippetIDByLenght(List<Integer> snippetIdList, double lengthMaxFilter) {
    List<Integer> snippgetIDs = new ArrayList<>();
    for (int i : snippetIdList) {
      SnippetInfo snippetInfo = readSnippetInf(i);
      if (snippetInfo.getLengthSec() <= lengthMaxFilter) {
        snippgetIDs.add(i);
      }
    }
    return snippetIdList;
  }

  private static List<Integer> getSnippetIDForTagID(List<Integer> tagIDs) {
    List<Integer> snippetIDs = new ArrayList<>();

    try {
      for (int i : tagIDs) {
        String sql = "SELECT snippetID FROM bridgeSnippetTagTable WHERE tagID=?";
        PreparedStatement ps = myConnection.prepareStatement(sql);
        ps.setInt(1, i);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
          snippetIDs.add(rs.getInt("snippetID"));
        }
      }
      return snippetIDs;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static int getSnippetIDForTagID(int tagID) {
    int snippetID = 0;
    try {
      String sql = "SELECT snippetID FROM bridgeSnippetTagTable WHERE tagID=?";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ps.setInt(1, tagID);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        snippetID = rs.getInt("snippetID");
      }
      return snippetID;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }

  public static List<Integer> searchSnippetIDs(String tagName, double lengthMaxFilter) {
    List<Integer> snippetIDs = new ArrayList<>();
    tagName = tagName.toLowerCase();
    int tagID = getTagID(tagName);
    int snippetID = getSnippetIDForTagID(tagID);
    SnippetInfo snippetInfo = readSnippetInf(snippetID);
    if(lengthMaxFilter<=0){
      snippetIDs.add(snippetID);
    }else if (snippetInfo.getLengthSec() <= lengthMaxFilter) {
      snippetIDs.add(snippetID);
    }
    return snippetIDs;
  }

  public static Double getMaxFileLenSec() {
    double max = 0;
    double temp = 0;
    try {
      String sql = "SELECT fileLenSec FROM fileInfo";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        temp = rs.getDouble("fileLenSec");
        if (temp > max) {
          max = temp;
        }
      }
      return max;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0.0;
  }

  public static int getNumberOfSnippets() {
    int numberOfSnippets = 0;
    try {
      String sql = "SELECT COUNT(*) FROM snippetInfo";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        numberOfSnippets = rs.getInt(1);
      }
      return numberOfSnippets;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }

  public static Double getMinSnippetLenSec() {
    double returnDouble = 0.0;
    double tempDouble = 0.0;
    try {
      String sql = "SELECT fileLenSec FROM fileInfo";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        tempDouble = rs.getDouble("fileLenSec");
        if (tempDouble < returnDouble) {
          returnDouble = tempDouble;
        }
      }
      return returnDouble;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0.0;
  }

  public static Double getMaxSnippetLenSec() {
    double maxSnippetLen = 0.0;
    double temp = 0.0;
    try {
      String sql = "SELECT lenSec FROM snippetInfo";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        temp = rs.getDouble("lenSec");
        if (temp > maxSnippetLen) {
          maxSnippetLen = temp;
        }
      }
      return maxSnippetLen;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0.0;
  }

  public static Integer getOccuranceOfTag(String tagName) {
    int tagID = getTagID(tagName);
    int occurance = 0;
    try {
      String sql = "SELECT COUNT(*) FROM bridgeSnippetTagTable WHERE tagID=?";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ps.setInt(1,tagID);
      ResultSet rs = ps.executeQuery();
      while (rs.next()){
        occurance = rs.getInt(1);
      }
      return occurance;


     /* String sql = "SELECT snippetID FROM bridgeSnippetTagTable WHERE tagID=?";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ps.setInt(1, tagID);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        occurance++;
      }
      return occurance;
      */
    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }

  public static ArrayList<String> getComplementaryTags(String tag) {
    ArrayList<String> complementaryTags = new ArrayList<>();
    ArrayList<String> tempComplementaryTags = new ArrayList<>();

    ArrayList<Integer> tempSnippetIDs = new ArrayList<>();
    ArrayList<Integer> tempTagIDs = new ArrayList<>();
    try {
      int tagID = getTagID(tag);
      String sql = "SELECT snippetID FROM bridgeSnippetTagTable WHERE tagID=?";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ps.setInt(1, tagID);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {

        tempSnippetIDs.add(rs.getInt("snippetID"));
      }

      for (int i : tempSnippetIDs) {
        tempTagIDs.addAll(getTagIDsForSnippetID(i));
      }

      tempComplementaryTags.addAll(getTagNamesFromIDs(tempTagIDs));
      complementaryTags = tempComplementaryTags;
      for (int i = 0; i < tempComplementaryTags.size(); i++) {
        if (tempComplementaryTags.get(i).equals(tag)) {
          complementaryTags.remove(i);
        }
      }
      return complementaryTags;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static boolean isSnippetPartOfLongerFile(int snippetID) {
    boolean returnBool = false;
    double startTime = 0.0;
    double lengthSec = 0.0;
    int fileID = 0;
    double fileLenSec = 0.0;
    double snippetInfoEndTime;
    try {
      String sql = "SELECT startTime, lenSec, fileID FROM snippetInfo WHERE snippetID=?";
      PreparedStatement psSnippetInfo = myConnection.prepareStatement(sql);
      psSnippetInfo.setInt(1, snippetID);
      ResultSet rs = psSnippetInfo.executeQuery();
      if (rs.next()) {
        startTime = rs.getDouble("startTime");
        lengthSec = rs.getDouble("lenSec");
        fileID = rs.getInt("fileID");
      }

      sql = "SELECT fileLenSec FROM fileInfo WHERE fileID=?";
      PreparedStatement psFileInfo = myConnection.prepareStatement(sql);
      psFileInfo.setInt(1, fileID);
      ResultSet rsFileInf = psFileInfo.executeQuery();
      if (rsFileInf.next()) {
        fileLenSec = rsFileInf.getDouble("fileLenSec");
      }

      snippetInfoEndTime = startTime + lengthSec;
      if (fileLenSec > snippetInfoEndTime) {
        returnBool = true;
        return returnBool;
      } else {
        return returnBool;
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    return returnBool;
  }


  public static void updateUserInfo(String newUserName, String oldUserName) {
    int userID = getUserID(oldUserName);
    try {
      String sql = "UPDATE userInfo SET userName=? WHERE userID=?";
      PreparedStatement psUserInfo = myConnection.prepareStatement(sql);
      psUserInfo.setString(1, newUserName);
      psUserInfo.setInt(2, userID);
      psUserInfo.executeUpdate();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static boolean updateTagInfo(String newTagName, String oldTagName) {
    //TODO denna metod måste överlagras som en koll om det är en skyddad tagg eller inte
    boolean returnBool = false;
    int tagID = getTagID(oldTagName);
    try {

      String sql = "UPDATE tagInfo SET tagName=? WHERE tagID=?";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ps.setString(1, newTagName);
      ps.setInt(2, tagID);
      ps.executeUpdate();
      returnBool = true;
      return returnBool;

    } catch (Exception e) {
      e.printStackTrace();
    }

    return returnBool;
  }

  public static int getUserID(String userName) {
    int userID = 0;
    try {
      String sql = "SELECT userID FROM userInfo WHERE userName=?";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ps.setString(1, userName);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        userID = rs.getInt("userID");
      }
      return userID;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return userID;
  }

  public static SnippetSet getAllSnippetFromFile(int fileID) {
    SnippetSet snippetSet;
    SortedSet<SnippetInfo> snippetInfoSet = new TreeSet<>();
    List<Integer> snippetIDs = new ArrayList<>();
    Set<Integer> tagIDsSet = new TreeSet<>();
    List<Integer> tagIDsList = new ArrayList<>();
    List<String> tagNamesList;
    Set<String> tagNamesSet = new TreeSet<>();

    try {
      String sql = "SELECT snippetID FROM snippetInfo WHERE fileID=?";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ps.setInt(1, fileID);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        snippetIDs.add(rs.getInt("snippetID"));
      }
      for (int i : snippetIDs) {
        snippetInfoSet.add(readSnippetInf(i));
        tagIDsSet.addAll(getTagIDsForSnippetID(i));
      }
      ps.close();
      rs.close();
      snippetSet = new SnippetSet(snippetInfoSet);
      tagNamesList = getTagNamesFromIDs(tagIDsList);
      tagNamesSet.addAll(tagNamesList);
      return snippetSet;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }


  public static String getFileNameFromSnippetId(int snippetID){
    String returnString = "";
    int fileID = getFileID(snippetID);
    try {
      String sql = "SELECT fileName FROM fileInfo WHERE fileID=?";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ps.setInt(1,fileID);
      ResultSet rs = ps.executeQuery();
      if(rs.next()){
        returnString = rs.getString("fileName");
      }
      ps.close();
      rs.close();
      return returnString;
    } catch (Exception e){
      e.printStackTrace();
    }
    return returnString;
  }

  public static boolean deleteUnusedTag(String tagName){
    boolean returnBool = false;
    int tagID = getTagID(tagName);
    int nrOfOcc = 0;
    try{
      String sql = "SELECT COUNT(snippetID) FROM bridgeSnippetTagTable WHERE tagID=?";
      PreparedStatement psCount = myConnection.prepareStatement(sql);
      psCount.setInt(1,tagID);
      ResultSet rs = psCount.executeQuery();
      if(rs.next()){
       nrOfOcc = rs.getInt(1);
      }
      if(nrOfOcc<1){
        sql = "DELETE FROM tagInfo WHERE tagID=?";
        PreparedStatement psDelTag = myConnection.prepareStatement(sql);
        psDelTag.setInt(1,tagID);
        psDelTag.executeUpdate();

        sql = "DELETE FROM bridgeSnippetTagTable WHERE tagID=?";
        PreparedStatement psDelBride = myConnection.prepareStatement(sql);
        psDelBride.setInt(1,tagID);
        psDelBride.executeUpdate();
        returnBool = true;
      }
      return returnBool;

    }catch (Exception e){
      e.printStackTrace();
    }
    return returnBool;
  }

  public static String getUserNameForSnippet(int snippetID){
    String userName = "";
    int userID = 0;
    try{
      String sql = "SELECT userID FROM snippetInfo WHERE snippetID=?";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ps.setInt(1,snippetID);
      ResultSet rs = ps.executeQuery();
      if(rs.next()){
        userID = rs.getInt("userID");
      }
      ps.close();
      rs.close();
      return getUserName(userID);
    }catch (Exception e){
      e.printStackTrace();
    }
    return userName;
  }

  public static int writeSnippetAsAdmin(SnippetInfo snippetInfo, FileInfo fileInfo) {
    int returnInt = 0;
    if (snippetInfo.getUserName().equals(_adminUserName)) {
      tagsToLowerCase(snippetInfo);
      List<String> newTagList = removeUnwantedCharacters(snippetInfo.getTagNames());
      snippetInfo.getTagNames().clear();
      snippetInfo.getTagNames().addAll(newTagList);
      java.sql.Date date = java.sql.Date.valueOf(snippetInfo.getCreationDate());
      java.sql.Date date2 = java.sql.Date.valueOf(snippetInfo.getLastModified());
      insertIntoFileInfo(snippetInfo, fileInfo);
      insertIntoUserInfo(snippetInfo);
      try {
        PreparedStatement ps = myConnection.prepareStatement
                ("INSERT INTO snippetInfo (fileID,sizeKb,startTime,lenSec,creationDate,lastModifiedDate,userID) VALUES(?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, fileInfo.getFileID());
        ps.setInt(2, snippetInfo.getKbSize());
        ps.setDouble(3, snippetInfo.getStartTime());
        ps.setDouble(4, snippetInfo.getLengthSec());
        ps.setDate(5, date);
        ps.setDate(6, date2);
        ps.setInt(7, snippetInfo.getUserID());
        ps.executeUpdate();
        ResultSet tableKeys = ps.getGeneratedKeys();
        tableKeys.next();
        snippetInfo.setSnippetID(tableKeys.getInt(1));
        ps.close();
        tableKeys.close();
      } catch (Exception ex) {
        ex.printStackTrace();
      }

      insertIntoTagInfo(snippetInfo);

      insertIntoBrigeTable(snippetInfo);

      returnInt = snippetInfo.getSnippetID();
      return returnInt;
    } else {
      return returnInt;
    }
  }

  //TODO fixa så att det inte blir dubletter i snippetInfo. Om en snippet skrivs som är likadan så ska de två
  // snippetsnarnas taggar slås ihop. Den senast editerade snippetens användare blir den slutliga.
  // Den gamla snippeten tas bort


}

