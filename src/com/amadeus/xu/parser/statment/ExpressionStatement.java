package com.amadeus.xu.parser.statment;


import com.amadeus.xu.parser.expression.ExpressionNode;

public class ExpressionStatement extends StatementNode {

    public final ExpressionNode expression;

    public ExpressionStatement(ExpressionNode expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "expr(" + expression.toString() + ")";
    }

    @Override
    public String getTreeNode() {
        return "<li><code>Expression statement</code><ul>" + expression.getTreeNode() + "</ul></li>";

    }
}
