/*
 * Copyright 2015 S. Webber
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
package org.oakgp.function.compare;

import static org.oakgp.util.NodeComparator.NODE_COMPARATOR;

import org.oakgp.function.RulesEngine;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;
import org.oakgp.type.Types.Type;

/**
 * Determines if two objects are equal.
 * <p>
 * <b>Note:</b> Equality is checked using {@code Comparable#compareTo(Object)} rather than {@code Object#equals(Object)}.
 */
public final class Equal extends ComparisonOperator {
   private static final Equal SINGLETON = new Equal();

   public static Equal getSingleton() {
      return SINGLETON;
   }

   /** Constructs a function that compares two arguments of the specified type. */
   private Equal() {
      super(true);
   }

   @Override
   protected boolean evaluate(int diff) {
      return diff == 0;
   }

   @Override
   public Node simplify(FunctionNode functionNode) {
      Type returnType = functionNode.getType();
      ChildNodes children = functionNode.getChildren();
      Node simplifiedVersion = simplifyToTrue(children);
      if (simplifiedVersion == null && NODE_COMPARATOR.compare(children.first(), children.second()) > 0) {
         return new FunctionNode(this, returnType, children.second(), children.first());
      } else {
         return simplifiedVersion;
      }
   }

   @Override
   public RulesEngine getEngine(FunctionNode fn) {
      RulesEngine e = new RulesEngine();

      e.addRule(fn, (_e, fact, value) -> {
         _e.addFact(new FunctionNode(NotEqual.getSingleton(), fn.getType(), fn.getChildren()), !value);

         ChildNodes swappedArgs = fn.getChildren().swap(0, 1);

         if (value) {
            _e.addFact(new FunctionNode(GreaterThanOrEqual.getSingleton(), fn.getType(), fn.getChildren()), true);
            _e.addFact(new FunctionNode(GreaterThanOrEqual.getSingleton(), fn.getType(), swappedArgs), true);
            _e.addFact(new FunctionNode(GreaterThan.getSingleton(), fn.getType(), fn.getChildren()), false);
            _e.addFact(new FunctionNode(GreaterThan.getSingleton(), fn.getType(), swappedArgs), false);
         } else {
            _e.addRule(new FunctionNode(GreaterThan.getSingleton(), fn.getType(), fn.getChildren()), (__e, __fact, __value) -> {
               if (!value) {
                  __e.addFact(new FunctionNode(GreaterThan.getSingleton(), fn.getType(), swappedArgs), true);
               }
            });
            _e.addRule(new FunctionNode(GreaterThan.getSingleton(), fn.getType(), swappedArgs), (__e, __fact, __value) -> {
               if (!value) {
                  __e.addFact(new FunctionNode(GreaterThanOrEqual.getSingleton(), fn.getType(), fn.getChildren()), true);
               }
            });
         }
      });

      return e;
   }

   @Override
   public String getDisplayName() {
      return "=";
   }
}
