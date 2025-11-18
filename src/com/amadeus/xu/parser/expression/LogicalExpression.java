package com.amadeus.xu.parser.expression;

import com.amadeus.xu.lexer.Token;
import com.amadeus.xu.parser.visitor.ExpressionVisitor;

public class LogicalExpression extends ExpressionNode {

    public final ExpressionNode leftExpression;
    public final Token operator;
    public final ExpressionNode rightExpression;

    public LogicalExpression(ExpressionNode leftExpression, Token operator, ExpressionNode rightExpression) {
        this.leftExpression = leftExpression;
        this.operator = operator;
        this.rightExpression = rightExpression;
    }

    @Override
    public String toString() {
        return "Logical(" + leftExpression.toString() + operator.type.name() + rightExpression.toString() + ")";
    }

    @Override
    public String getTreeNode() {
        return "<li><code>Logical</code><ul>" + leftExpression.getTreeNode() + "<li><code>" + operator.type.name() + "</code></li>" + rightExpression.getTreeNode() + "</ul></li>";
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return null;
    }
}
