package org.xu.processor;

import org.xu.parser.expression.*;
import org.xu.parser.expression.XuVariableGetExpression;

public interface XuExpressionNodeProcessor<T> {

    T processLiteralExpression(XuLiteralExpression expression);

    T processBinaryExpression(XuBinaryExpression expression);

    T processUnaryExpression(XuUnaryExpression expression);

    T processLogicalExpression(XuLogicalExpression expression);

    T processGroupExpression(XuGroupExpression expression);

    T processVariableGetExpression(XuVariableGetExpression expression);

    T processAssignmentExpression(XuAssignmentExpression expression);
}
