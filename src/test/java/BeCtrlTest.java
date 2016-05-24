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
 * @since 2016-04-06
 */


/*import cocktail.old_controller.BeCtrl;
import cocktail.old_controller.CommandResult;
import cocktail.stream_io.StreamingService;
import cocktail.stream_io.XmlStreamer;

public class BeCtrlTest {
//  RESTORE_CONTEXT
//      STORE_CONTEXT
//  GET_SAVED_CONTEXTS
//      GET_WORKING_SETS
//  SET_OP
//      GET_SET
//  SEARCH
//      ZIP
//  UNZIP
//      NOP

  BeCtrl beCtrl = new BeCtrl();
//  StreamingService<ControllerInstruction> ctrlInstXmlConv = new XmlStreamer<>();
  StreamingService<CommandResult> comResXmlConv = new XmlStreamer<>();

  @Test
  public void testSearchCommand() {

 /*   Command command = Commands.SEARCH.create(beCtrl, new String[]{"clap", "10", "false"});
    CommandResult commandResult = command.execute();

    System.out.println(commandResult);
    System.out.println(beCtrl.getStorageUnit().getWorkingSetNames());
//    File instXml = new File("build/tmp/searchInst.xml");
    File resXml = new File("build/tmp/searchRes.xml");
    try {
//      ctrlInstXmlConv.toStream(ControllerInstruction.class,searchInst,instXml);
      comResXmlConv.toStream(CommandResult.class, commandResult,resXml);
    } catch (JAXBException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

  }

//  @Test
//  public void testGetWorkingSetsCommand() {
//
//    ControllerInstruction
//        searchInst =
//        new ControllerInstruction("search", new String[]{"clap", "10", "false"});
//    CommandResult commandResult = beCtrl.runCommand(searchInst);
//
//    System.out.println(commandResult);
//    System.out.println(beCtrl.getStorageUnit().getWorkingSetNames());
//    File instXml = new File("build/tmp/searchInst.xml");
//    File resXml = new File("build/tmp/searchRes.xml");
//    try {
//      ctrlInstXmlConv.toStream(ControllerInstruction.class,searchInst,instXml);
//      comResXmlConv.toStream(CommandResult.class, commandResult,resXml);
//    } catch (JAXBException e) {
//      e.printStackTrace();
//    } catch (FileNotFoundException e) {
//      e.printStackTrace();
//    }

//  }
          */

