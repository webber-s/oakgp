package org.oakgp.function.math;

import java.math.BigDecimal;

import org.oakgp.Assignments;
import org.oakgp.Type;
import org.oakgp.node.ConstantNode;
import org.oakgp.node.Node;

public final class BigDecimalUtils implements NumberUtils {
   public static final BigDecimalUtils BIG_DECIMAL_UTILS = new BigDecimalUtils();

   private final ConstantNode zero = createConstant(BigDecimal.ZERO);
   private final ConstantNode one = createConstant(BigDecimal.ONE);
   private final ConstantNode two = createConstant(BigDecimal.valueOf(2));
   private final Add add = new Add(this);
   private final Subtract subtract = new Subtract(this);
   private final Multiply multiply = new Multiply(this);

   /** @see #BIG_DECIMAL_UTILS */
   private BigDecimalUtils() {
      // do nothing
   }

   @Override
   public Type getType() {
      return Type.bigDecimalType();
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
      BigDecimal bd = n.evaluate(null);
      return createConstant(bd.negate());
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
      BigDecimal bd = n.evaluate(null);
      return bd.compareTo(BigDecimal.ZERO) < 0;
   }

   @Override
   public ConstantNode add(Node n1, Node n2, Assignments assignments) {
      BigDecimal bd1 = n1.evaluate(assignments);
      BigDecimal bd2 = n2.evaluate(assignments);
      return createConstant(bd1.add(bd2));
   }

   @Override
   public ConstantNode add(Node n, int i) {
      BigDecimal bd = n.evaluate(null);
      // TODO having to do new BigDecimal(i) each time
      // TODO replace this method with inc(Node) and dec(Node)
      return createConstant(bd.add(new BigDecimal(i)));
   }

   @Override
   public ConstantNode subtract(Node n1, Node n2, Assignments assignments) {
      BigDecimal bd1 = n1.evaluate(assignments);
      BigDecimal bd2 = n2.evaluate(assignments);
      return createConstant(bd1.subtract(bd2));
   }

   @Override
   public ConstantNode multiply(Node n1, Node n2, Assignments assignments) {
      BigDecimal bd1 = n1.evaluate(assignments);
      BigDecimal bd2 = n2.evaluate(assignments);
      return createConstant(bd1.multiply(bd2));
   }

   private ConstantNode createConstant(BigDecimal bd) {
      return new ConstantNode(bd, Type.bigDecimalType());
   }
}
