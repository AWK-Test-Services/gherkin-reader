package com.awk.featr.gherkin.reader;

import com.awk.featr.ast.Document;
import com.awk.featr.ast.GherkinError;
import com.awk.featr.gherkin.helper.CompositeParserException;
import com.awk.featr.gherkin.helper.ParserException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static com.awk.featr.gherkin.reader.helpers.Utils.*;
import static org.junit.jupiter.api.Assertions.*;

class EmptyFeatureTest {
    @Test
    void testEmptyFeatureFile() {
        LineReader lineReader = null;
        try {
            lineReader = getLineReader(DIRECTORY_GOOD + "empty.feature");
        } catch (IOException e) {
            e.printStackTrace();
            fail("Could not read test-file");
        }

        DocumentReader reader = new DocumentReader();
        Document document = reader.parse(lineReader, TOKEN_MATCHER_EN);
        assertNotNull(document);

        List<GherkinError> errorList = document.getErrors();
        assertEquals(1, errorList.size());

        assertEquals("No Feature found in this file.", errorList.get(0).getMessage());
    }
}
