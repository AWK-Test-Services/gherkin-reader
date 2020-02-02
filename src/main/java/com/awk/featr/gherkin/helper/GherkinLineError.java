package com.awk.featr.gherkin.helper;

import com.awk.featr.ast.GherkinError;
import com.awk.featr.gherkin.model.GherkinLine;

public class GherkinLineError implements GherkinError {
    private final String message;
    private final GherkinLine line;

    public GherkinLineError(String message, GherkinLine line) {
        this.message = message;
        this.line = line;
    }

    @Override
    public String getMessage() {
        return String.format("\"%s\" on line %s:\n\"%s\"", message, line.getLineNr(), line.getLineText());
    }
}
