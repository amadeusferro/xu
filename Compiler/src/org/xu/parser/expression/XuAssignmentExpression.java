package org.xu.parser.expression;

import org.xu.processor.XuExpressionNodeProcessor;
import org.xu.scanner.Token;

public class XuAssignmentExpression extends XuExpressionNode {

    public final Token name;
    public final Token equals;
    public final XuExpressionNode value;

    public XuAssignmentExpression(Token name, Token equals, XuExpressionNode value) {
        this.name = name;
        this.equals = equals;
        this.value = value;
    }

    @Override
    public <T> T acceptProcessor(XuExpressionNodeProcessor<T> processor) {
        return processor.processAssignmentExpression(this);
    }
}
