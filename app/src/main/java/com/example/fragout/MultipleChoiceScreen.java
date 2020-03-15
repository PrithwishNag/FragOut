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

import com.firebase.client.Firebase;

import org.w3c.dom.Text;

import java.util.Locale;
import java.util.Random;

public class MultipleChoiceScreen extends AppCompatActivity {

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
    int lvlsetter6=60;

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
        setContentView(R.layout.activity_multiple_choice_screen);

        final TextView A= findViewById(R.id.display);
        final TextView B= findViewById(R.id.display2);
        final TextView Res= findViewById(R.id.display3);
        TextView sc= findViewById(R.id.score);
        TextView bonusTime= findViewById(R.id.bonusTime);
        final TextView insStart=findViewById(R.id.instructionStart);
        final TextView operator= findViewById(R.id.operator);
        final TextView exit= findViewById(R.id.exit);
        TextView hs=findViewById(R.id.highscore);

        final Button next = findViewById(R.id.next);
        final Button start=findViewById(R.id.start);
        final Button op1 = findViewById(R.id.option1);
        final Button op2 = findViewById(R.id.option2);
        final Button op3 = findViewById(R.id.option3);
        final Button op4 = findViewById(R.id.option4);

        A.setVisibility(View.INVISIBLE);
        B.setVisibility(View.INVISIBLE);
        operator.setVisibility(View.INVISIBLE);
        Res.setVisibility(View.INVISIBLE);
        bonusTime.setVisibility(View.INVISIBLE);
        exit.setVisibility(View.INVISIBLE);
        next.setVisibility(View.INVISIBLE);
        op1.setVisibility(View.INVISIBLE);
        op2.setVisibility(View.INVISIBLE);
        op3.setVisibility(View.INVISIBLE);
        op4.setVisibility(View.INVISIBLE);


