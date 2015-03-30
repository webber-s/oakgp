package org.oakgp.operator;

import static org.oakgp.util.NodeComparator.NODE_COMPARATOR;

import java.util.Optional;

import org.oakgp.Arguments;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

public final class Equal extends ComparisonOperator {
	public Equal() {
		super(true);
	}

	@Override
	protected boolean evaluate(int arg1, int arg2) {
		return arg1 == arg2;
	}

	@Override
	public Optional<Node> simplify(Arguments arguments) {
		Optional<Node> o = super.simplify(arguments);
		if (!o.isPresent() && NODE_COMPARATOR.compare(arguments.get(0), arguments.get(1)) > 0) {
			return Optional.of(new FunctionNode(this, arguments.get(1), arguments.get(0)));
		} else {
			return o;
		}
	}
}