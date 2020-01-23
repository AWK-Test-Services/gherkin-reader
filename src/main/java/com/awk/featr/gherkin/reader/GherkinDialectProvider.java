package com.awk.featr.gherkin.reader;

import com.awk.featr.gherkin.helper.NoSuchLanguageException;
import com.awk.featr.gherkin.model.GherkinDialect;
import com.awk.featr.gherkin.model.GherkinLanguages;
import com.awk.featr.gherkin.model.GherkinLine;

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

    public GherkinDialect getDialect(String language, GherkinLine line) throws NoSuchLanguageException {
        if ( GherkinLanguages.has(language) )
        {
            return GherkinLanguages.get(language);
        }
        throw new NoSuchLanguageException("There is no language defined for \"" + language + "\"", line);
    }
}