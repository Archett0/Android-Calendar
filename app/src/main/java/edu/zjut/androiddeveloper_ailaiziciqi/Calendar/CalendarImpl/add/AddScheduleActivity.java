package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.add;

import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.getChineseDate;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.isScheduleValid;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.loadOrReloadDataFromDatabase;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.transformUserInputToCorrectForm;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Model.Schedule.getScheduleById;

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

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DB.DbContact;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DesktopWidget.DayCalenderWidget;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DesktopWidget.ListRemoteViewsFactory;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DesktopWidget.ListWidgetService;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.R;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Model.Schedule;

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

    private boolean isInputValid;   // ?????????????????????????????????Flag
    private Cursor mCursor; // ?????????????????????????????????Cursor
    private TextView mTitle;    // ????????????
    private Uri mCurrentScheduleUri;    // ?????????????????????uri
    private int sid;    // ???????????????id

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
        mTitle = findViewById(R.id.editor_title);   // ????????????

        getDate(1);
        String fixhour_start = "";
        if (hour_start >= 0 && hour_start <= 9) {
            fixhour_start = "0";
        }
        ClickEvent clickEventTimeStart = new ClickEvent(timestart, year_start, month_start, day_start, hour_end, min_end, timeend, "end");
        timestart.setText(year_start + "???" + (month_start + 1) + "???" + day_start + "???" + " " + fixhour_start + hour_start + ":00");

        getDate(2);
        String fixhour_end = "";
        if (hour_end >= 0 && hour_end <= 9) {
            fixhour_end = "0";
        }
        ClickEvent clickEventTimeEnd = new ClickEvent(timeend, year_end, month_end, day_end, hour_end, min_end, timestart, "start");
        timeend.setText(year_end + "???" + (month_end + 1) + "???" + day_end + "???" + " " + fixhour_end + hour_end + ":00");

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

        // ???????????????????????????????????????
        Intent lastIntent = getIntent();
        mCurrentScheduleUri = lastIntent.getData();
        sid = lastIntent.getIntExtra("sid", -1);

        // ??????
        if (mCurrentScheduleUri == null || sid == -1) {
            mTitle.setText("????????????");
        }
        // ??????
        else {
            mTitle.setText("????????????");
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
                mTitle.setText("????????????");
            }
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isInputValid = false;
                //??????????????????????????? ?????? ?????????
                scheduleTitle.getText();
                // execute to database
                if (!saveSchedule()) {
                    // failed and do nothing
                    Log.i("Random Debug", "???????????????????????????");
                } else {
                    loadOrReloadDataFromDatabase(getContentResolver(), "Day Load");
                    try {
                        DayCalenderWidget.updateView();
                    } catch (Exception e) {
                        Log.w("ListRemoteViewsFactory refresh exceprion", e.getMessage());
                    }
//                    ListRemoteViewsFactory.refresh();
                    finish();
                }
            }
        });
    }

    // ????????????????????????
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) { //??????????????????BACK?????????????????????
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * ???????????????????????????????????????????????????????????????????????????
     */
    private boolean saveSchedule() {

        // ??????????????????
        String scheduleName = scheduleTitle.getText().toString().trim();    // event name: done
        String tmpStartString = timestart.getText().toString().trim();  // raw start data
        String tmpEndString = timeend.getText().toString().trim();  // raw end data

        // ???????????????????????????
        if (isSwitched) {
            tmpStartString += " 00:00";
            tmpEndString += " 23:59";
        }

        // ??????????????????????????????
        Schedule readySchedule = transformUserInputToCorrectForm(scheduleName, tmpStartString, tmpEndString);

        // ????????????,?????????????????????
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
            Toast.makeText(this, "??????????????????", Toast.LENGTH_SHORT).show();
            return isInputValid;
        } else {
            values.put(DbContact.ScheduleEntry.COLUMN_EVENT_NAME, scheduleName);
        }

        if (TextUtils.isEmpty(scheduleDate)) {
            Toast.makeText(this, "????????????????????????", Toast.LENGTH_SHORT).show();
            return isInputValid;
        } else {
            values.put(DbContact.ScheduleEntry.COLUMN_START_DATE, scheduleDate);
        }

        if (TextUtils.isEmpty(scheduleEndDate)) {
            Toast.makeText(this, "????????????????????????", Toast.LENGTH_SHORT).show();
            return isInputValid;
        } else {
            values.put(DbContact.ScheduleEntry.COLUMN_END_DATE, scheduleEndDate);
        }

        if (TextUtils.isEmpty(scheduleStartTime)) {
            Toast.makeText(this, "????????????????????????", Toast.LENGTH_SHORT).show();
            return isInputValid;
        } else {
            values.put(DbContact.ScheduleEntry.COLUMN_START_TIME, scheduleStartTime);
        }

        if (TextUtils.isEmpty(scheduleEndTime)) {
            Toast.makeText(this, "????????????????????????", Toast.LENGTH_SHORT).show();
            return isInputValid;
        } else {
            values.put(DbContact.ScheduleEntry.COLUMN_END_TIME, scheduleEndTime);
            values.put(DbContact.ScheduleEntry.COLUMN_WEEK, scheduleWeek);
            values.put(DbContact.ScheduleEntry.COLUMN_LUNAR, scheduleLunar);
        }

        // ?????????????????????????????????????????????
        String errorMsg = isScheduleValid(readySchedule);
        if (errorMsg != null) {
            Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
            return isInputValid;
        }

        if (mCurrentScheduleUri == null) {
            Uri newUri = getContentResolver().insert(DbContact.ScheduleEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentScheduleUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
            }
        }
        isInputValid = true;
        return isInputValid;
    }

    private void getDate(int num) {
        if (num == 1) {
            cal = Calendar.getInstance();
            year_start = cal.get(Calendar.YEAR);       //????????????????????????
            month_start = cal.get(Calendar.MONTH);   //????????????????????????0????????????
            day_start = cal.get(Calendar.DAY_OF_MONTH);
            hour_start = cal.get(Calendar.HOUR_OF_DAY);
            min_start = 0;
        } else {
            cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY) + 1);
            year_end = cal.get(Calendar.YEAR);       //????????????????????????
            month_end = cal.get(Calendar.MONTH);   //????????????????????????0????????????
            day_end = cal.get(Calendar.DAY_OF_MONTH);
            hour_end = cal.get(Calendar.HOUR_OF_DAY);
            Log.w("hour_end", hour_end + "");
            min_end = 0;
        }

    }

    private class ClickEvent implements View.OnClickListener {
        private TextView textView;
        private int year, month, day;
        private int hour, min;
        private TextView otherTextView;
        private String whichOtherTextView;

        //        TextView textView: ?????????textview???     TextView otherTextView??????????????????textview;       String whichOtherTextView:??????????????????textview
        public ClickEvent(TextView textView, int year, int month, int day, int hour, int min, TextView otherTextView, String whichOtherTextView) {
            this.textView = textView;
            this.year = year;
            this.month = month;
            this.day = day;
            this.hour = hour;
            this.min = min;
            this.otherTextView = otherTextView;
            this.whichOtherTextView = whichOtherTextView;
        }

        @Override
        public void onClick(View view) {
            DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker arg0, int year, int month, int day) {

                    if (isSwitched) {
                        textView.setText(year + "???" + (month + 1) + "???" + day + "???");      //???????????????????????????TextView???,??????????????????month??????????????????????????????+1????????????????????????????????????+1
                        return;
                    }

                    TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int hour, int minute) {
                            String fixhour = "";
                            if (hour >= 0 && hour <= 9) {
                                fixhour = "0";
                            }

                            String fixmin = "";
                            if (minute >= 0 && minute <= 9) {
                                fixmin = "0";
                            }

                            if (whichOtherTextView.equals("end")) { //????????????start???????????????end
                                if (year_end <= year || month_end <= month || day_end <= day || hour_end <= hour || min_end <= minute) {
                                    otherTextView.setText(year + "???" + (month + 1) + "???" + day + "???" + " " + fixhour + (hour + 1) + ":" + fixmin + minute);
                                    year_end = year;
                                    month_end = month;
                                    day_end = day;
                                    hour_end = hour + 1;
                                    min_end = minute;
                                }

                                textView.setText(year + "???" + (month + 1) + "???" + day + "???" + " " + fixhour + hour + ":" + fixmin + minute);
                                year_start = year;
                                month_start = month;
                                day_start = day;
                                hour_start = hour;
                                min_start = minute;

                            } else if (whichOtherTextView.equals("start")) {
                                Log.w("starttime", year_start + "???" + (month_start + 1) + "???" + day_start + "???" + " " + fixhour + hour_start + ":" + fixmin + min_start);
                                Log.w("endtime", year + "???" + (month + 1) + "???" + day + "???" + " " + fixhour + hour + ":" + fixmin + minute);


                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                                Calendar calendar_start = Calendar.getInstance();
                                Calendar calendar_end = Calendar.getInstance();
                                try {
                                    calendar_start.setTime(sdf.parse(year_start + "-" + (month_start + 1) + "-" + day_start + " " + fixhour + hour_start + ":" + fixmin + min_start + ":00"));
                                    calendar_end.setTime(sdf.parse(year + "-" + (month + 1) + "-" + day + " " + fixhour + hour + ":" + fixmin + minute + ":00"));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                                if (calendar_start.before(calendar_end)) {
                                    Log.w("starttime", year_start + "???" + (month_start + 1) + "???" + day_start + "???" + " " + fixhour + hour_start + ":" + fixmin + min_start);
                                    Log.w("endtime", year + "???" + (month + 1) + "???" + day + "???" + " " + fixhour + hour + ":" + fixmin + minute);
                                    textView.setText(year + "???" + (month + 1) + "???" + day + "???" + " " + fixhour + hour + ":" + fixmin + minute);
                                    year_end = year;
                                    month_end = month;
                                    day_end = day;
                                    hour_end = hour;
                                    min_end = minute;
                                }

                            }
                            String raw_timeEnd = timeend.getText().toString();
                            int indexRi = raw_timeEnd.indexOf("???");
                            int indexMao = raw_timeEnd.indexOf(":");
                            String partLeft = raw_timeEnd.substring(0, indexRi + 1);
                            String partRight = raw_timeEnd.substring(indexMao - 2);
                            timeend.setText(partLeft + " " + partRight);
                        }
                    };
                    TimePickerDialog timePickerDialog = new TimePickerDialog(AddScheduleActivity.this, timeSetListener, hour, min, true);
                    timePickerDialog.show();
                }
            };
            DatePickerDialog dialog = new DatePickerDialog(AddScheduleActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, listener, year, month, day);//???????????????????????????dialog??????????????????????????????0?????????0-11??????1-12??????
            dialog.show();


        }
    }

    private class SwitchEvent implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                timestart.setText(year_start + "???" + (month_start + 1) + "???" + day_start + "???");
                timeend.setText(year_end + "???" + (month_end + 1) + "???" + day_end + "???");
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
                timestart.setText(year_start + "???" + (month_start + 1) + "???" + day_start + "???" + " " + fixhour_start + hour_start + ":00");
                timeend.setText(year_end + "???" + (month_end + 1) + "???" + day_end + "???" + " " + fixhour_end + hour_end + ":00");
                isSwitched = false;
            }
        }
    }
}