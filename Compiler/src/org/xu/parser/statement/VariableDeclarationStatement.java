package org.xu.parser.statement;

import org.xu.parser.expression.XuExpressionNode;
import org.xu.processor.XuStatementNodeProcessor;
import org.xu.scanner.Token;

public class VariableDeclarationStatement extends XuStatementNode {

    public final Token type;
    public final Token name;
    public final XuExpressionNode value;

    public VariableDeclarationStatement(Token type, Token name, XuExpressionNode value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    @Override
    public <T> T acceptProcessor(XuStatementNodeProcessor<T> processor) {
        return processor.processVariableDeclaration(this);
    }
}
