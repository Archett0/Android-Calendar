package edu.zjut.androiddeveloper_ailaiziciqi.Calendar;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.TypeReference;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.search.SmsSearchActivity;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DB.DbContact;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model.Schedule;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model.ScheduleString;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.model.SmsSearchInformation;

public class SmsReceiver extends BroadcastReceiver {
    private static SmsSearchActivity smsSearchActivity = null;
    private static boolean SEND_OR_NOT = true;
    private static boolean RECEIVE_OR_NOT = true;

    public static void setSmsSearchActivity(SmsSearchActivity smsSearchActivity) {
        SmsReceiver.smsSearchActivity = smsSearchActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.w("sms", "run");
//        Toast.makeText(context, "收到信息", Toast.LENGTH_SHORT).show();
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

        // 接收到对方的查询请求后，立刻发送信息
        if (fullMessage.contains("【爱瓷日历】查询日程时间")) {
            // do not send same data
            if (SEND_OR_NOT) {
                SEND_OR_NOT = false;
                Log.w("send", "");
                SmsManager massage = SmsManager.getDefault();

                int indexNian = fullMessage.indexOf("年");
                int indexYue = fullMessage.indexOf("月");
                int indexRi = fullMessage.lastIndexOf("日");
                String yearS = fullMessage.substring(indexNian - 4, indexNian);
                String monthS = fullMessage.substring(indexNian + 1, indexYue);
                String dayS = fullMessage.substring(indexYue + 1, indexRi);
                int year = Integer.parseInt(yearS);
                int month = Integer.parseInt(monthS);
                int day = Integer.parseInt(dayS);
                LocalDate localDate = LocalDate.of(year, month, day);
                ArrayList<Schedule> listS = new ArrayList<>();
                listS = Schedule.eventsForDate(localDate);
                ArrayList<String> result = scheduleLists2StringList(listS, fullMessage.split(":")[2], localDate);

                Log.w("send", "result");
                massage.sendMultipartTextMessage(address.substring(address.length() - 4, address.length()), null, result, null, null);
            }
            // if duplicated data is detected, we just don't send again
            else {
                SEND_OR_NOT = true;
            }
        }

        // 接收到对方发送的信息后，立刻保存进入数据库
        if (fullMessage.contains("【爱瓷日历】查询结果:")) {
            // only receive the first set of data
            if (RECEIVE_OR_NOT) {
                RECEIVE_OR_NOT = false;
                String[] result = fullMessage.split("/");
                String phone = result[1];
                String[] date = result[2].split("-");
                LocalDate sendDate = LocalDate.of(Integer.parseInt(date[0]), Integer.parseInt(date[1]), Integer.parseInt(date[2]));

                if (result.length == 3) {
                    SmsSearchInformation modelToSave = new SmsSearchInformation(phone, sendDate, new ArrayList<>());
                    Log.i("Random Debug", modelToSave.getPhone());
                    Log.i("Random Debug", String.valueOf(modelToSave.getSendDate()));
                    Log.i("Random Debug", "没有日程");

                    // 调用方法进行保存
                    saveSms(modelToSave);
                    if (smsSearchActivity != null) {
                        // 通知更新列表
                        smsSearchActivity.updateListView();
                    }
                    return; //代表没有查询结果
                }

                // 构建Schedule列表
                List<Schedule> listS = new ArrayList<>();
                for (int i = 3; i < result.length; ++i) {
                    String[] item = result[i].split(";");
                    // name / date + dayOfWeek + lunar / startTime / endTime
                    Schedule s = new Schedule(item[0], item[1], item[2], item[3]);
                    listS.add(s);
                }

                // 构建要保存的对象
                SmsSearchInformation modelToSave = new SmsSearchInformation(phone, sendDate, listS);

                Log.i("Random Debug", modelToSave.getPhone());
                Log.i("Random Debug", String.valueOf(modelToSave.getSendDate()));
                if (modelToSave.getSmsScheduleList() != null) {
                    for (Schedule schedule : modelToSave.getSmsScheduleList()) {
                        Log.i("Random Debug", schedule.toString());
                    }
                }
                // 调用方法进行保存
                saveSms(modelToSave);
            } else {
                RECEIVE_OR_NOT = true;
            }

            if (smsSearchActivity != null) {
                // 通知更新列表
                smsSearchActivity.updateListView();
            }
        }
    }

    /**
     * 将一个短信内容保存到数据库
     *
     * @param model 短信对象
     * @return 是否保存成功：1成功保存，0为保存失败
     */
    private boolean saveSms(final SmsSearchInformation model) {
        boolean isSaved = false;

        //将List转化成可以保存的String
        String arrString = JSONArray.toJSONString(model.getTransformedStringList());
        Log.i("Random Debug", "正尝试保存短信内容");
        Log.i("Random Debug", "date to store:" + String.valueOf(model.getSendDate()));
        Log.i("Random Debug", "phone to store:" + model.getPhone());
        Log.i("Random Debug", "schedule list:" + arrString);

        // 获取ContentValues并放入值
        ContentValues values = new ContentValues();
        values.put(DbContact.SmsEntry.COLUMN_PHONE_NUMBER, model.getPhone());
        values.put(DbContact.SmsEntry.COLUMN_SMS_DATE, String.valueOf(model.getSendDate()));
        values.put(DbContact.SmsEntry.COLUMN_SCHEDULES, arrString);

        // 生成Uri并执行存储
        Uri newUri = smsSearchActivity.getContentResolver().insert(DbContact.SmsEntry.SMS_CONTENT_URI, values);
        if (newUri == null) {
            Toast.makeText(smsSearchActivity, "保存出错", Toast.LENGTH_SHORT).show();
            Log.i("Random Debug", "保存出错");
        } else {
            Toast.makeText(smsSearchActivity, "接收的日程已经保存", Toast.LENGTH_SHORT).show();
            isSaved = true;
            Log.i("Random Debug", "接收的日程已经保存");
        }
        return isSaved;
    }

    /**
     * 用于发送日程时的格式化，将schedule转为string
     *
     * @param list      要保存的日程列表
     * @param address   手机号或端口号
     * @param localDate 查询的日期
     * @return 生成好的String类型List
     */
    private ArrayList<String> scheduleLists2StringList(ArrayList<Schedule> list, String address, LocalDate localDate) {
        ArrayList<String> result = new ArrayList<>();

        result.add("【爱瓷日历】查询结果:/");

        result.add(address + "/");

        result.add(localDate.toString() + "/");

        for (Schedule s : list) {
            String item = "";
            item += s.getSchedule() + ";";
            DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
            item += s.getScheduleDate().format(date) +
                    s.getWeek() + " 农历" + s.getLunar() + ";";

            DateTimeFormatter time = DateTimeFormatter.ofPattern("HH:mm");
            item += s.getScheduleStartTime().format(time) + ";";

            item += s.getScheduleEndTime().format(time) + "/";

            result.add(item);
        }

        return result;
    }
}
