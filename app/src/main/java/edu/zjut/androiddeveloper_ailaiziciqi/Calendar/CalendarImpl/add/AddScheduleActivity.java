package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.add;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.nlf.calendar.Lunar;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.mix.MixActivity;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DB.DbContact;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.R;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model.Schedule;

public class AddScheduleActivity extends AppCompatActivity {
    private TextView timestart, timeend;
    private Calendar cal;
    private int year_start, month_start, day_start;
    private int hour_start, min_start;

    private int year_end, month_end, day_end;
    private int hour_end, min_end;

    private ImageButton timeStartSpinner, timeEndSpinner;

    private boolean isSwitched = false;
    private Switch aSwitch;

    private ImageView back, submit;

    private EditText scheduleTitle;

    private boolean isInputValid;   // 记录用户输入是否合法的Flag
    private Cursor mCursor; // 重新加载数据需要使用的Cursor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);


        timestart = findViewById(R.id.timestart);
        timeend = findViewById(R.id.timeend);

        getDate(1);
        String fixhour_start = "";
        if (hour_start >= 0 && hour_start <= 9) {
            fixhour_start = "0";
        }
        ClickEvent clickEventTimeStart = new ClickEvent(timestart, year_start, month_start, day_start, hour_end, min_end);
        timestart.setText(year_start + "年" + (month_start + 1) + "月" + day_start + "日" + " " + fixhour_start + hour_start + ":00");

        getDate(2);
        String fixhour_end = "";
        if (hour_end >= 0 && hour_end <= 9) {
            fixhour_end = "0";
        }
        ClickEvent clickEventTimeEnd = new ClickEvent(timeend, year_end, month_end, day_end, hour_end, min_end);
        timeend.setText(year_end + "年" + (month_end + 1) + "月" + day_end + "日" + " " + fixhour_end + hour_end + ":00");

        timeStartSpinner = findViewById(R.id.timestartspinner);
        timeStartSpinner.setOnClickListener(clickEventTimeStart);

        timeEndSpinner = findViewById(R.id.timeendspinner);
        timeEndSpinner.setOnClickListener(clickEventTimeEnd);

        SwitchEvent switchEvent = new SwitchEvent();
        aSwitch = findViewById(R.id.timeswitch);
        aSwitch.setOnCheckedChangeListener(switchEvent);

