package com.example.fragout;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;

import static android.content.Context.VIBRATOR_SERVICE;
import static androidx.core.content.ContextCompat.getSystemService;

public class MultiplayerFrag1 extends Fragment {

    private static final String Tag = MainActivity.class.getSimpleName();
    public static final String SCORE_MESSAGE = "Key1";
    public static final String HS_TYPE_MESSAGE = "Key2";

    View view;

    int num1 = 10, num2 = 20, sum = 0, mult = 0, sub = 0, div = 0, r = 0, score = 0;
    Random rand;
    long timeLeft = 20000;
    long startTime = 20000;

    int lvlcount = 0;

    int lvlsetter1 = 5;
    int lvlsetter2 = 13;
    int lvlsetter3 = 23;
    int lvlsetter4 = 35;
    int lvlsetter5 = 45;
    int lvlsetter6 = 100;

    int lvl = 1;

    int HighScoreAdd = 0;
    int HighScoreMult = 0;
    int HighScoreSub = 0;
    int HighScoreDiv = 0;
    int HighScoreCommon = 0;

    String type = "+";
    String HighScoreType;
    CountDownTimer cdg;

    boolean stopped;
    boolean started = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_multiplayer_frag1, container, false);
        return view;
    }
}
