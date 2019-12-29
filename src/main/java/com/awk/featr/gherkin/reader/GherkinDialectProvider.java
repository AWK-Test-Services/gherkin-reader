package com.awk.featr.gherkin.reader;

import com.awk.featr.gherkin.helper.ParserException;
import com.awk.featr.gherkin.model.GherkinDialect;
import com.awk.featr.gherkin.model.GherkinLanguages;

public class GherkinDialectProvider {
    private final String defaultDialectName;

    public GherkinDialectProvider(String defaultDialectName) {
        this.defaultDialectName = defaultDialectName;
    }

    public GherkinDialectProvider() {
        this("en");
    }

    public GherkinDialect getDefaultDialect() {
        return GherkinLanguages.get("en");
    }

    public GherkinDialect getDialect(String language, int lineNr) {
        if ( GherkinLanguages.has(language) )
        {
            return GherkinLanguages.get(language);
        }
        throw new ParserException.NoSuchLanguageException("There is no language defined for \"" + language + "\"", lineNr);
    }
}