package dev.karl.wordwander;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabsServiceConnection;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;


import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

public class PolicyActivity extends AppCompatActivity {
    String TAG = "WebActivityLogcat";

    private String loadUrl = "";
    private Uri webpageUri;
    private final  int REQUEST_CODE_FILE_CHOOSER = 888;
    private CustomTabsIntent customTabsIntent;
    private CustomTabsClient customTabsClient;
    private CustomTabsServiceConnection serviceConnection;


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_launch_web);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));

        //region [ Get 'url' from calling parent activity ]
        loadUrl = getIntent().getStringExtra("url");
        if (TextUtils.isEmpty(loadUrl)) {
            finish();
        }

        if(loadUrl.contains("file:"))
        {
            File htmlFile = null;
            try {
                InputStream inputStream = getAssets().open("userconsent.html");
                File cacheDir = getCacheDir();
                htmlFile = new File(cacheDir, "userconsent.html");

                OutputStream outputStream = Files.newOutputStream(htmlFile.toPath());
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                inputStream.close();
                outputStream.close();

                // Now you have copied the file to the cache directory
            } catch (IOException e) {
                e.printStackTrace();
            }

            assert htmlFile != null;
            webpageUri = FileProvider.getUriForFile(this, "dev.karl.wordwander.provider", htmlFile);
        }


        //endregion

        //region [ Setup Custom WebView using CustomTabsIntent ]
        // Initialize Chrome Custom Tabs

        serviceConnection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient client) {
                customTabsClient = client;
                Log.d(TAG, "CustomTabsService Connected.");
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                customTabsClient = null;
                Log.d(TAG, "CustomTabsService Disconnected.");
            }
        };

        CustomTabsClient.bindCustomTabsService(this, "com.android.chrome", serviceConnection);

        // Load the webpage in Chrome Custom Tabs
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        customTabsIntent = builder.build();
        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        builder.setShowTitle(false);
        builder.setUrlBarHidingEnabled(true);

        // Set up a WebViewClient to handle custom tabs
        WebView webView = new WebView(this);
        webView.setWebViewClient(new CustomTabWebViewClient());
        webView.clearCache(true);
        // Setup WebView Settings to handle functionality
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setUseWideViewPort(false);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        webView.getSettings().setSupportZoom(false);
        //webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");

        // Replace UA for WebView to allow Google and Facebook Login
        WordsDatasetHelper.replaceUA(webView);

        // Dock the Helper needed for the WebView if Adjust or AppsFlyer
        webView.addJavascriptInterface(new JSInterface(), "android");

        // Attach a download handler for the WebView
        webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.parse(url);
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        // Attach a FileChooser for WebChrome
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, android.os.Message resultMsg) {
                // Handle new window requests here
                WebView newWebView = new WebView(PolicyActivity.this);
                newWebView.setWebViewClient(new CustomTabWebViewClient());
                newWebView.getSettings().setJavaScriptEnabled(true);
                //newWebView.setLayoutParams(new WebView.LayoutParams(WebView.LayoutParams.MATCH_PARENT, WebView.LayoutParams.MATCH_PARENT));

                // Create a WebView container and add the new WebView to it
                WebViewContainer webViewContainer = new WebViewContainer(PolicyActivity.this);
                webViewContainer.addView(newWebView);

                // Set the WebView container as the result
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(newWebView);
                resultMsg.sendToTarget();

                return true;
            }

            @Override
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             WebChromeClient.FileChooserParams fileChooserParams) {
                openFileChooseProcess();
                return true;
            }
        });

        // Attach the CustomTabsIntent to WebView Client


        // Load your webpage in the WebView
        webView.loadUrl(loadUrl);

        setContentView(webView);
        //endregion
    }

    private class WebViewContainer extends androidx.appcompat.widget.LinearLayoutCompat {
        public WebViewContainer(android.content.Context context) {
            super(context);
            setOrientation(VERTICAL);
        }
    }

    //region [ CustomTabsIntent WebView Client ]
    private class CustomTabWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            Uri url = request.getUrl();
            String urlString = url.toString();

            Log.d(WWCore.APP_TAG, "URL: "+ urlString);
            if(urlString.contains("accounts.google.com") || urlString.contains("mzdt") || urlString.contains("facebook") || urlString.contains("instagram") || urlString.contains("tiktok") || urlString.contains("/t.me/"))
            {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                customTabsIntent = builder.build();
                customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                builder.setShowTitle(false);
                builder.setUrlBarHidingEnabled(true);

                customTabsIntent.launchUrl(PolicyActivity.this, Uri.parse(urlString));
                return true;
            }

            return false;
        }
    }
    //endregion

    //region [ WebView Client File Chooser ]
    private void openFileChooseProcess() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Select Picture"), REQUEST_CODE_FILE_CHOOSER);
    }
    //endregion

    //region [ Unbind ServiceConnection of CustomTabsIntent ]
    @Override
    protected void onStop() {
        super.onStop();

        if (customTabsClient != null) {
            unbindService(serviceConnection);
            customTabsClient = null;
        }
    }
    //endregion

    private static final String POLICY_STATUS = "policyStatus";
    private class JSInterface {
        @JavascriptInterface
        public void onEventJs(String eventName) {
            Log.e("注册成功: ", eventName);

            AdjustEvent adjustEvent;

            SharedPreferences prefs = getSharedPreferences(WWCore.APP_PREFS, Context.MODE_PRIVATE);
            switch (eventName)
            {
                case "userconsent_accept":
                    Intent intent = new Intent(PolicyActivity.this, MenuActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    prefs.edit().putBoolean(POLICY_STATUS, Boolean.TRUE).apply();
                    startActivity(intent);

                    break;
                case "userconsent_dismiss":
                    prefs.edit().putBoolean(POLICY_STATUS, Boolean.FALSE).apply();
                    finishAffinity();
                    break;
                case "register_success":
                case "register":
                    adjustEvent = new AdjustEvent("z3q6rv");
                    Adjust.trackEvent(adjustEvent);
                    break;
                case "purchase":
                    adjustEvent = new AdjustEvent("4x7st1");
                    Adjust.trackEvent(adjustEvent);
                    break;
                case "first_purchase":
                    adjustEvent = new AdjustEvent("q6njhb");
                    Adjust.trackEvent(adjustEvent);
                    break;
                default:
                    adjustEvent = new AdjustEvent(eventName);
                    Adjust.trackEvent(adjustEvent);
                    break;
            }
        }

        @JavascriptInterface
        public void onEventJsRecharge(String eventName) {
            Log.e("注册成功: ", eventName);

            AdjustEvent adjustEvent;
            adjustEvent = new AdjustEvent("4x7st1");
            Adjust.trackEvent(adjustEvent);
        }
        @JavascriptInterface
        public void onEventJsFirstRecharge(String eventName) {
            Log.e("注册成功: ", eventName);

            AdjustEvent adjustEvent;
            adjustEvent = new AdjustEvent("q6njhb");
            Adjust.trackEvent(adjustEvent);
        }
    }
}