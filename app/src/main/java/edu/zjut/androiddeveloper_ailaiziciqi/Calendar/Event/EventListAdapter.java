package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import org.w3c.dom.Text;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.group.GroupRecyclerAdapter;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.mix.MixActivity;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.R;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * 每日日程列表适配器
 */
public class EventListAdapter extends GroupRecyclerAdapter<String, edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.Event> {

    private RequestManager mLoader;
    private static final Event defaultEvent = new Event("今日暂无日程", null, null, null);
    private static boolean emptyFlag = false;
    private OnItemClickListener mEventItemClickListener;    // 用于Activity监听列表点击事件的接口

    public interface OnItemClickListener {
        void onItemClick(View view, int position, Event event);
    }

    public void setOnItemClickListener(OnItemClickListener mEventItemClickListener) {
        this.mEventItemClickListener = mEventItemClickListener;
    }

    public EventListAdapter(Context context, LocalDate dayClickRecord) {
        super(context);
        mLoader = Glide.with(context.getApplicationContext());
        LinkedHashMap<String, List<edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.Event>> map = new LinkedHashMap<>();
        List<String> titles = new ArrayList<>();
        map.put("今日日程", getEventList(0, dayClickRecord));
        map.put("类别2待完善", getEventList(1, dayClickRecord));
        map.put("类别3待完善", getEventList(2, dayClickRecord));
        titles.add("今日日程");
        titles.add("类别2待完善");
        titles.add("类别3待完善");
        resetGroups(map, titles);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int type) {
        return new EventViewHolder(mInflater.inflate(R.layout.item_list_event, parent, false));
    }

    @Override
    protected void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.Event item, int position) {
        EventViewHolder h = (EventViewHolder) holder;
        h.mTextTitle.setText(item.getName());
        h.mEventTime.setText(String.valueOf(item.getTime()));
        h.mEventTimeEnd.setText(String.valueOf(item.getEndTime()));
        if (emptyFlag) {
            h.mTextTitle.setVisibility(View.VISIBLE);
            h.mEventTime.setVisibility(View.INVISIBLE);
            h.mEventTimeEnd.setVisibility(View.INVISIBLE);
            h.mMainCardBar.setVisibility(View.INVISIBLE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) h.mTextTitle.getLayoutParams();
            params.leftMargin = 0;
            h.mTextTitle.setLayoutParams(params);
        } else {
            h.mTextTitle.setVisibility(View.VISIBLE);
            h.mEventTime.setVisibility(View.VISIBLE);
            h.mEventTimeEnd.setVisibility(View.VISIBLE);
            h.mMainCardBar.setVisibility(View.VISIBLE);
        }
        // 监听子项的点击事件
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String eventName = item.getName();
                    int eventPosition = holder.getLayoutPosition();
                    Log.i("Event List Click", "------------");
                    Log.i("Event List Click", "Item:" + eventName);
                    Log.i("Event List Click", "Item:" + eventPosition);
                    Log.i("Event List Click", "------------");
                    mEventItemClickListener.onItemClick(holder.itemView, eventPosition, item);
                }
            });
    }

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

    private static List<Event> getEventList(int type, LocalDate date) {
        List<Event> eventList = new ArrayList<>();
        if (type == 0) {
            emptyFlag = false;
            eventList = Event.eventsForDate(date);
            if (eventList.isEmpty()) {
                eventList.add(defaultEvent);
                emptyFlag = true;
            }
        } else if (type == 1) {

        } else if (type == 2) {

        }
        return eventList;
    }


//    private static List<edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Article> create(int p) {
//        List<edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Article> list = new ArrayList<>();
//        if (p == 0) {
//
//        } else if (p == 1) {
//            //暂无类别2
//        } else if (p == 2) {
//            //暂无类别3
//        }
//
//
//        return list;
//    }
//
//    private static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Article create(String title, String content, String imgUrl) {
//        edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Article article = new edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Article();
//        article.setTitle(title);
//        article.setContent(content);
//        article.setImgUrl(imgUrl);
//        return article;
//    }
}
