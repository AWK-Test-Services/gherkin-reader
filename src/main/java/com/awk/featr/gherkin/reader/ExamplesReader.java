package com.awk.featr.gherkin.reader;

import com.awk.featr.gherkin.helper.CompositeParserException;
import com.awk.featr.gherkin.helper.GherkinLanguageConstants;
import com.awk.featr.gherkin.helper.ParserException;
import com.awk.featr.gherkin.model.GherkinLine;
import com.awk.featr.ast.Examples;
import com.awk.featr.ast.TableRow;
import com.awk.featr.ast.builder.ExampleBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExamplesReader {
    private final ExampleBuilder exampleBuilder;

    ExamplesReader(String keyword, String title) {
        exampleBuilder = new ExampleBuilder(keyword, title, new TableRow(new ArrayList<>()));
    }

    public Examples parse(LineReader reader, TokenMatcher tokenMatcher) throws ParserException {
        boolean gotHeader = false;
        List<TableRow> rowsToRemember = new ArrayList<>();
        CompositeParserException errors = new CompositeParserException();

        GherkinLine line = reader.read();
        while(line.isNotEOF()) {
            try {
                if (line.isNotEmpty()) {
                    Logger.getLogger(ExamplesReader.class.getName()).log(Level.INFO,line.getLineNr() + ": Processing line: " + line.getLineText());

                    if (line.startsWith(GherkinLanguageConstants.COMMENT_PREFIX)) {
                        //ignore
                    } else if (tokenMatcher.match_ScenarioDefinitionLine(line) ||
                            tokenMatcher.match_FeatureLine(line) ||
                            tokenMatcher.match_StepLine(line) ||
                            tokenMatcher.match_ExamplesLine(line)) {
                        return build(rowsToRemember);
                    } else if (tokenMatcher.match_TableRow(line)) {
                        List<String> cells = line.getTableCells();
                        TableRow row = new TableRow(cells);
                        if ( ! gotHeader ) {
                            exampleBuilder.withHeader(row);
                            gotHeader = true;
                        } else {
                            rowsToRemember.add(row);
                        }
                    } else {
                        throw new ParserException("Line is not detected as relevant for a Step", line);
                    }
                }
            } catch ( ParserException e ) {
                errors.addError(e);
            }

            line = reader.read();
        }
        if (!errors.isEmpty()) throw errors.getParserException();

        return build(rowsToRemember);
    }

    private Examples build(List<TableRow> rows) {
        return exampleBuilder.withExamples(rows).build();
    }
}
