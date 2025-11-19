package org.xu.compiler;

import org.xu.parser.statement.*;
import org.xu.processor.XuStatementNodeProcessor;
import org.xu.type.XuValue;
import org.xu.parser.XuParsedData;
import org.xu.parser.expression.*;
import org.xu.pass.XuCompilationPass;
import org.xu.processor.XuExpressionNodeProcessor;

import java.util.*;

public class XuCompilePass extends XuCompilationPass<XuParsedData, XuCompiledCode>
        implements XuExpressionNodeProcessor<List<Byte>>, XuStatementNodeProcessor<List<Byte>> {

    private final Map<XuValue, Byte> constantTable = new HashMap<>();
    private final Map<Byte, XuValue> indexToValue = new HashMap<>();
    private final List<String> stringPool = new ArrayList<>();
    private final Map<Byte, String> stringTable = new HashMap<>();
    private byte currentConstantIndex = 0;
    private int currentScope = 0;

    @Override
    public Class<XuParsedData> getInputType() {
        return XuParsedData.class;
    }

    @Override
    public Class<XuCompiledCode> getOutputType() {
        return XuCompiledCode.class;
    }

    @Override
    public String getDebugName() {
        return "Compile Pass";
    }

    @Override
    protected XuCompiledCode pass(XuParsedData input) {
        List<Byte> bytecode = new ArrayList<>();
        for (XuStatementNode statement : input.getStatements()) {
            bytecode.addAll(statement.acceptProcessor(this));
        }

        bytecode.add(XuInstructionCode.RETURN);

        return new XuCompiledCode(bytecode, bytecode, indexToValue, stringTable);
    }

    private List<Byte> generateBytecode(XuExpressionNode expression) {
        return expression.acceptProcessor(this);
    }

    private List<Byte> generateBytecode(XuStatementNode statement) {
        return statement.acceptProcessor(this);
    }

    @Override
    public List<Byte> processLiteralExpression(XuLiteralExpression expression) {
        List<Byte> result = new ArrayList<>();

        byte constantIndex = writeConstant(expression.literal.literal());

        emitByte(XuInstructionCode.LOAD_CONST, result);
        emitByte(constantIndex, result);

        return result;
    }

    @Override
    public List<Byte> processBinaryExpression(XuBinaryExpression expression) {
        List<Byte> leftBytecode = generateBytecode(expression.left);
        List<Byte> rightBytecode = generateBytecode(expression.right);
        List<Byte> result = new ArrayList<>();
        result.addAll(leftBytecode);
        result.addAll(rightBytecode);

        switch (expression.operator.type()) {
            case PLUS:
                emitByte(XuInstructionCode.ADD, result);
                break;

            case MINUS:
                emitByte(XuInstructionCode.SUB, result);
                break;

            case STAR:
                emitByte(XuInstructionCode.MUL, result);
                break;

            case SLASH:
                emitByte(XuInstructionCode.DIV, result);
                break;
        }

        return result;
    }

    @Override
    public List<Byte> processUnaryExpression(XuUnaryExpression expression) {
        List<Byte> expressionCode = generateBytecode(expression.expression);
        List<Byte> result = new ArrayList<>(expressionCode);

        switch (expression.operator.type()) {
            case MINUS:
                emitByte(XuInstructionCode.NEGATE, result);
                break;

            case MARK:
                emitByte(XuInstructionCode.INVERSE, result);
                break;
        }

        return result;
    }

    @Override
    public List<Byte> processLogicalExpression(XuLogicalExpression expression) {
        List<Byte> result = generateBytecode(expression.left);
        result.addAll(generateBytecode(expression.right));

        switch (expression.operator.type()) {
            case GREATER:
                emitByte(XuInstructionCode.GREATER, result);
                break;

            case GREATER_EQUAL:
                emitByte(XuInstructionCode.GREATER_EQUAL, result);
                break;

            case LESS:
                emitByte(XuInstructionCode.LESS, result);
                break;

            case LESS_EQUAL:
                emitByte(XuInstructionCode.LESS_EQUAL, result);
                break;

            case EQUAL_EQUAL:
                emitByte(XuInstructionCode.EQUAL_EQUAL, result);
                break;

            case MARK_EQUAL:
                emitByte(XuInstructionCode.MARK_EQUAL, result);
                break;

            case AND:
                emitByte(XuInstructionCode.AND, result);
                break;

            case OR:
                emitByte(XuInstructionCode.OR, result);
                break;
        }

        return result;
    }

    @Override
    public List<Byte> processGroupExpression(XuGroupExpression expression) {
        return generateBytecode(expression.expression);
    }

    @Override
    public List<Byte> processVariableGetExpression(XuVariableGetExpression expression) {
        List<Byte> result = new ArrayList<>();

        emitByte(XuInstructionCode.LOAD, result);
        emitByte((byte) stringPool.indexOf(expression.name.lexeme()), result);

        return result;
    }

    @Override
    public List<Byte> processAssignmentExpression(XuAssignmentExpression expression) {
        List<Byte> value = generateBytecode(expression.value);
        List<Byte> result = new ArrayList<>(value);
        emitByte(XuInstructionCode.STORE, result);
        emitByte((byte) stringPool.indexOf(expression.name.lexeme()), result);

        return result;
    }

    private byte writeConstant(XuValue constant) {
        if (constantTable.containsKey(constant)) {
            return constantTable.get(constant);
        }

        constantTable.put(constant, currentConstantIndex);
        indexToValue.put(currentConstantIndex, constant);
        return currentConstantIndex++;
    }

    private void emitByte(Byte instruction, List<Byte> code) {
        code.add(instruction);
    }

    @Override
    public List<Byte> processIfStatement(XuIfStatement statement) {

        List<Byte> result = new ArrayList<>(statement.condition.acceptProcessor(this));

        List<Byte> thenBranch = statement.thenStatement.acceptProcessor(this);
        List<Byte> elseBranch = new ArrayList<>();

        if (statement.elseStatement != null) {
            elseBranch = statement.elseStatement.acceptProcessor(this);
        }

        short ifInstructionCount = (short) (thenBranch.size() + 3);
        byte ifHighByte = (byte) (ifInstructionCount >> 8);
        byte ifLowByte = (byte) (ifInstructionCount & 0xFF);

        emitByte(XuInstructionCode.JUMP_IF_FALSE, result);
        emitByte(ifLowByte, result);
        emitByte(ifHighByte, result);

        beginScope();
        result.addAll(thenBranch);
        endScope();

        short elseInstructionCount = (short) (elseBranch.size());
        byte elseHighByte = (byte) (elseInstructionCount >> 8);
        byte elseLowByte = (byte) (elseInstructionCount & 0xFF);

        emitByte(XuInstructionCode.JUMP, result);
        emitByte(elseLowByte, result);
        emitByte(elseHighByte, result);

        beginScope();
        result.addAll(elseBranch);
        endScope();

        return result;
    }

    @Override
    public List<Byte> processExpressionStatement(XuExpressionStatement statement) {
        return statement.expression.acceptProcessor(this);
    }

    @Override
    public List<Byte> processBodyStatement(XuBodyStatement statement) {
        List<Byte> result = new ArrayList<>();

        for (XuStatementNode statementNode : statement.statements) {
            result.addAll(statementNode.acceptProcessor(this));
        }

        return result;
    }

    @Override
    public List<Byte> processPrintStatement(PrintStatement statement) {
        List<Byte> result = new ArrayList<>(generateBytecode(statement.value));
        emitByte(XuInstructionCode.PRINT, result);

        return result;
    }

    @Override
    public List<Byte> processVariableDeclaration(VariableDeclarationStatement statement) {

        // TODO Local variables when current scope greater than zero.

        List<Byte> value = generateBytecode(statement.value);
        byte index = emitString(statement.name.lexeme());
        List<Byte> result = new ArrayList<>(value);
        emitByte(XuInstructionCode.DEFINE, result);
        result.add(index);


        return result;
    }

    @Override
    public List<Byte> processWhileStatement(XuWhileStatement statement) {
        List<Byte> condition = generateBytecode(statement.condition);
        List<Byte> body = generateBytecode(statement.body);

        List<Byte> result = new ArrayList<>(condition);

        short instructionCount = (short) (body.size() + 3);
        byte highByte = (byte) (instructionCount >> 8);
        byte lowByte = (byte) (instructionCount & 0xFF);

        emitByte(XuInstructionCode.JUMP_IF_FALSE, result);
        emitByte(lowByte, result);
        emitByte(highByte, result);

        result.addAll(body);
        emitByte(XuInstructionCode.LOOP, result);

        short loopSize = (short) (condition.size() + instructionCount + 3);
        byte loopHighByte = (byte) (loopSize >> 8);
        byte loopLowByte = (byte) (loopSize & 0xFF);

        emitByte(loopLowByte, result);
        emitByte(loopHighByte, result);

        return result;
    }

    private byte emitString(String text) {
        if (!stringPool.contains(text)) {
            stringPool.add(text);
        }
        stringTable.put((byte) stringPool.indexOf(text), text);

        return (byte) stringPool.indexOf(text);
    }

    private void beginScope() {
        currentScope++;
    }

    private void endScope() {
        currentScope--;
    }
}
