package cocktail.sound_processing;

import java.io.File;

/**
 * Converts and extracts sound from original source files.
 * Audio files are converted into 16bit, 44khz mono wav's.
 * Video files have their sound extracted with the same specs as above.
 *
 * ffmpeg and sox are required to be installed on the server and their paths updated
 * if needed (for windows and osx esp).
 *
 */
public interface SoundExtractor {

  /*
  These methods depend on having sox and ffmpeg installed on the server *
  * (if on windows/osx, update paths)!
  *
  *
  MAKE SURE THEY ARE INSTALLED AND HAVE CORRECT PATHS BEFORE USING THIS.
   */

  /**
   * Extracts and converts the audio from a video file. Rely on having ffmpeg installed on the
   * system.
   * @param inputFile sourcefile
   * @param outputFile outputfile
   * @return bool.
   */
  boolean vidToWav(File inputFile, File outputFile);

  /**
   * Converts the audio from a audio file. Rely on having sox installed on the
   * system.
   * @param inputFile
   * @param outputFile
   * @return bool
   */
  boolean soundToWav(File inputFile, File outputFile);

}
