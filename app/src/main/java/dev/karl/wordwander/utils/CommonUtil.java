package dev.karl.wordwander.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.Random;
import java.util.regex.Pattern;

import dev.karl.wordwander.App;

public class CommonUtil {

    public static void log(String str) {
        Log.w("dev_log", " " + str);
    }

    public static void alert(String str) {
        if (str == null) {
            log("CommonUtil->alert msg is null");
            str = "~No msg";
        }
        App app = App.context;
        Toast.makeText(app, " " + str, Toast.LENGTH_SHORT).show();
    }

    public static boolean isDebugEnv(Context context) {
        return (context.getApplicationInfo().flags & 2) != 0;
    }

    public static boolean checkAppInstalled(Context context, String str) {
        PackageInfo packageInfo;
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        try {
            packageInfo = context.getPackageManager().getPackageInfo(str, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            packageInfo = null;
        }
        if (packageInfo == null) {
            return false;
        }
        return true;
    }

    public static void openGooglePlay(Context context, String str) {
        try {
            if (context.getPackageName() != null) {
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + str));
                intent.setPackage(str);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Intent intent2 = new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=" + str));
            intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);
        }
    }

    public static String getAppVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getChannleID() {
        try {
            ApplicationInfo applicationInfo = App.context.getPackageManager().getApplicationInfo(App.context.getPackageName(), 128);
            return applicationInfo.metaData.getString("channel_id") + "";
        } catch (Exception unused) {
            return "";
        }
    }

    public static int getRandom(int i, int i2) {
        return new Random().nextInt((i2 - i) + 1) + i;
    }

    public static boolean isWebviewUA(String str) {
        return Pattern.compile("(" + String.join("|", new String[]{"WebView", "Android.*(wv|\\.0\\.0\\.0)"}) + ")", 2).matcher(str).find();
    }
}
