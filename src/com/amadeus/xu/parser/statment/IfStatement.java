package com.amadeus.xu.parser.statment;

import com.amadeus.xu.parser.expression.ExpressionNode;

public class IfStatement extends StatementNode {

    private final ExpressionNode condition;
    private final StatementNode thenStatement;
    private final StatementNode elseStatement;

    public IfStatement(ExpressionNode condition, StatementNode thenStatement, StatementNode elseStatement) {
        this.condition = condition;
        this.thenStatement = thenStatement;
        this.elseStatement = elseStatement;
    }

    @Override
    public String toString() {
        return "If(" + condition.toString() + thenStatement.toString() + (elseStatement == null ? "" : elseStatement.toString()) + ")";
    }

    @Override
    public String getTreeNode() {
        return "<li><code>If statement</code><ul>" + condition.getTreeNode() + thenStatement.getTreeNode() + (elseStatement == null ? "" : ("<li><code>else</code><ul>" + elseStatement.getTreeNode() + "</ul>"))  + "</li></li>";

    }




}
