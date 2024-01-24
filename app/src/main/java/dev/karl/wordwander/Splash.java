package dev.karl.wordwander;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import java.util.Objects;

public class Splash extends AppCompatActivity {
    SplashScreen splashScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.white));

        splashScreen.setKeepOnScreenCondition(new SplashScreen.KeepOnScreenCondition() {
            @Override
            public boolean shouldKeepOnScreen() {
                return true;
            }
        });

        new Handler(Objects.requireNonNull(Looper.myLooper())).postDelayed(() -> {
            //
            splashScreen.setKeepOnScreenCondition(new SplashScreen.KeepOnScreenCondition() {
                @Override
                public boolean shouldKeepOnScreen() {
                    return false;
                }
            });
            Intent i = new Intent(this, MenuActivity.class);
            //put extra url
            startActivity(i);
            finish();
        }, 2000);
    }
}