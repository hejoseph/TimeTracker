package com.hejoseph;

import com.hejoseph.common.Utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;


public class Timer {


//    public final String dataDir = "C:\\workspace\\monitorBright\\";
    public final String dataDir = "./";
    public final String tagFile = "tags.txt";
    public String exec;

    public final String dataJsonFile = "timer-data.json";

    //    public String input;
    public Scanner in;
    public boolean busy;

    int MAX = 999999;

    int min;
    String subject;

    Map<String, Integer> hm;
    private TagService tagService;

    private boolean justStarted = true;

    private String currentFileName;

    private List<String> inputs;



    public Timer() throws Exception {
        inputs = new ArrayList<>();
        in = new Scanner(System.in);
        refreshFileName();
        hm = loadSubjects(dataDir + currentFileName);
        tagService = new TagService(tagFile);
    }

    public String consumeInput(){
        if(inputExists()){
            return inputs.remove(0);
        }
        return "";
    }

    public void addInputToPool(String input){
        inputs.add(input);
    }

    public String getFirstInput(){
        if(inputExists()){
            return inputs.get(0);
        }
        return "";
    }

    public boolean inputExists(){
        return inputs.size() > 0;
    }

    public void refreshFileName() {
        Date date = new Date();
        currentFileName = new SimpleDateFormat("'time-'yyyy-MM-dd'.txt'").format(date);
    }

