package org.xu.optimizer;

import org.xu.compiler.XuInstructionCode;
import org.xu.parser.XuParsedData;
import org.xu.parser.expression.*;
import org.xu.parser.statement.*;
import org.xu.pass.XuCompilationPass;
import org.xu.scanner.Token;
import org.xu.scanner.TokenType;
import org.xu.type.XuValue;
import org.xu.type.XuValueType;

import java.util.ArrayList;
import java.util.List;

public class XuOptimizationPass extends XuCompilationPass<XuParsedData, XuParsedData> {

    @Override
    public Class<XuParsedData> getInputType() {
        return XuParsedData.class;
    }

    @Override
    public Class<XuParsedData> getOutputType() {
        return XuParsedData.class;
    }

    @Override
    public String getDebugName() {
        return "Optimization Pass";
    }

    @Override
    protected XuParsedData pass(XuParsedData input) {
        List<XuStatementNode> statements = new ArrayList<>();

        for (XuStatementNode node : input.getStatements()) {
            statements.add(optimize(node));
        }

        return new XuParsedData(statements);
    }

    private XuStatementNode optimize(XuStatementNode node) {
        if (node instanceof XuExpressionStatement) {
            XuExpressionNode expression = ((XuExpressionStatement) node).expression;
            return new XuExpressionStatement(optimize(expression));
        }

        if (node instanceof XuIfStatement ifStatement) {
            XuExpressionNode condition = ifStatement.condition;

            if (condition instanceof XuLogicalExpression logicalCondition) {
                XuExpressionNode left = optimize(logicalCondition.left);
                XuExpressionNode right = optimize(logicalCondition.right);
                Token operator = logicalCondition.operator;

                if (operator.type() == TokenType.AND) {
                    if (left instanceof XuLiteralExpression leftLiteral) {
                        if (leftLiteral.literal.literal().type == XuValueType.BOOL &&
                                (boolean) leftLiteral.literal.literal().value) {
                            return new XuIfStatement(right, ifStatement.thenStatement, ifStatement.elseStatement);
                        } else if (leftLiteral.literal.literal().type == XuValueType.BOOL &&
                                !(boolean) leftLiteral.literal.literal().value) {
                            return ifStatement.elseStatement;
                        }
                    }
                } else if (operator.type() == TokenType.OR) {
                    if (left instanceof XuLiteralExpression leftLiteral) {
                        if (leftLiteral.literal.literal().type == XuValueType.BOOL &&
                                (boolean) leftLiteral.literal.literal().value) {
                            return ifStatement.thenStatement;  // Retorna apenas o bloco then
                        } else if (right instanceof XuLiteralExpression rightLiteral) {
                            if (rightLiteral.literal.literal().type == XuValueType.BOOL &&
                                    (boolean) rightLiteral.literal.literal().value) {
                                return ifStatement.thenStatement;  // Retorna apenas o bloco then
                            }
                        }
                    }
                }

                condition = new XuLogicalExpression(left, operator, right);
            }

            if (condition instanceof XuLiteralExpression conditionLiteral) {
                if (conditionLiteral.literal.literal().type == XuValueType.BOOL) {
                    if ((boolean) conditionLiteral.literal.literal().value) {
                        return ifStatement.thenStatement;
                    } else {
                        return ifStatement.elseStatement;
                    }
                }
            }

            return new XuIfStatement(condition, ifStatement.thenStatement, ifStatement.elseStatement);
        }

        if (node instanceof VariableDeclarationStatement statement) {
            XuExpressionNode value = optimize(statement.value);
            return new VariableDeclarationStatement(statement.type, statement.name, value);
        }

        return node;
    }


    private XuExpressionNode optimize(XuExpressionNode expression) {
        if (expression instanceof XuBinaryExpression binary) {
            return tryResolveBinary(binary);
        }
        if (expression instanceof XuLogicalExpression logical) {
            return tryResolveLogical(logical);
        }
        if (expression instanceof XuGroupExpression group) {
            return tryResolveGroup(group);
        }

        return expression;
    }

    private XuExpressionNode tryResolveGroup(XuGroupExpression group) {
        XuExpressionNode optimizedExpression = optimize(group.expression);

        if (optimizedExpression instanceof XuLiteralExpression) {
            return optimizedExpression;
        }

        return group;
    }

