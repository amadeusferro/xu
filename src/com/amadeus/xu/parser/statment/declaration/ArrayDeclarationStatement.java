package com.amadeus.xu.parser.statment.declaration;

import com.amadeus.xu.lexer.Token;
import com.amadeus.xu.parser.statment.StatementNode;

public class ArrayDeclarationStatement extends StatementNode {

    private Token openBracket;
    private Token closeBracket;

    public ArrayDeclarationStatement(Token openBracket, Token closeBracket) {
        this.openBracket = openBracket;
        this.closeBracket = closeBracket;
    }

    @Override
    public String toString() {
        return "Array Declaration([])";
    }

    @Override
    public String getTreeNode() {
        return "<li><code>Array Declaration</code><ul><li><code>[]</code></li></ul></li></li>";

    }
}
