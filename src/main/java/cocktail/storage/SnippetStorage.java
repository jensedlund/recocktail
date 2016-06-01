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
 * @since 2016-04-05
 */

package cocktail.storage;

import java.util.List;
import java.util.Set;

import cocktail.snippet.SnippetSet;

/**
 * A SnippetStorage object handles generated snippet sets by name so that there is a history of
 * created sets that all can be retrieved.
 *
 * @version 1.0
 * @since 05/04/16
 */
public interface SnippetStorage {

  /**
   * Get the latest added set.
   * @return The last added snippet set.
   */
  SnippetSet getLatestSet();

  boolean addSet(SnippetSet snippetSet);

  boolean removeSet(String snippetSetName);

  SnippetSet renameSet(String oldSetName, String newSetName);

  List<String> getWorkingSetNames();

  String[] storedContextIds();

  SnippetSet getSet(String id);

  boolean restoreContext(String id);

  boolean storeContext(String id);

  void setLogNote(String log, SnippetSet snippetSet);

  Set<String> getAllSetNames();

  void deleteSavedSets(List<String> savedSetNames );
}
