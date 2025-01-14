package com.amadeus.xu.compiler.parser.statement;

import com.amadeus.xu.compiler.parser.expression.ExpressionNode;

public class ExpressionStatement extends StatementNode {
    public final ExpressionNode expression;

    public ExpressionStatement(ExpressionNode expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "Expression statement(" + expression.toString() + ")";
    }

    @Override
    public String getVisualTree() {
        return "<li><code>Expression statement</code><ul>" + expression.getVisualTree() + "</ul></li>";

    }
}
