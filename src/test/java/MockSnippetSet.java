import java.util.List;

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
 */
public class MockSnippetSet {
  private List<MockSnippetInfo> snippetInfoSet;

  MockSnippetSet() {}

  public List<MockSnippetInfo> getSnippetInfoSet() {
    return snippetInfoSet;
  }

  public void setSnippetInfoSet(List<MockSnippetInfo> snippetInfoSet) {
    this.snippetInfoSet = snippetInfoSet;
  }

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    for (MockSnippetInfo s : snippetInfoSet) {
      stringBuilder.append(s.toString());
      stringBuilder.append("\n");
    }

    return stringBuilder.toString();
  }
}
