package org.xu.util;

import org.xu.component.XuIOComponent;
import org.xu.component.XuValidationComponent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public final class XuFile extends XuIOComponent<XuFile> {

    private final String source;

    public XuFile(String path) {
        XuValidationComponent<String> validator = new FileExtensionValidator("xu");
        validator.validate(path);
        source = extract(path);
    }

    public XuFile(XuFile file) {
        this.source = file.source;
    }

    public String getSource() {
        return source;
    }

    private String extract(String path) {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return sb.toString();
    }

    @Override
    public XuIOComponent clone() {
        return new XuFile(this);
    }
}
