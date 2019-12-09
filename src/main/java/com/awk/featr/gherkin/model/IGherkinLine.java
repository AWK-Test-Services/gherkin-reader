package com.awk.featr.gherkin.model;

import java.util.List;

public interface IGherkinLine {

    String getLineText(int indentToRemove);

    boolean isNotEmpty();

    boolean startsWith(String prefix);

    String getRestTrimmed(int length);

    List<GherkinLineSpan> getTags();

    boolean startsWithTitleKeyword(String keyword);

    List<String> getTableCells();
}
