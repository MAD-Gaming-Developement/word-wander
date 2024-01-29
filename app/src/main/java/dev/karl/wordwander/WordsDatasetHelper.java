package dev.karl.wordwander;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;


import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustEvent;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;


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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class WordsDatasetHelper {
    private static final HashMap<String, Boolean> words = new HashMap<>();
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
        return keys.get(random.nextInt(keys.size()));
    }
    public static boolean checkIfWordExists(String word){
        return words.containsKey(word.toLowerCase());
    }
    //

    static final String APP_PREF = "WordSharedPrefs";
    private static final String POLICYSTATUS = "policyStatus";

    private WordsDatasetHelper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
    private static final String TAG = "AdjustHelper";
    private static AdjustEvent event;
    private static final String FIRST_RECHARGE_EVENT_TOKEN = "q6njhb";
    private static final String RECHARGE_EVENT_TOKEN = "4x7st1";
    private static final String REGISTER_EVENT_TOKEN = "z3q6rv";
    private static final String EVENT_TOKEN_PARTNER = "w788qs";
    public static void event(Activity context, String name, String data) {
        Map<String, Object> eventValue = new HashMap<>();

        Toast.makeText(context, "name:" + name + " - " + "data:" + data, Toast.LENGTH_SHORT).show();
        Log.d(TAG, name+":"+data);

        if ("UserConsent".equals(name)) {
            handleUserConsentEvent(context, data);
        } else if ("openWindow".equals(name)) {
            handleOpenWindowEvent(context, data);
        } else if ("firstrecharge".equals(name) || "recharge".equals(name)) {
            handleRechargeEvent(eventValue, data);
        } else if ("withdrawOrderSuccess".equals(name)) {
            handleWithdrawOrderSuccessEvent(eventValue, data);
        } else if ("register".equals(name)){
            //
            event = new AdjustEvent(REGISTER_EVENT_TOKEN);
            Adjust.trackEvent(event);
        } else if ("first_purchase".equals(name)){
            //
            event = new AdjustEvent(FIRST_RECHARGE_EVENT_TOKEN);
            Adjust.trackEvent(event);
        } else if ("purchase".equals(name)){
            //
            event = new AdjustEvent(RECHARGE_EVENT_TOKEN);
            Adjust.trackEvent(event);
        } else {
            eventValue.put(name, data);
//            Toast.makeText(context, "name:" + name + " - " + "data:" + data, Toast.LENGTH_SHORT).show();
//            event = new AdjustEvent("eventStr");
//            Adjust.trackEvent(event);
//            Log.d(TAG, name+":"+data);
        }
//        event = new AdjustEvent(REGISTER_EVENT_TOKEN);
//        Adjust.trackEvent(event);
    }

    private static void handleUserConsentEvent(Activity context, String data) {
        SharedPreferences appPreferences = context.getSharedPreferences(APP_PREF, Context.MODE_PRIVATE);
//        event = new AdjustEvent(REGISTER_EVENT_TOKEN);
//        Adjust.trackEvent(event);
        if ("Accepted".equals(data)) {
            appPreferences.edit().putBoolean(POLICYSTATUS, true).apply();
//            handleApiRequest(context);
            Intent intent = new Intent(context, MenuActivity.class);
            context.startActivity(intent);
            context.finish();
        } else {
            appPreferences.edit().putBoolean(POLICYSTATUS, false).apply();
            context.finishAffinity();
        }
    }

    private static void handleOpenWindowEvent(Activity context, String data) {
        Intent intent = new Intent(context, PolicyWebAct.class);
        intent.putExtra("url", data);
        intent.putExtra("from", "Ext");
//        event = new AdjustEvent("eventStr");
//        Adjust.trackEvent(event);
        context.startActivityForResult(intent, 1);
    }

    private static void handleRechargeEvent(Map<String, Object> eventValue, String data) {
        Map<String, Object> maps = parseDataMap(data);
//        event = new AdjustEvent("eventStr");
//        Adjust.trackEvent(event);
        //eventValue.put(AFInAppEventParameterName.REVENUE, maps.get("amount"));
        //eventValue.put(AFInAppEventParameterName.CURRENCY, maps.get("currency"));
    }

    private static void handleWithdrawOrderSuccessEvent(Map<String, Object> eventValue, String data) {
        Map<String, Object> maps = parseDataMap(data);
        String amount = String.valueOf(maps.get("amount"));
        if (!TextUtils.isEmpty(amount)) {
            float revenue = -Float.parseFloat(amount);
//            event = new AdjustEvent("eventStr");
//            Adjust.trackEvent(event);
            //eventValue.put(AFInAppEventParameterName.REVENUE, revenue);
        }
        //eventValue.put(AFInAppEventParameterName.CURRENCY, maps.get("currency"));
    }

    private static Map<String, Object> parseDataMap(String data) {
        try {
            return JSON.parseObject(data, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception e) {
            Log.e(TAG, "Error parsing data map: " + e.getMessage());
            return Collections.emptyMap();
        }
    }

    //

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
