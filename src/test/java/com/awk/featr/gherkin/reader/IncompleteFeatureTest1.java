package com.awk.featr.gherkin.reader;

import com.awk.featr.ast.*;
import com.awk.featr.gherkin.helper.CompositeParserException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.List;

import static com.awk.featr.gherkin.reader.helpers.Utils.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class IncompleteFeatureTest1 {
    private Feature feature;

    // TODO combine 3 Incomplete Feature tests

    @BeforeAll
    void readFile() {
        LineReader lineReader = null;
        try {
            lineReader = getLineReader(DIRECTORY_GOOD + "incomplete_feature_1.feature");
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
        assertEquals("Just a description", feature.getName());

        assertNoBackground(feature);
    }
    @Test
    void testIncompleteFeatureHasDescription() {
        assertEquals("  A short description", feature.getDescription());
    }

    @Test
    void testIncompleteFeatureWithoutScenarios() {
        List<ScenarioDefinition> scenarioDefinitions = feature.getScenarioDefinitions();
        assertNotNull(scenarioDefinitions);
        assertEquals(0, scenarioDefinitions.size());
    }
}
