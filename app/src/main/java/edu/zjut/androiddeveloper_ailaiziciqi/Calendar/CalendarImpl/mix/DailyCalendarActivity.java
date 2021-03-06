package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.mix;

import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.SCHEDULE_DESCRIPTION_END;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.SCHEDULE_DESCRIPTION_START;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.WEATHER_REPORTS;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.generateScheduleDescription;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.getRandomColor;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.getScheduleWeatherReport;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.loadOrReloadDataFromDatabase;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Model.Schedule.getScheduleById;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Model.Schedule.schedulesForMonth;

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
import com.nlf.calendar.Solar;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.add.AddScheduleActivity;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DB.DbContact;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleDetailsActivity;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Model.Schedule;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.R;

public class DailyCalendarActivity extends AppCompatActivity {

    private WeekView mWeekView; // ?????????
    private TextView mTodayBtn; // ??????????????????
    private ImageView mBack; // ????????????
    Context context;    // ?????????
    private boolean ACCESS_FROM_INDEX = true;    // ???????????????????????????????????????

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_calendar);

        // ??????Context
        context = DailyCalendarActivity.this;

        // ????????????????????????????????????
        mWeekView = findViewById(R.id.weekView);
        mTodayBtn = findViewById(R.id.hint_text);
        // ???????????????????????????
        mWeekView.setNumberOfVisibleDays(1);

        // ????????????View?????????
        mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
        mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));

        // ???????????????
        mBack = findViewById(R.id.today_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // ???????????????????????????????????????
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(java.util.Calendar date) {
                Solar solar = Solar.fromDate(date.getTime());
                String solarDate = "??????" + solar.getWeekInChinese() + "  " + solar.getMonth() + "???" + solar.getDay() + "???                 ";
                return solarDate;
            }

            @Override
            public String interpretTime(int hour, int minutes) {
                String result = "";
                if (hour == 0) {
                    result = "??????12???";
                } else if (hour > 11) {
                    result = "??????" + (hour - 12) + "???";
                } else if (hour < 7) {
                    result = "??????" + hour + "???";
                } else {
                    result = "??????" + hour + "???";
                }
//                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
                return result;
            }
        });

        // ???????????????????????????
        mTodayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                java.util.Calendar today = java.util.Calendar.getInstance();
                mWeekView.goToDate(today);
            }
        });

        // ?????????????????????
        mWeekView.setOnEventClickListener(new WeekView.EventClickListener() {
            @Override
            public void onEventClick(WeekViewEvent event, RectF eventRect) {
                String _id = event.getIdentifier();
                Schedule schedule = getScheduleById(Integer.parseInt(_id));
                if (schedule == null) {
                    Toast.makeText(context, "?????????,?????????", Toast.LENGTH_SHORT).show();
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
                    // ?????????????????????,?????????
                    if (WEATHER_REPORTS != null && !WEATHER_REPORTS.isEmpty()) {
                        int weatherIndex = getScheduleWeatherReport(schedule);
                        if (weatherIndex != -1) {
                            intent.putExtra("Weather", WEATHER_REPORTS.get(weatherIndex).getWeather());
                            intent.putExtra("WeatherDetails", WEATHER_REPORTS.get(weatherIndex).getWeatherDetails());
                        } else {
                            intent.putExtra("Weather", "??????????????????");
                            intent.putExtra("WeatherDetails", "??????????????????");
                        }
                    } else {
                        intent.putExtra("Weather", "??????????????????");
                        intent.putExtra("WeatherDetails", "??????????????????");
                    }
                    intent.putExtra("Type", "????????????");
                    startActivity(intent);
                }
            }
        });

        // ???????????????
        mWeekView.setMonthChangeListener(new MonthLoader.MonthChangeListener() {
            @Override
            public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                Log.i("Day View", "??????" + newMonth + "???");
                // ?????????List????????????
                List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();

                // ??????????????????
                loadOrReloadDataFromDatabase(context.getContentResolver(), "Day Load");

                // If there's no data in the DB, we load a default data.
                if (Schedule.scheduleArrayList.isEmpty()) {
                    Log.i("Day View", "???????????????");
                    java.util.Calendar startTime = java.util.Calendar.getInstance();
                    startTime.set(java.util.Calendar.HOUR_OF_DAY, 12);
                    startTime.set(java.util.Calendar.MINUTE, 0);
                    startTime.set(java.util.Calendar.MONTH, newMonth);
                    startTime.set(java.util.Calendar.YEAR, newYear);
                    java.util.Calendar endTime = (java.util.Calendar) startTime.clone();
                    endTime.add(java.util.Calendar.HOUR, 1);
                    endTime.set(java.util.Calendar.MONTH, newMonth);
                    WeekViewEvent event = new WeekViewEvent("DEFAULT", "???????????????????????????", startTime, endTime);
                    event.setColor(getResources().getColor(R.color.event_color_01));
                    events.add(event);
                }
                // if not, we load data from static List(only the data in this month)
                else {
                    Log.i("Day View", "???????????????,????????????");
                    ArrayList<Schedule> schedules = schedulesForMonth(newYear, newMonth);
                    // if there's no schedules in this month, just return the empty list
                    if (schedules.isEmpty()) {
                        Log.i("Day View", "??????????????????????????????");
                        return events;
                    }
                    Log.i("Day View", "????????????????????????????????????????????????" + schedules.size());
                    // if not, we transform these data and feed to the view
                    for (Schedule schedule : schedules) {
                        String id = String.valueOf(schedule.getId());
                        String name = schedule.toShortString();
                        LocalDate start = schedule.getScheduleDate();
                        LocalDate end = schedule.getScheduleEndDate();
                        LocalTime startLocalTime = schedule.getScheduleStartTime();
                        LocalTime endLocalTime = schedule.getScheduleEndTime();

                        // ????????????
                        java.util.Calendar startTime = java.util.Calendar.getInstance();
                        startTime.set(java.util.Calendar.HOUR_OF_DAY, startLocalTime.getHour());
                        startTime.set(java.util.Calendar.MINUTE, startLocalTime.getMinute());
                        startTime.set(java.util.Calendar.DATE, start.getDayOfMonth());
                        startTime.set(java.util.Calendar.MONTH, newMonth - 1);
                        startTime.set(java.util.Calendar.YEAR, newYear);

                        // ????????????
                        java.util.Calendar endTime = java.util.Calendar.getInstance();
                        endTime.set(java.util.Calendar.HOUR_OF_DAY, endLocalTime.getHour());
                        endTime.set(java.util.Calendar.MINUTE, endLocalTime.getMinute());
                        endTime.set(java.util.Calendar.DATE, end.getDayOfMonth());
                        endTime.set(java.util.Calendar.MONTH, newMonth - 1);
                        endTime.set(java.util.Calendar.YEAR, newYear);

                        // ??????
                        int colorId = getRandomColor();

                        // ????????????????????????
                        WeekViewEvent event = new WeekViewEvent(id, name, startTime, endTime);
                        event.setColor(getResources().getColor(colorId));
                        events.add(event);
                    }
                }
                Log.i("Day View", "?????????event???????????????" + events.size());
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
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); // ?????????????????????
        findViewById(R.id.add_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DailyCalendarActivity.this, AddScheduleActivity.class);
                startActivity(intent);
            }
        });
    }

    // ?????????????????????????????????Activity
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    // ?????????????????????????????????????????????????????????????????????????????????
    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Day View", "onResume");
        LocalDate clickedDate = MixActivity.getDayClickRecord();
        // ???????????????
        if (clickedDate != null && ACCESS_FROM_INDEX) {
            ACCESS_FROM_INDEX = false;
            java.util.Calendar targetDate = java.util.Calendar.getInstance();
            targetDate.set(java.util.Calendar.DATE, clickedDate.getDayOfMonth());
            targetDate.set(java.util.Calendar.MONTH, clickedDate.getMonthValue() - 1);
            targetDate.set(java.util.Calendar.YEAR, clickedDate.getYear());
            mWeekView.goToDate(targetDate);
            Log.i("Day View", "???????????????" + targetDate.getTime());
        } else {
            mWeekView.goToToday();
        }
    }
}