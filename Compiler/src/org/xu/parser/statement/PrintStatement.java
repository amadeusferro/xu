package org.xu.parser.statement;

import org.xu.parser.expression.XuExpressionNode;
import org.xu.processor.XuStatementNodeProcessor;

public class PrintStatement extends XuStatementNode {
    public final XuExpressionNode value;

    public PrintStatement(XuExpressionNode value) {
        this.value = value;
    }

    @Override
    public <T> T acceptProcessor(XuStatementNodeProcessor<T> processor) {
        return processor.processPrintStatement(this);
    }
}
