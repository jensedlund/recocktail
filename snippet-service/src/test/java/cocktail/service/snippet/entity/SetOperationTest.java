package cocktail.service.snippet.entity;/*
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
 * @since 2016-04-07
 */

import org.junit.Test;

import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.Assert.assertArrayEquals;

/**
 * @version 1.0
 * @since 07/04/16
 */
public class SetOperationTest {

  @Test
  public void testUnion() {
    Integer[] arrA = new Integer[] {5,3,4,2,1};
    Integer[] arrB = new Integer[] {9,7,3,2,1};
    Integer[] expected = new Integer[] {1, 2, 3, 4, 5, 7, 9};

    SortedSet<Integer> setA = new TreeSet<>();
    setA.addAll(Arrays.asList(arrA));

    SortedSet<Integer> setB = new TreeSet<>();
    setB.addAll(Arrays.asList(arrB));

    SortedSet<Integer> setC = SetOperation.UNION.calculate(setA,setB);
    assertArrayEquals("Union operation unexpected result" + setC.toString(), expected,
                      setC.toArray());
  }

  @Test
  public void testIntersect() {
    Integer[] arrA = new Integer[] {5,3,4,2,1};
    Integer[] arrB = new Integer[] {9,7,3,2,1};
    Integer[] expected = new Integer[] {1, 2, 3};

    SortedSet<Integer> setA = new TreeSet<>();
    setA.addAll(Arrays.asList(arrA));

    SortedSet<Integer> setB = new TreeSet<>();
    setB.addAll(Arrays.asList(arrB));

    SortedSet<Integer> setC = SetOperation.INTERSECT.calculate(setA,setB);
    assertArrayEquals("Intersect operation unexpected result" + setC.toString(), expected,
                      setC.toArray());
  }

  @Test
  public void testComplement() {
    Integer[] arrA = new Integer[] {5,3,4,2,1};
    Integer[] arrB = new Integer[] {9,7,3,2,1};
    Integer[] expected = new Integer[] {7, 9};

    SortedSet<Integer> setA = new TreeSet<>();
    setA.addAll(Arrays.asList(arrA));

    SortedSet<Integer> setB = new TreeSet<>();
    setB.addAll(Arrays.asList(arrB));

    SortedSet<Integer> setC = SetOperation.COMPLEMENT.calculate(setA,setB);
    assertArrayEquals("Complement operation unexpected result" + setC.toString(), expected,
                      setC.toArray());
  }

}
