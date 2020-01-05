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

import static org.oakgp.function.RulesEngineUtils.buildEngine;
import static org.oakgp.type.CommonTypes.booleanType;

import java.util.Map.Entry;

import org.oakgp.Arguments;
import org.oakgp.function.BooleanFunction;
import org.oakgp.function.RulesEngine;
import org.oakgp.function.RulesEngineUtils;
import org.oakgp.function.Signature;
import org.oakgp.function.bool.And;
import org.oakgp.function.bool.Or;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.node.NodeType;

/** Determines if a boolean expression evaluates to {@code false}. */
public final class IsFalse implements BooleanFunction {
   private static final Signature SIGNATURE = Signature.createSignature(booleanType(), booleanType());
   private static final IsFalse SINGLETON = new IsFalse();

   public static IsFalse getSingleton() {
      return SINGLETON;
   }

   private IsFalse() {
   }

   public static FunctionNode negate(Node n) { // TODO move to Utils?
      return new FunctionNode(SINGLETON, booleanType(), ChildNodes.createChildNodes(n));
   }

   @Override
   public Object evaluate(Arguments arguments) {
      boolean b = arguments.first();
      return !b;
   }

   @Override
   public Signature getSignature() {
      return SIGNATURE;
   }

   @Override
   public Node simplify(FunctionNode functionNode) {
      if (functionNode.getChildren().size() != 1) {
         throw new IllegalArgumentException(); // TODO remove this check?
      }

      Node childNode = functionNode.getChildren().first();
      if (childNode.getNodeType() != NodeType.FUNCTION) {
         return null;
      }

      // (false? (false? v0)) -> v0
      FunctionNode childFunctionNode = (FunctionNode) childNode;
      if (childFunctionNode.getFunction() == this) {
         return childFunctionNode.getChildren().first();
      }

      // (false? (or v0 v1)) -> (and (false? v0) (false? v1))
      if (childFunctionNode.getFunction() == Or.getSingleton()) {
         FunctionNode newArg1 = new FunctionNode(this, functionNode.getType(), childFunctionNode.getChildren().first());
         FunctionNode newArg2 = new FunctionNode(this, functionNode.getType(), childFunctionNode.getChildren().second());
         return new FunctionNode(And.getSingleton(), functionNode.getType(), ChildNodes.createChildNodes(newArg1, newArg2));
      }

      // (false (odd? v0)) -> (even? v0)
      for (Entry<Node, Boolean> entry : RulesEngineUtils.buildEngine(childNode, true).getFacts().entrySet()) {
         if (!entry.getValue() && RulesEngineUtils.isEqual(functionNode, entry.getKey())) {
            return entry.getKey();
         }
      }

      return null;
   }

   @Override
   public RulesEngine getEngine(FunctionNode fn) {
      Node childNode = fn.getChildren().first();
      RulesEngine engine = buildEngine(childNode);
      engine.addRule(fn, (e, fact, value) -> e.addFact(childNode, !value));
      engine.addRule(childNode, (e, fact, value) -> e.addFact(fn, !value));
      return engine;
   }
}
