package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.search;

import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.SCHEDULE_DESCRIPTION_END;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.SCHEDULE_DESCRIPTION_START;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.WEATHER_REPORTS;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.generateScheduleDescription;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.generateShareText;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.getScheduleWeatherReport;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model.Schedule.schedulesForName;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarItemView;
import com.google.android.material.navigation.NavigationBarView;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.mix.MixActivity;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DB.DbContact;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleDetailsActivity;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.R;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model.Schedule;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model.ScheduleWithCheck;

public class SearchActivity extends AppCompatActivity {
    private SearchListAdapter searchListAdapter;

    private SearchView searchView;
    private ListView lv_show;

    private ImageView back; //返回按钮
    private ImageView cancle; //关闭选择状态按钮

    private View searchBar;
    private View selectBar;

    private BottomNavigationView bottomNavigationView;

    private boolean isCheckBoxOn = false;
    private Boolean isCheckBoxSelected = false;

    private BottomNavigationItemView itemView;

    public BottomNavigationItemView getItemView() {
        return itemView;
    }

    public void setItemView(BottomNavigationItemView itemView) {
        this.itemView = itemView;
    }

    public Boolean getCheckBoxSelected() {
        return isCheckBoxSelected;
    }

    public void setCheckBoxSelected(Boolean checkBoxSelected) {
        isCheckBoxSelected = checkBoxSelected;
    }

    private BottomNavigationMenuView menuView;

    public BottomNavigationMenuView getMenuView() {
        return menuView;
    }

    public void setMenuView(BottomNavigationMenuView menuView) {
        this.menuView = menuView;
    }

