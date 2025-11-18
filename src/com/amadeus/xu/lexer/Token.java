package com.amadeus.xu.lexer;

public class Token {

    public final TokenType type;
    public final String lexeme;
    public final int line;
    public final int column;
    public final Object literal;

    public Token(TokenType type, String lexeme, int line, int column, Object literal) {
        this.type = type;
        this.lexeme = lexeme;
        this.line = line;
        this.column = column;
        this.literal = literal;
    }

    @Override
    public String toString() {
        return "<" + type.toString() + ", " + lexeme + ", " + literal + ", " + line + ":" + column + ">";
    }
}
