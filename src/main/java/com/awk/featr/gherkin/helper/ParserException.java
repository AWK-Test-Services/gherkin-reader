package com.awk.featr.gherkin.helper;

public class ParserException extends Exception {

    public ParserException(String errorText) {
        super(errorText);
    }

    public String toString() {
        return getMessage();
    }
}
