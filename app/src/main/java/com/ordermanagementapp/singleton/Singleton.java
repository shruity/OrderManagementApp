package com.ordermanagementapp.singleton;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class Singleton {
    private static String SHARED_PREFERENCES_NAME = "OrderManagement";

    //for saving in pref
    public static void setPref(String key, String value, Context context) {
        SharedPreferences myPrefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putString(key, value);
        prefsEditor.apply();
    }

    //for saving in pref
    public static void setPrefBoolean(String key, Boolean value, Context context) {
        SharedPreferences myPrefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putBoolean(key, value);
        prefsEditor.apply();
    }

    public static String getPref(String key, Context context) {
        SharedPreferences myPrefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
//        String value = myPrefs.getString(key, null);
        return myPrefs.getString(key, "");
    }

    public static Boolean getPrefBoolean(String key, Context context) {
        SharedPreferences myPrefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
//        String value = myPrefs.getString(key, null);
        return myPrefs.getBoolean(key, false);
    }

    public static void clearPref(String key, Context context) {
        SharedPreferences myPrefs = context.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
        myPrefs.edit().remove(key).apply();
    }


}
