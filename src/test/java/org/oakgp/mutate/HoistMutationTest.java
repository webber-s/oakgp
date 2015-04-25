package org.oakgp.mutate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.oakgp.TestUtils.integerConstant;
import static org.oakgp.TestUtils.readNode;
import static org.oakgp.util.DummyRandom.GetIntExpectation.nextInt;

import org.junit.Test;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.selector.DummyNodeSelector;
import org.oakgp.util.DummyRandom;
import org.oakgp.util.Random;

public class HoistMutationTest {
   @Test
   public void testTerminal() {
      ConstantNode constant = integerConstant(1);
      Node result = hoistMutate(DummyRandom.EMPTY, constant);
      assertSame(constant, result);
   }

   @Test
   public void testFunctionNodeNoAlternatives() {
      // tests when the return type of the root node (in this case a boolean)
      // is not same type of any of the other nodes in the tree (in this case they are all integers)
      Node input = readNode("(zero? (+ (+ v0 v1) (+ 9 v2)))");
      Node result = hoistMutate(DummyRandom.EMPTY, input);
      assertSame(input, result);
   }

   @Test
   public void testFunctionNode() {
      Node input = readNode("(+ (+ (if (zero? v0) 7 8) v1) (+ 9 v2))");
      DummyNodeSelector selector = DummyNodeSelector.repeat(9, input);
      DummyRandom random = nextInt(9).returns(3, 4, 5, 2, 1, 8, 6, 7, 0);
      HoistMutation mutator = new HoistMutation(random);

      // TODO better if first arg is String and second uses writeNode
      // TODO add assertNodeEquals(String, Node) to TestUtils and retro-fit to existing tests
      assertEquals(readNode("(if (zero? v0) 7 8)"), mutator.evolve(selector));
      assertEquals(readNode("v1"), mutator.evolve(selector));
      assertEquals(readNode("(+ (if (zero? v0) 7 8) v1)"), mutator.evolve(selector));
      assertEquals(readNode("8"), mutator.evolve(selector));
      assertEquals(readNode("7"), mutator.evolve(selector));
      assertEquals(readNode("(+ 9 v2)"), mutator.evolve(selector));
      assertEquals(readNode("9"), mutator.evolve(selector));
      assertEquals(readNode("v2"), mutator.evolve(selector));
      assertEquals(readNode("v0"), mutator.evolve(selector));

      selector.assertEmpty();
      random.assertEmpty();
   }

   private Node hoistMutate(Random random, Node input) {
      DummyNodeSelector selector = new DummyNodeSelector(input);
      Node result = new HoistMutation(random).evolve(selector);
      selector.assertEmpty();
      return result;
   }
}