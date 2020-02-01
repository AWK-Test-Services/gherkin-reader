package com.awk.featr.gherkin.reader;

import com.awk.featr.gherkin.model.GherkinLine;

import static java.util.Objects.requireNonNull;

public class LineReader {

    private final String[] srcLines;
    private int lineNumber = -1;
    private String lastReadLine;

    private int sameLineNumberReadCount;

    public LineReader(String source) {
        this.srcLines = requireNonNull(source).split("\\R");
    }

    GherkinLine read() {
        lineNumber++;
        if ( srcLines.length > lineNumber ) {
            lastReadLine = srcLines[lineNumber];
        } else {
            lastReadLine = null;
        }
        sameLineNumberReadCount = 0;
        return new GherkinLine(lineNumber+1, lastReadLine);
    }

    GherkinLine getLastReadLine() {
        if (++sameLineNumberReadCount > 4) return read();
        return new GherkinLine(lineNumber+1, lastReadLine);
    }
}
