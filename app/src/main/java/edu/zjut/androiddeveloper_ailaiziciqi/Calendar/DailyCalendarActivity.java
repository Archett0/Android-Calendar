package edu.zjut.androiddeveloper_ailaiziciqi.Calendar;

import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.SCHEDULE_DESCRIPTION_END;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.SCHEDULE_DESCRIPTION_START;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.WEATHER_REPORTS;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.generateScheduleDescription;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.getRandomColor;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.getScheduleWeatherReport;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.loadOrReloadDataFromDatabase;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model.Schedule.getScheduleById;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model.Schedule.schedulesForMonth;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.MonthLoader;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;
import com.nlf.calendar.Lunar;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.add.AddScheduleActivity;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.mix.MixActivity;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DB.DbContact;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleDetailsActivity;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model.Schedule;

public class DailyCalendarActivity extends AppCompatActivity {

    private WeekView mWeekView; // 日视图
    private TextView mTodayBtn; // 今日日程按钮
    private ImageView mBack; // 返回按钮
    Context context;    // 上下文
    private boolean ACCESS_FROM_INDEX = true;    // 访问是否是从首页双击进入的

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_calendar);

        // 获取Context
        context = DailyCalendarActivity.this;

        // 拿到日视图和今日日程按钮
        mWeekView = findViewById(R.id.weekView);
        mTodayBtn = findViewById(R.id.hint_text);
        // 设置显示模式为单日
        mWeekView.setNumberOfVisibleDays(1);

        // 重置部分View的设置
        mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
        mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));

        // 监听返回键
        mBack = findViewById(R.id.today_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 设置左边栏和顶栏的文字显示
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(java.util.Calendar date) {
                Lunar lunar = Lunar.fromDate(date.getTime());
                return "星期" + lunar.getWeekInChinese() + "  " + (lunar.getMonth() + 1) + "月" + lunar.getDay() + "日                 ";
            }

            @Override
            public String interpretTime(int hour, int minutes) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });

        // 今日日程点击监听器
        mTodayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                java.util.Calendar today = java.util.Calendar.getInstance();
                mWeekView.goToDate(today);
            }
        });

        // 事件点击监听器
        mWeekView.setOnEventClickListener(new WeekView.EventClickListener() {
            @Override
            public void onEventClick(WeekViewEvent event, RectF eventRect) {
                String _id = event.getIdentifier();
                Schedule schedule = getScheduleById(Integer.parseInt(_id));
                if (schedule == null) {
                    Toast.makeText(context, "出错了,请重试", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(context, ScheduleDetailsActivity.class);
                    Uri scheduleUri = ContentUris.withAppendedId(DbContact.ScheduleEntry.CONTENT_URI, schedule.getId());
                    intent.setData(scheduleUri);
                    intent.putExtra("Name", schedule.getSchedule());
                    intent.putExtra("sid", schedule.getId());
                    intent.putExtra("StartDescription", generateScheduleDescription(schedule, SCHEDULE_DESCRIPTION_START));
                    intent.putExtra("EndDescription", generateScheduleDescription(schedule, SCHEDULE_DESCRIPTION_END));
                    intent.putExtra("Date", String.valueOf(schedule.getScheduleDate()));
                    intent.putExtra("EndDate", String.valueOf(schedule.getScheduleEndDate()));
                    intent.putExtra("Time", String.valueOf(schedule.getScheduleStartTime()));
                    intent.putExtra("EndTime", String.valueOf(schedule.getScheduleEndTime()));
                    // 获取相应的日期,并填充
                    if (WEATHER_REPORTS != null && !WEATHER_REPORTS.isEmpty()) {
                        int weatherIndex = getScheduleWeatherReport(schedule);
                        if (weatherIndex != -1) {
                            intent.putExtra("Weather", WEATHER_REPORTS.get(weatherIndex).getWeather());
                            intent.putExtra("WeatherDetails", WEATHER_REPORTS.get(weatherIndex).getWeatherDetails());
                        } else {
                            intent.putExtra("Weather", "暂无天气信息");
                            intent.putExtra("WeatherDetails", "暂无天气详情");
                        }
                    } else {
                        intent.putExtra("Weather", "暂无天气信息");
                        intent.putExtra("WeatherDetails", "暂无天气详情");
                    }
                    intent.putExtra("Type", "我的日历");
                    startActivity(intent);
                }
            }
        });

        // 事件提供器
        mWeekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
            @Override
            public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                Log.i("Day View", "加载" + newMonth + "月");
                // 从静态List拿到数据
                List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();

                // 重新加载数据
                loadOrReloadDataFromDatabase(context.getContentResolver(), "Day Load");

                // If there's no data in the DB, we load a default data.
                if (Schedule.scheduleArrayList.isEmpty()) {
                    Log.i("Day View", "数据库为空");
                    java.util.Calendar startTime = java.util.Calendar.getInstance();
                    startTime.set(java.util.Calendar.HOUR_OF_DAY, 12);
                    startTime.set(java.util.Calendar.MINUTE, 0);
                    startTime.set(java.util.Calendar.MONTH, newMonth);
                    startTime.set(java.util.Calendar.YEAR, newYear);
                    java.util.Calendar endTime = (java.util.Calendar) startTime.clone();
                    endTime.add(java.util.Calendar.HOUR, 1);
                    endTime.set(java.util.Calendar.MONTH, newMonth);
                    WeekViewEvent event = new WeekViewEvent("DEFAULT", "数据库中没有日程捏", startTime, endTime);
                    event.setColor(getResources().getColor(R.color.event_color_01));
                    events.add(event);
                }
                // if not, we load data from static List(only the data in this month)
                else {
                    Log.i("Day View", "数据不为空,准备加载");
                    ArrayList<Schedule> schedules = schedulesForMonth(newYear, newMonth);
                    // if there's no schedules in this month, just return the empty list
                    if (schedules.isEmpty()) {
                        Log.i("Day View", "从工具类获取了空列表");
                        return events;
                    }
                    Log.i("Day View", "从工具类获取的列表不为空，长度为" + schedules.size());
                    // if not, we transform these data and feed to the view
                    for (Schedule schedule : schedules) {
                        String id = String.valueOf(schedule.getId());
                        String name = schedule.toShortString();
                        LocalDate start = schedule.getScheduleDate();
                        LocalDate end = schedule.getScheduleEndDate();
                        LocalTime startLocalTime = schedule.getScheduleStartTime();
                        LocalTime endLocalTime = schedule.getScheduleEndTime();

                        // 开始时间
                        java.util.Calendar startTime = java.util.Calendar.getInstance();
                        startTime.set(java.util.Calendar.HOUR_OF_DAY, startLocalTime.getHour());
                        startTime.set(java.util.Calendar.MINUTE, startLocalTime.getMinute());
                        startTime.set(java.util.Calendar.DATE, start.getDayOfMonth());
                        startTime.set(java.util.Calendar.MONTH, newMonth - 1);
                        startTime.set(java.util.Calendar.YEAR, newYear);

                        // 结束时间
                        java.util.Calendar endTime = java.util.Calendar.getInstance();
                        endTime.set(java.util.Calendar.HOUR_OF_DAY, endLocalTime.getHour());
                        endTime.set(java.util.Calendar.MINUTE, endLocalTime.getMinute());
                        endTime.set(java.util.Calendar.DATE, end.getDayOfMonth());
                        endTime.set(java.util.Calendar.MONTH, newMonth - 1);
                        endTime.set(java.util.Calendar.YEAR, newYear);

                        // 颜色
                        int colorId = getRandomColor();

                        // 设置新事件并存入
                        WeekViewEvent event = new WeekViewEvent(id, name, startTime, endTime);
                        event.setColor(getResources().getColor(colorId));
                        events.add(event);
                    }
                }
                Log.i("Day View", "返回的event列表长度为" + events.size());
                return events;
            }
        });

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(new WeekView.EventLongPressListener() {
            @Override
            public void onEventLongPress(WeekViewEvent event, RectF eventRect) {

            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); // 设置状态栏颜色
        findViewById(R.id.add_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DailyCalendarActivity.this, AddScheduleActivity.class);
                startActivity(intent);
            }
        });
    }

    // 用户按下返回键结束当前Activity
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    // 打开页面或回到页面时如果指定了日期则直接跳转至那个日期
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Day View", "onResume");
        LocalDate clickedDate = MixActivity.getDayClickRecord();
        // 从首页加载
        if (clickedDate != null && ACCESS_FROM_INDEX) {
            ACCESS_FROM_INDEX = false;
            java.util.Calendar targetDate = java.util.Calendar.getInstance();
            targetDate.set(java.util.Calendar.DATE, clickedDate.getDayOfMonth());
            targetDate.set(java.util.Calendar.MONTH, clickedDate.getMonthValue() - 1);
            targetDate.set(java.util.Calendar.YEAR, clickedDate.getYear());
            mWeekView.goToDate(targetDate);
        } else {
            mWeekView.goToToday();
        }
    }
}