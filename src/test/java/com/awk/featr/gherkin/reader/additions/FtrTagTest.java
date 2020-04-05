package com.awk.featr.gherkin.reader.additions;

import com.awk.featr.ast.Document;
import com.awk.featr.ast.Feature;
import com.awk.featr.ast.GherkinError;
import com.awk.featr.ast.Image;
import com.awk.featr.gherkin.reader.DocumentReader;
import com.awk.featr.gherkin.reader.LineReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.List;

import static com.awk.featr.gherkin.reader.helpers.Utils.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FtrTagTest {
    private Feature feature;

    @BeforeAll
    void readFile() {
        LineReader lineReader = null;
        try {
            lineReader = getLineReader(DIRECTORY_ADDITIONS + "ftr-tags.feature");
        } catch (IOException e) {
            e.printStackTrace();
            fail("Could not read test-file");
        }

        DocumentReader reader = new DocumentReader();
        Document document = reader.parse(lineReader, TOKEN_MATCHER_EN);
        assertNotNull(document);

        List<GherkinError> errors = document.getErrors();
        assertTrue(errors.isEmpty());

        feature = document.getFeature();
        assertNotNull(feature);
        assertNoBackground(feature);
    }

    @Test
    void testId() {
        assertEquals("test-id", feature.getFeatureId());
    }

    @Test
    void testParent() {
        assertEquals("test-parent", feature.getParentId());
    }
}
