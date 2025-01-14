package com.amadeus.xu.exception;

public class XuException extends RuntimeException {

    public XuException() {
        super();
    }

    public XuException(String error) {
        super(error);
    }
}
