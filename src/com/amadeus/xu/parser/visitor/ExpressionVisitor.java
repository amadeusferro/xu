package com.amadeus.xu.parser.visitor;

import com.amadeus.xu.parser.expression.*;

public interface ExpressionVisitor<T> {

    T visitLiteralExpression(LiteralExpression expr);
    T visitUnaryExpression(UnaryExpression expr);
    T visitFactorExpression(FactorExpression expr);
    T visitTermExpression(TermExpression expr);
    T visitGroupExpression(GroupExpression expr);
}
