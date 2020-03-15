package com.example.fragout;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.client.Firebase;

public class HomeScreen extends AppCompatActivity {

    public static final String OP_TYPE = "Key1";
    public static final String PLAY_TYPE = "Key2";
    private static final String Tag=MainActivity.class.getSimpleName();

    public static String Name ;
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

        checkSavedName();

        initLevelExperience();
        checkSavedStuff();
        FillExperienceBar(Experience);
        CreateUserFirebase(Name);

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

    public void checkSavedName()
    {
        SharedPreferences prefs = this.getSharedPreferences("nameKey", Context.MODE_PRIVATE);
        if(!prefs.contains("Name"))
        {
            final EditText name_ed = new EditText(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            name_ed.setLayoutParams(lp);
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("NAME");
            builder.setMessage("This name cannot be changed afterwards!!!")
                    .setView(name_ed)
                    .setCancelable(false)
                    .setPositiveButton("Save",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(name_ed.getText().toString().equals("")) {
                                name_ed.setHint("Write Your name dumbass");
                                checkSavedName();
                            }
                            else {
                                saveName(name_ed.getText().toString());
                            }
                        }
                    });
            AlertDialog alert=builder.create();
            alert.show();
        }
        else
        {
            String name = prefs.getString("Name","");
            Name=name;
            TextView name_tv = findViewById(R.id.Name);
            name_tv.setText(""+name);
        }
    }

    public void saveName(String name)
    {
        Name=name;
        SharedPreferences prefs = this.getSharedPreferences("nameKey", Context.MODE_PRIVATE);
        prefs.edit().putString("Name",name).apply();
        TextView name_tv = findViewById(R.id.Name);
        name_tv.setText(""+name);
    }

    void CreateUserFirebase(String name)
    {
        mref.child("Users").child(""+name).child("Experience").setValue(""+Experience);
        mref.child("Users").child(""+name).child("Level").setValue(""+Experience_level);
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
        SharedPreferences.Editor editor = prefs.edit();
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
