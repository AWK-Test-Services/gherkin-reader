package com.awk.featr.gherkin.reader;

import com.awk.featr.gherkin.helper.GherkinLanguageConstants;
import com.awk.featr.gherkin.helper.ParserException;
import com.awk.featr.gherkin.model.GherkinLine;
import com.awk.featr.ast.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DocumentReader {

    public Document parse(LineReader reader, TokenMatcher tokenMatcher) {
        GherkinDialectProvider dialectProvider = new GherkinDialectProvider();
        String language = dialectProvider.getDefaultDialect().getLanguage();
        Feature feature = null;
        List<String> tagsToRemember = new ArrayList<>();
        StringBuilder comment = new StringBuilder();

        GherkinLine line = reader.read();
        while(line.isNotEOF()) {
            if (line.isNotEmpty()) {
                Logger.getLogger(DocumentReader.class.getName()).log(Level.INFO,line.getLineNr() + ": Processing line: " + line.getLineText());
                if (line.startsWith(GherkinLanguageConstants.COMMENT_PREFIX)) {
                    if (comment.length() > 0) comment.append("\n");
                    comment.append(tokenMatcher.getCommentLine(line));

                } else if (tokenMatcher.match_Language(line)) {
                    language = tokenMatcher.getLanguage(line);
                    tokenMatcher.setDialect( dialectProvider.getDialect(language, line.getLineNr()));

                } else if (tokenMatcher.match_FeatureLine(line)) {
                    String title = tokenMatcher.getFeatureTitle(line);
                    feature = new FeatureReader(title, tagsToRemember).parse(reader, tokenMatcher);
                    tagsToRemember = new ArrayList<>();

                } else if (tokenMatcher.match_TagLine(line)) {
                    List<String> tagsToAdd = tokenMatcher.getTags(line)
                            .stream()
                            .map(lineSpan -> lineSpan.text)
                            .collect(Collectors.toList());
                    tagsToRemember.addAll(tagsToAdd);

                } else {
                    // TODO throw when we have checked all valid tokens
                    Logger.getLogger(DocumentReader.class.getName()).log(Level.SEVERE,line.getLineNr() + ": No or unknown tag found: " + line.getLineText());
                }
            }

            line = reader.read();
        }

        if (feature == null) throw new ParserException("No Feature found in this file.");
        return new Document(UUID.randomUUID().toString(), feature, language, comment.toString());
    }

}
