package com.amadeus.xu.vm;

import com.amadeus.xu.bytecode.Bytecode;
import com.amadeus.xu.bytecode.Instruction;

import java.util.Objects;
import java.util.Stack;
import static com.amadeus.xu.vm.InstructionCodes.*;

public class VirtualMachine {

    private Stack<Object> stack;
    private int ip;
    private Bytecode bytecode;

    public void execute(Bytecode code) {
        stack = new Stack<>();
        ip = 0;
        bytecode = code;
        Byte b;
        while(!Objects.equals(b = readByte(), InstructionCodes.RETURN)) {
            executeInstruction(b);
        }
        while (stack.empty()) {
            System.out.println(stack.pop());
        }
    }

    private void executeInstruction(Byte instruction) {
        switch (instruction) {
            case PUSH:
                byte index = readByte();
                stack.push(bytecode.constantTable.get(index));
                break;
            case NEGATE:
                Object value = stack.pop();
                Float valueFloat = (Float) value;
                stack.push(-valueFloat);
                break;
            case ADD:{
                Object val1 = stack.pop();
                Float valueFloat1 = (Float) val1;
                Object val2 = stack.pop();
                Float valueFloat2 = (Float) val2;
                stack.push(valueFloat1 + valueFloat2);
                break;
            }
            case SUB:
                Object val1 = stack.pop();
                Float valueFloat1 = (Float) val1;
                Object val2 = stack.pop();
                Float valueFloat2 = (Float) val2;
                stack.push(valueFloat1 - valueFloat2);
                break;
        }
    }

    private Byte readByte() {
        return bytecode.bytes.get(ip++);
    }



}
