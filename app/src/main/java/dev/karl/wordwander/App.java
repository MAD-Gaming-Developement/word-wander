package dev.karl.wordwander;

import android.app.Application;

import com.facebook.FacebookSdk;
import com.facebook.LoggingBehavior;
import com.facebook.appevents.AppEventsLogger;

import dev.karl.wordwander.utils.CommonUtil;

public class App extends Application {

    public static App context;

    public void onCreate() {
        super.onCreate();
        context = this;

        FacebookSdk.fullyInitialize();
        AppEventsLogger.activateApp(this);
        FacebookSdk.setIsDebugEnabled(true);
        FacebookSdk.addLoggingBehavior(LoggingBehavior.APP_EVENTS);

    }
}
