package org.oakgp;

import static org.oakgp.Arguments.createArguments;

import org.oakgp.node.ConstantNode;
import org.oakgp.node.FunctionNode;
import org.oakgp.node.Node;

public class NodeSimplifier {
	public Node simplify(Node input) {
		if (input instanceof FunctionNode) {
			return simplifyFunctionNode((FunctionNode) input);
		} else {
			return input;
		}
	}

	private Node simplifyFunctionNode(FunctionNode input) {
		Arguments inputArgs = input.getArguments();
		Node[] simplifiedArgs = new Node[inputArgs.length()];
		boolean modified = false;
		boolean constants = true;
		for (int i = 0; i < simplifiedArgs.length; i++) {
			Node originalArg = inputArgs.get(i);
			simplifiedArgs[i] = simplify(originalArg);
			if (originalArg != simplifiedArgs[i]) {
				modified = true;
			}
			if (!(simplifiedArgs[i] instanceof ConstantNode)) {
				constants = false;
			}
		}

		FunctionNode output;
		if (modified) {
			output = new FunctionNode(input.getOperator(), createArguments(simplifiedArgs));
		} else {
			output = input;
		}
		if (constants) {
			return new ConstantNode(output.evaluate(null));
		} else {
			return output;
		}
	}
}
