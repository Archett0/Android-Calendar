package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.R;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Model.Schedule;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Model.ScheduleWithCheck;

public class SearchListAdapter extends BaseAdapter {
    //使用list<Nate>,list会存储数据库中note表所有记录。
    private List<ScheduleWithCheck> list;
    //用于将某个布局转换为view的对象。
    private LayoutInflater layoutInflater;
    //用于保存每一个view
    private Map<Integer, View> viewMap;

    private static SearchActivity searchActivity;

    public List<ScheduleWithCheck> getList() {
        return list;
    }

    // 返回所有被选中的schedule
    public List<Schedule> getListSelected() {
        List<Schedule> selectedList = new ArrayList<>();
        for (ScheduleWithCheck s : list) {
            if (s.getIsChecked()) {
                selectedList.add(s.getSchedule());
            }
        }
        return selectedList;
    }

    //当创建MyAdapter对象的时候，需要list的数据
    public SearchListAdapter(List<ScheduleWithCheck> list, Context context) {
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
        viewMap = new HashMap<>(); //创建与list相同大小的键值对viewholder数组
        SearchListAdapter.searchActivity = (SearchActivity) context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position).getSchedule();
    }

    //    设置每个checkbox可见
    public void setAllCheckboxVisible() {
        Log.w("list_size", list.size() + "");
        for (View v : viewMap.values()) {
            ViewHolder x = (ViewHolder) v.getTag();
            x.checkBox.setVisibility(View.VISIBLE);
            notifyDataSetChanged();
        }
        for (ScheduleWithCheck s : list) {
            s.setVisible(true);
        }
    }

    //    设置每个checkbox不可见
    public void setAllCheckboxInvisible() {
        for (View v : viewMap.values()) {
            ViewHolder x = (ViewHolder) v.getTag();
            x.checkBox.setVisibility(View.INVISIBLE);
            notifyDataSetChanged();
        }
        for (ScheduleWithCheck s : list) {
            s.setVisible(false);
        }
    }

    // 设置所有checkbox都被选择
    public void setAllCheckBoxSelected() {
        Log.w("所有checkbox都被选择函数：", "");
        for (View v : viewMap.values()) {
            ViewHolder x = (ViewHolder) v.getTag();
            x.checkBox.setSelected(true);
            notifyDataSetChanged();
        }
        for (ScheduleWithCheck s : list) {
            s.setIsChecked(true);
        }
    }

    // 设置所有checkbox都不被选择
    public void setAllCheckBoxNotSelected() {
        for (View v : viewMap.values()) {
            ViewHolder x = (ViewHolder) v.getTag();
            x.checkBox.setSelected(false);
            notifyDataSetChanged();
        }
        for (ScheduleWithCheck s : list) {
            s.setIsChecked(false);
        }
    }

    @SuppressLint("RestrictedApi")
    private void isAllCheckBoxSelected() {
        Log.w("size_of_viewHolder", list.size() + "");
        int i = 0;
        for (ScheduleWithCheck s : list) {
            if (s.getIsChecked()) {
                Log.w("当前checkbox被选中", i + "");
                ViewHolder h = (ViewHolder) viewMap.get(i).getTag();
                Log.w("当前ViewHolder被选中", h.position + "");
                searchActivity.setCheckBoxSelected(true);
                searchActivity.getItemView().setEnabled(true);
                return;
            }
            ++i;
        }
        Log.w("当前没有checkbox被选中", "");
        searchActivity.setCheckBoxSelected(false);
        searchActivity.getItemView().setEnabled(false);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        Log.w("getView position", position+"");

        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.layout_item, null, false);
            viewHolder = new ViewHolder(convertView, position);
            convertView.setTag(viewHolder);//绑定viewholder

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //将数据库中的内容加载到对应的控件上
        Schedule schedule = (Schedule) getItem(position);

        // 如果查找不到日程，就设置组件不可见
        if (schedule.getScheduleDate().equals(LocalDate.of(1999, 1, 1))) {
            viewHolder.date.setVisibility(View.GONE);
            viewHolder.mCardWithEvent.setVisibility(View.GONE);
            viewHolder.mCardWithNoEvent.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mCardWithEvent.setVisibility(View.VISIBLE);
            viewHolder.mCardWithNoEvent.setVisibility(View.GONE);
            viewHolder.schedule.setText(schedule.getSchedule());

            DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
            viewHolder.date.setText(schedule.getScheduleDate().format(date) + schedule.getWeek() + " 农历" + schedule.getLunar());

            DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm");
            LocalDate eventStartDate = schedule.getScheduleDate();
            LocalDate eventEndDate = schedule.getScheduleEndDate();
            LocalTime eventStartTime = schedule.getScheduleStartTime();
            LocalTime eventEndTime = schedule.getScheduleEndTime();
            LocalDate selectedDate = LocalDate.now();
            if (eventStartDate.equals(eventEndDate)) {
                // if its a all-day event
                if (eventStartTime.equals(LocalTime.of(0, 0)) && eventEndTime.equals(LocalTime.of(23, 59))) {
                    viewHolder.timeStart.setText("全天日程");
                    viewHolder.timeEnd.setVisibility(View.GONE);
                } else {
                    viewHolder.timeStart.setText(schedule.getScheduleStartTime().format(time));
                    viewHolder.timeEnd.setText(schedule.getScheduleEndTime().format(time));
                    viewHolder.timeEnd.setVisibility(View.VISIBLE);
                }
            }
            // spans date forward
            else if (eventStartDate.equals(selectedDate) && eventEndDate.isAfter(selectedDate)) {
                viewHolder.timeStart.setText("跨天日程");
                viewHolder.timeEnd.setText((eventStartDate + "").substring(5) + "开始");
                viewHolder.timeEnd.setVisibility(View.VISIBLE);
            }
            // spans date from before
            else if (eventEndDate.equals(selectedDate) && eventStartDate.isBefore(selectedDate)) {
                viewHolder.timeStart.setText("跨天日程");
                viewHolder.timeEnd.setText((eventEndDate + "").substring(5) + "结束");
                viewHolder.timeEnd.setVisibility(View.VISIBLE);
            }
            // spans date from both before and forward
            else if (eventStartDate.isBefore(selectedDate) && eventEndDate.isAfter(selectedDate)) {
                viewHolder.timeStart.setText("跨天日程");
                viewHolder.timeEnd.setText("今天全天");
                viewHolder.timeEnd.setVisibility(View.VISIBLE);
            } else {
                viewHolder.timeStart.setText((eventStartDate + "").substring(5));
                viewHolder.timeEnd.setText((eventEndDate + "").substring(5));
                viewHolder.timeEnd.setVisibility(View.VISIBLE);
            }

            if (list.get(position).getIsChecked()) {
                viewHolder.checkBox.setChecked(true);
            } else {
                viewHolder.checkBox.setChecked(false);
            }

            if (list.get(position).isVisible()) {
                viewHolder.checkBox.setVisibility(View.VISIBLE);
            } else {
                viewHolder.checkBox.setVisibility(View.INVISIBLE);
            }
        }
