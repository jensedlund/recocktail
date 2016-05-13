package cocktail.sound_processing;

import java.io.File;

public interface SoundExtractor {

  public boolean vidToWav(File inputFile, File outputFile);
  public boolean soundToWav(File inputFile, File outputFile);

}
