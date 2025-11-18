package com.amadeus.xu.bytecode;

import java.util.List;
import java.util.Map;

public class Bytecode {


    public final List<Byte> bytes;
    public final List<Object> constantTable;

    public Bytecode(List<Byte> bytes, List<Object> constantTable) {
        this.bytes = bytes;
        this.constantTable = constantTable;
    }

    @Override
    public String toString() {
        return "Bytecode{" +
                "bytes=" + bytes +
                ", constantTable=" + constantTable +
                '}';
    }
}
