package org.xu.compiler;

import org.xu.type.XuValue;
import org.xu.component.XuIOComponent;

import java.util.List;
import java.util.Map;

public class XuCompiledCode extends XuIOComponent<XuCompiledCode> {

    public final List<Byte> code;
    public final List<Byte> rawCode;
    public final Map<Byte, XuValue> constantTable;
    public final Map<Byte, String> stringTable;

    public XuCompiledCode(List<Byte> code, List<Byte> rawCode, Map<Byte, XuValue> constantTable, Map<Byte, String> stringTable) {
        this.code = code;
        this.rawCode = rawCode;
        this.constantTable = constantTable;
        this.stringTable = stringTable;
    }

    @Override
    public XuCompiledCode clone() {
        return new XuCompiledCode(code, rawCode, constantTable, stringTable);
    }
}
