package org.oakgp.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.oakgp.Arguments.createArguments;
import static org.oakgp.Assignments.createAssignments;
import static org.oakgp.TestUtils.assertNodeEquals;
import static org.oakgp.TestUtils.createVariable;
import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.TestUtils.readNode;
import static org.oakgp.Type.integerType;
import static org.oakgp.util.Utils.TRUE_NODE;

import java.util.function.Predicate;

import org.junit.Test;
import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.function.Function;
import org.oakgp.function.math.IntegerUtils;

public class FunctionNodeTest {
   @Test
   public void testConstructors() {
      Function function = IntegerUtils.INTEGER_UTILS.getMultiply();
      ConstantNode arg1 = integerConstant(42);
      VariableNode arg2 = createVariable(0);

      // construct using Node array
      FunctionNode n1 = new FunctionNode(function, arg1, arg2);

      // Construct using Arguments
      Arguments arguments = createArguments(arg1, arg2);
      FunctionNode n2 = new FunctionNode(function, arguments);

      // assert the result is the same
      assertEquals(n1, n2);
   }

   @Test
   public void testEvaluate() {
      Function function = IntegerUtils.INTEGER_UTILS.getMultiply();
      Arguments arguments = createArguments(integerConstant(42), createVariable(0));
      FunctionNode functionNode = new FunctionNode(function, arguments);

      assertSame(function, functionNode.getFunction());
      assertSame(arguments, functionNode.getArguments());

      Assignments assignments = createAssignments(3);
      assertEquals(126, functionNode.evaluate(assignments));
   }

   @Test
   public void testReplaceAt() {
      FunctionNode n = createFunctionNode();
      java.util.function.Function<Node, Node> replacement = t -> integerConstant(9);

      assertEquals("(+ (* 9 v1) (+ v2 1))", n.replaceAt(0, replacement).toString());
      assertEquals("(+ (* v0 9) (+ v2 1))", n.replaceAt(1, replacement).toString());
      assertEquals("(+ 9 (+ v2 1))", n.replaceAt(2, replacement).toString());
      assertEquals("(+ (* v0 v1) (+ 9 1))", n.replaceAt(3, replacement).toString());
      assertEquals("(+ (* v0 v1) (+ v2 9))", n.replaceAt(4, replacement).toString());
      assertEquals("(+ (* v0 v1) 9)", n.replaceAt(5, replacement).toString());
      assertEquals("9", n.replaceAt(6, replacement).toString());
   }

   @Test
   public void testGetAt() {
      FunctionNode n = createFunctionNode();

      assertEquals("v0", n.getAt(0).toString());
      assertEquals("v1", n.getAt(1).toString());
      assertEquals("(* v0 v1)", n.getAt(2).toString());
      assertEquals("v2", n.getAt(3).toString());
      assertEquals("1", n.getAt(4).toString());
      assertEquals("(+ v2 1)", n.getAt(5).toString());
      assertEquals("(+ (* v0 v1) (+ v2 1))", n.getAt(6).toString());
   }

   @Test
   public void testCountAndHeight() {
      assertCountAndHeight("(* 7 7)", 3, 2);
      assertCountAndHeight("(* (+ 8 9) 7)", 5, 3);
      assertCountAndHeight("(* 7 (+ 8 9))", 5, 3);
      assertCountAndHeight("(zero? (+ (* 4 5) (- 6 (+ 7 8))))", 10, 5);
      assertCountAndHeight("(zero? (+ (- 6 (+ 7 8)) (* 4 5)))", 10, 5);
      assertCountAndHeight("(if (zero? v0) v1 v2)", 5, 3);
      assertCountAndHeight("(if (zero? v0) v1 (+ v0 (* v1 v2)))", 9, 4);
      assertCountAndHeight("(if (zero? v0) (+ v0 (* v1 v2)) v1)", 9, 4);
   }

   private void assertCountAndHeight(String expression, int nodeCount, int height) {
      Node n = readNode(expression);
      assertEquals(nodeCount, n.getNodeCount());
      assertEquals(height, n.getHeight());
   }

   @Test
   public void testCountStrategy() {
      Node tree = readNode("(+ (+ 1 v0) (+ (+ v0 v1) 2))");
      assertEquals(3, tree.getNodeCount(NodeType::isVariable));
      assertEquals(2, tree.getNodeCount(NodeType::isConstant));
      assertEquals(4, tree.getNodeCount(NodeType::isFunction));
   }

   @Test
   public void testGetStrategy() {
      VariableNode v0 = createVariable(0);
      VariableNode v1 = createVariable(1);
      VariableNode v2 = createVariable(2);
      ConstantNode c1 = integerConstant(0);
      Function f = IntegerUtils.INTEGER_UTILS.getAdd();
      FunctionNode branch1 = new FunctionNode(f, v0, c1);
      FunctionNode branch2 = new FunctionNode(f, v2, v1);
      FunctionNode tree = new FunctionNode(f, branch1, branch2);

      assertSame(v0, tree.getAt(0, NodeType::isVariable));
      assertSame(v2, tree.getAt(1, NodeType::isVariable));
      assertSame(v1, tree.getAt(2, NodeType::isVariable));

      assertSame(c1, tree.getAt(0, NodeType::isConstant));

      assertSame(branch1, tree.getAt(0, NodeType::isFunction));
      assertSame(branch2, tree.getAt(1, NodeType::isFunction));
      assertSame(tree, tree.getAt(2, NodeType::isFunction));
   }

