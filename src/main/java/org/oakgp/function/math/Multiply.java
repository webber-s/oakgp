package org.oakgp.function.math;

import static org.oakgp.function.math.ArithmeticExpressionSimplifier.isAddOrSubtract;
import static org.oakgp.function.math.ArithmeticExpressionSimplifier.isMultiply;
import static org.oakgp.node.NodeType.isConstant;
import static org.oakgp.node.NodeType.isFunction;
import static org.oakgp.util.NodeComparator.NODE_COMPARATOR;

import org.oakgp.Arguments;
import org.oakgp.Type;
import org.oakgp.function.Function;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

/** Performs multiplication. */
public final class Multiply extends ArithmeticOperator<Integer> {
   private final IntegerUtils integerUtils = new IntegerUtils();

   public Multiply() {
      super(Type.integerType());
   }

   /**
    * Returns the result of multiplying the two elements of the specified arguments.
    *
    * @return the result of multiplying {@code arg1} and {@code arg2}
    */
   @Override
   protected Integer evaluate(Integer arg1, Integer arg2) {
      return arg1 * arg2;
   }

   @Override
   public Node simplify(Arguments arguments) {
      Node arg1 = arguments.firstArg();
      Node arg2 = arguments.secondArg();

      if (NODE_COMPARATOR.compare(arg1, arg2) > 0) {
         // as for addition the order of the arguments is not important, order arguments in a consistent way
         // e.g. (* v1 1) -> (* 1 v1)
         return new FunctionNode(this, arg2, arg1);
      } else if (integerUtils.ZERO.equals(arg1)) {
         // anything multiplied by zero is zero
         // e.g. (* 0 v0) -> 0
         return integerUtils.ZERO;
      } else if (integerUtils.ZERO.equals(arg2)) {
         // should never get here to to earlier ordering of arguments
         throw new IllegalArgumentException("arg1 " + arg1 + " arg2 " + arg2);
      } else if (integerUtils.ONE.equals(arg1)) {
         // anything multiplied by one is itself
         // e.g. (* 1 v0) -> v0
         return arg2;
      } else if (integerUtils.ONE.equals(arg2)) {
         // should never get here to to earlier ordering of arguments
         throw new IllegalArgumentException("arg1 " + arg1 + " arg2 " + arg2);
      } else {
         if (isConstant(arg1) && isFunction(arg2)) {
            FunctionNode fn = (FunctionNode) arg2;
            Function f = fn.getFunction();
            Arguments args = fn.getArguments();
            Node fnArg1 = args.firstArg();
            Node fnArg2 = args.secondArg();
            if (isConstant(fnArg1)) {
               if (isAddOrSubtract(f)) {
                  return new FunctionNode(f, integerUtils.multiply(arg1, fnArg1), new FunctionNode(this, arg1, fnArg2));
               } else if (isMultiply(f)) {
                  return new FunctionNode(this, integerUtils.multiply(arg1, fnArg1), fnArg2);
               } else {
                  throw new IllegalArgumentException();
               }
            } else if (isAddOrSubtract(f)) {
               return new FunctionNode(f, new FunctionNode(this, arg1, fnArg1), new FunctionNode(this, arg1, fnArg2));
            }
         }

         return null;
      }
   }

   @Override
   public String getDisplayName() {
      return "*";
   }
}
