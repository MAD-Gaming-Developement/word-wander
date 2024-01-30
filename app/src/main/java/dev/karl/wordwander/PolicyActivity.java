package dev.karl.wordwander;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabsServiceConnection;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;


import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.adjust.sdk.webbridge.AdjustBridge;

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

    private WebView webViewPopUp;
    private WebView webView;
    private Context mContext;
    private AlertDialog alertBuilder;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_launch_web);

        mContext = this;

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
//
//        CustomTabsClient.bindCustomTabsService(this, "com.android.chrome", serviceConnection);
//
//        // Load the webpage in Chrome Custom Tabs
//        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
//        customTabsIntent = builder.build();
//        customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//        builder.setShowTitle(false);
//        builder.setUrlBarHidingEnabled(true);

        // Set up a WebViewClient to handle custom tabs
        webView = new WebView(this);
        webView.setWebViewClient(new CustomTabWebViewClient());
//        webView.clearCache(true);
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
        webView.getSettings().setUserAgentString("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36");

        // Replace UA for WebView to allow Google and Facebook Login
//        WordsDatasetHelper.replaceUA(webView);

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

        webView.setWebChromeClient(new CustomChromeViewClient());

        AdjustBridge.registerAndGetInstance(getApplication(), webView);

        // Load your webpage in the WebView
        webView.loadUrl(loadUrl);

        setContentView(webView);
        //endregion
    }

    private class CustomChromeViewClient extends WebChromeClient {
        @SuppressLint("SetJavaScriptEnabled")
        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog,
                                      boolean isUserGesture, Message resultMsg) {
            webViewPopUp = new WebView(mContext);
            webViewPopUp.setVerticalScrollBarEnabled(false);
            webViewPopUp.setHorizontalScrollBarEnabled(false);
            webViewPopUp.setWebChromeClient(new CustomChromeViewClient());
            webViewPopUp.getSettings().setJavaScriptEnabled(true);
            webViewPopUp.getSettings().setSaveFormData(true);
            webViewPopUp.getSettings().setEnableSmoothTransition(true);
            webViewPopUp.getSettings().setUserAgentString("Mozilla/5.0 (Linux; Android 10; Pixel 4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Mobile Safari/537.36");

            //WebViewReplaceUA.replaceUA(webViewPopUp);

            // pop the  webview with alert dialog
            alertBuilder = new AlertDialog.Builder(PolicyActivity.this).create();
            alertBuilder.setTitle("");
            alertBuilder.setView(webViewPopUp);

            alertBuilder.setButton("Close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    webViewPopUp.destroy();
                    dialog.dismiss();
                }
            });

            alertBuilder.show();
            alertBuilder.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE|WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.setAcceptCookie(true);
            cookieManager.setAcceptThirdPartyCookies(webViewPopUp, true);
            cookieManager.setAcceptThirdPartyCookies(webView, true);

            WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
            transport.setWebView(webViewPopUp);
            resultMsg.sendToTarget();

            return true;
        }

        @Override
        public void onCloseWindow(WebView window) {
            Toast.makeText(PolicyActivity.this,"onCloseWindow called",Toast.LENGTH_SHORT).show();
            try {
                webViewPopUp.destroy();
            } catch (Exception e) {
                Log.d("Destroyed with Error ", e.getStackTrace().toString());
            }

            try {
                alertBuilder.dismiss();
            } catch (Exception e) {
                Log.d("Dismissed with Error: ", e.getStackTrace().toString());
            }

        }

        @Override
        public boolean onShowFileChooser(WebView webView,
                                         ValueCallback<Uri[]> filePathCallback,
                                         WebChromeClient.FileChooserParams fileChooserParams) {
            openFileChooseProcess();
            return true;
        }
    }

    //region [ CustomTabsIntent WebView Client ]
    private class CustomTabWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

            Uri url = request.getUrl();
            String urlString = url.toString();

            Log.d(WWCore.APP_TAG, "URL: "+ urlString);
            String host = Uri.parse(urlString).getHost();
            Log.d("Loading URL", String.valueOf(url));

            if (host.equals(loadUrl)) {
                if (webViewPopUp != null) {
                    webViewPopUp.setVisibility(View.GONE);
                    webViewPopUp.removeView(webViewPopUp);
                    webViewPopUp = null;
                }
                return false;
            }

            if (host.contains("m.facebook.com") || host.contains("facebook.co")
                    || host.contains("google.co")
                    || host.contains("www.facebook.com")
                    || host.contains(".google.com")
                    || host.contains(".google")
                    || host.contains("accounts.google.com/signin/oauth/consent")
                    || host.contains("accounts.youtube.com")
                    || host.contains("accounts.google.com")
                    || host.contains("accounts.google.co.in")
                    || host.contains("www.accounts.google.com")
                    || host.contains("oauth.googleusercontent.com")
                    || host.contains("content.googleapis.com")
                    || host.contains("ssl.gstatic.com")
                //     || host.contains("https://accounts.google.com/signin/oauth/consent")
            ) {
                CustomTabsClient.bindCustomTabsService(mContext, "com.android.chrome", serviceConnection);

                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                customTabsIntent = builder.build();
                customTabsIntent.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                builder.setShowTitle(false);
                builder.setUrlBarHidingEnabled(true);

                customTabsIntent.launchUrl(PolicyActivity.this, Uri.parse(urlString));
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch
            // another Activity that handles URLs



            //Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
            //startActivity(intent);
            return true;
        }


        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            Log.d("onReceivedSslError", "onReceivedSslError");
            super.onReceivedSslError(view, handler, error);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (url.startsWith("https://m.facebook.com/v2.7/dialog/oauth")

            ) {
                if (webViewPopUp != null) {
                    webViewPopUp.setVisibility(View.GONE);
                    webViewPopUp.removeView(webViewPopUp);
                    webViewPopUp = null;
                }
                view.loadUrl(loadUrl);
                return;
            }

            super.onPageFinished(view, url);
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

    public void onBackPressed() {
        if (webView!=null){
            if (webView.canGoBack()){
                webView.goBack();
                return;
            }
        }
        super.onBackPressed();
    }
    private static final String POLICY_STATUS = "policyStatus";
    private class JSInterface {
        @JavascriptInterface
        public void onEventJs(String eventName) {
            Log.e("注册成功: ", eventName);
            Toast.makeText(PolicyActivity.this, eventName, Toast.LENGTH_SHORT).show();

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
                default:
                    adjustEvent = new AdjustEvent(eventName);
                    Adjust.trackEvent(adjustEvent);
                    break;
            }
        }

        @JavascriptInterface
        public void onEventJsRecharge(String eventName) {
            Log.e("注册成功: ", eventName);
            Toast.makeText(PolicyActivity.this, "recharge event", Toast.LENGTH_SHORT).show();

            AdjustEvent adjustEvent;
            adjustEvent = new AdjustEvent("4x7st1");
            Adjust.trackEvent(adjustEvent);
        }
        @JavascriptInterface
        public void onEventJsFirstRecharge(String eventName) {
            Log.e("注册成功: ", eventName);
            Toast.makeText(PolicyActivity.this, "first recharge event", Toast.LENGTH_SHORT).show();

            AdjustEvent adjustEvent;
            adjustEvent = new AdjustEvent("q6njhb");
            Adjust.trackEvent(adjustEvent);
        }
    }
}