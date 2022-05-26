package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DesktopWidget;

import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model.Schedule.schedulesForName;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.R;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model.Schedule;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model.ScheduleWithCheck;

public class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private final static String TAG="Widget";
    private Context mContext;
    private int mAppWidgetId;

    private static List<Schedule> scheduleList;

    /**
     * 构造GridRemoteViewsFactory
     */
    public ListRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public RemoteViews getViewAt(int position) {
        //  HashMap<String, Object> map;
        Log.w("getViewAt",position+"");
        // 获取 item_widget_device.xml 对应的RemoteViews
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.layout_item_widget);

        // 设置 第position位的“视图”的数据
        Schedule schedule = scheduleList.get(position);
        //  rv.setImageViewResource(R.id.iv_lock, ((Integer) map.get(IMAGE_ITEM)).intValue());

        DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm");
        rv.setTextViewText(R.id.time_start_widget, schedule.getScheduleStartTime().format(time));

        rv.setTextViewText(R.id.time_end_widget, schedule.getScheduleEndTime().format(time));

        rv.setTextViewText(R.id.schedule_widget, schedule.getSchedule());

        // 设置 第position位的“视图”对应的响应事件
//        Intent fillInIntent = new Intent();
//        fillInIntent.putExtra("Type", 0);
//        fillInIntent.putExtra(DayCalenderWidget.COLLECTION_VIEW_EXTRA, position);
//        rv.setOnClickFillInIntent(R.id.rl_widget_device, fillInIntent);
//
//
//        Intent lockIntent = new Intent();
//        lockIntent.putExtra(DayCalenderWidget.COLLECTION_VIEW_EXTRA, position);
//        lockIntent.putExtra("Type", 1);
//        rv.setOnClickFillInIntent(R.id.iv_lock, lockIntent);
//
//        Intent unlockIntent = new Intent();
//        unlockIntent.putExtra("Type", 2);
//        unlockIntent.putExtra(DayCalenderWidget.COLLECTION_VIEW_EXTRA, position);
//        rv.setOnClickFillInIntent(R.id.iv_unlock, unlockIntent);

        return rv;
    }


    /**
     * 初始化ListView的数据
     */
    private void initListViewData() {
        scheduleList = new ArrayList<>();
        scheduleList.add(new Schedule(LocalDate.now(), LocalDate.now(), LocalTime.now(), LocalTime.now(), "1", "1", "1"));
        scheduleList.add(new Schedule(LocalDate.now(), LocalDate.now(), LocalTime.now(), LocalTime.now(), "1", "1", "1"));
        scheduleList.add(new Schedule(LocalDate.now(), LocalDate.now(), LocalTime.now(), LocalTime.now(), "1", "1", "1"));

        //连数据库
        // 搜索得到的对应DB的List

    }


    public static void refresh(){
        scheduleList.add(new Schedule(LocalDate.now(), LocalDate.now(), LocalTime.now(), LocalTime.now(), "1", "1", "1"));
    }

    @Override
    public void onCreate() {
        Toast.makeText(mContext,"onCreate", Toast.LENGTH_SHORT).show();
        // 初始化“集合视图”中的数据
        initListViewData();
    }

    @Override
    public int getCount() {
        // 返回“集合视图”中的数据的总数
        return scheduleList.size();
    }

    @Override
    public long getItemId(int position) {
        // 返回当前项在“集合视图”中的位置
        return position;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        // 只有一类 ListView
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onDataSetChanged() {
    }

    @Override
    public void onDestroy() {
        scheduleList.clear();
    }
}
