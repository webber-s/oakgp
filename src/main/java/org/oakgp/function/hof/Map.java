package org.oakgp.function.hof;

import static org.oakgp.Type.arrayType;
import static org.oakgp.Type.functionType;

import java.util.ArrayList;
import java.util.List;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.Type;
import org.oakgp.function.Function;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;

public class Map implements Function {
   private final Signature signature;

   public Map(Type from, Type to) {
      signature = Signature.createSignature(arrayType(to), functionType(to, from), arrayType(from));
   }

   @Override
   public Object evaluate(Arguments arguments, Assignments assignments) {
      Function f = arguments.get(0).evaluate(assignments);
      Type returnType = f.getSignature().getReturnType();
      Arguments candidates = arguments.get(1).evaluate(assignments);
      List<Node> result = new ArrayList<>();
      for (int i = 0; i < candidates.length(); i++) {
         Node inputNode = candidates.get(i);
         Object evaluateResult = f.evaluate(Arguments.createArguments(inputNode), assignments);
         ConstantNode outputNode = new ConstantNode(evaluateResult, returnType);
         result.add(outputNode);
      }
      return Arguments.createArguments(result.toArray(new Node[result.size()]));
   }

   @Override
   public Signature getSignature() {
      return signature;
   }
}