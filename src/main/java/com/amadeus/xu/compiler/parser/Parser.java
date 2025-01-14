package com.amadeus.xu.compiler.parser;

import com.amadeus.xu.compiler.lexer.Token;
import com.amadeus.xu.compiler.lexer.TokenType;
import com.amadeus.xu.compiler.parser.expression.*;
import com.amadeus.xu.compiler.parser.statement.*;
import com.amadeus.xu.compiler.parser.statement.declaration.*;

import java.util.ArrayList;
import java.util.List;

import static com.amadeus.xu.compiler.lexer.TokenType.*;

public class Parser {

    private List<Token> tokens;
    private List<StatementNode> statements;
    private int current;

    public Parser() {}

    public List<StatementNode> parseTokens(List<Token> tokens) {
        this.tokens = tokens;
        this.statements = new ArrayList<>();

        while(!isAtEnd()) {
            statements.add(declaration());
        }
        return statements;
    }

    private StatementNode declaration() {
        if(match(AUTO, CONST, IDENTIFIER)) return variableDeclaration();
        return statement();
    }

    private StatementNode statement() {
        if(match(IF)) return ifStatement();
        if(match(WHILE)) return whileStatement();
        if(match(LEFT_BRACES)) return blockStatement();
        //if(match(FOR)) return forStatement();
        //if(match(PRINT)) return printStatement();

        return expressionStatement();
    }

    private StatementNode expressionStatement() {
        ExpressionNode expression = expression();
        //consume(SEMICOLON, "Expect ';' after expression.");
        return new ExpressionStatement(expression);
    }

    private ExpressionNode expression() {
        return assignment();
    }

    private ExpressionNode assignment() {
        ExpressionNode leftExpression = ternary();
        if(match(EQUAL)) {
            Token equal = previous();
            ExpressionNode value = assignment();
            if(leftExpression instanceof VariableGetExpression) {
                Token name = ((VariableGetExpression) leftExpression).name;
                return new AssignmentExpression(name, equal, value);
            }
            error("Invalid assignment target.");
        }
        return leftExpression;
    }

    private ExpressionNode ternary() {
        if (match(IF)) {
            consume(LEFT_PARENTHESIS, "Expect '(' after 'if'.");
            ExpressionNode condition = expression();
            consume(RIGHT_PARENTHESIS, "Expect ')' after 'if'.");
            ExpressionNode thenExpression = expression();
            consume(ELSE, "Expect 'else' keyword after then branch in ternary expression.");
            ExpressionNode elseExpression = expression();
            return new TernaryExpression(condition, thenExpression, elseExpression);

        }

        return or();
    }

    private ExpressionNode or() {
        ExpressionNode leftExpression = and();
        while(match(OR)) {
            Token or = previous();
            ExpressionNode rightExpression = and();
            leftExpression = new LogicalExpression(leftExpression, or, rightExpression);
        }
        return leftExpression;
    }

    private ExpressionNode and() {
        ExpressionNode leftExpression = equality();
        while(match(AND)) {
            Token and = previous();
            ExpressionNode rightExpression = equality();
            leftExpression = new LogicalExpression(leftExpression, and, rightExpression);
        }
        return leftExpression;
    }

    private ExpressionNode equality() {
        ExpressionNode leftExpression = comparison();
        while(match(EQUAL_EQUAL, NOT_EQUAL)) {
            Token equal = previous();
            ExpressionNode rightExpression = comparison();
            leftExpression = new LogicalExpression(leftExpression, equal, rightExpression);
        }
        return leftExpression;
    }

