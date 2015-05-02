package org.oakgp.function.math;

import java.math.BigDecimal;
import java.util.List;

import org.oakgp.function.AbstractFunctionTest;
import org.oakgp.function.Function;

public class AddTest extends AbstractFunctionTest {
   @Override
   protected Function getFunction() {
      return IntegerUtils.INTEGER_UTILS.getAdd();
   }

   @Override
   protected void getEvaluateTests(EvaluateTestCases testCases) {
      // int
      testCases.put("(+ 3 21)", 24);
      testCases.put("(+ 2147483647 1)", -2147483648);

      // long
      testCases.put("(+ 3L 21L)", 24L);
      testCases.put("(+ 2147483647L 1L)", 2147483648L);
      testCases.put("(+ 9223372036854775807L 1L)", -9223372036854775808L);

      // BigDecimal
      testCases.put("(+ 3D 21D)", new BigDecimal("24"));
      testCases.put("(+ 2147483647D 1D)", new BigDecimal("2147483648"));
      testCases.put("(+ 9223372036854775807D 1D)", new BigDecimal("9223372036854775808"));
      testCases.put("(+ 7.5D -0.025D)", new BigDecimal("7.475"));
      testCases.put("(+ 1.7976931348623157E308D 1D)", BigDecimal.valueOf(Double.MAX_VALUE).add(BigDecimal.ONE));
   }

   @Override
   protected void getCanSimplifyTests(SimplifyTestCases testCases) {
      Object[][] assignedValues = { { 0, 0 }, { 1, 21 }, { 2, 14 }, { 3, -6 }, { 7, 3 }, { -1, 9 }, { -7, 0 } };

      // constants get simplified to the result of adding them together
      testCases.put("(+ 8 3)", "11", assignedValues);

      // arguments should be consistently ordered
      testCases.put("(+ 4 v0)", "(+ 4 v0)", assignedValues);
      testCases.put("(+ v0 4)", "(+ 4 v0)", assignedValues);
      testCases.put("(+ v0 v1)", "(+ v0 v1)", assignedValues);
      testCases.put("(+ v1 v0)", "(+ v0 v1)", assignedValues);

      // anything plus zero is itself
      testCases.put("(+ v1 0)", "v1", assignedValues);
      testCases.put("(+ 0 v1)", "v1", assignedValues);

      // anything plus itself is equal to itself multiplied by 2
      testCases.put("(+ v1 v1)", "(* 2 v1)", assignedValues);

      // convert addition of negative numbers to subtraction
      testCases.put("(+ v1 -7)", "(- v1 7)", assignedValues);
      testCases.put("(+ -7 v1)", "(- v1 7)", assignedValues);

      testCases.put("(+ v0 (- 7 3))", "(+ 4 v0)", assignedValues);

      testCases.put("(+ (+ v0 v1) 0)", "(+ v0 v1)", assignedValues);
      testCases.put("(+ 0 (+ v0 v1))", "(+ v0 v1)", assignedValues);

      testCases.put("(+ (+ 4 v0) (+ v0 (* 2 v1)))", "(+ 4 (+ (* 2 v1) (* 2 v0)))", assignedValues);
      testCases.put("(+ (- 10 v0) (+ 1 v0))", "11", assignedValues);
      testCases.put("(+ (+ 10 v0) (- 1 v0))", "11", assignedValues);
      testCases.put("(+ (- 10 v0) (+ 1 v1))", "(+ v1 (- 11 v0))", assignedValues);
      testCases.put("(+ (+ 10 v0) (- 1 v1))", "(+ v0 (- 11 v1))", assignedValues);
      testCases.put("(+ (- 10 v0) (- 1 v0))", "(- 11 (* 2 v0))", assignedValues);
      testCases.put("(+ (+ 10 v0) (+ 1 v0))", "(+ 11 (* 2 v0))", assignedValues);

      testCases.put("(+ 1 (+ 1 v0))", "(+ 2 v0)", assignedValues);
      testCases.put("(+ 1 (- 1 v0))", "(- 2 v0)", assignedValues);
      testCases.put("(+ 6 (+ 4 v0))", "(+ 10 v0)", assignedValues);
      testCases.put("(+ 6 (- 4 v0))", "(- 10 v0)", assignedValues);
      testCases.put("(+ (+ 1 v0) (+ 2 v0))", "(+ 3 (* 2 v0))", assignedValues);
      testCases.put("(+ (+ 3 v0) (+ 4 v0))", "(+ 7 (* 2 v0))", assignedValues);
      testCases.put("(+ (+ v0 3) (+ v0 4))", "(+ 7 (* 2 v0))", assignedValues);
      testCases.put("(+ (+ v0 3) (+ 4 v0))", "(+ 7 (* 2 v0))", assignedValues);
      testCases.put("(+ (+ 3 v0) (+ v0 4))", "(+ 7 (* 2 v0))", assignedValues);
      testCases.put("(+ v0 (* 2 v0))", "(* 3 v0)", assignedValues);
      testCases.put("(+ v0 (* 14 v0))", "(* 15 v0)", assignedValues);
      testCases.put("(+ 2 (+ (* 2 v0) 8))", "(+ 10 (* 2 v0))", assignedValues);
      testCases.put("(+ 2 (+ 8 (* 2 v0)))", "(+ 10 (* 2 v0))", assignedValues);
      testCases.put("(+ v0 (+ v0 8))", "(+ 8 (* 2 v0))", assignedValues);
      testCases.put("(+ v0 (+ 8 v0))", "(+ 8 (* 2 v0))", assignedValues);
      testCases.put("(+ v0 (- v0 2))", "(- (* 2 v0) 2)", assignedValues);
      testCases.put("(+ v0 (- 2 v0))", "2", assignedValues);

      testCases.put("(+ 5 (+ v0 5))", "(+ 10 v0)", assignedValues);
      testCases.put("(+ 5 (- v0 5))", "v0", assignedValues);
      testCases.put("(+ 5 (- 5 v0))", "(- 10 v0)", assignedValues);
      testCases.put("(+ 5 (- v0 2))", "(+ 3 v0)", assignedValues);

      testCases.put("(+ 9 (+ v1 (+ v0 8)))", "(+ v1 (+ 17 v0))", assignedValues);
   }

   @Override
   protected void getCannotSimplifyTests(List<String> testCases) {
      testCases.add("(+ 1 v0)");
      testCases.add("(+ v0 (* v0 v1))");
   }

   @Override
   protected Function[] getFunctionSet() {
      return new Function[] { getFunction(), IntegerUtils.INTEGER_UTILS.getMultiply(), BigDecimalUtils.BIG_DECIMAL_UTILS.getAdd(),
            BigDecimalUtils.BIG_DECIMAL_UTILS.getMultiply(), LongUtils.LONG_UTILS.getAdd(), LongUtils.LONG_UTILS.getMultiply() };
   }
}
