package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.CalendarImpl.mix;

import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DB.PreferencesHelper.OPTION_ACTIVATED;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DB.PreferencesHelper.OPTION_DEACTIVATED;
import static edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DB.PreferencesHelper.SHARED_PREFERENCE_NAME;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DB.PreferencesHelper;
import edu.zjut.androiddeveloper_ailaiziciqi.Calendar.R;

/**
 * 设置控制类
 */
public class SettingsActivity extends AppCompatActivity {

    private ImageView mBack;    // 返回按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        // 设置状态栏颜色
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        // 设置返回按钮
        mBack = findViewById(R.id.back);
        // 监听返回按钮
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, MixActivity.class);
                startActivity(intent);
                finish();
            }
        });

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    // 用户按下返回键结束当前Activity
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(SettingsActivity.this, MixActivity.class);
            startActivity(intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            ListPreference defaultView = findPreference("default_view");    // 启动样式
            SwitchPreference onlyWeekView = findPreference("only_week_view_mode");  // 仅周视图
            ListPreference weekBegin = findPreference("week_begin");    // 一周的开始
            SwitchPreference oldManMode = findPreference("old_man_mode");  // 老年模式
            EditTextPreference oldManName = findPreference("old_man_name"); // 老人的姓名
//            SwitchPreference voiceOver = findPreference("voice_over");  // 语音播报
            Preference aboutUs = findPreference("about_us");    // 关于我们
            Preference contactUs = findPreference("contact_us");    // 联系我们
            Preference appVersion = findPreference("app_version");    // 软件版本

            // 启动样式
            if (defaultView != null) {
                defaultView.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {

                        Log.i("Shared Preferences", "更改了启动样式,准备更改成为" + newValue);
                        PreferencesHelper helper = new PreferencesHelper(requireContext(), SHARED_PREFERENCE_NAME);
                        String before = helper.getString("default_view");
                        Log.i("Shared Preferences", "更改了启动样式,更改前的值为" + before);
                        if (newValue.equals("month")) {
                            helper.putString("default_view", "month");
                        } else if (newValue.equals("week")) {
                            helper.putString("default_view", "week");
                        } else {
                            Toast.makeText(requireContext(), "设置出错！", Toast.LENGTH_SHORT).show();
                        }
                        Log.i("Shared Preferences", "更改了启动样式,更改后的值为" + helper.getString("default_view"));
                        return true;
                    }
                });
            }

            // 仅周视图
            if (onlyWeekView != null) {
                onlyWeekView.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        PreferencesHelper helper = new PreferencesHelper(requireContext(), SHARED_PREFERENCE_NAME);
                        String before = helper.getString("only_week_view_mode");
                        Log.i("Shared Preferences", "点击了仅周视图模式,更改前的值为" + before);
                        if (before.equals(OPTION_DEACTIVATED)) {
                            helper.putString("only_week_view_mode", OPTION_ACTIVATED);
                            Log.i("Shared Preferences", "开启仅周视图模式,更改后的值为" + helper.getString("only_week_view_mode"));
                        } else if (before.equals(OPTION_ACTIVATED)) {
                            helper.putString("only_week_view_mode", OPTION_DEACTIVATED);
                            Log.i("Shared Preferences", "关闭仅周视图模式,更改后的值为" + helper.getString("only_week_view_mode"));
                        } else {
                            Toast.makeText(requireContext(), "设置出错！", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });
            }

            // 一周的开始
            if (weekBegin != null) {
                weekBegin.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {

                        Log.i("Shared Preferences", "更改了一周的开始,准备更改成为" + newValue);
                        PreferencesHelper helper = new PreferencesHelper(requireContext(), SHARED_PREFERENCE_NAME);
                        String before = helper.getString("week_begin");
                        Log.i("Shared Preferences", "更改了一周的开始,更改前的值为" + before);
                        if (newValue.equals("monday")) {
                            helper.putString("week_begin", "monday");
                        } else if (newValue.equals("sunday")) {
                            helper.putString("week_begin", "sunday");
                        } else if (newValue.equals("saturday")) {
                            helper.putString("week_begin", "saturday");
                        } else {
                            Toast.makeText(requireContext(), "设置出错！", Toast.LENGTH_SHORT).show();
                        }
                        Log.i("Shared Preferences", "更改了一周的开始,更改后的值为" + helper.getString("week_begin"));
                        return true;
                    }
                });
            }

            // 老年模式
            if (oldManMode != null) {
                oldManMode.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        PreferencesHelper helper = new PreferencesHelper(requireContext(), SHARED_PREFERENCE_NAME);
                        String before = helper.getString("old_man_mode");
                        Log.i("Shared Preferences", "点击了老年模式,更改前的值为" + before);
                        if (before.equals(OPTION_DEACTIVATED)) {
                            helper.putString("old_man_mode", OPTION_ACTIVATED);
                            Log.i("Shared Preferences", "开启老年模式,更改后的值为" + helper.getString("old_man_mode"));
                        }
                        else if (before.equals(OPTION_ACTIVATED)) {
                            helper.putString("old_man_mode", OPTION_DEACTIVATED);
                            Log.i("Shared Preferences", "关闭老年模式,更改后的值为" + helper.getString("old_man_mode"));
                        } else {
                            Toast.makeText(requireContext(), "设置出错！", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                });
            }

            // 老人姓名
            if (oldManName != null) {

                // 设置标题名称
                PreferencesHelper helper = new PreferencesHelper(requireContext(), SHARED_PREFERENCE_NAME);
                String before = helper.getString("old_man_name");
                // 之前没有输入名字的记录,那么就需要对title和summary也进行更改
                if (!before.equals("DEFAULT") && !before.isEmpty()) {
                    oldManName.setDialogTitle("重设您的姓名");
                    oldManName.setSummary(before);
                    oldManName.setDefaultValue(before);
                    oldManName.setText(before);
                } else {
                    oldManName.setDialogTitle("输入您的姓名");
                    oldManName.setSummary("若输入名字,语音播报时您的名字也会被读出");
                    oldManName.setDefaultValue("");
                }

                // 设置监听器
                oldManName.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        PreferencesHelper helper = new PreferencesHelper(requireContext(), SHARED_PREFERENCE_NAME);
                        String before = helper.getString("old_man_name");
                        String now = newValue.toString();
                        Log.i("Shared Preferences", "修改了老人名字,准备修改成为" + now);
                        Log.i("Shared Preferences", "修改了老人名字,更改前的值为" + before);
                        if (before.equals("DEFAULT") || !before.isEmpty()) {
                            oldManName.setDialogTitle("重设您的姓名");
                            oldManName.setSummary(now);
                            oldManName.setDefaultValue(now);
                            oldManName.setText(now);
                            helper.putString("old_man_name", now);
                            Toast.makeText(requireContext(), "设置姓名成功", Toast.LENGTH_SHORT).show();
                        } else {
                            oldManName.setDialogTitle("输入您的姓名");
                            oldManName.setSummary("若输入名字,语音播报时您的名字也会被读出");
                            oldManName.setDefaultValue("");
                        }
                        Log.i("Shared Preferences", "修改了老人名字,更改后的值为" + helper.getString("old_man_name"));
                        return false;
                    }
                });
            }

            // 语音播报
