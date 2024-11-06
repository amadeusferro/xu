package com.amadeus.xu.parser.expression;

import com.amadeus.xu.lexer.Token;

public class UnaryExpression extends ExpressionNode {

    public final Token operator;
    public final ExpressionNode expression;


    public UnaryExpression(Token operator, ExpressionNode expression) {
        this.operator = operator;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "Unary(" + operator.type.name() + ", " + expression.toString() + ")";
    }
}
