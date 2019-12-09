package com.awk.featr.gherkin.model;

import com.awk.featr.gherkin.helper.GherkinLanguageConstants;
import com.awk.featr.gherkin.helper.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

import static com.awk.featr.gherkin.helper.StringUtils.*;

public class GherkinLine implements IGherkinLine {
    private final int lineNr;
    private final String lineText;
    private final String trimmedLineText;

    public GherkinLine( int lineNr, String lineText ) {
        this.lineNr = lineNr;
        this.lineText = lineText;
        this.trimmedLineText = lineText != null ? trim(lineText) : "";
    }

    public int getLineNr() {
        return lineNr;
    }

    public String getLineText() {
        return lineText;
    }

    public String getLineText(int indentToRemove) {
        if (indentToRemove < 0)
            return trimmedLineText;
        return lineText.substring(indentToRemove);
    }

    public String getRestTrimmed(int length) {
        return trimmedLineText.substring(length).trim();
    }

    @Override
    public List<GherkinLineSpan> getTags() {
        List<GherkinLineSpan> lineSpans = new ArrayList<>();
        Scanner scanner = new Scanner(trimmedLineText).useDelimiter("\\s+");
        while (scanner.hasNext()) {
            String cell = scanner.next();
            String leftTrimmedCell = ltrim(cell);
            int cellIndent = symbolCount(cell) - symbolCount(leftTrimmedCell);

            String trimmedCell = rtrim(leftTrimmedCell);
            int scannerStart = scanner.match().start();
            int symbolLength = trimmedLineText.codePointCount(0, scannerStart);
            int column = 1 + symbolLength + cellIndent;
            lineSpans.add(new GherkinLineSpan(column, trimmedCell));
        }
        return lineSpans;
    }

    public boolean isNotEOF() {
        return lineText != null && lineNr <= 10000;
    }

    public boolean isNotEmpty() {
        return trimmedLineText.length() != 0;
    }

    public boolean startsWith(String prefix) {
        return trimmedLineText.startsWith(prefix);
    }

    public boolean startsWithTitleKeyword(String text) {
        int textLength = text.length();
        return trimmedLineText.length() > textLength &&
                trimmedLineText.startsWith(text) &&
                trimmedLineText.substring(textLength, textLength + GherkinLanguageConstants.TITLE_KEYWORD_SEPARATOR.length())
                        .equals(GherkinLanguageConstants.TITLE_KEYWORD_SEPARATOR);
    }

    public List<String> getTableCells() {
        return Arrays.stream(trimmedLineText.split("\\|"))
                .skip(1)
                .map(StringUtils::trim)
                .collect(Collectors.toList());
    }
}
