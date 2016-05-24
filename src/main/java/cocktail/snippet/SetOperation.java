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

package cocktail.snippet;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enum implementing different set operations on sorted sets.
 */

public enum SetOperation implements SetOperationInterface {
  UNION {
    @Override
    public <T> SortedSet<T> calculate(SortedSet<T> setA,
                                            SortedSet<T> setB) {
      return Stream.concat(setA.stream(),setB.stream())
          .collect(Collectors.toCollection(TreeSet<T>::new));
    }
  },
  INTERSECT {
    @Override
    public <T> SortedSet<T> calculate(SortedSet<T> setA,
                                            SortedSet<T> setB) {
      return setA.stream().filter(setB::contains)
          .collect(Collectors.toCollection(TreeSet<T>::new));
    }
  },
  COMPLEMENT {
    @Override
    public <T> SortedSet<T> calculate(SortedSet<T> setA,
                                            SortedSet<T> setB) {
      return setB.stream().filter(s -> !setA.contains(s))
          .collect(Collectors.toCollection(TreeSet<T>::new));
    }
  }
}

