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
 * @since 2016-03-16
 */

package cocktail.stream_io;

import java.io.File;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBException;

/**
 * Generic interface to stream out a xml file from a class of type T.
 *
 * @param <T> Class to stream.
 */
public interface StreamingService<T> {

  /**
   * Take a class of type T and stream it out as an xml to file.
   * @param tClass A class reference of type T.
   * @param tObject The object to stream out.
   * @param file File target.
   * @throws JAXBException
   * @throws FileNotFoundException
   */
  void toStream(Class<T> tClass, T tObject, File file)
      throws JAXBException, FileNotFoundException;

  /**
   * Create an object of type T from xml file.
   * @param tClass A class reference of type T.
   * @param file The file to stream in.
   * @return A new object of class T created from the file.
   * @throws JAXBException
   * @throws FileNotFoundException
   */

  T fromStream(Class<T> tClass, File file) throws JAXBException, FileNotFoundException;
}
