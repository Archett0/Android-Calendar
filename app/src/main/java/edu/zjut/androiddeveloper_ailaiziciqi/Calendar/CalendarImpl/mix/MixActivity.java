package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.mix;

import android.Manifest;

import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DB.PreferencesHelper.OPTION_ACTIVATED;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DB.PreferencesHelper.OPTION_DEACTIVATED;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DB.PreferencesHelper.SHARED_PREFERENCE_NAME;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.*;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.add.AddScheduleActivity;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.search.SearchActivity;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DB.DbContact;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DB.PreferencesHelper;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.SMS.SmsReceiver;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Model.Schedule;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleDetailsActivity;
import edu.zjut.androiddeveloper_ailaiziciqi.calendarview.Calendar;
import edu.zjut.androiddeveloper_ailaiziciqi.calendarview.CalendarLayout;
import edu.zjut.androiddeveloper_ailaiziciqi.calendarview.CalendarView;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleListAdapter;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.R;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.base.activity.BaseActivity;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.group.GroupItemDecoration;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.group.GroupRecyclerView;
import edu.zjut.androiddeveloper_ailaiziciqi.calendarview.LunarCalendar;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class MixActivity extends BaseActivity implements
        CalendarView.OnCalendarSelectListener,
        CalendarView.OnYearChangeListener,
        DialogInterface.OnClickListener,
        View.OnClickListener {

    TextView mTextMonthDay;

    TextView mTextYear;

    TextView mTextLunar;

    TextView mTextCurrentDay;

    CalendarView mCalendarView;

    RelativeLayout mRelativeTool;
    private int mYear;
    CalendarLayout mCalendarLayout;
    public GroupRecyclerView mRecyclerView;

    private SmsReceiver smsReceiver;


    // ???????????????
    private int dayClickCount;  // ?????????????????????
    private static LocalDate dayClickRecord;    // ?????????????????????
    private ScheduleListAdapter mScheduleListAdapter;   // ?????????????????????
    private ImageView mSettingBtn;  // ????????????
//    private Cursor mCursor; // ???????????????????????????
//    private Double longitude, latitude;  // ???????????????
//    private LocationManager locationManager;    // ???????????????
//    private FusedLocationProviderClient fusedLocationProviderClient;    // ???????????????

    /*
      ?????????????????????
     */
    private FloatingActionButton addButton;

    /*
      ????????????
     */
    private ImageView search;

    public static void show(Context context) {
        context.startActivity(new Intent(context, MixActivity.class));
    }

    /*
      ??????????????????
     */
    public static LocalDate getDayClickRecord() {
        return dayClickRecord;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mix;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void initView() {
        setStatusBarDarkMode();
        dayClickCount = 0;
        mTextMonthDay = findViewById(R.id.tv_month_day);
        mTextYear = findViewById(R.id.tv_year);
        mTextLunar = findViewById(R.id.tv_lunar);
        mRelativeTool = findViewById(R.id.rl_tool);
        mCalendarView = findViewById(R.id.calendarView);
        mTextCurrentDay = findViewById(R.id.tv_current_day);

        Log.i("Random Debug", "initView");

        // ??????????????????????????????
        mTextMonthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mCalendarLayout.isExpand()) {
                    mCalendarLayout.expand();
                    return;
                }
                mCalendarView.showYearSelectLayout(mYear);
                mTextLunar.setVisibility(View.GONE);
                mTextYear.setVisibility(View.GONE);
                mTextMonthDay.setText(String.valueOf(mYear));
            }
        });

        // ??????????????????????????????
        findViewById(R.id.fl_current).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.scrollToCurrent();
            }
        });

        // ??????CalendarLayout
        mCalendarLayout = findViewById(R.id.calendarLayout);
        mCalendarView.setOnCalendarSelectListener(this);
        mCalendarView.setOnYearChangeListener(this);
        // ??????layout??????
        setUserLayout();

        // ????????????????????????????????????
        mCalendarView.setOnClickCalendarPaddingListener(new CalendarView.OnClickCalendarPaddingListener() {
            @Override
            public void onClickCalendarPadding(float x, float y, boolean isMonthView,
                                               Calendar adjacentCalendar, Object obj) {
                Log.e("onClickCalendarPadding", "  --  " + x + "  " + y + "  " + obj + "  " + adjacentCalendar);
                Toast.makeText(MixActivity.this,
                        adjacentCalendar.getYear() + "?????????" + obj + "???",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // ??????????????????????????????
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mYear = mCalendarView.getCurYear();
        mTextMonthDay.setText(mCalendarView.getCurMonth() + "???" + mCalendarView.getCurDay() + "???");
        mTextLunar.setText("??????");
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));

        // ??????????????????
        mSettingBtn = findViewById(R.id.iv_settings_btn);
        mSettingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MixActivity.this, SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });

        // ????????????????????????
        addButton = findViewById(R.id.iv_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(MixActivity.this, AddScheduleActivity.class);
                startActivity(addIntent);
            }
        });

        // ??????????????????
        search = findViewById(R.id.iv_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(MixActivity.this, SearchActivity.class);
                startActivity(searchIntent);
            }
        });

        getSms();

        // ???????????????????????????
        mScheduleListAdapter = new ScheduleListAdapter(this, LocalDate.now(), null);
        mScheduleListAdapter.setOnItemClickListener(new ScheduleListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, Schedule schedule) {
                // ?????????????????????
                if (schedule.getScheduleDate() != null) {
                    Log.i("Event List Click", "In Activity:" + position);
                    Log.i("Event List Click", "In Activity:" + schedule.toString());
                    Intent intent = new Intent(MixActivity.this, ScheduleDetailsActivity.class);
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
                } else {
                    // ?????????"??????????????????",?????????????????????
                    Log.i("Event List Click", "In Activity:" + "No schedule today");
                    Intent intent = new Intent(MixActivity.this, AddScheduleActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void getSms() {
        ActivityCompat.requestPermissions(MixActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 1);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        smsReceiver = new SmsReceiver();
        //????????????????????????
        intentFilter.setPriority(2147483647);
        registerReceiver(smsReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsReceiver);
    }


    @Override
    protected void initData() {
        Log.i("Random Debug", "initData");

        mRecyclerView = findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new GroupItemDecoration<String, Schedule>());
        mRecyclerView.setAdapter(mScheduleListAdapter);
        mRecyclerView.notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//???????????????????????????????????????????????????
        calendar.setScheme(text);
        calendar.addScheme(new Calendar.Scheme());
        calendar.addScheme(0xFF008800, "???");
        calendar.addScheme(0xFF008800, "???");
        return calendar;
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    // ?????????????????????:??????????????????????????????,?????????????????????
    @SuppressLint("SetTextI18n")
    @Override
    public void onCalendarSelect(Calendar calendar, boolean isClick) {
        Log.i("Random Debug", "Recycler View should be notified");
        Log.i("Random Debug", "Current items in static List:" + Schedule.scheduleArrayList.size());
        dayClickCount += 1;
        LocalDate clickedDay = LocalDate.of(calendar.getYear(), calendar.getMonth(), calendar.getDay());
        if (dayClickCount >= 2 && dayClickRecord.equals(clickedDay)) {
            dayClickCount = 0;
            startActivity(new Intent(MixActivity.this, DailyCalendarActivity.class));
        } else {
            dayClickRecord = LocalDate.of(calendar.getYear(), calendar.getMonth(), calendar.getDay());
        }
        mTextLunar.setVisibility(View.VISIBLE);
        mTextYear.setVisibility(View.VISIBLE);
        mTextMonthDay.setText(calendar.getMonth() + "???" + calendar.getDay() + "???");
        mTextYear.setText(String.valueOf(calendar.getYear()));
        mTextLunar.setText(calendar.getLunar());
        mYear = calendar.getYear();
        // reset adapter
        mScheduleListAdapter.resetCurrentAdapter(this, dayClickRecord, null);
        mRecyclerView.notifyDataSetChanged();

        Log.e("onDateSelected", "  -- " + calendar.getYear() +
                "  --  " + calendar.getMonth() +
                "  -- " + calendar.getDay() +
                "  --  " + isClick + "  --   " + calendar.getScheme());
    }

    @Override
    public void onYearChange(int year) {
        mTextMonthDay.setText(String.valueOf(year));
    }

    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case 0:
                mCalendarView.setWeekStarWithSun();
                break;
            case 1:
                mCalendarView.setWeekStarWithMon();
                break;
            case 2:
                mCalendarView.setWeekStarWithSat();
                break;
            case 3:
                if (mCalendarView.isSingleSelectMode()) {
                    mCalendarView.setSelectDefaultMode();
                } else {
                    mCalendarView.setSelectSingleMode();
                }
                break;
            case 5:
                mCalendarView.setAllMode();
                break;
            case 6:
                mCalendarView.setOnlyCurrentMode();
                break;
            case 7:
                mCalendarView.setFixMode();
                break;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MixActivity.this);
        //getLocation();
        authorizeWeatherAccount();
        getWeather(MixActivity.this);

        Log.i("Shared Preferences", "onCreate??????");
        Log.i("Shared Preferences", "=========================================================================");
        // ??????Shared Preferences???????????????
        PreferencesHelper preferencesHelper = new PreferencesHelper(MixActivity.this, SHARED_PREFERENCE_NAME);
        // ??????????????????
        preferencesHelper.safePutString("default_view", "month");
        // ??????????????????
        preferencesHelper.safePutString("only_week_view_mode", OPTION_DEACTIVATED);
        // ???????????????
        preferencesHelper.safePutString("week_begin", "sunday");
        // ????????????
        preferencesHelper.safePutString("old_man_mode", OPTION_DEACTIVATED);
        // ???????????????
        preferencesHelper.safePutString("old_man_name", "DEFAULT");
        // ????????????
        preferencesHelper.safePutString("voice_over", OPTION_DEACTIVATED);
        Log.i("Shared Preferences", "=========================================================================");

        // ???????????????????????????
        String old_man_mode = preferencesHelper.getString("old_man_mode");
        if (old_man_mode.equals(OPTION_ACTIVATED)) {
            Intent intent = new Intent(MixActivity.this, OldmanActivity.class);
            startActivity(intent);
            finish();
            Log.i("Shared Preferences", "??????????????????");
            Log.i("Shared Preferences", "=========================================================================");
        }
    }

//    /**
//     * ??????????????????
//     */
//    private void getLocation() {
//
//        // ??????????????????????????????GPS?????????
//        if (ActivityCompat.checkSelfPermission(MixActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(MixActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // ????????????
//            ActivityCompat.requestPermissions(MixActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
//            ActivityCompat.requestPermissions(MixActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
//        }
//        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
//            @Override
//            public void onComplete(@NonNull Task<Location> task) {
//                Location location = task.getResult();
//                if (location != null) {
//                    Geocoder geocoder = new Geocoder(MixActivity.this, Locale.getDefault());
//                    try {
//                        List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//                        longitude = addressList.get(0).getLongitude();
//                        latitude = addressList.get(0).getLatitude();
//                        Log.i("Random Debug", "longitude:" + longitude);
//                        Log.i("Random Debug", "latitude:" + latitude);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    Log.i("Random Debug", "Can not get location");
//                }
//            }
//        });
//    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("Random Debug", "onResume");
        if (dayClickRecord != null) {
            mScheduleListAdapter.resetCurrentAdapter(this, dayClickRecord, null);
        } else {
            mScheduleListAdapter.resetCurrentAdapter(this, LocalDate.now(), null);
        }
        mCalendarLayout = findViewById(R.id.calendarLayout);
        int year = mCalendarView.getCurYear();

        Map<String, Calendar> map = new HashMap<>();
        map.put(getSchemeCalendar(year, 5, 1, 0xFFdf1356, "???").toString(),
                getSchemeCalendar(year, 5, 1, 0xFFdf1356, "???"));
        map.put(getSchemeCalendar(year, 5, 2, 0xFFdf1356, "???").toString(),
                getSchemeCalendar(year, 5, 2, 0xFFdf1356, "???"));
        map.put(getSchemeCalendar(year, 5, 3, 0xFFdf1356, "???").toString(),
                getSchemeCalendar(year, 5, 3, 0xFFdf1356, "???"));
        map.put(getSchemeCalendar(year, 6, 3, 0xFFdf1356, "???").toString(),
                getSchemeCalendar(year, 6, 3, 0xFFdf1356, "???"));
        map.put(getSchemeCalendar(year, 6, 4, 0xFFdf1356, "???").toString(),
                getSchemeCalendar(year, 6, 4, 0xFFdf1356, "???"));
        map.put(getSchemeCalendar(year, 6, 5, 0xFFdf1356, "???").toString(),
                getSchemeCalendar(year, 6, 5, 0xFFdf1356, "???"));
        for(Schedule schedule: Schedule.scheduleArrayList){
            map.put(getSchemeCalendar(schedule.getScheduleDate().getYear(), schedule.getScheduleDate().getMonthValue(), schedule.getScheduleDate().getDayOfMonth(), 0xFFe69138, "???").toString(),
                    getSchemeCalendar(schedule.getScheduleDate().getYear(), schedule.getScheduleDate().getMonthValue(), schedule.getScheduleDate().getDayOfMonth(), 0xFFe69138, "???"));

        }
        for(String fes: LunarCalendar.getSolarCalendar()){
            String rawMonth = fes.substring(0,2);
            String rawDay = fes.substring(2,4);
            int fesMonth = Integer.parseInt(rawMonth);
            int fesDay = Integer.parseInt(rawDay);
            map.put(getSchemeCalendar(year, fesMonth, fesDay, 0xFF13acf0, "???").toString(),
                    getSchemeCalendar(year, fesMonth, fesDay, 0xFF13acf0, "???"));

        }
        //?????????????????????????????????????????????????????????????????????
        mCalendarView.setSchemeDate(map);


        // ??????Preferences?????????
        PreferencesHelper preferencesHelper = new PreferencesHelper(MixActivity.this, SHARED_PREFERENCE_NAME);
        Log.i("Shared Preferences", "???onResume?????????setUserLayout??????");
        Log.i("Shared Preferences", "=========================================================================");
        String default_view = preferencesHelper.getString("default_view");
        // ????????????: ????????????
        if (default_view.equals("week")) {
            mCalendarLayout.shrink(1);
            Log.i("Shared Preferences", "???????????????????????????");
        } else {
            mCalendarLayout.expand(1);
            Log.i("Shared Preferences", "???????????????????????????");
        }
        Log.i("Shared Preferences", "=========================================================================");
    }

    private void setUserLayout() {
        // ??????Preferences?????????
        PreferencesHelper preferencesHelper = new PreferencesHelper(MixActivity.this, SHARED_PREFERENCE_NAME);
        Log.i("Shared Preferences", "setUserLayout??????");
        Log.i("Shared Preferences", "=========================================================================");
        String only_week_view_mode = preferencesHelper.getString("only_week_view_mode");
        String week_begin = preferencesHelper.getString("week_begin");
//        String old_man_name = preferencesHelper.getString("old_man_name");
//        String voice_over = preferencesHelper.getString("voice_over");

        // ????????????: ????????????
        if (only_week_view_mode.equals(OPTION_ACTIVATED)) {
            mCalendarLayout.setModeOnlyWeekView();
            Log.i("Shared Preferences", "????????????????????????");
        } else {
            mCalendarLayout.setModeBothMonthWeekView();
            Log.i("Shared Preferences", "?????????????????????");
        }

        // ????????????: ???????????????
        if (week_begin.equals("monday")) {
            mCalendarView.setWeekStarWithMon();
            Log.i("Shared Preferences", "??????????????????????????????");
        } else if (week_begin.equals("sunday")) {
            mCalendarView.setWeekStarWithSun();
            Log.i("Shared Preferences", "??????????????????????????????");
        } else if (week_begin.equals("saturday")) {
            mCalendarView.setWeekStarWithSat();
            Log.i("Shared Preferences", "??????????????????????????????");
        } else {
            mCalendarView.setWeekStarWithMon();
            Log.i("Shared Preferences", "????????????????????????????????????");
        }

        Log.i("Shared Preferences", "=========================================================================");
    }
}
