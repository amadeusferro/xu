package org.xu.interpreter;

import org.xu.type.XuValueType;

import java.util.HashMap;
import java.util.Map;

public final class XuValueTypeConverter {

    private static final Map<Byte, String> valueTypeToString;

    static {
        valueTypeToString = new HashMap<>();
        valueTypeToString.put(XuValueType.INT, "int");
        valueTypeToString.put(XuValueType.FLOAT, "float");
        valueTypeToString.put(XuValueType.CHAR, "char");
        valueTypeToString.put(XuValueType.STRING, "string");
        valueTypeToString.put(XuValueType.BOOL, "bool");
        valueTypeToString.put(XuValueType.NULL, "null");
    }

    private XuValueTypeConverter() {
    }

    public static String valueTypeToString(Byte type) {
        return valueTypeToString.get(type);
    }
}
