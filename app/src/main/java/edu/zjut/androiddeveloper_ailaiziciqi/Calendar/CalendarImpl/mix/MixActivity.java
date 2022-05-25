package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.mix;

import android.Manifest;

import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.*;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.content.IntentFilter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.add.AddScheduleActivity;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.search.SearchActivity;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DB.DbContact;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DailyCalendarActivity;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DayViewExpActivity;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.SmsReceiver;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model.Schedule;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleDetailsActivity;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.OldmanActivity;
import edu.zjut.androiddeveloper_ailaiziciqi.calendarview.Calendar;
import edu.zjut.androiddeveloper_ailaiziciqi.calendarview.CalendarLayout;
import edu.zjut.androiddeveloper_ailaiziciqi.calendarview.CalendarView;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleListAdapter;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.R;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.base.activity.BaseActivity;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.group.GroupItemDecoration;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.group.GroupRecyclerView;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.gson.Gson;
import com.qweather.sdk.bean.base.Code;
import com.qweather.sdk.bean.weather.WeatherNowBean;
import com.qweather.sdk.view.HeConfig;
import com.qweather.sdk.view.QWeather;

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


    // 新增的属性
    private AlertDialog mMoreDialog;    // 功能按钮
    private AlertDialog mFuncDialog;    // 功能按钮
    private int dayClickCount;  // 点击次数计数器
    private static LocalDate dayClickRecord;    // 点击日期记录器
    private ScheduleListAdapter mScheduleListAdapter;   // 今日日程适配器
    private ImageView mOldManBtn;   // TODO:老人页面按钮，之后会被整合进设置
//    private Cursor mCursor; // 查询数据得到的游标
//    private Double longitude, latitude;  // 位置经纬度
//    private LocationManager locationManager;    // 位置管理器
//    private FusedLocationProviderClient fusedLocationProviderClient;    // 位置提供器

    /*
      增加新日程按钮
     */
    private FloatingActionButton addButton;

    /*
      搜索按钮
     */
    private ImageView search;

    public static void show(Context context) {
        context.startActivity(new Intent(context, MixActivity.class));
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

        // 左上日期的点击监听器
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

        // 右上日期的点击监听器
        findViewById(R.id.fl_current).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendarView.scrollToCurrent();
            }
        });

        // 设置CalendarLayout
        mCalendarLayout = findViewById(R.id.calendarLayout);
        mCalendarView.setOnCalendarSelectListener(this);
        mCalendarView.setOnYearChangeListener(this);

        // 左边缘周标记的点击监听器
        mCalendarView.setOnClickCalendarPaddingListener(new CalendarView.OnClickCalendarPaddingListener() {
            @Override
            public void onClickCalendarPadding(float x, float y, boolean isMonthView,
                                               Calendar adjacentCalendar, Object obj) {
                Log.e("onClickCalendarPadding", "  --  " + x + "  " + y + "  " + obj + "  " + adjacentCalendar);
                Toast.makeText(MixActivity.this,
                        adjacentCalendar.getYear() + "年，第" + obj + "周",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // 设置左上角的日期显示
        mTextYear.setText(String.valueOf(mCalendarView.getCurYear()));
        mYear = mCalendarView.getCurYear();
        mTextMonthDay.setText(mCalendarView.getCurMonth() + "月" + mCalendarView.getCurDay() + "日");
        mTextLunar.setText("今日");
        mTextCurrentDay.setText(String.valueOf(mCalendarView.getCurDay()));

        // 监听功能按钮右
        findViewById(R.id.iv_more).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mMoreDialog == null) {
                    mMoreDialog = new AlertDialog.Builder(MixActivity.this)
                            .setTitle(R.string.list_dialog_title)
                            .setItems(R.array.list_dialog_items, MixActivity.this)
                            .create();
                }
                mMoreDialog.show();
            }
        });

        // 监听功能按钮右
        final DialogInterface.OnClickListener listener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                mCalendarLayout.expand();
                                break;
                            case 1:
                                boolean result = mCalendarLayout.shrink();
                                Log.e("shrink", " --  " + result);
                                break;
                            case 2:
                                mCalendarView.scrollToPre(false);
                                break;
                            case 3:
                                mCalendarView.scrollToNext(false);
                                break;
                            case 4:
                                mCalendarView.scrollToCurrent(true);
