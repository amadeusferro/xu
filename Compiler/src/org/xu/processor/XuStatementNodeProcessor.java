package org.xu.processor;

import org.xu.parser.statement.*;

public interface XuStatementNodeProcessor<T> {

    T processIfStatement(XuIfStatement statement);

    T processExpressionStatement(XuExpressionStatement statement);

    T processBodyStatement(XuBodyStatement statement);

    T processPrintStatement(PrintStatement statement);

    T processVariableDeclaration(VariableDeclarationStatement statement);

    T processWhileStatement(XuWhileStatement statement);
}
