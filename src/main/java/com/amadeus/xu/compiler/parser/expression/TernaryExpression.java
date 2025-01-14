package com.amadeus.xu.compiler.parser.expression;

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
    public String getVisualTree() {
        return "<li><code>Ternary expression</code><ul>" + condition.getVisualTree() + thenExpression.getVisualTree() + "<li><code>else</code><ul>" + (elseExpression == null ? "" : elseExpression.getVisualTree())  + "</ul></li></li>";
    }
}
