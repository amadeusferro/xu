package com.amadeus.xu.compiler.parser.expression;

import com.amadeus.xu.compiler.lexer.Token;

public class BinaryExpression extends ExpressionNode {

    public final ExpressionNode leftExpression;
    public final Token operator;
    public final ExpressionNode rightExpression;

    public BinaryExpression(ExpressionNode leftExpression, Token operator, ExpressionNode rightExpression) {
        this.leftExpression = leftExpression;
        this.operator = operator;
        this.rightExpression = rightExpression;
    }

    @Override
    public String toString() {
        return "Logical(" + leftExpression.toString() + operator.type.name() + rightExpression.toString() + ")";
    }

    @Override
    public String getVisualTree() {
        return "<li><code>Logical</code><ul>" + leftExpression.getVisualTree() + "<li><code>" + operator.type.name() + "</code></li>" + rightExpression.getVisualTree() + "</ul></li>";
    }
}
