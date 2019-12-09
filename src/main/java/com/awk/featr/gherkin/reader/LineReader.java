package com.awk.featr.gherkin.reader;

import com.awk.featr.gherkin.model.GherkinLine;

import static java.util.Objects.requireNonNull;

//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.Reader;
//import java.io.StringReader;
//import java.util.Arrays;
//import java.util.List;

public class LineReader {

//    private final BufferedReader reader;
    private final String[] srcLines;
    private int lineNumber;
    private String lastReadLine;

    public LineReader(String source) {
//        this(new StringReader(requireNonNull(source)));
        this.srcLines = requireNonNull(source).split("\\R");
    }

//    private LineReader(Reader source) {
//        this.reader = new BufferedReader(source);
//    }

    public GherkinLine read() {
        ++lineNumber;
        if ( srcLines.length >= lineNumber ) {
            lastReadLine = srcLines[lineNumber];
            return new GherkinLine(lineNumber, lastReadLine);
        }
        return new GherkinLine(lineNumber, null);
    }

    GherkinLine getLastReadLine() {
        return new GherkinLine(lineNumber, lastReadLine);
    }
}
