package dev.karl.wordwander;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.adjust.sdk.webbridge.AdjustBridge;

import org.greenrobot.eventbus.EventBus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

public class PolicyWebAct extends AppCompatActivity {
    private static final String TAG = "AdjustHelper";
    private WebView webView;
    String loadUrl = "";
    private ValueCallback<Uri> mUploadCallBack;
    private ValueCallback<Uri[]> mUploadCallBackAboveL;
    private final  int REQUEST_CODE_FILE_CHOOSER = 888;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_policy_web);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.black));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.black));

        loadUrl = getIntent().getStringExtra("url");

        if (TextUtils.isEmpty(loadUrl)) {
            finish();
        }
        if (getIntent().getStringExtra("from").equals("Web")){
            webView = findViewById(R.id.policyWeb);
            setSetting();
            webView.setWebViewClient(new WebViewClient() {

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    if (TextUtils.equals(request.getUrl().toString(), loadUrl)) {
                        view.post(() -> finish());
                    }
                }

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    String wgPackage = "javascript:window.WgPackage = {name:'" + getPackageName() + "', version:'"
                            + getAppVersionName(PolicyWebAct.this) + "'}";
                    webView.evaluateJavascript(wgPackage, value -> {

                    });
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);
                    String wgPackage = "javascript:window.WgPackage = {name:'" + getPackageName() + "', version:'"
                            + getAppVersionName(PolicyWebAct.this) + "'}";
                    webView.evaluateJavascript(wgPackage, value -> {

                    });
                }
            });
            webView.addJavascriptInterface(new JsInterface(), "jsBridge");

            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

            webView.loadUrl(loadUrl);
            AdjustBridge.registerAndGetInstance(getApplication(), webView);
        } else if (getIntent().getStringExtra("from").equals("Policy")) {
            setupWebView();
        } else {

        }

    }

    public String getAppVersionName(Context context) {
        String appVersionName = "";
        try {
            PackageInfo packageInfo = context.getApplicationContext().getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            appVersionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
        return appVersionName;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setSetting() {
        WebSettings setting = webView.getSettings();
        setting.setJavaScriptEnabled(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        setting.setSupportMultipleWindows(true);
        setting.setDomStorageEnabled(true);
        setting.setCacheMode(WebSettings.LOAD_DEFAULT);
        setting.setAllowContentAccess(true);
        setting.setDatabaseEnabled(true);
        setting.setGeolocationEnabled(true);
        //setting.setUseWideViewPort(true);
        setting.setLoadsImagesAutomatically(true);

        setting.setUserAgentString(setting.getUserAgentString().replace("; wv", ""));

        setting.setMediaPlaybackRequiresUserGesture(false);
        setting.setSupportZoom(false);
        EventBus.getDefault().post("");
        try {
            Class<?> clazz = setting.getClass();
            Method method = clazz.getMethod("setAllowUniversalAccessFromFileURLs", boolean.class);
            method.invoke(setting, true);
        } catch (IllegalArgumentException | NoSuchMethodException | IllegalAccessException
                 | InvocationTargetException e) {
            e.printStackTrace();
        }
        webView.setDownloadListener((url, userAgent, contentDisposition, mimetype, contentLength) -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            Uri uri = Uri.parse(url);
            intent.setData(uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                PolicyWebAct.this.mUploadCallBackAboveL = filePathCallback;
                openFileChooseProcessforWeb();
                return true;
            }
        });
    }
    private void openFileChooseProcessforWeb() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Select Picture"), REQUEST_CODE_FILE_CHOOSER);
    }
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
    public class JsInterface {
        @JavascriptInterface
        public void pushMessage(String name, String data) {
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(data)) {
                WordsDatasetHelper.event(PolicyWebAct.this, name, data);
                Log.d(TAG, "pushMsg");
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "---------requestCode = "+requestCode+ "      resultCode = "+resultCode);
        if (requestCode == this.REQUEST_CODE_FILE_CHOOSER) {
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (result != null) {
                if (mUploadCallBackAboveL != null) {
                    mUploadCallBackAboveL.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, data));
                    mUploadCallBackAboveL = null;
                    return;
                }
            }
            clearUploadMessageForWeb();
        }else if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                if (webView == null) {
                    return;
                }
                Log.e(TAG, "---------下分成功-----");
                webView.evaluateJavascript("javascript:window.closeGame()", value -> {

                });
            }
        }
    }
    private void clearUploadMessageForWeb() {
        if (mUploadCallBackAboveL != null) {
            mUploadCallBackAboveL.onReceiveValue(null);
            mUploadCallBackAboveL = null;
        }
        if (mUploadCallBack != null) {
            mUploadCallBack.onReceiveValue(null);
            mUploadCallBack = null;
        }
    }

    //policy code
    private static String LOADURL = "file:///android_asset/userconsent.html";

    ActivityResultLauncher<Intent> activityResultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        Intent data = result.getData();
                        int resultCode = result.getResultCode();
                        if (resultCode == RESULT_OK && data != null) {
                            handleFileChooseResult(data);
                        } else {
                            clearUploadMessage();
                        }
                    }
            );

    private void setupWebView() {
        webView = new WebView(this);
        setWebViewSettings();
        setWebViewClients();
        loadWebViewUrl();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void setWebViewSettings() {
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setSupportMultipleWindows(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAllowContentAccess(true);
        settings.setDatabaseEnabled(true);
        settings.setGeolocationEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setUserAgentString(settings.getUserAgentString().replace("; wv", ""));
        settings.setMediaPlaybackRequiresUserGesture(false);
        settings.setSupportZoom(false);
        enableUniversalAccessFromFileURLs(settings);
    }

    private void enableUniversalAccessFromFileURLs(WebSettings settings) {
        try {
            Class<?> clazz = settings.getClass();
            Method method = clazz.getMethod("setAllowUniversalAccessFromFileURLs", boolean.class);
            method.invoke(settings, true);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void setWebViewClients() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                injectWgPackage();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                injectWgPackage();
            }
        });
        webView.addJavascriptInterface(new JsInterface(), "jsBridge");
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                PolicyWebAct.this.mUploadCallBackAboveL = filePathCallback;
                openFileChooseProcess();
                return true;
            }
        });
    }

    private void loadWebViewUrl() {
//        LOADURL = loadUrl;
        webView.loadUrl(LOADURL);
        setContentView(webView);
    }

    private void injectWgPackage() {
        String wgPackage = "javascript:window.WgPackage = {name:'" + getPackageName() + "', version:'"
                + getAppVersionName() + "'}";
        webView.evaluateJavascript(wgPackage, value -> {

        });
    }

    private String getAppVersionName() {
        String appVersionName = "";
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            appVersionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
        return appVersionName;
    }

    private void openFileChooseProcess() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        activityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private void handleFileChooseResult(Intent data) {
        Uri result = data.getData();
        if (result != null && mUploadCallBackAboveL != null) {
            mUploadCallBackAboveL.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(RESULT_OK, data));
            mUploadCallBackAboveL = null;
        } else {
            clearUploadMessage();
        }
    }

    private void clearUploadMessage() {
        if (mUploadCallBackAboveL != null) {
            mUploadCallBackAboveL.onReceiveValue(null);
            mUploadCallBackAboveL = null;
        }
    }

}