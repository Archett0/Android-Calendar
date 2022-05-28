package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.add.AddScheduleActivity;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.R;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.databinding.AddScheduleActivityBinding;
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
    private Uri mCurrentScheduleUri;    // 当前日程的Uri
    private String scheduleTextForShare;    // 当前日程分享用描述文字
    private int scheduleId; // 日程id

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
        mCurrentScheduleUri = intent.getData();
        scheduleId = intent.getIntExtra("sid", -1);
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
        scheduleTextForShare = "日程\"" + intent.getStringExtra("Name") + "\"： "
                + intent.getStringExtra("StartDescription") + "， "
                + intent.getStringExtra("EndDescription") + "， 日程从"
                + intent.getStringExtra("Time") + "开始, 到"
                + intent.getStringExtra("EndTime") + "结束。当天天气是"
                + intent.getStringExtra("Weather") + "具体说， "
                + intent.getStringExtra("WeatherDetails") + "。本日程属于日历："
                + intent.getStringExtra("Type") + "。";

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
                        shareSchedule();
                        return true;
                    case R.id.single_event_modify:
                        // 进入编辑页面
                        Intent intent = new Intent(ScheduleDetailsActivity.this, AddScheduleActivity.class);
                        intent.setData(mCurrentScheduleUri);
                        intent.putExtra("sid", scheduleId);
                        startActivity(intent);
                        finish();
                        return true;
                    case R.id.single_event_delete:
                        showDeleteConfirmationDialog();
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    // 日程分享功能
    private void shareSchedule() {
        String shareString = (scheduleTextForShare == null) ? "分享不可用" : scheduleTextForShare;
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareString);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }


    // 编辑界面的确认删除功能
    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确定删除这个日程？");

        // 用户确认删除
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        // 不删除
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // 生成并显示确认弹窗
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // 执行删除
    private void deleteProduct() {
        if (mCurrentScheduleUri != null) {
            int rowsEffected = getContentResolver().delete(mCurrentScheduleUri, null, null);
            if (rowsEffected == 0) {
                // 如果没有一行被删除，报错toast
                Toast.makeText(this, "删除错误", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            Toast.makeText(ScheduleDetailsActivity.this, "无法获取Uri,错误发生", Toast.LENGTH_SHORT).show();
        }
    }
}