package org.oakgp.function.choice;

import static org.oakgp.node.NodeType.isFunction;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.Type;
import org.oakgp.function.Function;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

public final class SwitchEnum implements Function {
   private final Signature signature;
   final Enum<?>[] enumConstants;

   public SwitchEnum(Class<? extends Enum<?>> enumClass, Type enumType, Type returnType) {
      this.enumConstants = enumClass.getEnumConstants();
      Type[] types = new Type[enumConstants.length + 1];
      types[0] = enumType;
      for (int i = 1; i < types.length; i++) {
         types[i] = returnType;
      }
      this.signature = Signature.createSignature(returnType, types);
   }

   @Override
   public Object evaluate(Arguments arguments, Assignments assignments) {
      Enum<?> input = arguments.firstArg().evaluate(assignments);
      return arguments.getArg(input.ordinal() + 1).evaluate(assignments);
   }

   @Override
   public Node simplify(Arguments arguments) {
      // TODO share with function node replaceAll
      boolean updated = false;
      Node[] replacementArgs = new Node[arguments.getArgCount()];
      Node input = arguments.firstArg();
      replacementArgs[0] = input;
      for (int i = 1; i < arguments.getArgCount(); i++) {
         Node arg = arguments.getArg(i);
         final int idx = i;
         Node replacedArg = arg.replaceAll(n -> isFunction(n) && ((FunctionNode) n).getFunction() == this, n -> ((FunctionNode) n).getArguments().getArg(idx));
         if (arg != replacedArg) {
            updated = true;
         }
         replacementArgs[i] = replacedArg;
      }
      if (updated) {
         return new FunctionNode(this, Arguments.createArguments(replacementArgs));
      } else {
         return null;
      }
   }

   @Override
   public Signature getSignature() {
      return signature;
   }
}
