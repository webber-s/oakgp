package org.oakgp.crossover;

import org.oakgp.Arguments;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

final class CommonRegion {
   Node crossoverAt(Node n1, Node n2, int crossOverPoint) {
      boolean sameType = n1.getType() == n2.getType();
      if (sameType && crossOverPoint == 0) {
         return n2;
      }

      boolean isFirstFunction = n1 instanceof FunctionNode;
      boolean isSecondFunction = n2 instanceof FunctionNode;
      if (isFirstFunction && isSecondFunction) {
         FunctionNode f1 = (FunctionNode) n1;
         FunctionNode f2 = (FunctionNode) n2;
         Arguments arguments = f1.getArguments();
         int argCount = arguments.getArgCount();
         if (argCount == f2.getArguments().getArgCount()) {
            int total = sameType ? 1 : 0;
            for (int i = 0; i < argCount; i++) {
               Node a1 = arguments.getArg(i);
               Node a2 = f2.getArguments().getArg(i);
               int c = getNodeCount(a1, a2);
               if (total + c > crossOverPoint) {
                  return new FunctionNode(f1.getFunction(), arguments.replaceAt(i, crossoverAt(a1, a2, crossOverPoint - total)));
               } else {
                  total += c;
               }
            }
         }
      }

      return n1;
   }

   int getNodeCount(Node n1, Node n2) {
      boolean isFirstFunction = n1 instanceof FunctionNode;
      boolean isSecondFunction = n2 instanceof FunctionNode;
      if (isFirstFunction && isSecondFunction) {
         int total = n1.getType() == n2.getType() ? 1 : 0;
         FunctionNode f1 = (FunctionNode) n1;
         FunctionNode f2 = (FunctionNode) n2;
         int argCount = f1.getArguments().getArgCount();
         if (argCount == f2.getArguments().getArgCount()) {
            for (int i = 0; i < argCount; i++) {
               total += getNodeCount(f1.getArguments().getArg(i), f2.getArguments().getArg(i));
            }
         }
         return total;
      } else if (!isFirstFunction && !isSecondFunction) {
         // both terminal nodes
         return n1.getType() == n2.getType() ? 1 : 0;
      } else {
         // terminal node does not match with a function node
         return 0;
      }
   }
}