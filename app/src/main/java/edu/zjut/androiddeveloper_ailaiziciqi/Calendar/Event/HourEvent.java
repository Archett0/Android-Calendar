package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event;

import androidx.annotation.NonNull;

import java.time.LocalTime;
import java.util.ArrayList;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model.Schedule;

/**
 * 一个时间段的全部事件
 */
@Deprecated
public class HourEvent {

    LocalTime time;
    ArrayList<Schedule> schedules;

    public HourEvent(LocalTime time, ArrayList<Schedule> schedules) {
        this.time = time;
        this.schedules = schedules;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public ArrayList<Schedule> getEvents() {
        return schedules;
    }

    public void setEvents(ArrayList<Schedule> schedules) {
        this.schedules = schedules;
    }

    @NonNull
    @Override
    public String toString() {
        if(schedules.isEmpty()){
            return time.toString() + " " + "schedules are empty";
        }
        return time.toString() + " " + schedules.get(0).getSchedule();
    }
}
