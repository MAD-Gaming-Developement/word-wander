package dev.karl.wordwander;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.TextView;


public class MenuActivity extends AppCompatActivity {
    TextView play, howTo, exit, policy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.blue));

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
        policy.setVisibility(View.VISIBLE);
        policy.setOnClickListener(view -> {
            Intent intent = new Intent(this, WebActivity.class);
            String gameURL = "file:///android_asset/userconsent.html";
            intent.putExtra("url", gameURL);
            startActivity(intent);
            finish();
        });
    }
}