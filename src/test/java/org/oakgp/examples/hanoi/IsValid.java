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
package org.oakgp.examples.hanoi;

import static org.oakgp.examples.hanoi.TowersOfHanoiExample.MOVE_TYPE;
import static org.oakgp.examples.hanoi.TowersOfHanoiExample.STATE_TYPE;
import static org.oakgp.type.CommonTypes.booleanType;

import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.function.Signature;
import org.oakgp.node.AbstractDefinedFunctions;
import org.oakgp.node.ChildNodes;

// TODO move this functionality to TowersOfHanoi and plug-in to FunctionSetBuilder
// TODO using .add(TowersOfHanoi.class, "isValid")
/** Determines if a move is a valid move for a particular game state. */
class IsValid implements Function {
   private static final Signature SIGNATURE = Signature.createSignature(booleanType(), STATE_TYPE, MOVE_TYPE);

   @Override
   public Signature getSignature() {
      return SIGNATURE;
   }

   /**
    * @param arguments
    *           the first argument is a {@code TowersOfHanoi} representing a game state and the second argument is a {@code Move}
    * @param assignments
    *           the values assigned to each of member of the variable set
    * @return {@code true} if the specified move is a valid move for the specified game state, else {@code false}
    */
   @Override
   public Object evaluate(ChildNodes arguments, Assignments assignments, AbstractDefinedFunctions adfs) {
      TowersOfHanoi gameState = arguments.first().evaluate(assignments, adfs);
      Move move = arguments.second().evaluate(assignments, adfs);
      return gameState.move(move) != null;
   }
}
