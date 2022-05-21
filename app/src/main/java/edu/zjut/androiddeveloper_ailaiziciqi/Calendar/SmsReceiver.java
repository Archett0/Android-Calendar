package edu.zjut.androiddeveloper_ailaiziciqi.Calendar;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.alibaba.fastjson.JSON;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model.Schedule;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model.SmsSearchInformation;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w("sms", "run");
        Toast.makeText(context, "收到信息", Toast.LENGTH_SHORT).show();
        Bundle bundle = intent.getExtras();
        //提取短信消息
        Object[] pdus = (Object[]) bundle.get("pdus");
        SmsMessage[] messages = new SmsMessage[pdus.length];
        for (int i = 0; i < messages.length; i++) {
            messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
        }
        //获取发送方号码
        String address = messages[0].getOriginatingAddress();

        String fullMessage = "";
        for (SmsMessage message : messages) {
            //获取短信内容
            fullMessage += message.getMessageBody();
        }
        //截断广播,阻止其继续被Android自带的短信程序接收到
        Log.w("message", address);
        Log.w("message", fullMessage);
        abortBroadcast();

        if (fullMessage.contains("【爱瓷日历】查询日程时间")) {
            Log.w("send", "");
            SmsManager massage = SmsManager.getDefault();

            LocalDate localDate = LocalDate.now();
            LocalTime localTime = LocalTime.now();
            Schedule schedule1 = new Schedule(localDate, localTime, localTime, "0", "五月十二", "这是一些日程");
            Schedule schedule2 = new Schedule(localDate, localTime, localTime, "1", "五月十二", "这是一些日程");

            ArrayList<Schedule> listS = new ArrayList<>();
            listS.add(schedule1);
            listS.add(schedule2);

            ArrayList<String> result = scheduleLists2StringList(listS, fullMessage.split(":")[2]);

            Log.w("send", "result");
            massage.sendMultipartTextMessage(address.substring(address.length() - 4, address.length()), null, result, null, null);
        }
    }

    private ArrayList<String> scheduleLists2StringList(ArrayList<Schedule> list, String address) {
        ArrayList<String> result = new ArrayList<>();

        result.add("【爱瓷日历】查询结果:/");

        result.add(address + "/");

        DateTimeFormatter date_ = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        result.add(list.get(0).getScheduleDate().format(date_) + "/");

        for (Schedule s : list) {
            String item = "";
            item += s.getSchedule() + ";";
            DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
            item += s.getScheduleDate().format(date) +
                    "星期" + s.getWeek() + " 农历" + s.getLunar() + ";";

            DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm");
            item += s.getScheduleStartTime().format(time) + ";";

            item += s.getScheduleEndTime().format(time) + "/";

            result.add(item);
        }

        return result;
    }
}
