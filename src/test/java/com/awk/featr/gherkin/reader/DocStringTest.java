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
class DocStringTest {
    private List<Step> steps;

    @BeforeAll
    void readFile() {
        LineReader lineReader = null;
        try {
            lineReader = getLineReader(DIRECTORY_GOOD + "docstrings.feature");
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
        assertEquals("DocString variations", feature.getName());

        assertNoBackground(feature);
        Scenario scenario = assertAndGetFirstScenario(feature);

        steps = scenario.getSteps();
        assertEquals(7, steps.size());
    }

    @Test
    void testSimpleDocString() {
        assertDocString(steps, 0, "a simple DocString", "",
                "      first line (no indent)\n" +
                        "        second line (indented with two spaces)\n" +
//bug                "\n" +
                        "      third line was empty");

    }

    @Test
    void testDocStringWithContentType() {
        assertDocString(steps, 1, "a DocString with content type", "xml",
                "      <foo>\n" +
                        "        <bar />\n" +
                        "      </foo>");
    }

    @Test
    void testDocStringWithWrongIndentation() {
        assertDocString(steps, 2, "a DocString with wrong indentation", "",
                "    wrongly indented line");
    }

    @Test
    void testDocStringWithAlternativeSeparator() {
        assertDocString(steps, 3, "a DocString with alternative separator", "",
                "      first line\n" +
                        "      second line");
    }

    @Test
    void testDocStringWithNormalSeperatorInside() {
        assertDocString(steps, 4, "a DocString with normal separator inside", "",
                "      first line\n" +
                        "      \"\"\"\n" +
                        "      third line");
    }

    @Test
    void testDocStringWithAlternativeSeperatorInside() {
        assertDocString(steps, 5, "a DocString with alternative separator inside", "",
                "      first line\n" +
                        "      ```\n" +
                        "      third line");
    }

    @Test
    void testDocStringWithEscapedSeparatorInside() {
        assertDocString(steps, 6, "a DocString with escaped separator inside", "",
                "      first line\n" +
                        "      \\\"\\\"\\\"\n" +
                        "      third line");
    }

    private void assertDocString(List<Step> steps, int stepNr, String expectedTitle, String expectedType, String expectedDocString) {
        Step scenarioStep1 = steps.get(stepNr);
        assertEquals(expectedTitle, scenarioStep1.getText());

        StepArgument stepArgument1 = scenarioStep1.getArgument();
        assertNotNull(stepArgument1);
        assertEquals(DocString.class.getSimpleName(), stepArgument1.getClass().getSimpleName());

        DocString docString1 = (DocString) stepArgument1;
        assertEquals(expectedType, docString1.getType());
        assertEquals(expectedDocString, docString1.getContent());
    }

}
