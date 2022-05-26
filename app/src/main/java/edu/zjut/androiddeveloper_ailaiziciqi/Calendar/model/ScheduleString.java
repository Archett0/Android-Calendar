package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model;

import android.util.Log;

import androidx.annotation.NonNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * 日程类(用于暂存全是string的数据)
 */
public class ScheduleString {

    private int id; // 日程Id
    private String schedule;    // 日程名称
    private String scheduleDate; // 日程开始日期
    private String scheduleEndDate; // 日程结束日期
    private String scheduleStartTime; // 日程开始时间
    private String scheduleEndTime;  // 日程结束时间
    private String week; // 日程所在星期
    private String lunar; // 日程农历日期

    /**
     * 空构造函数,用来支持JSON格式转换
     */
    public ScheduleString() {
    }

    /**
     * 构造函数,以一个Schedule对象转换成当前类型的对象
     * @param s 一个Schedule对象
     */
    public ScheduleString(Schedule s) {
        this.schedule = s.getSchedule();
        if (s.getScheduleDate() != null) {
            this.scheduleDate = s.getScheduleDate().toString();
        }
        if (s.getScheduleEndDate() != null) {
            this.scheduleEndDate = s.getScheduleEndDate().toString();
        }
        if (s.getScheduleStartTime() != null) {
            this.scheduleStartTime = s.getScheduleStartTime().toString();
        }
        if (s.getScheduleEndTime() != null) {
            this.scheduleEndTime = s.getScheduleEndTime().toString();
        }
        this.week = s.getWeek();
        this.lunar = s.getLunar();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public String getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(String scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public String getScheduleEndDate() {
        return scheduleEndDate;
    }

    public void setScheduleEndDate(String scheduleEndDate) {
        this.scheduleEndDate = scheduleEndDate;
    }

    public String getScheduleStartTime() {
        return scheduleStartTime;
    }

    public void setScheduleStartTime(String scheduleStartTime) {
        this.scheduleStartTime = scheduleStartTime;
    }

    public String getScheduleEndTime() {
        return scheduleEndTime;
    }

    public void setScheduleEndTime(String scheduleEndTime) {
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

    /**
     * 将对象本身以标准Schedule的方式返回
     * @return  标准形式的Schedule对象
     */
    public Schedule convertToSchedule() {
        int _id = this.id;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate _scheduleDate = null;
        LocalDate _scheduleEndDate = null;
        LocalTime _scheduleStartTime = null;
        LocalTime _scheduleEndTime = null;

        if (this.scheduleDate != null) {
            _scheduleDate = LocalDate.parse(this.scheduleDate, formatter);
        }
        if (this.scheduleEndDate != null) {
            _scheduleEndDate = LocalDate.parse(this.scheduleEndDate, formatter);
        }
        DateTimeFormatter formatterTime = DateTimeFormatter.ofPattern("HH:mm");
        if (this.scheduleStartTime != null) {
            _scheduleStartTime = LocalTime.parse(this.scheduleStartTime, formatterTime);
        }
        if (this.scheduleEndTime != null) {
            _scheduleEndTime = LocalTime.parse(this.scheduleEndTime, formatterTime);
        }
        String _week = this.week;
        String _lunar = this.lunar;
        String _schedule = this.schedule;
        Schedule schedule = new Schedule(_id, _scheduleDate, _scheduleEndDate, _scheduleStartTime, _scheduleEndTime, _week, _lunar, _schedule);
        return schedule;
    }

    @NonNull
    @Override
    public String toString() {
        String msg = "日程: ";
        msg += "名称 = " + schedule + " " +
                "日期 = " + scheduleDate + " " +
                "开始时间 = " + scheduleStartTime + " " +
                "结束时间 = " + scheduleEndTime;
        return msg;
    }

    public String toShortString() {
        String msg;
        if (!scheduleDate.equals(scheduleEndDate)) {
            msg = schedule + " " + scheduleDate + " " + scheduleStartTime
                    + " - " + scheduleEndDate + " " + scheduleEndTime;
        } else {
            msg = schedule + " " + scheduleDate + " " +
                    scheduleStartTime + "-" +
                    scheduleEndTime;
        }
        return msg;
    }
}
