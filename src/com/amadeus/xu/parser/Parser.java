package com.amadeus.xu.parser;

import com.amadeus.xu.lexer.Token;
import static com.amadeus.xu.lexer.TokenType.*;
import  com.amadeus.xu.lexer.TokenType;
import com.amadeus.xu.parser.expression.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private List<Token> tokens;
    private List<ExpressionNode> expressions;
    private int current;

    public Parser() {

    }

    public List<ExpressionNode> parseTokens(List<Token> tokens) {
        this.tokens = tokens;
        this.expressions = new ArrayList<>();

        while(!isAtEnd()) {
            expressions.add(expression());
        }

        return expressions;
    }

    private ExpressionNode primary() {
        if (match(INT, FLOAT)) {
            return new LiteralExpression(previous());
        }
        if (match(LEFT_PARENTHESIS)) {
            return group();
        }
        if (match(STRING)) {
            return new LiteralExpression(previous());
        }
        if(match(TRUE, FALSE)) {
            return new LiteralExpression(previous());
        }
        if (match(IDENTIFIER)) {
            return new LiteralExpression(previous());
        }

        error("Invalid Expression " + peek().lexeme + ". ");
        return null;
    }

    private ExpressionNode expression() {
        return term();
    }

    private ExpressionNode unary() {
        if (match(MINUS, MARK)) {
            Token operator = previous();
            ExpressionNode expression = expression();
            return new UnaryExpression(operator, expression);
        }
        return primary();
    }

    private ExpressionNode term() {
        ExpressionNode leftExpression = factor();
        while (match(PLUS, MINUS)) {
            Token operator = previous();
            ExpressionNode rightExpression = factor();
            leftExpression = new TermExpression(leftExpression, operator, rightExpression);
        }
        return leftExpression;
    }

    private ExpressionNode factor() {
        ExpressionNode leftExpression = unary();
        while (match(ASTERISK, SLASH)) {
            Token operator = previous();
            ExpressionNode rightExpression = unary();
            leftExpression = new FactorExpression(leftExpression, operator, rightExpression);
        }
        return leftExpression;
    }


    private ExpressionNode group() {
        Token paren = previous();
        ExpressionNode expression = expression();
        consume(RIGHT_PARENTHESIS, "Expect ')' after expression.");
        return new GroupExpression(paren, expression);
    }

    private void consume(TokenType type, String message) {
        if (!match(type)) {
            error(message);
        }
    }

    private void error(String message) {
        throw new RuntimeException(message + "at line " + peek().line + ":" + peek().column);
    }

    private boolean check(TokenType... types) {
        for (TokenType tk : types) {
            if (peek().type == tk) return true;
        }
        return false;
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private Token peek() {
        return tokens.get(current);
    }

    private void advance() {
        current++;
    }

    private boolean match(TokenType... types) {
        if (check(types)) {
            advance();
            return true;
        }
        return false;
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

}
