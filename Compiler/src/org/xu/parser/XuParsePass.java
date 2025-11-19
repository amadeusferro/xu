package org.xu.parser;

import org.xu.exception.XuParseException;
import org.xu.parser.expression.*;
import org.xu.parser.statement.*;
import org.xu.pass.XuCompilationPass;
import org.xu.scanner.XuScannedData;
import org.xu.scanner.Token;
import org.xu.scanner.TokenType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.xu.scanner.TokenType.*;

public class XuParsePass extends XuCompilationPass<XuScannedData, XuParsedData> {


    private List<Token> tokens;
    private int current;

    @Override
    public Class<XuScannedData> getInputType() {
        return XuScannedData.class;
    }

    @Override
    public Class<XuParsedData> getOutputType() {
        return XuParsedData.class;
    }

    @Override
    public String getDebugName() {
        return "Parse Pass";
    }

    @Override
    protected XuParsedData pass(XuScannedData input) {
        return parseTokens(input);
    }

    private void resetInternalState(XuScannedData input) {
        tokens = input.getTokens();
        current = 0;
    }

    private XuParsedData parseTokens(XuScannedData input) {
        resetInternalState(input);

        List<XuStatementNode> statements = new ArrayList<>();

        while (!isAtEnd()) {
            XuStatementNode statementNode = declaration();
            statements.add(statementNode);
        }

        return new XuParsedData(statements);
    }

    private XuStatementNode declaration() {

        if (match(IDENTIFIER)) return variableDeclaration();

        return statement();
    }

    private XuStatementNode variableDeclaration() {
        if (check(IDENTIFIER)) {
            Token type = previous();
            consume(IDENTIFIER, "Expect variable name after type.");
            Token name = previous();
            consume(EQUAL, "Expect '=' after variable name, but got '" + peek().lexeme() + "'.");
            XuExpressionNode expression = expression();
            consume(SEMICOLON, "Expect ';' after initializer.");

            return new VariableDeclarationStatement(type, name, expression);
        }

        backTrack();
        return expressionStatement();
    }

    private void backTrack() {
        current--;
    }

    private XuStatementNode statement() {
        if (match(IF)) return ifStatement();
        if (match(WHILE)) return whileStatement();
        if (match(FOR)) return forStatement();
        if (match(LEFT_BRACE)) return bodyStatement();
        if (match(PRINT)) return printStatement();

        return expressionStatement();
    }

    private XuStatementNode forStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'for'.");

        XuStatementNode initializer = null;

        if (!match(SEMICOLON)) {
            if (match(IDENTIFIER)) {
                if (match(IDENTIFIER)) {
                    backTrack();
                    initializer = variableDeclaration();
                } else {
                    initializer = expressionStatement();
                }
            }
        }

        XuExpressionNode condition = null;

        if (!check(SEMICOLON)) {
            condition = expression();
        }

        consume(SEMICOLON, "Expect ')' after loop condition.");

        XuExpressionNode increment = null;

        if (!check(RIGHT_PAREN)) {
            increment = expression();
        }

        consume(RIGHT_PAREN, "Expect ')' after for clauses.");

        XuStatementNode body = statement();

        if (increment != null) {
            body = new XuBodyStatement(Arrays.asList(body, new XuExpressionStatement(increment)));
        }

        if (condition == null) {
            condition = new XuLiteralExpression(new Token(TRUE, "true", null, 0));
        }

        body = new XuWhileStatement(condition, body);

        if (initializer != null) {
            body = new XuBodyStatement(Arrays.asList(initializer, body));
        }

