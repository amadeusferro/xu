package org.xu.binary;

import org.xu.component.XuIOComponent;
import org.xu.exception.XuException;
import org.xu.type.XuValue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class XuBytecode extends XuIOComponent<XuBytecode> {

    public final byte[] code;
    public final List<Byte> rawCode;
    public final Map<Byte, XuValue> constantTable;
    public final Map<Byte, String> stringTable;

    public XuBytecode(String path, List<Byte> rawCode, Map<Byte, XuValue> constantTable, Map<Byte, String> stringTable) {
        this.rawCode = rawCode;
        this.stringTable = stringTable;
        try {
            this.code = Files.readAllBytes(Path.of(path));
            this.constantTable = constantTable;
        } catch (IOException e) {
            throw new XuException("Cannot read binary file.");
        }
    }

    private XuBytecode(byte[] code, List<Byte> rawCode, Map<Byte, XuValue> constantTable, Map<Byte, String> stringTable) {
        this.code = code;
        this.rawCode = rawCode;
        this.constantTable = constantTable;
        this.stringTable = stringTable;
    }

    @Override
    public XuBytecode clone() {
        return new XuBytecode(this.code, rawCode, constantTable, stringTable);
    }

    @Override
    public String toString() {
        return "XuBytecode [code=" + Arrays.toString(code) + "]";
    }
}
