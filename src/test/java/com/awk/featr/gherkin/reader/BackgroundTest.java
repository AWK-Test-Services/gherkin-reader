package com.awk.featr.gherkin.reader;

import com.awk.featr.ast.*;
import com.awk.featr.gherkin.helper.CompositeParserException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.awk.featr.gherkin.reader.helpers.Utils.*;
import static org.junit.jupiter.api.Assertions.*;

class BackgroundTest {

    @Test
    void testBackground() throws Exception {
        LineReader lineReader = getLineReader(DIRECTORY_GOOD + "background.feature");

        DocumentReader reader = new DocumentReader();
        Document document = reader.parse(lineReader, TOKEN_MATCHER_EN);
        assertNotNull(document);

        List<GherkinError> errors = document.getErrors();
        assertTrue(errors.isEmpty());

        Feature feature = document.getFeature();
        assertNotNull(feature);
        assertEquals("Background", feature.getName());

        Background background = feature.getBackground();
        assertNotNull(background);
        assertEquals("a simple background", background.getName());
        assertEquals(1, background.getSteps().size());

        Step backgroundStep = background.getSteps().get(0);
        assertNotNull(backgroundStep);
        assertEquals(GIVEN, backgroundStep.getKeyword());
        assertEquals("the minimalism inside a background", backgroundStep.getText());

        Scenario scenario = assertAndGetFirstScenario(feature);

        List<Step> steps = scenario.getSteps();
        assertEquals(1, steps.size());

        Step scenarioStep = steps.get(0);
        assertEquals(GIVEN, scenarioStep.getKeyword());
        assertEquals("the minimalism", scenarioStep.getText());
    }
}
