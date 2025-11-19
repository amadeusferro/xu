package org.xu.scanner;

import org.xu.type.XuValue;

public record Token(TokenType type, String lexeme, XuValue literal, int line) {

    @Override
    public String toString() {
        return "<" + type.toString() + ", '" + lexeme + "'>";
    }
}
