package com.amadeus.xu.lexer;

import java.util.ArrayList;
import java.util.HashMap;

import static com.amadeus.xu.lexer.TokenType.*;

//TODO 0b1010;       Binário para decimal (10)
//TODO 0o12;         Octal para decimal (10)
//TODO 0xA           Hexadecimal para decimal (10)
//TODO 100_000       Separador de casas (100000)

public class Lexer {

    private int start;
    private int current;
    private String source;
    private ArrayList<Token> tokensList;
    private static final HashMap<String, TokenType> keywords;
    private int line = 1;
    private int column = 1;

    static {
        keywords = new HashMap<>();
        keywords.put("import", IMPORT);
        keywords.put("return", RETURN);
        keywords.put("if", IF);
        keywords.put("else", ELSE);
        keywords.put("xu", XU);
        keywords.put("true", TRUE);
        keywords.put("false", FALSE);
        keywords.put("while", WHILE);
        keywords.put("for", FOR);
        keywords.put("or", OR);
        keywords.put("and", AND);
        keywords.put("auto", AUTO);
        keywords.put("const", CONST);
    }

    public Lexer() {
    }

    public ArrayList<Token> scanTokens(String source) {
        resetInternalState(source);

        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        makeToken(EOF, null);

        return tokensList;
    }

    private void scanToken() {
        char c = advance();

        switch (c) {
            case '\r':
            case ' ':
                break;
            case '\t':
                column+=4;
                break;
            case '\n':
                line++;
                column = 1;
                break;
            case '#':
                makeToken(HASH, "#");
                break;
            case '.':
                makeToken(DOT, ".");
                break;
            case '"':
                string();
                break;
            case '(':
                makeToken(LEFT_PARENTHESIS, "(");
                break;
            case ')':
                makeToken(RIGHT_PARENTHESIS, ")");
                break;
            case '{':
                makeToken(LEFT_BRACES, "{");
                break;
            case '}':
                makeToken(RIGHT_BRACES, "}");
                break;
            case '[':
                makeToken(OPENBRACKET, "[");
                break;
            case ']':
                makeToken(CLOSEBRACKET, "]");
                break;
            case ',':
                makeToken(COMMA, ",");
                break;
            case '>':
                if (match('=')) {
                    makeToken(BIGGER_EQUAL, ">=");
                } else {
                    makeToken(BIGGER, ">");
                }
                break;
            case '<':
                if (match('=')) {
                    makeToken(LESS_EQUAL, "<=");
                } else {
                    makeToken(LESS, "<");
                }
                break;
            case '+':
                makeToken(PLUS, "+");
                break;
            case ';':
                makeToken(SEMICOLON, ";");
                break;
            case '!':
                if (match('=')) {
                    makeToken(NOT_EQUAL, "!=");
                } else {
                    makeToken(MARK, "!");
                }
                break;
            case '-':
                makeToken(MINUS, "-");
                break;
            case '*':
                makeToken(ASTERISK, "*");
                break;
            case '=':
                if (match('=')) {
                  makeToken(EQUAL_EQUAL, "==");
                } else {
                    makeToken(EQUAL, "=");
                }
                break;
            case '/':
                makeToken(SLASH, "/");
                break;
            case '\'':
                makeToken(BACKSLASH, "\"");
                break;
            default:
                if (isAlpha(c)) {
                    identifier();
                } else if (isDigit(c)) {
                    number();
                }
        }
    }

    private void resetInternalState(String source) {
        this.start = 0;
        this.current = 0;
        this.line = 1;
        this.column = 1;
        this.source = source;
        this.tokensList = new ArrayList<>();
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char peek() {
        if (isAtEnd()) return '\0';

        return source.charAt(current);
    }

    private char advance() {
        column++;
        return source.charAt(current++);
    }

    private boolean isAlpha(char c) {
        return Character.isAlphabetic(c);
    }

    private boolean isDigit(char c) {
        return Character.isDigit(c);
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private void makeToken(TokenType tokenType, String lexeme, Object literal) {
        tokensList.add(new Token(tokenType, lexeme, line, column, literal));
    }

    private void makeToken(TokenType tokenType, Object literal) {
        String lexeme = source.substring(start, current);
        tokensList.add(new Token(tokenType, lexeme, line, column, literal));
    }

    private void makeToken(TokenType tokenType) {
        String lexeme = source.substring(start, current);
        tokensList.add(new Token(tokenType, lexeme, line, column, null));
    }

    private void number() {
        while (isDigit(peek())) advance();


        if (peek() == '.') {
            do {
                advance();
            } while (isDigit(peek()));
            String floatValue = source.substring(start, current);
            makeToken(FLOAT, Float.parseFloat(floatValue));

        } else {
            String intValue = source.substring(start, current);
            makeToken(INT, Integer.parseInt(intValue));
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        String text = source.substring(start, current);
        TokenType type = keywords.getOrDefault(text, IDENTIFIER);
        makeToken(type, text, text);
    }

    private void string() {
        while (peek() != '"') {
            advance();
        }
        advance();

        String str = source.substring(start + 1, current - 1);
        makeToken(STRING, str);
    }

    private boolean match(char c) {
        if (isAtEnd()) return false;
        if (peek() != c) return false;
        advance();
        return true;
    }
}
