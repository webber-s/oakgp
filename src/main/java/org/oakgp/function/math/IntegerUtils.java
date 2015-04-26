package org.oakgp.function.math;

import org.oakgp.Type;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;

public final class IntegerUtils implements NumberUtils {
   public static final IntegerUtils INTEGER_UTILS = new IntegerUtils();

   private final ConstantNode zero = createConstant(0);
   private final ConstantNode one = createConstant(1);
   private final ConstantNode two = createConstant(2);
   private final Add add = new Add(this);
   private final Subtract subtract = new Subtract(this);
   private final Multiply multiply = new Multiply(this);

   @Override
   public Type getType() {
      return Type.integerType();
   }

   @Override
   public Add getAdd() {
      return add;
   }

   @Override
   public Subtract getSubtract() {
      return subtract;
   }

   @Override
   public Multiply getMultiply() {
      return multiply;
   }

   @Override
   public ConstantNode negate(Node n) {
      return createConstant(-(int) n.evaluate(null));
   }

   @Override
   public boolean isZero(Node n) {
      return zero.equals(n);
   }

   @Override
   public boolean isOne(Node n) {
      return one.equals(n);
   }

   @Override
   public ConstantNode zero() {
      return zero;
   }

   @Override
   public ConstantNode one() {
      return one;
   }

   @Override
   public ConstantNode two() {
      return two;
   }

   @Override
   public boolean isNegative(Node n) {
      return ((int) n.evaluate(null)) < 0;
   }

   @Override
   public ConstantNode add(Node n1, Node n2) {
      int i1 = n1.evaluate(null);
      int i2 = n2.evaluate(null);
      return createConstant(i1 + i2);
   }

   @Override
   public ConstantNode add(Node n, int i) {
      return createConstant((int) n.evaluate(null) + i);
   }

   @Override
   public ConstantNode subtract(Node n1, Node n2) {
      int i1 = n1.evaluate(null);
      int i2 = n2.evaluate(null);
      return createConstant(i1 - i2);
   }

   @Override
   public ConstantNode multiply(Node n1, Node n2) {
      int i1 = n1.evaluate(null);
      int i2 = n2.evaluate(null);
      return createConstant(i1 * i2);
   }

   private ConstantNode createConstant(int i) {
      return new ConstantNode(i, Type.integerType());
   }
}