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
package org.oakgp.util;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.unmodifiableList;
import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.integerType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.oakgp.Type;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;

/** Utility methods that support the functionality provided by the rest of the framework. */
public final class Utils {
   public static final ConstantNode TRUE_NODE = new ConstantNode(TRUE, booleanType());
   public static final ConstantNode FALSE_NODE = new ConstantNode(FALSE, booleanType());

   /** Private constructor as all methods are static. */
   private Utils() {
      // do nothing
   }

   public static <T extends Node> Map<Type, List<T>> groupByType(T[] nodes) {
      return groupBy(nodes, Node::getType);
   }

   public static <K, V> Map<K, List<V>> groupBy(V[] values, Function<V, K> valueToKey) {
      Map<K, List<V>> nodesByType = new HashMap<>();
      for (V v : values) {
         addToListOfMap(nodesByType, valueToKey.apply(v), v);
      }
      makeValuesImmutable(nodesByType);
      return nodesByType;
   }

   private static <K, V> void addToListOfMap(Map<K, List<V>> map, K key, V value) {
      List<V> list = map.get(key);
      if (list == null) {
         list = new ArrayList<>();
         map.put(key, list);
      }
      list.add(value);
   }

   private static <K, V> void makeValuesImmutable(Map<K, List<V>> map) {
      for (Map.Entry<K, List<V>> e : map.entrySet()) {
         map.put(e.getKey(), unmodifiableList(e.getValue()));
      }
   }

   public static int selectSubNodeIndex(Random random, Node tree) {
      int nodeCount = tree.getNodeCount();
      if (nodeCount == 1) {
         // will get here if and only if 'tree' is a terminal (i.e. variable or constant) rather than a function node
         return 0;
      } else {
         return selectSubNodeIndex(random, nodeCount);
      }
   }

   public static int selectSubNodeIndex(Random random, int nodeCount) {
      // Note: -1 to avoid selecting root node
      return random.nextInt(nodeCount - 1);
   }

   public static <T> void addArray(List<T> list, T[] array) {
      for (T e : array) {
         list.add(e);
      }
   }

   public static ConstantNode[] createEnumConstants(Class<? extends Enum<?>> e, Type t) {
      Enum<?>[] enumConstants = e.getEnumConstants();
      ConstantNode[] constants = new ConstantNode[enumConstants.length];
      for (int i = 0; i < enumConstants.length; i++) {
         constants[i] = new ConstantNode(enumConstants[i], t);
      }
      return constants;
   }

   public static ConstantNode[] createIntegerConstants(int minInclusive, int maxInclusive) {
      ConstantNode[] constants = new ConstantNode[maxInclusive - minInclusive + 1];
      for (int n = minInclusive, i = 0; n <= maxInclusive; i++, n++) {
         constants[i] = new ConstantNode(n, integerType());
      }
      return constants;
   }

   public static Type[] createIntegerTypeArray(int size) {
      Type[] a = new Type[size];
      for (int i = 0; i < size; i++) {
         a[i] = Type.integerType();
      }
      return a;
   }
}
