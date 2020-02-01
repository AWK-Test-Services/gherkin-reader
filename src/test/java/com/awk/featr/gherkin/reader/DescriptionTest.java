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
class DescriptionTest {

    private List<ScenarioDefinition> scenarioDefinitions;

    @BeforeAll
    void readFile() {
        LineReader lineReader = null;
        try {
            lineReader = getLineReader(DIRECTORY_GOOD + "descriptions.feature");
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
        assertEquals("Descriptions everywhere", feature.getName());
        assertEquals("  This is a single line description", feature.getDescription());

        assertNoBackground(feature);

        scenarioDefinitions = feature.getScenarioDefinitions();
        assertNotNull(scenarioDefinitions);
        assertEquals(8, scenarioDefinitions.size());
    }

    @Test
    void testDescriptionOnTwoLines() {
        assertScenario(scenarioDefinitions, 0, "two lines",
                "  This description\n" +
                "  has two lines and indented with two spaces");
    }

    @Test
    void testDescriptionWithoutIndentation() {
        assertScenario(scenarioDefinitions, 1, "without indentation",
                "This is a description without indentation");
    }

    @Test
    void testDescriptionWithEmptyLineInBetween() {
        assertScenario(scenarioDefinitions, 2, "empty lines in the middle",
                "  This description\n" +
//bug                "\n" +
                "  has an empty line in the middle");
    }

    @Test
    void testDescriptionWithEmptyLinesAround() {
        assertScenario(scenarioDefinitions, 3, "empty lines around",
                "  This description\n" +
                "  has an empty lines around");
    }

    @Test
    void testDescriptionWithCommentAfterDescription() {
        assertScenario(scenarioDefinitions, 4, "comment after description",
                "  This description\n" +
                "  has a comment after");
    }

    @Test
    void testDescriptionWithCommentRightAfterDescription() {
        assertScenario(scenarioDefinitions, 5,
                "comment right after description",
                "  This description\n" +
                "  has a comment right after");
    }

    @Test
    void testDescriptionWithEscapedDocstring() {
        assertScenario(scenarioDefinitions, 6,
                "description with escaped docstring separator",
                "  This description has an \\\"\\\"\\\" (escaped docstring sparator)");
    }

    @Test
    void testDescriptionInScenarioOutline() {
        ScenarioDefinition scenarioDefinition8 = scenarioDefinitions.get(7);
        assertEquals(ScenarioOutline.class.getSimpleName(), scenarioDefinition8.getType());

        ScenarioOutline scenarioOutline = (ScenarioOutline) scenarioDefinition8;
        assertEquals("scenario outline with a description", scenarioOutline.getName());
        assertEquals("This is a scenario outline description", scenarioOutline.getDescription());

        List<Step> steps = scenarioOutline.getSteps();
        assertEquals(1, steps.size());

        Examples examples = scenarioOutline.getExamples();
        assertEquals("This is an examples description", examples.getDescription());
        assertEquals(1, examples.getHeader().getValues().size());
        assertEquals("foo", examples.getHeader().getValues().get(0));
        assertEquals(1, examples.getExamples().size());
        assertEquals(1, examples.getExamples().get(0).getValues().size());
        assertEquals("bar", examples.getExamples().get(0).getValues().get(0));
    }


    private void assertScenario(List<ScenarioDefinition> scenarioDefinitions, int scenarioNr, String expectedScenarioName, String expectedDescription) {
        ScenarioDefinition scenarioDefinition = scenarioDefinitions.get(scenarioNr);
        assertEquals(Scenario.class.getSimpleName(), scenarioDefinition.getType());

        Scenario scenario = (Scenario) scenarioDefinition;
        assertEquals(expectedScenarioName, scenario.getName());
        assertEquals(expectedDescription, scenario.getDescription());

        List<Step> steps = scenario.getSteps();
        assertEquals(1, steps.size());
    }
}
