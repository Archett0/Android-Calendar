package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.R;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Model.Schedule;

/**
 *  小时Cell的适配器
 */
@Deprecated
public class HourAdapter extends ArrayAdapter<HourEvent> {

    public HourAdapter(@NonNull Context context, @NonNull List<HourEvent> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        HourEvent event = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.hour_cell, parent, false);
        }

        setHour(convertView, event.time);
        setEvents(convertView, event.schedules);

        return convertView;
    }

    // 设置一个时间cell
    private void setHour(View convertView, LocalTime time) {
        TextView timeTV = convertView.findViewById(R.id.timeTV);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        timeTV.setText(time.format(formatter));
    }

    // 设置当前时间cell的全部日程
    private void setEvents(View convertView, ArrayList<Schedule> schedules) {
        TextView event1 = convertView.findViewById(R.id.schedule1);
        TextView event2 = convertView.findViewById(R.id.schedule2);
        TextView event3 = convertView.findViewById(R.id.schedule3);

        if (schedules.size() == 0) {
            hideEvent(event1);
            hideEvent(event2);
            hideEvent(event3);
        } else if (schedules.size() == 1) {
            setEvent(event1, schedules.get(0));
            hideEvent(event2);
            hideEvent(event3);
        } else if (schedules.size() == 2) {
            setEvent(event1, schedules.get(0));
            setEvent(event2, schedules.get(1));
            hideEvent(event3);
        } else if (schedules.size() == 3) {
            setEvent(event1, schedules.get(0));
            setEvent(event2, schedules.get(1));
            setEvent(event3, schedules.get(2));
        } else {    // 最多显示三个,若更多则显示更多events
            setEvent(event1, schedules.get(0));
            setEvent(event2, schedules.get(1));
            event3.setVisibility(View.VISIBLE);
            String eventsNotShown = "还有";
            eventsNotShown += String.valueOf(schedules.size() - 2) + "个日程";
            event3.setText(eventsNotShown);
        }
    }

    // 设置显示一个日程
    private void setEvent(TextView textView, Schedule schedule) {
        textView.setText(schedule.getSchedule());
        textView.setVisibility(View.VISIBLE);
    }

    // 隐藏一个日程
    private void hideEvent(TextView textView) {
        textView.setVisibility(View.INVISIBLE);
    }

}
