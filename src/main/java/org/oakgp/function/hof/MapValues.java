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
package org.oakgp.function.hof;

import static java.util.Collections.unmodifiableMap;
import static org.oakgp.Type.functionType;
import static org.oakgp.Type.mapType;

import java.util.LinkedHashMap;
import java.util.Map;

import org.oakgp.Arguments;
import org.oakgp.Type;
import org.oakgp.function.Function;
import org.oakgp.function.HigherOrderFunctionArguments;
import org.oakgp.function.Signature;

public final class MapValues implements Function {
   private final Signature signature;

   public MapValues(Type key, Type from, Type to) {
      this.signature = Signature.createSignature(mapType(key, to), functionType(to, from), mapType(key, from));
   }

   @Override
   public Object evaluate(Arguments arguments) {
      Function f = arguments.first();
      Map<Object, Object> candidates = arguments.second();
      Map<Object, Object> result = new LinkedHashMap<>();
      for (Map.Entry<Object, Object> e : candidates.entrySet()) {
         Object evaluateResult = f.evaluate(new HigherOrderFunctionArguments(e.getValue()));
         result.put(e.getKey(), evaluateResult);
      }
      return unmodifiableMap(result);
   }

   @Override
   public Signature getSignature() {
      return signature;
   }
}