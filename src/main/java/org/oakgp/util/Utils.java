package org.oakgp.util;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.unmodifiableList;
import static org.oakgp.Type.booleanType;

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

   public static ConstantNode[] createEnumConstants(Class<? extends Enum<?>> e, Type t) {
      Enum<?>[] enumConstants = e.getEnumConstants();
      ConstantNode[] constants = new ConstantNode[enumConstants.length];
      for (int i = 0; i < enumConstants.length; i++) {
         constants[i] = new ConstantNode(enumConstants[i], t);
      }
      return constants;
   }
}
