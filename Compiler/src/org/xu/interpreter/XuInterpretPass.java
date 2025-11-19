package org.xu.interpreter;

import org.xu.exception.XuException;
import org.xu.parser.XuParsedData;
import org.xu.parser.expression.*;
import org.xu.parser.statement.*;
import org.xu.pass.XuCompilationPass;
import org.xu.processor.XuExpressionNodeProcessor;
import org.xu.processor.XuStatementNodeProcessor;
import org.xu.scanner.Token;
import org.xu.type.XuValue;
import org.xu.type.XuValueType;

public class XuInterpretPass extends XuCompilationPass<XuParsedData, XuInterpretResult> implements XuExpressionNodeProcessor<XuValue>,
        XuStatementNodeProcessor<Void> {

    private XuEnvironment currentEnvironment = new XuEnvironment();
    private XuEnvironment previousEnvironment = null;
    private int depth = 0;

    @Override
    public Class<XuParsedData> getInputType() {
        return XuParsedData.class;
    }

    @Override
    public Class<XuInterpretResult> getOutputType() {
        return XuInterpretResult.class;
    }

    @Override
    public String getDebugName() {
        return "Interpreter Pass";
    }

    @Override
    protected XuInterpretResult pass(XuParsedData input) {
        long start = System.currentTimeMillis();

        for (XuStatementNode statement : input.getStatements()) {
            execute(statement);
        }

        long end = System.currentTimeMillis();
        long delta = end - start;
        System.out.println(delta / 1000f + " seconds.");

        return new XuInterpretResult(0);
    }

    private void execute(XuStatementNode statement) {
        statement.acceptProcessor(this);
    }


    private XuValue evaluate(XuExpressionNode expressionNode) {
        return expressionNode.acceptProcessor(this);
    }

    @Override
    public XuValue processLiteralExpression(XuLiteralExpression expression) {
        return expression.literal.literal();
    }

    @Override
    public XuValue processBinaryExpression(XuBinaryExpression expression) {
        XuValue left = evaluate(expression.left);
        XuValue right = evaluate(expression.right);

        switch (expression.operator.type()) {
            case PLUS:
                if (checkFloatOperands(left, right)) {
                    return new XuValue((float) left.value + (float) right.value, XuValueType.FLOAT);
                } else {
                    return new XuValue((int) left.value + (int) right.value, XuValueType.INT);
                }

            case MINUS:
                if (checkFloatOperands(left, right)) {
                    return new XuValue((float) left.value - (float) right.value, XuValueType.FLOAT);
                } else {
                    return new XuValue((int) left.value - (int) right.value, XuValueType.INT);
                }

            case STAR:
                if (checkFloatOperands(left, right)) {
                    return new XuValue((float) left.value * (float) right.value, XuValueType.FLOAT);
                } else {
                    return new XuValue((int) left.value * (int) right.value, XuValueType.INT);
                }

            case SLASH:
                if (checkFloatOperands(left, right)) {
                    return new XuValue((float) left.value * (float) right.value, XuValueType.FLOAT);
                } else {
                    return new XuValue((int) left.value / (int) right.value, XuValueType.INT);
                }

            default:
                throw new XuException("Operands must be numbers.");
        }
    }

    private boolean checkFloatOperands(XuValue left, XuValue right) {
        return left.type == XuValueType.FLOAT && right.type == XuValueType.INT ||
                left.type == XuValueType.INT && right.type == XuValueType.FLOAT ||
                left.type == XuValueType.FLOAT && right.type == XuValueType.FLOAT;
    }

    @Override
    public XuValue processUnaryExpression(XuUnaryExpression expression) {
        XuValue operand = evaluate(expression.expression);

        switch (expression.operator.type()) {
            case MINUS:
                if (checkFloatOperand(operand)) {
                    return new XuValue(-(float) operand.value, XuValueType.FLOAT);
                } else if (checkIntOperand(operand)) {
                    return new XuValue(-(int) operand.value, XuValueType.INT);
                } else {
                    throw new XuException("Operands must be number.");
                }

            case MARK:
                if (checkBooleanOperand(operand)) {
                    return new XuValue(!((boolean) operand.value), XuValueType.BOOL);
                } else {
                    throw new XuException("Operand must be boolean.");
                }

            default:
                throw new XuException("Operands must be number or boolean.");
        }
    }

    @Override
    public XuValue processLogicalExpression(XuLogicalExpression expression) {
        XuValue left = evaluate(expression.left);
        XuValue right = evaluate(expression.right);

        switch (expression.operator.type()) {
            case GREATER:
                if (checkNumberOperands(left, right)) {
                    //noinspection unchecked
                    return new XuValue(((Comparable<Number>) left.value).compareTo((Number) right.value) > 0, XuValueType.BOOL);
                }
            case GREATER_EQUAL:
                if (checkNumberOperands(left, right)) {
                    //noinspection unchecked
                    return new XuValue(((Comparable<Number>) left.value).compareTo((Number) right.value) >= 0, XuValueType.BOOL);
                }
            case LESS:
                if (checkNumberOperands(left, right)) {
                    //noinspection unchecked
                    return new XuValue(((Comparable<Number>) left.value).compareTo((Number) right.value) < 0, XuValueType.BOOL);
                }
            case LESS_EQUAL:
                if (checkNumberOperands(left, right)) {
                    //noinspection unchecked
                    return new XuValue(((Comparable<Number>) left.value).compareTo((Number) right.value) <= 0, XuValueType.BOOL);
                }


            case EQUAL_EQUAL:
                return new XuValue((left.equals(right)), XuValueType.BOOL);

            case MARK_EQUAL:
                return new XuValue(!(left.equals(right)), XuValueType.BOOL);

            case AND:
                return new XuValue((boolean) left.value && (boolean) right.value, XuValueType.BOOL);

            case OR:
                return new XuValue((boolean) left.value || (boolean) right.value, XuValueType.BOOL);

            default:
                throw new XuException("Invalid logical expression.");
        }
    }

    private boolean checkNumberOperands(XuValue left, XuValue right) {
        return checkFloatOperands(left, right) || checkIntOperands(left, right);
    }

    private boolean checkIntOperands(XuValue left, XuValue right) {
        return left.type == XuValueType.INT && right.type == XuValueType.INT;
    }

    @Override
    public XuValue processGroupExpression(XuGroupExpression expression) {
        return evaluate(expression.expression);
    }

    @Override
    public XuValue processVariableGetExpression(XuVariableGetExpression expression) {
        return lookupVariable(expression.name);
    }

    @Override
    public XuValue processAssignmentExpression(XuAssignmentExpression expression) {
        XuValue value = evaluate(expression.value);
        currentEnvironment.set(expression.name, value);
        return value;
    }

    private XuValue lookupVariable(Token name) {
        return currentEnvironment.get(name);
    }


    private boolean checkIntOperand(XuValue operand) {
        return operand.type == XuValueType.INT;
    }

    private boolean checkBooleanOperand(XuValue operand) {
        return operand.type == XuValueType.BOOL;
    }

    private boolean checkFloatOperand(XuValue operand) {
        return operand.type == XuValueType.FLOAT;
    }

    @Override
    public Void processIfStatement(XuIfStatement statement) {
        XuValue condition = evaluate(statement.condition);

        if (((boolean) condition.value)) {
            execute(statement.thenStatement);
        } else if (statement.elseStatement != null) {
            execute(statement.elseStatement);
        }

        return null;
    }

    @Override
    public Void processExpressionStatement(XuExpressionStatement statement) {
        evaluate(statement.expression);
        return null;
    }

    @Override
    public Void processBodyStatement(XuBodyStatement statement) {

        beginScope();

        for (XuStatementNode statementNode : statement.statements) {
            execute(statementNode);
        }

        endScope();

        return null;
    }

    @Override
    public Void processPrintStatement(PrintStatement statement) {
        XuValue value = evaluate(statement.value);
        System.out.println(stringify(value));

        return null;
    }

    @Override
    public Void processVariableDeclaration(VariableDeclarationStatement statement) {
        XuValue value = evaluate(statement.value);
        Token name = statement.name;

        defineVariable(name, value);

        return null;
    }

    @Override
    public Void processWhileStatement(XuWhileStatement statement) {

        while (((boolean) evaluate(statement.condition).value)) {
            execute(statement.body);
        }

        return null;
    }

    private String stringify(XuValue value) {
        if (value.type == XuValueType.NULL) {
            return "null";
        } else {
            return value.toString();
        }
    }

    private void defineVariable(Token name, XuValue value) {
        currentEnvironment.define(name, value);
    }

    private void beginScope() {
        previousEnvironment = currentEnvironment;
        currentEnvironment = new XuEnvironment(previousEnvironment);
        depth++;
    }

    private void endScope() {
        currentEnvironment = previousEnvironment;
        previousEnvironment = currentEnvironment.enclosing;
        depth--;
    }
}
