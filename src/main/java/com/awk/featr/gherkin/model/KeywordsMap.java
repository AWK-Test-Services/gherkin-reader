package com.awk.featr.gherkin.model;

import java.util.HashMap;
import java.util.List;

public class KeywordsMap extends HashMap<String, List<String>> {

    public KeywordsMap add(String key, List<String> keywords)
    {
        this.put(key, keywords);
        return this;
    }
}
