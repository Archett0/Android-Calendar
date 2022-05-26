package edu.zjut.androiddeveloper_ailaiziciqi.Calendar;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nlf.calendar.Lunar;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class OldmanActivity extends AppCompatActivity {

    private TextView mYear;
    private TextView mMonth;
    private TextView mDate;
    private TextView mLunarDate;
    private TextView mDayOfWeek;
    private TextView mTodayGood;
    private TextView mTodayBad;
    private Lunar lunarToday;
    private ImageView settingsBtn;
    private List<String> todayGood;
    private List<String> todayBad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oldman);

        // 设置状态栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); // 设置状态栏颜色

        // 设置今天
        lunarToday = Lunar.fromDate(new Date());

        // 设置今天的宜忌
        todayGood = lunarToday.getDayYi();
        todayBad = lunarToday.getDayJi();

        // 转成String类型
        String todayGoodStr = "今日宜: ";
        String todayBadStr = "今日忌: ";
        for (String s : todayGood) {
            todayGoodStr += s;
            todayGoodStr += ",";
        }
        for (String s : todayBad) {
            todayBadStr += s;
            todayBadStr += ",";
        }
        todayGoodStr = todayGoodStr.substring(0, todayGoodStr.length() - 1);
        todayBadStr = todayBadStr.substring(0, todayBadStr.length() - 1);

        // 获取本日的信息
        LocalDate todayDate = LocalDate.now();
        String year = String.valueOf(todayDate.getYear()) + "年";
        String month = lunarToday.getMonthInChinese() + "月";
        String date = String.valueOf(todayDate.getDayOfMonth());
        String dateLunar = lunarToday.getDayInChinese();
        String dayOfWeek = "星期" + lunarToday.getWeekInChinese();

        // 设置本日的黄历
        mYear = findViewById(R.id.old_man_year);
        mMonth = findViewById(R.id.old_man_month);
        mDate = findViewById(R.id.old_man_date);
        mLunarDate = findViewById(R.id.old_man_lunar_date);
        mDayOfWeek = findViewById(R.id.old_man_day_of_week);
        mTodayGood = findViewById(R.id.old_man_good);
        mTodayBad = findViewById(R.id.old_man_bad);
        mYear.setText(year);
        mMonth.setText(month);
        mDate.setText(date);
        mLunarDate.setText(dateLunar);
        mDayOfWeek.setText(dayOfWeek);
        mTodayGood.setText(todayGoodStr);
        mTodayBad.setText(todayBadStr);

        // 监听设置按钮
        settingsBtn = findViewById(R.id.btn_settings);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OldmanActivity.this,SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}