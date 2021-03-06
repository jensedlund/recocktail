package cocktail.sound_processing;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;


public class SoundProcessImpl implements SoundProcess {


  public byte[] trimAudioClip(byte[] byteArray, double startSecond,
                              double secondsToCopy) {
    AudioInputStream audioInputStream = null;
    AudioInputStream shortenedAudioStream = null;
    ByteArrayOutputStream preOutput = null;
    //ByteArrayInputStream output = null;
    byte[] output = null;
    ByteArrayInputStream bais = new ByteArrayInputStream(byteArray);

    try {
      AudioFileFormat fileFormat = AudioSystem.getAudioFileFormat(bais);
      audioInputStream = AudioSystem.getAudioInputStream(bais);
      AudioFormat format = fileFormat.getFormat();

      int bytesPerSecond = format.getFrameSize() * (int) format.getFrameRate();
      audioInputStream.skip((long) (startSecond * bytesPerSecond));
      double framesOfAudioToCopy = secondsToCopy * (int) format.getFrameRate();
      shortenedAudioStream =
          new AudioInputStream(audioInputStream, format, (long) framesOfAudioToCopy);

      preOutput = new ByteArrayOutputStream();
      AudioSystem.write(shortenedAudioStream, fileFormat.getType(), preOutput);

    } catch (Exception e) {
      System.out.println(e);
    } finally {
      if (audioInputStream != null) {
        try {
          audioInputStream.close();
        } catch (Exception e) {
          System.out.println(e);
        }
      }
      if (shortenedAudioStream != null) {
        try {
          shortenedAudioStream.close();
        } catch (Exception e) {
          System.out.println(e);
        }
      }
      try {
        bais.close();
      } catch (Exception e) {
        System.out.println(e);
      }
    }
    if (preOutput != null) {
      output = preOutput.toByteArray();
    }
    return output;
  }


  public double getSnippetLength(byte[] snippet) {
    BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(snippet));
    AudioInputStream audioInputStream = null;
    double snippetLength = 0;
    try {
      try {
        audioInputStream = AudioSystem.getAudioInputStream(bis);
        AudioFormat format = audioInputStream.getFormat();
        snippetLength = audioInputStream.getFrameLength() / format.getFrameRate();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (UnsupportedAudioFileException e) {
      e.printStackTrace();
    } finally {
      try {
        bis.close();
        if (audioInputStream != null) {
          audioInputStream.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    return snippetLength;
  }
}
