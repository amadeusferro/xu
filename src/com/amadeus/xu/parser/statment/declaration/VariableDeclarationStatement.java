package com.amadeus.xu.parser.statment.declaration;

import com.amadeus.xu.lexer.Token;
import com.amadeus.xu.parser.expression.ExpressionNode;
import com.amadeus.xu.parser.statment.StatementNode;

public class VariableDeclarationStatement extends StatementNode {

    private final Token constant;
    private final ExpressionNode type;
    private final Token name;
    private final Token equal;
    private final ExpressionNode value;


    public VariableDeclarationStatement(Token constant,ExpressionNode type, Token name, Token equal, ExpressionNode value) {
        this.constant = constant;
        this.type = type;
        this.name = name;
        this.equal = equal;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Variable Declaration(" + ((constant == null) ? "" : (constant.lexeme)) + type.toString() + name.lexeme + ((equal == null) ? "" : (equal.lexeme)) + ((value == null) ? "" : (value.toString())) + ")";
    }

    @Override
    public String getTreeNode() {
        String result = "<li><code>Variable Declaration</code><ul>";

        result += (constant == null) ? "" : ("<li><code>" + constant.lexeme + "</code></li>");
        result += type.getTreeNode();
        result += "<li><code>" + name.lexeme + "</code></li>";

        result += (equal == null) ? "" : ("<li><code>" + equal.lexeme + "</code></li>");
        result += (value == null) ? "" : value.getTreeNode();

        result += "</ul></li>";

        return result;
    }

}