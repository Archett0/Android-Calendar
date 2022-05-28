package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.add;

import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.getChineseDate;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.isScheduleValid;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.transformUserInputToCorrectForm;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model.Schedule.getScheduleById;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DB.DbContact;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DesktopWidget.DayCalenderWidget;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DesktopWidget.ListRemoteViewsFactory;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DesktopWidget.ListWidgetService;
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
    private TextView mTitle;    // 页面标题
    private Uri mCurrentScheduleUri;    // 上个页面传来的uri
    private int sid;    // 当前日程的id

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_schedule_activity);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        timestart = findViewById(R.id.timestart);
        timeend = findViewById(R.id.timeend);
        scheduleTitle = findViewById(R.id.schedule_title);
        aSwitch = findViewById(R.id.timeswitch);
        mTitle = findViewById(R.id.editor_title);   // 页面标题

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
        aSwitch.setOnCheckedChangeListener(switchEvent);

        back = findViewById(R.id.cancle);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        submit = findViewById(R.id.ok);

        // 判断是添加日程还是修改日程
        Intent lastIntent = getIntent();
        mCurrentScheduleUri = lastIntent.getData();
        sid = lastIntent.getIntExtra("sid", -1);

        // 新建
        if (mCurrentScheduleUri == null || sid == -1) {
            mTitle.setText("新建日程");
        }
        // 修改
        else {
            mTitle.setText("修改日程");
            Schedule schedule = getScheduleById(sid);
            if (schedule != null) {
                scheduleTitle.setText(schedule.getSchedule());
                String startDate = getChineseDate(schedule.getScheduleDate());
                String endDate = getChineseDate(schedule.getScheduleEndDate());
                if (schedule.getScheduleStartTime().equals(LocalTime.of(0, 0)) && schedule.getScheduleEndTime().equals(LocalTime.of(23, 59))) {
                    aSwitch.setChecked(true);
                    timestart.setText(startDate);
                    timeend.setText(endDate);
                } else {
                    timestart.setText(startDate + " " + schedule.getScheduleStartTime().toString());
                    timeend.setText(endDate + " " + schedule.getScheduleEndTime().toString());
                }
            } else {
                mTitle.setText("新建日程");
            }
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isInputValid = false;
                //保存日程信息（主题 开始 结束）
                scheduleTitle.getText();
                // execute to database
                if (!saveSchedule()) {
                    // failed and do nothing
                    Log.i("Random Debug", "添加或更新日程失败");
                } else {
                    // TODO: need to delete
//                    ListRemoteViewsFactory.refresh();
                    finish();
                }
            }
        });
    }

    // 覆写返回键的监听
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //按下的如果是BACK，同时没有重复
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 检查用户输入是否合法，并将合法的用户输入写入数据库
     */
    private boolean saveSchedule() {

        // 保存用户输入
        String scheduleName = scheduleTitle.getText().toString().trim();    // event name: done
        String tmpStartString = timestart.getText().toString().trim();  // raw start data
        String tmpEndString = timeend.getText().toString().trim();  // raw end data

        // 判断是否为全天事件
        if (isSwitched) {
            tmpStartString += " 00:00";
            tmpEndString += " 23:59";
        }

        // 用工具类处理用户输入
        Schedule readySchedule = transformUserInputToCorrectForm(scheduleName, tmpStartString, tmpEndString, month_start, month_end);

        // 处理完毕,获取处理好的值
        String scheduleDate = String.valueOf(readySchedule.getScheduleDate());
        String scheduleEndDate = String.valueOf(readySchedule.getScheduleEndDate());
        String scheduleStartTime = String.valueOf(readySchedule.getScheduleStartTime());
        String scheduleEndTime = String.valueOf(readySchedule.getScheduleEndTime());
        String scheduleWeek = String.valueOf(readySchedule.getWeek());
        String scheduleLunar = String.valueOf(readySchedule.getLunar());

        // Judge if the inputs are all empty
        if (TextUtils.isEmpty(scheduleName)
                && TextUtils.isEmpty(scheduleDate)
                && TextUtils.isEmpty(scheduleEndDate)
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
            Toast.makeText(this, "必须选择开始日期", Toast.LENGTH_SHORT).show();
            return isInputValid;
        } else {
            values.put(DbContact.ScheduleEntry.COLUMN_START_DATE, scheduleDate);
        }

        if (TextUtils.isEmpty(scheduleEndDate)) {
            Toast.makeText(this, "必须选择结束日期", Toast.LENGTH_SHORT).show();
            return isInputValid;
        } else {
            values.put(DbContact.ScheduleEntry.COLUMN_END_DATE, scheduleEndDate);
        }

        if (TextUtils.isEmpty(scheduleStartTime)) {
            Toast.makeText(this, "必须选择开始时间", Toast.LENGTH_SHORT).show();
            return isInputValid;
        } else {
            values.put(DbContact.ScheduleEntry.COLUMN_START_TIME, scheduleStartTime);
        }

        if (TextUtils.isEmpty(scheduleEndTime)) {
            Toast.makeText(this, "必须选择结束时间", Toast.LENGTH_SHORT).show();
            return isInputValid;
        } else {
            values.put(DbContact.ScheduleEntry.COLUMN_END_TIME, scheduleEndTime);
            values.put(DbContact.ScheduleEntry.COLUMN_WEEK, scheduleWeek);
            values.put(DbContact.ScheduleEntry.COLUMN_LUNAR, scheduleLunar);
        }

        // 使用工具类检查用户输入是否合理
        String errorMsg = isScheduleValid(readySchedule);
        if (errorMsg != null) {
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            return isInputValid;
        }

        if (mCurrentScheduleUri == null) {
            Uri newUri = getContentResolver().insert(DbContact.ScheduleEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, "保存出错", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "成功保存", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected =getContentResolver().update(mCurrentScheduleUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, "更新出错", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "成功更新", Toast.LENGTH_SHORT).show();
            }
        }
        isInputValid = true;
        return isInputValid;
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