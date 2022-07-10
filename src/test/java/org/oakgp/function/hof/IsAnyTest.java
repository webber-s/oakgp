/*
 * Copyright 2022 S. Webber
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
package org.oakgp.function.hof;

import static org.oakgp.type.CommonTypes.integerType;
import static org.oakgp.type.CommonTypes.listType;

import java.util.Collections;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.node.ConstantNode;

public class IsAnyTest extends AbstractFunctionTest {
   @Override
   protected IsAny getFunction() {
      return new IsAny();
   }

   @Override
   public void testEvaluate() {
      ConstantNode emptyList = new ConstantNode(Collections.emptyList(), listType(integerType()));
      evaluate("(any? zero? v0)").assigned(emptyList).to(false);
      evaluate("(any? zero? [0])").to(true);
      evaluate("(any? zero? [1])").to(false);
      evaluate("(any? zero? [0 0 0])").to(true);
      evaluate("(any? zero? [1 0 0])").to(true);
      evaluate("(any? zero? [0 1 0])").to(true);
      evaluate("(any? zero? [0 0 1])").to(true);
      evaluate("(any? zero? [1 1 0])").to(true);
      evaluate("(any? zero? [1 0 1])").to(true);
      evaluate("(any? zero? [0 1 1])").to(true);
      evaluate("(any? zero? [1 1 1])").to(false);
   }

   @Override
   public void testCanSimplify() {
      simplify("(any? zero? [0])").to("true");
      simplify("(any? zero? [1])").to("false");
   }

   @Override
   public void testCannotSimplify() {
   }
}