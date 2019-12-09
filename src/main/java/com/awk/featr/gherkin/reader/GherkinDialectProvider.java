package com.awk.featr.gherkin.reader;

import com.awk.featr.gherkin.helper.GherkinException;
import com.awk.featr.gherkin.helper.ParserException;
import com.awk.featr.gherkin.model.GherkinDialect;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import static java.nio.charset.StandardCharsets.UTF_8;

public class GherkinDialectProvider {
    private static JsonObject DIALECTS;
    private final String defaultDialectName;

    private static final String JSON_PATH = "/gherkin-languages.json";

    static {
        try (Reader reader = new InputStreamReader(GherkinDialectProvider.class.getResourceAsStream(JSON_PATH), UTF_8)) {
            DIALECTS = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            throw new GherkinException("Unable to parse " + JSON_PATH, e);
        }
    }

    public GherkinDialectProvider(String defaultDialectName) {
        this.defaultDialectName = defaultDialectName;
    }

    public GherkinDialectProvider() {
        this("en");
    }

    public GherkinDialect getDefaultDialect() {
        return getDialect(defaultDialectName, 0);
    }

    public GherkinDialect getDialect(String language, int lineNumber) {
        JsonElement languageObject = DIALECTS.get(language);
        if (languageObject == null) {
            throw new ParserException.NoSuchLanguageException(language, lineNumber);
        }

        return new GherkinDialect(language, languageObject.getAsJsonObject());
    }
//
//    public List<String> getLanguages() {
//        List<String> languages = new ArrayList<>(DIALECTS.keySet());
//        sort(languages);
//        return unmodifiableList(languages);
//    }
}