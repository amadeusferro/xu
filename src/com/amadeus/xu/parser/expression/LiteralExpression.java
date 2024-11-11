package com.amadeus.xu.parser.expression;

import com.amadeus.xu.lexer.Token;

public class LiteralExpression extends ExpressionNode {


    public final Token literal;

    public LiteralExpression(Token literal) {
        this.literal = literal;
    }

    @Override
    public String toString() {
        return "Literal(" + literal.literal.toString() + ')';
    }

    @Override
    public String getTreeNode() {
        return "<li><code>Literal(" + literal.literal.toString() + ")</code></li>";
    }
}
