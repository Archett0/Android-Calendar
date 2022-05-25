package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event;

import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.loadOrReloadDataFromDatabase;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.group.GroupRecyclerAdapter;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.R;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model.Schedule;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 每日日程列表适配器
 */
public class ScheduleListAdapter extends GroupRecyclerAdapter<String, Schedule> {

    private RequestManager mLoader;
    private static final Schedule DEFAULT_SCHEDULE = new Schedule("今日暂无日程");
    private static boolean emptyFlag = false;
    private OnItemClickListener mEventItemClickListener;    // 用于Activity监听列表点击事件的接口
    private Context context;
    private Cursor cursor;
    private LocalDate selectedDate;

    public interface OnItemClickListener {
        void onItemClick(View view, int position, Schedule schedule);
    }

    public void setOnItemClickListener(OnItemClickListener mEventItemClickListener) {
        this.mEventItemClickListener = mEventItemClickListener;
    }

    // 1: Constructor will be executed first.
    public ScheduleListAdapter(Context context, LocalDate dayClickRecord, Cursor cursor) {
        super(context);
        // 从数据库读取所有日程
        loadOrReloadDataFromDatabase(context.getContentResolver(), "Test load");
        this.context = context;
        this.cursor = cursor;
        this.selectedDate = dayClickRecord;
        mLoader = Glide.with(context.getApplicationContext());
        LinkedHashMap<String, List<Schedule>> map = new LinkedHashMap<>();
        List<String> titles = new ArrayList<>();
        map.put("今日日程", getEventList(0, dayClickRecord));
//        map.put("类别2待完善", getEventList(1, dayClickRecord));
//        map.put("类别3待完善", getEventList(2, dayClickRecord));
        titles.add("今日日程");
//        titles.add("类别2待完善");
//        titles.add("类别3待完善");
        resetGroups(map, titles);
    }

    // A method to reset data to the adapter
    public void resetCurrentAdapter(Context context, LocalDate dayClickRecord, Cursor cursor) {
        loadOrReloadDataFromDatabase(context.getContentResolver(), "Test loading");
        this.context = context;
        this.cursor = cursor;
        this.selectedDate = dayClickRecord;
        mLoader = Glide.with(context.getApplicationContext());
        LinkedHashMap<String, List<Schedule>> map = new LinkedHashMap<>();
        List<String> titles = new ArrayList<>();
        map.put("今日日程", getEventList(0, dayClickRecord));
//        map.put("类别2待完善", getEventList(1, dayClickRecord));
//        map.put("类别3待完善", getEventList(2, dayClickRecord));
        titles.add("今日日程");
//        titles.add("类别2待完善");
//        titles.add("类别3待完善");
        resetGroups(map, titles);
    }

    // 2. And we should get the inflated view to ViewHolder class.
    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_list_event, parent, false);
        return new EventViewHolder(view);
        //        return new EventViewHolder(mInflater.inflate(R.layout.item_list_event, parent, false));
    }

    // 3: After that we go here to set the parameters and texts for the views we got.
    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") Schedule item, int position) {
        EventViewHolder h = (EventViewHolder) holder;

        // 设定日程的标题
        h.mTextTitle.setText(item.getSchedule());

        if (!emptyFlag) {
            // 判断日程的时间,并根据情况来设定
            LocalDate eventStartDate = item.getScheduleDate();
            LocalDate eventEndDate = item.getScheduleEndDate();
            LocalTime eventStartTime = item.getScheduleStartTime();
            LocalTime eventEndTime = item.getScheduleEndTime();

            // event within this date
            if (eventStartDate.equals(eventEndDate)) {
                h.mEventTime.setText(String.valueOf(eventStartTime));
                h.mEventTimeEnd.setText(String.valueOf(eventEndTime));
            }
            // spans date forward
            else if (eventStartDate.equals(selectedDate) && eventEndDate.isAfter(selectedDate)) {
                h.mEventTime.setText("跨天日程");
                h.mEventTimeEnd.setText(eventStartTime + "开始");
            }
            // spans date from before
            else if (eventEndDate.equals(selectedDate) && eventStartDate.isBefore(selectedDate)) {
                h.mEventTime.setText("跨天日程");
                h.mEventTimeEnd.setText(eventEndTime + "结束");
            }
            // spans date from both before and forward
            else if (eventStartDate.isBefore(selectedDate) && eventEndDate.isAfter(selectedDate)) {
                h.mEventTime.setText("跨天日程");
                h.mEventTimeEnd.setText("今天全天");
            }
        } else {
            h.mEventTime.setText(String.valueOf(item.getScheduleStartTime()));
            h.mEventTimeEnd.setText(String.valueOf(item.getScheduleEndTime()));
        }

        // 根据是否没有事件来改变控件可见度
        if (emptyFlag) {
            h.mTextTitle.setVisibility(View.VISIBLE);
            h.mEventTime.setVisibility(View.INVISIBLE);
            h.mEventTimeEnd.setVisibility(View.INVISIBLE);
            h.mMainCardBar.setVisibility(View.INVISIBLE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) h.mTextTitle.getLayoutParams();
            params.leftMargin = 0;
            h.mTextTitle.setLayoutParams(params);
        } else {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) h.mTextTitle.getLayoutParams();
            params.leftMargin = 50;
            h.mTextTitle.setLayoutParams(params);
            h.mTextTitle.setVisibility(View.VISIBLE);
            h.mEventTime.setVisibility(View.VISIBLE);
            h.mEventTimeEnd.setVisibility(View.VISIBLE);
            h.mMainCardBar.setVisibility(View.VISIBLE);
        }
        // 监听子项的点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String eventName = item.getSchedule();
                int eventPosition = holder.getLayoutPosition();
                Log.i("Event List Click", "------------");
                Log.i("Event List Click", "Item:" + eventName);
                Log.i("Event List Click", "Item:" + eventPosition);
                Log.i("Event List Click", "------------");
                mEventItemClickListener.onItemClick(holder.itemView, eventPosition, item);
            }
        });
    }

    // 3: Then we should write this static class to get the views.
    private static class EventViewHolder extends RecyclerView.ViewHolder {
        private TextView mTextTitle, mEventTime, mEventTimeEnd;
        private View mMainCardBar;

        private EventViewHolder(View itemView) {
            super(itemView);
            mTextTitle = itemView.findViewById(R.id.tv_title);
            mEventTime = itemView.findViewById(R.id.eventTime);
            mEventTimeEnd = itemView.findViewById(R.id.eventTimeEnd);
            mMainCardBar = itemView.findViewById(R.id.main_card_vertical_bar);
        }
    }

    private static List<Schedule> getEventList(int type, LocalDate date) {
        List<Schedule> scheduleList = new ArrayList<>();
        if (type == 0) {
            emptyFlag = false;
            scheduleList = Schedule.eventsForDate(date);
            if (scheduleList.isEmpty()) {
                scheduleList.add(DEFAULT_SCHEDULE);
                emptyFlag = true;
            }
        }
//        else if (type == 1) {
//
//        } else if (type == 2) {
//
//        }
        return scheduleList;
    }
}
