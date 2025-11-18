package com.amadeus.xu.parser.expression;

import com.amadeus.xu.lexer.Token;
import com.amadeus.xu.parser.visitor.ExpressionVisitor;

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

    @Override
    public String getTreeNode() {
        return "<li><code>Unary(" + operator.type.name() + ")</code><ul>" + expression.getTreeNode() + " </ul></li>";
    }

    @Override
    public <T> T accept(ExpressionVisitor<T> visitor) {
        return visitor.visitUnaryExpression(this);
    }
}
