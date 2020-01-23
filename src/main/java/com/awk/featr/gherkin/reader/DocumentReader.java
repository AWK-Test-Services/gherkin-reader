package com.awk.featr.gherkin.reader;

import com.awk.featr.gherkin.helper.CompositeParserException;
import com.awk.featr.gherkin.helper.GherkinLanguageConstants;
import com.awk.featr.gherkin.helper.ParserException;
import com.awk.featr.gherkin.model.GherkinLine;
import com.awk.featr.ast.*;
import com.awk.featr.gherkin.model.ParseEnvelope;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DocumentReader {

    public ParseEnvelope parse(LineReader reader, TokenMatcher tokenMatcher) {
        GherkinDialectProvider dialectProvider = new GherkinDialectProvider();
        String language = dialectProvider.getDefaultDialect().getLanguage();
        Feature feature = null;
        List<String> tagsToRemember = new ArrayList<>();
        StringBuilder comment = new StringBuilder();

        CompositeParserException errors = new CompositeParserException();

        GherkinLine line = reader.read();
        while(line.isNotEOF()) {
            try {
                if (line.isNotEmpty()) {
                    Logger.getLogger(DocumentReader.class.getName()).log(Level.INFO,line.getLineNr() + ": Processing line: " + line.getLineText());
                    if (line.startsWith(GherkinLanguageConstants.COMMENT_PREFIX)) {
                        if (comment.length() > 0) comment.append("\n");
                            comment.append(tokenMatcher.getCommentLine(line));

                    } else if (tokenMatcher.match_Language(line)) {
                        language = tokenMatcher.getLanguage(line);
                        tokenMatcher.setDialect( dialectProvider.getDialect(language, line));

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
                        throw new ParserException("No or unknown tag found", line);
                    }
                }

                line = reader.read();
            } catch (ParserException e) {
                errors.addError( e );
            }
        }

        if (feature == null) {
            return new ParseEnvelope(null, new CompositeParserException(new ParserException("No Feature found in this file.")));
        }
        Document document = new Document(UUID.randomUUID().toString(), feature, language, comment.toString());
        if (!errors.isEmpty()) {
            return new ParseEnvelope(document, errors);
        }
        return new ParseEnvelope(document);
    }

}
