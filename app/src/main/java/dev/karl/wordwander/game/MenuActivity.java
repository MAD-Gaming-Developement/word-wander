package dev.karl.wordwander.game;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;

import android.widget.TextView;

import dev.karl.wordwander.R;


public class MenuActivity extends AppCompatActivity {
    TextView play, howTo, exit, policy;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.blue));

        setButtonsAndListeners();
        WordsDatasetHelper.initializeWordsList(this);
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
            Intent intent = new Intent(this, PolicyActivity.class);
            startActivity(intent);
        });
    }
}