//        Log.w("SchedulePosition", viewHolder.position+"");
        viewHolder.position = position;
        viewMap.put(position, convertView);
//        Log.w("data", viewHolder.date.getText()+"");
        return convertView;
    }

    //用于给item的视图加载数据内容。
    public class ViewHolder {
        int position;
        TextView schedule;
        TextView date;
        TextView timeStart;
        TextView timeEnd;
        CheckBox checkBox;
        private LinearLayout mCardWithEvent;
        private LinearLayout mCardWithNoEvent;
        private TextView mNoEventHint;

        public ViewHolder(View view, int position) {
            this.position = position;
            Log.w("ViewHolder position", position + "");
            schedule = view.findViewById(R.id.schedule);
            date = view.findViewById(R.id.date);
            timeStart = view.findViewById(R.id.time_start);
            timeEnd = view.findViewById(R.id.time_end);
            checkBox = view.findViewById(R.id.check_box);
            mCardWithEvent = view.findViewById(R.id.select_card_with_event);
            mCardWithNoEvent = view.findViewById(R.id.select_card_with_no_event);
            mNoEventHint = view.findViewById(R.id.no_event_info);
            mNoEventHint.setText("没有符合条件的日程");
            checkBox.setVisibility(View.INVISIBLE);
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    list.get(ViewHolder.this.position).setIsChecked(checkBox.isChecked());
//                    Log.w("checkbox",String.valueOf(checkBox.isChecked()));
                    isAllCheckBoxSelected();
                    Log.w("星期", list.get(ViewHolder.this.position).getSchedule().getWeek());
                }
            });
        }
    }
}