package com.awk.featr.gherkin.reader.helpers;

import com.awk.featr.ast.*;
import com.awk.featr.gherkin.model.GherkinLanguages;
import com.awk.featr.gherkin.reader.LineReader;
import com.awk.featr.gherkin.reader.TokenMatcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.*;

public class Utils {

    private static final String DIRECTORY_FEATURE_FILES = "feature-files" + File.separator;
    public static final String DIRECTORY_GOOD = "good" + File.separator;
    public static final TokenMatcher TOKEN_MATCHER_EN = new TokenMatcher(GherkinLanguages.get("en"));
    public static final String GIVEN = "Given";
    public static final String AND = "And";


    public static void assertTableRow(DataTable dataTable, int rowNr, String... values) {
        TableRow tableRow = dataTable.getRows().get(rowNr);
        assertTableRow(tableRow, values);
    }

    private static void assertTableRow(TableRow tableRow, String... values) {
        assertEquals(values.length, tableRow.getValues().size());
        for (int i = 0; i < values.length; i++) {
            assertEquals(values[i], tableRow.getValues().get(i));
        }
    }

    public static Scenario assertAndGetFirstScenario(Feature feature) {
        List<ScenarioDefinition> scenarioDefinitions = feature.getScenarioDefinitions();
        assertNotNull(scenarioDefinitions);
        assertEquals(1, scenarioDefinitions.size());

        ScenarioDefinition scenarioDefinition = scenarioDefinitions.get(0);
        assertEquals(Scenario.class.getSimpleName(), scenarioDefinition.getType());

        Scenario scenario = (Scenario) scenarioDefinition;
        assertTrue(scenario.getDescription().isEmpty());
        assertTrue(scenario.getTags().isEmpty());
        return scenario;
    }

    public static ScenarioOutline assertAndGetFirstScenarioOutline(Feature feature) {
        List<ScenarioDefinition> scenarioDefinitions = feature.getScenarioDefinitions();
        assertNotNull(scenarioDefinitions);
        assertEquals(1, scenarioDefinitions.size());

        ScenarioDefinition scenarioDefinition = scenarioDefinitions.get(0);
        assertEquals(ScenarioOutline.class.getSimpleName(), scenarioDefinition.getType());

        ScenarioOutline scenarioOutline = (ScenarioOutline) scenarioDefinition;
        assertTrue(scenarioOutline.getDescription().isEmpty());
        assertTrue(scenarioOutline.getTags().isEmpty());
        return scenarioOutline;
    }


    public static void assertNoBackground(Feature feature) {
        Background background = feature.getBackground();
        assertNull(background);
    }

    public static LineReader getLineReader(String testFeatureFileName) throws IOException {
        File testFeatureFile = getFeatureFile(DIRECTORY_FEATURE_FILES + testFeatureFileName);
        String fullPath = testFeatureFile.getAbsolutePath();
        String gherkinString = new String(Files.readAllBytes(Paths.get(fullPath)));
        return new LineReader(gherkinString);
    }

    private static File getFeatureFile(String fileName) {
        ClassLoader classLoader = Utils.class.getClassLoader();
        return new File(requireNonNull(classLoader.getResource(fileName)).getFile());
    }
}
