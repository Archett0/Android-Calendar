package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.search;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
                if(isCheckBoxOn) {
                    // 如果checkbox模式启动，则该点击不能跳转到详情页
                    Log.w("checkbox模式启动", "");
                } else {
                    // 如果checkbox模式没有启动，则可以跳转到详情页
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

                updateListView();
                return true;
            }
        });

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setVisibility(View.GONE);
        menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);
        for (int i = 0; i < menuView.getChildCount(); i++) {
            BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(i);
            itemView.setShifting(false);
            if(i == 0 || i == 1) {
                itemView.setEnabled(false);
            }
        }
        itemView = (BottomNavigationItemView) menuView.getChildAt(1);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.share:
                        break;
                    case R.id.delete:
                        break;
                    case R.id.select_all_items:
                        if (!isCheckBoxSelected) {
                            Log.w("isCheckBoxSelected","");
                            searchListAdapter.setAllCheckBoxSelected();
                            isCheckBoxSelected = true;
                            item.setIcon(R.drawable.ic_baseline_library_add_check_24_selected);
                            item.setTitle("取消全选");
                            BottomNavigationItemView itemView0 = (BottomNavigationItemView) menuView.getChildAt(0);
                            itemView0.setEnabled(false);
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
        Log.w("主要构造函数","运行");

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateListView() {
//        userList = dbHelper.query();
        List<ScheduleWithCheck> list = new ArrayList<>();
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now();
        Schedule schedule1 = new Schedule(localDate, localTime, localTime, "0", "五月十二", "这是一些日程");
        list.add(new ScheduleWithCheck(schedule1));

        Schedule schedule2 = new Schedule(localDate, localTime, localTime, "1", "五月十二", "这是一些日程");
        list.add(new ScheduleWithCheck(schedule2));

        Schedule schedule3 = new Schedule(localDate, localTime, localTime, "2", "五月十二", "这是一些日程");
        list.add(new ScheduleWithCheck(schedule3));

        Schedule schedule4 = new Schedule(localDate, localTime, localTime, "3", "五月十二", "这是一些日程");
        list.add(new ScheduleWithCheck(schedule4));

        Schedule schedule5 = new Schedule(localDate, localTime, localTime, "4", "五月十二", "这是一些日程");
        list.add(new ScheduleWithCheck(schedule5));

        Schedule schedule6 = new Schedule(localDate, localTime, localTime, "5", "五月十二", "这是一些日程");
        list.add(new ScheduleWithCheck(schedule6));

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

}