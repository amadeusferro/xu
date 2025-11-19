package org.xu.parser.expression;

import org.xu.processor.XuExpressionNodeProcessor;
import org.xu.scanner.Token;

public class XuGroupExpression extends XuExpressionNode {

    public final Token paren;
    public final XuExpressionNode expression;

    public XuGroupExpression(Token paren, XuExpressionNode expression) {
        this.paren = paren;
        this.expression = expression;
    }

    @Override
    public <T> T acceptProcessor(XuExpressionNodeProcessor<T> processor) {
        return processor.processGroupExpression(this);
    }
}
