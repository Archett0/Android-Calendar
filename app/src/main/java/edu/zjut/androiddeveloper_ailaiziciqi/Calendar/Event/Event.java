package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * 日程类
 */
public class Event {
    private String name;
    private LocalDate date;
    private LocalTime time;
    private LocalTime endTime;

    public Event(String name, LocalDate date, LocalTime time) {
        this.name = name;
        this.date = date;
        this.time = time;
        endTime = time.plusHours(1);
    }

    public Event(String name, LocalDate date, LocalTime time, LocalTime endTime) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public static ArrayList<Event> eventArrayList = new ArrayList<>();

    public static ArrayList<Event> eventsForDate(LocalDate date) {
        ArrayList<Event> events = new ArrayList<>();
        for (Event event : eventArrayList) {
            if (event.getDate().equals(date)) {
                events.add(event);
            }
        }
        return events;
    }

    public static ArrayList<Event> eventsForDateAndTime(LocalDate date, LocalTime time) {
        ArrayList<Event> events = new ArrayList<>();
        for (Event event : eventArrayList) {
            int eventHour = event.time.getHour();
            int cellHour = time.getHour();
            if (event.getDate().equals(date) && eventHour == cellHour) {
                events.add(event);
            }
        }
        return events;
    }

    @NonNull
    @Override
    public String toString() {
        String msg = "日程: ";
        msg += "名称 = " + name + " " +
                "日期 = " + date + " " +
                "开始时间 = " + time + " " +
                "结束时间 = " + endTime;
        return msg;
    }
}
