package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event;

import android.content.ContentResolver;
import android.database.Cursor;
import android.util.Log;

import com.nlf.calendar.Lunar;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DB.DbContact;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model.Schedule;

/**
 * 日程工具类
 */
public class ScheduleUtils {

    /**
     * 从数据库刷新数据到内存
     */
    public static void loadOrReloadDataFromDatabase(Cursor cursor, ContentResolver contentResolver, String message) {

        String[] projection = {DbContact.ScheduleEntry._ID,
                DbContact.ScheduleEntry.COLUMN_EVENT_NAME,
                DbContact.ScheduleEntry.COLUMN_START_DATE,
                DbContact.ScheduleEntry.COLUMN_END_DATE,
                DbContact.ScheduleEntry.COLUMN_START_TIME,
                DbContact.ScheduleEntry.COLUMN_END_TIME,
                DbContact.ScheduleEntry.COLUMN_WEEK,
                DbContact.ScheduleEntry.COLUMN_LUNAR
        };
        cursor = contentResolver.query(DbContact.ScheduleEntry.CONTENT_URI, projection, null, null, null);
        // clear the old static list
        if (Schedule.scheduleArrayList.size() != 0) {
            Schedule.scheduleArrayList.clear();
        }
        // retrieve data from database
        if (cursor != null && cursor.moveToFirst()) {
            do {
                /// 获取Column的位置
                int scheduleIndex = cursor.getColumnIndex(DbContact.ScheduleEntry.COLUMN_EVENT_NAME);
                int scheduleDateIndex = cursor.getColumnIndex(DbContact.ScheduleEntry.COLUMN_START_DATE);
                int scheduleEndDateIndex = cursor.getColumnIndex(DbContact.ScheduleEntry.COLUMN_END_DATE);
                int scheduleStartTimeIndex = cursor.getColumnIndex(DbContact.ScheduleEntry.COLUMN_START_TIME);
                int scheduleEndTimeIndex = cursor.getColumnIndex(DbContact.ScheduleEntry.COLUMN_END_TIME);
                int weekIndex = cursor.getColumnIndex(DbContact.ScheduleEntry.COLUMN_WEEK);
                int lunarIndex = cursor.getColumnIndex(DbContact.ScheduleEntry.COLUMN_LUNAR);
                // 取值
                String scheduleValue = cursor.getString(scheduleIndex);
                String scheduleDateValue = cursor.getString(scheduleDateIndex);
                String scheduleEndDateValue = cursor.getString(scheduleEndDateIndex);
                String scheduleStartTimeValue = cursor.getString(scheduleStartTimeIndex);
                String scheduleEndTimeValue = cursor.getString(scheduleEndTimeIndex);
                String weekValue = cursor.getString(weekIndex);
                String lunarValue = cursor.getString(lunarIndex);
                // 转换
                LocalDate date = LocalDate.parse(scheduleDateValue);
                LocalDate endDate = LocalDate.parse(scheduleEndDateValue);
                LocalTime time = LocalTime.parse(scheduleStartTimeValue);
                LocalTime endTime = LocalTime.parse(scheduleEndTimeValue);
                // 保存
                Schedule newSchedule = new Schedule(date, endDate, time, endTime, weekValue, lunarValue, scheduleValue);
                Schedule.scheduleArrayList.add(newSchedule);
            } while (cursor.moveToNext());
            // Tag
            Log.i("Utils Class Called", message + "ed from database");
            Log.i("Utils Class Called", "Data" + message + "ed:" + Schedule.scheduleArrayList.size());
        }
        // if there's no data from database, just insert these default data
        else {
            // TODO:测试完成后删去这个sector
            Schedule schedule1 = new Schedule("Play apex", LocalDate.now(), LocalTime.of(20, 0));
            Schedule schedule2 = new Schedule("Play Android studio", LocalDate.now(), LocalTime.of(21, 0));
            Schedule schedule3 = new Schedule("Tea with Jack Ma", LocalDate.now().plusDays(1), LocalTime.of(15, 0));
            Schedule schedule4 = new Schedule("Take a bath", LocalDate.now(), LocalTime.of(20, 0));
            Schedule schedule5 = new Schedule("Event no.1", LocalDate.now(), LocalTime.of(18, 0));
            Schedule schedule6 = new Schedule("Event no.2", LocalDate.now(), LocalTime.of(18, 0));
            Schedule schedule7 = new Schedule("Event no.3", LocalDate.now(), LocalTime.of(18, 0));
            Schedule schedule8 = new Schedule("Event no.4", LocalDate.now(), LocalTime.of(18, 0));
            Schedule schedule9 = new Schedule("Neutralize CB's Server", LocalDate.now().plusDays(2), LocalTime.of(4, 0));
            Schedule schedule10 = new Schedule("Neutralize CB's Website", LocalDate.now().plusDays(2), LocalTime.of(6, 0));
            Schedule.scheduleArrayList.add(schedule1);
            Schedule.scheduleArrayList.add(schedule2);
            Schedule.scheduleArrayList.add(schedule3);
            Schedule.scheduleArrayList.add(schedule4);
            Schedule.scheduleArrayList.add(schedule5);
            Schedule.scheduleArrayList.add(schedule6);
            Schedule.scheduleArrayList.add(schedule7);
            Schedule.scheduleArrayList.add(schedule8);
            Schedule.scheduleArrayList.add(schedule9);
            Schedule.scheduleArrayList.add(schedule10);
            Log.i("Load Cursor", "No data from database, default data is loaded");
        }
    }


