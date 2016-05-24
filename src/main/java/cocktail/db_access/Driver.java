

package cocktail.db_access;

import cocktail.snippet.FileInfo;
import cocktail.snippet.SnippetInfo;
import cocktail.snippet.SnippetSet;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.sql.*;
import java.util.*;

// I chose to use enum instead of a regular class with static methods. This way the call for the methods looks just like
//it would have done whit a regular class whit static methods but it is impossible to instantiate the class and still have the
//benefit of using static methods. It's like belt and suspenders to avoid instantiation.
public enum Driver {
  ;
  private static Connection myConnection;
  private static Statement myStatment;
  private static String _adminUserName;



  static {
    _adminUserName = "Admin";
  }

  public static void setAdminUserName(String adminUserName) {
    _adminUserName = adminUserName;
  }

  public static String getAdminUserName() {
    return _adminUserName;
  }


  protected static boolean connectToMySql() {
    boolean returnBool = false;

    try {
      myConnection = DriverManager.getConnection("jdbc:mysql://130.237.67.145:3306/recocktail?aoutoReconnect=true&useSSL=false", DbAccessHandler.getAccessInfo("username"), DbAccessHandler.getAccessInfo("password"));
      myStatment = myConnection.createStatement();
      returnBool = true;

    } catch (Exception e) {
      System.out.println(myConnection);
      e.printStackTrace();

    }
    return returnBool;
  }

  //Method use regex to remove charachter such as &%# from tagNames. [. - _ ] are allowed
  private static List<String> removeUnwantedCharacters(List<String> tagNames) {
    List<String> newTagList = new ArrayList<>();
    for (String s : tagNames) {
      String temp = s.replaceAll("[^\\.\\_\\-åäö\\w]", "");
      if (temp.length() > 1) {
        newTagList.add(temp);
      }
    }
    return newTagList;
  }

  //Method is transforming tags with upperCase to new tags in lowerCase
  private static void tagsToLowerCase(SnippetInfo snippetInfo) {
    List<String> tempTagNames = new ArrayList<>();

    int size = snippetInfo.getTagNames().size();
    for (int i = 0; i < size; i++) {
      String temp = snippetInfo.getTagNames().get(i).toLowerCase();
      tempTagNames.add(temp);
    }
    snippetInfo.getTagNames().clear();
    snippetInfo.getTagNames().addAll(tempTagNames);
  }

  //Method trows away tags that starts with [.], tags like ".demo-sea-birds" are protected sample data and can only be
  //inserted or altered if the userName is equal to "Admin"
  private static void removeProtectedTags(SnippetInfo snippetInfo) {
    List<String> listToDel = new ArrayList<>();
    for (String s : snippetInfo.getTagNames()) {
      if (s.charAt(0) == '.') {
        listToDel.add(s);
      }
    }
    snippetInfo.getTagNames().removeAll(listToDel);

  }

