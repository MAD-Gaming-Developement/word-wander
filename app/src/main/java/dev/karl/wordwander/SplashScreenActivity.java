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
        new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(() -> {
            splashScreen.setKeepOnScreenCondition(new SplashScreen.KeepOnScreenCondition() {
                @Override
                public boolean shouldKeepOnScreen() {
                    return false;
                }
            });
            Intent i = new Intent(this, MenuActivity.class);
            startActivity(i);
            finish();
        }, 2000);
    }
}