package com.amadeus.xu.parser.expression;

import com.amadeus.xu.parser.visitor.ExpressionVisitor;

public class ArrayTypeExpression extends ExpressionNode {

    private final ExpressionNode type;
    private final int length;

    public ArrayTypeExpression(ExpressionNode type, int length) {
        this.type = type;
        this.length = length;
    }

    @Override
    public String getTreeNode() {

            String result = "<li><code>Array Type</code><ul>";
            result+= type.getTreeNode();
            result+="<li><code>[";
            result+=length;
            result+="]</li></code></ul></li>";
            return result;

    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return null;
    }
}