        return body;
    }

    private XuStatementNode whileStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'for'.");
        XuExpressionNode condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after condition.");

        XuStatementNode body = statement();

        return new XuWhileStatement(condition, body);
    }

    private XuStatementNode printStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'print'.");
        XuExpressionNode expression = expression();
        consume(RIGHT_PAREN, "Expect ')' after argument.");
        consume(SEMICOLON, "Expect ';' after call.");
        return new PrintStatement(expression);
    }

    private XuStatementNode bodyStatement() {
        List<XuStatementNode> statements = new ArrayList<>();

        while (!check(RIGHT_BRACE)) {
            statements.add(declaration());
        }

        consume(RIGHT_BRACE, "Expect '}' after body.");

        return new XuBodyStatement(statements);
    }

    private XuStatementNode expressionStatement() {
        XuExpressionNode expression = expression();
        consume(SEMICOLON, "Expect ';' after expression.");
        return new XuExpressionStatement(expression);
    }

    private XuStatementNode ifStatement() {
        consume(LEFT_PAREN, "Expect '(' after 'if'.");
        XuExpressionNode condition = expression();
        consume(RIGHT_PAREN, "Expect ')' after 'if' condition.");
        XuStatementNode thenBranch = statement();
        XuStatementNode elseBranch = null;

        if (match(ELSE)) {
            elseBranch = statement();
        }

        return new XuIfStatement(condition, thenBranch, elseBranch);
    }

    private XuExpressionNode expression() {
        return assignment();
    }

    private XuExpressionNode assignment() {
        XuExpressionNode expression = or();

        if (match(EQUAL)) {
            Token equals = previous();
            XuExpressionNode value = assignment();

            if (expression instanceof XuVariableGetExpression) {
                Token name = ((XuVariableGetExpression) expression).name;
                return new XuAssignmentExpression(name, equals, value);
            }

            error("Invalid assignment target.");
        }

        return expression;
    }

    private XuExpressionNode or() {
        XuExpressionNode expressionNode = and();

        while (match(OR)) {
            Token operator = previous();
            XuExpressionNode right = and();
            expressionNode = new XuLogicalExpression(expressionNode, operator, right);
        }

        return expressionNode;
    }

    private XuExpressionNode and() {
        XuExpressionNode expressionNode = equality();

        while (match(AND)) {
            Token operator = previous();
            XuExpressionNode right = equality();
            expressionNode = new XuLogicalExpression(expressionNode, operator, right);
        }

        return expressionNode;
    }

    private XuExpressionNode equality() {

        XuExpressionNode expressionNode = comparison();

        while (match(EQUAL_EQUAL, MARK_EQUAL)) {
            Token operator = previous();
            XuExpressionNode right = comparison();
            expressionNode = new XuLogicalExpression(expressionNode, operator, right);
        }

        return expressionNode;
    }

    private XuExpressionNode comparison() {

        XuExpressionNode expressionNode = term();

        if (match(GREATER, GREATER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            XuExpressionNode right = term();
            expressionNode = new XuLogicalExpression(expressionNode, operator, right);
        }

        return expressionNode;
    }

    private XuExpressionNode term() {
        XuExpressionNode expressionNode = factor();

        while (match(PLUS, MINUS)) {
            Token operator = previous();
            XuExpressionNode right = factor();
            expressionNode = new XuBinaryExpression(expressionNode, operator, right);
        }

        return expressionNode;
    }

    private XuExpressionNode factor() {
        XuExpressionNode expressionNode = unary();

        while (match(STAR, SLASH)) {
            Token operator = previous();
            XuExpressionNode right = factor();
            expressionNode = new XuBinaryExpression(expressionNode, operator, right);
        }

        return expressionNode;
    }

    private XuExpressionNode unary() {
        if (match(MINUS, MARK)) {
            Token operator = previous();
            XuExpressionNode expressionNode = unary();
            return new XuUnaryExpression(operator, expressionNode);
        }

        return literal();
    }

    private XuExpressionNode literal() {
        if (match(INT, FLOAT, TRUE, FALSE, STRING, CHAR)) {
            return new XuLiteralExpression(previous());
        }

        if (match(IDENTIFIER)) {
            return new XuVariableGetExpression(previous());
        }

        if (match(LEFT_PAREN)) {
            return group();
        }

        error("Invalid expression: " + peek().type());
        return null;
    }

    private XuExpressionNode group() {
        Token paren = previous();
        XuExpressionNode expressionNode = expression();
        consume(RIGHT_PAREN, "Expect ')' after group expression.");

        return new XuGroupExpression(paren, expressionNode);
    }

    private void consume(TokenType type, String message) {
        if (!match(type)) {
            error(message);
        }
    }

    private void error(String message) {
        throw new XuParseException(message);
    }

    private boolean isAtEnd() {
        return peek().type() == TokenType.EOF;
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private void advance() {
        current++;
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type() == type;
    }

    private Token peek() {
        return tokens.get(current);
    }
}
