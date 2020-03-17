package com.example.fragout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class FinalScore extends AppCompatActivity {

    public static final String EXP_MSG = "Key1";
    private static final String Tag=MainActivity.class.getSimpleName();


    int Exp_factor=1;
    int Experience;
    int Experience_level=0;
    int maximum_levels=10;
    int Experience_max[]=new int[maximum_levels];

    String HighScoreType;

    boolean backed=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_score);

        Button home = findViewById(R.id.home);
        Button exit = findViewById(R.id.exit);
        Button leaderboardbut = findViewById(R.id.leaderboard);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendHome();
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(startMain);
                //System.exit(0);
            }
        });

        leaderboardbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMain = new Intent(FinalScore.this,Leaderboard.class);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(startMain);
            }
        });

        Intent intent = getIntent();
        String message_score = intent.getStringExtra(PlayScreen.SCORE_MESSAGE);
        String message_hs_type = intent.getStringExtra(PlayScreen.HS_TYPE_MESSAGE);

        HighScoreType=message_hs_type;

        TextView disp = findViewById(R.id.FinalDisp);

        int score=Integer.parseInt(message_score);
        int HighScore=getHighScore(HighScoreType);
        if(score==HighScore)
        {
            disp.setTextSize(20f);
            disp.setText("Congratulation! You Made a HighScore\n\t"+message_score);
        }
        else
            disp.setText("Score: "+message_score);

        int exp=getExperience()+ExperienceDecider(score);
        Experience=exp;

        initLevelExperience();
        LevelUP_or_SetExp();

    }

    public int getHighScore(String hs_type) {
        SharedPreferences prefs = this.getSharedPreferences("myKey", Context.MODE_PRIVATE);
        int HighScore = prefs.getInt(hs_type, 0);
        return HighScore;
    }

    public boolean isPositive(int num)
    {
        if(num>0)
            return true;
        else
            return false;
    }

    public int ExperienceDecider(int score)
    {
        TextView expgain = findViewById(R.id.exp_gain);
        int level=getExperience_level();
        if(level==0)
        {
            if(isPositive(score))
                Exp_factor=10;
            else
                Exp_factor=2;
        }
        if(level==1)
        {
            if(isPositive(score))
                Exp_factor=7;
            else
                Exp_factor=3;
        }
        if(level==2)
        {
            if(isPositive(score))
                Exp_factor=5;
            else
                Exp_factor=3;
        }
        if(level==3)
        {
            if(isPositive(score))
                Exp_factor=4;
            else
                Exp_factor=5;
        }

        int Exp=Exp_factor*score;
        if(isPositive(Exp))
            expgain.setText("+"+Exp+" Exp");
        else
            expgain.setText(Exp+" Exp");

        return Exp;
    }

    public int getExperience() {
        SharedPreferences prefs = this.getSharedPreferences("myKey", Context.MODE_PRIVATE);
        int Exp = prefs.getInt("Experience", 0);
        return Exp;
    }

    public int getExperience_level() {
        SharedPreferences prefs = this.getSharedPreferences("myKey", Context.MODE_PRIVATE);
        int lvl = prefs.getInt("Level", 0);
        return lvl;
    }

    public void SendHome()
    {
        Intent intent=new Intent(this, HomeScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //intent.putExtra(EXP_MSG,""+Experience);
        this.finish();
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        backed=true;
        SendHome();
    }

    /*@Override
    public  void onStop() {

        super.onStop();
        if(backed==false)
            SendHome();
    }*/

    public void initLevelExperience()
    {
        for(int lvl=0;lvl<maximum_levels;lvl++)
        {
            Experience_max[lvl]=500;
        }
    }

    public void LevelUP_or_SetExp()
    {
        int exp=Experience;
        if(exp>=Experience_max[Experience_level])
        {
            //Level Up
            Experience=exp-Experience_max[Experience_level];
            setExperience(Experience);
            Experience_level=getExperience_level();
            Experience_level++;
            setExperience_level(Experience_level);
        }
        else
        {
            Experience=exp;
            setExperience(Experience);
        }
    }

    public void setExperience_level(int level)
    {
        SharedPreferences prefs = this.getSharedPreferences("myKey", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =prefs.edit();
        editor.putInt("Level",level);
        editor.commit();
    }

    public int setExperience(int exp)
    {
        SharedPreferences prefs = this.getSharedPreferences("myKey", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =prefs.edit();
        editor.putInt("Experience",exp);
        editor.commit();
        return exp;
    }
}
