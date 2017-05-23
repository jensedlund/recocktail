/*
 * Copyright 2016 Jens Edlund, Joakim Gustafson, Jonas Beskow, Ulrika Goloconda Fahlen, Jan Eriksson, Marcus Viden
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @version 1.0
 * @since 2016-04-12
 */

package cocktail.service.snippet.entity;

import java.io.ByteArrayInputStream;

/**
 * FileInfo describes the necessary data for a file to be added to database. Not the same as a
 * snippet set.
 */
public class FileInfo {
  private ByteArrayInputStream inputStream;
  private String fileName;
  private double fileLenSec;
  private int fileSizeKb;
  private int fileID;


  public FileInfo(ByteArrayInputStream inputStream, String fileName,int fileSizeKb, double fileLenSec){
    this.inputStream = inputStream;
    this.fileName = fileName;
    this.fileLenSec = fileLenSec;
    this.fileSizeKb = fileSizeKb;
  }

  public FileInfo(){}

  public int getFileSizeKb() {
        return fileSizeKb;
    }

  public void setFileSizeKb(int fileSizeKb) {
        this.fileSizeKb = fileSizeKb;
    }

  public ByteArrayInputStream getInputStream() {
        return inputStream;
    }

  public void setInputStream(ByteArrayInputStream inputStream) {
        this.inputStream = inputStream;
    }

  public String getFileName() {
        return fileName;
    }

  public void setFileName(String fileName) {
        this.fileName = fileName;
    }

  public double getFileLenSec() {
        return fileLenSec;
    }

  public void setFileLenSec(double fileLenSec) {
        this.fileLenSec = fileLenSec;
    }

  public int getFileID() {
        return fileID;
    }

  public void setFileID(int fileID) {
        this.fileID = fileID;
    }
}
