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
class IncompleteBackgroundTest1 {
    private List<Step> steps;

    // TODO combine 2 Incomplete Background tests with normal BackgroundTest

    @BeforeAll
    void readFile() {
        LineReader lineReader = null;
        try {
            lineReader = getLineReader(DIRECTORY_GOOD + "incomplete_background_1.feature");
        } catch (IOException e) {
            e.printStackTrace();
            fail("Could not read test-file");
        }

        DocumentReader reader = new DocumentReader();
        Document document = reader.parse(lineReader, TOKEN_MATCHER_EN);
        assertNotNull(document);

        List<GherkinError> errors = document.getErrors();
        assertTrue(errors.isEmpty());

        Feature feature = document.getFeature();
        assertNotNull(feature);
        assertEquals("Incomplete backgrounds, Part 1", feature.getName());

//        assertNoBackground(feature);
        Scenario scenario = assertAndGetFirstScenario(feature);

        steps = scenario.getSteps();
        assertEquals(1, steps.size());
    }

    @Test
    void testSimpleDocString() {

    }
}
