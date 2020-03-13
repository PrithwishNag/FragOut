package com.example.fragout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Locale;
import java.util.Random;

public class PlayScreen extends AppCompatActivity {

    private static final String Tag=MainActivity.class.getSimpleName();
    public static final String SCORE_MESSAGE = "Key1";
    public static final String HS_TYPE_MESSAGE = "Key2";

    int num1=10,num2=20,sum=0,mult=0,sub=0,div=0,r=0,score=0;
    Random rand;
    long timeLeft=20000;
    long startTime=20000;

    int lvlcount=0;

    int lvlsetter1=5;
    int lvlsetter2=13;
    int lvlsetter3=23;
    int lvlsetter4=35;
    int lvlsetter5=45;
    int lvlsetter6=100;

    /*int lvlsetterm1=5;
    int lvlsetterm2=13;
    int lvlsetterm3=23;
    int lvlsetterm4=35;
    int lvlsetterm5=45;
    int lvlsetterm6=100;

    int lvlsetters1=5;
    int lvlsetters2=13;
    int lvlsetters3=23;
    int lvlsetters4=35;
    int lvlsetters5=45;
    int lvlsetters6=100;

    int lvlsetterd1=5;
    int lvlsetterd2=13;
    int lvlsetterd3=23;
    int lvlsetterd4=35;
    int lvlsetterd5=45;
    int lvlsetterd6=100;*/

    int lvl=1;

    int HighScoreAdd= 0;
    int HighScoreMult=0;
    int HighScoreSub= 0;
    int HighScoreDiv=0;
    int HighScoreCommon=0;

    String type;
    String HighScoreType;
    CountDownTimer cdg;

