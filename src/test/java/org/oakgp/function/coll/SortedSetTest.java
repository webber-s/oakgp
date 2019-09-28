/*
 * Copyright 2019 S. Webber
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.oakgp.function.coll;

import static org.oakgp.TestUtils.asSet;
import static org.oakgp.type.CommonTypes.doubleType;
import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.listType;

import java.util.Collections;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.hof.Map;
import org.oakgp.function.math.Logarithm;
import org.oakgp.node.ConstantNode;
import org.oakgp.primitive.FunctionSet;
import org.oakgp.util.FunctionSetBuilder;

public class SortedSetTest extends AbstractFunctionTest {
   @Override
   protected SortedSet getFunction() {
      return SortedSet.getSingleton();
   }

   @Override
   public void testEvaluate() {
      ConstantNode emptyList = new ConstantNode(Collections.emptyList(), listType(integerType()));
      evaluate("(sorted-set v0)").assigned(emptyList).to(asSet());
      evaluate("(sorted-set [7])").to(asSet(7));
      evaluate("(sorted-set [7 7 7])").to(asSet(7));
      evaluate("(sorted-set [8 4 7])").to(asSet(4, 7, 8));
      evaluate("(sorted-set [2 5 3 6 9 8 2 7])").to(asSet(2, 3, 5, 6, 7, 8, 9));
   }

   @Override
   public void testCanSimplify() {
      simplify("(sorted-set (sorted-set v0))").with(listType(integerType())).to("(sorted-set v0)");
      simplify("(sorted-set (set v0))").with(listType(integerType())).to("(sorted-set v0)");
      simplify("(sorted-set (sort v0))").with(listType(integerType())).to("(sorted-set v0)");

      simplify("(sorted-set (map log (sort v0)))").with(listType(doubleType())).to("(sorted-set (map log v0))");
      simplify("(sorted-set (map log (sorted-set v0)))").with(listType(doubleType())).to("(sorted-set (map log (set v0)))");

      simplify("(sorted-set (map log (sort (map log (sort v0)))))").with(listType(doubleType())).to("(sorted-set (map log (map log v0)))");
      simplify("(sorted-set (map log (sorted-set (map log (sorted-set v0)))))").with(listType(doubleType()))
            .to("(sorted-set (map log (set (map log (set v0)))))");

      simplify("(sorted-set (map log (sort (map log v0))))").with(listType(doubleType())).to("(sorted-set (map log (map log v0)))");
      simplify("(sorted-set (map log (sorted-set (map log v0))))").with(listType(doubleType())).to("(sorted-set (map log (set (map log v0))))");
   }

   @Override
   public void testCannotSimplify() {
   }

   @Override
   protected FunctionSet getFunctionSet() {
      return new FunctionSetBuilder().add(getFunction(), integerType()).add(Set.getSingleton(), integerType()).add(Sort.getSingleton(), integerType())
            .add(SortedSet.getSingleton(), doubleType()).add(Set.getSingleton(), doubleType()).add(Sort.getSingleton(), doubleType())
            .add(Map.getSingleton(), doubleType(), doubleType()).add(Logarithm.getSingleton(), doubleType()).build();
   }
}
