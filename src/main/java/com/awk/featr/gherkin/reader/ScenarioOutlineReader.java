package com.awk.featr.gherkin.reader;

import com.awk.featr.gherkin.helper.CompositeParserException;
import com.awk.featr.gherkin.helper.GherkinLanguageConstants;
import com.awk.featr.gherkin.helper.ParseLineException;
import com.awk.featr.gherkin.helper.ParserException;
import com.awk.featr.gherkin.model.GherkinLine;
import com.awk.featr.ast.Examples;
import com.awk.featr.ast.ScenarioOutline;
import com.awk.featr.ast.Step;
import com.awk.featr.ast.builder.ScenarioOutlineBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScenarioOutlineReader {
    private final ScenarioOutlineBuilder scenarioOutlineBuilder;
    private final StringBuilder descriptionToRemember = new StringBuilder();


    ScenarioOutlineReader(String scenarioTitle, List<String> tags) {
        scenarioOutlineBuilder = new ScenarioOutlineBuilder(scenarioTitle)
            .withTags(tags);
    }

    public ScenarioOutline parse(LineReader reader, TokenMatcher tokenMatcher) throws CompositeParserException {
        List<Step> stepsToRemember = new ArrayList<>();
        boolean rereadLastLine = false;
        List<ParseLineException> exceptions = new ArrayList<>();

        GherkinLine line = reader.read();
        while(line.isNotEOF()) {
            try {
                if (line.isNotEmpty()) {
                    Logger.getLogger(ScenarioOutlineReader.class.getName()).log(Level.INFO,line.getLineNr() + ": Processing line: " + line.getLineText());

                    if (line.startsWith(GherkinLanguageConstants.COMMENT_PREFIX)) {
                        //ignore
                    } else if (tokenMatcher.match_StepLine(line)) {
                        String keyword = tokenMatcher.getStepKeyword(line);
                        String stepText = tokenMatcher.getStepText(line, keyword);
                        Step step = new StepReader(keyword, stepText).parse(reader, tokenMatcher);
                        stepsToRemember.add(step);
                        rereadLastLine = true;

                    } else if (tokenMatcher.match_ExamplesLine(line)) {
                        String keyword = tokenMatcher.getExampleKeyword(line);
                        String exampleText = tokenMatcher.getExamplesLine(line, keyword);
                        Examples examples = new ExamplesReader(keyword, exampleText).parse(reader, tokenMatcher);
                        scenarioOutlineBuilder.withExamples(examples);
                        rereadLastLine = true;

                    } else if (tokenMatcher.match_ScenarioDefinitionLine(line)) {
                        return build(stepsToRemember);
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

        return build(stepsToRemember);
    }

    private ScenarioOutline build(List<Step> steps) {
        return scenarioOutlineBuilder
                .withSteps(steps)
                .withDescription(descriptionToRemember.toString())
                .build();
    }
}
