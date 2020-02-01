package com.awk.featr.gherkin.helper;

import java.util.ArrayList;
import java.util.List;

public class CompositeParserException extends ParserException {
    private final List<ParseLineException> exceptions;

    public CompositeParserException() {
        super("One or more errors were found");
        this.exceptions = new ArrayList<>();
    }

    public CompositeParserException(List<ParseLineException> exceptions) {
        super("One or more errors were found");
        this.exceptions = exceptions;
    }

    public List<ParseLineException> getExceptions() {
        return exceptions;
    }

    public void addError(CompositeParserException e) {
        exceptions.addAll( e.getExceptions() );
    }

    public void addError(ParseLineException e) {
        exceptions.add(e);
    }

    public boolean isEmpty() {
        return exceptions.isEmpty();
    }
}
