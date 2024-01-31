package dev.karl.wordwander.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingHelper {
    public static final String PREFERENCE_NAME = "dev.jo.core97cc";
    private static volatile SettingHelper mInstance;
    Context mContext;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mSharedPreferences;

    private SettingHelper(Context context) {
        this.mContext = context;
    }

    public static SettingHelper getInstance(Context context) {

        if (mInstance == null) {
            synchronized (SettingHelper.class) {
                if (mInstance == null) {
                    mInstance = new SettingHelper(context);
                }
            }
        }
        return mInstance;
    }

    public void putString(String str, String str2) {
        this.mSharedPreferences = this.mContext.getSharedPreferences(PREFERENCE_NAME, 0);
        this.mEditor = this.mSharedPreferences.edit();

        this.mEditor.putString(str, str2);
        this.mEditor.commit();
    }

    public String getString(String str) {
        this.mSharedPreferences = this.mContext.getSharedPreferences(PREFERENCE_NAME, 0);
        return this.mSharedPreferences.getString(str, "");
    }

    public void putInt(String str, int i) {
        this.mSharedPreferences = this.mContext.getSharedPreferences(PREFERENCE_NAME, 0);
        this.mEditor = this.mSharedPreferences.edit();

        this.mEditor.putInt(str, i);
        this.mEditor.commit();
    }

    public int getInt(String str) {
        this.mSharedPreferences = this.mContext.getSharedPreferences(PREFERENCE_NAME, 0);

        return this.mSharedPreferences.getInt(str, 0);
    }

    public int getInt(String str, int i) {
        this.mSharedPreferences = this.mContext.getSharedPreferences(PREFERENCE_NAME, 0);

        return this.mSharedPreferences.getInt(str, i);
    }

    public void putBoolean(String str, boolean z) {
        this.mSharedPreferences = this.mContext.getSharedPreferences(PREFERENCE_NAME, 0);
        this.mEditor = this.mSharedPreferences.edit();

        this.mEditor.putBoolean(str, z);
        this.mEditor.commit();
    }

    public boolean getBoolean(String str) {
        this.mSharedPreferences = this.mContext.getSharedPreferences(PREFERENCE_NAME, 0);

        return this.mSharedPreferences.getBoolean(str, false);
    }

    public boolean getBoolean(String str, boolean z) {
        this.mSharedPreferences = this.mContext.getSharedPreferences(PREFERENCE_NAME, 0);

        return this.mSharedPreferences.getBoolean(str, z);
    }
}