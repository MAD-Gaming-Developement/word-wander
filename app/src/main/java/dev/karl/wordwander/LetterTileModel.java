package dev.karl.wordwander;

import androidx.annotation.ColorRes;

public class LetterTileModel {
    public String letter;
    public Integer textColorResource, bgColorResource;
    public LetterTileModel(String letter, @ColorRes Integer textColorResource, @ColorRes Integer bgColorResource) {
        this.letter = letter;
        this.textColorResource = textColorResource;
        this.bgColorResource = bgColorResource;
    }
}
