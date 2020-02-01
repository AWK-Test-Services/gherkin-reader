package com.awk.featr.gherkin.reader;

import com.awk.featr.gherkin.helper.CompositeParserException;
import com.awk.featr.gherkin.helper.GherkinLanguageConstants;
import com.awk.featr.gherkin.helper.ParseLineException;
import com.awk.featr.gherkin.helper.ParserException;
import com.awk.featr.gherkin.model.GherkinLine;
import com.awk.featr.ast.*;
import com.awk.featr.ast.builder.StepBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class StepReader {
    private final StepBuilder stepBuilder;

    StepReader(String keyword, String stepTitle) {
        stepBuilder = new StepBuilder(keyword, stepTitle);
    }

    Step parse(LineReader reader, TokenMatcher tokenMatcher) throws CompositeParserException {
        List<TableRow> rowsToRemember = new ArrayList<>();
        StringBuilder docStringText = new StringBuilder();
        String docStringType = "";
        List<ParseLineException> exceptions = new ArrayList<>();

        GherkinLine line = reader.read();
        while(line.isNotEOF()) {
            try {
                if (line.isNotEmpty()) {
                    Logger.getLogger(StepReader.class.getName()).log(Level.INFO,line.getLineNr() + ": Processing line: " + line.getLineText());

                    if (tokenMatcher.match_DocStringSeparator(line)) {
                        if (tokenMatcher.isDocStringActive()) {
                            docStringType = tokenMatcher.getDocStringType(line);
                        } else {
                            DocString docString = new DocString(docStringText.toString(), docStringType);
                            stepBuilder.withStepArgument(docString);
                            docStringText = new StringBuilder();
                            docStringType = "";
                        }

                    } else if (tokenMatcher.isDocStringActive()) {
                        if (docStringText.length() > 0) docStringText.append("\n");
                        docStringText.append(line.getLineText());

                    } else if (line.startsWith(GherkinLanguageConstants.COMMENT_PREFIX)) {
                        //ignore

                    } else if (tokenMatcher.match_ScenarioDefinitionLine(line) ||
                            tokenMatcher.match_FeatureLine(line) ||
                            tokenMatcher.match_StepLine(line) ||
                            tokenMatcher.match_ExamplesLine(line)) {
                        return build(rowsToRemember);

                    } else if (tokenMatcher.match_TableRow(line)) {
                        List<String> cells = line.getTableCells();
                        TableRow row = new TableRow(cells);
                        rowsToRemember.add(row);

                    } else {
                        throw new ParseLineException("No or unknown tag found", line);
                    }
                }
            } catch ( ParseLineException e ) {
                exceptions.add(e);
            }

            line = reader.read();
        }

        if (!exceptions.isEmpty()) throw new CompositeParserException(exceptions);

        return build(rowsToRemember);
    }

    private Step build(List<TableRow> rows) {
        if ( !rows.isEmpty() ) {
            DataTable dataTable = new DataTable(rows);
            stepBuilder.withStepArgument(dataTable);
        }
        return stepBuilder.build();
    }
}
