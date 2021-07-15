package com.example.fragout;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;
import java.util.Random;

public class PlayScreenMult extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.fragout.MESSAGE";
    int num1=10,num2=20,m=0,r=0,score=0;
    Random rand;
    long timeLeft=20000;
    long startTime=20000;
    int lvlcount=0;
    int lvl=1;
    int HighScoreMult= 0;
    CountDownTimer cdg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_screen_mult);

        final TextView A= findViewById(R.id.display);
        final TextView B= findViewById(R.id.display2);
        final TextView Res= findViewById(R.id.display3);
        TextView sc= findViewById(R.id.score);
        TextView bonusTime= findViewById(R.id.bonusTime);
        final TextView insStart=findViewById(R.id.instructionStart);
        final TextView operator= findViewById(R.id.operator);
        final TextView exit= findViewById(R.id.exit);
        TextView hs=findViewById(R.id.highscore);

        final Button sum=findViewById(R.id.submit);
        final Button next=findViewById(R.id.next);
        final Button start=findViewById(R.id.start);

        final EditText entry = findViewById(R.id.entry2);

        A.setVisibility(View.INVISIBLE);
        B.setVisibility(View.INVISIBLE);
        operator.setVisibility(View.INVISIBLE);
        Res.setVisibility(View.INVISIBLE);
        sum.setVisibility(View.INVISIBLE);
        next.setVisibility(View.INVISIBLE);
        bonusTime.setVisibility(View.INVISIBLE);
        entry.setVisibility(View.INVISIBLE);
        exit.setVisibility(View.INVISIBLE);

        rand=new Random();
        num1=rand.nextInt(10);
        num2=rand.nextInt(10);
        A.setText(""+num1);
        B.setText(""+num2);
        Res.setText("Try It!");
        m=num1*num2;
        HighScoreMult=getHighScoreMult();
        hs.setText("HighScore: "+HighScoreMult);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                A.setVisibility(View.VISIBLE);
                B.setVisibility(View.VISIBLE);
                operator.setVisibility(View.VISIBLE);
                Res.setVisibility(View.VISIBLE);
                sum.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
                entry.setVisibility(View.VISIBLE);
                exit.setVisibility(View.VISIBLE);
                cdg=countdown(cdg);
                start.setVisibility(View.INVISIBLE);
                insStart.setVisibility(View.INVISIBLE);
            }
        });
        sum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int err=Compare();
                if(err==0)
                    NextCombo(0);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NextCombo(1);
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Finish();
            }
        });
    }
    public int Compare()
    {
        TextView Res= findViewById(R.id.display3);
        TextView sc= findViewById(R.id.score);
        TextView bonusTime= findViewById(R.id.bonusTime);

        String st;
        EditText entry = findViewById(R.id.entry2);
        if(!entry.getText().toString().equals("")) {
            st = entry.getText().toString();
        }
        else
        {
            Res.setText("Enter Something Noob!");
            return 1;
        }
        r=Integer.parseInt(st);
        if(r==m)
        {
            bonusTime.setVisibility(View.VISIBLE);
            lvlcount++;
            if(lvl==1) {
                score++;
                timeLeft+=3000;
                bonusTime.setText("+3");
            }
            else if(lvl==2) {
                score += 2;
                timeLeft+=5000;
                bonusTime.setText("+5");
            }
            else if(lvl==3) {
                score += 4;
                timeLeft+=10000;
                bonusTime.setText("+10");
            }
            if(timeLeft>=40000)
                timeLeft=40000;
            bonusTime.setTextColor(Color.parseColor("#008577"));
            cdg.cancel();
            cdg=countdown(cdg);
            MakeInviCD(bonusTime,1500);
            Res.setText("Correct!");
        }
        else
        {
            score--;
            timeLeft-=2000;
            bonusTime.setVisibility(View.VISIBLE);
            bonusTime.setText("-2");
            bonusTime.setTextColor(Color.RED);
            cdg.cancel();
            cdg=countdown(cdg);
            MakeInviCD(bonusTime,1500);
            Res.setText("God Save You!");
        }
        sc.setText(""+score);
        entry.setText("");
        return 0;
    }

    public void NextCombo(int nextPressed)
    {
        int max=10,min=0;
        TextView A= findViewById(R.id.display);
        TextView B= findViewById(R.id.display2);
        TextView bonusTime= findViewById(R.id.bonusTime);

        EditText entry = findViewById(R.id.entry2);
        if(lvlcount<=7)
        {
            min=2;max=10;
            num1 = rand.nextInt(max-min+1)+min;
            num2 = rand.nextInt(max-min+1)+min;
            lvl=1;
        }
        else if(lvlcount<=15)
        {
            min=10;max=100;
            num1 = rand.nextInt(max-min+1)+min;
            min=3;max=10;
            num2 = rand.nextInt(max-min+1)+min;
            lvl=2;
        }
        else if(lvlcount>27) {
            min = 10;
            max = 100;
            num1 = rand.nextInt(max-min+1)+min;
            num2 = rand.nextInt(max-min+1)+min;
            lvl = 3;
        }
        A.setText(""+num1);
        B.setText(""+num2);
        m=num1*num2;
        if(nextPressed==1) {
            timeLeft -= 2000;
            bonusTime.setVisibility(View.VISIBLE);
            bonusTime.setText("-2");
            bonusTime.setTextColor(Color.parseColor("#D81B60"));
        }
        cdg.cancel();
        cdg=countdown(cdg);
        MakeInviCD(bonusTime,1500);
        entry.setText("");
    }

    public CountDownTimer countdown(CountDownTimer cd)
    {
        cd=new CountDownTimer(timeLeft,1000) {
            @Override
            public void onTick(long l) {
                timeLeft=l;
                UpdateTimer();
            }

            @Override
            public void onFinish() {

                Finish();
            }
        }.start();
        return cd;
    }

    public void UpdateTimer()
    {
        TextView time=findViewById(R.id.CountDown);
        int min=(int) (timeLeft/1000)/60;
        int sec=(int) (timeLeft/1000)%60;
        String FormatedTime=String.format(Locale.getDefault(),"%02d:%02d",min,sec);
        time.setText(FormatedTime);
        if(sec<=05)
            time.setTextColor(Color.parseColor("#D81B60"));
        else
            time.setTextColor(Color.parseColor("#81000000"));

    }

    public void Reset()
    {
        timeLeft=startTime;
    }

    public void Finish() {
        setHighScoreMult();
        Intent intent = new Intent(this, FinalScore.class);
        intent.putExtra(EXTRA_MESSAGE,""+score);
        this.finish();
        startActivity(intent);
    }

    public void MakeInviCD(final TextView tv, long tl)
    {

        CountDownTimer cd=new CountDownTimer(tl,1000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                tv.setVisibility(View.INVISIBLE);
            }
        }.start();
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        Intent intent=new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if(cdg!=null)
            cdg.cancel();
        startActivity(intent);
    }

    public void setHighScoreMult()
    {
        if(score>HighScoreMult)
        {
            HighScoreMult=score;
            SharedPreferences prefs = this.getSharedPreferences("myKey", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor =prefs.edit();
            editor.putInt("HighScoreMult",HighScoreMult);
            editor.commit();
        }
    }

    public int getHighScoreMult() {
        SharedPreferences prefs = this.getSharedPreferences("myKey", Context.MODE_PRIVATE);
        int hs = prefs.getInt("HighScoreMult", 0);
        return hs;
    }
}
