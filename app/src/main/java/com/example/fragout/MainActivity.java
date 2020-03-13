package com.example.fragout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Constraints;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import static java.lang.Boolean.FALSE;

public class MainActivity extends AppCompatActivity {

    private static final String Tag=MainActivity.class.getSimpleName();
    public static final String OP_TYPE = "Key1";

    int Experience_total;
    int Experience=0;
    int Experience_level=0;

    int maximum_levels=10;

    int Experience_max[]=new int[maximum_levels];


    /*int Experience_max_1=500;
    int Experience_max_2=500;
    int Experience_max_3=750;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button exit = findViewById(R.id.exit);

        PlayTypeDecider();
        initLevelExperience();
        checkSavedStuff();
        //ExperienceManager();
        Disabler();
        FillExperienceBar(Experience);
        //getMessage();



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
                Intent intent = new Intent(MainActivity.this,HomeScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    public void PlayTypeDecider()
    {
        Button playadd=findViewById(R.id.playAdd);
        Button playmult=findViewById(R.id.playMult);
        Button playsub=findViewById(R.id.playSubtract);
        Button playdiv=findViewById(R.id.playDivision);
        Intent intent = getIntent();
        final String pt= intent.getStringExtra(HomeScreen.PLAY_TYPE);

        playadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayAdd(pt);
            }
        });
        playmult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayMult(pt);
            }
        });
        playsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlaySub(pt);
            }
        });
        playdiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayDiv(pt);
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

    /*public void ExperienceManager()
    {
        for(int lvl=0;lvl<maximum_levels;lvl++) {
            if (Experience > Experience_max[lvl] && Experience_level == lvl+1)
                Experience_level = lvl + 1;
        }

        Disabler();
        FillExperienceBar(Experience);
    }*/

    public void Disabler()
    {
        Button playmult=findViewById(R.id.playMult);
        Button playsub=findViewById(R.id.playSubtract);
        Button playdiv=findViewById(R.id.playDivision);
        TextView lockadd=findViewById(R.id.lockAdd);
        TextView lockmult=findViewById(R.id.lockMult);
        TextView locksub=findViewById(R.id.lockSub);
        TextView lockdiv=findViewById(R.id.lockDiv);

        Drawable design = getDrawable(R.drawable.disabled_button_design);
        Drawable lock_design = getDrawable(R.drawable.lock);
        //Disabler
        if(Experience_level==0) {
            playmult.setEnabled(FALSE);
            playmult.setBackground(design);
            lockmult.setBackground(lock_design);
            playsub.setEnabled(FALSE);
            playsub.setBackground(design);
            locksub.setBackground(lock_design);
            playdiv.setEnabled(FALSE);
            playdiv.setBackground(design);
            lockdiv.setBackground(lock_design);
        }
        if(Experience_level==1) {
            playmult.setEnabled(FALSE);
            playmult.setBackground(design);
            lockmult.setBackground(lock_design);
            playdiv.setEnabled(FALSE);
            playdiv.setBackground(design);
            lockdiv.setBackground(lock_design);
        }
        if(Experience_level==2) {
            playdiv.setEnabled(FALSE);
            playdiv.setBackground(design);
            lockdiv.setBackground(lock_design);
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

    public void PlayAdd(String pt)
    {
        Intent intent1;
        Intent intent2;
        Intent intent;
        intent1=new Intent(this, PlayScreen.class);
        intent2=new Intent(this, MultipleChoiceScreen.class);
        if(pt.equals("noOption"))
        {
            intent=intent1;
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(OP_TYPE,"+");
            startActivity(intent);
        }
        else if(pt.equals("multiple"))
        {
            intent=intent2;
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(OP_TYPE,"+");
            startActivity(intent);
        }
    }

    public void PlayMult(String pt)
    {
        Intent intent1;
        Intent intent2;
        Intent intent;
        intent1=new Intent(this, PlayScreen.class);
        intent2=new Intent(this, MultipleChoiceScreen.class);
        if(pt.equals("noOption"))
        {
            intent=intent1;
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(OP_TYPE,"*");
            startActivity(intent);
        }
        else if(pt.equals("multiple"))
        {
            intent=intent2;
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(OP_TYPE,"*");
            startActivity(intent);
        }

    }

    public void PlaySub(String pt)
    {
        Intent intent1;
        Intent intent2;
        Intent intent;
        intent1=new Intent(this, PlayScreen.class);
        intent2=new Intent(this, MultipleChoiceScreen.class);
        if(pt.equals("noOption"))
        {
            intent=intent1;
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(OP_TYPE,"-");
            startActivity(intent);
        }
        else if(pt.equals("multiple"))
        {
            intent=intent2;
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(OP_TYPE,"-");
            startActivity(intent);
        }
    }

    public void PlayDiv(String pt)
    {
        Intent intent1;
        Intent intent2;
        Intent intent;
        intent1=new Intent(this, PlayScreen.class);
        intent2=new Intent(this, MultipleChoiceScreen.class);
        if(pt.equals("noOption"))
        {
            intent=intent1;
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(OP_TYPE,"/");
            startActivity(intent);
        }
        else if(pt.equals("multiple"))
        {
            intent=intent2;
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(OP_TYPE,"/");
            startActivity(intent);
        }
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

    /*public void getMessage()
    {
        Intent intent = getIntent();
        String message = intent.getStringExtra(FinalScore.EXP_MSG);
        //Log.d(Tag,"Msg: *"+message+"*");
        int exp;
        if(message!=null)
                exp=Integer.parseInt(message);
        else
            return;
        if(exp>=Experience_max[Experience_level])
        {
            //Level Up
            Experience=exp-Experience_max[Experience_level];
            setExperience(Experience);
            Experience_level++;
            setExperience_level(Experience_level);
        }
        else
        {
            Experience=exp;
            setExperience(Experience);
            FillExperienceBar(Experience);

        }
    }*/

}
