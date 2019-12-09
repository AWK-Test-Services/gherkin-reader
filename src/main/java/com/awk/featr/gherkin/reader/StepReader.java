package com.awk.featr.gherkin.reader;

import com.awk.featr.gherkin.helper.GherkinLanguageConstants;
import com.awk.featr.gherkin.model.GherkinLine;
import com.awk.featr.ast.*;
import com.awk.featr.ast.builder.StepBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StepReader {
    private final StepBuilder stepBuilder;

    StepReader(String keyword, String stepTitle) {
        stepBuilder = new StepBuilder(keyword, stepTitle);
    }

    public Step parse(LineReader reader, TokenMatcher tokenMatcher) {
        List<TableRow> rowsToRemember = new ArrayList<>();
        StringBuilder docStringText = new StringBuilder();
        String docStringType = "";

        GherkinLine line = reader.read();
        while(line.isNotEOF()) {
            if (line.isNotEmpty()) {
                Logger.getLogger(StepReader.class.getName()).log(Level.INFO,line.getLineNr() + ": Processing line: " + line.getLineText());

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
                    rowsToRemember.add(row);

                } else if (tokenMatcher.match_DocStringSeparator(line)) {
                    if (tokenMatcher.isDocStringActive()) {
                        docStringType = tokenMatcher.getDocStringType(line);
                    } else {
                        DocString docString = new DocString(docStringText.toString(), docStringType);
                        stepBuilder.withStepArgument(docString);
                        docStringText = new StringBuilder();
                        docStringType = "";

                    }
                } else {
                    if (tokenMatcher.isDocStringActive()) {
                        if (docStringText.length() > 0) docStringText.append("\n");
                        docStringText.append(line);
                    } else {
                        // TODO throw when we have checked all valid tokens
                        Logger.getLogger(BackgroundReader.class.getName()).log(Level.SEVERE,line.getLineNr() + ": Line is not detected as relevant for a Step: " + line.getLineText());
                    }
                }
            }

            line = reader.read();
        }

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
