package com.awk.featr.gherkin.helper;


import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ParserException extends RuntimeException {
    private final int lineNumber;

    public ParserException(String message) {
        super(message);
        lineNumber = -1;
    }

    public ParserException(String message, int lineNumber) {
        super(getMessage(message, lineNumber));
        this.lineNumber = lineNumber;
    }

    private static String getMessage(String message, int lineNumber) {
        return String.format("%s: %s", lineNumber, message);
    }

    public static class AstBuilderException extends ParserException {
        public AstBuilderException(String message, int lineNumber) {
            super(message, lineNumber);
        }
    }

    public static class NoSuchLanguageException extends ParserException {
        public NoSuchLanguageException(String language, int lineNumber) {
            super("Language not supported: " + language, lineNumber);
        }
    }

    public static class CompositeParserException extends ParserException {
        public final List<ParserException> errors;

        public CompositeParserException(List<ParserException> errors) {
            super(getMessage(errors));
            this.errors = Collections.unmodifiableList(errors);
        }

        private static String getMessage(List<ParserException> errors) {
            if (errors == null) throw new NullPointerException("errors");
            return "Parser errors:\n" + errors.stream().map(Throwable::getMessage).collect(Collectors.joining("\n"));
        }
    }
}
