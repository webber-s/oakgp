package org.oakgp;

import org.oakgp.node.Node;

/** Associates a {@code Node} with its fitness value. */
public final class RankedCandidate implements Comparable<RankedCandidate> { // TODO rename?
	private final double fitness;
	private final Node node;

	public RankedCandidate(Node node, double fitness) {
		this.node = node;
		this.fitness = fitness;
	}

	public double getFitness() {
		return fitness;
	}

	public Node getNode() {
		return node;
	}

	@Override
	public int compareTo(RankedCandidate o) {
		return Double.compare(fitness, o.fitness);
	}
}
