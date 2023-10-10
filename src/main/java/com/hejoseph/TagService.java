package com.hejoseph;

import com.hejoseph.common.JsonFileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.*;

public class TagService {
    private static TagService instance;
    public final String[] tagGroup = new String[]{"fun", "work", "need"};
    HashMap<String, Integer> tagCount;
    HashMap<String, String> tags;

    private String tagStr;
    private String newTags;

    public static TagService getInstance(String filePath) {
        if (instance == null) {
            instance = new TagService(filePath);
        }
        return instance;
    }

    private TagService(String filePath){
        this.filePath = filePath;
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                System.out.println("cannot create file " + filePath + " " + e.getMessage());
            }
            return;
        }

        this.jsonData = JsonFileUtils.loadJsonData(filePath);
        this.jsonData = keepTagsOnly();
    }

    public JSONObject getJsonData() {
        return jsonData;
    }

    public void setJsonData(JSONObject jsonData) {
        this.jsonData = jsonData;
    }

    public void printSubjects(){
        System.out.println("---------------- All Tags -------------");
        JSONObject tags = (JSONObject) this.jsonData.get("tags");
        List<String> keys = new ArrayList<>(tags.keySet());
        Collections.sort(keys);
        for (String key : keys) {
            JSONArray jsonArray = (JSONArray) tags.get(key);

            StringBuilder mergedValues = new StringBuilder();

            // Iterate through the JSON array and merge values with semicolons
            for (Object element : jsonArray) {
                if (element instanceof String) {
                    // Add the element to the merged string with a semicolon separator
                    mergedValues.append(element).append(";");
                }
            }

            // Convert the StringBuilder to a final merged string
            String result = mergedValues.toString();

            // Remove the trailing semicolon if it exists
            if (result.endsWith(";")) {
                result = result.substring(0, result.length() - 1);
            }
            System.out.println(key + ":" + result);
        }
    }

    private JSONObject keepTagsOnly() {
        JSONObject jsonObject = (JSONObject) jsonData.getOrDefault("tags",new JSONObject());
        JSONObject result = new JSONObject();
        result.put("tags", jsonObject);
        return result;
    }

    private JSONObject jsonData;
    private String filePath;

    public void createTag(String tagGroup, String tagName) {
        JSONArray tagArray = getOrCreateTagArray(tagGroup);
        tagArray.add(tagName);
        JSONObject tags = (JSONObject)this.jsonData.get("tags");
        tags.put(tagGroup, tagArray);
        this.jsonData.put("tags", tags);
//        saveJsonData();
    }

    public List<String> readTags(String tagGroup) {
        JSONArray tagArray = getOrCreateTagArray(tagGroup);
        return new ArrayList<>(tagArray);
    }

    public void updateTags(String tagGroup, List<String> updatedTags) {
        JSONArray tagArray = getOrCreateTagArray(tagGroup);
        tagArray.clear();
        tagArray.addAll(updatedTags);
        this.jsonData.put("tags", tagArray);
//        saveJsonData();
    }

    public void deleteTag(String tagGroup, String tagName) {
        JSONArray tagArray = getOrCreateTagArray(tagGroup);
        tagArray.remove(tagName);
        this.jsonData.put("tags", tagArray);
//        saveJsonData();
    }

    private JSONArray getOrCreateTagArray(String tagGroup) {
        if (!jsonData.containsKey("tags")) {
            jsonData.put("tags", new JSONObject());
        }

        JSONObject tagsObject = (JSONObject) jsonData.get("tags");

        if (!tagsObject.containsKey(tagGroup)) {
            tagsObject.put(tagGroup, new JSONArray());
        }

        return (JSONArray) tagsObject.get(tagGroup);
    }

//    private void saveJsonData() {
//        try{
//            ObjectMapper mapper = new ObjectMapper();
//            Path path = Paths.get(filePath);
//            Files.write(Paths.get(filePath),
//                    mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(mapper.readTree(jsonData.toJSONString())));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public String getTagGroupForTag(String tagName) {
        JSONObject tagsObject = (JSONObject) jsonData.get("tags");

        if (tagsObject != null) {
            for (Object tagGroupKey : tagsObject.keySet()) {
                String tagGroup = (String) tagGroupKey;
                JSONArray tagArray = (JSONArray) tagsObject.get(tagGroup);

                if (tagArray.contains(tagName)) {
                    return tagGroup;
                }
            }
        }

        return null;
    }

}
