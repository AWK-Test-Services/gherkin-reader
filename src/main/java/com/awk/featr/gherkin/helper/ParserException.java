package com.awk.featr.gherkin.helper;

import com.awk.featr.gherkin.model.GherkinLine;

public class ParserException extends Exception {
    private final GherkinLine line;

    public ParserException(String errorText) {
        super(errorText);
        line = null;
    }

    public ParserException(String errorText, GherkinLine line) {
        super(errorText);
        this.line = line;
    }

    public String toString() {
        return String.format("%s: %s\non line \"%s\"", line.getLineNr(), this.getMessage(), line.getLineText());
    }
}
