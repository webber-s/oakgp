package org.oakgp.examples.hanoi;

import static org.oakgp.Type.booleanType;
import static org.oakgp.Type.integerType;
import static org.oakgp.Type.nullableType;
import static org.oakgp.Type.type;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.oakgp.Arguments;
import org.oakgp.Assignments;
import org.oakgp.Signature;
import org.oakgp.Type;
import org.oakgp.examples.RunBuilder;
import org.oakgp.fitness.FitnessFunction;
import org.oakgp.function.Function;
import org.oakgp.function.choice.If;
import org.oakgp.function.choice.SwitchEnum;
import org.oakgp.function.compare.Equal;
import org.oakgp.function.compare.GreaterThan;
import org.oakgp.function.compare.LessThan;
import org.oakgp.function.math.IntegerUtils;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;
import org.oakgp.util.Utils;

public class TowersOfHanoiExample {
   private static final TowersOfHanoi START_STATE = new TowersOfHanoi(3);
   private static final Type STATE_TYPE = type("gameState");
   private static final Type MOVE_TYPE = type("move");
   private static final Type POLE_TYPE = type("pole");
   private static final int NUM_GENERATIONS = 1000;
   private static final int INITIAL_GENERATION_SIZE = 100;
   private static final int INITIAL_GENERATION_MAX_DEPTH = 4;

   public static void main(String[] args) {
      Function[] functions = { new If(MOVE_TYPE), new Equal(MOVE_TYPE), new IsValid(), new SwitchEnum(Move.class, nullableType(MOVE_TYPE), MOVE_TYPE),
            new GreaterThan(integerType()), new LessThan(integerType()), new Equal(integerType()), new Next() };
      ConstantNode[] constants = Utils.createEnumConstants(Move.class, MOVE_TYPE);
      constants = Arrays.copyOf(constants, constants.length + 5);
      constants[constants.length - 5] = IntegerUtils.INTEGER_UTILS.zero();
      constants[constants.length - 4] = Utils.TRUE_NODE;
      constants[constants.length - 3] = new ConstantNode(Pole.LEFT, POLE_TYPE);
      constants[constants.length - 2] = new ConstantNode(Pole.MIDDLE, POLE_TYPE);
      constants[constants.length - 1] = new ConstantNode(Pole.RIGHT, POLE_TYPE);
      Type[] variables = { STATE_TYPE, nullableType(MOVE_TYPE) };
      FitnessFunction fitnessFunction = new HanoiFitnessFunction(false);

      Node best = new RunBuilder().setReturnType(MOVE_TYPE).useDefaultRandom().setFunctionSet(functions).setConstants(constants).setVariables(variables)
            .setFitnessFunction(fitnessFunction).useDefaultGenerationEvolver().setMaxGenerations(NUM_GENERATIONS)
            .setInitialGenerationSize(INITIAL_GENERATION_SIZE).setTreeDepth(INITIAL_GENERATION_MAX_DEPTH).process();

      new HanoiFitnessFunction(true).evaluate(best);
   }

   private static class HanoiFitnessFunction implements FitnessFunction {
      private final boolean doLog;

      HanoiFitnessFunction(boolean doLog) {
         this.doLog = doLog;
      }

      @Override
      public double evaluate(Node n) {
         TowersOfHanoi towersOfHanoi = START_STATE;
         Set<TowersOfHanoi> previousStates = new HashSet<>();
         previousStates.add(towersOfHanoi);

         Move previousMove = null;
         int previousFitness = 1000;
         while (true) {
            Assignments assignments = Assignments.createAssignments(towersOfHanoi, previousMove);
            previousMove = n.evaluate(assignments);
            towersOfHanoi = towersOfHanoi.move(previousMove);
            if (doLog) {
               System.out.println(previousMove + " " + towersOfHanoi);
            }
            if (towersOfHanoi == null || !previousStates.add(towersOfHanoi)) {
               return previousFitness;
            }
            previousFitness = Math.min(previousFitness, towersOfHanoi.getFitness());
            if (previousFitness == 0) {
               return previousFitness;
            }
         }
      }
   }

   private static class IsValid implements Function {
      private final Signature signature;

      IsValid() {
         this.signature = Signature.createSignature(booleanType(), STATE_TYPE, MOVE_TYPE);
      }

      @Override
      public Signature getSignature() {
         return signature;
      }

      @Override
      public Object evaluate(Arguments arguments, Assignments assignments) {
         TowersOfHanoi gameState = arguments.firstArg().evaluate(assignments);
         Move move = arguments.secondArg().evaluate(assignments);
         return gameState.move(move) != null;
      }
   }

   private static class Next implements Function {
      private static final Signature SIGNATURE = Signature.createSignature(integerType(), STATE_TYPE, POLE_TYPE);

      @Override
      public Signature getSignature() {
         return SIGNATURE;
      }

      @Override
      public Object evaluate(Arguments arguments, Assignments assignments) {
         TowersOfHanoi gameState = arguments.firstArg().evaluate(assignments);
         Pole pole = arguments.secondArg().evaluate(assignments);
         return gameState.upperDisc(pole);
      }
   }
}