    boolean stopped;
    boolean started=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_screen);

        final TextView A= findViewById(R.id.display);
        final TextView B= findViewById(R.id.display2);
        final TextView Res= findViewById(R.id.display3);
        TextView sc= findViewById(R.id.score);
        TextView bonusTime= findViewById(R.id.bonusTime);
        final TextView insStart=findViewById(R.id.instructionStart);
        final TextView operator= findViewById(R.id.operator);
        final TextView exit= findViewById(R.id.exit);
        TextView hs=findViewById(R.id.highscore);

        final Button submit=findViewById(R.id.submit);
        final Button next=findViewById(R.id.next);
        final Button start=findViewById(R.id.start);

        final EditText entry = findViewById(R.id.entry2);

        A.setVisibility(View.INVISIBLE);
        B.setVisibility(View.INVISIBLE);
        operator.setVisibility(View.INVISIBLE);
        Res.setVisibility(View.INVISIBLE);
        submit.setVisibility(View.INVISIBLE);
        next.setVisibility(View.INVISIBLE);
        bonusTime.setVisibility(View.INVISIBLE);
        entry.setVisibility(View.INVISIBLE);
        exit.setVisibility(View.INVISIBLE);

        checkMessage();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                A.setVisibility(View.VISIBLE);
                B.setVisibility(View.VISIBLE);
                operator.setVisibility(View.VISIBLE);
                Res.setVisibility(View.VISIBLE);
                submit.setVisibility(View.VISIBLE);
                next.setVisibility(View.VISIBLE);
                entry.setVisibility(View.VISIBLE);
                exit.setVisibility(View.VISIBLE);
                cdg=countdown(cdg);
                start.setVisibility(View.INVISIBLE);
                insStart.setVisibility(View.INVISIBLE);
                started=true;
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Finish();
            }
        });
    }

    public void checkMessage()
    {
        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.OP_TYPE);
        type=message;
        TypeDecider();
        if(message.equals("+"))
        {
            Add();
        }
        else if(message.equals("*"))
        {
            Mult();
        }
        else if(message.equals("-"))
        {
            Sub();
        }
        else if(message.equals("/"))
        {
            Div();
        }
    }

    public void TypeDecider()
    {
        TextView hs=findViewById(R.id.highscore);
        SharedPreferences prefs = this.getSharedPreferences("myKey", Context.MODE_PRIVATE);
        if(type.equals("+")) {
            HighScoreType = "HighScoreAdd";

            if(prefs.contains(HighScoreType))
                HighScoreAdd=getHighScore(HighScoreType);
            else {
                setHighScore(HighScoreType,0);
            }
            HighScoreCommon=HighScoreAdd;
        }
        if(type.equals("*")) {
            HighScoreType = "HighScoreMult";

            if(prefs.contains(HighScoreType))
                HighScoreMult=getHighScore(HighScoreType);
            else {
                setHighScore(HighScoreType,0);
            }
            HighScoreCommon=HighScoreMult;
        }
        if(type.equals("-")) {
            HighScoreType = "HighScoreSub";

            if(prefs.contains(HighScoreType))
                HighScoreSub=getHighScore(HighScoreType);
            else {
                setHighScore(HighScoreType,0);
            }
            HighScoreCommon=HighScoreSub;
        }
        if(type.equals("/")) {
            HighScoreType = "HighScoreDiv";

            if(prefs.contains(HighScoreType))
                HighScoreDiv=getHighScore(HighScoreType);
            else {
                setHighScore(HighScoreType,0);
            }
            HighScoreCommon=HighScoreDiv;
        }
        hs.setText("HighScore: "+HighScoreCommon);
    }

    public void Add()
    {
        final TextView A= findViewById(R.id.display);
        final TextView B= findViewById(R.id.display2);
        final TextView Res= findViewById(R.id.display3);
        final TextView operator= findViewById(R.id.operator);
        TextView hs=findViewById(R.id.highscore);
        final Button submit=findViewById(R.id.submit);
        Button next=findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NextCombo(1,'+');
            }
        });

        operator.setText("+");

        rand=new Random();
        num1=rand.nextInt(10);
        num2=rand.nextInt(10);
        A.setText(""+num1);
        B.setText(""+num2);
        Res.setText("Try It!");
        sum=num1+num2;

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int err=checkEmptyEntry();
                if(err!=1) {
                    Compare('+');
                    NextCombo(0,'+');
                }
            }
        });
    }

    public void Mult()
    {
        final TextView A= findViewById(R.id.display);
        final TextView B= findViewById(R.id.display2);
        final TextView Res= findViewById(R.id.display3);
        final TextView operator= findViewById(R.id.operator);
        TextView hs=findViewById(R.id.highscore);
        final Button submit=findViewById(R.id.submit);
        Button next=findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NextCombo(1,'*');
            }
        });

        operator.setText("*");

        rand=new Random();
        num1=rand.nextInt(10);
        num2=rand.nextInt(10);
        A.setText(""+num1);
        B.setText(""+num2);
        Res.setText("Try It!");
        mult=num1*num2;

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int err=checkEmptyEntry();
                if(err!=1) {
                    Compare('*');
                    NextCombo(0,'*');
                }
            }
        });
    }

    public void Sub()
    {
        final TextView A= findViewById(R.id.display);
        final TextView B= findViewById(R.id.display2);
        final TextView Res= findViewById(R.id.display3);
        final TextView operator= findViewById(R.id.operator);
        TextView hs=findViewById(R.id.highscore);
        final Button submit=findViewById(R.id.submit);
        Button next=findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NextCombo(1,'-');
            }
        });

        operator.setText("-");

        rand=new Random();
        num1=rand.nextInt(10);
        num2=rand.nextInt(10);
        A.setText(""+num1);
        B.setText(""+num2);
        Res.setText("Try It!");
        sub=num1-num2;

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int err=checkEmptyEntry();
                if(err!=1) {
                    Compare('-');
                    NextCombo(0,'-');
                }
            }
        });
    }

    public void Div()
    {
        final TextView A= findViewById(R.id.display);
        final TextView B= findViewById(R.id.display2);
        final TextView Res= findViewById(R.id.display3);
        final TextView operator= findViewById(R.id.operator);
        TextView hs=findViewById(R.id.highscore);
        final Button submit=findViewById(R.id.submit);
        Button next=findViewById(R.id.next);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NextCombo(1,'/');
            }
        });

        operator.setText("/");

        rand=new Random();
        num1=rand.nextInt(10);
        num2=rand.nextInt(10);
        A.setText(""+num1);
        B.setText(""+num2);
        Res.setText("Try It!");
        div=num1/num2;

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int err=checkEmptyEntry();
                if(err!=1) {
                    Compare('/');
                    NextCombo(0,'/');
                }
            }
        });
    }

    public int checkEmptyEntry()
    {
        TextView Res= findViewById(R.id.display3);
        EditText entry = findViewById(R.id.entry2);
        if(!entry.getText().toString().equals("")) {
            return 0;
        }
        else
        {
            Res.setText("Enter Something Noob!");
            return 1;
        }
    }

    public void Compare(char operator)
    {
        TextView Res= findViewById(R.id.display3);
        TextView sc= findViewById(R.id.score);
        TextView bonusTime= findViewById(R.id.bonusTime);
        EditText entry = findViewById(R.id.entry2);
        String st=entry.getText().toString();
        r=Integer.parseInt(st);
        boolean notEqual=false;

        switch(operator) {
            case '+':
                if (r == sum) {
                    bonusTime.setVisibility(View.VISIBLE);
                    lvlcount++;
                    if (lvl == 1) {
                        score++;
                        timeLeft += 2000;
                        bonusTime.setText("+2");
                    } else if (lvl == 2) {
                        score += 2;
                        timeLeft += 3000;
                        bonusTime.setText("+3");
                    } else if (lvl == 3) {
                        score += 4;
                        timeLeft += 5000;
                        bonusTime.setText("+5");
                    }
                    else if (lvl == 4) {
                        score += 6;
                        timeLeft += 7000;
                        bonusTime.setText("+7");
                    }
                    else if (lvl == 5) {
                        score += 10;
                        timeLeft += 10000;
                        bonusTime.setText("+10");
                    }
                    else if (lvl == 6) {
                        score += 15;
                        timeLeft += 10000;
                        bonusTime.setText("+10");
                    }
                    if (timeLeft >= 40000)
                        timeLeft = 40000;
                    bonusTime.setTextColor(Color.parseColor("#008577"));
                    cdg.cancel();
                    cdg = countdown(cdg);
                    MakeInviCD(bonusTime, 1500);
                    Res.setText("Correct!");
                } else {
                    notEqual=true;
                }
                break;

            case '*':
                if (r == mult) {
                    bonusTime.setVisibility(View.VISIBLE);
                    lvlcount++;
                    if (lvl == 1) {
                        score++;
                        timeLeft += 2000;
                        bonusTime.setText("+2");
                    } else if (lvl == 2) {
                        score += 2;
                        timeLeft += 3000;
                        bonusTime.setText("+3");
                    } else if (lvl == 3) {
                        score += 4;
                        timeLeft += 5000;
                        bonusTime.setText("+5");
                    }
                    else if (lvl == 4) {
                        score += 6;
                        timeLeft += 7000;
                        bonusTime.setText("+7");
                    }
                    else if (lvl == 5) {
                        score += 10;
                        timeLeft += 10000;
                        bonusTime.setText("+10");
                    }
                    else if (lvl == 6) {
                        score += 15;
                        timeLeft += 10000;
                        bonusTime.setText("+10");
                    }
                    if (timeLeft >= 60000)
                        timeLeft = 60000;
                    bonusTime.setTextColor(Color.parseColor("#008577"));
                    cdg.cancel();
                    cdg = countdown(cdg);
                    MakeInviCD(bonusTime, 1500);
                    Res.setText("Correct!");
                } else {
                    notEqual=true;
                }
                break;

            case '-':
                if (r == sub) {
                    bonusTime.setVisibility(View.VISIBLE);
                    lvlcount++;
                    if (lvl == 1) {
                        score++;
                        timeLeft += 2000;
                        bonusTime.setText("+2");
                    } else if (lvl == 2) {
                        score += 2;
                        timeLeft += 3000;
                        bonusTime.setText("+3");
                    } else if (lvl == 3) {
                        score += 4;
                        timeLeft += 5000;
                        bonusTime.setText("+5");
                    }
                    else if (lvl == 4) {
                        score += 6;
                        timeLeft += 7000;
                        bonusTime.setText("+7");
                    }
                    else if (lvl == 5) {
                        score += 10;
                        timeLeft += 10000;
                        bonusTime.setText("+10");
                    }
                    else if (lvl == 6) {
                        score += 15;
                        timeLeft += 10000;
                        bonusTime.setText("+10");
                    }
                    if (timeLeft >= 40000)
                        timeLeft = 40000;
                    bonusTime.setTextColor(Color.parseColor("#008577"));
                    cdg.cancel();
                    cdg = countdown(cdg);
                    MakeInviCD(bonusTime, 1500);
                    Res.setText("Correct!");
                } else {
                    notEqual=true;
                }
                break;
            case '/':
                if (r == div) {
                    bonusTime.setVisibility(View.VISIBLE);
                    lvlcount++;
                    if (lvl == 1) {
                        score++;
                        timeLeft += 2000;
                        bonusTime.setText("+2");
                    } else if (lvl == 2) {
                        score += 2;
                        timeLeft += 3000;
                        bonusTime.setText("+3");
                    } else if (lvl == 3) {
                        score += 4;
                        timeLeft += 5000;
                        bonusTime.setText("+5");
                    }
                    else if (lvl == 4) {
                        score += 6;
                        timeLeft += 7000;
                        bonusTime.setText("+7");
                    }
                    else if (lvl == 5) {
                        score += 10;
                        timeLeft += 10000;
                        bonusTime.setText("+10");
                    }
                    else if (lvl == 6) {
                        score += 15;
                        timeLeft += 10000;
                        bonusTime.setText("+10");
                    }
                    if (timeLeft >= 60000)
                        timeLeft = 60000;
                    bonusTime.setTextColor(Color.parseColor("#008577"));
                    cdg.cancel();
                    cdg = countdown(cdg);
                    MakeInviCD(bonusTime, 1500);
                    Res.setText("Correct!");
                } else {
                    notEqual=true;
                }
                break;
        }
        if(notEqual)
        {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(200);
            score--;
            timeLeft -= 2000;
            bonusTime.setVisibility(View.VISIBLE);
            bonusTime.setText("-2");
            bonusTime.setTextColor(Color.RED);
            cdg.cancel();
            cdg = countdown(cdg);
            MakeInviCD(bonusTime, 1500);
            Res.setText("God Save You!");
        }
        sc.setText("" + score);
        entry.setText("");
        FillProgress();
    }

    public void NextCombo(int nextPressed,char operator)
    {
        int max=10,min=0;
        TextView A= findViewById(R.id.display);
        TextView B= findViewById(R.id.display2);
        TextView bonusTime= findViewById(R.id.bonusTime);

        EditText entry = findViewById(R.id.entry2);
        switch(operator) {
            case '+':
                if (lvlcount < lvlsetter1) {
                    min = 1;
                    max = 5;
                    lvl = 1;
                } else if (lvlcount < lvlsetter2) {
                    min = 5;
                    max = 10;
                    lvl = 2;
                } else if (lvlcount < lvlsetter3) {
                    min = 10;
                    max = 30;
                    lvl = 3;
                }
                else if (lvlcount < lvlsetter4) {
                    min = 30;
                    max = 50;
                    lvl = 4;
                }
                else if (lvlcount < lvlsetter5) {
                    min = 50;
                    max = 100;
                    lvl = 5;
                }
                else if (lvlcount < lvlsetter6) {
                    min = 1;
                    max = 100;
                    lvl = 6;
                }
                num1 = rand.nextInt(max - min + 1) + min;
                num2 = rand.nextInt(max - min + 1) + min;
                A.setText("" + num1);
                B.setText("" + num2);
                sum = num1 + num2;
                break;

            case '*':
                if(lvlcount<lvlsetter1)
                {
                    min=1;max=5;
                    num1 = rand.nextInt(max-min+1)+min;
                    num2 = rand.nextInt(max-min+1)+min;
                    lvl=1;
                }
                else if(lvlcount<lvlsetter2)
                {
                    min=5;max=10;
                    num1 = rand.nextInt(max-min+1)+min;
                    num2 = rand.nextInt(max-min+1)+min;
                    lvl=2;
                }
                else if(lvlcount<lvlsetter3) {
                    min=10;max=30;
                    num1 = rand.nextInt(max-min+1)+min;
                    min=1;max=7;
                    num2 = rand.nextInt(max-min+1)+min;
                    lvl=3;
                }
                else if(lvlcount<lvlsetter4) {
                    min=10;max=30;
                    num1 = rand.nextInt(max-min+1)+min;
                    min=3;max=10;
                    num2 = rand.nextInt(max-min+1)+min;
                    lvl=4;
                }
                else if(lvlcount<lvlsetter5) {
                    min=30;max=50;
                    num1 = rand.nextInt(max-min+1)+min;
                    min=10;max=20;
                    num2 = rand.nextInt(max-min+1)+min;
                    lvl=5;
                }
                else if(lvlcount<lvlsetter6) {
                    min = 10;
                    max = 100;
                    num1 = rand.nextInt(max - min + 1) + min;
                    num2 = rand.nextInt(max - min + 1) + min;
                    lvl = 6;
                }
                A.setText("" + num1);
                B.setText("" + num2);
                mult = num1 * num2;
                break;

            case '-':
                if (lvlcount < lvlsetter1) {
                    min = 1;
                    max = 5;
                    lvl = 1;
                } else if (lvlcount < lvlsetter2) {
                    min = 5;
                    max = 10;
                    lvl = 2;
                } else if (lvlcount < lvlsetter3) {
                    min = 10;
                    max = 30;
                    lvl = 3;
                }
                else if (lvlcount < lvlsetter4) {
                    min = 30;
                    max = 50;
                    lvl = 4;
                }
                else if (lvlcount < lvlsetter5) {
                    min = 50;
                    max = 100;
                    lvl = 5;
                }
                else if (lvlcount < lvlsetter6) {
                    min = 1;
                    max = 100;
                    lvl = 6;
                }
                num1 = rand.nextInt(max - min + 1) + min;
                num2 = rand.nextInt(max - min + 1) + min;
                A.setText("" + num1);
                B.setText("" + num2);
                sub = num1 - num2;
                break;

            case '/':
                if(lvlcount<lvlsetter1)
                {
                    min=1;max=5;
                    num1 = rand.nextInt(max-min+1)+min;
                    num2 = rand.nextInt(max-min+1)+min;
                    lvl=1;
                }
                else if(lvlcount<lvlsetter2)
                {
                    min=5;max=10;
                    num1 = rand.nextInt(max-min+1)+min;
                    num2 = rand.nextInt(max-min+1)+min;
                    lvl=2;
                }
                else if(lvlcount<lvlsetter3) {
                    min=10;max=30;
                    num1 = rand.nextInt(max-min+1)+min;
                    min=1;max=7;
                    num2 = rand.nextInt(max-min+1)+min;
                    lvl=3;
                }
                else if(lvlcount<lvlsetter4) {
                    min=10;max=30;
                    num1 = rand.nextInt(max-min+1)+min;
                    min=3;max=10;
                    num2 = rand.nextInt(max-min+1)+min;
                    lvl=4;
                }
                else if(lvlcount<lvlsetter5) {
                    min=30;max=50;
                    num1 = rand.nextInt(max-min+1)+min;
                    min=10;max=20;
                    num2 = rand.nextInt(max-min+1)+min;
                    lvl=5;
                }
                else if(lvlcount<lvlsetter6) {
                    min = 10;
                    max = 100;
                    num1 = rand.nextInt(max - min + 1) + min;
                    num2 = rand.nextInt(max - min + 1) + min;
                    lvl = 6;
                }
                A.setText("" + num1);
                B.setText("" + num2);
                div = (int)(num1 / num2);
                break;
        }
        if (nextPressed == 1) {
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
        setHighScore(HighScoreType,HighScoreCommon);
        Intent intent = new Intent(this, FinalScore.class);
        intent.putExtra(SCORE_MESSAGE,""+score);
        intent.putExtra(HS_TYPE_MESSAGE,HighScoreType);
        if(cdg!=null)
            cdg.cancel();
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
        if (cdg != null)
            cdg.cancel();
        popupExit();
        /*super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (cdg != null)
            cdg.cancel();
        startActivity(intent);*/
    }

    @Override
    public void onStop() {

        super.onStop();
        if (cdg != null) {
            cdg.cancel();
            stopped = true;
        }
    }

    @Override
    public void onResume() {

        super.onResume();
        if(stopped)
            cdg=countdown(cdg);
    }

    public void popupExit()
    {
        Log.d(Tag,"here");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit!");
        builder.setMessage("Are you sure you want to exit?\nCurrent score will not be counted!")
            .setCancelable(false)
            .setPositiveButton("I Quit",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(PlayScreen.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    if (cdg != null)
                        cdg.cancel();
                    startActivity(intent);
                }
            }).setNegativeButton("I'll Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int gg=10;
                if(started)
                    cdg=countdown(cdg);
            }
        });
        AlertDialog alert=builder.create();
        alert.show();
    }

    public void setHighScore(String HighScoreString,int HighScore)
    {
        if(score>HighScore)
        {
            HighScore=score;
            SharedPreferences prefs = this.getSharedPreferences("myKey", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor =prefs.edit();
            editor.putInt(HighScoreString,HighScore);
            editor.commit();
        }
    }

    public int getHighScore(String HighScore) {
        SharedPreferences prefs = this.getSharedPreferences("myKey", Context.MODE_PRIVATE);
        int hs = prefs.getInt(HighScore, 0);
        return hs;
    }

    public void FillProgress() {
        ProgressBar progresslvl = findViewById(R.id.progress_lvl);
        TextView minilvl = findViewById(R.id.minilvl);

        int progress = 0;
        int inlvl = 0;
            if (lvlcount < lvlsetter1) {
                inlvl = 1;
                progress = (lvlcount * 100) / lvlsetter1;
            } else if (lvlcount < lvlsetter2) {
                inlvl = 2;
                progress = ((lvlcount - lvlsetter1) * 100) / (lvlsetter2 - lvlsetter1);
            } else if (lvlcount < lvlsetter3) {
                inlvl = 3;
                progress = ((lvlcount - lvlsetter2) * 100) / (lvlsetter3 - lvlsetter2);
            }
            else if (lvlcount < lvlsetter4) {
                inlvl = 4;
                progress = ((lvlcount - lvlsetter3) * 100) / (lvlsetter4 - lvlsetter3);
            }
            else if (lvlcount < lvlsetter5) {
                inlvl = 5;
                progress = ((lvlcount - lvlsetter4) * 100) / (lvlsetter5 - lvlsetter4);
            }
            else if (lvlcount < lvlsetter6) {
                inlvl = 6;
                progress = ((lvlcount - lvlsetter5) * 100) / (lvlsetter6 - lvlsetter5);
            }
            //level = 1 to 2
            if (lvlcount == lvlsetter1)
                progress = 0;
            //level = 2 to 3
            if (lvlcount == lvlsetter2)
                progress = 0;
            //level = 3 to 4
            if (lvlcount == lvlsetter3)
                progress = 0;

        minilvl.setText("Level: "+inlvl);
        progresslvl.setProgress(progress);

    }
}
