package dev.karl.wordwander;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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
    //Adjust
}
