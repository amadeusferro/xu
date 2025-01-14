package com.amadeus.xu.compiler.lexer;

//TODO 0b1010;
//TODO 0o12;
//TODO 0xA
//TODO 100_000
//TODO bitwise

import com.amadeus.xu.util.XuFile;

import java.util.ArrayList;
import java.util.HashMap;

import static com.amadeus.xu.compiler.lexer.TokenType.*;

public class Lexer {

    private int start;
    private int current;
    private String source;
    private ArrayList<Token> tokenList;
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

    public Lexer() {}

    public ArrayList<Token> scanTokens(XuFile xuFile) {
        resetInternalState(xuFile);

        while(!isAtEnd()) {
            start = current;
            scanToken();
        }
        makeToken(EOF, "\0", null);

        return tokenList;
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
            case ';':
                makeToken(SEMICOLON, ";");
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
                makeToken(OPEN_BRACKET, "[");
                break;
            case ']':
                makeToken(CLOSE_BRACKET, "]");
                break;
            case ',':
                makeToken(COMMA, ",");
                break;
            case '>':
                if(match('=')) {
                    makeToken(BIGGER_EQUAL, ">=");
                } else {
                    makeToken(BIGGER, ">");
                }
                break;
            case '<':
                if(match('=')) {
                    makeToken(LESS_EQUAL, "<=");
                } else {
                    makeToken(LESS, "<");
                }
                break;
            case '+':
                makeToken(PLUS, "+");
                break;
            case '-':
                makeToken(MINUS, "-");
                break;
            case '*':
                makeToken(ASTERISK, "*");
                break;
            case '/':
                makeToken(SLASH, "/");
                break;
            case '\'':
                makeToken(BACKSLASH, "\"");
                break;
            case '!':
                if(match('=')) {
                    makeToken(NOT_EQUAL, "!=");
                } else {
                    makeToken(MARK, "!");
                }
                break;
            case '=':
                if(match('=')) {
                    makeToken(EQUAL_EQUAL, "==");
                } else {
                    makeToken(EQUAL, "=");
                }
                break;
            default:
                if(isAlpha(c)) {
                    identifier();
                } else if(isDigit(c)) {
                    number();
                }
        }
    }



    private void resetInternalState(XuFile xuFile) {
        this.start = 0;
        this.current = 0;
        this.line = 1;
        this.column = 1;
        this.source = xuFile.getSource();
        this.tokenList = new ArrayList<>();
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

    private boolean match(char c) {
        if(isAtEnd()) return false;
        if(peek() != c) return false;
        advance();
        return true;
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
        tokenList.add(new Token(tokenType, line, column, lexeme, literal));
    }

    private void makeToken(TokenType tokenType, Object literal) {
        String lexeme = source.substring(start, current);
        tokenList.add(new Token(tokenType, line, column, lexeme, literal));
    }

    private void makeToken(TokenType tokenType) {
        String lexeme = source.substring(start, current);
        tokenList.add(new Token(tokenType, line, column, lexeme, null));
    }

    private void number() {
        while (isDigit(peek())) advance();

        String floatValue;
        if (peek() == '.') {
            do {
                advance();
            } while (isDigit(peek()));
            floatValue = source.substring(start, current);
            makeToken(FLOAT, floatValue);
        } else {
            String intValue = source.substring(start, current);
            makeToken(INT, intValue);
        }
    }

    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        String str = source.substring(start, current);
        TokenType type = keywords.getOrDefault(str, IDENTIFIER);
        makeToken(type, str);
    }

    private void string() {
        while(peek() != '"') {
            advance();
        }
        advance();

        String str = source.substring(start+1, current - 1);
        makeToken(STRING, str);
    }
}
