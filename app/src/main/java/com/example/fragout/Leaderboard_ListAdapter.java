package com.example.fragout;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Leaderboard_ListAdapter extends ArrayAdapter {
    Context context;
    String Item[];
    String Sub_Item[];
    int img[];

    Leaderboard_ListAdapter(Context c, String Item[], String Sub_Item[], int img[])
    {
        super(c,R.layout.listview_design,R.id.Item,Item);
        this.context=c;
        this.Item=Item;
        this.Sub_Item=Sub_Item;
        this.img=img;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = layoutInflater.inflate(R.layout.listview_design,parent,false);
        ImageView imgtv = row.findViewById(R.id.img);
        TextView itemtv = row.findViewById(R.id.Item);
        TextView subitemtv = row.findViewById(R.id.Sub_Item);

        imgtv.setImageResource(img[position]);
        itemtv.setText(Item[position]);
        subitemtv.setText(Sub_Item[position]);

        return row;
    }

}
