package com.awk.featr.gherkin.reader;

import com.awk.featr.gherkin.model.GherkinLine;

import static java.util.Objects.requireNonNull;

public class LineReader {

    private final String[] srcLines;
    private int lineNumber;
    private String lastReadLine;

    public LineReader(String source) {
        this.srcLines = requireNonNull(source).split("\\R");
    }

    public GherkinLine read() {
        ++lineNumber;
        if ( srcLines.length > lineNumber ) {
            lastReadLine = srcLines[lineNumber];
            return new GherkinLine(lineNumber, lastReadLine);
        }
        lastReadLine = null;
        return new GherkinLine(lineNumber, null);
    }

    GherkinLine getLastReadLine() {
        return new GherkinLine(lineNumber, lastReadLine);
    }
}