  //Check if the userName is equal to the adminUserName
  private static boolean isCallProtectedAdmin(SnippetInfo snippetInfo) {
    boolean isAdmin;
    if (snippetInfo.getUserName().equals(_adminUserName)) {
      isAdmin = true;
    } else {
      isAdmin = false;
    }
    return isAdmin;
  }

//This overloaded method inserts one snippet to the database and is used when the file connected to the snippet
  // is already in the database and the fileID is known.
  protected static int writeSnippet(SnippetInfo snippetInfo, int fileID) {
    int returnInt = 0;

    if (isSnippetADuplicate(snippetInfo, fileID)) {
      joinTwoSnippets(snippetInfo, fileID);
    }
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
    } else {
      returnInt = writeSnippetAsAdmin(snippetInfo, fileID);
    }
    return returnInt;
  }

  //Method check if the snippet already is saved in the database
  private static boolean isSnippetADuplicate(SnippetInfo snippetInfo, FileInfo fileInfo) {
    boolean isDublicate = false;
    if (!isFileInDb(fileInfo)) {
      isDublicate = false;
      return isDublicate;
    } else {
      int fileID = getFileIDFromFileNameSizeLen(fileInfo.getFileName(),
          fileInfo.getFileLenSec(), fileInfo.getFileSizeKb());
      double lenSec = 0.0;
      double startTime = 0.0;

      try {
        String sql = "SELECT startTime, lenSec FROM snippetInfo WHERE fileID=?";
        PreparedStatement ps = myConnection.prepareStatement(sql);
        ps.setInt(1, fileID);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
          lenSec = rs.getDouble("lenSec");
          startTime = rs.getDouble("startTime");
        }
        ps.close();
        rs.close();
      } catch (Exception e) {
        e.printStackTrace();
      }

      if (lenSec == snippetInfo.getLengthSec() && startTime == snippetInfo.getStartTime()) {
        isDublicate = true;
        return isDublicate;
      }
    }
    return isDublicate;
  }


  //This overloaded method checks if the snippet already is saved in the database when the fileID is already known

  private static boolean isSnippetADuplicate(SnippetInfo snippetInfo, int fileID) {
    boolean isDublicate = false;
    double lenSec = 0.0;
    double startTime = 0.0;
    try {
      String sql = "SELECT startTime, lenSec FROM snippetInfo WHERE fileID=?";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ps.setInt(1, fileID);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        lenSec = rs.getDouble("lenSec");
        startTime = rs.getDouble("startTime");
      }
      ps.close();
      rs.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (lenSec == snippetInfo.getLengthSec() && startTime == snippetInfo.getStartTime()) {
      isDublicate = true;
      return isDublicate;
    }

    return isDublicate;
  }


  //Method checks if a file already is saved in database or not
  private static boolean isFileInDb(FileInfo fileInfo) {

//TODO fixa checkusm här
    boolean isFileInDb = false;
    try {
      String sql = "SELECT fileName, fileSizeKb, fileLenSec FROM fileInfo";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        if (fileInfo.getFileName().equals(rs.getString("fileName"))) {
          if (fileInfo.getFileLenSec() == rs.getDouble("fileLenSec") &&
              fileInfo.getFileSizeKb() == rs.getDouble("fileSizeKb")) {
            isFileInDb = true;
            return isFileInDb;
          }
        }
      }
      ps.close();
      rs.close();
      return isFileInDb;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

//Method takes a string, fileName, as an argument and return the string decoded in UTF-8
  private static String utf8Decode(String s) {
    String returnString = "";
    System.out.println(s);
    try {
      returnString = URLDecoder.decode(s, "UTF-8");
    } catch (IOException e) {
      e.printStackTrace();
    }
    System.out.println(returnString);
    return returnString;
  }

  //Overloaded method that inserts one snippet in database
  protected static int writeSnippet(FileInfo fileInfo, SnippetInfo snippetInfo) {

    String tempFileName = fileInfo.getFileName();
    fileInfo.setFileName(utf8Decode(tempFileName));

    int returnInt = 0;
    if (isSnippetADuplicate(snippetInfo, fileInfo)) {
      returnInt = joinTwoSnippets(snippetInfo, fileInfo);
    } else if (isCallProtectedAdmin(snippetInfo)) {
      returnInt = writeSnippetAsAdmin(snippetInfo, fileInfo);
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


  //Overloaded method is called when a snippet is a duplicate. This method is joining the two lists of tags and
  //is calling writeSnippet with single, indirect recursion
  private static int joinTwoSnippets(SnippetInfo snippetInfo, FileInfo fileInfo) {
    int oldSnippetID = 0;
    int fileID = getFileIDFromFileNameSizeLen(fileInfo.getFileName(), fileInfo.getFileLenSec(), fileInfo.getFileSizeKb());
    try {
      String sql = "SELECT snippetID FROM snippetInfo WHERE fileID=? AND startTime=? " +
          "AND lenSec=?";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ps.setInt(1, fileID);
      ps.setDouble(2, snippetInfo.getStartTime());
      ps.setDouble(3, snippetInfo.getLengthSec());
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        oldSnippetID = rs.getInt("snippetID");
      }
      ps.close();
      rs.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    List<Integer> tagIds = getTagIDsForSnippetID(oldSnippetID);
    List<String> tagNames = getTagNamesFromTagIDs(tagIds);
    tagNames.addAll(snippetInfo.getTagNames());
    Set<String> tagSet = new HashSet<>();
    tagSet.addAll(tagNames);
    snippetInfo.getTagNames().clear();
    snippetInfo.getTagNames().addAll(tagSet);
    deleteSnippet(oldSnippetID); //Really important to delete the snippet first, this is the "get out of recursion" call
    int newSnippetID = writeSnippet(fileInfo, snippetInfo);
    return newSnippetID;
  }

  //Overloaded method is called when a snippet is a duplicate. This method is joining the two lists of tags and
  //is calling writeSnippet with single, indirect recursion
  private static int joinTwoSnippets(SnippetInfo snippetInfo, int fileID) {
    int oldSnippetID = 0;
    try {
      String sql = "SELECT snippetID FROM snippetInfo WHERE fileID=? AND startTime=? " +
          "AND lenSec=?";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ps.setInt(1, fileID);
      ps.setDouble(2, snippetInfo.getStartTime());
      ps.setDouble(3, snippetInfo.getLengthSec());
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        oldSnippetID = rs.getInt("snippetID");
      }
      ps.close();
      rs.close();
    } catch (Exception e) {
      e.printStackTrace();
    }

    List<Integer> tagIds = getTagIDsForSnippetID(oldSnippetID);
    List<String> tagNames = getTagNamesFromTagIDs(tagIds);
    tagNames.addAll(snippetInfo.getTagNames());
    Set<String> tagSet = new HashSet<>();
    tagSet.addAll(tagNames);
    snippetInfo.getTagNames().clear();
    snippetInfo.getTagNames().addAll(tagSet);
    deleteSnippet(oldSnippetID);//Really imporant to delete the snippet first, this is the "get out of recursion" call
    int newSnippetID = writeSnippet(snippetInfo, fileID);
    return newSnippetID;
  }


//Method write file to database
  protected static boolean insertIntoFileInfo(SnippetInfo snippetInfo, FileInfo fileInfo) {
    boolean returnBool = false;
    if (!isFileInDb(fileInfo)) {
      try {
        PreparedStatement ps = myConnection.prepareStatement
            ("INSERT INTO fileInfo (file,fileName,fileSizeKb,fileLenSec) VALUES(?,?,?,?)",
                Statement.RETURN_GENERATED_KEYS);
        ps.setBinaryStream(1, fileInfo.getInputStream());
        ps.setString(2, fileInfo.getFileName());
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
      fileInfo.setFileID(getFileIDFromFileNameSizeLen(snippetInfo.getFileName(),
          fileInfo.getFileLenSec(), fileInfo.getFileSizeKb()));
      returnBool = true;
    }
    return returnBool;
  }

//Method returns fileID from information that identifies the file
  protected static int getFileIDFromFileNameSizeLen(String fileName, double fileSizeSec, int fileSizeKb) {
    int fileID = 0;
    try {
      String sql = "SELECT fileID FROM fileInfo WHERE fileName =? " +
          "AND fileLenSec=? AND fileInfo.fileSizeKb=?";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ps.setString(1, fileName);
      ps.setDouble(2, fileSizeSec);
      ps.setInt(3, fileSizeKb);
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


//Method returns all users registered in the database
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

//Method inserts unserName in userInfo and set the userID in the snippetInfo object
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


  //Method inserts values of snippetID and tagID in this bridge table between snippetInfo and tagInfo
  private static boolean insertIntoBrigeTable(SnippetInfo snippetInfo) {
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


//Method inserts tags into tagInfo table and sets a list of tagIDs in snippetInfo object
  private static boolean insertIntoTagInfo(SnippetInfo snippetInfo) {
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


  //Overloaded method inserts info into table snippetInfo and sets the generated snippetID in the snippetInfo object
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

  //Method returns all file names
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


  //Method returns all tag names in database.
  protected static List<String> getAllTagNames() {
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

  //Method takes a tagName as argument and returns the tagID
  protected static int getTagID(String tagName) {
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


  //Method updates info in fileInfo but is limited by foregin key constraints. The fileID can not be changed
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


  //Method is a way to update a snippet by deleteing it and then reinsert it with the same snippetID. This method
  //is therefore a way to walk around all the constraints in the design.
  protected static boolean deleteInsertSnippetInfo(FileInfo fileInfo, SnippetInfo snippetInfo, int snippetID) {
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

  ///Method is a way to update a snippet by deleteing it and then reinsert it with the same snippetID. This method
  //is therefore a way to walk around all the constraints in the design. This method is called when the userName is equal to the admin name
  //and if the tags are protected, like ".demo-sea-bird"
  protected static boolean deleteInsertAsAdmin(SnippetInfo snippetInfo, FileInfo fileInfo, int snippetID) {
    boolean returnBool = false;
    if (snippetInfo.getUserName().equals(_adminUserName)) {
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


  //Method deletes a snippet and is called when the snippet has a protected tag and the userName is equal to teh admin name.
  protected static boolean deleteSnippetAdm(int snippetID) {
    boolean returnBool;
    String userName = getUserNameForSnippet(snippetID);
    if (userName.equals(_adminUserName)) {
      int fileID = getFileIDFromSnippetID(snippetID);
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


  //Returns the fileID ant takes a snippetID as argument
  protected static int getFileIDFromSnippetID(int snippetID) {
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


  //Returns a list of tagIDs from a snippetID
  private static List<Integer> getTagIDsForSnippetID(int snippetID) {
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


  //Returns a list of tagNams and takes a list of tagIDs as argument
  private static List<String> getTagNamesFromTagIDs(List<Integer> tagIDs) {
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


  //Returns a list of all fileIds in the database
  protected static List<Integer> getAllFileIDs() {
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


  //Method is called from method deleteSnippet and is deletign the informatione where the snippetID is occuring
  private static boolean deleteFromBridgeTable(int snippetID) {
    boolean returnBool = false;
    int fileID = getFileIDFromSnippetID(snippetID);
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

  //Method is deleteing one row in table snippetInfo
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


  //Returns the fileID from a row specified by the snippetID from the argument
  private static int getFileIDFromSnippetInfoTable(int snippetID) {
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


  //Returns a complete list of all the snippetIDs in the database
  protected static List<Integer> getAllSnippetIDs() {
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


  //Method check if one specific file is connected to any snippet
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


  //Method delete one row in fileInfo table
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


  //Check if one tag is connected to äny snippetor not
  protected static boolean isTagInUse(int tagID) {
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


  //Method deletes one tag in tagInfo table
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


  //Method check if the snippet has tags that ar protected, like ".demo-sea-bird"
  private static boolean isSnippetProtectedSample(int snippetID) {
    boolean isProtected = false;
    List<Integer> id = new ArrayList<>();
    id.add(snippetID);
    List<String> tags = getTagNamesFromTagIDs(id);
    for (String tag : tags) {
      if (tag.charAt(0) == '.') {
        isProtected = true;
      }
    }
    return isProtected;
  }

  //Method deletes one snippet by calling severel methods that deletes from all the tables
  protected static boolean deleteSnippet(int snippetID) {
    boolean returnBool;
    String userName = getUserNameForSnippet(snippetID);
    if (!isSnippetProtectedSample(snippetID) || userName.equals(_adminUserName)) {
      int fileID = getFileIDFromSnippetID(snippetID);
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


  //Method read the file from fileInfo table and return it as a bite array
  protected static byte[] readSnippet(int snippetID) {
    int sourceID = getFileIDFromSnippetID(snippetID);
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
      }

    } catch (Exception e) {
      e.printStackTrace();
    }
    return buffer;
  }


  //Returns the userName and takes a userID as an argument
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


  //Method returns one SnippetInfo object whit information from severel tables
  protected static SnippetInfo readSnippetInf(int snippetID) {
    SnippetInfo snippetInfo = new SnippetInfo();
    snippetInfo.setSnippetID(snippetID);
    snippetInfo.setFileID(getFileIDFromSnippetID(snippetID));
    List<Integer> tagIDs = getTagIDsForSnippetID(snippetID);
    List<String> tagNames = getTagNamesFromTagIDs(tagIDs);
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
        snippetInfo.setStartTime(rs.getDouble("startTime"));
      }
      snippetInfo.setUserName(getUserName(snippetInfo.getUserID()));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return snippetInfo;
  }


  //Returns the complete number of files in database
  protected static int getTotlNumberOfFiles() {
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


  //Returns the total amount of kb from files stored in database
  protected static Integer getTotalFileSizeKb() {
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


  //Returns the size in kb of the smallest file
  protected static Integer getMinFileSizeKb() {
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


  //Returns the size in kb of the largest file in database
  protected static Integer getMaxFileSizeKb() {
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

  //Returns a double representing the length of the shortest file in database.
  protected static double getMinFileLenSec() {
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


  //Method search the database for snippet connected to the tags in the argument list. Returns a list of snippetIDs
  protected static List<Integer> searchSnippetIDs(List<String> tagArray, double lengthMaxFilter) {
    List<String> tagLowerCas = new ArrayList<>();
    for (String tag : tagArray) {
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


  //Method check if the snippet from search is whitin the limits of the length filter

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


  //Returns the list of snippetIDs that is conected with tagIDs from argument
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

  //Returns the snippetID from one row defined by the argument, tagID
  protected static int getSnippetIDForTagID(int tagID) {
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

  //Search for snippets connected to a collection of tagNames and sort out snippets that are larger than the length filter
  protected static List<Integer> searchSnippetIDs(String tagName, double lengthMaxFilter) {
    List<Integer> snippetIDs = new ArrayList<>();
    tagName = tagName.toLowerCase();
    int tagID = getTagID(tagName);
    int snippetID = getSnippetIDForTagID(tagID);
    SnippetInfo snippetInfo = readSnippetInf(snippetID);
    if (lengthMaxFilter <= 0) {
      snippetIDs.add(snippetID);
    } else if (snippetInfo.getLengthSec() <= lengthMaxFilter) {
      snippetIDs.add(snippetID);
    }
    return snippetIDs;
  }


  //Returns a double representing the longest file in database in seconds
  protected static Double getMaxFileLenSec() {
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


  //Returns the total number of snippets in the database
  protected static int getNumberOfSnippets() {
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

  protected static Double getMinSnippetLenSec() {
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

  protected static Double getMaxSnippetLenSec() {
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

  protected static Integer getOccuranceOfTag(String tagName) {
    int tagID = getTagID(tagName);
    int occurance = 0;
    try {
      String sql = "SELECT COUNT(*) FROM bridgeSnippetTagTable WHERE tagID=?";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ps.setInt(1, tagID);
      ResultSet rs = ps.executeQuery();
      while (rs.next()) {
        occurance = rs.getInt(1);
      }
      return occurance;

    } catch (Exception e) {
      e.printStackTrace();
    }
    return 0;
  }

  protected static ArrayList<String> getComplementaryTags(String tag) {
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

      tempComplementaryTags.addAll(getTagNamesFromTagIDs(tempTagIDs));
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


  //Method is checking two tables and match the results to answer the question if one snippet is part of a larger file or not
  protected static boolean isSnippetPartOfLongerFile(int snippetID) {
    boolean returnBool = false;
    double startTime = 0.0;
    double lengthSec = 0.0;
    int fileID = 0;
    double fileLenSec = 0.0;
    try {
      String sql = "SELECT lenSec, fileID FROM snippetInfo WHERE snippetID=?";
      PreparedStatement psSnippetInfo = myConnection.prepareStatement(sql);
      psSnippetInfo.setInt(1, snippetID);
      ResultSet rs = psSnippetInfo.executeQuery();
      if (rs.next()) {
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

      if (fileLenSec > lengthSec) {
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


  //Change userName for a user, for one userID
  protected static void updateUserInfo(String newUserName, String oldUserName) {
    int userID = getUserIDForUserName(oldUserName);
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


  //Change one tagName, the tagID stays the same
  protected static boolean updateTagName(String newTagName, String oldTagName) {

    boolean returnBool = false;
    if(newTagName.charAt(0)=='.'){
      return returnBool;
    }
    if(getAllTagNames().contains(newTagName)){
      return returnBool;
    }else {
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
    }
    return returnBool;
  }

  //Method is called when the new or old tag is protected and the userName is equal to the adminUserName
  protected static boolean updateTagNameAsAdmin(String newTagName, String oldTagName, String userName){
    boolean returnBool = false;
    if(!userName.equals(_adminUserName)){
      return returnBool;
    }
    if(getAllTagNames().contains(newTagName)){
      return returnBool;
    }else {
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
    }
    return returnBool;
  }

  //Returns the userID and take the userName as argument. This method need some work in order to be useful in a program where
  //the user is logged in. Example: getUserIDForUserNameUserPassword(String userName, String password); If there is more than one user with the same userName
  //there will be problem.
  private static int getUserIDForUserName(String userName) {
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


  //Method returns a snippetSet including all the snippets connected to one specific file
  protected static SnippetSet getAllSnippetFromFile(int fileID) {
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
      tagNamesList = getTagNamesFromTagIDs(tagIDsList);
      tagNamesSet.addAll(tagNamesList);
      return snippetSet;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }


  //Returns the name of the file connected to one specific snippetID
  protected static String getFileNameFromSnippetId(int snippetID) {
    String returnString = "";
    int fileID = getFileIDFromSnippetID(snippetID);
    try {
      String sql = "SELECT fileName FROM fileInfo WHERE fileID=?";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ps.setInt(1, fileID);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        returnString = rs.getString("fileName");
      }
      ps.close();
      rs.close();
      return returnString;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return returnString;
  }


//Delete one row in tagInfo table if the tag is not connected to any snippet
  protected static boolean deleteUnusedTag(String tagName) {
    boolean returnBool = false;
    int tagID = getTagID(tagName);
    int nrOfOcc = 0;
    try {
      String sql = "SELECT COUNT(snippetID) FROM bridgeSnippetTagTable WHERE tagID=?";
      PreparedStatement psCount = myConnection.prepareStatement(sql);
      psCount.setInt(1, tagID);
      ResultSet rs = psCount.executeQuery();
      if (rs.next()) {
        nrOfOcc = rs.getInt(1);
      }
      if (nrOfOcc < 1) {
        sql = "DELETE FROM tagInfo WHERE tagID=?";
        PreparedStatement psDelTag = myConnection.prepareStatement(sql);
        psDelTag.setInt(1, tagID);
        psDelTag.executeUpdate();

        sql = "DELETE FROM bridgeSnippetTagTable WHERE tagID=?";
        PreparedStatement psDelBride = myConnection.prepareStatement(sql);
        psDelBride.setInt(1, tagID);
        psDelBride.executeUpdate();
        returnBool = true;
      }
      return returnBool;

    } catch (Exception e) {
      e.printStackTrace();
    }
    return returnBool;
  }


  //Returns the name of the user hwo inserted one specific snippet
  protected static String getUserNameForSnippet(int snippetID) {
    String userName = "";
    int userID = 0;
    try {
      String sql = "SELECT userID FROM snippetInfo WHERE snippetID=?";
      PreparedStatement ps = myConnection.prepareStatement(sql);
      ps.setInt(1, snippetID);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        userID = rs.getInt("userID");
      }
      ps.close();
      rs.close();
      return getUserName(userID);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return userName;
  }


  //Method  is called when the username is equal to the adminUserName and one of the tagNames is protected, ".demo-sea-bird"
  public static int writeSnippetAsAdmin(SnippetInfo snippetInfo, FileInfo fileInfo) {
    int returnInt = 0;
    String tempFileName = fileInfo.getFileName();
    fileInfo.setFileName(utf8Decode(tempFileName));

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



  //Oaverloaded method that insert one snippet if the userName i s equal to the adminUserName and one of
  // the tags is protected and the file already is inserted into the database
  public static int writeSnippetAsAdmin(SnippetInfo snippetInfo, int fileID) {
    int returnInt = 0;
    snippetInfo.setFileID(fileID);

    if (snippetInfo.getUserName().equals(_adminUserName)) {
      tagsToLowerCase(snippetInfo);
      List<String> newTagList = removeUnwantedCharacters(snippetInfo.getTagNames());
      snippetInfo.getTagNames().clear();
      snippetInfo.getTagNames().addAll(newTagList);
      java.sql.Date date = java.sql.Date.valueOf(snippetInfo.getCreationDate());
      java.sql.Date date2 = java.sql.Date.valueOf(snippetInfo.getLastModified());
      insertIntoUserInfo(snippetInfo);
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

      insertIntoTagInfo(snippetInfo);

      insertIntoBrigeTable(snippetInfo);

      returnInt = snippetInfo.getSnippetID();
      return returnInt;
    } else {
      return returnInt;
    }
  }

}
