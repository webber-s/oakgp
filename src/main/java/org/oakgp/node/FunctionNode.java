package org.oakgp.node;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Type;
import org.oakgp.function.Function;

/** Contains a function (operator) and the arguments (operands) to apply to it. */
public final class FunctionNode implements Node {
   private final Function function;
   private final Arguments arguments;
   private final int hashCode;

   /**
    * Constructs a new {@code FunctionNode} with the specified function function and arguments.
    *
    * @param function
    *           the function to associate with this {@code FunctionNode}
    * @param arguments
    *           the arguments (i.e. operands) to apply to {@code function} when evaluating this {@code FunctionNode}
    */
   public FunctionNode(Function function, Node... arguments) {
      this(function, Arguments.createArguments(arguments));
   }

   /**
    * Constructs a new {@code FunctionNode} with the specified function function and arguments.
    *
    * @param function
    *           the function to associate with this {@code FunctionNode}
    * @param arguments
    *           the arguments (i.e. operands) to apply to {@code function} when evaluating this {@code FunctionNode}
    */
   public FunctionNode(Function function, Arguments arguments) {
      this.function = function;
      this.arguments = arguments;
      this.hashCode = (function.getClass().getName().hashCode() * 31) * arguments.hashCode();
   }

   public Function getFunction() {
      return function;
   }

   public Arguments getArguments() {
      return arguments;
   }

   @Override
   public Object evaluate(Assignments assignments) {
      return function.evaluate(arguments, assignments);
   }

   @Override
   public Node replaceAt(int index, java.util.function.Function<Node, Node> replacement) {
      int total = 0;
      for (int i = 0; i < arguments.length(); i++) {
         Node node = arguments.get(i);
         int c = node.getNodeCount();
         if (total + c > index) {
            return new FunctionNode(function, arguments.replaceAt(i, node.replaceAt(index - total, replacement)));
         } else {
            total += c;
         }
      }
      return replacement.apply(this);
   }

   @Override
   public Node getAt(int index) {
      int total = 0;
      for (int i = 0; i < arguments.length(); i++) {
         Node node = arguments.get(i);
         int c = node.getNodeCount();
         if (total + c > index) {
            return arguments.get(i).getAt(index - total);
         } else {
            total += c;
         }
      }
      return this;
   }

   @Override
   public int getNodeCount() {
      int total = 1;
      for (int i = 0; i < arguments.length(); i++) {
         total += arguments.get(i).getNodeCount();
      }
      return total;
   }

   @Override
   public Type getType() {
      return function.getSignature().getReturnType();
   }

   @Override
   public int hashCode() {
      return hashCode;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (hashCode() != o.hashCode()) {
         return false;
      } else if (o instanceof FunctionNode) {
         FunctionNode fn = (FunctionNode) o;
         // TODO see how often we return false when we get here - as that indicates hashCode() could be improved
         return this.function.getClass().equals(fn.function.getClass()) && this.arguments.equals(fn.arguments);
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append('(').append(function.getClass().getName());
      for (int i = 0; i < arguments.length(); i++) {
         sb.append(' ').append(arguments.get(i));
      }
      return sb.append(')').toString();
   }
}
