package cocktail.archive_handler;

import cocktail.db_access.DbAdapter;
import cocktail.db_access.DbAdapterImpl;
import cocktail.snippet.FileInfo;
import cocktail.snippet.SnippetInfo;
import cocktail.snippet.SnippetSet;
import cocktail.sound_processing.SoundExtractor;
import cocktail.sound_processing.SoundExtractorImpl;
import cocktail.sound_processing.SoundProcess;
import cocktail.sound_processing.SoundProcessImpl;
import cocktail.stream_io.XmlStreamer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.xml.bind.JAXBException;
import java.io.*;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * This class handles archives to and from the frontend. Populating zip's and database with
 * information inside SnippetSets
 */
public class ArchiveHandler {

  private static final SoundExtractor convert = new SoundExtractorImpl();
  private static final DbAdapter dbAdapter = new DbAdapterImpl();
  private static final SoundProcess process = new SoundProcessImpl();

  /**
   * creates a zip and populate it with snippets from the database.
   * Files are trimmed if needed before added to the archive.
   * @param set SnippetSet to prepare
   * @return path to zip-archive
   */
  public String zip(SnippetSet set) {
    File zipWorkspace = new File("./src/main/resources/zip/");
    if (!zipWorkspace.exists()) {
      zipWorkspace.mkdir();
    }

    File tempDir = new File(zipWorkspace + File.separator + set.getSetName());
    String outputFile = tempDir + File.separator + set.getSetName() + ".zip";
    final Path path = Paths.get(outputFile);
    final URI uri = URI.create("jar:file:" + path.toUri().getPath());
    Map<String, String> env = new HashMap<>();
    env.put("create", "true");
    env.put("encoding", "UTF-8");

    Path zipFilePath;
    File addNewFile;
    byte[] sourceFile = null;
    int currentFileID = -1; //set to -1 to ensure a file is getting loaded when working thru set.
    FileSystem fs = null;

    if (!tempDir.exists()) {
      tempDir.mkdir();
    }

    try {
      fs = FileSystems.newFileSystem(uri, env);
    } catch (IOException e) {
      e.printStackTrace();
    }

    //here the process of working thru the snippetset to collect clips and populating the archive.
    //trying to optimize it trying to avoid loading source files multiple times.
    for (SnippetInfo snippet : sortSetByFileID(set)) {

      if (fs != null) {
        if (currentFileID == -1 || currentFileID != snippet.getFileID()) {
          currentFileID = snippet.getFileID();
          sourceFile = dbAdapter.readSnippet(snippet.getSnippetID());
          System.out.println("----------loaded a new file from DB: " + snippet.getFileName());
        }

        if (!dbAdapter.isSnippetPartOfLongerFile(snippet.getSnippetID())) {
          addNewFile =
              byteArrayToFile(sourceFile, tempDir + File.separator + snippet.getFileName());
          System.out.println("-----this is a single file snippet! ------");
        } else {
          addNewFile =
              byteArrayToFile(process.trimAudioClip(sourceFile,
                                                    snippet.getStartTime(), snippet.getLengthSec()),
                              tempDir + File.separator + snippet.getFileName());
          System.out.println("----this snippet is a cutout from a larger file----");
        }

        Path parentDir = fs.getPath("/snippets/" + snippet.getTagNames().get(0));
        zipFilePath =
            fs.getPath(
                parentDir + File.separator + snippet.getFileName() + "_" + snippet.getSnippetID()
                + ".wav");
        if (Files.notExists(parentDir)) {
          System.out.println("Creating directory " + parentDir);
          try {
            Files.createDirectories(parentDir);
          } catch (IOException e) {
            e.printStackTrace();
          }
        }
        try {
          Files.copy(addNewFile.toPath(), zipFilePath);
        } catch (IOException e) {
          e.printStackTrace();
        }
        snippet.setFileName(snippet.getFileName() + "_" + snippet.getSnippetID() + ".wav");
        addNewFile.delete();
      }
    }
    File
        xmlFile =
        new File(tempDir + File.separator + "SnippetSet.xml");
    try {
      if (fs != null) {
        set.toStream(new XmlStreamer<SnippetSet>(), xmlFile);
        Files.copy(xmlFile.toPath(), fs.getPath("SnippetSet.xml"));
        fs.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
      try {
        fs.close();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }
    xmlFile.delete();
    return outputFile;
  }

  /**
   * Unpacking and processing of zip archive from the frontend.
   * populate the database with content.
   *
   * @param inputFile path to zip archive to process
   * @return snippetSet of files added to database
   */
  public SnippetSet unzip(String inputFile) {
    FileSystem fs = null;
    String tempZipDir = null;
    SnippetSet snippetSet = null;

    File unzipWorkspace = new File("./src/main/resources/unzip/");
    if (!unzipWorkspace.exists()) {
      unzipWorkspace.mkdir();
    }

    int lastIndex = inputFile.lastIndexOf('/');
    if (lastIndex >= 0) {
      System.out.println(lastIndex + " last index " + inputFile);
      tempZipDir = inputFile.substring(lastIndex + 1);
      tempZipDir = tempZipDir.substring(0, tempZipDir.lastIndexOf('.'));
    }

    File tempDir = new File("./src/main/resources/unzip/" + File.separator + tempZipDir);
    if (!tempDir.exists()) {
      tempDir.mkdir();
    }

    Map<String, String> env = new HashMap<>();
    env.put("create", "false");
    final Path path = Paths.get(inputFile);
    final URI uri = URI.create("jar:file:" + path.toUri().getPath());
    try {
      fs = FileSystems.newFileSystem(uri, env);
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (fs != null) {
      final Path zipRoot = fs.getPath("/");

      try {
        Files.walkFileTree(zipRoot, new SimpleFileVisitor<Path>() {

          @Override
          public FileVisitResult visitFile(Path file,
                                           BasicFileAttributes attrs) throws IOException {
            File currentFile = new File(tempDir.toString(), file.toString());
            final Path destFile = Paths.get(currentFile.toString());
            Files.copy(file, destFile, StandardCopyOption.REPLACE_EXISTING);
            System.out.println(file.getFileName() + " unpacked.");

            //converting files to 16 bit 44khz mono wav's:
            String fileExtension = getFileExtension(currentFile.toString());
            File workFile = destFile.toFile();
            File
                tmpWorkFile =
                new File(
                    destFile + "_tmp.wav");
            try {
              if (vidCodec(fileExtension)) {
                convert.vidToWav(workFile, tmpWorkFile);
                tmpWorkFile.renameTo(workFile);
              } else if (soundCodec(fileExtension)) {
                convert.soundToWav(workFile, tmpWorkFile);
                tmpWorkFile.renameTo(workFile);
              }
            } catch (IOException e) {
              e.printStackTrace();
            }
            //end of convert

            return FileVisitResult.CONTINUE;
          }

          @Override
          public FileVisitResult preVisitDirectory(Path dir,
                                                   BasicFileAttributes attrs) throws IOException {
            final Path subDir = Paths.get(tempDir.toString(),
                                          dir.toString());
            if (Files.notExists(subDir)) {
              Files.createDirectory(subDir);
            }
            return FileVisitResult.CONTINUE;
          }
        });
      } catch (IOException e) {
        e.printStackTrace();
      }


      //here the procedure of going thru the snippetset inside the archive begins
      //set getting sorted by filename and processed one by one.
      ArrayList<Integer> snippetIDs = new ArrayList<>();
      String fileName;
      String lastFileName = null;
      int lastFileID = 0;
      String snippetSetPath = tempDir.toString() + File.separator + "SnippetSet.xml";
      SnippetSet
          currentSnippetSet =
          getSnippetSet(new File(snippetSetPath));

      for (SnippetInfo snippet : sortSetBySourceFile(currentSnippetSet)) {
        System.out.println("--------processing starts--------");
        System.out.println("---file: " + snippet.getFileName());
        System.out.println("---strt: " + snippet.getStartTime());
        System.out.println("---lgth: " + snippet.getLengthSec());
        System.out.println("---------------------------------");

        //fileExtension = getFileExtension(snippet.getFileName());
        fileName = snippet.getFileName();
        Path
            workPath =
            Paths.get(tempDir.toString(),
                      "snippets/" + snippet.getTagNames().get(0) + File.separator + fileName);

        try {
          byte[] bArray = Files.readAllBytes(workPath);
          ByteArrayInputStream bis = new ByteArrayInputStream(bArray);
          snippet.setFileName(trimFileName(fileName));

          System.out.println("file size: " + (bArray.length / 1024) + "kb.");
          FileInfo
              fileInfo =
              new FileInfo(bis, trimFileName(fileName), bArray.length / 1024,
                           getSnippetLength(bArray));

          //check for which approach needed for current snippet
          if (snippet.getSnippetID() > 0) {
            fileInfo.setFileName(dbAdapter.getFileNameFromSnippetId(snippet.getSnippetID()));
            snippet.setFileName(dbAdapter.getFileNameFromSnippetId(snippet.getSnippetID()));
            System.out.println("edited file being updated in the database (" + dbAdapter
                .getFileNameFromSnippetId(snippet.getSnippetID()) + ")");
            dbAdapter.editSnippet(snippet, fileInfo, snippet.getSnippetID());
            snippetIDs.add(snippet.getSnippetID());
          } else {
            if (fileName.equals(lastFileName)) {
              System.out.println(
                  "----same source file as last snippet. not uploading clip again (" + fileName + ").");
              snippetIDs.add(dbAdapter.writeSnippet(snippet, lastFileID));
              System.out.println("lastFileID: " + lastFileID);
            } else {
              System.out.println("new file, uploading clip to database! (" + fileName + ")");
              int tempSnippetID = dbAdapter.writeSnippet(fileInfo, snippet);
              snippetIDs.add(tempSnippetID);
              lastFileID = dbAdapter.getFileIdFromSnippetId(tempSnippetID);
              lastFileName = fileName;
              //lastTag = snippet.getTagNames().get(0);
            }
          }
          bis.close();
        } catch (IOException e) {
          e.printStackTrace();
          try {
            fs.close();
          } catch (IOException e1) {
            e1.printStackTrace();
          }
          removeDirectory(tempDir);
        }
      }
      snippetSet = dbAdapter.createSnippetSetFromIds(snippetIDs);
    }
    try {
      assert fs != null;
      fs.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    removeDirectory(tempDir);
    return snippetSet;
  }

  /**
   * remove files and delete directory
   * @param dir directory to delete.
   */
  private static void removeDirectory(File dir) {
    if (dir.isDirectory()) {
      File[] files = dir.listFiles();
      if (files != null && files.length > 0) {
        for (File theFile : files) {
          removeDirectory(theFile);
        }
      }
      dir.delete();
    } else {
      dir.delete();
    }
  }

  /**
   * get length of snippet (in secs)
   * @param snippet snippet to check
   * @return length of snippet
   */
  private static double getSnippetLength(byte[] snippet) {
    AudioFormat format;
    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(snippet));
    AudioInputStream ais = null;
    double output = 0;
    try {
      ais = AudioSystem.getAudioInputStream(bis);
      format = ais.getFormat();
      long audioFileLength = ais.getFrameLength();
      output = (audioFileLength + 0.0) / format.getFrameRate();
    } catch (UnsupportedAudioFileException | IOException e) {
      e.printStackTrace();
    }
    try {
      bis.close();
      assert ais != null;
      ais.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    output = Math.round(output * 10000.0) / 10000.0; //4 decimals
    return output;
  }

  /**
   * compare extension of file to determine if its a video file using the file vidcodecs.txt
   * inside /resources
   * @param extension file extension
   * @return bool
   * @throws IOException
   */
  private static boolean vidCodec(String extension) throws IOException {
    BufferedReader in = new BufferedReader(new FileReader("./src/main/resources/vidcodecs.txt"));
    String str;
    List<String> list = new ArrayList<>();
    while ((str = in.readLine()) != null) {
      list.add(str);
    }
    for (String ext : list) {
      if (extension.equals(ext)) {
        return true;
      }
    }
    return false;
  }

  /**
   * compare extension of file to determine if its a sound file using the file soundCodecs.txt
   * inside /resources
   *
   * @param extension file extension
   * @return bool
   * @throws IOException
   */
  private static boolean soundCodec(String extension) throws IOException {
    BufferedReader in = new BufferedReader(new FileReader("./src/main/resources/soundcodecs.txt"));
    String str;
    List<String> list = new ArrayList<>();
    while ((str = in.readLine()) != null) {
      list.add(str);
    }
    for (String ext : list) {
      if (extension.equals(ext)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Collect/Fetch a snippetSet from an xmlFile
   * @param xmlFile compatible xml file
   * @return SnippetSet
   */
  private static SnippetSet getSnippetSet(File xmlFile) {
    XmlStreamer<SnippetSet> xmlStreamer = new XmlStreamer<>();
    SnippetSet snippetSet = null;
    try {
      snippetSet = xmlStreamer.fromStream(SnippetSet.class, xmlFile);
    } catch (JAXBException | FileNotFoundException e) {
      e.printStackTrace();
    }
    return snippetSet;
  }

  /**
   * Returns a File from a ByteArray (from database)
   * @param byteIn audio data as byteArray
   * @param path location of output file.
   * @return clip as File
   */
  private static File byteArrayToFile(byte[] byteIn, String path) {
    File output = new File(path);
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(path);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    try {
      assert fos != null;
      fos.write(byteIn);

    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (fos != null) {
          fos.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return output;
  }

  /**
   * Sorts a SnippetSet according to FileID.
   * Used by zip to ensure files aren't loaded multiple times from database.
   * @param set SnippetSet to sort
   * @return An ArrayList of SnippetInfo's.
   */
  private static ArrayList<SnippetInfo> sortSetByFileID(SnippetSet set) {
    ArrayList<SnippetInfo> output = new ArrayList<>();
    output.addAll(set.getSnippetCollection());
    Collections
        .sort(output, (b1, b2) -> ((Integer) b1.getFileID()).compareTo(((Integer) b2.getFileID())));
    return output;
  }

  /**
   * Sorts a SnippetSet according to FileNames.
   *
   * @param set SnippetSet to sort
   * @return An ArrayList of SnippetInfo's.
   */
  private static ArrayList<SnippetInfo> sortSetBySourceFile(SnippetSet set) {
    ArrayList<SnippetInfo> output = new ArrayList<>();
    output.addAll(set.getSnippetCollection());
    Collections
        .sort(output, (b1, b2) -> (b1.getFileName()).compareTo(b2.getFileName()));
    return output;
  }

  /**
   * Delete a File (zip) when delivery to frontend is complete.
   * @param setName path to zipFile.
   * @return bool
   */
  public boolean deleteUsedZip(String setName) {
    File deadFile = new File(setName);
    if (!deadFile.isFile()) {
      System.out.println(setName + " is not a file.");
      System.out.println("deleting: " + deadFile.toPath().getParent().toFile());
      removeDirectory(deadFile.toPath().getParent().toFile());
      return false;
    }
    System.out.println(deadFile + " deleting....");
    deadFile.delete();
    removeDirectory(deadFile.toPath().getParent().toFile());
    return true;
  }

  /**
   * Collect/fetch all snippets related to a single file.
   * @param fileID ID of file to fetch
   * @return SnippetSet of all related snippets attached to file.
   */
  public SnippetSet getSingleFile(int fileID) {
    return dbAdapter.getAllSnippetFromFile(fileID);
  }

  /**
   * Get file extension from filename
   * @param fileName Filename to trim
   * @return Extension as String
   */
  private static String getFileExtension(String fileName) {
    String output = null;
    int lastIndex = fileName.lastIndexOf('.');
    if (lastIndex >= 0) {
      output = fileName.substring(lastIndex + 1);
    }
    return output;
  }

  /**
   * Trims the Filename of any extensions, used by unzip to deliver clean names to DB.
   * @param fileName Filename to trim
   * @return Filename as String
   */
  private static String trimFileName(String fileName) {
    String output = null;
    int dotIndex = fileName.lastIndexOf('.');
    if (dotIndex >= 0) {
      output = fileName.substring(0, dotIndex);
    }
    return output;
  }
}