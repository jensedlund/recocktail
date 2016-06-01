package cocktail.db_access;

import cocktail.snippet.FileInfo;
import cocktail.snippet.SnippetInfo;
import cocktail.snippet.SnippetSet;
import com.google.gson.JsonObject;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
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
 * @since 25/03/16
 */


//This class was used to test methods in Driver and dbAdapter. The class will be removed later.
public class DriverTest {

  public static DbAdapterImpl impl = new DbAdapterImpl();
  static List<SnippetInfo> snippetCollection = new ArrayList<>();
private static SnippetSet snippetSet;
  JsonObject snippetJsonObject;
  public static JsonObject getSnippetJsonObject(){
    return getSnippetJsonObject();
  }
  public static void main(String[] args) throws IOException {


      //Driver.deleteFromFileInfo(1355);
      crateSeveralSamples();

      /*for(int i = 1220; i <= 1230; i++){
    Driver.deleteSnippet(i);
}

      for(int i = 1349; i <= 1353; i++){
          Driver.deleteFromFileInfo(i);
      }


      for(int i = 1346; i <= 1348; i++){
          Driver.deleteFromFileInfo(i);
      }
      */
  }



      /*
  }
    impl = new DbAdapterImpl();

      private String id;
      private SortedSet<SnippetInfo> snippetCollection;
      private List<String> operationLog;
      private double maxLenSec;
      private double minLenSec;
      private double avgLenSec;
      private int numSnippets;
      private Set<String> tagsInSet;

    List<Integer> listOfSampelSnippetID = createSampelList();
// SnippetSet snippetSet = new SnippetSet();
 //List<String> operationLog = new ArrayList<>();

  Set<String> tagList = new HashSet<>();
    tagList.add("clap");
    operationLog.add("operationLogEntry");
    System.out.println(snippetCollection.size() + " storleken på snippetCollection ");
    SortedSet<SnippetInfo> sortetSnippets = new TreeSet<>(snippetCollection);
    snippetSet.setSetName("SetID");
    snippetSet.setOperationLog(operationLog);
    snippetSet.setMaxLenSec(3.5);
    snippetSet.setMinLenSec(0.5);
    snippetSet.setAvgLenSec(2.0);
    snippetSet.setNumSnippets(4);
    snippetSet.setTagsInSet(tagList);
    snippetSet.setSnippetCollection(sortetSnippets);*/
    //JsonObject snippetJsonObject =  ConvertSnippetSetJson.convertToJson(snippetSet);
   // System.out.println(snippetJsonObject);
//    RestfulService.setJsonObject(snippetJsonObject);
//    RestfulService.runSpark();
//    RestfulService.sendSearchReq("Blaha ");
    //RestfulService.addHtmlToPage();
      //  TemplateEngine.setConfiguration();
        //RestfulService.getSnippetSetJson(snippetJsonObject);

       // RestfulService.getMaxLenSec(14);


   /* String[] testString = RestfulService.extractStrings("userName=goloconda&snippetName=varför&tagName=fågl");
    System.out.println("I testAv metoder i Restful " + testString.length);
    Map<String,String> testMap = RestfulService.createMapFromRequest(testString);


      for(Map.Entry<String, String> entry : testMap.entrySet()){
        System.out.println(entry.getKey() + " " + entry.getValue());
      }
      */

     /* ArchiveHandler archHand = new ArchiveHandler();

      File file = new File("build/tmp/maintest.xml");
      DbAdapter dbAdapter = new DbAdapterImpl();
      SnippetSet snippetSet1 = dbAdapter.search(new String[]{"Clap"}, false);
      snippetSet1.toStream(new XmlStreamer<SnippetSet>(), file);

      archHand.zip(snippetSet1);


  }


*/


    //Driver.connectToMySql();

