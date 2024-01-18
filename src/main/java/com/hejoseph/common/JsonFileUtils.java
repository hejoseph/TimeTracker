package com.hejoseph.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class JsonFileUtils {
    public static JSONObject loadJsonData(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            JSONParser jsonParser = new JSONParser();
            return (JSONObject) jsonParser.parse(reader);
        } catch (IOException | ParseException e) {
//            e.printStackTrace();
            return new JSONObject();
        }
    }

    public static void saveJsonData(JSONObject jsonData, String filePath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Path path = Paths.get(filePath);
            Files.write(path,
                    mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(mapper.readTree(jsonData.toJSONString())));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
