package com.awk.featr.gherkin.model;

import java.util.Arrays;
import java.util.HashMap;

public class GherkinLanguages {
    public static DialectMap dialectMap = new DialectMap()
        .add("en", new GherkinDialect("en", "English", "English", new KeywordsMap()
            .add(GherkinDialect.AND, Arrays.asList("And", "*"))
            .add(GherkinDialect.BACKGROUND, Arrays.asList("Background"))
            .add(GherkinDialect.BUT, Arrays.asList("But", "*"))
            .add(GherkinDialect.EXAMPLES, Arrays.asList("Examples", "Scenarios"))
            .add(GherkinDialect.FEATURE, Arrays.asList("Feature", "Business Need", "Ability"))
            .add(GherkinDialect.GIVEN, Arrays.asList("Given", "*"))
            .add(GherkinDialect.RULE, Arrays.asList("Rule"))
            .add(GherkinDialect.SCENARIO, Arrays.asList("Example", "Scenario"))
            .add(GherkinDialect.SCENARIOOUTLINE, Arrays.asList("Scenario Outline", "Scenario Template"))
            .add(GherkinDialect.THEN, Arrays.asList("Then", "*"))
            .add(GherkinDialect.WHEN, Arrays.asList("When", "*"))
        ));

    public static boolean has(String language) {
        return dialectMap.containsKey(language);
    }

    public static GherkinDialect get(String language) {
        return dialectMap.get(language);
    }

    private static class DialectMap extends HashMap<String, GherkinDialect>
    {
        DialectMap add(String language, GherkinDialect gherkinDialect)
        {
            this.put(language, gherkinDialect);
            return this;
        }
    }
}
