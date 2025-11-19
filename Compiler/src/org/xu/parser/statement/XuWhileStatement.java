package org.xu.parser.statement;

import org.xu.parser.expression.XuExpressionNode;
import org.xu.processor.XuStatementNodeProcessor;

public class XuWhileStatement extends XuStatementNode {

    public final XuExpressionNode condition;
    public final XuStatementNode body;

    public XuWhileStatement(XuExpressionNode condition, XuStatementNode body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public <T> T acceptProcessor(XuStatementNodeProcessor<T> processor) {
        return processor.processWhileStatement(this);
    }
}
