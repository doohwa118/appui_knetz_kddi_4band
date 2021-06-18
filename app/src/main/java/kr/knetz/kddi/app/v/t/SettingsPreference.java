package kr.knetz.kddi.app.v.t;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;


public class SettingsPreference {

    private static final String PREF_NAME = "kr.knetz.kddi.app.pref";
    static Context mContext;

    public final static String INITIALIZE_VALUE = "INITIALIZE_VALUE";

    public final static String OLD_PASSWORD = "OLD_PASSWORD";
    public final static String NEW_PASSWORD = "NEW_PASSWORD";
    public final static String NEW_PASSWORD_CONFIRM = "NEW_PASSWORD_CONFIRM";
    public final static String REMEMBER_PASSWORD = "REMEMBER_PASSWORD";
    public final static String REMEMBER_PASSWORD_FLAG = "REMEMBER_PASSWORD_FLAG";
    public final static String EMAIL = "EMAIL";

    public SettingsPreference(Context c) {
        mContext = c;
    }

    public static void put(String key, String value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putString(key, value);
        editor.commit();
    }

    public static void put(String key, boolean value) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean(key, value);
        editor.commit();
    }

    public static String getValue(String key, String dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);

        try {
            return pref.getString(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
    }

    public static String getValue(String key) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);

        try {
            return pref.getString(key, "");
        } catch (Exception e) {
            return "";
        }
    }

    public static   boolean getValue(String key, boolean dftValue) {
        SharedPreferences pref = mContext.getSharedPreferences(PREF_NAME, Activity.MODE_PRIVATE);

        try {
            return pref.getBoolean(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
    }
}
