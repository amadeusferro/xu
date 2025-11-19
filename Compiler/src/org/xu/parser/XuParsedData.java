package org.xu.parser;

import org.xu.component.XuIOComponent;
import org.xu.parser.statement.XuStatementNode;

import java.util.List;

public class XuParsedData extends XuIOComponent<XuParsedData> {

    private final List<XuStatementNode> expressions;

    public XuParsedData(List<XuStatementNode> expressions) {
        this.expressions = expressions;
    }

    public List<XuStatementNode> getStatements() {
        return expressions;
    }

    @Override
    public XuParsedData clone() {
        return new XuParsedData(expressions);
    }
}
