package com.awk.featr.gherkin.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

public class GherkinDialect {
    private final JsonObject keywords;
    private String language;

    public GherkinDialect(String language, JsonObject keywords) {
        this.language = language;
        this.keywords = keywords;
    }

    public List<String> getFeatureKeywords() {
        return toStringList(this.keywords.getAsJsonArray("feature"));
    }

    private static List<String> toStringList(JsonArray array) {
        List<String> result = new ArrayList<>();
        for (JsonElement jsonValue : array) {
            result.add(jsonValue.getAsString());
        }
        return result;
    }

    public String getName() {
        return keywords.get("name").getAsString();
    }

    public String getNativeName() {
        return keywords.get("native").getAsString();
    }

    public List<String> getRuleKeywords() {
        return toStringList(keywords.getAsJsonArray("rule"));
    }

    public List<String> getScenarioKeywords() {
        return toStringList(keywords.getAsJsonArray("scenario"));
    }

    public List<String> getScenarioOutlineKeywords() {
        return toStringList(keywords.getAsJsonArray("scenarioOutline"));
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

    public List<String> getBackgroundKeywords() {
        return toStringList(keywords.getAsJsonArray("background"));
    }

    public List<String> getExamplesKeywords() {
        return toStringList(keywords.getAsJsonArray("examples"));
    }

    public List<String> getGivenKeywords() {
        return toStringList(keywords.getAsJsonArray("given"));
    }

    public List<String> getWhenKeywords() {
        return toStringList(keywords.getAsJsonArray("when"));
    }

    public List<String> getThenKeywords() {
        return toStringList(keywords.getAsJsonArray("then"));
    }

    public List<String> getAndKeywords() {
        return toStringList(keywords.getAsJsonArray("and"));
    }

    public List<String> getButKeywords() {
        return toStringList(keywords.getAsJsonArray("but"));
    }

    public String getLanguage() {
        return language;
    }
}
