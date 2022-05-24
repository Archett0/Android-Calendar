package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.search;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.R;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model.SmsSearchInformation;

public class SmsSearchAdapter extends BaseAdapter {
    //使用list<Nate>,list会存储数据库中note表所有记录。
    private List<SmsSearchInformation> list;
    //用于将某个布局转换为view的对象。
    private LayoutInflater layoutInflater;

    public List<SmsSearchInformation> getList() {
        return list;
    }

    //当创建MyAdapter对象的时候，需要list的数据
    public SmsSearchAdapter(List<SmsSearchInformation> list, Context context) {
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
            convertView = layoutInflater.inflate(R.layout.sms_search_layout_item, null, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);//绑定viewholder
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        //将数据库中的内容加载到对应的控件上
        SmsSearchInformation s = (SmsSearchInformation) getItem(position);
        viewHolder.phone_num.setText(s.getPhone());
        viewHolder.send_date.setText(s.getSendDate().toString());
        if(s.getSmsScheduleList() == null) {
            viewHolder.message.setText("无日程结果");
        } else {
            viewHolder.message.setText(s.getSmsScheduleList().get(0).getSchedule());
        }

        return convertView;
    }

    //用于给item的视图加载数据内容。
    class ViewHolder {
        TextView phone_num;
        TextView send_date;
        TextView message;

        public ViewHolder(View view) {
            phone_num = view.findViewById(R.id.phone_num);
            send_date = view.findViewById(R.id.send_date);
            message = view.findViewById(R.id.message);
        }
    }
}
