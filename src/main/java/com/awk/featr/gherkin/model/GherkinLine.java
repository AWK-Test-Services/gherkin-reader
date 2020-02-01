package com.awk.featr.gherkin.model;

import com.awk.featr.gherkin.helper.GherkinLanguageConstants;

import java.util.*;

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
        List<String> cells = new ArrayList<>();
        StringBuilder cellBuilder = new StringBuilder();
        boolean beforeFirst = true;
        boolean escape = false;
        PrimitiveIterator.OfInt iterator = lineText.codePoints().iterator();
        while (iterator.hasNext() ) {
            int c = iterator.next() ;
            if (escape) {
                switch (c) {
                    case 'n':
                        cellBuilder.append('\n');
                        break;
                    case '\\':
                        cellBuilder.append('\\');
                        break;
                    case '|':
                        cellBuilder.append('|');
                        break;
                    default:
                        // Invalid escape. We'll just ignore it.
                        cellBuilder.append("\\");
                        cellBuilder.appendCodePoint(c);
                        break;
                }
                escape = false;
            } else {
                if (c == '\\') {
                    escape = true;
                } else if (c == '|') {
                    if (beforeFirst) {
                        // Skip the first empty span
                        beforeFirst = false;
                    } else {
                        String cell = cellBuilder.toString();
                        String leftTrimmedCell = ltrim(cell);
                        cells.add(rtrim(leftTrimmedCell));
                    }
                    cellBuilder = new StringBuilder();
                } else {
                    cellBuilder.appendCodePoint(c);
                }
            }
        }
        return cells;
    }

}
