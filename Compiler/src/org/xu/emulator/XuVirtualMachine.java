package org.xu.emulator;

import org.xu.binary.XuBytecode;
import org.xu.compiler.XuInstructionCode;
import org.xu.component.XuNullIOComponent;
import org.xu.exception.XuException;
import org.xu.pass.XuCompilationPass;
import org.xu.type.XuValue;
import org.xu.type.XuValueType;

import java.util.*;

public class XuVirtualMachine extends XuCompilationPass<XuBytecode, XuNullIOComponent> {

    private final Stack<XuValue> stack;
    private int ip;
    private List<Byte> code;
    private List<XuValue> constantTable;
    private Map<Byte, XuValue> variables;
    private List<String> stringPool;

    public XuVirtualMachine() {
        stack = new Stack<>();
    }

    private void execute(XuBytecode bytecode) {
        code = new ArrayList<>(bytecode.rawCode);
        constantTable = bytecode.constantTable.values().stream().toList();
        variables = new HashMap<>();
        stringPool = new ArrayList<>();

        stringPool.addAll(bytecode.stringTable.values());

        long start = System.currentTimeMillis();

        for (ip = 0; ip < bytecode.code.length; ) {
            byte b;

            switch (b = readByte()) {
                case XuInstructionCode.LOAD: {
                    byte index = readByte();
                    stack.push(variables.get(index));
                    break;
                }

                case XuInstructionCode.LOAD_CONST:
                    loadConst();
                    break;

                case XuInstructionCode.ADD:
                case XuInstructionCode.SUB:
                case XuInstructionCode.MUL:
                case XuInstructionCode.DIV:
                    binaryExpression(b);
                    break;

                case XuInstructionCode.LESS:
                case XuInstructionCode.LESS_EQUAL:
                case XuInstructionCode.GREATER:
                case XuInstructionCode.GREATER_EQUAL:
                case XuInstructionCode.EQUAL_EQUAL:
                case XuInstructionCode.MARK_EQUAL:
                    comparisonExpression(b);
                    break;

                case XuInstructionCode.AND:
                case XuInstructionCode.OR:
                    logicalExpression(b);
                    break;

                case XuInstructionCode.NEGATE:
                    negate();
                    break;

                case XuInstructionCode.INVERSE:
                    inverse();
                    break;

                case XuInstructionCode.PRINT:
                    print();
                    break;

                case XuInstructionCode.JUMP: {
                    byte lowByte = readByte();
                    byte highByte = readByte();

                    short offset = (short) ((highByte << 8) | (lowByte & 0xFF));
                    ip += offset;
                    break;
                }

                case XuInstructionCode.LOOP: {
                    byte lowByte = readByte();
                    byte highByte = readByte();

                    short offset = (short) ((highByte << 8) | (lowByte & 0xFF));
                    ip -= offset;
                    break;
                }

                case XuInstructionCode.JUMP_IF_FALSE: {
                    byte lowByte = readByte();
                    byte highByte = readByte();

                    short offset = (short) ((highByte << 8) | (lowByte & 0xFF));

                    XuValue value = stack.pop();

                    if (!((boolean) value.value)) {
                        ip += offset;
                    }

                    break;
                }

                case XuInstructionCode.DEFINE: {
                    variables.put(readByte(), stack.pop());
                    break;
                }

                case XuInstructionCode.STORE: {
                    Byte index = readByte();
                    if (variables.containsKey(index)) {
                        variables.put(index, stack.pop());
                    } else {
                        throw new XuException("Variable '" + index + "' not found");
                    }
                    break;
                }

                case XuInstructionCode.RETURN:
                    return;
            }
        }
    }

    private void print() {
        XuValue value = stack.pop();
        System.out.println(value.toString());
    }

    private void inverse() {
        XuValue value = stack.pop();
        stack.push(new XuValue(!((boolean) value.value), XuValueType.BOOL));
    }

    private void logicalExpression(byte type) {
        XuValue b = stack.pop();
        XuValue a = stack.pop();

        switch (type) {
            case XuInstructionCode.AND: {
                XuValue value = new XuValue((boolean) a.value && (boolean) b.value, XuValueType.BOOL);
                stack.push(value);
                break;
            }

            case XuInstructionCode.OR: {
                XuValue value = new XuValue((boolean) a.value || (boolean) b.value, XuValueType.BOOL);
                stack.push(value);
                break;
            }
        }
    }

    private void comparisonExpression(byte type) {

        XuValue b = stack.pop();
        XuValue a = stack.pop();

        switch (type) {
            case XuInstructionCode.LESS: {
                XuValue value = new XuValue((int) a.value < (int) b.value, XuValueType.BOOL);
                stack.push(value);
                break;
            }

            case XuInstructionCode.LESS_EQUAL: {
                XuValue value = new XuValue((int) a.value <= (int) b.value, XuValueType.BOOL);
                stack.push(value);
                break;
            }

            case XuInstructionCode.GREATER: {
                XuValue value = new XuValue((int) a.value > (int) b.value, XuValueType.BOOL);
                stack.push(value);
                break;
            }

            case XuInstructionCode.GREATER_EQUAL: {
                XuValue value = new XuValue((int) a.value >= (int) b.value, XuValueType.BOOL);
                stack.push(value);
                break;
            }

            case XuInstructionCode.EQUAL_EQUAL: {
                XuValue value = new XuValue(a.value.equals(b.value), XuValueType.BOOL);
                stack.push(value);
                break;
            }

            case XuInstructionCode.MARK_EQUAL: {
                XuValue value = new XuValue(!(a.value.equals(b.value)), XuValueType.BOOL);
                stack.push(value);
                break;
            }
        }
    }

    private void negate() {
        XuValue value = stack.pop();
        stack.push(new XuValue(-(int) value.value, XuValueType.INT));
    }

    private void binaryExpression(byte type) {

        XuValue b = stack.pop();
        XuValue a = stack.pop();

        switch (type) {
            case XuInstructionCode.ADD: {
                XuValue result = new XuValue((int) a.value + (int) b.value, XuValueType.INT);
                stack.push(result);
                break;
            }

            case XuInstructionCode.SUB: {
                XuValue result = new XuValue((int) a.value - (int) b.value, XuValueType.INT);
                stack.push(result);
                break;
            }

            case XuInstructionCode.MUL: {
                XuValue result = new XuValue((int) a.value * (int) b.value, XuValueType.INT);
                stack.push(result);
                break;
            }
            case XuInstructionCode.DIV: {
                XuValue result = new XuValue((int) a.value / (int) b.value, XuValueType.INT);
                stack.push(result);
                break;
            }
        }
    }

    private byte readByte() {
        return code.get(ip++);
    }

    private void loadConst() {
        byte constantIndex = readByte();
        XuValue value = constantTable.get(constantIndex);
        stack.push(value);
    }

    private int getInt() {
        int value = (code.get(ip) & 0xFF) << 24 |
                (code.get(ip + 1) & 0xFF) << 16 |
                (code.get(ip + 2) & 0xFF) << 8 |
                (code.get(ip + 3) & 0xFF);

        ip += 4;
        return value;
    }

    @Override
    public Class<XuBytecode> getInputType() {
        return XuBytecode.class;
    }

    @Override
    public Class<XuNullIOComponent> getOutputType() {
        return XuNullIOComponent.class;
    }

    @Override
    public String getDebugName() {
        return "Virtual Machine";
    }

    @Override
    protected XuNullIOComponent pass(XuBytecode input) {
        execute(input);
        return new XuNullIOComponent();
    }
}
