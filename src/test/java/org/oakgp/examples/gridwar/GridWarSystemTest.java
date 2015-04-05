package org.oakgp.examples.gridwar;

import static org.oakgp.TestUtils.createTypeArray;
import static org.oakgp.examples.SystemTestUtils.COMPARISON_FUNCTION_SET;
import static org.oakgp.examples.SystemTestUtils.ELITISM_SIZE;
import static org.oakgp.examples.SystemTestUtils.GENERATION_SIZE;
import static org.oakgp.examples.SystemTestUtils.RANDOM;
import static org.oakgp.examples.SystemTestUtils.RATIO_VARIABLES;
import static org.oakgp.examples.SystemTestUtils.SELECTOR_FACTORY;
import static org.oakgp.examples.SystemTestUtils.createConstants;
import static org.oakgp.examples.SystemTestUtils.createInitialGeneration;
import static org.oakgp.examples.SystemTestUtils.makeRandomTree;
import static org.oakgp.examples.SystemTestUtils.printRankedCandidate;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.junit.Test;
import org.oakgp.ConstantSet;
import org.oakgp.GenerationEvolver;
import org.oakgp.GenerationProcessor;
import org.oakgp.NodeEvolver;
import org.oakgp.PrimitiveSet;
import org.oakgp.RankedCandidate;
import org.oakgp.Runner;
import org.oakgp.Type;
import org.oakgp.VariableSet;
import org.oakgp.crossover.SubtreeCrossover;
import org.oakgp.mutate.PointMutation;
import org.oakgp.node.Node;
import org.oakgp.tournament.FirstPlayerAdvantageGame;
import org.oakgp.tournament.RoundRobinTournament;
import org.oakgp.tournament.TwoPlayerGame;
import org.oakgp.tournament.TwoPlayerGameCache;

public class GridWarSystemTest {
   private static final int NUM_GENERATIONS = 100;
   private static final Type[] VARIABLE_TYPES = createTypeArray(5);
   private static final int NUM_CONSTANTS = 5;

   @Test
   public void test() {
      // set-up
      ConstantSet constants = createConstants(NUM_CONSTANTS);
      VariableSet variables = VariableSet.createVariableSet(VARIABLE_TYPES);
      PrimitiveSet primitiveSet = new PrimitiveSet(COMPARISON_FUNCTION_SET, constants, variables, RANDOM, RATIO_VARIABLES);
      Collection<Node> initialGeneration = createInitialGeneration(primitiveSet, GENERATION_SIZE);
      Map<NodeEvolver, Long> nodeEvolvers = createNodeEvolvers(primitiveSet);
      Predicate<List<RankedCandidate>> terminator = createTerminator();

      // run process
      TwoPlayerGame game = createGridWarGame();
      GenerationProcessor generationProcessor = new RoundRobinTournament(game);
      GenerationEvolver generationEvolver = new GenerationEvolver(ELITISM_SIZE, SELECTOR_FACTORY, nodeEvolvers);
      RankedCandidate best = Runner.process(generationProcessor, generationEvolver, terminator, initialGeneration);

      // print best
      printRankedCandidate(best);
   }

   private TwoPlayerGame createGridWarGame() {
      TwoPlayerGame game = new FirstPlayerAdvantageGame(new GridWar(RANDOM));
      return new TwoPlayerGameCache(GENERATION_SIZE * 2, game);
   }

   private Map<NodeEvolver, Long> createNodeEvolvers(PrimitiveSet primitiveSet) {
      Map<NodeEvolver, Long> nodeEvolvers = new HashMap<>();
      nodeEvolvers.put(t -> makeRandomTree(primitiveSet, 4), 5L);
      nodeEvolvers.put(new SubtreeCrossover(RANDOM), 21L);
      nodeEvolvers.put(new PointMutation(RANDOM, primitiveSet), 21L);
      return nodeEvolvers;
   }

   private Predicate<List<RankedCandidate>> createTerminator() {
      return new Predicate<List<RankedCandidate>>() {
         int ctr = 1;

         @Override
         public boolean test(List<RankedCandidate> t) {
            if (ctr % 50 == 0) {
               System.out.println(ctr);
            }
            return ctr++ > NUM_GENERATIONS;
         }
      };
   }
}
