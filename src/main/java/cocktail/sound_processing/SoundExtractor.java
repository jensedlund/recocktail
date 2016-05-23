package cocktail.sound_processing;

import java.io.File;

public interface SoundExtractor {

  /*
  These methods depend on having sox and ffpeg installed on the system!
  MAKE SURE THEY ARE INSTALLED BEFORE USING THIS.
   */
  public boolean vidToWav(File inputFile, File outputFile);
  public boolean soundToWav(File inputFile, File outputFile);

}
