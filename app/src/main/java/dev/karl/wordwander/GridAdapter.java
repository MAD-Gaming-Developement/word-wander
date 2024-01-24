package dev.karl.wordwander;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {
    Context context;
    ArrayList<LetterTileModel> letterTileList;

    LayoutInflater inflater;

    public GridAdapter(Context context, ArrayList<LetterTileModel> list) {
        this.context = context;
        this.letterTileList = list;
    }
    public void removeItem(Integer card){
        letterTileList.remove(card);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return letterTileList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(inflater == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if(convertView == null){
            convertView = inflater.inflate(R.layout.letter_grid_item, null);
        }
        //
        TextView tv = convertView.findViewById(R.id.gridItem);

        //
        tv.setText(letterTileList.get(position).letter);
        tv.setTextColor(ContextCompat.getColor(context, letterTileList.get(position).textColorResource));
        tv.setBackgroundResource(letterTileList.get(position).bgColorResource);

        return convertView;
    }
    public void setLetter(int pos, String letter){
        letterTileList.set(
                pos,
                new LetterTileModel(
                        letter,
                        letterTileList.get(pos).textColorResource,
                        letterTileList.get(pos).bgColorResource
                )
        );
        notifyDataSetChanged();
    }
    public void setTextColorResource(int pos, Integer color){
        letterTileList.set(
                pos,
                new LetterTileModel(
                        letterTileList.get(pos).letter,
                        color,
                        letterTileList.get(pos).bgColorResource
                )
        );
        notifyDataSetChanged();
    }
    public void setBgColorResource(int pos, Integer color){
        letterTileList.set(
                pos,
                new LetterTileModel(
                        letterTileList.get(pos).letter,
                        letterTileList.get(pos).textColorResource,
                        color
                )
        );
        notifyDataSetChanged();
    }
}
