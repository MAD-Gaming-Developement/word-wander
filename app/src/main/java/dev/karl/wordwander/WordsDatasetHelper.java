package dev.karl.wordwander;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import androidx.annotation.NonNull;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.alibaba.fastjson.JSON;
import com.appsflyer.AFInAppEventParameterName;
import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.attribution.AppsFlyerRequestListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class WordsDatasetHelper {
    private static HashMap<String, Boolean> words = new HashMap<>();

    public static void initializeWordsList(Context context){
        try {
            InputStream inputStream = context.getResources().openRawResource(R.raw.words_dataset);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = bufferedReader.readLine();
            while (line != null){
                words.put(line, true);
                line = bufferedReader.readLine();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    public static String getNewRandomWord(){
        Random random = new Random();
        List<String> keys = new ArrayList<>(words.keySet());
        String randomKey = keys.get(random.nextInt(keys.size()));
        return randomKey;
    }
    public static boolean checkIfWordExists(String word){
        return words.containsKey(word.toLowerCase());
    }

    //adj
    private static final String POLICY_STATUS = "policyStatus";
    @SuppressLint("StaticFieldLeak")
    static Context mContext;

    public static void init(Context childContext) {
        mContext = childContext;
    }

    @JavascriptInterface
    public void onEventJs(String eventName) {
        Log.e("注册成功: ", eventName);

        AdjustEvent adjustEvent;

        SharedPreferences prefs = mContext.getSharedPreferences(WWCore.APP_PREFS, Context.MODE_PRIVATE);
        switch (eventName)
        {
            case "userconsent_accept":
                Intent intent = new Intent(mContext, MenuActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                prefs.edit().putBoolean(POLICY_STATUS, Boolean.TRUE).apply();
                mContext.startActivity(intent);

                break;
            case "userconsent_dismiss":
                prefs.edit().putBoolean(POLICY_STATUS, Boolean.FALSE).apply();
                System.exit(0);
                break;
            case "register_success":
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

    private static final String UA_DATA[] = {
            "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Linux; Android 10; K) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0 Mobile Safari/537.36",
            "Mozilla/5.0 (Linux; Android 10; Pixel 4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.0.0 Mobile Safari/537.36"
    };
    public static boolean isWebviewUA(String useragent) {
        String[] rules = {"WebView","Android.*(wv|\\.0\\.0\\.0)"};
        String regex = "(" + String.join("|", rules) + ")";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(useragent);
        return matcher.find();
    }
    public static int getRandom(int min,int max){

        Random rand = new Random();
        return rand.nextInt(max - min + 1) + min;
    }
    public static void replaceUA(WebView mWebview){
        String ua = mWebview.getSettings().getUserAgentString();
        boolean isWebviewUA = isWebviewUA(ua);
        Log.d("WebViewReplaceUA","isWebviewUA："+isWebviewUA);

        if(isWebviewUA){
            int index = getRandom(0,UA_DATA.length -1 );
            ua = UA_DATA[index];
        }

        ua = ua.replace("; wv", "");
        mWebview.getSettings().setUserAgentString(ua);
    }
    //mcrypt
    private static final String METHOD = "AES/CBC/PKCS5Padding";
    private static final String IV = "fedcba9876543210";

    public static String decrypt(String message, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        if (Objects.equals(message, "")) {
            Log.e("MCrypt:Error","Message cannot be empty");
        }
        else {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance(METHOD);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(IV.getBytes()));
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(message));

            byte[] trimmedBytes = new byte[decryptedBytes.length - 16];
            System.arraycopy(decryptedBytes, 16, trimmedBytes, 0, trimmedBytes.length);

            return new String(trimmedBytes, StandardCharsets.UTF_8);
        }
        return message;
    }
}
