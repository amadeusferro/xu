package com.amadeus.xu.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class XuFile {

    private final String source;
    private final String path;

    public XuFile(String path) {
        this.path = path;

        ExtensionValidator extensionValidator = new ExtensionValidator("xu");
        extensionValidator.validateExtension(path);

        source = readFile(path);
    }

    private String readFile(String path) {

        StringBuilder sb = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;

            while((line = reader.readLine()) != null) sb.append(line).append("\n");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return sb.toString();
    }

    public String getSource() {
        return source;
    }

    public String getPath() {
        return path;
    }
}
