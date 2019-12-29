package com.awk.featr.gherkin.model;

import java.util.*;

import static java.util.Objects.requireNonNull;

public class GherkinDialect {
    public static final String AND = "and";
    public static final String BACKGROUND = "background";
    public static final String BUT = "but";
    public static final String EXAMPLES = "examples";
    public static final String FEATURE = "feature";
    public static final String GIVEN = "given";
    public static final String RULE = "rule";
    public static final String SCENARIO = "scenario";
    public static final String SCENARIOOUTLINE = "scenarioOutline";
    public static final String THEN = "then";
    public static final String WHEN = "when";

    private final String language;
    private final String name;
    private final String nativeName;
    private final KeywordsMap keywordMap;

    public GherkinDialect(String language, String name, String nativeName, KeywordsMap keywordMap) {
        this.language = requireNonNull(language);
        this.name = requireNonNull(name);
        this.nativeName = requireNonNull(nativeName);
        this.keywordMap = requireNonNull(keywordMap);
    }

    public String getLanguage() {
        return language;
    }

    public String getName() {
        return name;
    }

    public String getNativeName() {
        return nativeName;
    }

    public List<String> getFeatureKeywords() {
        return keywordMap.get(FEATURE);
    }

    public List<String> getRuleKeywords() {
        return keywordMap.get(RULE);
    }

    public List<String> getScenarioKeywords() {
        return keywordMap.get(SCENARIO);
    }

    public List<String> getScenarioOutlineKeywords() {
        return keywordMap.get(SCENARIOOUTLINE);
    }

    public List<String> getBackgroundKeywords() {
        return keywordMap.get(BACKGROUND);
    }

    public List<String> getExamplesKeywords() {
        return keywordMap.get(EXAMPLES);
    }

    public List<String> getGivenKeywords() {
        return keywordMap.get(GIVEN);
    }

    public List<String> getWhenKeywords() {
        return keywordMap.get(WHEN);
    }

    public List<String> getThenKeywords() {
        return keywordMap.get(THEN);
    }

    public List<String> getAndKeywords() {
        return keywordMap.get(AND);
    }

    public List<String> getButKeywords() {
        return keywordMap.get(BUT);
    }

    public List<String> getStepKeywords() {
        List<String> result = new ArrayList<>();
        result.addAll(getGivenKeywords());
        result.addAll(getWhenKeywords());
        result.addAll(getThenKeywords());
        result.addAll(getAndKeywords());
        result.addAll(getButKeywords());
        return result;
    }
}
