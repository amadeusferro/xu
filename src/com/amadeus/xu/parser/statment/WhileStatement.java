package com.amadeus.xu.parser.statment;

import com.amadeus.xu.parser.expression.ExpressionNode;

public class WhileStatement extends StatementNode {

    private final ExpressionNode condition;
    private final StatementNode body;

    public WhileStatement(ExpressionNode condition, StatementNode body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public String toString() {
        return "While(" + condition.toString() + body.toString() + ")";
    }

    @Override
    public String getTreeNode() {
        return "<li><code>While statement</code><ul>" + condition.getTreeNode() + body.getTreeNode()  + "</li></li>";

    }
}
