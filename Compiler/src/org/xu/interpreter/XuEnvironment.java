package org.xu.interpreter;

import org.xu.exception.XuException;
import org.xu.scanner.Token;
import org.xu.type.XuValue;

import java.util.HashMap;
import java.util.Map;

public class XuEnvironment {

    private final Map<String, XuValue> variables;
    public XuEnvironment enclosing = null;

    public XuEnvironment() {
        variables = new HashMap<>();
    }

    public XuEnvironment(XuEnvironment enclosing) {
        this.enclosing = enclosing;
        variables = new HashMap<>();
    }

    public void define(Token name, XuValue value) {
        if (variables.containsKey(name.toString())) {
            System.out.println("Variable '" + name + "' already defined.");
            System.exit(1);
        }

        variables.put(name.lexeme(), value);
    }

    public void set(Token name, XuValue value) {
        if (variables.containsKey(name.lexeme())) {
            XuValue oldValue = variables.get(name.lexeme());

            if (oldValue.type == value.type) {
                variables.put(name.lexeme(), value);
            } else {
                System.out.println("Cannot assign '" + XuValueTypeConverter.valueTypeToString(value.type) +
                        "' to '" + XuValueTypeConverter.valueTypeToString(oldValue.type) + "'.");
                System.exit(1);
            }

        } else if (enclosing != null) {
            enclosing.set(name, value);
        } else {
            System.out.println("Undefined variable '" + name.lexeme() + "'.");
            System.exit(1);
        }
    }

    public XuValue get(Token name) {
        if (variables.containsKey(name.lexeme())) {
            return variables.get(name.lexeme());
        } else if (enclosing != null) {
            return enclosing.get(name);
        } else {
            System.out.println("Undefined variable '" + name.lexeme() + "'.");
            System.exit(1);
        }

        return null;
    }
}
