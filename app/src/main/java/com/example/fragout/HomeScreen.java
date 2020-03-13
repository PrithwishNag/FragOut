package com.example.fragout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.client.Firebase;

public class HomeScreen extends AppCompatActivity {

    public static final String OP_TYPE = "Key1";
    public static final String PLAY_TYPE = "Key2";
    private static final String Tag=MainActivity.class.getSimpleName();

    int Experience_total;
    int Experience=0;
    int Experience_level=0;

    int maximum_levels=10;
    int Experience_max[]=new int[maximum_levels];

    private Firebase mref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        Button noOpt = findViewById(R.id.noOpt);
        Button multiOpt = findViewById(R.id.multiOpt);

        Button exit = findViewById(R.id.exit);
        Button multiplayer = findViewById(R.id.multiplayer);
        final TextView test =findViewById(R.id.MatchmakingUpdate);
        mref = new Firebase("https://fragsout.firebaseio.com/");

        initLevelExperience();
        checkSavedStuff();
        FillExperienceBar(Experience);

        multiplayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this,MultiplayerOptionRoom.class);
                startActivity(intent);
            }
        });

        noOpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this,MainActivity.class);
                intent.putExtra(PLAY_TYPE,"noOption");
                startActivity(intent);
            }
        });

        multiOpt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this,MainActivity.class);
                intent.putExtra(PLAY_TYPE,"multiple");
                startActivity(intent);
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                System.exit(0);
            }
        });

        Button home=findViewById(R.id.home);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this,HomeScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        //test
        Button testmult = findViewById(R.id.mult_test);
        testmult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeScreen.this,test_multiplayer_graphics.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

    }

    public void initLevelExperience()
    {
        for(int lvl=0;lvl<maximum_levels;lvl++)
        {
            Experience_max[lvl]=500;
        }
    }

    public int setExperience(int exp)
    {
        SharedPreferences prefs = this.getSharedPreferences("myKey", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =prefs.edit();
        editor.putInt("Experience",exp);
        editor.commit();
        return exp;
    }

    public void checkSavedStuff()
    {
        SharedPreferences prefs_exp = this.getSharedPreferences("myKey", Context.MODE_PRIVATE);
        if (prefs_exp.contains("Experience"))
        {
            Experience=getExperience();
        }
        else
        {
            setExperience(0);
        }

        SharedPreferences prefs_lvl = this.getSharedPreferences("myKey", Context.MODE_PRIVATE);
        if (prefs_lvl.contains("Level"))
        {
            Experience_level=getExperience_level();
        }
        else
        {
            setExperience_level(0);
        }
    }

    public int getExperience() {
        SharedPreferences prefs = this.getSharedPreferences("myKey", Context.MODE_PRIVATE);
        int Exp = prefs.getInt("Experience", 0);
        return Exp;
    }

    public void setExperience_level(int level)
    {
        SharedPreferences prefs = this.getSharedPreferences("myKey", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =prefs.edit();
        editor.putInt("Level",level);
        editor.commit();
    }

    public int getExperience_level() {
        SharedPreferences prefs = this.getSharedPreferences("myKey", Context.MODE_PRIVATE);
        int lvl = prefs.getInt("Level", 0);
        return lvl;
    }

    public void FillExperienceBar(int Exp)
    {
        ProgressBar expfill=findViewById(R.id.experience_bar);
        TextView expcount=findViewById(R.id.exp_count);
        TextView level=findViewById(R.id.level);

        expcount.setText("Exp: "+Exp);
        int lvl=getExperience_level();
        Experience_level=lvl;
        level.setText("Level: "+Experience_level);

        int progress=(Exp*100)/Experience_max[Experience_level];
        expfill.setProgress(progress);

    }
}
