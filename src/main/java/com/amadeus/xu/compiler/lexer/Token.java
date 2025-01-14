package com.amadeus.xu.compiler.lexer;

public class Token {

    public final TokenType type;
    public final int line;
    public final int column;
    public final String lexeme;
    public final Object literal;

    public Token(TokenType type, int line, int column, String lexeme, Object literal) {
        this.type = type;
        this.line = line;
        this.column = column;
        this.lexeme = lexeme;
        this.literal = literal;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("<")
                .append(type.toString())
                .append(", ")
                .append(lexeme)
                .append(", ")
                .append(literal)
                .append(", ")
                .append(line)
                .append(":").
                append(column)
                .append(">");

        return sb.toString();
    }
}
