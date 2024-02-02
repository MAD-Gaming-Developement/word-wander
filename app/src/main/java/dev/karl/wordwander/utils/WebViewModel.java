package dev.karl.wordwander.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.facebook.internal.AnalyticsEvents;

import java.net.URISyntaxException;
import java.util.List;

import dev.karl.wordwander.R;

public class WebViewModel {

    public AlertDialog builder;
    public WebLoadStatus callback;
    public boolean isFirstLoading = true;
    private JS2Android jsObj;
    public Activity mContext;
    public WebView mWebview;
    public TextView view_bottom_text;
    public ProgressBar view_progressbar;
    private ViewGroup view_rootview;
    public View view_webview_load_error;
    public String web_url;
    public String webview_ua = "";

    public interface WebLoadStatus {
        void onFinish();

        void progress(int i);
    }

    public WebViewModel(Activity activity, String str, JS2Android jS2Android, WebLoadStatus webLoadStatus) {
        this.callback = webLoadStatus;
        init(activity, str, jS2Android);
    }

    private void init(Activity activity, String str, JS2Android jS2Android) {
        this.mContext = activity;
        this.web_url = str;
        this.jsObj = jS2Android;
        initUI();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initUI() {
        String[] stringArray;
        this.view_rootview = (ViewGroup) LayoutInflater.from(this.mContext).inflate(R.layout.view_webview, (ViewGroup) null);
        this.mWebview = (WebView) this.view_rootview.findViewById(R.id.webview);
        this.jsObj.setmWebview(this.mWebview);
        this.view_progressbar = (ProgressBar) this.view_rootview.findViewById(R.id.progress);
        this.view_progressbar.setProgress(0);
        this.view_bottom_text = (TextView) this.view_rootview.findViewById(R.id.bottom_text);
        this.view_webview_load_error = this.view_rootview.findViewById(R.id.webview_load_error);
        this.view_webview_load_error.setOnClickListener(view -> {
            WebViewModel.this.mWebview.reload();
            WebViewModel.this.view_webview_load_error.setEnabled(false);
            new Handler().postDelayed(() -> {
                if (!WebViewModel.this.view_webview_load_error.isEnabled()) {
                    WebViewModel.this.view_webview_load_error.setVisibility(View.GONE);
                }
            }, 200);
        });
        this.view_webview_load_error.setVisibility(View.GONE);
        this.mWebview.getSettings().setDisplayZoomControls(false);
        this.mWebview.getSettings().setUseWideViewPort(true);
        this.mWebview.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        this.mWebview.getSettings().setLoadWithOverviewMode(true);
        this.mWebview.getSettings().setJavaScriptEnabled(true);
        this.mWebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        this.mWebview.getSettings().setSupportMultipleWindows(true);
        this.mWebview.getSettings().setAllowFileAccess(true);
        this.mWebview.getSettings().setAllowFileAccessFromFileURLs(true);
        this.mWebview.getSettings().setAllowUniversalAccessFromFileURLs(true);
        this.mWebview.getSettings().setAllowContentAccess(true);
        this.mWebview.getSettings().setDomStorageEnabled(true);
        this.mWebview.getSettings().setDatabaseEnabled(true);
        this.mWebview.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        this.mWebview.getSettings().setDefaultTextEncodingName("utf-8");
        this.mWebview.getSettings().setBlockNetworkImage(false);
        this.mWebview.getSettings().setMixedContentMode(2);
        this.mWebview.getSettings().setLoadsImagesAutomatically(true);
        String userAgentString = this.mWebview.getSettings().getUserAgentString();
        boolean isWebviewUA = CommonUtil.isWebviewUA(userAgentString);
        CommonUtil.log("isWebviewUA：" + isWebviewUA);
        if (isWebviewUA && (stringArray = this.mContext.getResources().getStringArray(R.array.userAgent)) != null && stringArray.length > 0) {
            userAgentString = stringArray[CommonUtil.getRandom(0, stringArray.length - 1)];
        }
        String replace = userAgentString.replace("; wv", "");
        this.webview_ua = replace + "; Android_Native;";
        CommonUtil.log("curr_webview_UserAgent : " + this.webview_ua);
        this.mWebview.getSettings().setUserAgentString(this.webview_ua);
        this.mWebview.getSettings().setMixedContentMode(2);
        this.mWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView webView, int i) {
                super.onProgressChanged(webView, i);
                if (WebViewModel.this.callback != null) {
                    WebViewModel.this.callback.progress(i);
                }
                if (i > 70) {
                    if (WebViewModel.this.callback != null) {
                        WebViewModel.this.callback.onFinish();
                    }
                    if (WebViewModel.this.view_progressbar.getVisibility() != View.GONE) {
                        WebViewModel.this.view_progressbar.setVisibility(View.GONE);
                        WebViewModel.this.view_bottom_text.setVisibility(View.GONE);
                        return;
                    }
                    return;
                }
                if (WebViewModel.this.view_progressbar.getVisibility() != View.VISIBLE) {
                    WebViewModel.this.view_progressbar.setVisibility(View.VISIBLE);
                    WebViewModel.this.view_bottom_text.setVisibility(View.VISIBLE);
                }
                WebViewModel.this.view_progressbar.setProgress(i);
            }

            @Override
            public boolean onJsAlert(WebView webView, String str, String str2, JsResult jsResult) {
                return super.onJsAlert(webView, str, str2, jsResult);
            }

            @Override
            public void onReceivedTitle(WebView webView, String str) {
                if (!TextUtils.isEmpty(str)) {
                    CommonUtil.log("web onReceivedTitle : " + str);
                    if (str.toLowerCase().contains("404") || str.toLowerCase().contains("error")) {
                        WebViewModel.this.showErrorPage();
                    }
                }
            }

            @Override
            public boolean onCreateWindow(WebView webView, boolean z, boolean z2, Message message) {
                CommonUtil.log("onCreateWindow new " + z + "   " + webView.getUrl() + "  " + message.getData().toString());
                final WebView webView2 = new WebView(webView.getContext());
                webView2.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
                WebSettings settings = webView2.getSettings();
                settings.setDisplayZoomControls(false);
                settings.setUseWideViewPort(true);
                settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                settings.setLoadWithOverviewMode(true);
                settings.setJavaScriptEnabled(true);
                settings.setAllowFileAccess(true);
                settings.setAllowFileAccessFromFileURLs(true);
                settings.setAllowUniversalAccessFromFileURLs(true);
                settings.setAllowContentAccess(true);
                settings.setDomStorageEnabled(true);
                settings.setDatabaseEnabled(true);
                settings.setCacheMode(WebSettings.LOAD_DEFAULT);
                webView2.setWebChromeClient(new WebChromeClient() {
                    @Override
                    public void onProgressChanged(WebView webView, int i) {
                        super.onProgressChanged(webView, i);
                        if (i > 70) {
                            webView.setVisibility(View.VISIBLE);
                        } else {
                            webView.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCloseWindow(WebView webView) {
                        super.onCloseWindow(webView);
                        CommonUtil.log("onCloseWindow");
                        webView.destroy();
                        if (WebViewModel.this.builder != null) {
                            WebViewModel.this.builder.dismiss();
                            AlertDialog unused = WebViewModel.this.builder = null;
                        }
                    }
                });
                webView2.getSettings().setUserAgentString(WebViewModel.this.webview_ua);
                CookieManager instance = CookieManager.getInstance();
                instance.setAcceptCookie(true);
                instance.setAcceptThirdPartyCookies(webView, true);
                instance.setAcceptThirdPartyCookies(webView2, true);
                webView2.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                        CommonUtil.log("shouldOverrideUrlLoading onCreateWindow " + str);
                        return super.shouldOverrideUrlLoading(webView, str);
                    }

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
                        String lowerCase = webResourceRequest.getUrl().toString().toLowerCase();
                        CommonUtil.log("shouldOverrideUrlLoading onCreateWindow  " + lowerCase);
                        if (lowerCase.contains("http") && (lowerCase.contains("accounts.google.com") || lowerCase.contains("accounts.google.co.in") || lowerCase.contains("www.accounts.google.com"))) {
                            return false;
                        }
                        webView2.destroy();
                        if (WebViewModel.this.builder != null) {
                            WebViewModel.this.builder.dismiss();
                            AlertDialog unused = WebViewModel.this.builder = null;
                        }
                        if (lowerCase.contains("http")) {
                            return WebViewModel.this.checkJump(webResourceRequest);
                        }
                        return WebViewModel.this.checkJump(webResourceRequest);
                    }
                });
                if (WebViewModel.this.builder != null) {
                    WebViewModel.this.builder.dismiss();
                    AlertDialog unused = WebViewModel.this.builder = null;
                }
                WebViewModel webViewModel = WebViewModel.this;
                AlertDialog unused2 = webViewModel.builder = new AlertDialog.Builder(webViewModel.mContext, 5).create();
                WebViewModel.this.builder.setOnDismissListener(dialogInterface -> {
                    CommonUtil.log("AlertDialog onDismiss");
                    webView2.destroy();
                });
                WebViewModel.this.builder.setOnCancelListener(dialogInterface -> CommonUtil.log("AlertDialog onCancel"));
                WebViewModel.this.builder.setTitle("");
                WebViewModel.this.builder.setView(webView2);
                WebViewModel.this.builder.show();
                Window window = WebViewModel.this.builder.getWindow();
                window.clearFlags(131080);
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.gravity = Gravity.CENTER;
                attributes.width = -1;
                attributes.height = -1;
                window.getDecorView().setPadding(20, 20, 20, 20);
                window.setAttributes(attributes);
                webView2.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                ((WebView.WebViewTransport) message.obj).setWebView(webView2);
                message.sendToTarget();
                return true;
            }

            @Override
            public void onCloseWindow(WebView webView) {
                super.onCloseWindow(webView);
                webView.destroy();
            }
        });
        this.mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                return super.shouldOverrideUrlLoading(webView, str);
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
                if (webResourceRequest.getUrl().toString().toLowerCase().contains("http")) {
                    return false;
                }
                return WebViewModel.this.checkJump(webResourceRequest);
            }

            public void onReceivedSslError(WebView webView, SslErrorHandler sslErrorHandler, SslError sslError) {
                sslErrorHandler.proceed();
            }

            @Override
            public void onReceivedError(WebView webView, int i, String str, String str2) {
                super.onReceivedError(webView, i, str, str2);
                if (Build.VERSION.SDK_INT < 23) {
                    if (WebViewModel.this.callback != null) {
                        WebViewModel.this.callback.onFinish();
                    }
                    CommonUtil.log("web onReceivedError_old_version : " + str);
                    if ("net::ERR_INTERNET_DISCONNECTED".equalsIgnoreCase(str)) {
                        WebViewModel.this.showErrorPage();
                    } else if (!"net::ERR_CONNECTION_TIMED_OUT".equalsIgnoreCase(str)) {
                        if ("net::ERR_NAME_NOT_RESOLVED".equalsIgnoreCase(str)) {
                            WebViewModel.this.showErrorPage();
                        } else if ("net::ERR_CONNECTION_CLOSED".equalsIgnoreCase(str)) {
                            WebViewModel.this.showErrorPage();
                        }
                    }
                }
            }

            @Override
            public void onReceivedError(WebView webView, WebResourceRequest webResourceRequest, WebResourceError webResourceError) {
                super.onReceivedError(webView, webResourceRequest, webResourceError);
                if (Build.VERSION.SDK_INT >= 23) {
                    CommonUtil.log("web onReceivedError_new_version : " + webResourceError.getDescription() + "  " + webResourceRequest.getUrl().getHost());
                    String host = webResourceRequest.getUrl().getHost();
                    if (WebViewModel.this.web_url == null || host == null || WebViewModel.this.web_url.indexOf(host) != -1) {
                        if (WebViewModel.this.callback != null) {
                            WebViewModel.this.callback.onFinish();
                        }
                        if (webResourceRequest.isForMainFrame()) {
                            WebViewModel.this.showErrorPage();
                        } else if (webResourceError.getDescription() != null && "net::ERR_INTERNET_DISCONNECTED".equalsIgnoreCase(webResourceError.getDescription().toString())) {
                            WebViewModel.this.showErrorPage();
                        } else if (webResourceError.getDescription() != null && "net::ERR_CONNECTION_TIMED_OUT".equalsIgnoreCase(webResourceError.getDescription().toString())) {
                        } else {
                            if (webResourceError.getDescription() != null && "net::ERR_NAME_NOT_RESOLVED".equalsIgnoreCase(webResourceError.getDescription().toString())) {
                                WebViewModel.this.showErrorPage();
                            } else if (webResourceError.getDescription() != null && "net::ERR_CONNECTION_CLOSED".equalsIgnoreCase(webResourceError.getDescription().toString())) {
                                WebViewModel.this.showErrorPage();
                            }
                        }
                    }
                }
            }

            @Override
            public void onPageStarted(WebView webView, String str, Bitmap bitmap) {
                super.onPageStarted(webView, str, bitmap);
            }

            @Override
            public void onPageFinished(WebView webView, String str) {
                super.onPageFinished(webView, str);
                if (WebViewModel.this.isFirstLoading) {
                    boolean unused = WebViewModel.this.isFirstLoading = false;
                }
                WebViewModel.this.view_bottom_text.setVisibility(View.GONE);
                new Handler().postDelayed(() -> {
                }, 500);
            }
        });
        this.mWebview.addJavascriptInterface(this.jsObj, "android");
        this.mWebview.addJavascriptInterface(this.jsObj, AnalyticsEvents.PARAMETER_SHARE_DIALOG_SHOW_NATIVE);
        WebView webView = this.mWebview;
        webView.loadUrl("" + this.web_url);
    }

    public boolean checkJump(WebResourceRequest webResourceRequest) {
        try {
            Intent parseUri = Intent.parseUri(webResourceRequest.getUrl().toString(), Intent.URI_INTENT_SCHEME);
            if (parseUri.resolveActivity(this.mContext.getPackageManager()) == null) {
                return false;
            }
            parseUri.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            parseUri.setData(webResourceRequest.getUrl());
            this.mContext.startActivity(parseUri);
            return true;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void showErrorPage() {
        View view = this.view_webview_load_error;
        if (view != null) {
            view.setEnabled(true);
            this.view_webview_load_error.setVisibility(View.VISIBLE);
        }
    }

    public void callJS(final String str) {
        WebView webView = this.mWebview;
        if (webView != null) {
            webView.post(() -> {
                if (Build.VERSION.SDK_INT >= 19) {
                    WebView access$000 = WebViewModel.this.mWebview;
                    access$000.evaluateJavascript("javascript:" + str, new ValueCallback<String>() {
                        public void onReceiveValue(String str1) {
                        }
                    });
                    return;
                }
                WebView access$0002 = WebViewModel.this.mWebview;
                access$0002.loadUrl("javascript:" + str);
            });
        }
    }

    public View getContentView() {
        return this.view_rootview;
    }

    public WebView getWebview() {
        return this.mWebview;
    }

    public static class JS2Android {
        private Activity activity;
        private WebView mWebview;

        public Activity getActivity() {
            return this.activity;
        }

        public void setmWebview(WebView webView) {
            this.mWebview = webView;
        }

        public JS2Android(Activity activity2) {
            this.activity = activity2;
        }

        @JavascriptInterface
        public void showSource(String str) {
            CommonUtil.log(str);
        }

        @JavascriptInterface
        public void log(String str) {
            CommonUtil.log("weblog = " + str);
        }

        @JavascriptInterface
        public void vibrator() {
            ((Vibrator) this.activity.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(new long[]{10, 100}, -1);
        }

        @JavascriptInterface
        public void setScreenOrientation(boolean z) {
            if (z) {
                this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            } else {
                this.activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    }
}