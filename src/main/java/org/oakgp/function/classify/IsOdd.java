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
package org.oakgp.function.classify;

import static org.oakgp.type.CommonTypes.booleanType;
import static org.oakgp.type.CommonTypes.integerType;

import java.util.Collections;
import java.util.Set;

import org.oakgp.Assignments;
import org.oakgp.function.BooleanFunction;
import org.oakgp.function.Signature;
import org.oakgp.node.AutomaticallyDefinedFunctions;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

/** Determines if a number is odd. */
public final class IsOdd implements BooleanFunction {
   private static final Signature SIGNATURE = Signature.createSignature(booleanType(), integerType());
   private static final IsOdd SINGLETON = new IsOdd();

   public static IsOdd getSingleton() {
      return SINGLETON;
   }

   private IsOdd() {
   }

   @Override
   public Object evaluate(ChildNodes arguments, Assignments assignments, AutomaticallyDefinedFunctions adfs) {
      int i = arguments.first().evaluate(assignments, adfs);
      return i % 2 != 0;
   }

   @Override
   public Signature getSignature() {
      return SIGNATURE;
   }

   @Override
   public Node getOpposite(FunctionNode fn) {
      return new FunctionNode(IsEven.getSingleton(), fn.getType(), fn.getChildren());
   }

   @Override
   public Set<Node> getIncompatibles(FunctionNode fn) {
      return Collections.singleton(new FunctionNode(IsZero.getSingleton(), fn.getType(), fn.getChildren()));
   }
}
