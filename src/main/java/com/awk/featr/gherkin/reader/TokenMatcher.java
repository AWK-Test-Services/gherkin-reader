package com.awk.featr.gherkin.reader;

import com.awk.featr.gherkin.helper.GherkinLanguageConstants;
import com.awk.featr.gherkin.model.GherkinDialect;
import com.awk.featr.gherkin.model.GherkinLine;
import com.awk.featr.gherkin.model.GherkinLineSpan;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

public class TokenMatcher {

    private static final Pattern LANGUAGE_PATTERN = compile("^\\s*#\\s*language\\s*:\\s*([a-zA-Z\\-_]+)\\s*$");
    private GherkinDialect currentDialect;
    private String activeDocStringSeparator = null;
    private int indentToRemove;

    public TokenMatcher(GherkinDialect dialect) {
        currentDialect = dialect;
        indentToRemove = 0;
    }

    public boolean match_TagLine(GherkinLine line) {
        return line.startsWith(GherkinLanguageConstants.TAG_PREFIX);
    }

    public List<GherkinLineSpan> getTags(GherkinLine line) {
        if (line.startsWith(GherkinLanguageConstants.TAG_PREFIX)) {
            return line.getTags();
        }
        throw new IllegalArgumentException("Line (" + line.getLineNr() + ") does not contain tags: " + line.getLineText());
    }

    public String getCommentLine(GherkinLine line)
    {
        if (line.startsWith(GherkinLanguageConstants.COMMENT_PREFIX)) {
            return line.getRestTrimmed(GherkinLanguageConstants.COMMENT_PREFIX.length());
        }
        throw new IllegalArgumentException("Line (" + line.getLineNr() + ") is not a comment line: " + line.getLineText());
    }

    public boolean match_FeatureLine(GherkinLine line) {
        for (String keyword : currentDialect.getFeatureKeywords()) {
            if (line.startsWithTitleKeyword(keyword) ) {
                return true;
            }
        }
        return false;
    }

    public String getFeatureTitle(GherkinLine line) {
        for (String keyword : currentDialect.getFeatureKeywords()) {
            if (line.startsWithTitleKeyword(keyword)) {
                return line.getRestTrimmed(keyword.length() + GherkinLanguageConstants.TITLE_KEYWORD_SEPARATOR.length());
            }
        }
        throw new IllegalArgumentException("Line (" + line.getLineNr() + ") is not a Feature title line: " + line.getLineText());
    }

    public boolean match_RuleLine(GherkinLine line) {
        for (String keyword : currentDialect.getRuleKeywords()) {
            if (line.startsWithTitleKeyword(keyword)) {
                return true;
            }
        }
        return false;
    }

    public String getRule(GherkinLine line) {
        for (String keyword : currentDialect.getRuleKeywords()) {
            if (line.startsWithTitleKeyword(keyword)) {
                return line.getRestTrimmed(keyword.length() + GherkinLanguageConstants.TITLE_KEYWORD_SEPARATOR.length());
            }
        }
        throw new IllegalArgumentException("Line (" + line.getLineNr() + ") is not a Rule line: " + line.getLineText());
    }

    public boolean match_BackgroundLine(GherkinLine line) {
        for (String keyword : currentDialect.getBackgroundKeywords()) {
            if (line.startsWithTitleKeyword(keyword)) {
                return true;
            }
        }
        return false;
    }

    public String getBackgroundTitle(GherkinLine line) {
        for (String keyword : currentDialect.getBackgroundKeywords()) {
            if (line.startsWithTitleKeyword(keyword)) {
                return line.getRestTrimmed(keyword.length() + GherkinLanguageConstants.TITLE_KEYWORD_SEPARATOR.length());
            }
        }
        throw new IllegalArgumentException("Line (" + line.getLineNr() + ") is not a Background line: " + line.getLineText());
    }

    public boolean match_ScenarioLine(GherkinLine line) {
        for (String keyword : currentDialect.getScenarioKeywords()) {
            if (line.startsWithTitleKeyword(keyword)) {
                return true;
            }
        }
        return false;
    }

    public String getScenarioLine(GherkinLine line) {
        for (String keyword : currentDialect.getScenarioKeywords()) {
            if (line.startsWithTitleKeyword(keyword)) {
                return line.getRestTrimmed(keyword.length() + GherkinLanguageConstants.TITLE_KEYWORD_SEPARATOR.length());
            }
        }
        throw new IllegalArgumentException("Line (" + line.getLineNr() + ") is not a Scenario line: " + line.getLineText());
    }


    public boolean match_ScenarioOutlineLine(GherkinLine line) {
        for (String keyword : currentDialect.getScenarioOutlineKeywords()) {
            if (line.startsWithTitleKeyword(keyword)) {
                return true;
            }
        }
        return false;
    }

