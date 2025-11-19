package org.xu.parser.expression;

import org.xu.processor.XuExpressionNodeProcessor;
import org.xu.scanner.Token;

public class XuLogicalExpression extends XuExpressionNode {

    public final XuExpressionNode left;
    public final Token operator;
    public final XuExpressionNode right;

    public XuLogicalExpression(XuExpressionNode left, Token operator, XuExpressionNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public <T> T acceptProcessor(XuExpressionNodeProcessor<T> processor) {
        return processor.processLogicalExpression(this);
    }
}
