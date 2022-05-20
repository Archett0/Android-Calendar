package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Schedule {
    private int id;

    private LocalDate scheduleDate;

    private LocalTime scheduleStartTime;
    private LocalTime scheduleEndTime;

    private String week; //星期

    private String lunar; //农历
    private String schedule;

    public Schedule(Schedule s) {
        this.scheduleDate = s.scheduleDate;
        this.scheduleStartTime = s.scheduleStartTime;
        this.scheduleEndTime = s.scheduleEndTime;
        this.week = s.week;
        this.lunar = s.lunar;
        this.schedule = s.schedule;
    }

    public Schedule(LocalDate scheduleDate, LocalTime scheduleStartTime, LocalTime scheduleEndTime, String week, String lunar, String schedule) {
        this.scheduleDate = scheduleDate;
        this.scheduleStartTime = scheduleStartTime;
        this.scheduleEndTime = scheduleEndTime;
        this.week = week;
        this.lunar = lunar;
        this.schedule = schedule;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public LocalTime getScheduleStartTime() {
        return scheduleStartTime;
    }

    public void setScheduleStartTime(LocalTime scheduleStartTime) {
        this.scheduleStartTime = scheduleStartTime;
    }

    public LocalTime getScheduleEndTime() {
        return scheduleEndTime;
    }

    public void setScheduleEndTime(LocalTime scheduleEndTime) {
        this.scheduleEndTime = scheduleEndTime;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getLunar() {
        return lunar;
    }

    public void setLunar(String lunar) {
        this.lunar = lunar;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }
}
