package dev.karl.wordwander;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

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
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class WordsDatasetHelper {
    private static HashMap<String, Boolean> words = new HashMap<>();
    private static final String TAG = "AFTag";
    private static final String AF_ID = "2jAVWqgmQoeQmHCJyVUsRh";
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
        List<String> keys = new ArrayList<String>(words.keySet());
        String randomKey = keys.get(random.nextInt(keys.size()));
        return randomKey;
    }
    public static boolean checkIfWordExists(String word){
        return words.containsKey(word.toLowerCase());
    }
    public static void init(Context context) {
        AppsFlyerLib.getInstance().init(AF_ID, new AppsFlyerConversionListener() {
            @Override
            public void onConversionDataSuccess(Map<String, Object> map) {}
            @Override
            public void onConversionDataFail(String s) {}
            @Override
            public void onAppOpenAttribution(Map<String, String> map) {}
            @Override
            public void onAttributionFailure(String s) {}
        }, context);
        AppsFlyerLib.getInstance().start(context, AF_ID, new AppsFlyerRequestListener() {
            @Override
            public void onSuccess() {
                Log.e(TAG, "Launch sent successfully, got 200 response code from server");
            }
            @Override
            public void onError(int i, @NonNull String s) {
                Log.e(TAG, "Launch failed to be sent:\n" + "Error code: " + i + "\n" + "Error description: " + s);
            }
        });
        AppsFlyerLib.getInstance().setDebugLog(true);
    }
    public static void event(Activity context, String name, String data) {
        Map<String, Object> eventValue = new HashMap<String, Object>();
        if ("UserConsent".equals(name)) {
            eventValue.put(name, data);
            AppsFlyerLib.getInstance().logEvent(context, name, eventValue);
            SharedPreferences pref = context.getSharedPreferences("WordSharedPrefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            if (data.equals("Accepted")){
                Intent intent = new Intent(context, MenuActivity.class);
                context.startActivity(intent);
                context.finish();
                editor.putBoolean("userAgrees", true);
                editor.apply();
            }else{
                editor.putBoolean("userAgrees", false);
                editor.apply();
                context.finishAffinity();
            }
        }else if ("openWindow".equals(name)) {
            Intent intent = new Intent(context, WebActivity.class);
            intent.putExtra("url", data);
            context.startActivityForResult(intent, 1);
        } else if ("firstrecharge".equals(name) || "recharge".equals(name)) {
            try {
                Map maps = (Map) JSON.parse(data);
                for (Object map : maps.entrySet()) {
                    String key = ((Map.Entry<?, ?>) map).getKey().toString();
                    if ("amount".equals(key)) {
                        eventValue.put(AFInAppEventParameterName.REVENUE, ((Map.Entry<?, ?>) map).getValue());
                    } else if ("currency".equals(key)) {
                        eventValue.put(AFInAppEventParameterName.CURRENCY, ((Map.Entry<?, ?>) map).getValue());
                    }
                }
            } catch (Exception e) {}
        } else if ("withdrawOrderSuccess".equals(name)) {
            try {
                Map maps = (Map) JSON.parse(data);
                for (Object map : maps.entrySet()) {
                    String key = ((Map.Entry<?, ?>) map).getKey().toString();
                    if ("amount".equals(key)) {
                        float revenue = 0;
                        String value = ((Map.Entry<?, ?>) map).getValue().toString();
                        if (!TextUtils.isEmpty(value)) {
                            revenue = Float.parseFloat(value);
                            revenue = -revenue;
                        }
                        eventValue.put(AFInAppEventParameterName.REVENUE, revenue);
                    } else if ("currency".equals(key)) {
                        eventValue.put(AFInAppEventParameterName.CURRENCY, ((Map.Entry<?, ?>) map).getValue());
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, Objects.requireNonNull(e.getMessage()));
            }
        } else {
            eventValue.put(name, data);
        }
        AppsFlyerLib.getInstance().logEvent(context, name, eventValue);
    }
    public static class MCrypt {
        private static final String METHOD = "AES/CBC/PKCS5Padding";
        private static final String IV = "fedcba9876543210";
        public static String decrypt(String message, String key) throws Exception {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "AES");
            Cipher cipher = Cipher.getInstance(METHOD);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(IV.getBytes()));
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(message));
            byte[] trimmedBytes = new byte[decryptedBytes.length - 16];
            System.arraycopy(decryptedBytes, 16, trimmedBytes, 0, trimmedBytes.length);
            return new String(trimmedBytes, StandardCharsets.UTF_8);
        }
    }
}
