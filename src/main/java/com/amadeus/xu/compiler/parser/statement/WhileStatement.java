package com.amadeus.xu.compiler.parser.statement;

import com.amadeus.xu.compiler.parser.expression.ExpressionNode;

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
    public String getVisualTree() {
        return "<li><code>While statement</code><ul>" + condition.getVisualTree() + body.getVisualTree()  + "</li></li>";

    }
}