  public static void writeSampelsSnippet(){
  /*  String sampleName = ".demo-sample-skogsfågel";
    FileInfo fileInfo;
    SnippetInfo snippetInfo;
    List<Integer> listOfSampelSnippetIDs = new ArrayList<>();
    try {
      FileInputStream input = new FileInputStream("C:\\Users\\Rickard\\Desktop\\db-ljud\\b-duvhök-1.wav");
      byte[] b = spark.utils.IOUtils.toByteArray(input);
      ByteArrayInputStream bInput = new ByteArrayInputStream(b);
    //  public FileInfo(ByteArrayInputStream inputStream, String fileName,int fileSizeKb, double fileLenSec)
      fileInfo = new FileInfo(bInput, "b-duvhök-1.wav",830 ,3.0);
      List<String> tagNames = new ArrayList<>();
      tagNames.add(sampleName);
      tagNames.add("fågel");
      tagNames.add("skog");
      tagNames.add("sverige");
      tagNames.add("duvhök");
      tagNames.add("rovdjur");
      LocalDate ld1 = LocalDate.now();
      LocalDate ld2 = LocalDate.now();
     // public SnippetInfo(String fileName, List<String> tagNames,double startTime, double lengthSec, int kbSize, LocalDate creationDate,
      //LocalDate lastModified, String userName)
      snippetInfo = new SnippetInfo("b-duvhök-1", tagNames, 0.0, 3.0, 830, ld1, ld2, "goloconda");
      snippetCollection.add(snippetInfo);
      int ansInt = impl.writeSnippet(fileInfo, snippetInfo);
      listOfSampelSnippetIDs.add(ansInt);
    } catch (Exception e){
      e.printStackTrace();
    }*/
  }


  public static boolean deletSampels(int snippetID){

    boolean returnBool = false;
    //returnBool = Driver.deleteSnippet(snippetID);
    return returnBool;
  }

  public static void crateSeveralSamples(){
    Map<SnippetInfo,FileInfo> testMap = new HashMap<>();

    try {
      FileInputStream input = new FileInputStream("src/test/resources/clap1.wav");
      byte[] b = spark.utils.IOUtils.toByteArray(input);
      ByteArrayInputStream bInput = new ByteArrayInputStream(b);
      FileInfo fileInfo = new FileInfo(bInput, "test1", 498, 0.5);
      List<String> tagNames = new ArrayList<>();
      tagNames.add(".test-clap+");
      tagNames.add("test1%");
      LocalDate ld1 = LocalDate.now();
      LocalDate ld2 = LocalDate.now();

     SnippetInfo snippetInfo = new SnippetInfo("test2", tagNames, 0.0, 5.0, 498, ld1, ld2, "Devel2");
      testMap.put(snippetInfo,fileInfo);
    } catch (Exception e){
      e.printStackTrace();
    }

    try {
      FileInputStream input = new FileInputStream("src/test/resources/clap2.wav");
      byte[] b = spark.utils.IOUtils.toByteArray(input);
      ByteArrayInputStream bInput = new ByteArrayInputStream(b);
      FileInfo fileInfo = new FileInfo(bInput, "clap2", 263, 3.0);
      List<String> tagNames = new ArrayList<>();
      tagNames.clear();
      tagNames.add("cl ap");
      tagNames.add("te st1");
      tagNames.add("tes$t2@");
      tagNames.add("ÅÄöåäö");
      LocalDate ld1 = LocalDate.now();
      LocalDate ld2 = LocalDate.now();
     SnippetInfo snippetInfo = new SnippetInfo("clap2", tagNames, 0.0, 3.0, 263, ld1, ld2, "Devel2");
testMap.put(snippetInfo,fileInfo);
    } catch (Exception e){
      e.printStackTrace();
    }

    try {
      FileInputStream input = new FileInputStream("src/test/resources/clap3.wav");
      byte[] b = spark.utils.IOUtils.toByteArray(input);
      ByteArrayInputStream bInput = new ByteArrayInputStream(b);
      FileInfo fileInfo = new FileInfo(bInput, "bb-gra%CC%8Ahakedopping-4", 394, 4.0);
      List<String> tagNames = new ArrayList<>();
      tagNames.clear();
      tagNames.add("clap");
      tagNames.add("test2");
      tagNames.add("test3");
      tagNames.add(" ");

      LocalDate ld1 = LocalDate.now();
      LocalDate ld2 = LocalDate.now();

      SnippetInfo snippetInfo = new SnippetInfo("bb-gra%CC%8Ahakedopping-4", tagNames, 0.0, 3.0, 294, ld1, ld2, "Devel1");
testMap.put(snippetInfo,fileInfo);
    } catch (Exception e){
      e.printStackTrace();
    }

    try {
      FileInputStream input = new FileInputStream("src/test/resources/clap4.wav");
      byte[] b = spark.utils.IOUtils.toByteArray(input);
      ByteArrayInputStream bInput = new ByteArrayInputStream(b);
     FileInfo fileInfo = new FileInfo(bInput, "clap4", 233, 2.0);
      List<String> tagNames = new ArrayList<>();
      tagNames.clear();
      tagNames.add("clap");
      LocalDate ld1 = LocalDate.now();
      LocalDate ld2 = LocalDate.now();

     SnippetInfo snippetInfo = new SnippetInfo("clap4", tagNames, 0.0, 2.0, 233, ld1, ld2, "Devel1");
      testMap.put(snippetInfo,fileInfo);
    } catch (Exception e){
      e.printStackTrace();
    }

    List<Integer> testLIst = Driver.writeSnippets(testMap);
      System.out.println(testLIst);

  }


