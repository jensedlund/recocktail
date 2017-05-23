package cocktail.service.snippet.entity;

import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import cocktail.stream_io.XmlStreamer;

import static org.junit.Assert.assertEquals;

/**
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
 * @since 25/03/16
 * */

public class XmlStreamerTest {

  @Test
  public void testXmlStreaming() {
    MockSnippetSet snippetSet = new MockSnippetSet();

    // Populate a list and send it to snippetSet.
    List<MockSnippetInfo> aList = new ArrayList<>();
    aList.add(new MockSnippetInfo(1, 2));
    aList.add(new MockSnippetInfo(3, 4));
    aList.add(new MockSnippetInfo(5, 6));
    snippetSet.setSnippetInfoSet(aList);

    // Verify that all fields can be read, first get the list back
    // then check all fields.
    aList = snippetSet.getSnippetInfoSet();
    assertEquals("Id not equal.",1,aList.get(0).getSnippetId());
    assertEquals("File not equal.",2,aList.get(0).getFileId());
    assertEquals("Id not equal.",3,aList.get(1).getSnippetId());
    assertEquals("File not equal.",4,aList.get(1).getFileId());
    assertEquals("Id not equal.",5,aList.get(2).getSnippetId());
    assertEquals("File not equal.",6,aList.get(2).getFileId());

    // Create xml, read it back to new object
    File xmlFile = new File("build/tmp/test.xml");
    XmlStreamer<MockSnippetSet> xmlStreamer = new XmlStreamer<>();
    MockSnippetSet snippetSet1 = new MockSnippetSet();
    try {
      xmlStreamer.toStream(MockSnippetSet.class,snippetSet,xmlFile);
      snippetSet1 = xmlStreamer.fromStream(MockSnippetSet.class, xmlFile);
    } catch (JAXBException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    // Check of new object have the same fields.
    List<MockSnippetInfo> aList1 = snippetSet1.getSnippetInfoSet();
    assertEquals("Id not equal.",1,aList1.get(0).getSnippetId());
    assertEquals("File not equal.",2,aList1.get(0).getFileId());
    assertEquals("Id not equal.",3,aList1.get(1).getSnippetId());
    assertEquals("File not equal.",4,aList1.get(1).getFileId());
    assertEquals("Id not equal.",5,aList1.get(2).getSnippetId());
    assertEquals("File not equal.",6,aList1.get(2).getFileId());
  }
}
