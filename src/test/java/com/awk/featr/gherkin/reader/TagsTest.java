package com.awk.featr.gherkin.reader;

import com.awk.featr.ast.Document;
import com.awk.featr.ast.Feature;
import com.awk.featr.ast.GherkinError;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.List;

import static com.awk.featr.gherkin.reader.helpers.Utils.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TagsTest {
    private Feature feature;

    @BeforeAll
    void readFile() {
        LineReader lineReader = null;
        try {
            lineReader = getLineReader(DIRECTORY_GOOD + "tags.feature");
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
    void testFeatureTags() {
        List<String> tags = feature.getTags();
        assertEquals(3, tags.size());
    }

//    @Test
//    void testScenarioTags() {
//        assertEquals("test-id", feature.getFeatureId());
//    }
//
//    @Test
//    void testScenarioOutlineTags() {
//        assertEquals("test-id", feature.getFeatureId());
//    }
//
//    @Test
//    void testExamplesTags() {
//        assertEquals("test-id", feature.getFeatureId());
//    }

}
