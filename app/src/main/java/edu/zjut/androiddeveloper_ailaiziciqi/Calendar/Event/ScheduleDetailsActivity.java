package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.R;
import edu.zjut.androiddeveloper_ailaiziciqi.calendarview.CalendarView;

/**
 * 日程概览控制类
 */
public class ScheduleDetailsActivity extends AppCompatActivity {


    private ImageView mBackBtn; // 返回按钮
    private TextView mScheduleNameView; // 日程名称
    private TextView mScheduleStartDescriptionView; // 日程开始时间与星期描述
    private TextView mScheduleEndDescriptionView; // 日程结束时间与星期描述
    private TextView mStartTimeView;    // 日程开始时间
    private TextView mStartDateView;    // 日程开始日期
    private TextView mEndTimeView;  // 日程结束时间
    private TextView mEndDateView;  // 日程结束日期
    private TextView mWeather;  // 日程天气
    private TextView mWeatherHint;  // 日程天气提示
    private TextView mScheduleType; // 日程类别
    private BottomNavigationView bottomNavigationMenu;  // 菜单

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_details);

        // 设置状态栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); // 设置状态栏颜色

        // 设置View
        mBackBtn = findViewById(R.id.back);
        mScheduleNameView = findViewById(R.id.single_schedule_name);
        mScheduleStartDescriptionView = findViewById(R.id.single_schedule_start_description);
        mScheduleEndDescriptionView = findViewById(R.id.single_schedule_end_description);
        mStartTimeView = findViewById(R.id.single_schedule_start_time);
        mStartDateView = findViewById(R.id.single_schedule_start_date);
        mEndTimeView = findViewById(R.id.single_schedule_end_time);
        mEndDateView = findViewById(R.id.single_schedule_end_date);
        mWeather = findViewById(R.id.single_schedule_day_weather);
        mWeatherHint = findViewById(R.id.single_schedule_day_weather_description);
        mScheduleType = findViewById(R.id.single_schedule_type);
        bottomNavigationMenu = findViewById(R.id.single_schedule_bottom_navigation);

        // 设置数据
        Intent intent = getIntent();
        mScheduleNameView.setText(intent.getStringExtra("Name"));
        mScheduleStartDescriptionView.setText(intent.getStringExtra("StartDescription"));
        mScheduleEndDescriptionView.setText(intent.getStringExtra("EndDescription"));
        mStartDateView.setText(intent.getStringExtra("Date"));
        mStartTimeView.setText(intent.getStringExtra("Time"));
        mEndDateView.setText(intent.getStringExtra("EndDate"));
        mEndTimeView.setText(intent.getStringExtra("EndTime"));
        mWeather.setText(intent.getStringExtra("Weather"));
        mWeatherHint.setText(intent.getStringExtra("WeatherDetails"));
        mScheduleType.setText(intent.getStringExtra("Type"));

        // 设置返回按钮监听
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        // 设置菜单监听
        bottomNavigationMenu.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.single_event_share:
                        Toast.makeText(ScheduleDetailsActivity.this, "Share Btn Clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.single_event_modify:
                        Toast.makeText(ScheduleDetailsActivity.this, "Modify Btn Clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.single_event_delete:
                        Toast.makeText(ScheduleDetailsActivity.this, "Delete Btn Clicked", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }


}