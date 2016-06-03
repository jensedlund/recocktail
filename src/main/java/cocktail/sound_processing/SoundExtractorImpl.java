package cocktail.sound_processing;

import java.io.File;
import java.io.IOException;


public class SoundExtractorImpl implements SoundExtractor {


  public boolean vidToWav(File inputFile, File outputFile) {

    try {
      Process convert = Runtime.getRuntime().exec
          ("ffmpeg -i " + inputFile + " -v quiet -y -vn -acodec pcm_s16le -ar 44100 -ac 1 "
           + outputFile);
      try {
        convert.waitFor();
      } catch (InterruptedException e) {
        e.printStackTrace();
        return false;
      }
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }


  public boolean soundToWav(File inputFile, File outputFile) {

    try {
      Process convert =
//       Runtime.getRuntime().exec("C:\\sox\\sox.exe " + inputFile + " -c 1 -r 44000 -b 16 " + outputFile);
//       Runtime.getRuntime().exec("sox " + inputFile + " -c 1 -r 44000 -b 16 " + outputFile);
      //osx example
      Runtime.getRuntime().exec("/usr/local/bin/sox " + inputFile + " -c 1 -r 44000 -b 16 " + outputFile);
      try {
        convert.waitFor();
      } catch (InterruptedException e) {
        e.printStackTrace();
        return false;
      }
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
    return true;
  }
}
