package org.xu.parser.expression;

import org.xu.processor.XuExpressionNodeProcessor;
import org.xu.scanner.Token;

public class XuUnaryExpression extends XuExpressionNode {

    public final Token operator;
    public final XuExpressionNode expression;

    public XuUnaryExpression(Token operator, XuExpressionNode expression) {
        this.operator = operator;
        this.expression = expression;
    }

    @Override
    public <T> T acceptProcessor(XuExpressionNodeProcessor<T> processor) {
        return processor.processUnaryExpression(this);
    }

    @Override
    public String toString() {
        return "Unary('" + operator.lexeme() + "', " + expression.toString() + ")";
    }
}
