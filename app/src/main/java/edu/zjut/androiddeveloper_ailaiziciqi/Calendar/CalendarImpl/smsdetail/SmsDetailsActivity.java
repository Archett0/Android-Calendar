package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.smsdetail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.search.SearchActivity;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.search.SearchListAdapter;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.search.SmsSearchActivity;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.search.SmsSearchAdapter;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.R;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model.Schedule;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model.ScheduleWithCheck;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model.SmsSearchInformation;

public class SmsDetailsActivity extends AppCompatActivity {
    private SmsDetailsAdapter smsDetailsAdapter;

    private ImageView back; //返回按钮

    private ListView lv_show; // 列表

    private TextView phone;

    private Intent intent;
    private String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_details);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        intent = this.getIntent();
        id = intent.getStringExtra("id");

        phone = findViewById(R.id.phone);
        phone.setText("18811678671");

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        lv_show = findViewById(R.id.lv_show);

        updateListView();
    }

    public void updateListView() {
        List<Schedule> list = new ArrayList<>();
        LocalDate localDate = LocalDate.now();
        LocalTime localTime = LocalTime.now();
        Schedule schedule1 = new Schedule(localDate, localTime, localTime, "0", "五月十二", "这是一些日程");
        list.add(schedule1);

        smsDetailsAdapter = new SmsDetailsAdapter(list, SmsDetailsActivity.this);
        lv_show.setAdapter(smsDetailsAdapter);
    }
}