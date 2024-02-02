package dev.karl.wordwander.game;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

import dev.karl.wordwander.R;

public class PolicyActivity extends AppCompatActivity {
    WebView wv;
    TextView accept, reject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));
        //
        wv = findViewById(R.id.wvPolicy);
        accept = findViewById(R.id.tvAcceptPolicy);
        reject = findViewById(R.id.tvRejectPolicy);

        wv.loadUrl("https://sites.google.com/view/word-wander-policy");

        accept.setOnClickListener(v -> finish());
        reject.setOnClickListener(v -> finishAffinity());
    }
}