package com.hejoseph;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimerServiceTest {

    private TimerService timerService;
    private String testFilePath = "src/test/resources/test-timer-data.json";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        timerService = new TimerService(testFilePath);
    }

    @Test
    void setTagValue_ShouldAddTagValue() throws ParseException, IOException {
        // Arrange
        JSONParser jsonParser = new JSONParser();
        JSONObject expectedJsonData = (JSONObject) jsonParser.parse("{\"2023-10-10\":{\"game\":\"0:00:01\"}}");

        timerService.setJsonData(new JSONObject());

        // Act
        timerService.setTagValue("2023-10-10", "game", "0:00:01");

        // Assert
        JSONObject actualJsonData = timerService.getJsonData();
        assertEquals(expectedJsonData, actualJsonData);
    }

    @Test
    void getTagValue_ShouldReturnTagValue() {
        // Arrange
        timerService.setTagValue("2023-10-10", "game", "0:00:01");

        // Act
        String tagValue = timerService.getTagValue("2023-10-10", "game");

        // Assert
        assertEquals("0:00:01", tagValue);
    }

    @Test
    void deleteTagValue_ShouldRemoveTagValue() throws ParseException, IOException {
        // Arrange
        JSONParser jsonParser = new JSONParser();
        JSONObject initialJsonData = (JSONObject) jsonParser.parse("{\"2023-10-10\":{\"game\":\"0:00:01\"}}");

        timerService.setJsonData(initialJsonData);

        // Act
        timerService.deleteTagValue("2023-10-10", "game");

        // Assert
        JSONObject expectedJsonData = (JSONObject) jsonParser.parse("{\"2023-10-10\":{}}");
        JSONObject actualJsonData = (JSONObject) timerService.getJsonData();
        assertEquals(expectedJsonData, actualJsonData);
    }
}
