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
package org.oakgp.evaluate.fitness;

import static org.oakgp.util.CacheMap.createCache;

import java.util.Map;

import org.oakgp.node.Node;

/** Wraps a {@code FitnessFunction} to provide caching of results. */
public final class FitnessFunctionCache implements FitnessFunction {
   private final FitnessFunction fitnessFunction;
   private final Map<Node, Double> cache;

   public FitnessFunctionCache(int maxSize, FitnessFunction fitnessFunction) {
      this.fitnessFunction = fitnessFunction;
      this.cache = createCache(maxSize);
   }

   @Override
   public double evaluate(Node n) {
      Double result = cache.get(n);
      if (result == null) {
         result = fitnessFunction.evaluate(n);
         cache.put(n, result);
      }
      return result;
   }
}