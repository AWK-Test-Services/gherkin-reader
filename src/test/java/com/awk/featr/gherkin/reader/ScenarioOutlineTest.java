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
class ScenarioOutlineTest {
    private List<Step> steps;

    @BeforeAll
    void readFile() {
        LineReader lineReader = null;
        try {
            lineReader = getLineReader(DIRECTORY_GOOD + "scenario_outline.feature");
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
        assertEquals("Minimal Scenario Outline", feature.getName());

        assertNoBackground(feature);
        ScenarioOutline scenarioOutline = assertAndGetFirstScenarioOutline(feature);

        steps = scenarioOutline.getSteps();
        assertEquals(1, steps.size());

        Step scenarioStep = steps.get(0);
        assertEquals(GIVEN, scenarioStep.getKeyword());
        assertEquals("the <what>", scenarioStep.getText());

        Examples examples = scenarioOutline.getExamples();
        assertNotNull(examples);

        TableRow header = examples.getHeader();
        assertNotNull(header);
        List<String> headerValues = header.getValues();
        assertEquals(1, headerValues.size());
        assertEquals("what", headerValues.get(0));

        List<TableRow> rows = examples.getExamples();
        assertNotNull(rows);
        assertEquals(1, rows.size());

        TableRow row = rows.get(0);
        List<String> rowValues = row.getValues();
        assertEquals(1, rowValues.size());
        assertEquals("minimalism", rowValues.get(0));
    }

    @Test
    void testSimpleDocString() {

    }
}
