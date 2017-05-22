package cocktail.sound_processing;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Handles trimming of sound files. Cutting short snippet from a longer clip.
 */
public interface SoundProcess {

  /**
   * Trims a long audio file into a short snippet
   * @param byteArray AudioClip to trim
   * @param startSecond Start time of Snippet.
   * @param secondsToCopy Duration of Snippet.
   * @return Trimmed byteArray.
   */
  byte[] trimAudioClip(byte[] byteArray, double startSecond, double secondsToCopy);

  /**
   * Get length of a wav as byteArray (in seconds).
   * @param snippet Snippet to check.
   * @return Snippet length as double.
   */
  double getSnippetLength(byte[] snippet);
}
