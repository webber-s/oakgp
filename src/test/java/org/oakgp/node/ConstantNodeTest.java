package org.oakgp.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.oakgp.TestUtils.createConstant;
import static org.oakgp.Type.INTEGER;

import org.junit.Test;

public class ConstantNodeTest {
	@Test
	public void testGetters() {
		final ConstantNode n = createConstant(7);
		assertEquals(1, n.getNodeCount());
		assertSame(INTEGER, n.getType());
	}

	@Test
	public void testToString() {
		assertEquals("5", createConstant(5).toString());
	}

	@Test
	public void testEvaluate() {
		Integer expected = 9;
		ConstantNode n = createConstant(expected);
		Object actual = n.evaluate(null);
		assertSame(expected, actual);
	}

	@Test
	public void testReplaceAt() {
		ConstantNode n1 = createConstant(9);
		ConstantNode n2 = createConstant(5);
		assertEquals(n1, n1.replaceAt(0, t -> t));
		assertEquals(n2, n1.replaceAt(0, t -> n2));
	}

	@Test
	public void testEqualsAndHashCode() {
		final ConstantNode n1 = createConstant(7);
		final ConstantNode n2 = createConstant(7);
		assertNotSame(n1, n2);
		assertEquals(n1, n1);
		assertEquals(n1.hashCode(), n2.hashCode());
		assertEquals(n1, n2);
	}

	@Test
	public void testNotEquals() {
		final ConstantNode n = createConstant(7);
		assertNotEquals(n, createConstant(8));
		assertNotEquals(n, createConstant(-7));
		assertNotEquals(n, new VariableNode(7));
		assertNotEquals(n, new Integer(7));
	}
}
