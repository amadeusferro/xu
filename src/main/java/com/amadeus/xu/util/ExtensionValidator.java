package com.amadeus.xu.util;

import com.amadeus.xu.exception.XuInvalidExtension;

import java.util.Arrays;
import java.util.List;

public class ExtensionValidator {

    private final List<String> allowedExtensions;

    public ExtensionValidator(String... allowedExtensions) {
        this.allowedExtensions = Arrays.asList(allowedExtensions);
    }

    public void validateExtension(String path) {
        String extension = getExtension(path);

        if(extension == null || !allowedExtensions.contains(extension)) throw new XuInvalidExtension("Invalid file extension: " + path + ". Allowed extensions: " + allowedExtensions);
    }

    private String getExtension(String path) {

        int dotIndex = path.lastIndexOf('.');

        if (dotIndex > 0 && dotIndex < path.length() - 1) {
            return path.substring(dotIndex + 1).toLowerCase();
        }
        return null;
    }

}
