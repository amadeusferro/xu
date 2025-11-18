package com.amadeus.xu.parser.expression;

import com.amadeus.xu.parser.statment.StatementNode;
import com.amadeus.xu.parser.visitor.ExpressionVisitor;

public class TernaryExpression extends ExpressionNode {

    private final ExpressionNode condition;
    private final ExpressionNode thenExpression;
    private final ExpressionNode elseExpression;


    public TernaryExpression(ExpressionNode condition, ExpressionNode thenStatement, ExpressionNode elseStatement) {
        this.condition = condition;
        this.elseExpression = thenStatement;
        this.thenExpression = elseStatement;
    }

    @Override
    public String toString() {
        return "Ternary(" + condition.toString() + thenExpression.toString() + (elseExpression == null ? "" : elseExpression.toString()) + ")";
    }

    @Override
    public String getTreeNode() {
        return "<li><code>Ternary expression</code><ul>" + condition.getTreeNode() + thenExpression.getTreeNode() + "<li><code>else</code><ul>" + (elseExpression == null ? "" : elseExpression.getTreeNode())  + "</ul></li></li>";

    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return null;
    }
}