        checkMessage();

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                A.setVisibility(View.VISIBLE);
                B.setVisibility(View.VISIBLE);
                operator.setVisibility(View.VISIBLE);
                Res.setVisibility(View.VISIBLE);
                exit.setVisibility(View.VISIBLE);
                start.setVisibility(View.INVISIBLE);
                insStart.setVisibility(View.INVISIBLE);
                next.setVisibility(View.VISIBLE);
                op1.setVisibility(View.VISIBLE);
                op2.setVisibility(View.VISIBLE);
                op3.setVisibility(View.VISIBLE);
                op4.setVisibility(View.VISIBLE);
                cdg=countdown(cdg);
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
        switch (message) {
            case "+":
                AddSubMultDiv(type);
                break;
            case "*":
                AddSubMultDiv(type);
                break;
            case "-":
                AddSubMultDiv(type);
                break;
            case "/":
                AddSubMultDiv(type);
                break;
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

    public int digitCalculate(int num)
    {
        int count=0;
        while(num!=0)
        {
            num/=10;
            count++;
        }
        return count;
    }

    public void OptionGenerator() {
        Button op1 = findViewById(R.id.option1);
        Button op2 = findViewById(R.id.option2);
        Button op3 = findViewById(R.id.option3);
        Button op4 = findViewById(R.id.option4);
        int opnum1;
        int opnum2;
        int opnum3;
        int opnum4;
        int inmax=0;
        int inmin=0;

        int tenthousands,thousands,hundreds,tens,units;
        int opnum;

        int smsd=0;

        int randop = rand.nextInt(4)+1;
        Log.d(Tag,"Gg");
        //randop=1;
        switch (type) {
            case "+":
                smsd = sum;
                inmax = smsd + 15;
                inmin = smsd - 15;
                break;
            case "*":
                smsd = mult;
                inmax = smsd + 15;
                inmin = smsd - 15;
                break;
            case "-":
                smsd = sub;
                inmax = smsd + 15;
                inmin = smsd - 15;
                break;
            case "/":
                smsd = div;
                inmax = smsd + 15;
                inmin = smsd - 15;
                break;
        }
        switch (randop) {
            case 1:
                opnum2=0;opnum3=0;opnum4=0;
                do {
                    units = smsd % 10;
                    tens = (smsd / 10) % 10;
                    hundreds = (smsd / 100) % 10;
                    thousands= (smsd / 1000) % 10;
                    tenthousands= (smsd / 10000) % 10;
                    int innerrand;
                    int digits = digitCalculate(smsd);
                    Log.d(Tag,digits+": DICK");
                    switch (digits) {
                        case 0:
                        case 1:
                            if(smsd<0)
                            {
                                inmax=0;
                                inmin=-10;
                            }
                            else if(smsd>0)
                            {
                                inmax=10;
                                inmin=0;
                            }
                            else
                            {
                                inmax=5;
                                inmin=-5;
                            }
                            opnum2 = rand.nextInt(inmax-inmin+1)+inmin;
                            opnum3 = rand.nextInt(inmax-inmin+1)+inmin;
                            opnum4 = rand.nextInt(inmax-inmin+1)+inmin;
                            break;
                        case 2:
                            Log.d(Tag,digits+": DICK");
                            innerrand = rand.nextInt(3);
                            units = smsd % 10;
                            tens = (smsd / 10) % 10;
                            if(smsd<0)
                            {
                                inmin=smsd-15;
                                if(smsd>-15)
                                    inmax=0;
                                else
                                    inmax=smsd+15;
                            }
                            else if(smsd>0)
                            {
                                inmax=smsd+15;
                                if(smsd<15)
                                    inmin=0;
                                else
                                    inmin=smsd-15;
                            }
                            else
                            {
                                inmax=5;
                                inmin=-5;
                            }
                            switch (innerrand) {
                                case 0:
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+(tens-2)+1;
                                    opnum2 = tens * 10 + units;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+(tens-2)+1;
                                    units = rand.nextInt((units+2)-(units-2)-1)+(units-2);
                                    opnum3 = tens*10 + units;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+(tens-2)+1;
                                    opnum4 = tens*10 + units;
                                    break;
                                case 1:
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+tens+1;
                                    opnum3 = tens * 10 + units;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+(tens-2)+1;
                                    units = rand.nextInt((units+2)-(units-2)-1)+(units-2)+1;
                                    opnum2 = tens*10 + units;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+(tens-2)+1;
                                    opnum4 = tens*10 + units;
                                    break;
                                case 2:
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+(tens-2);
                                    opnum4 = tens * 10 + units;
                                    opnum3 = rand.nextInt(inmax-inmin+1)+inmin;
                                    opnum2 = rand.nextInt(inmax-inmin+1)+inmin;
                                    break;
                            }
                            break;
                        case 3:
                            innerrand = rand.nextInt(3);
                            switch (innerrand) {
                                case 0:
                                    //all random
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds = rand.nextInt((hundreds+1)-(hundreds-1)-1)+(hundreds-1)+1;
                                    opnum2 = hundreds * 100 + tens * 10 + units;
                                    units = rand.nextInt(10+1)-1;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds = rand.nextInt((hundreds+1)-(hundreds-1)-1)+(hundreds-1)+1;
                                    opnum3 = hundreds * 100 + tens * 10 + units;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds = rand.nextInt((hundreds+1)-(hundreds-1)-1)+(hundreds-1)+1;
                                    opnum4 = hundreds * 100 + tens * 10 + units;
                                    break;
                                case 1:
                                    //unit constant
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds = rand.nextInt((hundreds+1)-(hundreds-1)-1)+(hundreds-1)+1;
                                    opnum4 = hundreds * 100 + tens * 10 + units;
                                    units = rand.nextInt(10+1)-1;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds=(smsd/100)%10;
                                    opnum2 = hundreds * 100 + tens * 10 + units;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds = rand.nextInt((hundreds+1)-(hundreds-1)-1)+(hundreds-1)+1;
                                    opnum3 = hundreds * 100 + tens * 10 + units;
                                    break;
                                case 2:
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds = rand.nextInt((hundreds+1)-(hundreds-1)-1)+(hundreds-1)+1;
                                    opnum3 = hundreds * 100 + tens * 10 + units;
                                    hundreds=(smsd/100)%10;
                                    units = rand.nextInt(10+1)-1;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    //hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                                    opnum2 = hundreds * 100 + tens * 10 + units;
                                    units = rand.nextInt(10+1)-1;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                                    opnum4 = hundreds * 100 + tens * 10 + units;
                                    break;
                            }
                            break;
                        case 4:
                            //units = rand.nextInt(10-1+1);
                            //tens = rand.nextInt(10-1)+1;
                            hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                            thousands = rand.nextInt((thousands+2)-(thousands-2)-1)+(thousands-2)+1;
                            opnum2 = thousands*1000 + hundreds * 100 + tens * 10 + units;
                            //units = rand.nextInt(10-1+1);
                            tens = rand.nextInt(10-1)+1;
                            hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                            thousands = rand.nextInt((thousands+2)-(thousands-2)-1)+(thousands-2)+1;
                            opnum3 = thousands*1000 + hundreds * 100 + tens * 10 + units;
                            //units = rand.nextInt(10-1+1);
                            tens = rand.nextInt(10-1)+1;
                            hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                            thousands = rand.nextInt((thousands+2)-(thousands-2)-1)+(thousands-2)+1;
                            opnum4 = thousands*1000 + hundreds * 100 + tens * 10 + units;
                            break;
                        case 5:
                            op1.setTextSize(23f);
                            op2.setTextSize(23f);
                            op3.setTextSize(23f);
                            op4.setTextSize(23f);
                            //units = rand.nextInt(10-1+1);
                            //tens = rand.nextInt(10-1)+1;
                            hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                            thousands = rand.nextInt((thousands+2)-(thousands-2)-1)+(thousands-2)+1;
                            tenthousands = rand.nextInt((tenthousands+2)-(tenthousands-2)-1)+(tenthousands-2)+1;
                            opnum2 = tenthousands*10000 + thousands*1000 + hundreds * 100 + tens * 10 + units;
                            //units = rand.nextInt(10-1+1);
                            //tens = rand.nextInt(10-1)+1;
                            tens = rand.nextInt(10-1)+1;
                            hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                            thousands = rand.nextInt((thousands+2)-(thousands-2)-1)+(thousands-2)+1;
                            tenthousands = rand.nextInt((tenthousands+2)-(tenthousands-2)-1)+(tenthousands-2)+1;
                            opnum3 = tenthousands*10000 + thousands*1000 + hundreds * 100 + tens * 10 + units;
                            //units = rand.nextInt(10-1+1);
                            //tens = rand.nextInt(10-1)+1;
                            hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                            thousands = rand.nextInt((thousands+2)-(thousands-2)-1)+(thousands-2)+1;
                            tenthousands = rand.nextInt((tenthousands+2)-(tenthousands-2)-1)+(tenthousands-2)+1;
                            opnum4 = tenthousands*10000 + thousands*1000 + hundreds * 100 + tens * 10 + units;
                            break;
                    }
                }while(opnum2==opnum3 || opnum3==opnum4 || opnum2==opnum4 || opnum2==smsd || opnum3==smsd || opnum4==smsd);
                /*do
                {
                    opnum2=rand.nextInt(inmax-inmin+1)+inmin;
                    opnum3=rand.nextInt(inmax-inmin+1)+inmin;
                    opnum4=rand.nextInt(inmax-inmin+1)+inmin;
                }while(opnum2==opnum3 || opnum3==opnum4 || opnum2==opnum4 || opnum2==smsd || opnum3==smsd || opnum4==smsd);*/
                op2.setText(""+opnum2);
                op3.setText(""+opnum3);
                op4.setText(""+opnum4);
                op1.setText(""+smsd);
                break;
            case 2:
                opnum1=0;opnum3=0;opnum4=0;
                do {
                    units = smsd % 10;
                    tens = (smsd / 10) % 10;
                    hundreds = (smsd / 100) % 10;
                    thousands= (smsd / 1000) % 10;
                    tenthousands= (smsd / 10000) % 10;
                    int innerrand;
                    int digits = digitCalculate(smsd);
                    Log.d(Tag,digits+": DICK");
                    switch (digits) {
                        case 0:
                        case 1:
                            if(smsd<0)
                            {
                                inmax=0;
                                inmin=-10;
                            }
                            else if(smsd>0)
                            {
                                inmax=10;
                                inmin=0;
                            }
                            else
                            {
                                inmax=5;
                                inmin=-5;
                            }
                            opnum1 = rand.nextInt(inmax-inmin+1)+inmin;
                            opnum3 = rand.nextInt(inmax-inmin+1)+inmin;
                            opnum4 = rand.nextInt(inmax-inmin+1)+inmin;
                            break;
                        case 2:
                            Log.d(Tag,digits+": DICK");
                            innerrand = rand.nextInt(3);
                            units = smsd % 10;
                            tens = (smsd / 10) % 10;
                            if(smsd<0)
                            {
                                inmin=smsd-15;
                                if(smsd>-15)
                                    inmax=0;
                                else
                                    inmax=smsd+15;
                            }
                            else if(smsd>0)
                            {
                                inmax=smsd+15;
                                if(smsd<15)
                                    inmin=0;
                                else
                                    inmin=smsd-15;
                            }
                            else
                            {
                                inmax=5;
                                inmin=-5;
                            }
                            switch (innerrand) {
                                case 0:
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+(tens-2)+1;
                                    opnum1 = tens * 10 + units;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+(tens-2)+1;
                                    units = rand.nextInt((units+2)-(units-2)-1)+(units-2);
                                    opnum3 = tens*10 + units;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+(tens-2)+1;
                                    opnum4 = tens*10 + units;
                                    break;
                                case 1:
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+tens+1;
                                    opnum3 = tens * 10 + units;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+(tens-2)+1;
                                    units = rand.nextInt((units+2)-(units-2)-1)+(units-2)+1;
                                    opnum1 = tens*10 + units;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+(tens-2)+1;
                                    opnum4 = tens*10 + units;
                                    break;
                                case 2:
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+(tens-2);
                                    opnum4 = tens * 10 + units;
                                    opnum3 = rand.nextInt(inmax-inmin+1)+inmin;
                                    opnum1 = rand.nextInt(inmax-inmin+1)+inmin;
                                    break;
                            }
                            break;
                        case 3:
                            innerrand = rand.nextInt(3);
                            switch (innerrand) {
                                case 0:
                                    //all random
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds = rand.nextInt((hundreds+1)-(hundreds-1)-1)+(hundreds-1)+1;
                                    opnum1 = hundreds * 100 + tens * 10 + units;
                                    units = rand.nextInt(10+1)-1;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds = rand.nextInt((hundreds+1)-(hundreds-1)-1)+(hundreds-1)+1;
                                    opnum3 = hundreds * 100 + tens * 10 + units;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds = rand.nextInt((hundreds+1)-(hundreds-1)-1)+(hundreds-1)+1;
                                    opnum4 = hundreds * 100 + tens * 10 + units;
                                    break;
                                case 1:
                                    //unit constant
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds = rand.nextInt((hundreds+1)-(hundreds-1)-1)+(hundreds-1)+1;
                                    opnum4 = hundreds * 100 + tens * 10 + units;
                                    units = rand.nextInt(10+1)-1;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds=(smsd/100)%10;
                                    opnum1 = hundreds * 100 + tens * 10 + units;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds = rand.nextInt((hundreds+1)-(hundreds-1)-1)+(hundreds-1)+1;
                                    opnum3 = hundreds * 100 + tens * 10 + units;
                                    break;
                                case 2:
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds = rand.nextInt((hundreds+1)-(hundreds-1)-1)+(hundreds-1)+1;
                                    opnum3 = hundreds * 100 + tens * 10 + units;
                                    hundreds=(smsd/100)%10;
                                    units = rand.nextInt(10+1)-1;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    //hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                                    opnum1 = hundreds * 100 + tens * 10 + units;
                                    units = rand.nextInt(10+1)-1;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                                    opnum4 = hundreds * 100 + tens * 10 + units;
                                    break;
                            }
                            break;
                        case 4:
                            //units = rand.nextInt(10-1+1);
                            //tens = rand.nextInt(10-1)+1;
                            hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                            thousands = rand.nextInt((thousands+2)-(thousands-2)-1)+(thousands-2)+1;
                            opnum1 = thousands*1000 + hundreds * 100 + tens * 10 + units;
                            //units = rand.nextInt(10-1+1);
                            tens = rand.nextInt(10-1)+1;
                            hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                            thousands = rand.nextInt((thousands+2)-(thousands-2)-1)+(thousands-2)+1;
                            opnum3 = thousands*1000 + hundreds * 100 + tens * 10 + units;
                            //units = rand.nextInt(10-1+1);
                            tens = rand.nextInt(10-1)+1;
                            hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                            thousands = rand.nextInt((thousands+2)-(thousands-2)-1)+(thousands-2)+1;
                            opnum4 = thousands*1000 + hundreds * 100 + tens * 10 + units;
                            break;
                        case 5:
                            op1.setTextSize(23f);
                            op2.setTextSize(23f);
                            op3.setTextSize(23f);
                            op4.setTextSize(23f);
                            //units = rand.nextInt(10-1+1);
                            //tens = rand.nextInt(10-1)+1;
                            hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                            thousands = rand.nextInt((thousands+2)-(thousands-2)-1)+(thousands-2)+1;
                            tenthousands = rand.nextInt((tenthousands+2)-(tenthousands-2)-1)+(tenthousands-2)+1;
                            opnum1 = tenthousands*10000 + thousands*1000 + hundreds * 100 + tens * 10 + units;
                            //units = rand.nextInt(10-1+1);
                            //tens = rand.nextInt(10-1)+1;
                            tens = rand.nextInt(10-1)+1;
                            hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                            thousands = rand.nextInt((thousands+2)-(thousands-2)-1)+(thousands-2)+1;
                            tenthousands = rand.nextInt((tenthousands+2)-(tenthousands-2)-1)+(tenthousands-2)+1;
                            opnum3 = tenthousands*10000 + thousands*1000 + hundreds * 100 + tens * 10 + units;
                            //units = rand.nextInt(10-1+1);
                            //tens = rand.nextInt(10-1)+1;
                            hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                            thousands = rand.nextInt((thousands+2)-(thousands-2)-1)+(thousands-2)+1;
                            tenthousands = rand.nextInt((tenthousands+2)-(tenthousands-2)-1)+(tenthousands-2)+1;
                            opnum4 = tenthousands*10000 + thousands*1000 + hundreds * 100 + tens * 10 + units;
                            break;
                    }
                }while(opnum1==opnum3 || opnum3==opnum4 || opnum1==opnum4 || opnum1==smsd || opnum3==smsd || opnum4==smsd);
                op1.setText(""+opnum1);
                op3.setText(""+opnum3);
                op4.setText(""+opnum4);
                op2.setText(""+smsd);
                break;
            case 3:
                opnum1=0;opnum2=0;opnum4=0;
                do {
                    units = smsd % 10;
                    tens = (smsd / 10) % 10;
                    hundreds = (smsd / 100) % 10;
                    thousands= (smsd / 1000) % 10;
                    tenthousands= (smsd / 10000) % 10;
                    int innerrand;
                    int digits = digitCalculate(smsd);
                    Log.d(Tag,digits+": DICK");
                    switch (digits) {
                        case 0:
                        case 1:
                            if(smsd<0)
                            {
                                inmax=0;
                                inmin=-10;
                            }
                            else if(smsd>0)
                            {
                                inmax=10;
                                inmin=0;
                            }
                            else
                            {
                                inmax=5;
                                inmin=-5;
                            }
                            opnum1 = rand.nextInt(inmax-inmin+1)+inmin;
                            opnum2 = rand.nextInt(inmax-inmin+1)+inmin;
                            opnum4 = rand.nextInt(inmax-inmin+1)+inmin;
                            break;
                        case 2:
                            Log.d(Tag,digits+": DICK");
                            innerrand = rand.nextInt(3);
                            units = smsd % 10;
                            tens = (smsd / 10) % 10;
                            if(smsd<0)
                            {
                                inmin=smsd-15;
                                if(smsd>-15)
                                    inmax=0;
                                else
                                    inmax=smsd+15;
                            }
                            else if(smsd>0)
                            {
                                inmax=smsd+15;
                                if(smsd<15)
                                    inmin=0;
                                else
                                    inmin=smsd-15;
                            }
                            else
                            {
                                inmax=5;
                                inmin=-5;
                            }
                            switch (innerrand) {
                                case 0:
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+(tens-2)+1;
                                    opnum1 = tens * 10 + units;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+(tens-2)+1;
                                    units = rand.nextInt((units+2)-(units-2)-1)+(units-2);
                                    opnum2 = tens*10 + units;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+(tens-2)+1;
                                    opnum4 = tens*10 + units;
                                    break;
                                case 1:
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+tens+1;
                                    opnum2 = tens * 10 + units;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+(tens-2)+1;
                                    units = rand.nextInt((units+2)-(units-2)-1)+(units-2)+1;
                                    opnum1 = tens*10 + units;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+(tens-2)+1;
                                    opnum4 = tens*10 + units;
                                    break;
                                case 2:
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+(tens-2);
                                    opnum4 = tens * 10 + units;
                                    opnum2 = rand.nextInt(inmax-inmin+1)+inmin;
                                    opnum1 = rand.nextInt(inmax-inmin+1)+inmin;
                                    break;
                            }
                            break;
                        case 3:
                            innerrand = rand.nextInt(3);
                            switch (innerrand) {
                                case 0:
                                    //all random
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds = rand.nextInt((hundreds+1)-(hundreds-1)-1)+(hundreds-1)+1;
                                    opnum1 = hundreds * 100 + tens * 10 + units;
                                    units = rand.nextInt(10+1)-1;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds = rand.nextInt((hundreds+1)-(hundreds-1)-1)+(hundreds-1)+1;
                                    opnum2 = hundreds * 100 + tens * 10 + units;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds = rand.nextInt((hundreds+1)-(hundreds-1)-1)+(hundreds-1)+1;
                                    opnum4 = hundreds * 100 + tens * 10 + units;
                                    break;
                                case 1:
                                    //unit constant
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds = rand.nextInt((hundreds+1)-(hundreds-1)-1)+(hundreds-1)+1;
                                    opnum4 = hundreds * 100 + tens * 10 + units;
                                    units = rand.nextInt(10+1)-1;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds=(smsd/100)%10;
                                    opnum1 = hundreds * 100 + tens * 10 + units;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds = rand.nextInt((hundreds+1)-(hundreds-1)-1)+(hundreds-1)+1;
                                    opnum2 = hundreds * 100 + tens * 10 + units;
                                    break;
                                case 2:
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds = rand.nextInt((hundreds+1)-(hundreds-1)-1)+(hundreds-1)+1;
                                    opnum2 = hundreds * 100 + tens * 10 + units;
                                    hundreds=(smsd/100)%10;
                                    units = rand.nextInt(10+1)-1;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    //hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                                    opnum1 = hundreds * 100 + tens * 10 + units;
                                    units = rand.nextInt(10+1)-1;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                                    opnum4 = hundreds * 100 + tens * 10 + units;
                                    break;
                            }
                            break;
                        case 4:
                            //units = rand.nextInt(10-1+1);
                            //tens = rand.nextInt(10-1)+1;
                            hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                            thousands = rand.nextInt((thousands+2)-(thousands-2)-1)+(thousands-2)+1;
                            opnum1 = thousands*1000 + hundreds * 100 + tens * 10 + units;
                            //units = rand.nextInt(10-1+1);
                            tens = rand.nextInt(10-1)+1;
                            hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                            thousands = rand.nextInt((thousands+2)-(thousands-2)-1)+(thousands-2)+1;
                            opnum2 = thousands*1000 + hundreds * 100 + tens * 10 + units;
                            //units = rand.nextInt(10-1+1);
                            tens = rand.nextInt(10-1)+1;
                            hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                            thousands = rand.nextInt((thousands+2)-(thousands-2)-1)+(thousands-2)+1;
                            opnum4 = thousands*1000 + hundreds * 100 + tens * 10 + units;
                            break;
                        case 5:
                            op1.setTextSize(23f);
                            op2.setTextSize(23f);
                            op3.setTextSize(23f);
                            op4.setTextSize(23f);
                            //units = rand.nextInt(10-1+1);
                            //tens = rand.nextInt(10-1)+1;
                            hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                            thousands = rand.nextInt((thousands+2)-(thousands-2)-1)+(thousands-2)+1;
                            tenthousands = rand.nextInt((tenthousands+2)-(tenthousands-2)-1)+(tenthousands-2)+1;
                            opnum1 = tenthousands*10000 + thousands*1000 + hundreds * 100 + tens * 10 + units;
                            //units = rand.nextInt(10-1+1);
                            //tens = rand.nextInt(10-1)+1;
                            tens = rand.nextInt(10-1)+1;
                            hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                            thousands = rand.nextInt((thousands+2)-(thousands-2)-1)+(thousands-2)+1;
                            tenthousands = rand.nextInt((tenthousands+2)-(tenthousands-2)-1)+(tenthousands-2)+1;
                            opnum2 = tenthousands*10000 + thousands*1000 + hundreds * 100 + tens * 10 + units;
                            //units = rand.nextInt(10-1+1);
                            //tens = rand.nextInt(10-1)+1;
                            hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                            thousands = rand.nextInt((thousands+2)-(thousands-2)-1)+(thousands-2)+1;
                            tenthousands = rand.nextInt((tenthousands+2)-(tenthousands-2)-1)+(tenthousands-2)+1;
                            opnum4 = tenthousands*10000 + thousands*1000 + hundreds * 100 + tens * 10 + units;
                            break;
                    }
                }while(opnum1==opnum2 || opnum2==opnum4 || opnum1==opnum4 || opnum1==smsd || opnum2==smsd || opnum4==smsd);
                op2.setText(""+opnum2);
                op1.setText(""+opnum1);
                op4.setText(""+opnum4);
                op3.setText(""+smsd);
                break;
            case 4:
                opnum1=0;opnum2=0;opnum3=0;
                do {
                    units = smsd % 10;
                    tens = (smsd / 10) % 10;
                    hundreds = (smsd / 100) % 10;
                    thousands= (smsd / 1000) % 10;
                    tenthousands= (smsd / 10000) % 10;
                    int innerrand;
                    int digits = digitCalculate(smsd);
                    Log.d(Tag,digits+": DICK");
                    switch (digits) {
                        case 0:
                        case 1:
                            if(smsd<0)
                            {
                                inmax=0;
                                inmin=-10;
                            }
                            else if(smsd>0)
                            {
                                inmax=10;
                                inmin=0;
                            }
                            else
                            {
                                inmax=5;
                                inmin=-5;
                            }
                            opnum1 = rand.nextInt(inmax-inmin+1)+inmin;
                            opnum2 = rand.nextInt(inmax-inmin+1)+inmin;
                            opnum3 = rand.nextInt(inmax-inmin+1)+inmin;
                            break;
                        case 2:
                            Log.d(Tag,digits+": DICK");
                            innerrand = rand.nextInt(3);
                            units = smsd % 10;
                            tens = (smsd / 10) % 10;
                            if(smsd<0)
                            {
                                inmin=smsd-15;
                                if(smsd>-15)
                                    inmax=0;
                                else
                                    inmax=smsd+15;
                            }
                            else if(smsd>0)
                            {
                                inmax=smsd+15;
                                if(smsd<15)
                                    inmin=0;
                                else
                                    inmin=smsd-15;
                            }
                            else
                            {
                                inmax=5;
                                inmin=-5;
                            }
                            switch (innerrand) {
                                case 0:
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+(tens-2)+1;
                                    opnum1 = tens * 10 + units;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+(tens-2)+1;
                                    units = rand.nextInt((units+2)-(units-2)-1)+(units-2);
                                    opnum2 = tens*10 + units;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+(tens-2)+1;
                                    opnum3 = tens*10 + units;
                                    break;
                                case 1:
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+tens+1;
                                    opnum2 = tens * 10 + units;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+(tens-2)+1;
                                    units = rand.nextInt((units+2)-(units-2)-1)+(units-2)+1;
                                    opnum1 = tens*10 + units;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+(tens-2)+1;
                                    opnum3 = tens*10 + units;
                                    break;
                                case 2:
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+(tens-2);
                                    opnum3 = tens * 10 + units;
                                    opnum2 = rand.nextInt(inmax-inmin+1)+inmin;
                                    opnum1 = rand.nextInt(inmax-inmin+1)+inmin;
                                    break;
                            }
                            break;
                        case 3:
                            innerrand = rand.nextInt(3);
                            switch (innerrand) {
                                case 0:
                                    //all random
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds = rand.nextInt((hundreds+1)-(hundreds-1)-1)+(hundreds-1)+1;
                                    opnum1 = hundreds * 100 + tens * 10 + units;
                                    units = rand.nextInt(10+1)-1;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds = rand.nextInt((hundreds+1)-(hundreds-1)-1)+(hundreds-1)+1;
                                    opnum2 = hundreds * 100 + tens * 10 + units;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds = rand.nextInt((hundreds+1)-(hundreds-1)-1)+(hundreds-1)+1;
                                    opnum3 = hundreds * 100 + tens * 10 + units;
                                    break;
                                case 1:
                                    //unit constant
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds = rand.nextInt((hundreds+1)-(hundreds-1)-1)+(hundreds-1)+1;
                                    opnum3 = hundreds * 100 + tens * 10 + units;
                                    units = rand.nextInt(10+1)-1;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds=(smsd/100)%10;
                                    opnum1 = hundreds * 100 + tens * 10 + units;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds = rand.nextInt((hundreds+1)-(hundreds-1)-1)+(hundreds-1)+1;
                                    opnum2 = hundreds * 100 + tens * 10 + units;
                                    break;
                                case 2:
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds = rand.nextInt((hundreds+1)-(hundreds-1)-1)+(hundreds-1)+1;
                                    opnum2 = hundreds * 100 + tens * 10 + units;
                                    hundreds=(smsd/100)%10;
                                    units = rand.nextInt(10+1)-1;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    //hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                                    opnum1 = hundreds * 100 + tens * 10 + units;
                                    units = rand.nextInt(10+1)-1;
                                    tens = rand.nextInt((tens+2)-(tens-2)-1)+1;
                                    hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                                    opnum3 = hundreds * 100 + tens * 10 + units;
                                    break;
                            }
                            break;
                        case 4:
                            //units = rand.nextInt(10-1+1);
                            //tens = rand.nextInt(10-1)+1;
                            hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                            thousands = rand.nextInt((thousands+2)-(thousands-2)-1)+(thousands-2)+1;
                            opnum1 = thousands*1000 + hundreds * 100 + tens * 10 + units;
                            //units = rand.nextInt(10-1+1);
                            tens = rand.nextInt(10-1)+1;
                            hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                            thousands = rand.nextInt((thousands+2)-(thousands-2)-1)+(thousands-2)+1;
                            opnum2 = thousands*1000 + hundreds * 100 + tens * 10 + units;
                            //units = rand.nextInt(10-1+1);
                            tens = rand.nextInt(10-1)+1;
                            hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                            thousands = rand.nextInt((thousands+2)-(thousands-2)-1)+(thousands-2)+1;
                            opnum3 = thousands*1000 + hundreds * 100 + tens * 10 + units;
                            break;
                        case 5:
                            op1.setTextSize(23f);
                            op2.setTextSize(23f);
                            op3.setTextSize(23f);
                            op4.setTextSize(23f);
                            //units = rand.nextInt(10-1+1);
                            //tens = rand.nextInt(10-1)+1;
                            hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                            thousands = rand.nextInt((thousands+2)-(thousands-2)-1)+(thousands-2)+1;
                            tenthousands = rand.nextInt((tenthousands+2)-(tenthousands-2)-1)+(tenthousands-2)+1;
                            opnum1 = tenthousands*10000 + thousands*1000 + hundreds * 100 + tens * 10 + units;
                            //units = rand.nextInt(10-1+1);
                            //tens = rand.nextInt(10-1)+1;
                            tens = rand.nextInt(10-1)+1;
                            hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                            thousands = rand.nextInt((thousands+2)-(thousands-2)-1)+(thousands-2)+1;
                            tenthousands = rand.nextInt((tenthousands+2)-(tenthousands-2)-1)+(tenthousands-2)+1;
                            opnum2 = tenthousands*10000 + thousands*1000 + hundreds * 100 + tens * 10 + units;
                            //units = rand.nextInt(10-1+1);
                            //tens = rand.nextInt(10-1)+1;
                            hundreds = rand.nextInt((hundreds+2)-(hundreds-2)-1)+(hundreds-2)+1;
                            thousands = rand.nextInt((thousands+2)-(thousands-2)-1)+(thousands-2)+1;
                            tenthousands = rand.nextInt((tenthousands+2)-(tenthousands-2)-1)+(tenthousands-2)+1;
                            opnum3 = tenthousands*10000 + thousands*1000 + hundreds * 100 + tens * 10 + units;
                            break;
                    }
                }while(opnum1==opnum2 || opnum2==opnum3 || opnum1==opnum3 || opnum1==smsd || opnum2==smsd || opnum3==smsd);
                op2.setText(""+opnum2);
                op3.setText(""+opnum3);
                op1.setText(""+opnum1);
                op4.setText(""+smsd);
                break;
        }
    }

    public void checkOptionCorrect()
    {
        final Button op1 = findViewById(R.id.option1);
        final Button op2 = findViewById(R.id.option2);
        final Button op3 = findViewById(R.id.option3);
        final Button op4 = findViewById(R.id.option4);
        //String tempAns;
        char typechar;
        op1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempAns=op1.getText().toString();
                r=Integer.parseInt(tempAns);
                Compare(type);
                NextCombo(0,type);}
        });

        op2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempAns=op2.getText().toString();
                r=Integer.parseInt(tempAns);
                Compare(type);
                NextCombo(0,type);}
        });

        op3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempAns=op3.getText().toString();
                r=Integer.parseInt(tempAns);
                Compare(type);
                NextCombo(0,type);}
        });

        op4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempAns=op4.getText().toString();
                r=Integer.parseInt(tempAns);
                Compare(type);
                NextCombo(0,type); }
        });
    }

    public void AddSubMultDiv(final String op)
    {
        final TextView A= findViewById(R.id.display);
        final TextView B= findViewById(R.id.display2);
        final TextView Res= findViewById(R.id.display3);
        final TextView operator= findViewById(R.id.operator);
        TextView hs=findViewById(R.id.highscore);
        Button next=findViewById(R.id.next);

        rand=new Random();
        num1=rand.nextInt(10);
        num2=rand.nextInt(10);
        A.setText(""+num1);
        B.setText(""+num2);
        Res.setText("Try It!");
        switch(op) {
            case "+":sum = num1 + num2;
                break;
            case "*":mult = num1 * num2;
                break;
            case "-":sub = num1 - num2;
                break;
            case "/":div = num1 / (num2+1);
                break;
        }
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NextCombo(1, op);
            }
        });
        operator.setText(op);
        OptionGenerator();
        checkOptionCorrect();
    }

    public void Compare(String operator)
    {
        TextView Res= findViewById(R.id.display3);
        TextView sc= findViewById(R.id.score);
        TextView bonusTime= findViewById(R.id.bonusTime);
        boolean notEqual=false;

        switch(operator) {
            case "+":
                if (r == sum) {
                    bonusTime.setVisibility(View.VISIBLE);
                    lvlcount++;
                    if (lvl == 1) {
                        score++;
                        timeLeft += 2000;
                        bonusTime.setText("+2");
                    } else if (lvl == 2) {
                        score += 2;
                        timeLeft += 2000;
                        bonusTime.setText("+2");
                    } else if (lvl == 3) {
                        score += 2;
                        timeLeft += 3000;
                        bonusTime.setText("+3");
                    }
                    else if (lvl == 4) {
                        score += 3;
                        timeLeft += 3000;
                        bonusTime.setText("+3");
                    }
                    else if (lvl == 5) {
                        score += 4;
                        timeLeft += 3000;
                        bonusTime.setText("+3");
                    }
                    else if (lvl == 6) {
                        score += 5;
                        timeLeft += 3000;
                        bonusTime.setText("+3");
                    }
                    else
                    {
                        Log.d(Tag,"GG: compare"+lvl+":"+score);
                        score+=5;
                        timeLeft += 3000;
                        bonusTime.setText("+3");
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

            case "*":
                if (r == mult) {
                    bonusTime.setVisibility(View.VISIBLE);
                    lvlcount++;
                    if (lvl == 1) {
                        score++;
                        timeLeft += 2000;
                        bonusTime.setText("+2");
                    } else if (lvl == 2) {
                        score += 2;
                        timeLeft += 2000;
                        bonusTime.setText("+2");
                    } else if (lvl == 3) {
                        score += 2;
                        timeLeft += 3000;
                        bonusTime.setText("+3");
                    }
                    else if (lvl == 4) {
                        score += 3;
                        timeLeft += 3000;
                        bonusTime.setText("+3");
                    }
                    else if (lvl == 5) {
                        score += 4;
                        timeLeft += 3000;
                        bonusTime.setText("+3");
                    }
                    else if (lvl == 6) {
                        score += 5;
                        timeLeft += 3000;
                        bonusTime.setText("+3");
                    }
                    else
                    {
                        score += 5;
                        timeLeft += 3000;
                        bonusTime.setText("+3");
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

            case "-":
                if (r == sub) {
                    bonusTime.setVisibility(View.VISIBLE);
                    lvlcount++;
                    if (lvl == 1) {
                        score++;
                        timeLeft += 2000;
                        bonusTime.setText("+2");
                    } else if (lvl == 2) {
                        score += 2;
                        timeLeft += 2000;
                        bonusTime.setText("+2");
                    } else if (lvl == 3) {
                        score += 2;
                        timeLeft += 3000;
                        bonusTime.setText("+3");
                    }
                    else if (lvl == 4) {
                        score += 3;
                        timeLeft += 3000;
                        bonusTime.setText("+3");
                    }
                    else if (lvl == 5) {
                        score += 4;
                        timeLeft += 3000;
                        bonusTime.setText("+3");
                    }
                    else if (lvl == 6) {
                        score += 5;
                        timeLeft += 3000;
                        bonusTime.setText("+3");
                    }
                    else
                    {
                        score+=5;
                        timeLeft += 3000;
                        bonusTime.setText("+3");
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
            case "/":
                if (r == div) {
                    bonusTime.setVisibility(View.VISIBLE);
                    lvlcount++;
                    if (lvl == 1) {
                        score++;
                        timeLeft += 2000;
                        bonusTime.setText("+2");
                    } else if (lvl == 2) {
                        score += 2;
                        timeLeft += 2000;
                        bonusTime.setText("+2");
                    } else if (lvl == 3) {
                        score += 2;
                        timeLeft += 3000;
                        bonusTime.setText("+3");
                    }
                    else if (lvl == 4) {
                        score += 3;
                        timeLeft += 3000;
                        bonusTime.setText("+3");
                    }
                    else if (lvl == 5) {
                        score += 4;
                        timeLeft += 3000;
                        bonusTime.setText("+3");
                    }
                    else if (lvl == 6) {
                        score += 5;
                        timeLeft += 3000;
                        bonusTime.setText("+3");
                    }
                    else
                    {
                        score+=5;
                        timeLeft += 3000;
                        bonusTime.setText("+3");
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
        FillProgress();
    }

    public void NextCombo(int nextPressed,String operator)
    {
        int max=10,min=0;
        TextView A= findViewById(R.id.display);
        TextView B= findViewById(R.id.display2);
        TextView bonusTime= findViewById(R.id.bonusTime);

        switch(operator) {
            case "+":
                if (lvlcount < lvlsetter1) {
                    min = 1;
                    max = 5;
                    lvl = 1;
                } else if (lvlcount < lvlsetter2) {
                    min = 5;
                    max = 20;
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
                else
                {
                    Log.d(Tag,"GG: else");
                    min = 100;
                    max = 300;
                    lvl = 7;
                }
                num1 = rand.nextInt(max - min + 1) + min;
                num2 = rand.nextInt(max - min + 1) + min;
                A.setText("" + num1);
                B.setText("" + num2);
                sum = num1 + num2;
                break;

            case "*":
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
                else
                {
                    Log.d(Tag,"GG: else");
                    min = 100;
                    max = 300;
                    num1 = rand.nextInt(max - min + 1) + min;
                    num2 = rand.nextInt(max - min + 1) + min;
                    lvl = 7;
                }
                A.setText("" + num1);
                B.setText("" + num2);
                mult = num1 * num2;
                break;

            case "-":
                if (lvlcount < lvlsetter1) {
                    min = 1;
                    max = 5;
                    lvl = 1;
                } else if (lvlcount < lvlsetter2) {
                    min = 5;
                    max = 20;
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
                else
                {
                    min = 100;
                    max = 300;
                    lvl = 7;
                }
                num1 = rand.nextInt(max - min + 1) + min;
                num2 = rand.nextInt(max - min + 1) + min;
                A.setText("" + num1);
                B.setText("" + num2);
                sub = num1 - num2;
                break;

            case "/":
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
                else
                {
                    min = 100;
                    max = 300;
                    num1 = rand.nextInt(max - min + 1) + min;
                    num2 = rand.nextInt(max - min + 1) + min;
                    lvl = 7;
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
        OptionGenerator();
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
        if(sec<=5)
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
                        Intent intent = new Intent(MultipleChoiceScreen.this, HomeScreen.class);
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
        Firebase mref = new Firebase("https://fragsout.firebaseio.com/");
        if(score>HighScore)
        {
            HighScore=score;
            SharedPreferences prefs = this.getSharedPreferences("myKey", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(HighScoreString,HighScore);
            editor.commit();
        }
        mref.child("Leaderboard").child(""+HighScoreString).child(""+HomeScreen.Name).child("PlayerScore").setValue(""+HighScore);
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
        else
        {
            inlvl = 7;
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
        if(inlvl<7)
            minilvl.setText("Level: "+inlvl);
        else
            minilvl.setText("Level: ENDLESS");

        progresslvl.setProgress(progress);

    }
}
