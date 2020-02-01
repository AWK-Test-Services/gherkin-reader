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
class EscapedPipesTest {
    private List<Step> steps;

    @BeforeAll
    void readFile() {
        LineReader lineReader = null;
        try {
            lineReader = getLineReader(DIRECTORY_GOOD + "escaped_pipes.feature");
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
        assertEquals("Escaped pipes", feature.getName());
        assertEquals("    The \\-character will be considered as an escape in table cell\n" +
                "    if it is followed by a |-character, a \\-character or an n.", feature.getDescription());

        assertNoBackground(feature);
        Scenario scenario = assertAndGetFirstScenario(feature);

        steps = scenario.getSteps();
        assertEquals(2, steps.size());
    }

    @Test
    void testDataTableWithSpecialCharacters() {
        Step scenarioStep = steps.get(0);
        assertEquals(GIVEN, scenarioStep.getKeyword());
        assertEquals("they have arrived", scenarioStep.getText());

        StepArgument stepArgument = scenarioStep.getArgument();
        assertNotNull(stepArgument);
        assertEquals(DataTable.class.getSimpleName(), stepArgument.getClass().getSimpleName());

        DataTable dataTable = (DataTable) stepArgument;
        assertEquals(2, dataTable.getRows().size());
        assertTableRow(dataTable, 0, "æ", "o");
        assertTableRow(dataTable, 1, "a", "ø");

    }

    @Test
    void testDataTableWithEscapedCharacters() {

        Step scenarioStep = steps.get(1);
        assertEquals(GIVEN, scenarioStep.getKeyword());
        assertEquals("they have arrived", scenarioStep.getText());

        StepArgument stepArgument = scenarioStep.getArgument();
        assertNotNull(stepArgument);
        assertEquals(DataTable.class.getSimpleName(), stepArgument.getClass().getSimpleName());

        DataTable dataTable = (DataTable) stepArgument;
        assertEquals(2, dataTable.getRows().size());
        assertTableRow(dataTable, 0, "|æ\\n", "\\o\no\\");
        assertTableRow(dataTable, 1, "\\|a\\\\n", "ø\\\nø\\");
    }
}
