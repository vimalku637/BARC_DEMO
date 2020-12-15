/*
 * Copyright (c)  2020. Indev Consultancy Private Limited,
 * Auther : Vimal Kumar
 * Date : 2020/12/15
 * Modified Date :
 * Modified By :
 */

package com.vrp.barc_demo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

public class SharedPrefHelper {
    private static final String PREF_FILE = "barc";
    private SharedPreferences settings;
    private SharedPreferences.Editor editor;
    private static Map<Context, SharedPrefHelper> instances = new HashMap<Context, SharedPrefHelper>();

    public SharedPrefHelper(Context context) {
        settings = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        editor = settings.edit();
    }

    public static SharedPrefHelper getInstance(Context context) {
        if (!instances.containsKey(context))
            instances.put(context, new SharedPrefHelper(context));
        return instances.get(context);
    }

    public String getString(String key, String defValue) {
        return settings.getString(key, defValue);
    }

    public SharedPrefHelper setString(String key, String value) {
        editor.putString(key, value);
        editor.commit();
        System.out.print(this);
        return this;
    }

    public int getInt(String key, int defValue) {
        return settings.getInt(key, defValue);
    }


    public SharedPrefHelper setInt(String key, int value) {
        editor.putInt(key, value);
        editor.commit();
        return this;
    }

    public void removeAll(Context context) {
        settings = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE);
        editor = settings.edit();
        editor.clear();
        editor.commit();
        editor.apply();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return settings.getBoolean(key, defValue);
    }

    public SharedPrefHelper setBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.commit();
        return this;
    }
}