    public String getScenarioOutlineLine(GherkinLine line) {
        for (String keyword : currentDialect.getScenarioOutlineKeywords()) {
            if (line.startsWithTitleKeyword(keyword)) {
                return line.getRestTrimmed(keyword.length() + GherkinLanguageConstants.TITLE_KEYWORD_SEPARATOR.length());
            }
        }
        throw new IllegalArgumentException("Line (" + line.getLineNr() + ") is not a ScenarioOutline line: " + line.getLineText());
    }

    public boolean match_ScenarioDefinitionLine(GherkinLine line) {
        return match_ScenarioLine(line) || match_ScenarioOutlineLine(line);
    }

    public boolean match_ExamplesLine(GherkinLine line) {
        for (String keyword : currentDialect.getExamplesKeywords()) {
            if (line.startsWithTitleKeyword(keyword)) {
                return true;
            }
        }
        return false;
    }

    public String getExampleKeyword(GherkinLine line) {
        for (String keyword : currentDialect.getExamplesKeywords()) {
            if (line.startsWithTitleKeyword(keyword)) {
                return keyword;
            }
        }
        throw new IllegalArgumentException("Line (" + line.getLineNr() + ") is not an Examples line: " + line.getLineText());
    }

    public String getExamplesLine(GherkinLine line, String keyword) {
        if (line.startsWithTitleKeyword(keyword)) {
            return line.getRestTrimmed(keyword.length() + GherkinLanguageConstants.TITLE_KEYWORD_SEPARATOR.length());
        }
        throw new IllegalArgumentException("Line (" + line.getLineNr() + ") is not an Examples line: " + line.getLineText());
    }

    public boolean match_StepLine(GherkinLine line) {
        List<String> keywords = currentDialect.getStepKeywords();
        for (String keyword : keywords) {
            if (line.startsWith(keyword)) {
                return true;
            }
        }
        return false;
    }

    public String getStepKeyword(GherkinLine line) {
        List<String> keywords = currentDialect.getStepKeywords();
        for (String keyword : keywords) {
            if (line.startsWith(keyword)) {
                return keyword;
            }
        }
        throw new IllegalArgumentException("Line (" + line.getLineNr() + ") is not a Step line: " + line.getLineText());
    }

    public String getStepText(GherkinLine line, String keyword) {
        if (line.startsWith(keyword)) {
            return line.getRestTrimmed(keyword.length());
        }
        throw new IllegalArgumentException("Line (" + line.getLineNr() + ") did not start with '" + keyword + "': " + line.getLineText());
    }

    public boolean match_DocStringSeparator(GherkinLine line) {
        return activeDocStringSeparator == null
                // open
                ? match_DocStringSeparator(line, GherkinLanguageConstants.DOCSTRING_SEPARATOR, true) ||
                match_DocStringSeparator(line, GherkinLanguageConstants.DOCSTRING_ALTERNATIVE_SEPARATOR, true)
                // close
                : match_DocStringSeparator(line, activeDocStringSeparator, false);
    }

    private boolean match_DocStringSeparator(GherkinLine line, String separator, boolean isOpen) {
        if (line.startsWith(separator)) {
            if (isOpen) {
                activeDocStringSeparator = separator;
                indentToRemove = 0; //line.indent();
            } else {
                activeDocStringSeparator = null;
                indentToRemove = 0;
            }

            return true;
        }
        return false;
    }

    public String getDocStringType(GherkinLine line) {
        if (activeDocStringSeparator != null && line.startsWith(activeDocStringSeparator)) {
            return line.getRestTrimmed(activeDocStringSeparator.length());
        }

        throw new IllegalArgumentException("Line (" + line.getLineNr() + ") is not a DocString opening line: " + line.getLineText());
    }

    public boolean isDocStringActive() {
        return activeDocStringSeparator != null;
    }

    public boolean match_TableRow(GherkinLine line) {
        return line.startsWith(GherkinLanguageConstants.TABLE_CELL_SEPARATOR);
    }

    public String getOther(GherkinLine line) {
        return line.getLineText(indentToRemove); //take the entire line, except removing DocString indents
    }

    public boolean match_Language(GherkinLine line) {
        Matcher matcher = LANGUAGE_PATTERN.matcher(line.getLineText());
        return matcher.matches();
    }

    public String getLanguage(GherkinLine line) {
        Matcher matcher = LANGUAGE_PATTERN.matcher(line.getLineText());
        if (matcher.matches()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("Line (" + line.getLineNr() + ") is not a language line: " + line.getLineText());
    }

    public void setDialect(GherkinDialect dialect) {
        currentDialect = dialect;
    }
}
