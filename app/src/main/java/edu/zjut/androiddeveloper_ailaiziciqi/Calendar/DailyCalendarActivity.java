package edu.zjut.androiddeveloper_ailaiziciqi.Calendar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model.Schedule;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.HourAdapter;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.HourEvent;
import edu.zjut.androiddeveloper_ailaiziciqi.calendarview.Calendar;
import edu.zjut.androiddeveloper_ailaiziciqi.calendarview.CalendarLayout;
import edu.zjut.androiddeveloper_ailaiziciqi.calendarview.CalendarView;

public class DailyCalendarActivity extends AppCompatActivity implements
        CalendarView.OnCalendarSelectListener,
        View.OnClickListener {

    private CalendarView mCalendarView;
    private CalendarLayout mCalendarLayout;

    private ListView hourListView;
    private LocalDate selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_calendar);
        mCalendarView = findViewById(R.id.calendarView);
        mCalendarLayout = findViewById(R.id.calendarLayout);
        mCalendarView.setOnCalendarSelectListener(this);
        selectedDate = LocalDate.now();
        initWidgets();
    }

    // 初始化View
    private void initWidgets() {
        hourListView = findViewById(R.id.hourListView);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        setDayView();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); // 设置状态栏颜色
        findViewById(R.id.add_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DailyCalendarActivity.this, "Add Event Button Clicked", Toast.LENGTH_SHORT).show();

            }
        });
    }

    // 设置当前日期并设置当天的时间cell与日程
    private void setDayView() {
        //TODO: 设置当前日期
        setHourAdapter();
    }

    // 当日的每个小时和其对应的事件集合
    private ArrayList<HourEvent> hourEventList() {
        ArrayList<HourEvent> list = new ArrayList<>();
        for (int hour = 0; hour < 24; ++hour) {
            LocalDate date = selectedDate;   // 获取用户选中的日期,默认为当天
            LocalTime time = LocalTime.of(hour, 0); // 当前在设置的小时
            ArrayList<Schedule> schedules = Schedule.eventsForDateAndTime(date, time);   // 当前在设置的小时所包含的所有日程
            HourEvent hourEvent = new HourEvent(time, schedules);
            list.add(hourEvent);
        }
        return list;
    }

    // 设置HourAdapter
    private void setHourAdapter() {
        HourAdapter hourAdapter = new HourAdapter(getApplicationContext(), hourEventList());
        hourListView.setAdapter(hourAdapter);
    }

    // 用户按下返回键结束当前Activity
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        selectedDate = LocalDate.of(calendar.getYear(),calendar.getMonth(),calendar.getDay());
        setHourAdapter();
        Log.i("Daily View Date Selected", "  -- " + selectedDate);
    }

    @Override
    public void onClick(View view) {

    }
}