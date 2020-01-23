package com.awk.featr.gherkin.helper;

import com.awk.featr.gherkin.model.GherkinLine;

public class NoSuchLanguageException extends ParserException {
    public NoSuchLanguageException(String language, GherkinLine line) {
        super("Language not supported: " + language, line);
    }
}

