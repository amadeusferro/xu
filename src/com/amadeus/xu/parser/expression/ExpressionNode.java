package com.amadeus.xu.parser.expression;

import com.amadeus.xu.parser.visitor.ExpressionVisitor;

public abstract class ExpressionNode {

    public abstract String getTreeNode();
    public abstract<T> T accept(ExpressionVisitor<T> visitor);
}

