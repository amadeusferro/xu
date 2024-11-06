package com.amadeus.xu.parser;

import com.amadeus.xu.lexer.Token;
import static com.amadeus.xu.lexer.TokenType.*;
import  com.amadeus.xu.lexer.TokenType;
import com.amadeus.xu.parser.expression.ExpressionNode;
import com.amadeus.xu.parser.expression.GroupExpression;
import com.amadeus.xu.parser.expression.LiteralExpression;
import com.amadeus.xu.parser.expression.UnaryExpression;

import javax.swing.text.html.parser.AttributeList;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    private List<Token> tokens;
    private List<ExpressionNode> expressions;
    private int current;

    public List<ExpressionNode> parseTokens(List<Token> tokens) {
        this.tokens = tokens;
        this.expressions = new ArrayList<>();

        while(!isAtEnd()) {
            expressions.add(expression());
        }

        return expressions;
    }

    private ExpressionNode expression() {
        return unary();
    }

    private ExpressionNode unary() {


        if (match(MINUS, MARK)) {
            Token operator = previous();
            ExpressionNode expression = expression();
            return new UnaryExpression(operator, expression);
        }
        return primary();
    }

    private ExpressionNode primary() {
        if (match(INT, FLOAT)) {
            return new LiteralExpression(previous());
        }
        if (match(LEFT_PARENTHESIS)) {
            return group();
        }

        error("Invalid Expression " + peek().lexeme + ". ");
        return null;
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
        throw new RuntimeException(message + "At line " + peek().line);
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
