package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SmsSearchInformation {
    private int id;
    private String phone;
    private LocalDate sendDate;

    private List<Schedule> smsScheduleList;

    public SmsSearchInformation(int id, String phone, LocalDate sendDate, List<Schedule> smsScheduleList) {
        this.id = id;
        this.phone = phone;
        this.sendDate = sendDate;
        this.smsScheduleList = smsScheduleList;
    }

    public SmsSearchInformation(String phone, LocalDate sendDate, List<Schedule> smsScheduleList) {
        this.phone = phone;
        this.sendDate = sendDate;
        this.smsScheduleList = smsScheduleList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getSendDate() {
        return sendDate;
    }

    public void setSendDate(LocalDate sendDate) {
        this.sendDate = sendDate;
    }

    public List<Schedule> getSmsScheduleList() {
        return smsScheduleList;
    }

    public void setSmsScheduleList(List<Schedule> smsScheduleList) {
        this.smsScheduleList = smsScheduleList;
    }

    /**
     * 将自己的Schedule类型List转换为ScheduleString类型的List
     * @return  一个ScheduleString类型的List
     */
    public List<ScheduleString> getTransformedStringList(){
        List<ScheduleString> result = new ArrayList<>();
        for(Schedule schedule: smsScheduleList){
            ScheduleString newItem = new ScheduleString(schedule);
            result.add(newItem);
        }
        return result;
    }
}
