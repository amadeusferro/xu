package com.amadeus.xu.compiler.parser.statement;

import java.util.List;

public class BlockStatement extends StatementNode {

    List<StatementNode> statements;

    public BlockStatement(List<StatementNode> statements) {
        this.statements = statements;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Block{ ");
        for (StatementNode statement : statements) {
            sb.append(statement.toString());
        }
        return sb.append("}").toString();
    }

    @Override
    public String getVisualTree() {

        StringBuilder sb = new StringBuilder();
        sb.append("<li><code>Block{}</code><ul><code>");
        for (StatementNode statement : statements) {
            sb.append(statement.getVisualTree());
        }
        return sb.append("</ul></code>").toString();
    }
}
