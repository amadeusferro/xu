package com.amadeus.xu.parser.expression;

import com.amadeus.xu.lexer.Token;
import com.amadeus.xu.parser.visitor.ExpressionVisitor;

public class AssignmentExpression extends ExpressionNode {


    private final Token name;
    private final Token equal;
    private final ExpressionNode value;

    public AssignmentExpression(Token name, Token equal, ExpressionNode value) {
        this.name = name;
        this.equal = equal;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Assignment(" + name.lexeme + equal.lexeme + value.toString() + ")";
    }

    @Override
    public String getTreeNode() {
        return "<li><code>Assignment Expression</code><ul><li><code>" + name.lexeme + "</li></code><li><code>" + equal.lexeme + "</code></li>" + value.getTreeNode() + "</ul></li>";
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return null;
    }
}