    private ImageView smsSearch;

    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        lv_show = findViewById(R.id.lv_show);
        lv_show.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
                searchListAdapter.setAllCheckboxVisible();
                isCheckBoxOn = true;
                selectBar.setVisibility(View.VISIBLE);
                searchBar.setVisibility(View.GONE);
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });
        lv_show.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // i 代表位置
                if (isCheckBoxOn) {
                    // 如果checkbox模式启动，则该点击不能跳转到详情页
                    Log.w("checkbox模式启动", "");
                } else {
                    // 如果checkbox模式没有启动，则可以跳转到详情页

                    Schedule schedule = (Schedule) searchListAdapter.getItem(i);
                    // 点击已有的日程
                    if (schedule.getScheduleDate() != null) {
                        Log.i("Event List Click", "In Activity:" + i);
                        Log.i("Event List Click", "In Activity:" + schedule.toString());
                        Intent intent = new Intent(SearchActivity.this, ScheduleDetailsActivity.class);
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
                        // 获取相应的日期,并填充
                        if (WEATHER_REPORTS != null && !WEATHER_REPORTS.isEmpty()) {
                            int weatherIndex = getScheduleWeatherReport(schedule);
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
                    }

                    Log.w("checkbox模式没有启动", "");
                    searchListAdapter.getListSelected();
                }
            }
        });

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (isCheckBoxOn) {
//                    searchListAdapter.setAllCheckboxInvisible();
//                    isCheckBoxOn = false;
//                } else
                finish();
            }
        });
        cancle = findViewById(R.id.cancle);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchListAdapter.setAllCheckboxInvisible();
                isCheckBoxOn = false;
                selectBar.setVisibility(View.GONE);
                searchBar.setVisibility(View.VISIBLE);
                bottomNavigationView.setVisibility(View.GONE);
            }
        });

        searchBar = findViewById(R.id.search_bar);

        selectBar = findViewById(R.id.select_bar);
        selectBar.setVisibility(View.GONE);

        searchView = findViewById(R.id.searchview);
        searchView.setIconifiedByDefault(false); //展开
        searchView.requestFocus();  //获取焦点
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            //当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String s) {

                updateListView(s);
                return true;
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);
        menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(i);
            itemView.setShifting(false);
        }

        itemView = (BottomNavigationItemView) menuView.getChildAt(1);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.share:
                        shareSelectedSchedules();
                        break;
                    case R.id.delete:
                        showDeleteConfirmationDialog();
                        break;
                    case R.id.select_all_items:
                        if (!isCheckBoxSelected) {
                            Log.w("isCheckBoxSelected", "");
                            searchListAdapter.setAllCheckBoxSelected();
                            isCheckBoxSelected = true;
                            item.setIcon(R.drawable.ic_baseline_library_add_check_24_selected);
                            item.setTitle("取消全选");
                            BottomNavigationItemView itemView0 = (BottomNavigationItemView) menuView.getChildAt(0);
                            itemView0.setEnabled(true);
                            BottomNavigationItemView itemView1 = (BottomNavigationItemView) menuView.getChildAt(1);
                            itemView1.setEnabled(true);

                        } else {
                            searchListAdapter.setAllCheckBoxNotSelected();
                            isCheckBoxSelected = false;
                            item.setIcon(R.drawable.ic_baseline_library_add_check_24);
                            item.setTitle("全选");
                            BottomNavigationItemView itemView0 = (BottomNavigationItemView) menuView.getChildAt(0);
                            itemView0.setEnabled(false);
                            BottomNavigationItemView itemView1 = (BottomNavigationItemView) menuView.getChildAt(1);
                            itemView1.setEnabled(false);
                        }

                        break;
                }
                return false;
            }
        });

        smsSearch = findViewById(R.id.sms_search);
        smsSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SearchActivity.this, SmsSearchActivity.class);
                startActivity(intent);
            }
        });

        Log.w("主要构造函数", "运行");

    }

    /**
     * 分享选中的一个或多个日程
     */
    private void shareSelectedSchedules() {
        String resultString = "";
        if (searchListAdapter.getListSelected().size() == 1) {
            String prefix = "日程\"";
            prefix += generateShareText(searchListAdapter.getListSelected().get(0));
            resultString = prefix;
        } else {
            for (int i = 0; i < searchListAdapter.getListSelected().size(); ++i) {
                String prefix = "日程#" + (i + 1) + "\"";
                prefix += generateShareText(searchListAdapter.getListSelected().get(i));
                resultString += prefix + "  ";
            }
        }
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, resultString);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    /**
     * 更新搜索日程的结果列表
     *
     * @param input 用户的输入String
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateListView(String input) {

        // 返回的新结果List
        List<ScheduleWithCheck> list = new ArrayList<>();

        // 搜索得到的对应DB的List
        List<Schedule> searchResults = schedulesForName(input);
        if (!searchResults.isEmpty()) {
            for (Schedule schedule : searchResults) {
                list.add(new ScheduleWithCheck(schedule));
            }
        } else {
            Schedule default_schedule = new Schedule(LocalDate.of(1999,1,1), LocalTime.now(), LocalTime.now(), "", "", "没有符合的日程");
            list.add(new ScheduleWithCheck(default_schedule));
        }

        searchListAdapter = new SearchListAdapter(list, SearchActivity.this);
        lv_show.setAdapter(searchListAdapter);
    }

    @Override
    public void onBackPressed() {
        if (isCheckBoxOn) {
            searchListAdapter.setAllCheckboxInvisible();
            isCheckBoxOn = false;
            selectBar.setVisibility(View.GONE);
            searchBar.setVisibility(View.VISIBLE);
            bottomNavigationView.setVisibility(View.GONE);
        } else
            finish();
    }

    /**
     *  编辑界面的确认删除功能
     */
    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        boolean deleteMultiple = false;
        if (searchListAdapter.getListSelected().size() == 1) {
            builder.setMessage("确定删除这个日程？");
            deleteMultiple = false;
        } else {
            builder.setMessage("确定删除这些日程？");
            deleteMultiple = true;
        }
        // 用户确认删除
        boolean finalDeleteMultiple = deleteMultiple;
        builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct(finalDeleteMultiple);
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

    /**
     * 执行删除
     *
     * @param deleteMultiple    是否删除多个，若是则传入true
     */
    private void deleteProduct(boolean deleteMultiple) {

        // single delete
        if (!deleteMultiple) {
            int _id = searchListAdapter.getListSelected().get(0).getId();
            Uri mCurrentScheduleUri = ContentUris.withAppendedId(DbContact.ScheduleEntry.CONTENT_URI, _id);
            if (mCurrentScheduleUri != null) {
                int rowsEffected = getContentResolver().delete(mCurrentScheduleUri, null, null);
                if (rowsEffected == 0) {
                    // 如果没有一行被删除，报错toast
                    Toast.makeText(this, "删除错误", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "删除成功", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(SearchActivity.this, "无法获取Uri,错误发生", Toast.LENGTH_SHORT).show();
            }
        }
        // multiple deleted so we do row by row
        else {
            boolean successFlag = true;
            for (int i = 0; i < searchListAdapter.getListSelected().size(); ++i) {
                int _id = searchListAdapter.getListSelected().get(i).getId();
                Uri mCurrentScheduleUri = ContentUris.withAppendedId(DbContact.ScheduleEntry.CONTENT_URI, _id);
                if (mCurrentScheduleUri != null) {
                    int rowsEffected = getContentResolver().delete(mCurrentScheduleUri, null, null);
                    if (rowsEffected == 0) {
                        successFlag = false;
                    }
                } else {
                    Toast.makeText(SearchActivity.this, "无法获取Uri,错误发生", Toast.LENGTH_SHORT).show();
                }
            }
            if (successFlag) {
                // 如果没有一行被删除，报错toast
                Toast.makeText(this, "全部删除成功", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "存在删除错误", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}