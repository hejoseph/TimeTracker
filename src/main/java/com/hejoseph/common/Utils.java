package com.jaysef.demo.common;

import com.jaysef.demo.twitch.Compute;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static void main(String[] args) throws ParseException {

        String time = "00:11h01";
        String[] arr = time.split(":|h");
//        String duration = "03:09:04";
//        int after = convertStringToTimeSec(time) + convertStringToTimeSec(duration);
//        System.out.println(convertTimeSecToString(after));

        Date d = new Date();
        String ss = convertDateToString(d);


        String s = "Just Chatting(00:03:04)->02:18:04";
        String duration = Compute.singleRegex(".*\\((.*)\\)->",s);
        String position = Compute.singleRegex("->(.*)",s);
        String title = Compute.singleRegex("(.*)\\(.*\\)->.*",s);
        System.out.println(title);
    }

    public static String singleRegex(String pattern, String value){
        Pattern p=Pattern.compile(pattern);
        Matcher m=p.matcher(value);
        if(m.find()){
            return m.group(1);
        }
        return null;
    }

    public static String getFileContent(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            file.createNewFile();
            return "";
        }
        FileInputStream fin = new FileInputStream(filePath);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        copy(fin, bout);
        fin.close();
        byte[] b = bout.toByteArray();
        StringBuffer buf = new StringBuffer();
        return new String(b);
    }

    public static String convertTimeFormat(int time){
        String hour = (time / 60) + "";
        String min = String.format("%02d", new Object[] { Integer.valueOf(time % 60) });
        return hour + "h" + min;
    }

    public static String convertTimeSecToString(int seconds){
        long HH = seconds / 3600;
        long MM = (seconds % 3600) / 60;
        long SS = seconds % 60;
        String timeInHHMMSS = String.format("%02d:%02d:%02d", HH, MM, SS);
        return timeInHHMMSS;
    }

    public static int convertStringToTimeSec(String time){
        int hour = 0;
        int min = 0;
        int sec = 0;
        if(time==null){
            return 0;
        }
        String[] times = time.split(":|h|m|s");
        if(times.length==3){
            hour = Integer.parseInt(times[0]);
            min = Integer.parseInt(times[1]);
            sec = Integer.parseInt(times[2]);
        }else if(times.length==2){
            min = Integer.parseInt(times[0]);
            sec = Integer.parseInt(times[1]);
        }
        return hour*3600 + min*60 +sec;
    }

    public static Date convertFrenchDateStringToDate(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.FRENCH);
        Date firstDate = sdf.parse(dateString);
        return firstDate;
    }

    public static Date convertStringToDate(String dateString) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date firstDate = sdf.parse(dateString);
        return firstDate;
    }

    public static String convertDateToString(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd");
        String d = sdf.format(date);
        return d;
    }
    public static String convertDateToStringWithHour(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd.HH-mm-ss");
        String d = sdf.format(date);
        return d;
    }

    public static String convertDateToFrenchDateString(Date date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm:ss", Locale.FRENCH);
        String d = sdf.format(date);
        return d;
    }

    public static void copy(InputStream in, OutputStream out) throws IOException {
        synchronized (in) {
            synchronized (out) {
                byte[] buffer = new byte[256];
                while (true) {
                    int bytesRead = in.read(buffer);
                    if (bytesRead == -1)
                        break;
                    out.write(buffer, 0, bytesRead);
                }
            }
        }
    }

    public static void writeContentToFile(String fileName, String text) throws IOException {
        File file = new File(fileName);
        file.getParentFile().mkdirs();
        PrintWriter out = new PrintWriter(new FileWriter(fileName));
        out.println(text);
        out.close();
    }

    public static void backupAndWriteToFile(String fileName, String fileExtension, String content) throws IOException {
        File file = new File(fileName+fileExtension);
        if(file.exists()){
            BasicFileAttributes attr = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            FileTime fileTime = attr.lastModifiedTime();
            Instant creationTime = fileTime.toInstant();
            creationTime = creationTime.plus(8, TimeUnit.HOURS.toChronoUnit());
            String append = creationTime.toString().replaceAll(":","-");
            append = "-"+append.substring(0,append.indexOf("."));
            File dest = new File(fileName+append+fileExtension);
            if(dest.exists()){
                dest.delete();
            }
            file.renameTo(dest);
            File file2 = new File(fileName+fileExtension);
            Utils.writeContentToFile(file2.getAbsolutePath(),content);
        }else{
            Utils.writeContentToFile(file.getAbsolutePath(),content);
        }
    }

    public static String getTodayDate(){
        // Create a Date object representing the current date and time
        Date currentDate = new Date();

        // Define the desired date format pattern
        String pattern = "dd MMM yyyy HH:mm:ss";

        // Create a SimpleDateFormat object with the pattern
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

        // Format the date to the desired string format
        String formattedDate = dateFormat.format(currentDate);

        // Print the formatted date
        return (formattedDate);
    }

    public static void sendKeys() throws InterruptedException, AWTException {
        try {
            Robot robot = new Robot();
            robot.setAutoDelay(250);
            robot.keyPress(KeyEvent.VK_WINDOWS);
            robot.keyPress(KeyEvent.VK_1);
            robot.keyRelease(KeyEvent.VK_1);
            robot.keyRelease(KeyEvent.VK_WINDOWS);
        } catch (AWTException ex) {
            ex.printStackTrace();
        }
    }

}