  public static List<Integer> createSampelList() {
    FileInfo fileInfo;
    SnippetInfo snippetInfo;
      List<Integer> listOfSampelSnippetIDs = new ArrayList<>();
    try {
      FileInputStream input = new FileInputStream("src/test/resources/clap1.wav");
      byte[] b = spark.utils.IOUtils.toByteArray(input);
      ByteArrayInputStream bInput = new ByteArrayInputStream(b);
      fileInfo = new FileInfo(bInput, "clap1", 498, 0.5);
      List<String> tagNames = new ArrayList<>();
      tagNames.add("clap+");
      tagNames.add("test1%");
      LocalDate ld1 = LocalDate.now();
      LocalDate ld2 = LocalDate.now();

      snippetInfo = new SnippetInfo("clap1", tagNames, 0.0, 5.0, 498, ld1, ld2, "Devel1");
        snippetCollection.add(snippetInfo);
      int ansInt = impl.writeSnippet(fileInfo, snippetInfo);
        listOfSampelSnippetIDs.add(ansInt);
    } catch (Exception e){
      e.printStackTrace();
    }

    try {
      FileInputStream input = new FileInputStream("src/test/resources/clap2.wav");
      byte[] b = spark.utils.IOUtils.toByteArray(input);
      ByteArrayInputStream bInput = new ByteArrayInputStream(b);
      fileInfo = new FileInfo(bInput, "clap2", 263, 3.0);
      List<String> tagNames = new ArrayList<>();
      tagNames.clear();
      tagNames.add("cl ap");
      tagNames.add("te st1");
      tagNames.add("tes$t2@");
      tagNames.add("ÅÄöåäö");
      LocalDate ld1 = LocalDate.now();
      LocalDate ld2 = LocalDate.now();
      snippetInfo = new SnippetInfo("clap2", tagNames, 0.0, 3.0, 263, ld1, ld2, "Devel2");
        snippetCollection.add(snippetInfo);

        int ansInt = impl.writeSnippet(fileInfo, snippetInfo);
        listOfSampelSnippetIDs.add(ansInt);
    } catch (Exception e){
      e.printStackTrace();
    }

    try {
      FileInputStream input = new FileInputStream("src/test/resources/clap3.wav");
      byte[] b = spark.utils.IOUtils.toByteArray(input);
      ByteArrayInputStream bInput = new ByteArrayInputStream(b);
      fileInfo = new FileInfo(bInput, "bb-gra%CC%8Ahakedopping-4", 394, 4.0);
      List<String> tagNames = new ArrayList<>();
      tagNames.clear();
      tagNames.add("clap");
      tagNames.add("test2");
      tagNames.add("test3");
      tagNames.add(" ");

      LocalDate ld1 = LocalDate.now();
      LocalDate ld2 = LocalDate.now();

      snippetInfo = new SnippetInfo("bb-gra%CC%8Ahakedopping-4", tagNames, 0.0, 3.0, 294, ld1, ld2, "Devel1");
        snippetCollection.add(snippetInfo);

       int ansInt = impl.writeSnippet(fileInfo, snippetInfo);
      System.out.println(ansInt + "inten som ska ha en tom tagg ");
        listOfSampelSnippetIDs.add(ansInt);
    } catch (Exception e){
      e.printStackTrace();
    }

      try {
          FileInputStream input = new FileInputStream("src/test/resources/clap4.wav");
          byte[] b = spark.utils.IOUtils.toByteArray(input);
          ByteArrayInputStream bInput = new ByteArrayInputStream(b);
          fileInfo = new FileInfo(bInput, "clap4", 233, 2.0);
          List<String> tagNames = new ArrayList<>();
          tagNames.clear();
          tagNames.add("clap");
          LocalDate ld1 = LocalDate.now();
          LocalDate ld2 = LocalDate.now();

          snippetInfo = new SnippetInfo("clap4", tagNames, 0.0, 2.0, 233, ld1, ld2, "Devel1");
          snippetCollection.add(snippetInfo);

         int ansInt = impl.writeSnippet(fileInfo, snippetInfo);
          listOfSampelSnippetIDs.add(ansInt);
      } catch (Exception e){
          e.printStackTrace();
      }
      return listOfSampelSnippetIDs;
  }
}
