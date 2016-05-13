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
 * @since 2016-04-04
 */

package cocktail.old_controller;

import cocktail.snippet.SnippetSet;

public class CommandResult {
  String resultMessage;
  String command;
  String[] resultData;
  SnippetSet resultSet;

  CommandResult() {}

  /*CommandResult(String command, String resultMessage, SnippetSet resultSet, String... resultData) {
    this.resultMessage = resultMessage;
    this.command = command;
    this.resultSet = resultSet;
    this.resultData = resultData;
  }

  CommandResult(String command, String resultMessage, String... resultData) {
    this.resultMessage = resultMessage;
    this.command = command;
    this.resultSet = null;
    this.resultData = resultData;
  }

  public String getResultMessage() {
    return resultMessage;
  }

  public SnippetSet getResultSet() {
    return resultSet;
  }

  public void setResultSet(SnippetSet resultSet) {
    this.resultSet = resultSet;
  }

  public void setResultMessage(String resultMessage) {
    this.resultMessage = resultMessage;
  }

  public String getCommand() {
    return command;
  }

  public void setCommand(String command) {
    this.command = command;
  }

  public String[] getResultData() {
    return resultData;
  }

  public void setResultData(String[] resultData) {
    this.resultData = resultData;
  }

  @Override
  public String toString() {
    return "CommandResult{" +
           "command='" + command + '\'' +
           ", resultMessage='" + resultMessage + '\'' +
           ", resultData=" + Arrays.toString(resultData) +
           ", resultSet=" + resultSet +
           '}';
  }
  */
}
