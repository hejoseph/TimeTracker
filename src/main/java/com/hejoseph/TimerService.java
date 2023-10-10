package com.hejoseph;

import com.hejoseph.common.JsonFileUtils;
import org.json.simple.JSONObject;

public class TimerService {
    private JSONObject jsonData;
    private String filePath;

    public TimerService(String filePath) {
        this.filePath = filePath;
        this.jsonData = JsonFileUtils.loadJsonData(filePath);
        keepOnlyDateData();
    }

    public JSONObject getJsonData() {
        return jsonData;
    }

    public void setJsonData(JSONObject jsonData) {
        this.jsonData = jsonData;
    }

    private void keepOnlyDateData() {
        this.jsonData.remove("tags");
    }

    public void setTagValue(String date, String tagName, String value) {
        JSONObject dateData = getOrCreateDateData(date);
        dateData.put(tagName, value);
    }

    public String getTagValue(String date, String tagName) {
        JSONObject dateData = (JSONObject) jsonData.get(date);
        if (dateData != null && dateData.containsKey(tagName)) {
            return (String) dateData.get(tagName);
        }
        return null; // Tag not found for the specified date
    }

    public void deleteTagValue(String date, String tagName) {
        JSONObject dateData = (JSONObject) jsonData.get(date);
        if (dateData != null) {
            dateData.remove(tagName);
        }
    }

    private JSONObject getOrCreateDateData(String date) {
        if (!jsonData.containsKey(date)) {
            jsonData.put(date, new JSONObject());
        }
        return (JSONObject) jsonData.get(date);
    }

}
