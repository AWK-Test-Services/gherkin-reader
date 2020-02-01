package com.awk.featr.gherkin.reader;

import com.awk.featr.gherkin.helper.GherkinLanguageConstants;
import com.awk.featr.gherkin.helper.ParseLineException;
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

    boolean match_TagLine(GherkinLine line) {
        return line.startsWith(GherkinLanguageConstants.TAG_PREFIX);
    }

    List<GherkinLineSpan> getTags(GherkinLine line) throws ParseLineException {
        if (line.startsWith(GherkinLanguageConstants.TAG_PREFIX)) {
            return line.getTags();
        }
        throw new ParseLineException("No tag found", line);
    }

    String getCommentLine(GherkinLine line) throws ParseLineException {
        if (line.startsWith(GherkinLanguageConstants.COMMENT_PREFIX)) {
            return line.getRestTrimmed(GherkinLanguageConstants.COMMENT_PREFIX.length());
        }
        throw new ParseLineException("Not a comment line", line);
    }

    boolean match_FeatureLine(GherkinLine line) {
        for (String keyword : currentDialect.getFeatureKeywords()) {
            if (line.startsWithTitleKeyword(keyword) ) {
                return true;
            }
        }
        return false;
    }

    String getFeatureTitle(GherkinLine line) throws ParseLineException {
        for (String keyword : currentDialect.getFeatureKeywords()) {
            if (line.startsWithTitleKeyword(keyword)) {
                return line.getRestTrimmed(keyword.length() + GherkinLanguageConstants.TITLE_KEYWORD_SEPARATOR.length());
            }
        }
        throw new ParseLineException("Not a Feature title line", line);
    }

    boolean match_RuleLine(GherkinLine line) {
        for (String keyword : currentDialect.getRuleKeywords()) {
            if (line.startsWithTitleKeyword(keyword)) {
                return true;
            }
        }
        return false;
    }

    String getRule(GherkinLine line) throws ParseLineException {
        for (String keyword : currentDialect.getRuleKeywords()) {
            if (line.startsWithTitleKeyword(keyword)) {
                return line.getRestTrimmed(keyword.length() + GherkinLanguageConstants.TITLE_KEYWORD_SEPARATOR.length());
            }
        }
        throw new ParseLineException("Not a Rule line", line);
    }

    boolean match_BackgroundLine(GherkinLine line) {
        for (String keyword : currentDialect.getBackgroundKeywords()) {
            if (line.startsWithTitleKeyword(keyword)) {
                return true;
            }
        }
        return false;
    }

    String getBackgroundTitle(GherkinLine line) throws ParseLineException {
        for (String keyword : currentDialect.getBackgroundKeywords()) {
            if (line.startsWithTitleKeyword(keyword)) {
                return line.getRestTrimmed(keyword.length() + GherkinLanguageConstants.TITLE_KEYWORD_SEPARATOR.length());
            }
        }
        throw new ParseLineException("Not a Background line", line);
    }

    boolean match_ScenarioLine(GherkinLine line) {
        for (String keyword : currentDialect.getScenarioKeywords()) {
            if (line.startsWithTitleKeyword(keyword)) {
                return true;
            }
        }
        return false;
    }

    String getScenarioLine(GherkinLine line) throws ParseLineException {
        for (String keyword : currentDialect.getScenarioKeywords()) {
            if (line.startsWithTitleKeyword(keyword)) {
                return line.getRestTrimmed(keyword.length() + GherkinLanguageConstants.TITLE_KEYWORD_SEPARATOR.length());
            }
        }
        throw new ParseLineException("Not a Scenario line", line);
    }


    boolean match_ScenarioOutlineLine(GherkinLine line) {
        for (String keyword : currentDialect.getScenarioOutlineKeywords()) {
            if (line.startsWithTitleKeyword(keyword)) {
                return true;
            }
        }
        return false;
    }

    String getScenarioOutlineLine(GherkinLine line) throws ParseLineException {
        for (String keyword : currentDialect.getScenarioOutlineKeywords()) {
            if (line.startsWithTitleKeyword(keyword)) {
                return line.getRestTrimmed(keyword.length() + GherkinLanguageConstants.TITLE_KEYWORD_SEPARATOR.length());
            }
        }
        throw new ParseLineException("Not a ScenarioOutline line", line);
    }

    boolean match_ScenarioDefinitionLine(GherkinLine line) {
        return match_ScenarioLine(line) || match_ScenarioOutlineLine(line);
    }

    boolean match_ExamplesLine(GherkinLine line) {
        for (String keyword : currentDialect.getExamplesKeywords()) {
            if (line.startsWithTitleKeyword(keyword)) {
                return true;
            }
        }
        return false;
    }

    String getExampleKeyword(GherkinLine line) throws ParseLineException {
        for (String keyword : currentDialect.getExamplesKeywords()) {
            if (line.startsWithTitleKeyword(keyword)) {
                return keyword;
            }
        }
        throw new ParseLineException("Not an Examples line", line);
    }

    String getExamplesLine(GherkinLine line, String keyword) throws ParseLineException {
        if (line.startsWithTitleKeyword(keyword)) {
            return line.getRestTrimmed(keyword.length() + GherkinLanguageConstants.TITLE_KEYWORD_SEPARATOR.length());
        }
        throw new ParseLineException("Not a Examples line", line);
    }

    boolean match_StepLine(GherkinLine line) {
        List<String> keywords = currentDialect.getStepKeywords();
        for (String keyword : keywords) {
            if (line.startsWith(keyword)) {
                return true;
            }
        }
        return false;
    }

    String getStepKeyword(GherkinLine line) throws ParseLineException {
        List<String> keywords = currentDialect.getStepKeywords();
        for (String keyword : keywords) {
            if (line.startsWith(keyword)) {
                return keyword;
            }
        }
        throw new ParseLineException("Not a Step line", line);
    }

    String getStepText(GherkinLine line, String keyword) throws ParseLineException {
        if (line.startsWith(keyword)) {
            return line.getRestTrimmed(keyword.length());
        }
        throw new ParseLineException("Line does not start with '" + keyword + "'", line);
    }

    boolean match_DocStringSeparator(GherkinLine line) {
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

    String getDocStringType(GherkinLine line) throws ParseLineException {
        if (activeDocStringSeparator != null && line.startsWith(activeDocStringSeparator)) {
            return line.getRestTrimmed(activeDocStringSeparator.length());
        }

        throw new ParseLineException("Not a DocString opening line", line);
    }

    boolean isDocStringActive() {
        return activeDocStringSeparator != null;
    }

    boolean match_TableRow(GherkinLine line) {
        return line.startsWith(GherkinLanguageConstants.TABLE_CELL_SEPARATOR);
    }

    String getOther(GherkinLine line) {
        return line.getLineText(indentToRemove); //take the entire line, except removing DocString indents
    }

    boolean match_Language(GherkinLine line) {
        Matcher matcher = LANGUAGE_PATTERN.matcher(line.getLineText());
        return matcher.matches();
    }

    String getLanguage(GherkinLine line) throws ParseLineException {
        Matcher matcher = LANGUAGE_PATTERN.matcher(line.getLineText());
        if (matcher.matches()) {
            return matcher.group(1);
        }
        throw new ParseLineException("Not a language line", line);
    }

    void setDialect(GherkinDialect dialect) {
        currentDialect = dialect;
    }
}
