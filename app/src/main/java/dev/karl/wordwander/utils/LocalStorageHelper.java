package dev.karl.wordwander.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class LocalStorageHelper {
    public static final String PREFERENCE_NAME = "dev.karl.wordwander";
    private static volatile LocalStorageHelper mInstance;
    Context mContext;
    private SharedPreferences.Editor mEditor = this.mSharedPreferences.edit();
    private SharedPreferences mSharedPreferences = this.mContext.getSharedPreferences(PREFERENCE_NAME, 0);

    private LocalStorageHelper(Context context) {
        this.mContext = context;
    }

    public static LocalStorageHelper getInstance(Context context) {
        if (mInstance == null) {
            synchronized (LocalStorageHelper.class) {
                if (mInstance == null) {
                    mInstance = new LocalStorageHelper(context);
                }
            }
        }
        return mInstance;
    }

    public void putString(String str, String str2) {
        this.mEditor.putString(str, str2);
        this.mEditor.commit();
    }

    public String getString(String str) {
        return this.mSharedPreferences.getString(str, "");
    }

    public void removeString(String str) {
        this.mEditor.remove(str);
        this.mEditor.commit();
    }
}
