package com.example.fragout;

import android.app.Activity;
import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

public class Leaderboard_ListAdapter extends ArrayAdapter {
    private final Activity context;
    /*private final Integer[] imageIDarray;
    private final String[] Rank;
    private final String[] Name_Score;*/

    public Leaderboard_ListAdapter(@NonNull Context context, int resource, Activity context1) {
        super(context, resource);
        this.context = context1;
    }
}
