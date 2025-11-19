package org.xu.parser.statement;

import org.xu.processor.XuStatementNodeProcessor;

import java.util.List;

public class XuBodyStatement extends XuStatementNode {

    public final List<XuStatementNode> statements;

    public XuBodyStatement(List<XuStatementNode> statements) {
        this.statements = statements;
    }

    @Override
    public <T> T acceptProcessor(XuStatementNodeProcessor<T> processor) {
        return processor.processBodyStatement(this);
    }
}
