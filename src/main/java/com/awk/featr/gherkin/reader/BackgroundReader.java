package com.awk.featr.gherkin.reader;

import com.awk.featr.gherkin.helper.CompositeParserException;
import com.awk.featr.gherkin.helper.GherkinLanguageConstants;
import com.awk.featr.gherkin.helper.ParseLineException;
import com.awk.featr.gherkin.helper.ParserException;
import com.awk.featr.gherkin.model.GherkinLine;
import com.awk.featr.ast.Background;
import com.awk.featr.ast.Step;
import com.awk.featr.ast.builder.BackgroundBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BackgroundReader {
    private final BackgroundBuilder backgroundBuilder;

    BackgroundReader(String backgroundTitle) {
        backgroundBuilder = new BackgroundBuilder(backgroundTitle);
    }

    public Background parse(LineReader reader, TokenMatcher tokenMatcher) throws CompositeParserException {
        List<Step> stepsToRemember = new ArrayList<>();
        boolean rereadLastLine = false;
        List<ParseLineException> exceptions = new ArrayList<>();

        GherkinLine line = reader.read();
        while(line.isNotEOF()) {
            try {
                if (line.isNotEmpty()) {
                    Logger.getLogger(BackgroundReader.class.getName()).log(Level.INFO, line.getLineNr() + ": Processing line: " + line.getLineText());

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
                        throw new ParseLineException("No or unknown tag found", line);
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

    private Background build(List<Step> steps) {
        backgroundBuilder.withSteps(steps);
        return backgroundBuilder.build();
    }
}
