package com.awk.featr.gherkin.reader;

import com.awk.featr.ast.builder.ImageBuilder;
import com.awk.featr.gherkin.helper.*;
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

    private List<Image> usedImages;

    FeatureReader(String featureTitle, List<String> tags) {
        featureBuilder = new FeatureBuilder(featureTitle)
                .withTags(tags);

        usedImages = new ArrayList<>();
    }

    public Feature parse(LineReader reader, TokenMatcher tokenMatcher) throws CompositeParserException {
        List<String> tagsToRemember = new ArrayList<>();
        List<ScenarioDefinition> scenarioDefinitionsToRemember = new ArrayList<>();
        StringBuilder descriptionToRemember = new StringBuilder();
        boolean rereadLastLine = false;
        List<ParseLineException> exceptions = new ArrayList<>();

        GherkinLine line = reader.read();
        while(line.isNotEOF()) {
            try {
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
                            throw new ParseLineException("Background was not first in the Feature", line);
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
            } catch ( ParseLineException e ) {
                exceptions.add(e);
            } catch (CompositeParserException e) {
                exceptions.addAll(e.getExceptions());
            }

            line = rereadLastLine ? reader.getLastReadLine() : reader.read();
            rereadLastLine = false;
        }

        if (!exceptions.isEmpty()) throw new CompositeParserException(exceptions);

        featureBuilder.withDescription(extractAndReplaceImages(descriptionToRemember.toString()));
        featureBuilder.withScenarioDefinitions(scenarioDefinitionsToRemember);
        return featureBuilder.build();
    }

    List<Image> getUsedImages() {
        return usedImages;
    }

    void clearUsedImages() {
        usedImages = new ArrayList<>();
    }

    private String extractAndReplaceImages(String description) {
        if( ! hasImage(description) ) {
            return description;
        }

        Image image = getImage(description);
        String newDescription = replaceFirstImage(description, image);
        this.usedImages.add(image);

        return extractAndReplaceImages(newDescription);
    }

    private static boolean hasImage(String description) {
        return description.contains("<ftr-image");
    }

    private static Image getImage(String description) {
        String imageLine = getSubstring(description, "<ftr-image", ">");
        String location = getSubstring(imageLine, "src=\"", "\"");
        String type = getSubstring(imageLine, "type=\"", "\"");
        ImageBuilder imageBuilder = new ImageBuilder()
                .withLocation(location)
                .withType(type);
        return imageBuilder.build();
    }

    private static String getSubstring(String text, String beginTag, String endTag) {
        int begin = text.indexOf(beginTag) + beginTag.length();
        int end = text.indexOf(endTag, begin);
        return text.substring(begin, end);
    }

    private static String replaceFirstImage(String description, Image image) {
        return description.replaceFirst("<ftr-image", "<img id=\"" + image.getId() + "\"");
    }
}
