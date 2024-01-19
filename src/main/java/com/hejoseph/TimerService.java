package com.hejoseph;

import com.hejoseph.common.JsonFileUtils;
import com.hejoseph.common.Utils;
import org.json.simple.JSONObject;

import java.text.SimpleDateFormat;
import java.util.*;

public class TimerService {

    private static TimerService instance;

    private JSONObject jsonData;
    private String filePath;
    private TagService tagService;
    private HashMap<String, String> tagCount;

    // Public static method to provide the instance of the Singleton class
    public static TimerService getInstance(String filePath) {
        if (instance == null) {
            instance = new TimerService(filePath);
        }
        return instance;
    }

    private TimerService(String filePath) {
        this.filePath = filePath;
        this.jsonData = JsonFileUtils.loadJsonData(filePath);
        keepOnlyDateData();
        tagService = TagService.getInstance(filePath);
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

    public void printToday() {
        tagCount = new HashMap<>();
        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String content = "------------ Today : " + today + "------------\n\r";
        JSONObject dateState = (JSONObject) this.jsonData.get(today);
        if(dateState == null)return;
        List<String> subjects = new ArrayList<>(dateState.keySet());
        Collections.sort(subjects);
        String total = "00:00:00";
        String untagSubject = "";
        for (String subject : subjects) {
            String timeStr = (String) dateState.get(subject);
            total = Utils.addTime(total, timeStr);
            content+= String.format("%s=%s\n\r", subject, timeStr);
            String tagGroup = tagService.getTagGroupForTag(subject);

            if (tagGroup == null) {
                tagGroup = "_NoTag";
                untagSubject += subject + ";";
            }

            updateTagCount(tagGroup, timeStr);
        }
        System.out.println(content);
        printTagCount(total, untagSubject);
    }

    public void printLastXDays(int days) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();

        for (int i = 7; i >= 0; i--) {
            tagCount = new HashMap<>();
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.add(Calendar.DAY_OF_MONTH, -i);
            Date date = calendar.getTime();
            String formattedDate = dateFormat.format(date);

            System.out.println("------------" + formattedDate + "------------");
            JSONObject dateState = (JSONObject) jsonData.get(formattedDate);

            if (dateState != null) {
                List<String> subjects = new ArrayList<>(dateState.keySet());
                Collections.sort(subjects);
                String total = "00:00:00";
                String untagSubject = "";

                for (String subject : subjects) {
                    String timeStr = (String) dateState.get(subject);
                    total = Utils.addTime(total, timeStr);
                    System.out.println(String.format("%s=%s", subject, timeStr));
                    String tagGroup = tagService.getTagGroupForTag(subject);

                    if (tagGroup == null) {
                        tagGroup = "_NoTag";
                        untagSubject += subject + ";";
                    }

                    updateTagCount(tagGroup, timeStr);
                }

                printTagCount(total, untagSubject);
            }
        }
    }

    private void updateTagCount(String tagGroup, String timeStr) {
        if (tagCount.containsKey(tagGroup)) {
            String totalDuration = tagCount.get(tagGroup);
            tagCount.put(tagGroup, Utils.addTime(totalDuration, timeStr));
        } else {
            tagCount.put(tagGroup, timeStr);
        }
    }

    private void printTagCount(String total, String untagSubject) {
        String content = "---------- Time by tags ----------\n\r";
        content += ("total=" + total+"\n\r");
        Set<String> keys = tagCount.keySet();
        List<String> sortedKeys = new ArrayList<>(keys);
        Collections.sort(sortedKeys);
        for (String key : sortedKeys) {
            String value = tagCount.get(key);
            content += ("Tag: " + key + ", Total Duration: " + value+"\n\r");
        }

        if (untagSubject.length() > 0) {
            untagSubject = untagSubject.substring(0, untagSubject.length() - 1);
            content+=("notag subject : " + untagSubject+"\n\r");
        }

        System.out.println(content);
    }

    public void sumDurationsLastSevenDays() {
        sumDurationsLastXDays(7);
    }

    public synchronized void addTimeToSubjectForDate(String date, String subject, String time) {
        if (jsonData.containsKey(date)) {
            JSONObject dateData = (JSONObject) jsonData.get(date);
            if (dateData.containsKey(subject)) {
                String existingTime = (String) dateData.get(subject);
                String newTime = Utils.addTime(existingTime, time);
                dateData.put(subject, newTime);
            } else {
                dateData.put(subject, time);
            }
        } else {
            JSONObject dateData = new JSONObject();
            dateData.put(subject, time);
            jsonData.put(date, dateData);
        }
        updateDataToJsonFile();
    }

    public void updateDataToJsonFile() {
        jsonData.put("tags", tagService.getJsonData().get("tags"));
        JsonFileUtils.saveJsonData(jsonData, filePath);
    }

    public String getStringDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    public void sumDurationsLastXDays(int days) {
        System.out.println("----------------- last " + days + " days----------------");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date currentDate = new Date();
        Map<String, String> totalDurations = new HashMap<>();
        tagCount = new HashMap<>();
        String untagSubject = "";
        String total = "00:00:00";

        for (int i = 0; i < days; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.add(Calendar.DAY_OF_MONTH, -i);
            Date date = calendar.getTime();
            String formattedDate = dateFormat.format(date);
            JSONObject dateState = (JSONObject) jsonData.get(formattedDate);

            if (dateState != null) {
                for (Object subjectKey : dateState.keySet()) {
                    if (subjectKey instanceof String) {
                        String subject = (String) subjectKey;
                        String timeStr = (String) dateState.get(subject);
                        total = Utils.addTime(total, timeStr);

                        if (totalDurations.containsKey(subject)) {
                            String totalDuration = totalDurations.get(subject);
                            totalDurations.put(subject, Utils.addTime(totalDuration, timeStr));
                        } else {
                            totalDurations.put(subject, timeStr);
                        }

                        String tagGroup = tagService.getTagGroupForTag(subject);

                        if (tagGroup == null) {
                            tagGroup = "_NoTag";
                            untagSubject += subject + ";";
                        }

                        updateTagCount(tagGroup, timeStr);
                    }
                }
            }
        }

        for (Map.Entry<String, String> entry : totalDurations.entrySet()) {
            String subject = entry.getKey();
            String totalDuration = entry.getValue();
            System.out.println("Subject: " + subject + ", Total Duration: " + totalDuration);
        }

        printTagCount(total, untagSubject);
    }

    public static void main(String[] args) {
        TimerService timerService = new TimerService("timer-data.json");
        timerService.sumDurationsLastSevenDays();
    }

    public void printNoTaggedSubject() {
        this.jsonData.remove("tags");
        List<String> dates = new ArrayList<>(this.jsonData.keySet());
        Collections.sort(dates);
        String untagSubject = "";
        for(String date : dates) {
            JSONObject dateState = (JSONObject) this.jsonData.get(date);
            List<String> subjects = new ArrayList<>(dateState.keySet());
            for (String subject : subjects) {
                String tagGroup = tagService.getTagGroupForTag(subject);
                if (tagGroup == null) {
                    untagSubject += subject + ";";
                }
            }
        }
        if(untagSubject.length()>0){
            untagSubject = untagSubject.substring(0, untagSubject.length()-1);
            System.out.println("no tag for subject : "+untagSubject);
        }
    }
}
