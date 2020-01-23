package com.awk.featr.gherkin.reader;

import com.awk.featr.gherkin.helper.CompositeParserException;
import com.awk.featr.gherkin.helper.GherkinLanguageConstants;
import com.awk.featr.gherkin.helper.ParserException;
import com.awk.featr.gherkin.model.GherkinLine;
import com.awk.featr.ast.Scenario;
import com.awk.featr.ast.Step;
import com.awk.featr.ast.builder.ScenarioBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ScenarioReader {
    private final ScenarioBuilder scenarioBuilder;

    ScenarioReader(String scenarioTitle, List<String> tags) {
        scenarioBuilder = new ScenarioBuilder(scenarioTitle)
            .withTags(tags);
    }

    public Scenario parse(LineReader reader, TokenMatcher tokenMatcher) throws ParserException {
        List<Step> stepsToRemember = new ArrayList<>();
        boolean rereadLastLine = false;
        CompositeParserException errors = new CompositeParserException();

        GherkinLine line = reader.read();
        while(line.isNotEOF()) {
            try {
                if (line.isNotEmpty()) {
                    Logger.getLogger(ScenarioReader.class.getName()).log(Level.INFO,line.getLineNr() + ": Processing line: " + line.getLineText());

                    if (line.startsWith(GherkinLanguageConstants.COMMENT_PREFIX)) {
                        //ignore
                    } else if (tokenMatcher.match_StepLine(line)) {
                        String keyword = tokenMatcher.getStepKeyword(line);
                        String stepText = tokenMatcher.getStepText(line, keyword);
                        Step step = new StepReader(keyword, stepText).parse(reader, tokenMatcher);
                        stepsToRemember.add(step);
                        rereadLastLine = true;

                    } else if (tokenMatcher.match_ScenarioDefinitionLine(line)) {
                        return build(stepsToRemember);
                    } else {
                        throw new ParserException("No or unknown tag found", line);
                    }
                }
            } catch ( ParserException e ) {
                errors.addError(e);
            }

            line = rereadLastLine ? reader.getLastReadLine() : reader.read();
            rereadLastLine = false;
        }
        if (!errors.isEmpty()) throw errors.getParserException();

        return build(stepsToRemember);
    }

    private Scenario build(List<Step> steps) {
        scenarioBuilder.withSteps(steps);
        return scenarioBuilder.build();
    }
}
