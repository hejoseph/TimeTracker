package com.hejoseph;
import com.hejoseph.common.Utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Tag {

    public final String[] tagGroup = new String[]{"fun", "work", "need"};
    HashMap<String, Integer> tagCount;
    HashMap<String, String> tags;

    private String tagStr;
    private String newTags;

    public Tag(String filePath){
        tagStr="";
        clear();
        tags = new HashMap<String, String>();
        File file = new File(filePath);

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (Exception e) {
                System.out.println("cannot create file " + filePath + " " + e.getMessage());
            }
            return;
        }

        try{
            String content = Utils.getFileContent(filePath);
            String[] lines = content.split("\r\n");
            for (String line : lines) {
                tagStr+=line+"\n\r";
                String[] arr = line.split(":");
                if (arr.length == 2){
                    tags.put(arr[0], arr[1]);
//                    tagCount.put(arr[0], 0);
                }
            }
        }catch(Exception e){
            System.out.println("file tag not found");
        }
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
}
