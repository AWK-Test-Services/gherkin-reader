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
class DataTableTest {

    private List<Step> steps;

    @BeforeAll
    void readFile() {
        LineReader lineReader = null;
        try {
            lineReader = getLineReader(DIRECTORY_GOOD + "datatables.feature");
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
        assertEquals("DataTables", feature.getName());

        assertNoBackground(feature);
        Scenario scenario = assertAndGetFirstScenario(feature);

        steps = scenario.getSteps();
        assertEquals(5, steps.size());
    }

    @Test
    void testSimpleDataTable() {
        Step scenarioStep = steps.get(0);
        assertEquals(GIVEN, scenarioStep.getKeyword());
        assertEquals("a simple data table", scenarioStep.getText());

        StepArgument stepArgument = scenarioStep.getArgument();
        assertNotNull(stepArgument);
        assertEquals(DataTable.class.getSimpleName(), stepArgument.getClass().getSimpleName());

        DataTable dataTable = (DataTable) stepArgument;
        assertEquals(2, dataTable.getRows().size());
        assertTableRow(dataTable, 0, "foo", "bar");
        assertTableRow(dataTable, 1, "boz", "boo");
    }

    @Test
    void testDataTableWithASingleCell() {
        Step scenarioStep = steps.get(1);
        assertEquals(AND, scenarioStep.getKeyword());
        assertEquals("a data table with a single cell", scenarioStep.getText());

        StepArgument stepArgument = scenarioStep.getArgument();
        assertNotNull(stepArgument);
        assertEquals(DataTable.class.getSimpleName(), stepArgument.getClass().getSimpleName());

        DataTable dataTable = (DataTable) stepArgument;
        assertEquals(1, dataTable.getRows().size());
        assertTableRow(dataTable, 0, "foo");
    }

    @Test
    void testDataTableWithDifferentFormatting() {
        Step scenarioStep = steps.get(2);
        assertEquals(AND, scenarioStep.getKeyword());
        assertEquals("a data table with different formatting", scenarioStep.getText());

        StepArgument stepArgument = scenarioStep.getArgument();
        assertNotNull(stepArgument);
        assertEquals(DataTable.class.getSimpleName(), stepArgument.getClass().getSimpleName());

        DataTable dataTable = (DataTable) stepArgument;
        assertEquals(1, dataTable.getRows().size());
        assertTableRow(dataTable, 0, "foo", "bar", "boz");
    }

    @Test
    void testDataTableWithAnEmptyCell() {
        Step scenarioStep = steps.get(3);
        assertEquals(AND, scenarioStep.getKeyword());
        assertEquals("a data table with an empty cell", scenarioStep.getText());

        StepArgument stepArgument = scenarioStep.getArgument();
        assertNotNull(stepArgument);
        assertEquals(DataTable.class.getSimpleName(), stepArgument.getClass().getSimpleName());

        DataTable dataTable = (DataTable) stepArgument;
        assertEquals(1, dataTable.getRows().size());
        assertTableRow(dataTable, 0, "foo", "", "boz");
    }

    @Test
    void testDataTableWithCommentsAndNewlinesInside() {

        Step scenarioStep = steps.get(4);
        assertEquals(AND, scenarioStep.getKeyword());
        assertEquals("a data table with comments and newlines inside", scenarioStep.getText());

        StepArgument stepArgument = scenarioStep.getArgument();
        assertNotNull(stepArgument);
        assertEquals(DataTable.class.getSimpleName(), stepArgument.getClass().getSimpleName());

        DataTable dataTable = (DataTable) stepArgument;
        assertEquals(3, dataTable.getRows().size());
        assertTableRow(dataTable, 0, "foo", "bar");
        assertTableRow(dataTable, 1, "boz", "boo");
        assertTableRow(dataTable, 2, "boz2", "boo2");
    }

}
