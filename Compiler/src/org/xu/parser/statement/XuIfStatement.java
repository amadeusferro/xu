package org.xu.parser.statement;

import org.xu.parser.expression.XuExpressionNode;
import org.xu.processor.XuStatementNodeProcessor;

public class XuIfStatement extends XuStatementNode {

    public final XuExpressionNode condition;
    public final XuStatementNode thenStatement;
    public final XuStatementNode elseStatement;

    public XuIfStatement(XuExpressionNode condition, XuStatementNode thenStatement, XuStatementNode elseStatement) {
        this.condition = condition;
        this.thenStatement = thenStatement;
        this.elseStatement = elseStatement;
    }

    @Override
    public <T> T acceptProcessor(XuStatementNodeProcessor<T> processor) {
        return processor.processIfStatement(this);
    }
}
