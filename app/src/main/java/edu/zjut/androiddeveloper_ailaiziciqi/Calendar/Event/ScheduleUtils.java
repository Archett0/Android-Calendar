package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.google.gson.Gson;
import com.nlf.calendar.Lunar;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.weather.WeatherDailyBean;
import com.qweather.sdk.view.HeConfig;
import com.qweather.sdk.view.QWeather;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DB.DbContact;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.R;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model.Schedule;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model.WeatherOfDate;

/**
 * 日程工具类
 */
public class ScheduleUtils {
    public static final int SCHEDULE_DESCRIPTION_START = 0; // 描述类型：开始
    public static final int SCHEDULE_DESCRIPTION_END = 1; // 描述类型：结束
    public static boolean WEATHER_ACCOUNT_AUTHORIZED = false; // 天气API是否已经授权
    public static final String CITY_LOCATION = "101210101";  // 杭州的地理位置
    public static boolean IS_WEATHER_DATA_RECEIVED = false; // 是否成功拿到天气预报信息
    public static List<WeatherOfDate> WEATHER_REPORTS;  // 处理好的15天预报
    private static final int COLOR_A = R.color.event_color_01;  // 颜色1
    private static final int COLOR_B = R.color.event_color_02;  // 颜色2
    private static final int COLOR_C = R.color.event_color_03;  // 颜色3
    private static final int COLOR_D = R.color.event_color_04;  // 颜色4

