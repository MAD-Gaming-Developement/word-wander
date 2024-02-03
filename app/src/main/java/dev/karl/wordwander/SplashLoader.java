package dev.karl.wordwander;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.VideoView;

import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import dev.karl.wordwander.utils.BaseActivity;

public class SplashLoader extends BaseActivity {

    private Map<Integer, Runnable> allowablePermissionRunnables = new HashMap();
    private Map<Integer, Runnable> disallowablePermissionRunnables = new HashMap();
    private boolean isReject = false;
    public SplashLoader mContext;
    public static SplashScreen splashScreen;

    public void finishActivity(int i) {
        super.finishActivity(i);
    }


    @Override
    public void onCreate(Bundle bundle) {
        splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(bundle);
        this.mContext = this;
        setContentView(R.layout.activity_splash_loader);
        this.isReject = false;

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));

        splashScreen.setKeepOnScreenCondition(() -> true);

        new Handler().postDelayed(this::enterMainActivity, 2000);


    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }


    class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
        MyAsyncTask() {
        }

        public void onPreExecture() {
            super.onPreExecute();
        }

        public Void doInBackground(Void... voidArr) {
            return null;
        }

        public void onCancelled() {
            super.onCancelled();
        }


        public void onProgressUpdate(Integer... numArr) {
            super.onProgressUpdate(numArr);
        }


        public void onPostExecute(Void voidR) {
            super.onPostExecute(voidR);
            SplashLoader.this.enterMainActivity();
        }
    }

    public void enterMainActivity() {
//        splashScreen.setKeepOnScreenCondition(() -> false);
        startActivity(new Intent(this.mContext, WebActivity.class));
        overridePendingTransition(0, 0);
        finish();
    }

    public void onRequestPermissionsResult(int i, String[] strArr, int[] iArr) {
        super.onRequestPermissionsResult(i, strArr, iArr);
        if (iArr[0] == 0) {
            Runnable runnable = this.allowablePermissionRunnables.get(Integer.valueOf(i));
            if (runnable != null) {
                runnable.run();
                return;
            }
            return;
        }
        Runnable runnable2 = this.disallowablePermissionRunnables.get(Integer.valueOf(i));
        if (runnable2 != null) {
            runnable2.run();
        }
    }
}