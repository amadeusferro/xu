package com.amadeus.xu.compiler.parser.statement.declaration;


import com.amadeus.xu.compiler.lexer.Token;
import com.amadeus.xu.compiler.parser.expression.ExpressionNode;
import com.amadeus.xu.compiler.parser.statement.StatementNode;

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
    public String getVisualTree() {
        StringBuilder sb = new StringBuilder();
        sb.append("<li><code>Variable Declaration</code><ul>")
                .append((constant == null) ? "" : ("<li><code>" + constant.lexeme + "</code></li>"))
                .append(type.getVisualTree())
                .append("<li><code>")
                .append(name.lexeme)
                .append("</code></li>")
                .append((equal == null) ? "" : ("<li><code>" + equal.lexeme + "</code></li>"))
                .append((value == null) ? "" : value.getVisualTree())
                .append("</ul></li>");

        return sb.toString();
    }
}
