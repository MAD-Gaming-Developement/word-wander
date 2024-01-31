package dev.karl.wordwander.utils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import dev.karl.wordwander.App;
import dev.karl.wordwander.R;

public class BaseActivity extends Activity {
    protected static BaseActivity context;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        context = this;
    }

    public void next(Intent intent) {
        context.startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void nextFor_resule(Intent intent) {
        context.startActivityForResult(intent, 0);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    public void back(Activity activity) {
        CommonUtil.hideCurrActivitySoftInput(activity);
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    public static PopupWindow createPopupWindow4panel(View view, boolean z) {
        View inflate = LayoutInflater.from(App.context).inflate(R.layout.popwin_panel, (ViewGroup) null);
        ((ViewGroup) inflate.findViewById(R.id.popwin_panel)).addView(view);
        PopupWindow popupWindow = new PopupWindow(inflate, -1, -1);
        if (z) {
            popupWindow.setBackgroundDrawable(new BitmapDrawable());
            popupWindow.setFocusable(z);
            popupWindow.setTouchable(true);
            popupWindow.setOutsideTouchable(true);
        }
        popupWindow.setAnimationStyle(R.style.popupAnimation_alpha);
        return popupWindow;
    }

    public static PopupWindow createConfirmPopupWindow4panel(View view, boolean z) {
        View inflate = LayoutInflater.from(App.context).inflate(R.layout.popwin_panel, (ViewGroup) null);
        ((ViewGroup) inflate.findViewById(R.id.popwin_panel)).addView(view);
        PopupWindow popupWindow = new PopupWindow(inflate, -1, -1);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(z);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setAnimationStyle(R.style.popupAnimation_dialog);
        return popupWindow;
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

    public void startActivityEmptyIntent(Class cls) {
        startActivity(new Intent(this, cls));
    }
}
