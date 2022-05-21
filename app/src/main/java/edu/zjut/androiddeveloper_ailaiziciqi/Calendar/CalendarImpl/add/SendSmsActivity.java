package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.add;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.R;

public class SendSmsActivity extends AppCompatActivity {
    private int year, month, day;

    // 手机号
    private TextView schedule_phone;
    // 日程时间
    private TextView time_schedule;
    // 时间选择下拉框
    private ImageView time_spinner;

    // 返回
    private ImageView cancle;
    // 发送
    private ImageView send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_sms);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // 获得发送短信权限
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS} , 1);

        schedule_phone = findViewById(R.id.schedule_phone);

        time_schedule = findViewById(R.id.time_schedule);

        time_spinner = findViewById(R.id.time_spinner);

        getDate();
        time_schedule.setText(year + "年" + (month + 1) + "月" + day + "日");
        ClickEvent clickEventTimeStart = new ClickEvent(time_schedule, year, month, day);

        time_spinner.setOnClickListener(clickEventTimeStart);

        cancle = findViewById(R.id.cancle);
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        send = findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = schedule_phone.getText().toString();
                String time = time_schedule.getText().toString();

                if (phone.equals("") || time.equals(""))
                //判断输入是否有空格
                {
                    Toast.makeText(SendSmsActivity.this, "输入有误，请检查输入", Toast.LENGTH_LONG).show();
                } else {
                    SmsManager massage = SmsManager.getDefault();
                    massage.sendTextMessage(phone, null, "【爱瓷日历】查询日程时间:"+time+",待查询号码:"+phone, null, null);
                    //调用senfTextMassage方法来发送短信
                    Toast.makeText(SendSmsActivity.this, "短信发送成功", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        });
    }

    private void getDate() {
        Calendar cal = Calendar.getInstance();
        year = cal.get(Calendar.YEAR);   //获取年月日时分秒
        month = cal.get(Calendar.MONTH);   //获取到的月份是从0开始计数
        day = cal.get(Calendar.DAY_OF_MONTH);
    }

    private class ClickEvent implements View.OnClickListener {
        private TextView textView;
        private int year, month, day;

        public ClickEvent(TextView textView, int year, int month, int day) {
            this.textView = textView;
            this.year = year;
            this.month = month;
            this.day = day;
        }

        @Override
        public void onClick(View view) {
            DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker arg0, int year, int month, int day) {
                    textView.setText(year + "年" + (month + 1) + "月" + day + "日");      //将选择的日期显示到TextView中,因为之前获取month直接使用，所以不需要+1，这个地方需要显示，所以+1
                }
            };
            DatePickerDialog dialog = new DatePickerDialog(SendSmsActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, listener, year, month, day);//后边三个参数为显示dialog时默认的日期，月份从0开始，0-11对应1-12个月
            dialog.show();
        }
    }
}