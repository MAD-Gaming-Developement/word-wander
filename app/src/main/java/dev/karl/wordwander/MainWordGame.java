package dev.karl.wordwander;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import dev.karl.wordwander.databinding.ActivityMainWordGameBinding;

public class MainWordGame extends AppCompatActivity {
    SharedPreferences pref;
    MediaPlayer bg, newTry, reveal, win, lose, mediaPlayer;
    String userWordGuess, masterWord;
    TextView tvQ,tvW,tvE,tvR,tvT,tvY,tvU,tvI,tvO,tvP,tvA,tvS,tvD,tvF,tvG,tvH,tvJ,tvK,tvL,tvZ,tvX,tvC,tvV,tvB,tvN,tvM, tvEnter, tvClear, tvHintText;
    Integer LIGHT_BLUE = R.color.light_blue;
    Integer ORANGE = R.color.orange;
    Integer GRAY = R.color.gray;
    Integer WHITE = R.color.white;
    Integer BLACK = R.color.black;
    boolean isGameOver, isWon, isSoundEnabled, isMusicEnabled;
    ActivityMainWordGameBinding binding;
    GridAdapter gridAdapter;
    ArrayList<LetterTileModel> letterList = new ArrayList<>();
    int gridViewCursor, currentTurnCursor, triesCounter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainWordGameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().setStatusBarColor(ContextCompat.getColor(MainWordGame.this, R.color.dark_blue));
        getWindow().setNavigationBarColor(ContextCompat.getColor(MainWordGame.this, R.color.dark_blue));

        bg = MediaPlayer.create(this, R.raw.bullet_train_fantasy_loop);
        bg.setLooping(true);
        newTry = MediaPlayer.create(this, R.raw.new_try_sound);
        reveal = MediaPlayer.create(this, R.raw.reveal_sound);
        win = MediaPlayer.create(this, R.raw.wins_sound);
        lose = MediaPlayer.create(this, R.raw.lose_sound);

        pref = this.getSharedPreferences("WordSharedPrefs", Context.MODE_PRIVATE);

        tvHintText = findViewById(R.id.tvHintText);

        isMusicEnabled = pref.getBoolean("music", true);
        isSoundEnabled = pref.getBoolean("sound", true);

        initializeTextviews();
        setupEmptyGridView();
        setTextviewClickListeners();

        if (isMusicEnabled){
            bg.seekTo(0);
            bg.start();
        }

        tvEnter = findViewById(R.id.tvEnter);
        tvClear = findViewById(R.id.tvClear);

