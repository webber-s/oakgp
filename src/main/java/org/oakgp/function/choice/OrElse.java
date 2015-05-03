package org.oakgp.function.choice;

import static org.oakgp.node.NodeType.isFunction;

import java.util.ArrayList;
import java.util.List;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.Type;
import org.oakgp.function.Function;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

public final class OrElse implements Function {
   private final Signature signature;

   public OrElse(Type type) {
      signature = Signature.createSignature(type, Type.optionalType(type), type);
   }

   @Override
   public Object evaluate(Arguments arguments, Assignments assignments) {
      Object result = arguments.firstArg().evaluate(assignments);
      if (result == null) {
         return arguments.secondArg().evaluate(assignments);
      } else {
         return result;
      }
   }

   @Override
   public Signature getSignature() {
      return signature;
   }

   @Override
   public Node simplify(Arguments arguments) {
      List<Node> nodes = new ArrayList<>();
      nodes.add(arguments.firstArg());
      Node next = arguments.secondArg();
      boolean hasDuplicates = false;
      while (isFunction(next) && ((FunctionNode) next).getFunction() == this) {
         FunctionNode fn = ((FunctionNode) next);
         Arguments args = fn.getArguments();
         if (nodes.contains(args.firstArg())) {
            hasDuplicates = true;
         } else {
            nodes.add(args.firstArg());
         }
         next = args.secondArg();
      }

      if (!hasDuplicates) {
         return null;
      }

      FunctionNode fn = new FunctionNode(this, nodes.get(nodes.size() - 1), next);
      for (int i = nodes.size() - 2; i > -1; i--) {
         fn = new FunctionNode(this, nodes.get(i), fn);
      }
      return fn;
   }
}
