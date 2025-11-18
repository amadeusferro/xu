package com.amadeus.xu.decompiler;

import com.amadeus.xu.bytecode.Bytecode;
import com.amadeus.xu.vm.InstructionCodes;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Decompiler {

    private PrintWriter writer;
    private int ip;
    private Bytecode bytecode;
    private StringBuilder sb = new StringBuilder();
    int tabSize = 4;

    public void decompile(Bytecode code, String path) {
        sb = new StringBuilder();
        ip = 0;
        bytecode = code;

        byte b;

        sb.append("CODE:");
        newline();

        while ((b = readByte()) != InstructionCodes.RETURN) {
            indent();
            decompileInstruction(b);
        }

        indent();
        sb.append("RETURN");

        newline();
        newline();

        sb.append("CONSTANTS:");
        newline();

        for(int i = 0; i < bytecode.constantTable.size(); i++) {
            indent();
            sb.append(i);
            indent();
            sb.append(bytecode.constantTable.get(i));
            newline();
        }


        try {
            writer = new PrintWriter(new FileWriter(path));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writer.print(sb.toString());
        writer.flush();
        writer.close();
    }

    private void decompileInstruction(byte instruction) {
        switch (instruction) {
            case InstructionCodes.PUSH:
                sb.append("PUSH");
                indent();
                byte index = readByte();
                sb.append(index);
                newline();
                break;

            case InstructionCodes.NEGATE:
                sb.append("NEGATE");
                newline();
                break;
            case InstructionCodes.ADD:
                sb.append("ADD");
                newline();
                break;
            case InstructionCodes.SUB:
                sb.append("SUB");
                newline();
                break;
        }
    }

    private void newline() {
        sb.append("\n");
    }

    private void indent() {
        sb.append(" ".repeat(tabSize));
    }

    private byte readByte() {
        return bytecode.bytes.get(ip++);
    }
}
