package edu.zjut.androiddeveloper_ailaiziciqi.Calendar.DB;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Objects;

/**
 * 用于读写SharedPreferences的工具类
 */
public class PreferencesHelper {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    public static final String SHARED_PREFERENCE_NAME = "USER_PREFERENCES";
    public static final String NO_STRING_DATA = "no_data";
    public static final boolean NO_BOOLEAN_DATA = false;
    public static final String OPTION_ACTIVATED = "YES";
    public static final String OPTION_DEACTIVATED = "NO";

    /**
     * 构造方法
     * 需要在Application中执行初始化
     *
     * @param context        上下文
     * @param preferenceName 要获取的Preference名称
     */
    public PreferencesHelper(Context context, String preferenceName) {

        preferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.apply();
    }

    /**
     * 安全写入String类型value
     * 先检查是否已经存在这个值,是则不保存,反正保存
     *
     * @param key   键
     * @param value 值
     */
    public void safePutString(String key, String value) {
        if (Objects.equals(preferences.getString(key, NO_STRING_DATA), NO_STRING_DATA)) {
            editor.putString(key, value).apply();
            Log.i("Shared Preferences", "Safe putting string:(" + key + "::" + value + ")");
        } else {
            Log.i("Shared Preferences", "Safe putting string failed: key already exists:(" + key + "::" + preferences.getString(key, NO_STRING_DATA) + ")");
        }
    }

    /**
     * 保存或直接覆写String类型value
     * 不检查是否已经有保存的值,直接进行覆写
     *
     * @param key   键
     * @param value 值
     */
    public void putString(String key, String value) {
        editor.putString(key, value).apply();
        Log.i("Shared Preferences", "Putting string:(" + key + "::" + value + ")");
    }

    /**
     * 通过Key读取对应String值
     *
     * @param key 键
     * @return 存在则返回对应值, 不存在则返回NO_STRING_DATA
     */
    public String getString(String key) {
        Log.i("Shared Preferences", "Getting string:(" + key + "::" + preferences.getString(key, NO_STRING_DATA) + ")");
        return preferences.getString(key, NO_STRING_DATA);
    }

    /**
     * 安全写入Boolean类型value
     * 先检查是否已经存在这个值,是则不保存,反正保存
     *
     * @param key   键
     * @param value 值
     */
    public void safePutBoolean(String key, boolean value) {
        if (Objects.equals(preferences.getBoolean(key, NO_BOOLEAN_DATA), NO_BOOLEAN_DATA)) {
            editor.putBoolean(key, value).apply();
            Log.i("Shared Preferences", "Safe putting boolean:(" + key + "::" + value + ")");
        } else {
            Log.i("Shared Preferences", "Safe putting string failed: key already exists:(" + key + "::" + preferences.getBoolean(key, NO_BOOLEAN_DATA) + ")");
        }
    }

    /**
     * 保存或直接覆写Boolean类型value
     * 不检查是否已经有保存的值,直接进行覆写
     *
     * @param key   键
     * @param value 值
     */
    public void putBoolean(String key, boolean value) {
        editor.putBoolean(key, value).apply();
        Log.i("Shared Preferences", "Putting boolean:(" + key + "::" + value + ")");
    }

    /**
     * 通过Key读取对应Boolean值
     *
     * @param key 键
     * @return 存在则返回对应值, 不存在则返回NO_BOOLEAN_DATA
     */
    public Boolean getBoolean(String key) {
        Log.i("Shared Preferences", "Getting boolean:(" + key + "::" + preferences.getBoolean(key, NO_BOOLEAN_DATA) + ")");
        return preferences.getBoolean(key, NO_BOOLEAN_DATA);
    }
}
