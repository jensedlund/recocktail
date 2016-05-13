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


public enum Commands implements CommandFactory {
  /*

  RESTORE_CONTEXT {
    @Override
    public Command create(BeCtrl beCtrl, String... args) {
      return () -> {
        beCtrl.getStorageUnit().restoreContext(args[0]);
        return new CommandResult("RESTORE_CONTEXT", "Context restored ok.", "true");
      };
    }
  },

  STORE_CONTEXT {
    @Override
    public Command create(BeCtrl beCtrl, String... args) {
      return () -> {
        beCtrl.getStorageUnit().storeContext(args[0]);
        return new CommandResult("STORE_CONTEXT", "Context stored ok.", "true");
      };
    }
  },

  GET_SAVED_CONTEXTS {
    @Override
    public Command create(BeCtrl beCtrl, String... args) {
      return () -> {
        String[] contextIds = beCtrl.getStorageUnit().storedContextIds();
        return new CommandResult("GET_SAVED_CONTEXTS", "Returning your saved contexts.", contextIds);
      };
    }
  },

  GET_WORKING_SETS {
    @Override
    public Command create(BeCtrl beCtrl, String... args) {
      return () -> {
        List<String> workingSetsList = beCtrl.getStorageUnit().getWorkingSetNames();
        String[] workingSets = new String[workingSetsList.size()];
        workingSetsList.toArray(workingSets);
        return new CommandResult("GET_WORKING_SETS", "Working set ids.", workingSets);
      };
    }
  },

  SET_OP {
    @Override
    public Command create(BeCtrl beCtrl, String... args) {
      return () -> {
        SnippetStorage storageUnit = beCtrl.getStorageUnit();

        SnippetSet snippetSetA = storageUnit.getSet(args[0]);
        SnippetSet snippetSetB = storageUnit.getSet(args[1]);
        SetOperation op = SetOperation.valueOf(args[2]);

        SnippetSet resultSnippetSet = snippetSetA.setOperation(snippetSetB,op);

        storageUnit.addSet(resultSnippetSet);

        return new CommandResult("SET_OP", op.name() + " operation done.",resultSnippetSet,
                                 resultSnippetSet.getSetName());
      };
    }
  },

  GET_SET {
    @Override
    public Command create(BeCtrl beCtrl, String... args) {
      return () -> {

        SnippetStorage storageUnit = beCtrl.getStorageUnit();
        SnippetSet snippetSet = storageUnit.getSet(args[0]);

        return new CommandResult("GET_SET", "Here is your Snippet Set.", snippetSet);
      };
    }
  },

  SEARCH {
    @Override
    public Command create(BeCtrl beCtrl, String... args) {
      return () -> {
        DbAdapter dbAdapter = new DbAdapterImpl();
        SnippetStorage snippetStorage = beCtrl.getStorageUnit();

        String[] tags = new String[] {args[0]};
        double maxLen = Double.valueOf(args[1]);
        boolean exclusive = Boolean.valueOf(args[2]);

        SnippetSet snippetSet = dbAdapter.search(Arrays.asList(tags), maxLen, exclusive);
        snippetStorage.addSet(snippetSet);
        return new CommandResult("SEARCH", "Here is your search result.", snippetSet);
      };
    }
  },

  ZIP {
    @Override
    public Command create(BeCtrl beCtrl, String... args) {
      return () -> {
        String fileStr = args[0];
        SnippetStorage storageUnit = beCtrl.getStorageUnit();
        SnippetSet snippetSet = storageUnit.getSet(args[0]);
        ArchiveHandler archiveHandler = new ArchiveHandler();
        archiveHandler.zip(snippetSet);
        return new CommandResult("ZIP", "Zipping done");
      };
    }
  },

  UNZIP {
    @Override
    public Command create(BeCtrl beCtrl, String... args) {
      return () -> {
        CommandResult commandResult = new CommandResult();
        commandResult.setCommand("unzip");
        String fileStr = args[0];
        ArchiveHandler archiveHandler = new ArchiveHandler();
        archiveHandler.unzip(fileStr);
        commandResult.setResultMessage("Uzip done...");
        return new CommandResult("UNZIP", "Unzip done...");
      };
    }
  },

  NOP {
    @Override
    public Command create(BeCtrl beCtrl, String... args) {
      return () -> {
        return new CommandResult("NOP","NOP","NOP");
      };
    }
  }
  */
}
