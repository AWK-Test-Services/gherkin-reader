package com.awk.featr.gherkin.reader;

import com.awk.featr.gherkin.helper.GherkinLanguageConstants;
import com.awk.featr.gherkin.helper.ParserException;
import com.awk.featr.gherkin.model.GherkinLine;
import com.awk.featr.ast.*;
import com.awk.featr.ast.builder.FeatureBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class FeatureReader {
    private final FeatureBuilder featureBuilder;

    FeatureReader(String featureTitle, List<String> tags) {
        featureBuilder = new FeatureBuilder(featureTitle)
                .withTags(tags);
    }

    public Feature parse(LineReader reader, TokenMatcher tokenMatcher) {
        List<String> tagsToRemember = new ArrayList<>();
        List<ScenarioDefinition> scenarioDefinitionsToRemember = new ArrayList<>();
        StringBuilder descriptionToRemember = new StringBuilder();
        boolean rereadLastLine = false;

        GherkinLine line = reader.read();
        while(line.isNotEOF()) {
            if (line.isNotEmpty()) {
                Logger.getLogger(FeatureReader.class.getName()).log(Level.INFO,line.getLineNr() + ": Processing line: " + line.getLineText());

                if (line.startsWith(GherkinLanguageConstants.COMMENT_PREFIX)) {
                    //ignore
                } else if (tokenMatcher.match_TagLine(line)) {
                    List<String> tagsToAdd = tokenMatcher.getTags(line)
                            .stream()
                            .map(lineSpan -> lineSpan.text)
                            .collect(Collectors.toList());
                    tagsToRemember.addAll(tagsToAdd);

                } else if (tokenMatcher.match_BackgroundLine(line)) {
                    if ( !scenarioDefinitionsToRemember.isEmpty() ) {
                        throw new ParserException.AstBuilderException("Background was not first in the Feature", line.getLineNr());
                    }
                    String title = tokenMatcher.getBackgroundTitle(line);
                    Background background = new BackgroundReader(title).parse(reader, tokenMatcher);
                    featureBuilder.withBackground(background);
                    rereadLastLine = true;

                } else if (tokenMatcher.match_ScenarioLine(line)) {
                    String title = tokenMatcher.getScenarioLine(line);
                    Scenario scenario = new ScenarioReader(title, tagsToRemember).parse(reader, tokenMatcher);
                    scenarioDefinitionsToRemember.add(scenario);
                    rereadLastLine = true;

                } else if (tokenMatcher.match_ScenarioOutlineLine(line)) {
                    String title = tokenMatcher.getScenarioOutlineLine(line);
                    ScenarioOutline scenarioOutline = new ScenarioOutlineReader(title, tagsToRemember).parse(reader, tokenMatcher);
                    scenarioDefinitionsToRemember.add(scenarioOutline);
                    rereadLastLine = true;

                } else if (tokenMatcher.match_RuleLine(line)) {
                    String rule = tokenMatcher.getRule(line);
                    featureBuilder.withRule( rule );

                } else {
                    if (descriptionToRemember.length() > 0)
                        descriptionToRemember.append("\n");
                    descriptionToRemember.append(tokenMatcher.getOther(line));
                }
            }

            line = rereadLastLine ? reader.getLastReadLine() : reader.read();
            rereadLastLine = false;
        }

        featureBuilder.withDescription(descriptionToRemember.toString());
        featureBuilder.withScenarioDefinitions(scenarioDefinitionsToRemember);
        return featureBuilder.build();
    }
}
