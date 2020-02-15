package com.example.crxzy.centertainment.tools;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

public class SPUtils {
    /**
     * 保存在手机里的SP文件名
     */
    private static final String FILE_NAME = "my_sp";

    /**
     * 保存数据
     */
    public static void put(Context context, String key, Object obj) {
        SharedPreferences sp = context.getSharedPreferences (FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit ( );
        if (obj instanceof Boolean) {
            editor.putBoolean (key, (Boolean) obj);
        } else if (obj instanceof Float) {
            editor.putFloat (key, (Float) obj);
        } else if (obj instanceof Integer) {
            editor.putInt (key, (Integer) obj);
        } else if (obj instanceof Long) {
            editor.putLong (key, (Long) obj);
        } else {
            editor.putString (key, (String) obj);
        }
        editor.apply ( );
    }


    /**
     * @param context    the context of current
     * @param key        the key of be used in search SharedPreferences
     * @param defaultObj default return value when not find key-value
     * @return value return a value searched by key
     */
    public static Object get(Context context, String key, Object defaultObj) {
        SharedPreferences sp = context.getSharedPreferences (FILE_NAME, Context.MODE_PRIVATE);
        if (defaultObj.equals (Boolean.class)) {
            return sp.getBoolean (key, (Boolean) defaultObj);
        } else if (defaultObj instanceof Float) {
            return sp.getFloat (key, (Float) defaultObj);
        } else if (defaultObj instanceof Integer) {
            return sp.getInt (key, (Integer) defaultObj);
        } else if (defaultObj instanceof Long) {
            return sp.getLong (key, (Long) defaultObj);
        } else if (defaultObj instanceof String) {
            return sp.getString (key, (String) defaultObj);
        }
        return null;
    }

    /**
     * 删除指定数据
     */
    public static void remove(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences (FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit ( );
        editor.remove (key);
        editor.apply ( );
    }


    /**
     * 返回所有键值对
     */
    public static Map <String, ?> getAll(Context context) {
        SharedPreferences sp = context.getSharedPreferences (FILE_NAME, Context.MODE_PRIVATE);
        Map <String, ?> map = sp.getAll ( );
        return map;
    }

    /**
     * 删除所有数据
     */
    public static void clear(Context context) {
        SharedPreferences sp = context.getSharedPreferences (FILE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit ( );
        editor.clear ( );
        editor.apply ( );
    }

    /**
     * 检查key对应的数据是否存在
     */
    public static boolean contains(Context context, String key) {
        SharedPreferences sp = context.getSharedPreferences (FILE_NAME, Context.MODE_PRIVATE);
        return sp.contains (key);
    }
}