        tvEnter.setOnClickListener(view -> {

            if(isGameOver){
            }else if (userWordGuess.length() < 5){
                tvHintText.setText("Must have 5 letters");
                playSoundEffect(lose);
            }else if (!WordsDatasetHelper.checkIfWordExists(userWordGuess)){
                tvHintText.setText("\"" + userWordGuess + "\" is not in the word list");
                playSoundEffect(lose);
            }else{
                checkMatch(userWordGuess, masterWord);
                currentTurnCursor = 0;
                userWordGuess = "";
                triesCounter++;
                if (triesCounter == 6 || isWon){
                    //show dialog
                    endGameDialog(isWon);
                    isGameOver = true;
                    tvHintText.setText("Correct answer is: " + masterWord);
                }else{
                    playSoundEffect(reveal);
                }
            }
        });
        tvClear.setOnClickListener(view -> {
            if(gridViewCursor > 0 && currentTurnCursor > 0){
                gridAdapter.setLetter(gridViewCursor-1, "");
                gridViewCursor--;
                userWordGuess = userWordGuess.substring(0,currentTurnCursor-1);
                currentTurnCursor--;
                tvHintText.setText("");
            }
        });
    }

    void setupEmptyGridView(){
        playSoundEffect(newTry);
        for (int q1 = 0 ; q1 < 26 ; q1++){
            setLetterKeyColor("QWERTYUIOPASDFGHJKLZXCVBNM".charAt(q1), R.drawable.ripple_blue);
        }
        masterWord = WordsDatasetHelper.getNewRandomWord().toUpperCase();
        Log.d("masterword:", masterWord);
        gridViewCursor = 0;
        currentTurnCursor = 0;
        userWordGuess = "";
        isGameOver = false;
        isWon = false;
        triesCounter = 0;
        tvHintText.setText("");

        letterList.clear();

        for (int q1 = 0 ; q1 < 30 ; q1++){
            letterList.add(new LetterTileModel("", BLACK, WHITE));
        }

        gridAdapter = new GridAdapter(this, letterList);
        binding.gameGridView.setAdapter(gridAdapter);
        binding.gameGridView.setOnTouchListener((view, motionEvent) -> {
            return motionEvent.getAction() == MotionEvent.ACTION_MOVE;
        });

        binding.gameGridView.setOnItemClickListener((adapterView, view, i, l) -> {
            gameOptions();
        });
    }
    void initializeTextviews(){
        tvQ = findViewById(R.id.tvLetterQ);
        tvW = findViewById(R.id.tvLetterW);
        tvE = findViewById(R.id.tvLetterE);
        tvR = findViewById(R.id.tvLetterR);
        tvT = findViewById(R.id.tvLetterT);
        tvY = findViewById(R.id.tvLetterY);
        tvU = findViewById(R.id.tvLetterU);
        tvI = findViewById(R.id.tvLetterI);
        tvO = findViewById(R.id.tvLetterO);
        tvP = findViewById(R.id.tvLetterP);
        tvA = findViewById(R.id.tvLetterA);
        tvS = findViewById(R.id.tvLetterS);
        tvD = findViewById(R.id.tvLetterD);
        tvF = findViewById(R.id.tvLetterF);
        tvG = findViewById(R.id.tvLetterG);
        tvH = findViewById(R.id.tvLetterH);
        tvJ = findViewById(R.id.tvLetterJ);
        tvK = findViewById(R.id.tvLetterK);
        tvL = findViewById(R.id.tvLetterL);
        tvZ = findViewById(R.id.tvLetterZ);
        tvX = findViewById(R.id.tvLetterX);
        tvC = findViewById(R.id.tvLetterC);
        tvV = findViewById(R.id.tvLetterV);
        tvB = findViewById(R.id.tvLetterB);
        tvN = findViewById(R.id.tvLetterN);
        tvM = findViewById(R.id.tvLetterM);
    }
    void letterKeyClick(String letter){
        if(currentTurnCursor < 5 && !isGameOver){
            gridAdapter.setLetter(gridViewCursor, letter);
            gridViewCursor++;
            currentTurnCursor++;
            userWordGuess = userWordGuess + letter;
            tvHintText.setText("");
        }
    }
    void setTextviewClickListeners(){
        tvQ.setOnClickListener(view -> letterKeyClick("Q"));
        tvW.setOnClickListener(view -> letterKeyClick("W"));
        tvE.setOnClickListener(view -> letterKeyClick("E"));
        tvR.setOnClickListener(view -> letterKeyClick("R"));
        tvT.setOnClickListener(view -> letterKeyClick("T"));
        tvY.setOnClickListener(view -> letterKeyClick("Y"));
        tvU.setOnClickListener(view -> letterKeyClick("U"));
        tvI.setOnClickListener(view -> letterKeyClick("I"));
        tvO.setOnClickListener(view -> letterKeyClick("O"));
        tvP.setOnClickListener(view -> letterKeyClick("P"));
        tvA.setOnClickListener(view -> letterKeyClick("A"));
        tvS.setOnClickListener(view -> letterKeyClick("S"));
        tvD.setOnClickListener(view -> letterKeyClick("D"));
        tvF.setOnClickListener(view -> letterKeyClick("F"));
        tvG.setOnClickListener(view -> letterKeyClick("G"));
        tvH.setOnClickListener(view -> letterKeyClick("H"));
        tvJ.setOnClickListener(view -> letterKeyClick("J"));
        tvK.setOnClickListener(view -> letterKeyClick("K"));
        tvL.setOnClickListener(view -> letterKeyClick("L"));
        tvZ.setOnClickListener(view -> letterKeyClick("Z"));
        tvX.setOnClickListener(view -> letterKeyClick("X"));
        tvC.setOnClickListener(view -> letterKeyClick("C"));
        tvV.setOnClickListener(view -> letterKeyClick("V"));
        tvB.setOnClickListener(view -> letterKeyClick("B"));
        tvN.setOnClickListener(view -> letterKeyClick("N"));
        tvM.setOnClickListener(view -> letterKeyClick("M"));
    }
    void checkMatch(String guess, String master){
        StringBuilder guessStrBldr = new StringBuilder(guess);
        StringBuilder masterStrBldr = new StringBuilder(master);
        if (guess.equals(master)){
            isGameOver = true;
            isWon = true;
        }
        for (int q1 = 0 ; q1 < 5 ; q1++){
            if(guessStrBldr.charAt(q1) == masterStrBldr.charAt(q1)){
                setLetterKeyColor(guessStrBldr.charAt(q1), R.drawable.ripple_orange);
                guessStrBldr.setCharAt(q1, '.');
                masterStrBldr.setCharAt(q1, ',');
                gridAdapter.setTextColorResource(((gridViewCursor-5) + q1), WHITE);
                gridAdapter.setBgColorResource(((gridViewCursor-5) + q1), ORANGE);
            }
        }
        for (int q1 = 0 ; q1 < 5 ; q1++){
            for (int q2 = 0 ; q2 < 5 ; q2++){
                if (guessStrBldr.charAt(q1) == masterStrBldr.charAt(q2)){
                    setLetterKeyColor(guessStrBldr.charAt(q1), R.drawable.ripple_light_blue);
                    guessStrBldr.setCharAt(q1, '.');
                    masterStrBldr.setCharAt(q2, ',');
                    gridAdapter.setTextColorResource(((gridViewCursor-5) + q1), WHITE);
                    gridAdapter.setBgColorResource(((gridViewCursor-5) + q1), LIGHT_BLUE);
                    break;
                }
            }
        }
        for (int q1 = 0 ; q1 < 5 ; q1++){
            if (guessStrBldr.charAt(q1) != '.'){
                setLetterKeyColor(guessStrBldr.charAt(q1), R.drawable.ripple_gray);
                gridAdapter.setTextColorResource(((gridViewCursor-5) + q1), WHITE);
                gridAdapter.setBgColorResource(((gridViewCursor-5) + q1), GRAY);
            }
        }
    }
    void setLetterKeyColor(Character tv, @DrawableRes Integer ripple){
        switch (tv){
            case 'Q':
                tvQ.setBackgroundResource(ripple);
                break;
            case 'W':
                tvW.setBackgroundResource(ripple);
                break;
            case 'E':
                tvE.setBackgroundResource(ripple);
                break;
            case 'R':
                tvR.setBackgroundResource(ripple);
                break;
            case 'T':
                tvT.setBackgroundResource(ripple);
                break;
            case 'Y':
                tvY.setBackgroundResource(ripple);
                break;
            case 'U':
                tvU.setBackgroundResource(ripple);
                break;
            case 'I':
                tvI.setBackgroundResource(ripple);
                break;
            case 'O':
                tvO.setBackgroundResource(ripple);
                break;
            case 'P':
                tvP.setBackgroundResource(ripple);
                break;
            case 'A':
                tvA.setBackgroundResource(ripple);
                break;
            case 'S':
                tvS.setBackgroundResource(ripple);
                break;
            case 'D':
                tvD.setBackgroundResource(ripple);
                break;
            case 'F':
                tvF.setBackgroundResource(ripple);
                break;
            case 'G':
                tvG.setBackgroundResource(ripple);
                break;
            case 'H':
                tvH.setBackgroundResource(ripple);
                break;
            case 'J':
                tvJ.setBackgroundResource(ripple);
                break;
            case 'K':
                tvK.setBackgroundResource(ripple);
                break;
            case 'L':
                tvL.setBackgroundResource(ripple);
                break;
            case 'Z':
                tvZ.setBackgroundResource(ripple);
                break;
            case 'X':
                tvX.setBackgroundResource(ripple);
                break;
            case 'C':
                tvC.setBackgroundResource(ripple);
                break;
            case 'V':
                tvV.setBackgroundResource(ripple);
                break;
            case 'B':
                tvB.setBackgroundResource(ripple);
                break;
            case 'N':
                tvN.setBackgroundResource(ripple);
                break;
            case 'M':
                tvM.setBackgroundResource(ripple);
                break;
        }
    }
    void endGameDialog(boolean hasWon){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.gameover_dialog, null);

        TextView header = v.findViewById(R.id.dialogTvHeader);
        TextView description = v.findViewById(R.id.gameOverDialogTv);
        TextView correctWord = v.findViewById(R.id.tvGameOverDialogCorrectAnswer);
        TextView button = v.findViewById(R.id.tvTryAgainBtn);

        correctWord.setText(masterWord);
        if (hasWon){
            header.setText("You got it!");
            description.setText("You guessed correctly:");
            playSoundEffect(win);
        }else{
            header.setText("Game Over.");
            description.setText("The correct word was:");
            playSoundEffect(lose);
        }

        builder.setView(v);
        builder.setCancelable(true);

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        button.setOnClickListener(view -> {
            setupEmptyGridView();
            alertDialog.dismiss();
        });
    }
    void gameOptions(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = getLayoutInflater().inflate(R.layout.alert_dialog, null);

        CheckBox music = v.findViewById(R.id.musicCb);
        CheckBox sound = v.findViewById(R.id.soundCb);
        TextView button = v.findViewById(R.id.tvNewWord);
        ImageView close = v.findViewById(R.id.closeDia);

        music.setChecked(isMusicEnabled);
        sound.setChecked(isSoundEnabled);

        builder.setView(v);
        builder.setCancelable(false);

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        button.setOnClickListener(view -> {
            setupEmptyGridView();
            alertDialog.dismiss();
        });

        music.setOnCheckedChangeListener((compoundButton, b) -> {
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("music", music.isChecked());
            editor.apply();
            isMusicEnabled = music.isChecked();
            if (isMusicEnabled) bg.start();
            else bg.pause();
        });
        sound.setOnCheckedChangeListener((compoundButton, b1) -> {
            SharedPreferences.Editor editor = pref.edit();
            editor.putBoolean("sound", sound.isChecked());
            editor.apply();
            isSoundEnabled = sound.isChecked();
        });
        close.setOnClickListener(view -> {
            alertDialog.dismiss();
        });
    }
    void playSoundEffect(MediaPlayer mp){
        mediaPlayer = mp;
        if (isSoundEnabled){
            mediaPlayer.seekTo(0);
            mediaPlayer.start();
        }
    }
    @Override
    public void onPause() {
        super.onPause();
        bg.pause();
        mediaPlayer.pause();
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (isMusicEnabled) bg.start();
    }
}