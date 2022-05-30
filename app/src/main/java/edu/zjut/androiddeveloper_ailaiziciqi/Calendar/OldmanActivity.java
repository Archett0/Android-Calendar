package edu.zjut.androiddeveloper_ailaiziciqi.Calendar;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.nlf.calendar.Lunar;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.mix.MixActivity;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.search.SearchActivity;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.voice.VoiceAssistant;
import edu.zjut.androiddeveloper_ailaiziciqi.baiduvoice.control.MySyntherizer;
import edu.zjut.androiddeveloper_ailaiziciqi.layouttools.FriendlyScrollView;

public class OldmanActivity extends AppCompatActivity {

    private TextView mYear;
    private TextView mMonth;
    private TextView mDate;
    private TextView mLunarDate;
    private TextView mDayOfWeek;
    private TextView mTodayGood;
    private TextView mTodayBad;
    private Lunar lunarToday;
    private ImageView settingsBtn;
    private List<String> todayGood;
    private List<String> todayBad;
    private Button voice_all_details;
    private ImageView iv_search;
    private ImageView iv_add;
    private TextView poem_left;
    private TextView poem_right;

    private ViewFlipper viewFlipper;
    private float oldTouchValue = 0;
    private MyGestureListener mgListener;
    private GestureDetector mDetector;
    private final static int MIN_MOVE = 200;   //最小距离

    private LocalDate date;
    //    private View flipperView;
    protected Handler mainHandler;

    private VoiceAssistant voiceAssistant;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        voiceAssistant = new VoiceAssistant(this, mainHandler);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oldman_new);

        // 设置状态栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); // 设置状态栏颜色

        iv_add = findViewById(R.id.iv_add);
        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voiceAssistant.release();
                Intent intent = new Intent(OldmanActivity.this, OldmanAddActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        iv_search = findViewById(R.id.iv_search);
        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent searchIntent = new Intent(OldmanActivity.this, SearchActivity.class);
                startActivity(searchIntent);
            }
        });



        // 百度语音
        mainHandler = new Handler() {
            /*
             * @param msg
             */
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }

        };
        voiceAssistant = new VoiceAssistant(this, mainHandler);

        voice_all_details = findViewById(R.id.voice_all_details);
        voice_all_details.setOnClickListener(new OnClickEventAll());

        mgListener = new MyGestureListener();
        mDetector = new GestureDetector(this, mgListener);
        viewFlipper = findViewById(R.id.viewflipper);
        //动态导入添加子View
