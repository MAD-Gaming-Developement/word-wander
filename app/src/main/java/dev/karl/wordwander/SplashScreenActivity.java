package dev.karl.wordwander;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;

public class SplashScreenActivity extends AppCompatActivity {
    private static final int SPLASH_TIME_OUT = 2000;
    SplashScreen splashScreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.dark_blue));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.dark_blue));

        splashScreen.setKeepOnScreenCondition(() -> true);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                splashScreen = SplashScreen.installSplashScreen(SplashScreenActivity.this);
                Intent i = new Intent(SplashScreenActivity.this, MenuActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}