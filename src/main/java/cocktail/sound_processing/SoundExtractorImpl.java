package cocktail.sound_processing;

import java.io.File;
import java.io.IOException;

/**
 * Converts and extracts sound from original source files.
 * Audio files are converted into 16bit, 44khz mono wav's.
 * Video files have their sound extracted with the same specs as above.
 *
 * ffmpeg and sox are required to be installed on the server and their paths updated
 * if needed (for windows and osx esp).
 *
 */
public class SoundExtractorImpl implements SoundExtractor {

  /**
   * Extracts and converts the audio from a video file. Rely on having ffmpeg installed on the
   * system.
   * @param inputFile sourcefile
   * @param outputFile outputfile
   * @return bool.
   */
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
      Process
          convert =
          Runtime.getRuntime().exec("sox " + inputFile + " -c 1 -r 44000 -b 16 " + outputFile);
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
