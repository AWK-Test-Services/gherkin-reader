package com.awk.featr.gherkin.reader;

import com.awk.featr.ast.Document;
import com.awk.featr.ast.GherkinError;
import com.awk.featr.ast.Step;
import com.awk.featr.gherkin.helper.CompositeParserException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.List;

import static com.awk.featr.gherkin.reader.helpers.Utils.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class I18nFrTest {
    private List<Step> steps;

    // TODO combine 3 i18n tests with Language Test

    @BeforeAll
    void readFile() {
        LineReader lineReader = null;
        try {
            lineReader = getLineReader(DIRECTORY_GOOD + "i18n_fr.feature");
        } catch (IOException e) {
            e.printStackTrace();
            fail("Could not read test-file");
        }

        DocumentReader reader = new DocumentReader();
        Document document = reader.parse(lineReader, TOKEN_MATCHER_EN);
        assertNotNull(document);

        List<GherkinError> errors = document.getErrors();
//        assertTrue(errors.isEmpty());
//
//        Feature feature = document.getFeature();
//        assertNotNull(feature);
//        assertEquals("i18n support", feature.getName());
//
//        assertNoBackground(feature);
    }

    @Test
    void testSimpleDocString() {

    }
}
