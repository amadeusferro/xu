package com.amadeus.xu.compiler.parser.expression;

import com.amadeus.xu.compiler.lexer.Token;

public class VariableGetExpression extends ExpressionNode {

    public final Token name;

    public VariableGetExpression(Token name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Variable Get(" + name.lexeme + ")";
    }

    @Override
    public String getVisualTree() {
        return "<li><code>Variable Get</code><ul><li><code>" + name.lexeme + "</code></li></ul></li>";
    }
}