        back = findViewById(R.id.cancle);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddScheduleActivity.this, MixActivity.class);
                startActivity(intent);
                finish();
            }
        });

        submit = findViewById(R.id.ok);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isInputValid = false;
                //保存日程信息（主题 开始 结束）
                scheduleTitle.getText();
                // execute to database
                if (saveSchedule() == false) {
                    // failed and do nothing
                    Toast.makeText(AddScheduleActivity.this, "添加日程失败", Toast.LENGTH_SHORT).show();
                } else {
                    // success and we should reload the data from db to static list
                    reloadDataFromDatabase();
                    Intent intent = new Intent(AddScheduleActivity.this, MixActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
        scheduleTitle = findViewById(R.id.schedule_title);
    }

    // 覆写返回键的监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            Intent intent = new Intent(AddScheduleActivity.this, MixActivity.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // 检查用户输入是否合法，并将合法的用户输入写入数据库
    private boolean saveSchedule() {

        // 保存结果
        String scheduleName = scheduleTitle.getText().toString().trim();    // event name: done
        String tmpStartString = timestart.getText().toString().trim();  // raw start data
        String tmpEndString = timeend.getText().toString().trim();  // raw end data
        LocalDate date = LocalDate.of(Integer.parseInt(tmpStartString.substring(0, 4)), month_start + 1, Integer.parseInt(tmpStartString.substring(7, 9)));
        LocalTime time = LocalTime.of(Integer.parseInt(tmpStartString.substring(tmpStartString.length() - 5, tmpStartString.length() - 3)), Integer.parseInt(tmpStartString.substring(tmpStartString.length() - 2, tmpStartString.length())));
        LocalTime endTime = LocalTime.of(Integer.parseInt(tmpEndString.substring(tmpEndString.length() - 5, tmpEndString.length() - 3)), Integer.parseInt(tmpEndString.substring(tmpEndString.length() - 2, tmpEndString.length())));
        Log.i("Random Debug", "Trying to save:" + String.valueOf(time));
        Log.i("Random Debug", "Trying to save:" + String.valueOf(endTime));
        String scheduleDate = String.valueOf(date);
        String scheduleStartTime = String.valueOf(time);
        String scheduleEndTime = String.valueOf(endTime);
        Lunar todayLunar = Lunar.fromDate(new Date());
        String scheduleWeek = "星期" + todayLunar.getWeekInChinese();
        String scheduleLunar = todayLunar.getDayInChinese();

        // Judge if the inputs are all empty
        if (TextUtils.isEmpty(scheduleName)
                && TextUtils.isEmpty(scheduleDate)
                && TextUtils.isEmpty(scheduleStartTime)
                && TextUtils.isEmpty(scheduleEndTime)
                && TextUtils.isEmpty(scheduleWeek)
                && TextUtils.isEmpty(scheduleLunar)) {
            isInputValid = true;
            return isInputValid;
        }

        // Judge if any input is invalid
        ContentValues values = new ContentValues();

        if (TextUtils.isEmpty(scheduleName)) {
            Toast.makeText(this, "必须输入主题", Toast.LENGTH_SHORT).show();
            return isInputValid;
        } else {
            values.put(DbContact.ScheduleEntry.COLUMN_EVENT_NAME, scheduleName);
        }

        if (TextUtils.isEmpty(scheduleDate)) {
            Toast.makeText(this, "必须选择日期", Toast.LENGTH_SHORT).show();
            return isInputValid;
        } else {
            values.put(DbContact.ScheduleEntry.COLUMN_DATE, scheduleDate);
        }

        if (TextUtils.isEmpty(scheduleStartTime)) {
            Toast.makeText(this, "必须选择日期", Toast.LENGTH_SHORT).show();
            return isInputValid;
        } else {
            values.put(DbContact.ScheduleEntry.COLUMN_START_TIME, scheduleStartTime);
        }

        if (TextUtils.isEmpty(scheduleEndTime)) {
            Toast.makeText(this, "必须选择日期", Toast.LENGTH_SHORT).show();
            return isInputValid;
        } else {
            values.put(DbContact.ScheduleEntry.COLUMN_END_TIME, scheduleEndTime);
            values.put(DbContact.ScheduleEntry.COLUMN_WEEK, scheduleWeek);
            values.put(DbContact.ScheduleEntry.COLUMN_LUNAR, scheduleLunar);
        }

        // check if end is earlier than start
        if (time.isAfter(endTime)) {
            Toast.makeText(this, "结束时间不可早于开始时间", Toast.LENGTH_SHORT).show();
            return isInputValid;
        }

        Uri newUri = getContentResolver().insert(DbContact.ScheduleEntry.CONTENT_URI, values);
        if (newUri == null) {
            Toast.makeText(this, "保存出错", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "成功保存", Toast.LENGTH_SHORT).show();
        }
        isInputValid = true;
        return isInputValid;
    }

    private void reloadDataFromDatabase() {
        String[] projection = {DbContact.ScheduleEntry._ID,
                DbContact.ScheduleEntry.COLUMN_EVENT_NAME,
                DbContact.ScheduleEntry.COLUMN_DATE,
                DbContact.ScheduleEntry.COLUMN_START_TIME,
                DbContact.ScheduleEntry.COLUMN_END_TIME,
                DbContact.ScheduleEntry.COLUMN_WEEK,
                DbContact.ScheduleEntry.COLUMN_LUNAR
        };
        mCursor = getContentResolver().query(DbContact.ScheduleEntry.CONTENT_URI, projection, null, null, null);
        // clear the old static list
        if (Schedule.scheduleArrayList.size() != 0) {
            Schedule.scheduleArrayList.clear();
        }
        // retrieve data from database
        if (mCursor != null && mCursor.moveToFirst()) {
            do {
                /// 获取Column的位置
                int scheduleIndex = mCursor.getColumnIndex(DbContact.ScheduleEntry.COLUMN_EVENT_NAME);
                int scheduleDateIndex = mCursor.getColumnIndex(DbContact.ScheduleEntry.COLUMN_DATE);
                int scheduleStartTimeIndex = mCursor.getColumnIndex(DbContact.ScheduleEntry.COLUMN_START_TIME);
                int scheduleEndTimeIndex = mCursor.getColumnIndex(DbContact.ScheduleEntry.COLUMN_END_TIME);
                int weekIndex = mCursor.getColumnIndex(DbContact.ScheduleEntry.COLUMN_WEEK);
                int lunarIndex = mCursor.getColumnIndex(DbContact.ScheduleEntry.COLUMN_LUNAR);
                // 取值
                String scheduleValue = mCursor.getString(scheduleIndex);
                String scheduleDateValue = mCursor.getString(scheduleDateIndex);
                String scheduleStartTimeValue = mCursor.getString(scheduleStartTimeIndex);
                String scheduleEndTimeValue = mCursor.getString(scheduleEndTimeIndex);
                String weekValue = mCursor.getString(weekIndex);
                String lunarValue = mCursor.getString(lunarIndex);
                // 转换
                LocalDate date = LocalDate.parse(scheduleDateValue);
                LocalTime time = LocalTime.parse(scheduleStartTimeValue);
                LocalTime endTime = LocalTime.parse(scheduleEndTimeValue);
                // 保存
                Schedule newSchedule = new Schedule(date, time, endTime, weekValue, lunarValue, scheduleValue);
                Schedule.scheduleArrayList.add(newSchedule);
            } while (mCursor.moveToNext());
            // Tag
            Log.i("Random Debug", "Reloaded from database");
            Log.i("Random Debug", "Data loaded:" + Schedule.scheduleArrayList.size());
        }
    }

    private void getDate(int num) {
        if (num == 1) {
            cal = Calendar.getInstance();
            year_start = cal.get(Calendar.YEAR);       //获取年月日时分秒
            month_start = cal.get(Calendar.MONTH);   //获取到的月份是从0开始计数
            day_start = cal.get(Calendar.DAY_OF_MONTH);
            hour_start = cal.get(Calendar.HOUR_OF_DAY);
            min_start = 0;
        } else {
            cal = Calendar.getInstance();
            cal.set(Calendar.HOUR, 1);
            year_end = cal.get(Calendar.YEAR);       //获取年月日时分秒
            month_end = cal.get(Calendar.MONTH);   //获取到的月份是从0开始计数
            day_end = cal.get(Calendar.DAY_OF_MONTH);
            hour_end = cal.get(Calendar.HOUR_OF_DAY);
            min_end = 0;
        }

    }

    private class ClickEvent implements View.OnClickListener {
        private TextView textView;
        private int year, month, day;
        private int hour, min;

        public ClickEvent(TextView textView, int year, int month, int day, int hour, int min) {
            this.textView = textView;
            this.year = year;
            this.month = month;
            this.day = day;
            this.hour = hour;
            this.min = min;
        }

        @Override
        public void onClick(View view) {
            DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker arg0, int year, int month, int day) {
                    textView.setText(year + "年" + (month + 1) + "月" + day + "日");      //将选择的日期显示到TextView中,因为之前获取month直接使用，所以不需要+1，这个地方需要显示，所以+1

                    if (isSwitched) return;

                    TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {
                            String fixhour = "";
                            if (i >= 0 && i <= 9) {
                                fixhour = "0";
                            }

                            String fixmin = "";
                            if (i1 >= 0 && i1 <= 9) {
                                fixmin = "0";
                            }
                            textView.setText(textView.getText() + " " + fixhour + i + ":" + fixmin + i1);
                        }
                    };
                    TimePickerDialog timePickerDialog = new TimePickerDialog(AddScheduleActivity.this, timeSetListener, hour, min, true);
                    timePickerDialog.show();
                }
            };
            DatePickerDialog dialog = new DatePickerDialog(AddScheduleActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, listener, year, month, day);//后边三个参数为显示dialog时默认的日期，月份从0开始，0-11对应1-12个月
            dialog.show();


        }
    }

    private class SwitchEvent implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                timestart.setText(year_start + "年" + (month_start + 1) + "月" + day_start + "日");
                timeend.setText(year_end + "年" + (month_end + 1) + "月" + day_end + "日");
                isSwitched = true;
            } else {
                String fixhour_start = "";
                if (hour_start >= 0 && hour_start <= 9) {
                    fixhour_start = "0";
                }
                String fixhour_end = "";
                if (hour_end >= 0 && hour_end <= 9) {
                    fixhour_end = "0";
                }
                timestart.setText(year_start + "年" + (month_start + 1) + "月" + day_start + "日" + " " + fixhour_start + hour_start + ":00");
                timeend.setText(year_end + "年" + (month_end + 1) + "月" + day_end + "日" + " " + fixhour_end + hour_end + ":00");
                isSwitched = false;
            }
        }
    }
}