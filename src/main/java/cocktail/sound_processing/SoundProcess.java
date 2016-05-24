package cocktail.sound_processing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public interface SoundProcess {

  /*
  Trims a snippet of sound from a longer audio clip.

   */

  byte[] trimAudioClip(byte[] byteArray, double startSecond, double secondsToCopy);
  double getSnippetLength(byte[] snippet);
}