   @Test
   public void testReplaceStrategy() {
      String input = "(+ (+ 1 v0) (+ (+ v0 v1) 2))";

      assertReplaceStrategy(input, 0, NodeType::isVariable, "(+ (+ 1 true) (+ (+ v0 v1) 2))");
      assertReplaceStrategy(input, 1, NodeType::isVariable, "(+ (+ 1 v0) (+ (+ true v1) 2))");
      assertReplaceStrategy(input, 2, NodeType::isVariable, "(+ (+ 1 v0) (+ (+ v0 true) 2))");

      assertReplaceStrategy(input, 0, NodeType::isConstant, "(+ (+ true v0) (+ (+ v0 v1) 2))");
      assertReplaceStrategy(input, 1, NodeType::isConstant, "(+ (+ 1 v0) (+ (+ v0 v1) true))");

      assertReplaceStrategy(input, 0, NodeType::isFunction, "(+ true (+ (+ v0 v1) 2))");
      assertReplaceStrategy(input, 1, NodeType::isFunction, "(+ (+ 1 v0) (+ true 2))");
      assertReplaceStrategy(input, 2, NodeType::isFunction, "(+ (+ 1 v0) true)");
      assertReplaceStrategy(input, 3, NodeType::isFunction, "true");
   }

   private void assertReplaceStrategy(String input, int index, Predicate<Node> treeWalkerStrategy, String expected) {
      Node actual = readNode(input).replaceAt(index, n -> TRUE_NODE, treeWalkerStrategy);
      assertNodeEquals(expected, actual);
   }

   @Test
   public void testGetType() {
      FunctionNode n = createFunctionNode();
      assertSame(integerType(), n.getType());
   }

   @Test
   public void testEqualsAndHashCode1() {
      final FunctionNode n1 = createFunctionNode();
      final FunctionNode n2 = createFunctionNode();
      assertNotSame(n1, n2); // just to sanity check createFunctionNode() doesn't return cached versions
      assertEquals(n1, n1);
      assertEquals(n1.hashCode(), n2.hashCode());
      assertEquals(n1, n2);
      assertEquals(n2, n1);
   }

   @Test
   public void testEqualsAndHashCode2() {
      Node n1 = readNode("(* 288 v1)");
      Node n2 = readNode("(* 288 v1)");
      assertNotSame(n1, n2); // just to sanity check readNode doesn't return cached versions
      assertEquals(n1, n1);
      assertEquals(n1, n2);
      assertEquals(n2, n1);
      assertEquals(n1.hashCode(), n2.hashCode());
   }

   @Test
   public void testNotEquals() {
      final FunctionNode n = new FunctionNode(IntegerUtils.INTEGER_UTILS.getAdd(), createVariable(0), integerConstant(7));

      // verify (sanity-check) that equals will return true when it should
      assertEquals(n, new FunctionNode(IntegerUtils.INTEGER_UTILS.getAdd(), createVariable(0), integerConstant(7)));

      // test different function
      assertNotEquals(n, new FunctionNode(IntegerUtils.INTEGER_UTILS.getMultiply(), createVariable(0), integerConstant(7)));

      // test different first argument
      assertNotEquals(n, new FunctionNode(IntegerUtils.INTEGER_UTILS.getAdd(), createVariable(1), integerConstant(7)));

      // test different second argument
      assertNotEquals(n, new FunctionNode(IntegerUtils.INTEGER_UTILS.getAdd(), createVariable(0), integerConstant(6)));

      // test same arguments but different order
      assertNotEquals(n, new FunctionNode(IntegerUtils.INTEGER_UTILS.getAdd(), integerConstant(7), createVariable(0)));

      // test wrong arguments but different order
      assertNotEquals(n, new FunctionNode(IntegerUtils.INTEGER_UTILS.getAdd(), integerConstant(0), createVariable(7)));

      // test extra argument
      assertNotEquals(n, new FunctionNode(IntegerUtils.INTEGER_UTILS.getAdd(), createVariable(0), integerConstant(7), integerConstant(7)));

      // test one less argument
      assertNotEquals(n, new FunctionNode(IntegerUtils.INTEGER_UTILS.getAdd(), createVariable(0)));

      // test no arguments
      assertNotEquals(n, new FunctionNode(IntegerUtils.INTEGER_UTILS.getAdd()));

      // test not equal to other Node implementations
      assertNotEquals(n, integerConstant(7));

      // test not equal to other non-Node instances
      assertNotEquals(n, new Object());

      assertFalse(n.equals(null));
   }

   @Test
   public void testHashCode() {
      // In Java the following results in two lists that have the same hashCode (even though they are different);
      // List a = new ArrayList();
      // a.add(Arrays.asList(9, 1));
      // a.add(Arrays.asList(2, 9));
      //
      // List b = new ArrayList();
      // b.add(Arrays.asList(9, 2));
      // b.add(Arrays.asList(1, 9));
      //
      // assertEquals(a.hashCode(), b.hashCode());

      // This is also true of Clojure's PersistentVector:
      // user=> (def x [[9 1] [2 9]])
      // #'user/x
      // user=> (def y [[9 2] [1 9]])
      // #'user/y
      // user=> (.hashCode x)
      // 40464
      // user=> (.hashCode y)
      // 40464

      // test that result of reading the following expressions is nodes with different hash codes:
      Node n1 = readNode("(- (- (* -1 v3) 0) (- 13 v1))");
      Node n2 = readNode("(- (- (* -1 v3) 13) (- 0 v1))");
      assertNotEquals(n1.hashCode(), n2.hashCode());
   }

   /** Returns representation of: {@code (x*y)+z+1} */
   private FunctionNode createFunctionNode() {
      return new FunctionNode(IntegerUtils.INTEGER_UTILS.getAdd(), new FunctionNode(IntegerUtils.INTEGER_UTILS.getMultiply(), createVariable(0),
            createVariable(1)), new FunctionNode(IntegerUtils.INTEGER_UTILS.getAdd(), createVariable(2), integerConstant(1)));
   }
}
