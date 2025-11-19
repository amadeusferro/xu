package org.xu.type;

import java.util.Objects;

public class XuValue {
    public final Object value;
    public final byte type;

    public XuValue(Object value, byte type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public String toString() {
        if (type == XuValueType.STRING) {
            String strValue = ((String) value);
            return strValue.substring(1, strValue.length() - 1);
        }

        return value.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XuValue xuValue = (XuValue) o;
        return Objects.equals(value, xuValue.value) && type == xuValue.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, type);
    }
}