//        for(int i = 0;i < resId.length;i++){
//            vflp_help.addView(getImageView(resId[i]));
//        }

        // 设置今天
        lunarToday = Lunar.fromDate(new Date());


        date = LocalDate.now();
        makeFillpper(date);

    }


    //重写onTouchEvent触发MyGestureListener里的方法
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mDetector.onTouchEvent(event);
    }

    //自定义一个GestureListener,这个是View类下的
    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float v, float v1) {
            if (e1.getX() - e2.getX() > MIN_MOVE) {
                date = date.plusDays(1);
                Log.w("date", date.toString());
                makeFillpper(date);
                Log.w("date+1", date.toString());

                viewFlipper.setInAnimation(OldmanActivity.this, R.anim.right_in);
                viewFlipper.setOutAnimation(OldmanActivity.this, R.anim.left_out);

                viewFlipper.showNext();
                viewFlipper.removeAllViews();
                makeFillpper(date);
            } else if (e2.getX() - e1.getX() > MIN_MOVE) {
                date = date.minusDays(1);
//                makeFillpper(date);
//                Log.w("date+1",date.toString());

                viewFlipper.setInAnimation(OldmanActivity.this, R.anim.left_in);
                viewFlipper.setOutAnimation(OldmanActivity.this, R.anim.right_out);

                viewFlipper.showPrevious();
                viewFlipper.removeAllViews();
                makeFillpper(date);

            }
            return true;
        }
    }

    private void makeFillpper(LocalDate date) {
        LayoutInflater mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View flipperView = (View) mInflater.inflate(R.layout.view_flipper_layout, null);

        mYear = findViewById(R.id.old_man_year);
        mMonth = flipperView.findViewById(R.id.old_man_month);
        mDate = flipperView.findViewById(R.id.old_man_date);
        mLunarDate = flipperView.findViewById(R.id.old_man_lunar_date);
        mDayOfWeek = flipperView.findViewById(R.id.old_man_day_of_week);
        mTodayGood = flipperView.findViewById(R.id.old_man_good);
        mTodayBad = flipperView.findViewById(R.id.old_man_bad);
        poem_left = flipperView.findViewById(R.id.poem_left);
        poem_right = flipperView.findViewById(R.id.poem_right);

        OnClickEvent onClickEvent = new OnClickEvent();

        mYear.setOnClickListener(onClickEvent);
        mMonth.setOnClickListener(onClickEvent);
        mDate.setOnClickListener(onClickEvent);
        mLunarDate.setOnClickListener(onClickEvent);
        mDayOfWeek.setOnClickListener(onClickEvent);
        mTodayGood.setOnClickListener(onClickEvent);
        mTodayBad.setOnClickListener(onClickEvent);
        poem_left.setOnClickListener(onClickEvent);
        poem_right.setOnClickListener(onClickEvent);

        makeLunar(date);

        FriendlyScrollView f = flipperView.findViewById(R.id.card_scroll);
        f.setGestureDetector(mDetector);

        viewFlipper.addView(flipperView);
    }

    private void makeLunar(LocalDate date) {
        Date date_ = Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant());
        lunarToday = Lunar.fromDate(date_);
        Log.w("lunarToday", lunarToday.toString());
        // 设置今天的宜忌
        todayGood = lunarToday.getDayYi();
        todayBad = lunarToday.getDayJi();

        // 转成String类型
        String todayGoodStr = "";
        String todayBadStr = "";
        for (String s : todayGood) {
            todayGoodStr += s;
            todayGoodStr += ",";
        }
        for (String s : todayBad) {
            todayBadStr += s;
            todayBadStr += ",";
        }
        todayGoodStr = todayGoodStr.substring(0, todayGoodStr.length() - 1);
        todayBadStr = todayBadStr.substring(0, todayBadStr.length() - 1);
        // 获取本日的信息

        String year = String.valueOf(date.getYear()) + "年";
        String month = String.valueOf(date.getMonthValue()) + "月";
        String dated = String.valueOf(date.getDayOfMonth());
        String dateLunar = lunarToday.getMonthInChinese() + "月" + lunarToday.getDayInChinese();
        String dayOfWeek = "星期" + lunarToday.getWeekInChinese();
        // 设置本日的黄历

        mYear.setText(year);
        mMonth.setText(month);
        mDate.setText(dated);
        mLunarDate.setText(dateLunar);
        mDayOfWeek.setText(dayOfWeek);
        mTodayGood.setText(todayGoodStr);
        mTodayBad.setText(todayBadStr);

        // 监听设置按钮
        settingsBtn = findViewById(R.id.btn_settings);
        settingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OldmanActivity.this,SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    private class OnClickEvent implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.old_man_year:
                    voiceAssistant.speak(mYear.getText().toString());
                    break;
                case R.id.old_man_month:
                    voiceAssistant.speak(mMonth.getText().toString());
                    break;
                case R.id.old_man_date:
                    voiceAssistant.speak(mDate.getText().toString() + "日");
                    break;
                case R.id.old_man_lunar_date:
                    voiceAssistant.speak("农历" + mLunarDate.getText().toString());
                    break;
                case R.id.old_man_day_of_week:
                    voiceAssistant.speak(mDayOfWeek.getText().toString());
                    break;
                case R.id.old_man_good:
                    voiceAssistant.speak("宜" + mTodayGood.getText().toString());
                    break;
                case R.id.old_man_bad:
                    voiceAssistant.speak("忌" + mTodayBad.getText().toString());
                    break;
                case R.id.poem_left:
                    voiceAssistant.speak(poem_left.getText().toString());
                    break;
                case R.id.poem_right:
                    voiceAssistant.speak(poem_right.getText().toString());
                    break;
            }
        }
    }

    private class OnClickEventAll implements View.OnClickListener {
        @Override
        public void onClick(View view) {

            voiceAssistant.speak("今天是" + mYear.getText().toString());

            voiceAssistant.speak(mMonth.getText().toString());

            voiceAssistant.speak(mDate.getText().toString() + "日");

            voiceAssistant.speak("农历" + mLunarDate.getText().toString());

            voiceAssistant.speak(mDayOfWeek.getText().toString());

            voiceAssistant.speak("宜" + mTodayGood.getText().toString());

            voiceAssistant.speak("忌" + mTodayBad.getText().toString());

            voiceAssistant.speak("今日诗句："+poem_left.getText().toString());
            voiceAssistant.speak(poem_right.getText().toString());

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 解除绑定
        voiceAssistant.release();
    }
}
