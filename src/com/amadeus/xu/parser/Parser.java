package com.amadeus.xu.parser;

import com.amadeus.xu.lexer.Token;

import static com.amadeus.xu.lexer.TokenType.*;

import com.amadeus.xu.lexer.TokenType;
import com.amadeus.xu.parser.expression.*;
import com.amadeus.xu.parser.statment.BlockStatement;
import com.amadeus.xu.parser.statment.ExpressionStatement;
import com.amadeus.xu.parser.statment.IfStatement;
import com.amadeus.xu.parser.statment.StatementNode;
import com.amadeus.xu.parser.statment.declaration.VariableDeclarationStatement;
import com.amadeus.xu.parser.statment.WhileStatement;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private List<Token> tokens;
    private List<StatementNode> statements;
    private int current;

    public Parser() {

    }

    public List<StatementNode> parseTokens(List<Token> tokens) {
        this.tokens = tokens;
        this.statements = new ArrayList<>();

        while (!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;
    }

    private ExpressionNode primary() {
        if (match(INT, FLOAT, STRING, TRUE, FALSE)) {
            return new LiteralExpression(previous());
        }
        if (match(IDENTIFIER)) {
            return new VariableGetExpression(previous());
        }
        if (match(LEFT_PARENTHESIS)) {
            return group();
        }
        error("Invalid Expression " + peek().lexeme + ". ");
        return null;
    }


    private StatementNode statement() {
        if (match(IF)) return ifStatement();
        if(match(WHILE)) return whileStatement();
        //if(match(FOR)) return forStatement();
        if (match(LEFT_BRACES)) return blockStatement();
        //if(match(PRINT)) return printStatement();

        return expressionStatement();
    }

    private StatementNode whileStatement() {
        consume(LEFT_PARENTHESIS, "cade o parenteses de abertura do while poxa?");
        ExpressionNode logicalExpr = comparison();
        consume(RIGHT_PARENTHESIS, "cade o parenteses de fechamento do while poxa?");
        StatementNode block = statement();
        return new WhileStatement(logicalExpr, block);
    }

    private StatementNode blockStatement() {
        List<StatementNode> statements = new ArrayList<>();

        while (!check(RIGHT_BRACES)) {
            statements.add(declaration());
        }

        consume(RIGHT_BRACES, "Expect '}' after body.");

        return new BlockStatement(statements);
    }


    private StatementNode declaration() {

        if (match(IDENTIFIER, AUTO, CONST)) return variableDeclaration();

        return statement();
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
                ExpressionNode expr = new VariableGetExpression(type);

                if(match(IDENTIFIER)) {
                    name = previous();
                    while (match(OPENBRACKET)) {
                        Token size = consume(INT, "Expect array size after '['.");
                        int sizeInt = (int) size.literal;
                        consume(CLOSEBRACKET, "Expect ']' after array size.");

                        expr = new ArrayTypeExpression(expr, sizeInt);
                    }
                    if(match(EQUAL)) {
                        equal = previous();
                        value = expression();
                        return new VariableDeclarationStatement(constant, expr, name, equal, value);
                    } else {
                        return new VariableDeclarationStatement(constant, expr, name, equal, value);
                    }
                } else {
                    backtrack();
                    return expressionStatement();
                }
            } else {
                type = previous();
                consume(IDENTIFIER, "cade o nome do auto poxa?");
                name = previous();
                consume(EQUAL, "cade o igual poxa?");
                equal = previous();
                value = expression();
                return new VariableDeclarationStatement(constant, new VariableGetExpression(type), name, equal, value);
            }
    }

//    private StatementNode variableDeclaration() {
//        Token type = previous();
//        if (type.type != CONST) {
//            //const
//            if (type.type != AUTO) {
//                //const - auto
//                if (match(IDENTIFIER)) {
//                    consume(IDENTIFIER, "xxxx");
//                    Token name = previous();
//                        if (match(EQUAL)) {
//                            Token equal = previous();
//                            ExpressionNode value = expression();
//                            return new VariableDeclarationStatement(null, type, name, equal, value);
//                        } else {
//                            return new VariableDeclarationStatement(null, type, name, null, null);
//                        }
//
//                }
//            } else {
//
//                //AUTO CONST
//
//                Token constant = previous();
//                if (match(IDENTIFIER)) {
//                    Token typee = previous();
//                    if (match(IDENTIFIER)) {
//                        Token namee = previous();
//                        if (match(EQUAL)) {
//                            Token equal = previous();
//                            ExpressionNode value = expression();
//                            return new VariableDeclarationStatement(type, typee, namee, equal, value);
//                        } else {
//                            return new VariableDeclarationStatement(constant, typee, namee, null, null);
//                        }
//                    }
//                }
//            }
//        } else {
//
//            //nop const
//
//            consume(IDENTIFIER, "Expect variable name after auto");
//            Token name = previous();
//            consume(EQUAL, "xxxx");
//            Token equal = previous();
//            ExpressionNode value = expression();
//            return new VariableDeclarationStatement(null, type, name, equal, value, dimensions);
//        }
//        backtrack();
//        return expressionStatement();
//    }

    private StatementNode expressionStatement() {
        ExpressionNode expression = expression();
        //consume(SEMICOLON, "Expect ';' after expression.");
        return new ExpressionStatement(expression);
    }

    private StatementNode ifStatement() {
        consume(LEFT_PARENTHESIS, "Expect '(' after 'if'.");
        ExpressionNode condition = expression();
        consume(RIGHT_PARENTHESIS, "Expect ')' after 'if'.");
        StatementNode thenBranch = statement();
        StatementNode elseBranch = null;

        if (match(ELSE)) {
            elseBranch = statement();
        }
        return new IfStatement(condition, thenBranch, elseBranch);
    }

    private ExpressionNode expression() {
        return assignment();
    }

    private ExpressionNode unary() {
        if (match(MINUS, MARK)) {
            Token operator = previous();
            ExpressionNode expression = expression();
            return new UnaryExpression(operator, expression);
        }
        return primary();
    }

    private ExpressionNode assignment() {
        ExpressionNode leftExpression = ternary();
        if (match(EQUAL)) {
            Token equal = previous();
            ExpressionNode value = assignment();

            if (leftExpression instanceof VariableGetExpression) {
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
        while (match(OR)) {
            Token operator = previous();
            ExpressionNode rightExpression = and();
            leftExpression = new LogicalExpression(leftExpression, operator, rightExpression);
        }
        return leftExpression;
    }

    private ExpressionNode and() {
        ExpressionNode leftExpression = equality();
        while (match(AND)) {
            Token operator = previous();
            ExpressionNode rightExpression = equality();
            leftExpression = new LogicalExpression(leftExpression, operator, rightExpression);
        }
        return leftExpression;
    }

    private ExpressionNode equality() {
        ExpressionNode leftExpression = comparison();

        while (match(EQUAL_EQUAL, NOT_EQUAL)) {
            Token operator = previous();
            ExpressionNode rightExpression = comparison();
            leftExpression = new LogicalExpression(leftExpression, operator, rightExpression);
        }

        return leftExpression;
    }

    private ExpressionNode comparison() {
        ExpressionNode leftExpression = term();
        while (match(BIGGER, BIGGER_EQUAL, LESS, LESS_EQUAL)) {
            Token operator = previous();
            ExpressionNode rightExpression = term();
            leftExpression = new LogicalExpression(leftExpression, operator, rightExpression);
        }
        return leftExpression;
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

    private Token consume(TokenType type, String message) {
        if (!match(type)) {
            error(message);
        }

        return previous();
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

    private void backtrack() {
        current--;
    }

}
