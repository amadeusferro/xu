package com.amadeus.xu.parser.expression;

import com.amadeus.xu.lexer.Token;

public class GroupExpression extends ExpressionNode {

    public final Token paren;
    public final ExpressionNode expression;

    public GroupExpression(Token paren, ExpressionNode expression) {
        this.paren = paren;
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "Group(" + expression.toString() + ")";
    }

    @Override
    public String getTreeNode() {
        return "<li><code>Group</code><ul>" + expression.getTreeNode() + "</ul></li>";
    }

}
