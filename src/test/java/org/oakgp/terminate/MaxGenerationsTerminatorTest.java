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
package org.oakgp.terminate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.oakgp.evaluate.RankedCandidate;
import org.oakgp.node.Node;

public class MaxGenerationsTerminatorTest {
   @Test
   public void test() {
      MaxGenerationsTerminator t = new MaxGenerationsTerminator(3);
      List<RankedCandidate> candidates = Collections.singletonList(new RankedCandidate(mock(Node.class), 1));
      assertFalse(t.test(candidates));
      assertFalse(t.test(candidates));
      assertFalse(t.test(candidates));
      assertTrue(t.test(candidates));
   }
}