    /**
     * 将添加日程时的用户输入转换为可存入数据库的数据
     */
    public static Schedule transformUserInputToCorrectForm(String scheduleName, String tmpStartString, String tmpEndString, int month_start, int month_end) {

        // 分析字符串得到日期和时间
        LocalDate startDate = LocalDate.of(Integer.parseInt(tmpStartString.substring(0, 4)), month_start + 1, Integer.parseInt(tmpStartString.substring(7, 9)));
        LocalDate endDate = LocalDate.of(Integer.parseInt(tmpEndString.substring(0, 4)), month_end + 1, Integer.parseInt(tmpEndString.substring(7, 9)));
        LocalTime startTime = LocalTime.of(Integer.parseInt(tmpStartString.substring(tmpStartString.length() - 5, tmpStartString.length() - 3)), Integer.parseInt(tmpStartString.substring(tmpStartString.length() - 2, tmpStartString.length())));
        LocalTime endTime = LocalTime.of(Integer.parseInt(tmpEndString.substring(tmpEndString.length() - 5, tmpEndString.length() - 3)), Integer.parseInt(tmpEndString.substring(tmpEndString.length() - 2, tmpEndString.length())));

        // Log用户操作
        Log.i("Utils Class Called", "Attempting to save:" + startDate);
        Log.i("Utils Class Called", "Attempting to save:" + endDate);
        Log.i("Utils Class Called", "Attempting to save:" + startTime);
        Log.i("Utils Class Called", "Attempting to save:" + endTime);

        // 将LocalDate转为Date.
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = startDate.atStartOfDay().atZone(zone).toInstant();
        Date lunarDate = Date.from(instant);

        // 转换完成,使用这些数据
        Lunar todayLunar = Lunar.fromDate(lunarDate);  // raw lunar data
        String scheduleWeek = "星期" + todayLunar.getWeekInChinese(); // event week: done
        String scheduleLunar = todayLunar.getDayInChinese();    // event lunar startDate: done

        Schedule schedule = new Schedule(startDate, endDate, startTime, endTime, scheduleWeek, scheduleLunar, scheduleName);
        return schedule;
    }

    /**
     * 判断一个日程是否合法
     */
    public static String isScheduleValid(Schedule schedule) {
        LocalDate startDate = schedule.getScheduleDate();
        LocalDate endDate = schedule.getScheduleEndDate();
        LocalTime startTime = schedule.getScheduleStartTime();
        LocalTime endTime = schedule.getScheduleEndTime();
        String errorMessage = null;

        // 同一天则只判断时间
        if (startDate.equals(endDate)) {
            if (startTime.isAfter(endTime)) { // 同一天时开始时间不可晚于结束时间
                errorMessage = "开始时间不可晚于结束时间";
            }
        }
        // 开始日期不得晚于结束日期
        else if (startDate.isAfter(endDate)) {
            errorMessage = "开始日期不得晚于结束日期";
        }

        return errorMessage;
    }

}
