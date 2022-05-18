package edu.zjut.androiddeveloper_ailaiziciqi.Calendar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.Event;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.HourAdapter;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.HourEvent;

@RequiresApi(api = Build.VERSION_CODES.O)
public class DailyCalendarActivity extends AppCompatActivity {

    private ListView hourListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_calendar);
        initWidgets();
    }

    private void initWidgets() {
        hourListView = findViewById(R.id.hourListView);
    }

//    public void newEventAction(View view) {
//        Toast.makeText(this,"Trying to add an event",Toast.LENGTH_SHORT);
//    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        setDayView();
    }

    private void setDayView() {
        //Do nothing here because we do not have this requirement
        setHourAdapter();

    }

    private void setHourAdapter() {
        HourAdapter hourAdapter = new HourAdapter(getApplicationContext(), hourEventList());
        hourListView.setAdapter(hourAdapter);
    }

    private ArrayList<HourEvent> hourEventList() {
        ArrayList<HourEvent> list = new ArrayList<>();
        for(int hour=0;hour<24;++hour){
            LocalDate date = LocalDate.now();
            LocalTime time = LocalTime.of(hour,0);
            ArrayList<Event> events = Event.eventsForDateAndTime(date,time);
            HourEvent hourEvent = new HourEvent(time,events);
            list.add(hourEvent);
        }
        return list;
    }
}