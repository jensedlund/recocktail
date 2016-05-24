package cocktail.sound_processing;

import java.io.File;

public interface SoundExtractor {

  /*
  These methods depend on having sox and ffmpeg installed on the server *
  * (if on windows/osx, update paths)!
  MAKE SURE THEY ARE INSTALLED BEFORE USING THIS.
   */
  public boolean vidToWav(File inputFile, File outputFile);

  public boolean soundToWav(File inputFile, File outputFile);

}
