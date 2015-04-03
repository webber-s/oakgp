package org.oakgp.operator.compare;

import org.oakgp.Arguments;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

public final class GreaterThanOrEqual extends ComparisonOperator {
   public GreaterThanOrEqual() {
      super(true);
   }

   @Override
   protected boolean evaluate(int arg1, int arg2) {
      return arg1 >= arg2;
   }

   @Override
   public Node simplify(Arguments arguments) {
      Node simplifiedVersion = super.simplify(arguments);
      if (simplifiedVersion == null) {
         return new FunctionNode(new LessThanOrEqual(), arguments.get(1), arguments.get(0));
      } else {
         return simplifiedVersion;
      }
   }
}