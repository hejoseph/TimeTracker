package com.hejoseph;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TagServiceTest {
    private static final String TEST_FILE_PATH = "src/test/resources/test-timer-data.json";
    private TagService tagService;

    @BeforeEach
    public void setUp() {

        // Create a new TagService for each test case with a clean JSON file
        tagService = TagService.getInstance(TEST_FILE_PATH);
    }

    @Test
    public void testReadTags() {
        List<String> tags = tagService.readTags("fun");
        assertNotNull(tags);
        assertEquals(5, tags.size());
    }

    @Test
    public void testCreateTag() {
        tagService.createTag("fun", "newTag");
        List<String> tags = tagService.readTags("fun");
        assertTrue(tags.contains("newTag"));
    }



    @Test
    public void testUpdateTags() {
        tagService.createTag("fun", "newTag");
        List<String> updatedTags = List.of("tag1", "tag2", "tag3");
        tagService.updateTags("fun", updatedTags);
        List<String> tags = tagService.readTags("fun");
        assertEquals(updatedTags, tags);
    }

    @Test
    public void testDeleteTag() {
        tagService.createTag("fun", "tagToDelete");
        tagService.deleteTag("fun", "tagToDelete");
        List<String> tags = tagService.readTags("fun");
        assertFalse(tags.contains("tagToDelete"));
    }

    @Test
    public void testGetTagGroupForTag() {
        tagService.createTag("fun", "newTag");
        String tagGroup = tagService.getTagGroupForTag("newTag");
        assertEquals("fun", tagGroup);

        tagGroup = tagService.getTagGroupForTag("nonexistentTag");
        assertNull(tagGroup);
    }

    @Test
    public void testFilePersistence() {
        tagService.createTag("fun", "newTag");
        tagService = null; // Clear the reference to the current instance

        // Create a new TagService and check if the tag persists in the JSON file
        tagService = TagService.getInstance(TEST_FILE_PATH);
        List<String> tags = tagService.readTags("fun");
        assertTrue(tags.contains("newTag"));
    }

    // Clean up: Delete the test JSON file after all tests have run
    @AfterAll
    public static void cleanUp() {
//        File testFile = new File(TEST_FILE_PATH);
//        if (testFile.exists()) {
//            testFile.delete();
//        }
    }
}
