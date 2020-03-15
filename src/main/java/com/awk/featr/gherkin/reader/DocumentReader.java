package com.awk.featr.gherkin.reader;

import com.awk.featr.ast.builder.DocumentBuilder;
import com.awk.featr.gherkin.helper.*;
import com.awk.featr.gherkin.model.GherkinLine;
import com.awk.featr.ast.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class DocumentReader {

    public Document parse(LineReader reader, TokenMatcher tokenMatcher) {
        GherkinDialectProvider dialectProvider = new GherkinDialectProvider();
        List<String> tagsToRemember = new ArrayList<>();
        StringBuilder comment = new StringBuilder();
        DocumentBuilder documentBuilder = new DocumentBuilder(dialectProvider.getDefaultDialect().getLanguage());

        GherkinLine line = reader.read();
        while(line.isNotEOF()) {
            try {
                if (line.isNotEmpty()) {
                    Logger.getLogger(DocumentReader.class.getName()).log(Level.INFO, line.getLineNr() + ": Processing line: " + line.getLineText());
                    if (line.startsWith(GherkinLanguageConstants.COMMENT_PREFIX)) {
                        if (comment.length() > 0) comment.append("\n");
                        comment.append(tokenMatcher.getCommentLine(line));

                    } else if (tokenMatcher.match_Language(line)) {
                        String language = tokenMatcher.getLanguage(line);
                        tokenMatcher.setDialect(dialectProvider.getDialect(language, line));
                        documentBuilder.withLanguage(language);

                    } else if (tokenMatcher.match_FeatureLine(line)) {
                        String title = tokenMatcher.getFeatureTitle(line);
                        FeatureReader featureReader = new FeatureReader(title, tagsToRemember);
                        Feature feature = featureReader.parse(reader, tokenMatcher);

                        if ( !featureReader.getUsedImages().isEmpty() ) {
                            documentBuilder.withUsedImages(featureReader.getUsedImages());
                            featureReader.clearUsedImages();
                        }
                        documentBuilder.withFeature(feature);
                        tagsToRemember = new ArrayList<>();

                    } else if (tokenMatcher.match_TagLine(line)) {
                        List<String> tagsToAdd = tokenMatcher.getTags(line)
                                .stream()
                                .map(lineSpan -> lineSpan.text)
                                .collect(Collectors.toList());
                        tagsToRemember.addAll(tagsToAdd);

                    } else {
                        throw new ParseLineException("No or unknown tag found", line);
                    }
                }
            } catch (ParseLineException e) {
                documentBuilder.addError(new GherkinLineError(e.getMessage(), e.getLine()));
            } catch (CompositeParserException e) {
                e.getExceptions().forEach(
                        exc -> documentBuilder.addError(new GherkinLineError(exc.getMessage(), exc.getLine()))
                );
            } catch (Exception e) {
                documentBuilder.addException(e);
            }

            line = reader.read();
        }

        return documentBuilder
                .withComment(comment.toString())
                .build();
    }
}