//            if (voiceOver != null) {
//                voiceOver.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
//                    @Override
//                    public boolean onPreferenceClick(Preference preference) {
//                        PreferencesHelper helper = new PreferencesHelper(requireContext(), SHARED_PREFERENCE_NAME);
//                        String before = helper.getString("voice_over");
//                        Log.i("Shared Preferences", "点击了语音播报,更改前的值为" + before);
//                        if (before.equals(OPTION_DEACTIVATED)) {
//                            helper.putString("voice_over", OPTION_ACTIVATED);
//                        } else if (before.equals(OPTION_ACTIVATED)) {
//                            helper.putString("voice_over", OPTION_DEACTIVATED);
//                        } else {
//                            Toast.makeText(requireContext(), "设置出错！", Toast.LENGTH_SHORT).show();
//                        }
//                        Log.i("Shared Preferences", "点击了语音播报,更改后的值为" + helper.getString("voice_over"));
//                        return true;
//                    }
//                });
//            }

            // 软件版本
            if (appVersion != null) {
                appVersion.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://github.com/Archett0/Android-Calendar"));
                        startActivity(intent);
                        return true;
                    }
                });
            }

            // 关于我们
            if (aboutUs != null) {
                aboutUs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("http://www.zjsfgkw.cn/"));
                        startActivity(intent);
                        return true;
                    }
                });
            }

            // 联系我们
            if (contactUs != null) {
                contactUs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("http://police.hangzhou.gov.cn/"));
                        startActivity(intent);
                        return true;
                    }
                });
            }
        }
    }
}