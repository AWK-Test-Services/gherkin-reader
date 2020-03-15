package com.awk.featr.gherkin.reader.additions;

import com.awk.featr.ast.*;
import com.awk.featr.gherkin.reader.DocumentReader;
import com.awk.featr.gherkin.reader.LineReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.List;

import static com.awk.featr.gherkin.reader.helpers.Utils.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ImageTest {
    private Document document;

    @BeforeAll
    void readFile() {
        LineReader lineReader = null;
        try {
            lineReader = getLineReader(DIRECTORY_ADDITIONS + "image.feature");
        } catch (IOException e) {
            e.printStackTrace();
            fail("Could not read test-file");
        }

        DocumentReader reader = new DocumentReader();
        document = reader.parse(lineReader, TOKEN_MATCHER_EN);
        assertNotNull(document);

        List<GherkinError> errors = document.getErrors();
        assertTrue(errors.isEmpty());
    }

    @Test
    void testImage() {
        assertEquals(1, document.getUsedImages().size());

        Image image = document.getUsedImages().get(0);
        assertNotNull(image);
        assertEquals("../Example-image.png", image.getLocation());
        assertEquals("png", image.getType());

        String id = image.getId();

        Feature feature = document.getFeature();
        assertNotNull(feature);
        assertNoBackground(feature);

        assertEquals("  This is a description with an image\n" +
                "  <img id=\""+ id + "\" src=\"../Example-image.png\" type=\"png\">", feature.getDescription());
    }

    //testImageOnTwoLines

    //testmultipleImages
}
