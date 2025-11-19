package org.xu.parser.expression;

import org.xu.processor.XuExpressionNodeProcessor;

public abstract class XuExpressionNode {

    public abstract <T> T acceptProcessor(XuExpressionNodeProcessor<T> processor);
}
