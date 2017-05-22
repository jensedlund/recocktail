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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

/**
 * Implementation of StreamingService using javax JAXB.
 * @param <T> Class to strem to/from xml.
 */
public class XmlStreamer<T> implements StreamingService<T> {

  @Override
  public void toStream(Class<T> tClass, T tObject, File file)
      throws JAXBException, FileNotFoundException {
    JAXBContext jaxbContext = JAXBContext.newInstance(tClass);
    Marshaller marshaller = jaxbContext.createMarshaller();

    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    JAXBElement<T> rootElement = new JAXBElement<T>(new QName(tClass.getName()), tClass, tObject);
    marshaller.marshal(rootElement, new FileOutputStream(file));
  }

  @Override
  public T fromStream(Class<T> tClass, File file) throws JAXBException, FileNotFoundException {
    JAXBContext jaxbContext = JAXBContext.newInstance(tClass);
    StreamSource xml = new StreamSource(new FileInputStream(file));
    Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
    JAXBElement<T> jaxbElement = unmarshaller.unmarshal(xml, tClass);
    return jaxbElement.getValue();
  }
}
