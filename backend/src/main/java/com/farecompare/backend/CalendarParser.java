package com.farecompare.backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*@Component
@ComponentScan*/
public class CalendarParser {
    private String calendarData;
    private List<String> listOfCalendarData;
    private static final String EVENT_BEGIN = "BEGIN:VEVENT";
    private static final String EVENT_END = "END:VEVENT";

    public CalendarParser(String calendarData) {
        this.calendarData = calendarData;
         listOfCalendarData = getEvents(calendarData);
    }

    /**
     * Finds the indices of the first EVENT_BEGIN and EVENT_END in a given String
     * @param fileContents the String to search
     * @return a List of Integers containing the indices of the first and last VEvent
     */
    public List<Integer> findRangeOfIndices(String fileContents) {
        List<Integer> indices = new ArrayList<>();
        int beginIndex = fileContents.indexOf(EVENT_BEGIN);
        int endIndex = fileContents.indexOf(EVENT_END) + EVENT_END.length();
        if(beginIndex != -1 && endIndex != -1) {
            indices.add(beginIndex);
            indices.add(endIndex);
            return indices;
        }
        else{
           return null;
        }
    }
/**
 * Returns a List of Strings containing all VEvents in the calendar
 * @return a List of Strings containing all VEvents in the calendar
 */
    public List<String> getEvents(String calendarData) {
        String fileContents = calendarData;
        List<String> events = new ArrayList<>();
        while(fileContents.contains(EVENT_BEGIN) && fileContents.contains(EVENT_END)) {
            List<Integer> indices = this.findRangeOfIndices(fileContents);
            int beginIndex = indices.get(0);
            int endIndex = indices.get(1);
            String event = fileContents.substring(beginIndex, endIndex);
            events.add(event);
            fileContents = fileContents.substring(endIndex);
        }
        return events;
    }

    public List<String> getListOfCalendarData() {
        return this.listOfCalendarData;
    }
    public int getNumberOfEvents() {
        return listOfCalendarData.size();
    }

    public String sayHello() {
        return "Hello, this is the Calendar Parser Class";
    }


    /**
     *
     * @param calendarData some calendar Data
     * @return  the DTSTART tag of the calendarData
     */
    public String getDStart(String calendarData) {
        String regex = "DTSTART:\\d{8}T\\d{6}Z";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(calendarData);

        if (matcher.find()) {
            String match = matcher.group();
            return match;
        }
        else{
            return "No DStart";
        }
    }
    /**
     * @return string just containing the time, DTSTART should be spliced off the original string
     */
    public String spliceDTSTART(String input) {
        System.out.println("splice DTStart (" + input + "): " + input.substring(8, input.length()));
        return input.substring(8, input.length());
    }

    /**
     *
     * @param zuluTime a timestamp formatted in the ISO standard eg. 20230120T090000Z
     * @throws IllegalArgumentException if timestamp is not in ISO standard
     */
    public String getDayOfMonthFromISO(String zuluTime) throws
            IllegalArgumentException{

        if (zuluTime.length() == 16 && zuluTime.contains("T") && zuluTime.contains("Z")) {
//                String date = zuluTime.substring(0, 8);
//                String dateFormat = zuluTime.substring(0,3) + "-" + zuluTime.substring(4,5) + "-" + zuluTime.substring(6,7);
            final int beginningOfISO = 0;
            final int startOfMonth = 0;
            final int startOfDayOfMonth = 0;
            final int endOfDayOfMonth = 0;
            int year = Integer.parseInt(zuluTime.substring(0,4)); //2023
            int month = Integer.parseInt(zuluTime.substring(4,6));//01
            int dayOfMonth = Integer.parseInt(zuluTime.substring(6,8));//20
            LocalDate localDate =  LocalDate.of(year, month, dayOfMonth);
            DayOfWeek dayOfWeek = localDate.getDayOfWeek();
            return dayOfWeek.toString();
        }
        else {
            throw new IllegalArgumentException("zuluTime not in ISO format");
        }
    }

    /**
     *
     * @param zuluTime a timestamp formatted in the ISO standard eg. 20230120T090000Z
     * @return  the actual time from the timestamp eg. 09:00
     */
    public String getTimeFromISO(String zuluTime) throws
            IllegalArgumentException{

        if (zuluTime.length() == 16 && zuluTime.contains("T") && zuluTime.contains("Z")) {
                String hourTime = zuluTime.substring(9, 11);
                String minuteTime = zuluTime.substring(12, 14);
                String time = hourTime + ":" +minuteTime;
//                System.out.println("The time of the event: " + time);
                return time;
        }
        else {
            throw new IllegalArgumentException("zuluTime not in ISO format");
        }
    }
    public void getDayAndTimeForAllEvents() {
        int counter = 0;
        for (String event: listOfCalendarData) {
            counter++;
            String dStartTag = getDStart(event);
            String timeStamp = spliceDTSTART(dStartTag);
            System.out.println("Event " + counter + ":" + getDayOfMonthFromISO(timeStamp) + " " +  getTimeFromISO(timeStamp));
            System.out.println();
        }
    }
}
