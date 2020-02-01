package com.awk.featr.gherkin.reader;

import com.awk.featr.ast.Scenario;
import com.awk.featr.gherkin.helper.ParserException;
import com.awk.featr.gherkin.model.GherkinLanguages;
import com.awk.featr.gherkin.model.GherkinLine;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class ScenarioReaderTest {
    @Test
    public void method() {
        String gherkinString = "  Scenario: minimalistic\n    Given the minimalism\n";
        ScenarioReader reader = new ScenarioReader("testTitle", new ArrayList<>());
        LineReader lineReader = new LineReader(gherkinString);

        TokenMatcher tokenMatcher = new TokenMatcher(GherkinLanguages.get("en"));
        try {
            lineReader.read();
            Scenario scenario = reader.parse(lineReader, tokenMatcher);
            assertEquals(1, scenario.getSteps().size());
        } catch (ParserException e) {
            fail(e.getMessage());
        }
    }

}
