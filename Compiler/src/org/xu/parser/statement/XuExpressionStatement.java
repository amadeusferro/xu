package org.xu.parser.statement;

import org.xu.parser.expression.XuExpressionNode;
import org.xu.processor.XuStatementNodeProcessor;

public class XuExpressionStatement extends XuStatementNode {

    public final XuExpressionNode expression;

    public XuExpressionStatement(XuExpressionNode expression) {
        this.expression = expression;
    }

    @Override
    public <T> T acceptProcessor(XuStatementNodeProcessor<T> processor) {
        return processor.processExpressionStatement(this);
    }
}
