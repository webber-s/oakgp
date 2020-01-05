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
package org.oakgp.function.bool;

import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.integerType;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.type.Types.Type;

public class AndTest extends AbstractFunctionTest {
   @Override
   protected And getFunction() {
      return And.getSingleton();
   }

   @Override
   public void testEvaluate() {
      evaluate("(and true true)").to(true);
      evaluate("(and true false)").to(false);
      evaluate("(and false true)").to(false);
      evaluate("(and false false)").to(false);

      evaluate("(and true (and true true))").to(true);
      evaluate("(and true (and true false))").to(false);
      evaluate("(and true (and false true))").to(false);
      evaluate("(and false (and true true))").to(false);
      evaluate("(and true (and false false))").to(false);
      evaluate("(and false (and false true))").to(false);
      evaluate("(and false (and true false))").to(false);
      evaluate("(and false (and false false))").to(false);
   }

   @Override
   public void testCanSimplify() {
      simplify("(and (xor (zero? v0) (even? v0)) (odd? v0))").with(integerType()).to("false");
      simplify("(and (xor (> v0 v1) (> v1 v0)) (= v0 v1))").with(integerType(), integerType()).to("false");
      simplify("(and (xor (> v0 v1) (>= v0 v1)) (= v0 v1))").with(integerType(), integerType()).to("false");
      simplify("(and (xor (> v1 v0) (> v0 v1)) (!= v1 v0))").with(integerType(), integerType()).to("(!= v0 v1)");
      simplify("(and (xor (> v2 v3) (>= v0 v1)) (= v0 v1))").with(integerType(), integerType(), integerType(), integerType()).to("(and (>= v3 v2) (= v0 v1))");

      simplify("(and v0 (false? v0))").with(booleanType()).to("false");

      simplify("(and true v0)").with(booleanType()).to("v0");
      simplify("(and v0 true)").with(booleanType()).to("v0");

      simplify("(and false v0)").with(booleanType()).to("false");
      simplify("(and v0 false)").with(booleanType()).to("false");

      simplify("(and v0 v0)").with(booleanType()).to("v0");

      Type[] types = { booleanType(), booleanType(), booleanType(), booleanType() };
      simplify("(and v0 (and v1 (and v2 v3)))").with(types).to("(and v3 (and v2 (and v1 v0)))");
      simplify("(and v3 (and v2 (and v1 v0)))").with(types).to("(and v3 (and v2 (and v1 v0)))");
      simplify("(and v1 (and v0 (and v3 v2)))").with(types).to("(and v3 (and v2 (and v1 v0)))");
      simplify("(and (and v1 v0) (and v3 v2)))").with(types).to("(and v3 (and v2 (and v1 v0)))");

      simplify("(and (and v1 v0) (and v2 v0)))").with(types).to("(and v2 (and v1 v0))");

      simplify("(and (false? v0) v0)").with(types).to("false");
      simplify("(and (and (false? v0) v1) v0)").with(booleanType(), booleanType()).to("false");
      simplify("(and (false? (and (false? v0) v1)) v0)").with(booleanType(), booleanType()).to("v0");
      simplify("(and (or (false? v0) v1) v0)").with(booleanType(), booleanType()).to("(and v1 v0)");
      simplify("(and (= v0 v1) (or (> v1 v0) (> v0 v1)))").with(booleanType(), booleanType()).to("false");

      simplify("(and (or v0 v1) v0)").with(types).to("v0");

      simplify("(and (= v0 v1) (!= v0 v1))").with(types).to("false");

      simplify("(and (= v0 v1) (!= v0 v1))").with(integerType(), integerType()).to("false");
      simplify("(and (> v0 v1) (> v1 v0))").with(integerType(), integerType()).to("false");
      simplify("(and (> v0 v1) (>= v1 v0))").with(integerType(), integerType()).to("false");
      simplify("(and (> v0 v1) (!= v0 v1))").with(integerType(), integerType()).to("(> v0 v1)");
      simplify("(and (> v0 v1) (>= v0 v1))").with(integerType(), integerType()).to("(> v0 v1)");

      simplify("(and (>= v1 v0) (>= v0 v1))").with(integerType(), integerType()).to("(= v0 v1)");

      simplify("(and (false? (pos? v0)) (false? (neg? v0)))").with(integerType()).to("(zero? v0)");
      simplify("(and (zero? v0) (or (pos? v0) (neg? v0)))").with(integerType()).to("false");
      simplify("(and (false? (zero? v0)) (or (pos? v0) (neg? v0)))").with(integerType()).to("(false? (zero? v0))");
      simplify("(and (zero? v0) (or (even? v0) (odd? v0)))").with(integerType()).to("(zero? v0)");
   }

   @Override
   public void testCannotSimplify() {
      cannotSimplify("(and (or (> v3 v2) (>= v0 v1)) (or (> v1 v0) (>= v2 v3)))", integerType(), integerType(), integerType(), integerType());
      // TODO replace with: (!= v1 v0)
      cannotSimplify("(and (xor (>= v1 v0) (>= v0 v1)) (!= v0 v1))", integerType(), integerType());
   }
}
