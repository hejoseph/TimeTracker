package com.hejoseph;

import com.hejoseph.common.JsonFileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TagService {

    public final String[] tagGroup = new String[]{"fun", "work", "need"};
    HashMap<String, Integer> tagCount;
    HashMap<String, String> tags;

    private String tagStr;
    private String newTags;

    public TagService(String filePath){
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

        tagStr="";
        clear();
        tags = new HashMap<String, String>();




//        try{
//            String content = Utils.getFileContent(filePath);
//            String[] lines = content.split("\r\n");
//            for (String line : lines) {
//                tagStr+=line+"\n\r";
//                String[] arr = line.split(":");
//                if (arr.length == 2){
//                    tags.put(arr[0], arr[1]);
////                    tagCount.put(arr[0], 0);
//                }
//            }
//        }catch(Exception e){
//            System.out.println("file tag not found");
//        }
    }

    private JSONObject keepTagsOnly() {
        JSONObject jsonObject = (JSONObject) jsonData.getOrDefault("tags",new JSONObject());
        JSONObject result = new JSONObject();
        result.put("tags", jsonObject);
        return result;
    }

    public void printTags(){
        System.out.println(tagStr);
    }

    public void clear(){
        tagCount = new HashMap<>();
        tagCount.put("fun",0);
        tagCount.put("work",0);
        newTags = "";
    }

    public String getGroup(String subject){
        for (Map.Entry<String, String> entry : tags.entrySet()) {
            String group = entry.getKey();
            String subjects = entry.getValue();
            if(subjects.contains(subject)){
                return group;
            }
        }

        for(int i = 0; i < tagGroup.length ; i++){
            String group = tagGroup[i];
            if(subject.startsWith(group+"_")){
                return group;
            }
        }

        appendTags(subject);
        return "other";
    }

    public void appendTags(String value){
        if(newTags.isEmpty()){
            newTags = value;
        }else{
            if(!newTags.contains(value)) newTags+=","+value;
        }
    }

    public void countTag(String subject, int nb){
        String group = getGroup(subject);
        if(tagCount.containsKey(group)){
            tagCount.put(group,tagCount.get(group)+nb);
        }else{
            tagCount.put(group,nb);
        }
    }

    public HashMap<String, Integer> getTagCount() {
        return tagCount;
    }

    public HashMap<String, String> getTags() {
        return tags;
    }

    public String getNewTags() {
        return newTags;
    }






    private JSONObject jsonData;
    private String filePath;


//    private JSONObject loadJsonData() {
//        try (FileReader reader = new FileReader(filePath)) {
//            JSONParser jsonParser = new JSONParser();
//            return (JSONObject) jsonParser.parse(reader);
//        } catch (IOException | ParseException e) {
//            e.printStackTrace();
//            return new JSONObject();
//        }
//    }

    public void createTag(String tagGroup, String tagName) {
        JSONArray tagArray = getOrCreateTagArray(tagGroup);
        tagArray.add(tagName);
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
//        saveJsonData();
    }

    public void deleteTag(String tagGroup, String tagName) {
        JSONArray tagArray = getOrCreateTagArray(tagGroup);
        tagArray.remove(tagName);
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

        return null; // Tag not found in any tag group
    }





}
