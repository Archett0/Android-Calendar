package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.search;

import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.SCHEDULE_DESCRIPTION_END;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.SCHEDULE_DESCRIPTION_START;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.WEATHER_REPORTS;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.generateScheduleDescription;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.generateShareText;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.getScheduleWeatherReport;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Model.Schedule.schedulesForName;

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
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DB.DbContact;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleDetailsActivity;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.R;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Model.Schedule;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Model.ScheduleWithCheck;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.SMS.SmsSearchActivity;

public class SearchActivity extends AppCompatActivity {
    private SearchListAdapter searchListAdapter;

    private SearchView searchView;
    private ListView lv_show;

    private ImageView back; //????????????
    private ImageView cancle; //????????????????????????

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
                // i ????????????
                if (isCheckBoxOn) {
                    // ??????checkbox???????????????????????????????????????????????????
                    Log.w("checkbox????????????", "");
                } else {
                    // ??????checkbox????????????????????????????????????????????????

                    Schedule schedule = (Schedule) searchListAdapter.getItem(i);
                    // ?????????????????????
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
                    }

                    Log.w("checkbox??????????????????", "");
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
        searchView.setIconifiedByDefault(false); //??????
        searchView.requestFocus();  //????????????
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //???????????????????????????????????????
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            //???????????????????????????????????????
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
                            item.setTitle("????????????");
                            BottomNavigationItemView itemView0 = (BottomNavigationItemView) menuView.getChildAt(0);
                            itemView0.setEnabled(true);
                            BottomNavigationItemView itemView1 = (BottomNavigationItemView) menuView.getChildAt(1);
                            itemView1.setEnabled(true);

                        } else {
                            searchListAdapter.setAllCheckBoxNotSelected();
                            isCheckBoxSelected = false;
                            item.setIcon(R.drawable.ic_baseline_library_add_check_24);
                            item.setTitle("??????");
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

        Log.w("??????????????????", "??????");

    }

    /**
     * ????????????????????????????????????
     */
    private void shareSelectedSchedules() {
        String resultString = "";
        if (searchListAdapter.getListSelected().size() == 1) {
            String prefix = "??????\"";
            prefix += generateShareText(searchListAdapter.getListSelected().get(0));
            resultString = prefix;
        } else {
            for (int i = 0; i < searchListAdapter.getListSelected().size(); ++i) {
                String prefix = "??????#" + (i + 1) + "\"";
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
     * ?????????????????????????????????
     *
     * @param input ???????????????String
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void updateListView(String input) {

        // ??????????????????List
        List<ScheduleWithCheck> list = new ArrayList<>();

        // ?????????????????????DB???List
        List<Schedule> searchResults = schedulesForName(input);
        if (!searchResults.isEmpty()) {
            for (Schedule schedule : searchResults) {
                list.add(new ScheduleWithCheck(schedule));
            }
        } else {
            Schedule default_schedule = new Schedule(LocalDate.of(1999,1,1), LocalTime.now(), LocalTime.now(), "", "", "?????????????????????");
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
     *  ?????????????????????????????????
     */
    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        boolean deleteMultiple = false;
        if (searchListAdapter.getListSelected().size() == 1) {
            builder.setMessage("???????????????????????????");
            deleteMultiple = false;
        } else {
            builder.setMessage("???????????????????????????");
            deleteMultiple = true;
        }
        // ??????????????????
        boolean finalDeleteMultiple = deleteMultiple;
        builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct(finalDeleteMultiple);
            }
        });
        // ?????????
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // ???????????????????????????
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * ????????????
     *
     * @param deleteMultiple    ????????????????????????????????????true
     */
    private void deleteProduct(boolean deleteMultiple) {

        // single delete
        if (!deleteMultiple) {
            int _id = searchListAdapter.getListSelected().get(0).getId();
            Uri mCurrentScheduleUri = ContentUris.withAppendedId(DbContact.ScheduleEntry.CONTENT_URI, _id);
            if (mCurrentScheduleUri != null) {
                int rowsEffected = getContentResolver().delete(mCurrentScheduleUri, null, null);
                if (rowsEffected == 0) {
                    // ????????????????????????????????????toast
                    Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(SearchActivity.this, "????????????Uri,????????????", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(SearchActivity.this, "????????????Uri,????????????", Toast.LENGTH_SHORT).show();
                }
            }
            if (successFlag) {
                // ????????????????????????????????????toast
                Toast.makeText(this, "??????????????????", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "??????????????????", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}