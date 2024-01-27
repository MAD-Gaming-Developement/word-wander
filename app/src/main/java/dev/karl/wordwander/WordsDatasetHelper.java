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
}
