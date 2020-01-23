package com.awk.featr.gherkin.helper;

import com.awk.featr.gherkin.model.GherkinLine;

public class ParseLineException extends ParserException {
    private final GherkinLine line;

    public ParseLineException(String errorText) {
        super(errorText);
        line = null;
    }

    public ParseLineException(String errorText, GherkinLine line) {
        super(errorText);
        this.line = line;
    }

    public String toString() {
        return String.format("%s: %s\non line \"%s\"", line.getLineNr(), this.getMessage(), line.getLineText());
    }
}