    /**
     * 从数据库刷新数据到内存
     */
    public static void loadOrReloadDataFromDatabase(ContentResolver contentResolver, String message) {

        // 查询得到的投影
        String[] projection = {DbContact.ScheduleEntry._ID,
                DbContact.ScheduleEntry.COLUMN_EVENT_NAME,
                DbContact.ScheduleEntry.COLUMN_START_DATE,
                DbContact.ScheduleEntry.COLUMN_END_DATE,
                DbContact.ScheduleEntry.COLUMN_START_TIME,
                DbContact.ScheduleEntry.COLUMN_END_TIME,
                DbContact.ScheduleEntry.COLUMN_WEEK,
                DbContact.ScheduleEntry.COLUMN_LUNAR
        };
        // 查询排序的规则
        String order = DbContact.ScheduleEntry.COLUMN_START_DATE + " " + "ASC";

        Cursor cursor = contentResolver.query(DbContact.ScheduleEntry.CONTENT_URI, projection, null, null, order);
        // clear the old static list
        if (Schedule.scheduleArrayList.size() != 0) {
            Schedule.scheduleArrayList.clear();
        }
        // retrieve data from database
        if (cursor != null && cursor.moveToFirst()) {
            do {
                /// 获取Column的位置
                int scheduleIdIndex = cursor.getColumnIndex(DbContact.ScheduleEntry._ID);
                int scheduleIndex = cursor.getColumnIndex(DbContact.ScheduleEntry.COLUMN_EVENT_NAME);
                int scheduleDateIndex = cursor.getColumnIndex(DbContact.ScheduleEntry.COLUMN_START_DATE);
                int scheduleEndDateIndex = cursor.getColumnIndex(DbContact.ScheduleEntry.COLUMN_END_DATE);
                int scheduleStartTimeIndex = cursor.getColumnIndex(DbContact.ScheduleEntry.COLUMN_START_TIME);
                int scheduleEndTimeIndex = cursor.getColumnIndex(DbContact.ScheduleEntry.COLUMN_END_TIME);
                int weekIndex = cursor.getColumnIndex(DbContact.ScheduleEntry.COLUMN_WEEK);
                int lunarIndex = cursor.getColumnIndex(DbContact.ScheduleEntry.COLUMN_LUNAR);
                // 取值
                int scheduleIdValue = cursor.getInt(scheduleIdIndex);
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
                // TODO: Something is not right here so we will fix this
                Schedule newSchedule = new Schedule(scheduleIdValue, date, endDate, time, endTime, weekValue, lunarValue, scheduleValue);
                Schedule.scheduleArrayList.add(newSchedule);
                Log.i("Utils Class Called", "Single Data added, id:" + newSchedule.getId());
            } while (cursor.moveToNext());
            // Tag
            Log.i("Utils Class Called", message + "ed from database");
            Log.i("Utils Class Called", "Data" + message + "ed:" + Schedule.scheduleArrayList.size());
        }
        // if there's no data from database, just don't insert any data
        else {
            Log.i("Load Cursor", "No data from database, nothing is loaded");
        }
        // TODO: Now we have the data, so we can sort them
        // TODO: Implement Sorting Methods
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

    /**
     * 生成一个日程的描述
     */
    public static String generateScheduleDescription(Schedule schedule, int type) {
        String description = "";
        if (type == SCHEDULE_DESCRIPTION_START) {
            description = "自 ";
            LocalDate startDate = schedule.getScheduleDate();
            // 将LocalDate转为Date.
            ZoneId zone = ZoneId.systemDefault();
            Instant instant = startDate.atStartOfDay().atZone(zone).toInstant();
            Date lunarDate = Date.from(instant);

            // 转换完成,使用这些数据
            Lunar todayLunar = Lunar.fromDate(lunarDate);
            String scheduleWeek = "星期" + todayLunar.getWeekInChinese();
            String scheduleLunar = todayLunar.getDayInChinese();

            description += startDate + " " + scheduleWeek + " " + scheduleLunar;
            description += " 开始";
        } else if (type == SCHEDULE_DESCRIPTION_END) {
            description = "于 ";
            LocalDate endDate = schedule.getScheduleEndDate();
            // 将LocalDate转为Date.
            ZoneId zone = ZoneId.systemDefault();
            Instant instant = endDate.atStartOfDay().atZone(zone).toInstant();
            Date lunarDate = Date.from(instant);

            // 转换完成,使用这些数据
            Lunar todayLunar = Lunar.fromDate(lunarDate);
            String scheduleWeek = "星期" + todayLunar.getWeekInChinese();
            String scheduleLunar = todayLunar.getDayInChinese();

            description += endDate + " " + scheduleWeek + " " + scheduleLunar;
            description += " 结束";
        }
        return description;
    }

    /**
     * 进行单次天气API的授权
     */
    public static void authorizeWeatherAccount() {
        if (!WEATHER_ACCOUNT_AUTHORIZED) {
            HeConfig.init("HE2205232030041442", "0057210cb33c4e3aadd2fac6d804126f");
            HeConfig.switchToDevService();
            Log.i("Random Debug", "天气API授权完成");
            WEATHER_ACCOUNT_AUTHORIZED = true;
        }
    }

    /**
     * 获取天气数据
     */
    public static void getWeather(Context context) {
        /**
         * 3天预报数据
         * @param location 所查询的地区，可通过该地区ID、经纬度进行查询经纬度格式：经度,纬度
         *                 （英文,分隔，十进制格式，北纬东经为正，南纬西经为负)
         * @param lang     (选填)多语言，可以不使用该参数，默认为简体中文
         * @param unit     (选填)单位选择，公制（m）或英制（i），默认为公制单位
         * @param listener 网络访问结果回调
         */
        QWeather.getWeather3D(context, CITY_LOCATION, new QWeather.OnResultWeatherDailyListener() {
            @Override
            public void onError(Throwable e) {
                Log.i("Random Debug", "getWeather onError: " + e);
            }

            @Override
            public void onSuccess(WeatherDailyBean weatherDailyBean) {
                Log.i("Random Debug", "getWeather onSuccess: " + new Gson().toJson(weatherDailyBean));

                //先判断返回的status是否正确，当status正确时获取数据，若status不正确，可查看status对应的Code值找到原因
                if (Code.OK == weatherDailyBean.getCode()) {
                    if (WEATHER_REPORTS == null) {
                        WEATHER_REPORTS = new ArrayList<>();
                    } else if (!WEATHER_REPORTS.isEmpty()) {
                        WEATHER_REPORTS.clear();
                    }
                    // 获取3天天气
                    List<WeatherDailyBean.DailyBean> dailyBeanList = weatherDailyBean.getDaily();
                    Log.i("Random Debug", "获得的天气数量: " + dailyBeanList.size());
                    for (int i = 0; i < dailyBeanList.size(); ++i) {
                        // 获取数据
                        String date = dailyBeanList.get(i).getFxDate();
                        String weather = dailyBeanList.get(i).getTextDay();
                        String tempMin = dailyBeanList.get(i).getTempMin();
                        String tempMax = dailyBeanList.get(i).getTempMax();
                        String moonPhase = dailyBeanList.get(i).getMoonPhase();
                        String wind = dailyBeanList.get(i).getWindDirDay();
                        String windScale = dailyBeanList.get(i).getWindScaleDay();
                        String rainMeters = dailyBeanList.get(i).getPrecip();
                        // 形成数据
                        LocalDate curDate = LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                        String curWeatherLine1 = weather + "   " + tempMin + " 到 " + tempMax + " 摄氏度";
                        String curWeatherLine2 = wind + windScale + "级   " + moonPhase + "   风力" + rainMeters + "级";
                        // 保存数据
                        WeatherOfDate weatherOfDate = new WeatherOfDate(curDate, curWeatherLine1, curWeatherLine2);
                        WEATHER_REPORTS.add(weatherOfDate);
                    }
                    IS_WEATHER_DATA_RECEIVED = true;
                    Log.i("Random Debug", "天气加载完毕:" + WEATHER_REPORTS.size());
                } else {
                    //在此查看返回数据失败的原因
                    Code code = weatherDailyBean.getCode();
                    Log.i("Random Debug", "failed code: " + code);
                }
            }
        });
    }

    /**
     * 给出日期获取当天天气数据
     */
    public static int getScheduleWeatherReport(Schedule schedule) {
        LocalDate startDate = schedule.getScheduleDate();
        LocalDate endDate = schedule.getScheduleEndDate();
        if (WEATHER_REPORTS == null || WEATHER_REPORTS.isEmpty()) {
            return -1;
        }
        int index = 0;
        for (; index < WEATHER_REPORTS.size(); ++index) {
            LocalDate weatherDate = WEATHER_REPORTS.get(index).getDate();
            // 当天的日程所以返回当天的天气
            if (weatherDate.equals(startDate)) {
                return index;
            }
            // 到这天截止所以也返回当天的天气
            else if (startDate.isBefore(weatherDate) && endDate.equals(weatherDate)) {
                return index;
            }
            // 日程横跨这天所以也返回这天的天气
            else if(startDate.isBefore(weatherDate) && endDate.isAfter(weatherDate)){
                return index;
            }
        }
        return -1;
    }

    /**
     * 给出日程来生成分享的文字
     */
    public static String generateShareText(Schedule schedule) {
        String msg;
        msg = schedule.getSchedule() + "\"： "
                + generateScheduleDescription(schedule, SCHEDULE_DESCRIPTION_START) + "， "
                + generateScheduleDescription(schedule, SCHEDULE_DESCRIPTION_END) + "， 日程时间从"
                + schedule.getScheduleStartTime() + "开始, 到"
                + schedule.getScheduleEndTime() + "结束。本日程属于日历：我的日历。";
        return msg;
    }

    /**
     * 随机选择日程背景颜色
     */
    public static int getRandomColor() {

        int randomColor = (int) (3 * Math.random());
        if (randomColor == 0) {
            return COLOR_A;
        } else if (randomColor == 1) {
            return COLOR_B;
        } else if (randomColor == 2) {
            return COLOR_C;
        } else {
            return COLOR_D;
        }
    }

}
