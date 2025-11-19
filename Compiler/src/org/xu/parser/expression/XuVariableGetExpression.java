package org.xu.parser.expression;

import org.xu.processor.XuExpressionNodeProcessor;
import org.xu.scanner.Token;

public class XuVariableGetExpression extends XuExpressionNode {

    public final Token name;

    public XuVariableGetExpression(Token name) {
        this.name = name;
    }

    @Override
    public <T> T acceptProcessor(XuExpressionNodeProcessor<T> processor) {
        return processor.processVariableGetExpression(this);
    }
}
