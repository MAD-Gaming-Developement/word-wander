package dev.karl.wordwander.utils;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import dev.karl.wordwander.R;

public class BaseActivity extends Activity {
    protected static BaseActivity context;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        context = this;
    }


    /* access modifiers changed from: protected */
    public void onStart() {
        super.onStart();
    }

    /* access modifiers changed from: protected */
    public void onResume() {
        super.onResume();
    }

    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (i != 4) {
            return super.onKeyDown(i, keyEvent);
        }
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        return true;
    }

}
