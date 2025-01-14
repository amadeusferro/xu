package com.amadeus.xu.compiler.parser.expression;

import com.amadeus.xu.compiler.lexer.Token;

public class ArrayTypeExpression extends ExpressionNode {

    private final ExpressionNode type;
    private final int length;

    public ArrayTypeExpression(ExpressionNode type, int length) {
        this.type = type;
        this.length = length;
    }

    @Override
    public String getVisualTree() {
        StringBuilder sb = new StringBuilder();
        sb.append("<li><code>Array Type</code><ul>")
                .append(type.getVisualTree())
                .append("<li><code>[")
                .append(length)
                .append("]</li></code></ul></li>");
        return sb.toString();
    }
}

