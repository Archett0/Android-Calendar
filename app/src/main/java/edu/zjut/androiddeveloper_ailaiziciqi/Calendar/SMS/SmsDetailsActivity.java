package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.SMS;

import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Event.ScheduleUtils.updateDataFromDatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.R;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Model.Schedule;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.Model.SmsSearchInformation;

public class SmsDetailsActivity extends AppCompatActivity {
    private SmsDetailsAdapter smsDetailsAdapter;

    private ImageView back; //返回按钮
    private ListView lv_show; // 列表
    private TextView phone;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_details);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        intent = this.getIntent();
        int id = intent.getIntExtra("id", -1);

        phone = findViewById(R.id.phone);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        lv_show = findViewById(R.id.lv_show);


        if (id != -1) {
            updateListView(id);
        } else {
            Toast.makeText(this, "点击的id为null", Toast.LENGTH_SHORT).show();
        }
    }

    public void updateListView(int id) {
        // 根据id找到这个短信
        List<SmsSearchInformation> singleSms = updateDataFromDatabase(this, id);

        // 仅仅在不为空且长度唯一的情况下才是正确的
        if (singleSms != null && singleSms.size() == 1) {
            List<Schedule> list = singleSms.get(0).getSmsScheduleList();
            phone.setText(singleSms.get(0).getPhone());
            smsDetailsAdapter = new SmsDetailsAdapter(list, SmsDetailsActivity.this);
            lv_show.setAdapter(smsDetailsAdapter);
        } else {
            Toast.makeText(this, "短信详情加载出错！", Toast.LENGTH_SHORT).show();
        }
    }
}