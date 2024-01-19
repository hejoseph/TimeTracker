package com.hejoseph;

import com.hejoseph.common.Utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


public class Timer {

    public final String dataJsonFile = "timer-data.json";

    public Scanner in;

    int MAX = 999999;

    int min;
    String subject;

    Map<String, Integer> hm;
    private TagService tagService;
    private TimerService timerService;

    private boolean justStarted = true;

//    volatile  Set<String> flags;

    private ConcurrentHashMap<String, Long> flags = new ConcurrentHashMap<>();

    boolean test;

    private String currentFileName;

    private List<String> inputs;



    public Timer() throws Exception {
        inputs = new ArrayList<>();
//        flags = new HashSet<>();
        in = new Scanner(System.in);
        tagService = TagService.getInstance(dataJsonFile);
        timerService = TimerService.getInstance(dataJsonFile);
    }

    public String consumeInput(){
//        Utils.clearConsole();
        if(inputExists()){
            Utils.clearConsole();
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

    public static void main(String[] args) throws Exception {
        Timer timer = new Timer();
        timer.start();
    }

    public void start() throws Exception {
        System.out.println("starting timer");
        inputThreadStart();
        boolean run = true;
        while (run) {
            System.out.println("---------------Menu------------");
            System.out.println("[count] startCounting");
            System.out.println("[add] add min to subject");
            System.out.println("[addTags] add tag for subject");
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
                case "addTags":
                    addTags();
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
                case "stop":
                    run = false;
                    break;
            }
        }
        System.out.println("stopped");
    }

    private void deleteSubject() throws Exception {
        while (!getFirstInput().equalsIgnoreCase("back")) {
            timerService.printToday();
            System.out.println("---------------------Delete Subject---------------------");
            System.out.println("type : 'subject' , to delete subject");
            System.out.println("[back] menu");
            waitInput();
            if(!getFirstInput().equalsIgnoreCase("back")){
                try {
                    String subject = consumeInput();
                    String dateStr = timerService.getStringDate(new Date());
                    timerService.deleteTagValue(dateStr, subject);
                    timerService.updateDataToJsonFile();
                } catch (Exception e) {
                    System.out.println("you did not respect the format");
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
        timerService.printLastXDays(days);
    }

    public void printSumLastXDays(int days) throws IOException {
        timerService.sumDurationsLastXDays(days);
    }

    public void countingMenu() throws Exception {
        boolean loop = true;
        while (true && loop) {
//            Utils.clearConsole();
            timerService.printToday();
            tagService.printSubjects();
            printTimerList();
            String content = "---------------------- Counting menu ----------------------\n\r" +
                    "Create/Choose subject ?:....  / [back]\n\r"+
                    "type 'youtube' to start the timer for youtube\n\r"+
                    "type 'youtube;5' to start the countdown for youtube, it will end in 5 minutes\n\r" +
                    "type 'youtube;stop' to stop the timer for youtube\n\r" +
                    "[back] menu\n\r";
            System.out.println(content);
            waitInput();
            String firstInput = consumeInput();
//            String firstInput = in.nextLine();
            String action = getAction(firstInput);
            switch(action) {
                case "back":
                    loop = false;
                    break;
                case "startTimer":
                    startTimer(firstInput);
                    break;
                case "stopTimer":
                    stopTimer(firstInput);
                    break;
            }
        }
//        consumeInput();
    }

    private void printTimerList() {
        if(flags.isEmpty()) return;
        String content = "---------------- List of started timers -------------\n\r";
        for(String key : flags.keySet()){
            content+=key+"\n\r";
        }

        System.out.println(content);
    }

    private void stopTimer(String firstInput) {
        String[] arr = firstInput.split(";");
        String subject = arr[0];
        if (flags.containsKey(subject)) {
            System.out.println("Stopping timer : " + subject);
            flags.remove(subject);
        }
    }




    private void startTimer(String firstInput) {
        String[] arr = firstInput.split(";");
        String subject = firstInput;
        int minutes = MAX;
        if(arr.length==2){
            subject = arr[0];
            minutes = Integer.parseInt(arr[1]);
        }



        // Get the current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        // Format the date and time (optional)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        final String formattedDateTime = currentDateTime.format(formatter);

        test = true;

        final String sub = subject;
        final int min = minutes;

        Thread counter = new Thread() {
            @Override
            public void run() {
                try {
                    startCounting(this.getId(), sub, min, formattedDateTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        };

        if (flags.containsKey(subject)) {
            System.out.println(String.format("Cannot start timer '%s', because already started", subject));
            return;
        } else {
            flags.put(subject, counter.getId());
        }
        counter.start();

    }

    private String getAction(String firstInput) {
        if(firstInput.equals("back")) return "back";
        if(!firstInput.contains(";")) return "startTimer";
        String[] arr = firstInput.split(";");
        if(arr[1].equals("stop")) return "stopTimer";

        try{
            Integer.parseInt(arr[1]);
            return "startTimer";
        }catch(NumberFormatException n){
            System.out.println(String.format("command not found : %s", firstInput));
        }
        return "";
    }

    public void startCounting(long id, String subject, int m, String date) throws InterruptedException {

//        String input = consumeInput();
//        update(input);
//        date = (new Date()).toString();
//        System.out.println(date+", start counting for '"+subject+"'");
        int tmpMin = m;
        String tmpSubject = subject;

        boolean tmp = (tmpMin == MAX);
        int i = 0;
        int display = (tmp == true) ? i : tmpMin;
        System.out.println(String.format("%s : Timer '%s' has started : %d", date, subject, display));
//        System.out.println(display);

        while (tmpMin > 0) {
            Date start = new Date();
            Thread.sleep(60000L);
            if(!flags.containsKey(subject)){
                return;
            }else{
                if(id!=flags.get(subject)){
                    return;
                }
            }
            Date current = new Date();
            long diff = current.getTime() - start.getTime();
            long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            if (minutes > 1 && minutes < 120) { //when computer goes to sleep, and you are for example cleaning the room, minutes will be added to subject
                System.out.println(start);
                System.out.println(current);
                System.out.println("diff = " + minutes + " min");
                i += minutes;
                tmpMin -= minutes;
                timerService.addTimeToSubjectForDate(timerService.getStringDate(current), tmpSubject, Utils.convertMinutesToTimeFormat((int) minutes));
            } else {
                i++;
                tmpMin--;
                timerService.addTimeToSubjectForDate(timerService.getStringDate(current), tmpSubject, "00:01:00");
            }
            display = (tmp == true) ? i : tmpMin;
            System.out.println(String.format("timer[%s]:%d", subject, display));
//            input = consumeInput();
        }
//        SoundPlayer.play();
        if (tmpMin == 0) {
            DisplayImage.run();
        }
//        Utils.sendKeys(); //send win+1, you can put terminal as first taskbar program to make it pop up when timer ends
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
        System.out.println("Listening for input ...");
        while (!inputExists()) {
            Thread.sleep(1000L);
        }
    }

    public void inputThreadStart() throws Exception {
        Thread thread = new Thread() {
            public void run() {
                while (!getFirstInput().equalsIgnoreCase("stop")) {

                    try {
                        addInputToPool(in.nextLine());
//                        update(getFirstInput());
                    } catch (Exception e) {
                        e.printStackTrace();
                        addInputToPool("stop");
                    }
                }
            }
        };
        thread.start();
    }

    public void addSubject() throws Exception {
        while (!getFirstInput().equalsIgnoreCase("back")) {
            timerService.printToday();
            System.out.println("---------------------Add Minute to subject-----------------------");
            System.out.println("type : 'subject;00:05:00' , to add 5 min to subject");
            System.out.println("[back] menu");
            waitInput();
            if(!getFirstInput().equalsIgnoreCase("back")){
                try {
                    String[] arr = consumeInput().split(";");
                    String subject = arr[0];
                    String timeStr = arr[1];
                    String dateStr = timerService.getStringDate(new Date());
                    timerService.addTimeToSubjectForDate(dateStr, subject, timeStr);
                } catch (Exception e) {
                    System.out.println("you did not respect the format <subject>:<time in format hh:mm:ss>");
                }
            }
        }
        consumeInput();
        timerService.updateDataToJsonFile();
    }

    public void addTags() throws Exception {
        while (!getFirstInput().equalsIgnoreCase("back")) {
            tagService.printSubjects();
            timerService.printNoTaggedSubject();
            System.out.println("---------------------Add new tags-----------------------");
            System.out.println("type : 'fun:youtube' , to tag youtube as 'fun' tag");
            System.out.println("[back] menu");
            waitInput();
            if(!getFirstInput().equalsIgnoreCase("back")){
                try {
                    String[] arr = consumeInput().split(":");
                    String tag = arr[0];
                    String subject = arr[1];
                    tagService.createTag(tag, subject);
                } catch (Exception e) {
                    System.out.println("you did not respect the format <tagGroup>:<subject>");
                }
            }
            timerService.updateDataToJsonFile();
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