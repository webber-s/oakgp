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
package org.oakgp.function.hof;

import static java.util.Collections.unmodifiableList;
import static org.oakgp.type.CommonTypes.functionType;
import static org.oakgp.type.CommonTypes.listType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.function.MapperFunction;
import org.oakgp.function.Signature;
import org.oakgp.node.ChildNodes;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.type.Types;
import org.oakgp.type.Types.Type;

/**
 * Returns the result of applying a function to each element of a collection.
 * <p>
 * Returns a new collection that exists of the result of applying the function (specified by the first argument) to each
 * element of the collection (specified by the second argument).
 *
 * @see <a href="http://en.wikipedia.org/wiki/Map_(higher-order_function)">Wikipedia</a>
 */
public final class Map implements MapperFunction { // TODO rename to transform
   private static final Map SINGLETON = new Map();

   public static Map getSingleton() {
      return SINGLETON;
   }

   private final Signature signature;

   /**
    * Creates a higher order functions that applies a function to each element of a collection.
    *
    * @param from the type of the elements contained in the collection provided as an argument to the function
    * @param to the type of the elements contained in the collection returned by the function
    */
   private Map() {
      Type from = Types.generic("From");
      Type to = Types.generic("To");
      signature = Signature.createSignature(listType(to), functionType(to, from), listType(from));
   }

   @Override
   public Object evaluate(ChildNodes arguments, Assignments assignments) {
      Function f = arguments.first().evaluate(assignments);
      Node second = arguments.second();
      Type type = second.getType().getParameter(0);
      Collection<Object> elements = second.evaluate(assignments);
      List<Object> result = new ArrayList<>();
      for (Object element : elements) {
         ChildNodes childNodes = ChildNodes.createChildNodes(new ConstantNode(element, type));
         Object evaluateResult = f.evaluate(childNodes, assignments);
         result.add(evaluateResult);
      }
      return unmodifiableList(result);
   }

   @Override
   public Signature getSignature() {
      return signature;
   }
}
