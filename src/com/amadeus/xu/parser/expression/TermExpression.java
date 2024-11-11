package com.amadeus.xu.parser.expression;

import com.amadeus.xu.lexer.Token;

public class TermExpression extends ExpressionNode {

    public final ExpressionNode leftExpression;
    public final Token operator;
    public final ExpressionNode rightExpression;

    public TermExpression(ExpressionNode leftExpression, Token operator, ExpressionNode rightExpression) {
        this.leftExpression = leftExpression;
        this.operator = operator;
        this.rightExpression = rightExpression;
    }

    @Override
    public String toString() {
        return "Factor(" + leftExpression.toString() + operator.type.name() + rightExpression.toString() + ")";
    }

    @Override
    public String getTreeNode() {
        return "<li><code>Term</code><ul>" + leftExpression.getTreeNode() + "<li><code>" + operator.type.name() + "</code></li>" + rightExpression.getTreeNode() + "</ul></li>";
    }

}
