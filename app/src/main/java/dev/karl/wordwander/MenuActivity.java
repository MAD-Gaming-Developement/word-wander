package dev.karl.wordwander;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.splashscreen.SplashScreen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import java.util.Objects;

public class MenuActivity extends AppCompatActivity {
    SplashScreen splashScreen;

    TextView play, howTo, exit, policy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.blue));

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
        }, 2000);
        setButtonsAndListeners();

    }
    private void setButtonsAndListeners(){
        play = findViewById(R.id.tvPlayBtn);
        howTo = findViewById(R.id.tvHowToPlayBtn);
        exit = findViewById(R.id.tvExitBtn);
        policy = findViewById(R.id.tvPolicyBtn);

        play.setOnClickListener(view -> {
            Intent i = new Intent(this, MainWordGame.class);
            startActivity(i);
        });
        howTo.setOnClickListener(view -> {
            Intent i = new Intent(this, HowToGame.class);
            startActivity(i);
        });
        exit.setOnClickListener(view -> {
            finishAffinity();
        });
        policy.setOnClickListener(view -> {
//            Intent i = new Intent(this, );
            //put extra url
//            startActivity(i);
        });
    }
}