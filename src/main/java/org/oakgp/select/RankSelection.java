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
package org.oakgp.select;

import java.util.List;

import org.oakgp.node.Node;
import org.oakgp.rank.RankedCandidate;
import org.oakgp.util.Random;

/** The <i>relative</i> fitness of candidates is used to determine the probability that they will be selected. */
public class RankSelection implements NodeSelector {
   private final Random random;
   private final List<RankedCandidate> candidates;
   private final int size;
   private final double sum;

   public RankSelection(Random random, List<RankedCandidate> candidates) {
      this.random = random;
      this.candidates = candidates;
      this.size = candidates.size();
      long s = 0;
      for (int i = 1; i <= size; i++) {
         s += i;
      }
      sum = s;
   }

   @Override
   public Node next() {
      final double r = random.nextDouble();
      double p = 0;
      for (int i = 0; i < size; i++) {
         p += (size - i) / sum;
         if (r < p) {
            RankedCandidate c = candidates.get(i);
            return c.getNode();
         }
      }
      // should only get here if rounding error - default to selecting the best candidate
      return candidates.get(0).getNode();
   }
}