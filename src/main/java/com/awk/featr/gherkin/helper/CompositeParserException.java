package com.awk.featr.gherkin.helper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class CompositeParserException extends ParserException {
    private final List<ParserException> errors;

    public CompositeParserException() {
        super("One or more errors were found");
        this.errors = Collections.EMPTY_LIST;
    }

    public CompositeParserException(List<ParserException> errors) {
        super("One or more errors were found");
        this.errors = Collections.unmodifiableList(errors);
    }

    public CompositeParserException(ParserException e) {
        super("One error was found");
        this.errors = new ArrayList<>(Collections.singleton(e));
    }

    public String toString() {
        if (errors == null) throw new NullPointerException("CompositeParserException contains no errors");
        return "Parser errors:\n" + errors.stream().map(ParserException::toString).collect(Collectors.joining("\n"));
    }

    public void addErrors(List<ParserException> errors) {
        this.errors.addAll(errors);
    }

    public List<ParserException> getErrors() {
        return errors;
    }

    public void addError(ParserException e) {
        if (e instanceof CompositeParserException) {
            errors.addAll( ((CompositeParserException)e).getErrors() );
        } else {
            errors.add(e);
        }
    }

    public boolean isEmpty() {
        return errors.isEmpty();
    }

    public ParserException getParserException() {
        if (errors.size() == 1) return errors.get(0);
        return this;
    }
}