    private int getDateOfMonth(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public static void main(String[] args) throws Exception {

        Timer timer = new Timer();
        timer.start();
    }

    public void printDayFile(String filePath) throws IOException {
        String date = filePath.substring(filePath.length() - 14, filePath.length() - 4);
        System.out.println("--------------" + date + "--------------");
        Map<String, Integer> hm = loadSubjects(filePath);
        printSubjects(hm);
    }

    public void start() throws Exception {
        System.out.println("starting timer");
        inputThreadStart();

        while (!consumeInput().equalsIgnoreCase("stop")) {
            System.out.println("---------------Menu------------");
            System.out.println("[count] startCounting");
            System.out.println("[add] add min to subject");
            System.out.println("[del] delete a subject");
            System.out.println("[p7] printLastXDays(7)");
            System.out.println("[ps7] printSumLastXDays(7)");
            System.out.println("[p] printLastXDays()");
            System.out.println("[ps] printSumLastXDays()");
            System.out.println("[stop] exit program");
            if(justStarted){
                addInputToPool("count");
                justStarted = false;
            }
            waitInput();
            switch (consumeInput()) {
                case "count":
                    countingMenu();
                    break;
                case "add":
                    addSubject();
                    break;
                case "del":
                    deleteSubject();
                    break;
                case "p7":
                    printLastXDays(7);
                    break;
                case "ps7":
                    printSumLastXDays(7);
                    break;
                case "p":
                    printLastXDays();
                    break;
                case "ps":
                    printSumLastXDays();
                    break;
            }
        }
        System.out.println("stopped");
    }

    private void deleteSubject() throws Exception {
        while (!getFirstInput().equalsIgnoreCase("back")) {
            printSubjects(hm);
            System.out.println("---------------------Delete Subject---------------------");
            System.out.println("type : 'subject' , to delete subject");
            System.out.println("[back] menu");
            waitInput();
            if(!getFirstInput().equalsIgnoreCase("back")){
                try {
                    String subject = consumeInput();
                    hm.remove(subject);
                    updateStateToFile(hm);
                } catch (Exception e) {
                }
            }
        }
        consumeInput();
    }

    public void printLastXDays() throws Exception {
        while (!getFirstInput().equalsIgnoreCase("back")) {
            System.out.println("-------------------Print Last X Days------------------------");
            System.out.println("Type number for displaying last x days:");
            System.out.println("[back] menu");
            waitInput();
            if(!getFirstInput().equalsIgnoreCase("back")){
                try {
                    int value = Integer.parseInt(consumeInput());
                    printLastXDays(value);
                } catch (Exception e) {
                }
            }
        }
        consumeInput();
    }

    public void printSumLastXDays() throws Exception {
        while (!getFirstInput().equalsIgnoreCase("back")) {
            System.out.println("-----------------------Summary of Last X Days-------------------------");
            System.out.println("Type number for summing last x days:");
            System.out.println("[back] menu");
            waitInput();
            if(!getFirstInput().equalsIgnoreCase("back")){
                try {
                    int value = Integer.parseInt(consumeInput());
                    printSumLastXDays(value);
                } catch (Exception e) {
                }
            }
        }
        consumeInput();
    }

    public void printLastXDays(int days) throws IOException {
        ZonedDateTime time = ZonedDateTime.now();
        for (int i = days; i >= 0; i--) {
            ZonedDateTime tmp = time.minus(i, ChronoUnit.DAYS);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("-yyyy-MM-dd");
            String formattedString = tmp.format(formatter);
            printDayFile(dataDir + "time" + formattedString + ".txt");
        }
    }

    public void printSumLastXDays(int days) throws IOException {
        Map<String, Integer> total = new TreeMap<>();
        ZonedDateTime time = ZonedDateTime.now();
        for (int i = days; i >= 0; i--) {
            ZonedDateTime tmp = time.minus(i, ChronoUnit.DAYS);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("-yyyy-MM-dd");
            String formattedString = tmp.format(formatter);
            Map<String, Integer> tmp2 = loadSubjects(dataDir + "time" + formattedString + ".txt");
            for (Map.Entry<String, Integer> entry : tmp2.entrySet()) {
                int value = ((Integer) entry.getValue()).intValue();
                String key = entry.getKey();
                addSubject(total, key, value);
            }
        }
        System.out.println("----------last " + days + " day(s)------");
        printSubjects(total);
    }

    public void countingMenu() throws Exception {
        while (!getFirstInput().equalsIgnoreCase("back")) {
            System.out.println("--------------Today---------------");
            printSubjects(hm);
            System.out.println("-----------------");
            tagService.printTags();
            System.out.println("Create/Choose subject ?:....  / [back]");
            System.out.println("[change] switch subject counter");
            System.out.println("[back] menu");
            waitInput();
            if (!getFirstInput().equalsIgnoreCase("back")) {
                startCounting();
            }
        }
        consumeInput();
    }

    public void startCounting() throws Exception {
        String input = consumeInput();
        update(input);
        System.out.println(new Date()+", start counting for '"+subject+"'");
        int tmpMin = min;
        String tmpSubject = subject;

        boolean tmp = (tmpMin == MAX);
        int i = 0;
        int display = (tmp == true) ? i : tmpMin;
        System.out.println(display);

        while (tmpMin > 0 && !input.equalsIgnoreCase("change") && !input.equalsIgnoreCase("back")) {
            Date start = new Date();
            Thread.sleep(60000L);
            Date current = new Date();
            long diff = current.getTime() - start.getTime();
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            if(getDateOfMonth(start) != getDateOfMonth(current)){
                hm = new TreeMap<>();
            }
            if(minutes > 1 && minutes < 120){
                System.out.println(start);
                System.out.println(current);
                System.out.println("diff = "+minutes+" min");
                i+=minutes;
                tmpMin-=minutes;
                updateStateToFile(hm, tmpSubject, (int) minutes);
            }else{
                i++;
                tmpMin--;
                updateStateToFile(hm, tmpSubject, 1);
            }
            display = (tmp == true) ? i : tmpMin;
            System.out.println(display);
            input = consumeInput();
        }
        SoundPlayer.play();
        Utils.sendKeys();
    }

    public void update(String inputText) {
        min = MAX;
        String[] inputs = inputText.split(";");
        subject = inputText;
        if (inputs.length >= 2) {
            subject = inputs[0];
            min = Integer.parseInt(inputs[1]);
        }
        subject = subject.toLowerCase();
    }

    public void waitInput() throws Exception {
        while (!inputExists()) {
            Thread.sleep(1000L);
        }
    }

    public void inputThreadStart() throws Exception {
        Thread thread = new Thread() {
            public void run() {
                while (!getFirstInput().equalsIgnoreCase("stop")) {
                    System.out.println("you have to input ...");
                    try {
                        addInputToPool(in.nextLine());
                        update(getFirstInput());
                    } catch (Exception e) {
                        addInputToPool("stop");
                    }
                }
            }
        };
        thread.start();
    }


    public void updateStateToFile(Map<String, Integer> hm, String subject, int time) throws IOException {
        addSubject(hm, subject, time);
        refreshFileName();
        saveStateToFile(hm, dataDir + currentFileName);
    }

    public void updateStateToFile(Map<String, Integer> hm) throws IOException {
        refreshFileName();
        saveStateToFile(hm, dataDir + currentFileName);
    }

    public void printSubjects(Map<String, Integer> hm) {
        boolean displaySummary = false;
        int total = 0;
        for (Map.Entry<String, Integer> entry : hm.entrySet()) {
            int time = ((Integer) entry.getValue()).intValue();
            String subject = entry.getKey();
            String timeStr = Utils.convertTimeFormat(time);
            System.out.println(subject + "=" + timeStr);
            total += time;
            tagService.countTag(subject, time);
        }

        if (total > 0) {
            System.out.println("--------");
            System.out.println("total:" + Utils.convertTimeFormat(total));
            displaySummary = true;
        }
        if(displaySummary){
            for (Map.Entry<String, Integer> entry : tagService.getTagCount().entrySet()) {
                int time = ((Integer) entry.getValue()).intValue();
                String key = entry.getKey();
                String timeStr = Utils.convertTimeFormat(time);
                System.out.println(key + ":" + timeStr);
            }
        }

        if (tagService.getNewTags().length() > 0) {
            System.out.println("newTags:" + tagService.getNewTags());
        }
        tagService.clear();
    }

    public void addSubject() throws Exception {
        while (!getFirstInput().equalsIgnoreCase("back")) {
            printSubjects(hm);
            System.out.println("---------------------Add Minute to subject-----------------------");
            System.out.println("type : 'subject;5' , to add 5 min to subject");
            System.out.println("[back] menu");
            waitInput();
            if(!getFirstInput().equalsIgnoreCase("back")){
                try {
                    String[] arr = consumeInput().split(";");
                    String subject = arr[0];
                    int value = Integer.parseInt(arr[1]);
                    updateStateToFile(hm, subject, value);
                } catch (Exception e) {
                }
            }
        }
        consumeInput();
    }

    public void addSubject(Map<String, Integer> hm, String subject, int time) {
        if (hm.containsKey(subject)) {
            int value = ((Integer) hm.get(subject)).intValue() + time;
            hm.put(subject, Integer.valueOf(value));
        } else {
            hm.put(subject, Integer.valueOf(time));
        }
    }

    public void saveStateToFile(Map<String, Integer> hm, String filepath) throws IOException {
        String content = "";
        for (Map.Entry<String, Integer> entry : hm.entrySet()) {
            String subject = entry.getKey();
            int time = ((Integer) entry.getValue()).intValue();
            content = content + subject + ";" + time + "\r\n";
        }
        content = content.substring(0,content.length()-2);
        Utils.writeContentToFile(filepath, content);
    }

    public Map<String, Integer> loadSubjects(String filepath) throws IOException {
        Map<String, Integer> hm = new TreeMap<>();
        File file = new File(filepath);
        if (!file.exists())
            return hm;
        String content = Utils.getFileContent(filepath);
        String[] lines = content.split("\r\n");
        for (String line : lines) {
            String[] arr = line.split(";");
            if (arr.length == 2)
                hm.put(arr[0], Integer.valueOf(Integer.parseInt(arr[1])));
        }
        return hm;
    }


}