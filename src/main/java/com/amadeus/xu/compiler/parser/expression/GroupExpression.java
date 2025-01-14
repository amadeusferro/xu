package com.amadeus.xu.compiler.parser.expression;

public class GroupExpression extends ExpressionNode {

    public final ExpressionNode expression;

    public GroupExpression(ExpressionNode expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "Group(" + expression.toString() + ")";
    }

    @Override
    public String getVisualTree() {
        return "<li><code>Group</code><ul>" + expression.getVisualTree() + "</ul></li>";
    }
}