//                                mCalendarView.scrollToCalendar(2018, 12, 30);
                                break;
                            case 5:
                                mCalendarView.setRange(2018, 7, 1, 2019, 4, 28);
//                                mCalendarView.setRange(mCalendarView.getCurYear(), mCalendarView.getCurMonth(), 6,
//                                        mCalendarView.getCurYear(), mCalendarView.getCurMonth(), 23);
                                break;
                            case 6:
                                Log.e("scheme", "  " + mCalendarView.getSelectedCalendar().getScheme() + "  --  "
                                        + mCalendarView.getSelectedCalendar().isCurrentDay());
                                List<Calendar> weekCalendars = mCalendarView.getCurrentWeekCalendars();
                                for (Calendar calendar : weekCalendars) {
                                    Log.e("onWeekChange", calendar.toString() + "  --  " + calendar.getScheme());
                                }
                                new AlertDialog.Builder(MixActivity.this)
                                        .setMessage(String.format("Calendar Range: \n%s —— %s",
                                                mCalendarView.getMinRangeCalendar(),
                                                mCalendarView.getMaxRangeCalendar()))
                                        .show();
                                break;
                        }
                    }
                };

        // 监听功能按钮左
        findViewById(R.id.iv_func).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mFuncDialog == null) {
                    mFuncDialog = new AlertDialog.Builder(MixActivity.this)
                            .setTitle(R.string.func_dialog_title)
                            .setItems(R.array.func_dialog_items, listener)
                            .create();
                }
                mFuncDialog.show();
            }
        });

        // 监听添加日程按钮
        addButton = findViewById(R.id.iv_add);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addIntent = new Intent(MixActivity.this, AddScheduleActivity.class);
                startActivity(addIntent);
            }
        });

        // 监听搜索按钮
        search = findViewById(R.id.iv_search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(MixActivity.this, SearchActivity.class);
                startActivity(searchIntent);
            }
        });

        getSms();

        // 今日日程列表的监听
        mScheduleListAdapter = new ScheduleListAdapter(this, LocalDate.now(), null);
        mScheduleListAdapter.setOnItemClickListener(new ScheduleListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, Schedule schedule) {
                // 点击已有的日程
                if (schedule.getScheduleDate() != null) {
                    Log.i("Event List Click", "In Activity:" + position);
                    Log.i("Event List Click", "In Activity:" + schedule.toString());
                    Intent intent = new Intent(MixActivity.this, ScheduleDetailsActivity.class);
                    Uri scheduleUri = ContentUris.withAppendedId(DbContact.ScheduleEntry.CONTENT_URI, schedule.getId());
                    intent.setData(scheduleUri);
                    intent.putExtra("Name", schedule.getSchedule());
                    intent.putExtra("StartDescription", generateScheduleDescription(schedule, SCHEDULE_DESCRIPTION_START));
                    intent.putExtra("EndDescription", generateScheduleDescription(schedule, SCHEDULE_DESCRIPTION_END));
                    intent.putExtra("Date", String.valueOf(schedule.getScheduleDate()));
                    intent.putExtra("EndDate", String.valueOf(schedule.getScheduleEndDate()));
                    intent.putExtra("Time", String.valueOf(schedule.getScheduleStartTime()));
                    intent.putExtra("EndTime", String.valueOf(schedule.getScheduleEndTime()));
                    // 获取相应的日期,并填充
                    if (WEATHER_REPORTS != null && !WEATHER_REPORTS.isEmpty()) {
                        int weatherIndex = getScheduleWeatherReport(schedule.getScheduleDate());
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
                } else {
                    // 点击了"暂无日程信息",跳转至新增日程
                    Log.i("Event List Click", "In Activity:" + "No schedule today");
                    Intent intent = new Intent(MixActivity.this, AddScheduleActivity.class);
                    startActivity(intent);
                }
            }
        });

        // 暂时的老年版按钮的监听
        mOldManBtn = findViewById(R.id.iv_old_man_btn);
        mOldManBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(MixActivity.this, OldmanActivity.class);
                Intent intent = new Intent(MixActivity.this, DayViewExpActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getSms() {
        ActivityCompat.requestPermissions(MixActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 1);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        SmsReceiver smsReceiver = new SmsReceiver();
        //设置较高的优先级
        intentFilter.setPriority(2147483647);
        registerReceiver(smsReceiver, intentFilter);
    }


    @Override
    protected void initData() {
        Log.i("Random Debug", "initData");
        int year = mCalendarView.getCurYear();
        int month = mCalendarView.getCurMonth();

        Map<String, Calendar> map = new HashMap<>();
        map.put(getSchemeCalendar(year, month, 3, 0xFF40db25, "假").toString(),
                getSchemeCalendar(year, month, 3, 0xFF40db25, "假"));
        map.put(getSchemeCalendar(year, month, 6, 0xFFe69138, "事").toString(),
                getSchemeCalendar(year, month, 6, 0xFFe69138, "事"));
        map.put(getSchemeCalendar(year, month, 9, 0xFFdf1356, "议").toString(),
                getSchemeCalendar(year, month, 9, 0xFFdf1356, "议"));
        map.put(getSchemeCalendar(year, month, 13, 0xFFedc56d, "记").toString(),
                getSchemeCalendar(year, month, 13, 0xFFedc56d, "记"));
        map.put(getSchemeCalendar(year, month, 14, 0xFFedc56d, "记").toString(),
                getSchemeCalendar(year, month, 14, 0xFFedc56d, "记"));
        map.put(getSchemeCalendar(year, month, 15, 0xFFaacc44, "假").toString(),
                getSchemeCalendar(year, month, 15, 0xFFaacc44, "假"));
        map.put(getSchemeCalendar(year, month, 18, 0xFFbc13f0, "记").toString(),
                getSchemeCalendar(year, month, 18, 0xFFbc13f0, "记"));
        map.put(getSchemeCalendar(year, month, 25, 0xFF13acf0, "假").toString(),
                getSchemeCalendar(year, month, 25, 0xFF13acf0, "假"));
        map.put(getSchemeCalendar(year, month, 27, 0xFF13acf0, "多").toString(),
                getSchemeCalendar(year, month, 27, 0xFF13acf0, "多"));
        //此方法在巨大的数据量上不影响遍历性能，推荐使用
        mCalendarView.setSchemeDate(map);


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
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        calendar.addScheme(new Calendar.Scheme());
        calendar.addScheme(0xFF008800, "假");
        calendar.addScheme(0xFF008800, "节");
        return calendar;
    }

    @Override
    public void onCalendarOutOfRange(Calendar calendar) {

    }

    // 日期的点击事件:单击修改左上角的日期,双击打开日视图
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
        mTextMonthDay.setText(calendar.getMonth() + "月" + calendar.getDay() + "日");
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
    }

//    /**
//     * 获取当前位置
//     */
//    private void getLocation() {
//
//        // 判断当前是否拥有使用GPS的权限
//        if (ActivityCompat.checkSelfPermission(MixActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(MixActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // 申请权限
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
        if (dayClickRecord != null) {
            mScheduleListAdapter.resetCurrentAdapter(this, dayClickRecord, null);
        }
        else {
            mScheduleListAdapter.resetCurrentAdapter(this, LocalDate.now(), null);
        }
    }
}
