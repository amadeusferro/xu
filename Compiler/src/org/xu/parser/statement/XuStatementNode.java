package org.xu.parser.statement;

import org.xu.processor.XuStatementNodeProcessor;

public abstract class XuStatementNode {

    public abstract <T> T acceptProcessor(XuStatementNodeProcessor<T> processor);
}
