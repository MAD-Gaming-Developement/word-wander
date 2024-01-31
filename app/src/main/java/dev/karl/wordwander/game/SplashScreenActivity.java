package dev.karl.wordwander.game;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import dev.karl.wordwander.R;
import dev.karl.wordwander.WebActivity;
import dev.karl.wordwander.utils.BaseActivity;
import dev.karl.wordwander.utils.CommonUtil;
import dev.karl.wordwander.utils.FileUtils;
import dev.karl.wordwander.utils.MCryptHelper;

public class SplashScreenActivity extends BaseActivity {
    private static final int SPLASH_TIME_OUT = 2000;
    private static final String APP_ID = "97CC";
    private static final String TAG = "SplashTag";
    private static final String POLICY_STATUS = "policyStatus";
    private static final String APPSTATS = "appStatus";
    private static final String GAME_URL = "gameURL";
    private String apiResponse = "";
    private String appStatus = "";
    private String gameURL = "";
    SplashScreen splashScreen;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        this.mContext = this;
        setContentView(R.layout.activity_splash_screen);
        this.isReject = false;

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.dark_blue));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.dark_blue));

        splashScreen.setKeepOnScreenCondition(() -> true);


    }
    private static final int SD_CARD_REQUEST_CODE = 1123;
    private Map<Integer, Runnable> allowablePermissionRunnables = new HashMap();
    private Map<Integer, Runnable> disallowablePermissionRunnables = new HashMap();
    private boolean isReject = false;
    /* access modifiers changed from: private */
    public SplashScreenActivity mContext;
    private String mRootDir;

    public void finishActivity(int i) {
        super.finishActivity(i);
    }
    //
    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(() -> new MyAsyncTask().execute(new Void[0]), 2000);
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
    }

    private void initStorage() {
        if (FileUtils.storageAvailable()) {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    new MyAsyncTask().execute(new Void[0]);
                }
            }, 2000);
        } else if (this.isReject) {
            showNormalDialog();
        } else {
            showConfirmDialog();
        }
    }

    class MyAsyncTask extends AsyncTask<Void, Integer, Void> {
        MyAsyncTask() {
        }

        public void onPreExecute() {
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
            SplashScreenActivity.this.enterMainActivity();
        }

    }

    /* access modifiers changed from: private */
    public void enterMainActivity() {


        startActivity(new Intent(this.mContext, WebActivity.class));
        overridePendingTransition(0, 0);
        finish();
    }

    private void showConfirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setCancelable(false);
        builder.setMessage("游戏资源需要更新下载,需要开启访问读写权限哦!~");
        builder.setPositiveButton("确定", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.setNegativeButton("关闭", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            SplashScreenActivity welcomeActivity = SplashScreenActivity.this;
            welcomeActivity.back(welcomeActivity.mContext);
        });
        builder.show();
    }

    private void showNormalDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.mContext);
        builder.setCancelable(false);
        builder.setMessage("游戏资源需要更新下载,请开启访问读写权限哦!~");
        builder.setPositiveButton("确定", (dialogInterface, i) -> {
            CommonUtil.toSelfSetting(SplashScreenActivity.this.mContext);
            dialogInterface.dismiss();
        });
        builder.setNegativeButton("关闭", (dialogInterface, i) -> {
            dialogInterface.dismiss();
            SplashScreenActivity welcomeActivity = SplashScreenActivity.this;
            welcomeActivity.back(welcomeActivity.mContext);
        });
        builder.show();
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