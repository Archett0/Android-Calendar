package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model;

import android.util.Log;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

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

    public Schedule(LocalDate scheduleDate, LocalTime scheduleStartTime,
                    LocalTime scheduleEndTime, String week, String lunar, String schedule) {
        this.scheduleDate = scheduleDate;
        this.scheduleStartTime = scheduleStartTime;
        this.scheduleEndTime = scheduleEndTime;
        this.week = week;
        this.lunar = lunar;
        this.schedule = schedule;
    }

    public Schedule(String schedule, String mainDate, String startTime, String endTime) {
        this.schedule = schedule;

        String year = mainDate.substring(0, 4);
        Log.w("year", year);
        String month = mainDate.substring(5, 7);
        Log.w("month_origin", month);
        if (month.substring(0, 1).equals("0")) {
            month = month.substring(1, 2);
            Log.w("month_", month);
        }

        String day = mainDate.substring(8, 10);
        Log.w("day_origin", day);
        if (day.substring(0, 1).equals("0")) {
            day = day.substring(1, 2);
        }
        Log.w("day_", day);
        this.scheduleDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));

        String week = mainDate.substring(13, 14);
        this.week = week;
        Log.w("week_", week);

        String lunar = mainDate.substring(17, 21);
        this.lunar = lunar;
        Log.w("lunar_", lunar);

        this.scheduleStartTime = LocalTime.parse(startTime + ":00",
                DateTimeFormatter.ISO_TIME);

        this.scheduleEndTime = LocalTime.parse(endTime + ":00",
                DateTimeFormatter.ISO_TIME);
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