    private XuExpressionNode tryResolveBinary(XuBinaryExpression binary) {
        XuExpressionNode left = optimize(binary.left);
        XuExpressionNode right = optimize(binary.right);

        if (left instanceof XuLiteralExpression leftLiteral && right instanceof XuLiteralExpression rightLiteral) {
            XuValue a = leftLiteral.literal.literal();
            XuValue b = rightLiteral.literal.literal();

            XuValue resultValue;

            switch (binary.operator.type()) {
                case TokenType.PLUS: {
                    resultValue = new XuValue((int) a.value + (int) b.value, XuValueType.INT);
                    break;
                }

                case TokenType.MINUS: {
                    resultValue = new XuValue((int) a.value - (int) b.value, XuValueType.INT);
                    break;
                }

                case TokenType.STAR: {
                    resultValue = new XuValue((int) a.value * (int) b.value, XuValueType.INT);
                    break;
                }
                case TokenType.SLASH: {
                    resultValue = new XuValue((int) a.value / (int) b.value, XuValueType.INT);
                    break;
                }

                default:
                    return binary;
            }

            switch (resultValue.type) {
                case XuValueType.INT:
                    return new XuLiteralExpression(new Token(TokenType.INT, null, resultValue, 0));
                case XuValueType.FLOAT:
                    return new XuLiteralExpression(new Token(TokenType.FLOAT, null, resultValue, 0));
                case XuValueType.CHAR:
                    return new XuLiteralExpression(new Token(TokenType.CHAR, null, resultValue, 0));
                case XuValueType.STRING:
                    return new XuLiteralExpression(new Token(TokenType.STRING, null, resultValue, 0));
            }
        }

        return new XuBinaryExpression(left, binary.operator, right);
    }

    private XuExpressionNode tryResolveLogical(XuLogicalExpression logical) {
        XuExpressionNode left = optimize(logical.left);
        XuExpressionNode right = optimize(logical.right);

        if (left instanceof XuLiteralExpression leftLiteral && right instanceof XuLiteralExpression rightLiteral) {
            boolean result;

            switch (logical.operator.type()) {
                case TokenType.AND: {
                    boolean a = (Boolean) leftLiteral.literal.literal().value;
                    boolean b = (Boolean) rightLiteral.literal.literal().value;
                    result = a && b;
                    break;
                }
                case TokenType.OR: {
                    boolean a = (Boolean) leftLiteral.literal.literal().value;
                    boolean b = (Boolean) rightLiteral.literal.literal().value;
                    result = a || b;
                    break;
                }
                case TokenType.GREATER: {
                    int a = (Integer) leftLiteral.literal.literal().value;
                    int b = (Integer) rightLiteral.literal.literal().value;
                    result = a > b;
                    break;
                }
                case TokenType.GREATER_EQUAL: {
                    int a = (Integer) leftLiteral.literal.literal().value;
                    int b = (Integer) rightLiteral.literal.literal().value;
                    result = a >= b;
                    break;
                }
                case TokenType.LESS: {
                    int a = (Integer) leftLiteral.literal.literal().value;
                    int b = (Integer) rightLiteral.literal.literal().value;
                    result = a < b;
                    break;
                }
                case TokenType.LESS_EQUAL: {
                    int a = (Integer) leftLiteral.literal.literal().value;
                    int b = (Integer) rightLiteral.literal.literal().value;
                    result = a <= b;
                    break;
                }
                case TokenType.EQUAL_EQUAL: {
                    Object a = leftLiteral.literal.literal().value;
                    Object b = rightLiteral.literal.literal().value;
                    result = a.equals(b);
                    break;
                }
                case TokenType.MARK_EQUAL: {
                    Object a = leftLiteral.literal.literal().value;
                    Object b = rightLiteral.literal.literal().value;
                    result = !a.equals(b);
                    break;
                }
                default:
                    return logical;
            }

            return new XuLiteralExpression(new Token(result ? TokenType.TRUE : TokenType.FALSE, null, new XuValue(result, XuValueType.BOOL), 0));
        }

        return new XuLogicalExpression(left, logical.operator, right);
    }
}
