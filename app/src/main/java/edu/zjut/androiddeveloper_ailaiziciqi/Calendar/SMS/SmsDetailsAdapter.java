package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.SMS;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.time.format.DateTimeFormatter;
import java.util.List;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.R;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Model.Schedule;

public class SmsDetailsAdapter extends BaseAdapter {
    //使用list<Nate>,list会存储数据库中note表所有记录。
    private List<Schedule> list;
    //用于将某个布局转换为view的对象。
    private LayoutInflater layoutInflater;

    public List<Schedule> getList() {
        return list;
    }

    //当创建MyAdapter对象的时候，需要list的数据
    public SmsDetailsAdapter(List<Schedule> list, Context context) {
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.layout_item, null, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);//绑定viewholder
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //将数据库中的内容加载到对应的控件上
        Schedule s = (Schedule) getItem(position);
        viewHolder.schedule.setText(s.getSchedule());
        DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
        viewHolder.date.setText(s.getScheduleDate().format(date) +
                s.getWeek() + " " + s.getLunar());

        DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm");
        viewHolder.timeStart.setText(s.getScheduleStartTime().format(time));

        viewHolder.timeEnd.setText(s.getScheduleEndTime().format(time));
        return convertView;
    }

    //用于给item的视图加载数据内容。
    class ViewHolder {
        TextView schedule;
        TextView date;
        TextView timeStart;
        TextView timeEnd;
        CheckBox checkBox;
        private LinearLayout mCardWithNoEvent;
        private TextView mNoEventHint;

        public ViewHolder(View view) {
            schedule = view.findViewById(R.id.schedule);
            date = view.findViewById(R.id.date);
            timeStart = view.findViewById(R.id.time_start);
            timeEnd = view.findViewById(R.id.time_end);
            checkBox = view.findViewById(R.id.check_box);
            checkBox.setVisibility(View.INVISIBLE);
            mCardWithNoEvent = view.findViewById(R.id.select_card_with_no_event);
            mCardWithNoEvent.setVisibility(View.GONE);
            mNoEventHint = view.findViewById(R.id.no_event_info);
            mNoEventHint.setText("没有符合条件的日程");
        }
    }
}
