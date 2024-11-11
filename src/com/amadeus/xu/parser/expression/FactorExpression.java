package com.amadeus.xu.parser.expression;

import com.amadeus.xu.lexer.Token;

public class FactorExpression extends ExpressionNode {

    public final ExpressionNode leftExpression;
    public final Token operator;
    public final ExpressionNode rightExpression;

    public FactorExpression(ExpressionNode leftExpression, Token operator, ExpressionNode rightExpression) {
        this.leftExpression = leftExpression;
        this.operator = operator;
        this.rightExpression = rightExpression;
    }


    @Override
    public String toString() {
        return "Factor(" + leftExpression.toString() + operator.type.name() + rightExpression.toString() + ")";
    }
}