    private ExpressionNode comparison() {
        ExpressionNode leftExpression = binary();
        while(match(BIGGER, BIGGER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            ExpressionNode rightExpression = binary();
            leftExpression = new LogicalExpression(leftExpression, operator, rightExpression);
        }
        return leftExpression;
    }

    private ExpressionNode binary() {
        ExpressionNode leftExpression = unary();
        while(match(PLUS, MINUS, ASTERISK, SLASH)) {
            Token operator = previous();
            ExpressionNode rightExpression = unary();
            leftExpression = new BinaryExpression(leftExpression, operator, rightExpression);
        }
        return leftExpression;
    }

    private ExpressionNode unary() {
        if(match(MINUS, MARK)) {
            Token operator = previous();
            ExpressionNode expression = expression();
            return new UnaryExpression(operator, expression);
        }
        return primary();
    }

    private ExpressionNode primary() {
        if(match(INT, FLOAT, STRING, TRUE, FALSE)) return new LiteralExpression(previous());
        if(match(IDENTIFIER)) return new VariableGetExpression(previous());
        if(match(LEFT_PARENTHESIS)) return group();
        error("Invalid Expression " + peek().lexeme + ". ");
        return null;
    }

    private ExpressionNode group() {
        ExpressionNode expression = expression();
        consume(RIGHT_PARENTHESIS, "Expect ')' after expression.");
        return new GroupExpression(expression);
    }

    private StatementNode variableDeclaration() {
        Token firstArgument = previous();
        Token constant = null;
        Token type = null;
        Token name = null;
        Token equal = null;
        ExpressionNode value = null;

        StatementNode declaration = null;

        if(firstArgument.type == CONST) {
            constant = previous();
            advance();
            firstArgument = previous();
        }
        if(firstArgument.type != AUTO) {
            type = previous();
            ExpressionNode expression = new VariableGetExpression(type);

            if(match(IDENTIFIER)) {
                name = previous();
                while (match(OPEN_BRACKET)) {
                    Token size = consume(INT, "Expect array size after '['.");
                    int sizeInt = Integer.parseInt(size.literal.toString());
                    consume(CLOSE_BRACKET, "Expect ']' after array size.");

                    expression = new ArrayTypeExpression(expression, sizeInt);
                }
                if(match(EQUAL)) {
                    equal = previous();
                    value = expression();
                    return new VariableDeclarationStatement(constant, expression, name, equal, value);
                } else {
                    return new VariableDeclarationStatement(constant, expression, name, equal, value);
                }
            } else {
                backtrack();
                return expressionStatement();
            }
        } else {
            type = previous();
            consume(IDENTIFIER, "Expect 'identifier' after 'auto'.");
            name = previous();
            consume(EQUAL, "Expect 'equal' after 'identifier'.");
            equal = previous();
            value = expression();
            return new VariableDeclarationStatement(constant, new VariableGetExpression(type), name, equal, value);
        }
    }

    private StatementNode ifStatement() {
        consume(LEFT_PARENTHESIS, "Expect '(' after 'if'.");
        ExpressionNode condition = expression();
        consume(RIGHT_PARENTHESIS, "Expect ')' after 'condition'.");
        StatementNode thenBranch = statement();
        StatementNode elseBranch = null;

        if (match(ELSE)) {
            elseBranch = statement();
        }
        return new IfStatement(condition, thenBranch, elseBranch);
    }

    private StatementNode whileStatement() {
        consume(LEFT_PARENTHESIS, "Expect '(' after 'while'.");
        ExpressionNode condition = comparison();
        consume(RIGHT_PARENTHESIS, "Expect ')' after 'condition'.");
        StatementNode block = statement();
        return new WhileStatement(condition, block);
    }

    private StatementNode blockStatement() {
        List<StatementNode> statements = new ArrayList<>();
        while(!check(RIGHT_BRACES)) statements.add(declaration());
        consume(RIGHT_BRACES, "Expect '}' after body.");
        return new BlockStatement(statements);
    }

    // Utility

    private void advance() {
        current++;
    }

    private void backtrack() {
        current--;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private void error(String message) {
        throw new RuntimeException(message + "at " + peek().line + ":" + peek().column);
    }

    private boolean check(TokenType... types) {
        for(TokenType tk : types) {
            if(peek().type == tk) return true;
        }
        return false;
    }

    private boolean match(TokenType... types) {
        if(check(types)) {
            advance();
            return true;
        }
        return false;
    }

    private Token consume(TokenType type, String message) {
        if(!match(type)) {
            error(message);
        }
        return previous();
    }
}
