package org.xu.parser.expression;

import org.xu.processor.XuExpressionNodeProcessor;
import org.xu.scanner.Token;

public class XuLiteralExpression extends XuExpressionNode {

    public final Token literal;

    public XuLiteralExpression(Token literal) {
        this.literal = literal;
    }

    @Override
    public <T> T acceptProcessor(XuExpressionNodeProcessor<T> processor) {
        return processor.processLiteralExpression(this);
    }

    @Override
    public String toString() {
        return "Literal(" + literal.literal() + ")";
    }
}
