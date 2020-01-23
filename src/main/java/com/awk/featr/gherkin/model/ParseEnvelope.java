package com.awk.featr.gherkin.model;

import com.awk.featr.ast.Document;
import com.awk.featr.gherkin.helper.CompositeParserException;

public class ParseEnvelope {
    private final boolean errorsFound;

    private final Document document;

    private final CompositeParserException errors;

    public ParseEnvelope(Document document) {
        errorsFound = false;
        this.document = document;
        this.errors = null;
    }

    public ParseEnvelope(Document document, CompositeParserException error) {
        errorsFound = true;
        this.document = document;
        this.errors = error;
    }

    public Document getDocument() {
        return document;
    }

    public CompositeParserException getErrors() {
        return errors;
    }
}
