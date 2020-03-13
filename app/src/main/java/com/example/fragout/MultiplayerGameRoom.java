package com.example.fragout;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.Locale;
import java.util.Random;

public class MultiplayerGameRoom extends AppCompatActivity {

    private static final String Tag = MainActivity.class.getSimpleName();

    Firebase mref;
    String Id;

    public static final String priority = "Key1";
    public static final String HS_TYPE_MESSAGE = "Key2";

    int num1 = 10, num2 = 20, sum = 0, mult = 0, sub = 0, div = 0, r = 0, score = 0;
    Random rand;
    long timeLeft = 60000;
    long startcount = 10000;

    int lvlcount = 0;

    int lvlsetter1 = 1;
    int lvlsetter2 = 3;
    int lvlsetter3 = 7;
    int lvlsetter4 = 10;
    int lvlsetter5 = 14;
    int lvlsetter6 = 17;

    int lvl = 1;

    String type;
    CountDownTimer cdg;
    CountDownTimer xcd;

    boolean started = false;
    boolean connected = false;
    boolean host = false;

    CountDownTimer subcd;
    long subtime = 7000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_game_room);
        TextView coverleft = findViewById(R.id.cover1);
        TextView startcountdownvar = findViewById(R.id.startcountdown);

        final TextView A = findViewById(R.id.display);
        final TextView B = findViewById(R.id.display2);
        final TextView Res = findViewById(R.id.display3);
        TextView bonusTime = findViewById(R.id.bonusTime);
        final TextView operator = findViewById(R.id.operator);

        final Button next = findViewById(R.id.next);
        final Button start = findViewById(R.id.start);
        final Button op1 = findViewById(R.id.option1);
        final Button op2 = findViewById(R.id.option2);
        final Button op3 = findViewById(R.id.option3);
        final Button op4 = findViewById(R.id.option4);

        A.setVisibility(View.INVISIBLE);
        B.setVisibility(View.INVISIBLE);
        operator.setVisibility(View.INVISIBLE);
        Res.setVisibility(View.INVISIBLE);
        bonusTime.setVisibility(View.INVISIBLE);
        next.setVisibility(View.INVISIBLE);
        op1.setVisibility(View.INVISIBLE);
        op2.setVisibility(View.INVISIBLE);
        op3.setVisibility(View.INVISIBLE);
        op4.setVisibility(View.INVISIBLE);

        View oppview = findViewById(R.id.rightoppview);
        oppview.setVisibility(View.INVISIBLE);


        getID();

        //MakeInviCD(startcountdownvar,startcount);

        updateScoreFirebase();

        xcd = WaitingLimit(10000);

        waitForPlayer();

        //MakeInviCD(startcountdownvar,startcount);

        //UpdateScore();

        //operatorRandomizer();

    }

    ValueEventListener waitingListener;

    void waitForPlayer() {
        final TextView startcountdownvar = findViewById(R.id.startcountdown);
        mref = new Firebase("https://fragsout.firebaseio.com/");
        waitingListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String gameroomid = (String) dataSnapshot.getValue();
                String oppId = "";
                String[] gameroomsplit = gameroomid.split("-");
                if (gameroomsplit[0].equals(Id)) {
                    oppId = gameroomsplit[1];
                } else {
                    oppId = gameroomsplit[0];
                }
                final String finalOppId = oppId;
                mref.child("GameRoom").child(gameroomid + "").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(finalOppId + "")) {
                            xcd.cancel();

                            MakeInviCD(startcountdownvar, startcount);

                            UpdateScore();

                            operatorRandomizer();

                            ShowOpponentScore();
                            mref.child("GameRoom").child(gameroomid + "").removeEventListener(this);
                        } else {
                            startcountdownvar.setText("Waiting For Players");
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
                mref.child("Multiplayer").child(Id + "").child("GameRoomid").removeEventListener(this);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
        mref.child("Multiplayer").child(Id + "").child("GameRoomid").addValueEventListener(waitingListener);
    }

    CountDownTimer WaitingLimit(long time) {
        CountDownTimer cd = new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long l) {

            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(MultiplayerGameRoom.this,MultiplayerOptionRoom.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra(priority,"Moving To Higher Priority");
                startActivity(intent);
            }
        }.start();
        return cd;
    }

    public void operatorRandomizer() {
        rand = new Random();
        int x = rand.nextInt(3);
        switch (x) {
            case 0:
                checkMessage("+");
                break;
            case 1:
                checkMessage("*");
                break;
            case 2:
                checkMessage("-");
                break;
            //case 3:checkMessage("/");break;
        }
    }

    public void checkMessage(String op) {
        String message = op;
        type = message;
        AddSubMultDiv(message);
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
        op1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempAns = op1.getText().toString();
                r = Integer.parseInt(tempAns);
                Compare(type);
                operatorRandomizer();
                NextCombo(0, type);
            }
        });

        op2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempAns = op2.getText().toString();
                r = Integer.parseInt(tempAns);
                Compare(type);
                operatorRandomizer();
                NextCombo(0, type);
            }
        });

        op3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempAns = op3.getText().toString();
                r = Integer.parseInt(tempAns);
                Compare(type);
                operatorRandomizer();
                NextCombo(0, type);
            }
        });

        op4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempAns = op4.getText().toString();
                r = Integer.parseInt(tempAns);
                Compare(type);
                operatorRandomizer();
                NextCombo(0, type);
            }
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
        boolean Equal=false;
        boolean notEqual=false;

        switch(operator) {
            case "+":
                if (r == sum) {
                    lvlcount++;
                    //bonusTime.setTextColor(Color.parseColor("#008577"));
                    Equal=true;
                } else {
                    notEqual=true;
                }
                break;

            case "*":
                if (r == mult) {
                    lvlcount++;
                    Equal=true;
                } else {
                    notEqual=true;
                }
                break;

            case "-":
                if (r == sub) {
                    lvlcount++;
                    Equal=true;
                } else {
                    notEqual=true;
                }
                break;
            case "/":
                if (r == div) {
                    lvlcount++;
                    Equal=true;
                } else {
                    notEqual=true;
                }
                break;
        }
        if(Equal)
        {
            bonusTime.setTextColor(Color.parseColor("#008577"));
            bonusTime.setVisibility(View.VISIBLE);
            if (lvl == 1) {
                score++;
                bonusTime.setText("+1");
            } else if (lvl == 2) {
                score += 2;
                bonusTime.setText("+2");
            } else if (lvl == 3) {
                score += 3;
                bonusTime.setText("+3");
            }
            else if (lvl == 4) {
                score += 4;
                bonusTime.setText("+4");
            }
            else if (lvl == 5) {
                score += 4;
                bonusTime.setText("+4");
            }
            else if (lvl == 6) {
                score += 5;
                bonusTime.setText("+5");
            }
            else {
                score+=6;
                bonusTime.setText("+6");
            }
            if(!subended)
            {
                MakeInviCD(bonusTime,2000);
                Res.setText("Correct!");
                opponentQuestion();
            }
        }
        else if(notEqual)
        {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(200);
            score-=2;
            Res.setText("God Save You!");
        }
        resetSubTime();
        resetSubCount();
        sc.setText("" + score);
    }

    public void NextCombo(int nextPressed,String operator)
    {
        int max=10,min=0;
        TextView A= findViewById(R.id.display);
        TextView B= findViewById(R.id.display2);
        TextView bonusTime= findViewById(R.id.bonusTime);

        int tempnum1=num1;
        int tempnum2=num2;

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
                    min=100;
                    max=200;
                    lvl = 7;
                }
                do {
                    num1 = rand.nextInt(max - min + 1) + min;
                    num2 = rand.nextInt(max - min + 1) + min;
                }
                while(tempnum1==num1 && tempnum2==num2);
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
                else {
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
                    min=100;
                    max=200;
                    lvl = 7;
                }
                do {
                    num1 = rand.nextInt(max - min + 1) + min;
                    num2 = rand.nextInt(max - min + 1) + min;
                }
                while(tempnum1==num1 && tempnum2==num2);
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
                else {
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
        MakeInviCD(bonusTime,1500);
        updateScoreFirebase();
        OptionGenerator();
    }

    //Opponent Shit

    int sumopp=0,divopp=0,subopp=0,multopp=0,num1opp,num2opp,ropp=0;
    String typeopp="";
    public void AddSubMultDivopp(final String op)
    {
        final TextView A= findViewById(R.id.displayopp);
        final TextView B= findViewById(R.id.display2opp);
        final TextView operator= findViewById(R.id.operatoropp);

        rand=new Random();
        num1opp=rand.nextInt(10);
        num2opp=rand.nextInt(10);
        A.setText(""+num1opp);
        B.setText(""+num2opp);
        switch(op) {
            case "+":sumopp = num1opp + num2opp;
                break;
            case "*":multopp = num1opp * num2opp;
                break;
            case "-":subopp = num1opp - num2opp;
                break;
            case "/":divopp = num1opp / (num2opp+1);
                break;
        }
        operator.setText(op);
        OptionGeneratoropp();
        checkOptionCorrectopp();
    }

    public void checkMessageopp(String op)
    {
        String message=op;
        typeopp=message;
        AddSubMultDivopp(message);
    }

    public void OptionGeneratoropp() {
        Button op1 = findViewById(R.id.option1opp);
        Button op2 = findViewById(R.id.option2opp);
        Button op3 = findViewById(R.id.option3opp);
        Button op4 = findViewById(R.id.option4opp);
        int opnum1;
        int opnum2;
        int opnum3;
        int opnum4;
        int inmax=0;
        int inmin=0;

        int smsd=0;

        int randop = rand.nextInt(4)+1;

        switch (typeopp) {
            case "+":
                smsd = sumopp;
                inmax = smsd + 15;
                inmin = smsd - 15;
                break;
            case "*":
                smsd = multopp;
                inmax = smsd + 15;
                inmin = smsd - 15;
                break;
            case "-":
                smsd = subopp;
                inmax = smsd + 15;
                inmin = smsd - 15;
                break;
            case "/":
                smsd = divopp;
                inmax = smsd + 15;
                inmin = smsd - 15;
                break;
        }
        switch (randop) {
            case 1:
                do
                {
                    opnum2=rand.nextInt(inmax-inmin+1)+inmin;
                    opnum3=rand.nextInt(inmax-inmin+1)+inmin;
                    opnum4=rand.nextInt(inmax-inmin+1)+inmin;
                }while(opnum2==opnum3 || opnum3==opnum4 || opnum2==opnum4 || opnum2==smsd || opnum3==smsd || opnum4==smsd);
                op1.setText(""+opnum2);
                op3.setText(""+opnum3);
                op4.setText(""+opnum4);
                op1.setText(""+smsd);
                break;
            case 2:
                do
                {
                    opnum1=rand.nextInt(inmax-inmin+1)+inmin;
                    opnum3=rand.nextInt(inmax-inmin+1)+inmin;
                    opnum4=rand.nextInt(inmax-inmin+1)+inmin;
                }while(opnum1==opnum3 || opnum3==opnum4 || opnum1==opnum4 || opnum1==smsd || opnum3==smsd || opnum4==smsd);
                op1.setText(""+opnum1);
                op3.setText(""+opnum3);
                op4.setText(""+opnum4);
                op2.setText(""+smsd);
                break;
            case 3:
                do
                {
                    opnum2=rand.nextInt(inmax-inmin+1)+inmin;
                    opnum1=rand.nextInt(inmax-inmin+1)+inmin;
                    opnum4=rand.nextInt(inmax-inmin+1)+inmin;
                }while(opnum2==opnum1 || opnum1==opnum4 || opnum2==opnum4 || opnum2==smsd || opnum1==smsd || opnum4==smsd);
                op2.setText(""+opnum2);
                op1.setText(""+opnum1);
                op4.setText(""+opnum4);
                op3.setText(""+smsd);
                break;
            case 4:
                do
                {
                    opnum2=rand.nextInt(inmax-inmin+1)+inmin;
                    opnum3=rand.nextInt(inmax-inmin+1)+inmin;
                    opnum1=rand.nextInt(inmax-inmin+1)+inmin;
                }while(opnum2==opnum3 || opnum3==opnum1 || opnum2==opnum1 || opnum2==smsd || opnum3==smsd || opnum1==smsd);
                op2.setText(""+opnum2);
                op3.setText(""+opnum3);
                op1.setText(""+opnum1);
                op4.setText(""+smsd);
                break;
        }
    }

    public void makeOppQuestionInvisible()
    {
        View oppview = findViewById(R.id.rightoppview);
        oppview.setVisibility(View.INVISIBLE);
    }

    public void checkOptionCorrectopp()
    {
        final Button op1 = findViewById(R.id.option1opp);
        final Button op2 = findViewById(R.id.option2opp);
        final Button op3 = findViewById(R.id.option3opp);
        final Button op4 = findViewById(R.id.option4opp);
        op1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempAns = op1.getText().toString();
                ropp = Integer.parseInt(tempAns);
                Compareopp(typeopp);
                NextComboopp(0, typeopp);
                makeOppQuestionInvisible();
            }
        });

        op2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempAns = op2.getText().toString();
                ropp = Integer.parseInt(tempAns);
                Compareopp(typeopp);
                NextComboopp(0, typeopp);
                makeOppQuestionInvisible();
            }
        });

        op3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempAns = op3.getText().toString();
                ropp = Integer.parseInt(tempAns);
                Compareopp(typeopp);
                NextComboopp(0, typeopp);
                makeOppQuestionInvisible();
            }
        });

        op4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tempAns = op4.getText().toString();
                ropp = Integer.parseInt(tempAns);
                Compareopp(typeopp);
                NextComboopp(0, typeopp);
                makeOppQuestionInvisible();
            }
        });
    }

    public void Compareopp(String operator)
    {
        TextView sc= findViewById(R.id.score);
        boolean notEqual=false;
        boolean Equal=false;
        switch(operator) {
            case "+":
                if (ropp == sumopp) {
                    lvlcount++;
                    Equal=true;
                } else {
                    notEqual=true;
                }
                break;

            case "*":
                if (ropp == multopp) {
                    lvlcount++;
                    Equal=true;
                } else {
                    notEqual=true;
                }
                break;

            case "-":
                if (ropp == subopp) {
                    lvlcount++;
                    Equal=true;
                } else {
                    notEqual=true;
                }
                break;
            case "/":
                if (ropp == divopp) {
                    lvlcount++;
                    Equal=true;
                } else {
                    notEqual=true;
                }
                break;
        }
        if(Equal)
        {
            ReduceOppScore();
            /*resetSubTime();
            resetSubCount();
            makeOppQuestionInvisible();*/
            TextView coverright = findViewById(R.id.cover2);
            coverright.setVisibility(View.VISIBLE);
        }
        else if(notEqual)
        {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            vibrator.vibrate(200);
            score-=4;
        }
        sc.setText("" + score);
        updateScoreFirebase();
    }

    public void NextComboopp(int nextPressed,String operator)
    {
        int max=10,min=0;
        TextView A= findViewById(R.id.displayopp);
        TextView B= findViewById(R.id.display2opp);
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
                num1opp = rand.nextInt(max - min + 1) + min;
                num2opp = rand.nextInt(max - min + 1) + min;
                A.setText("" + num1opp);
                B.setText("" + num2opp);
                sumopp = num1opp + num2opp;
                break;

            case "*":
                if(lvlcount<lvlsetter1)
                {
                    min=1;max=5;
                    num1opp = rand.nextInt(max-min+1)+min;
                    num2opp = rand.nextInt(max-min+1)+min;
                    lvl=1;
                }
                else if(lvlcount<lvlsetter2)
                {
                    min=5;max=10;
                    num1opp = rand.nextInt(max-min+1)+min;
                    num2opp = rand.nextInt(max-min+1)+min;
                    lvl=2;
                }
                else if(lvlcount<lvlsetter3) {
                    min=10;max=30;
                    num1opp = rand.nextInt(max-min+1)+min;
                    min=1;max=7;
                    num2opp = rand.nextInt(max-min+1)+min;
                    lvl=3;
                }
                else if(lvlcount<lvlsetter4) {
                    min=10;max=30;
                    num1opp = rand.nextInt(max-min+1)+min;
                    min=3;max=10;
                    num2opp = rand.nextInt(max-min+1)+min;
                    lvl=4;
                }
                else if(lvlcount<lvlsetter5) {
                    min=30;max=50;
                    num1opp = rand.nextInt(max-min+1)+min;
                    min=10;max=20;
                    num2opp = rand.nextInt(max-min+1)+min;
                    lvl=5;
                }
                else if(lvlcount<lvlsetter6) {
                    min = 10;
                    max = 100;
                    num1opp = rand.nextInt(max - min + 1) + min;
                    num2opp = rand.nextInt(max - min + 1) + min;
                    lvl = 6;
                }
                A.setText("" + num1opp);
                B.setText("" + num2opp);
                multopp = num1opp * num2opp;
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
                num1opp = rand.nextInt(max - min + 1) + min;
                num2opp = rand.nextInt(max - min + 1) + min;
                A.setText("" + num1opp);
                B.setText("" + num2opp);
                subopp = num1opp - num2opp;
                break;

            case "/":
                if(lvlcount<lvlsetter1)
                {
                    min=1;max=5;
                    num1opp = rand.nextInt(max-min+1)+min;
                    num2opp = rand.nextInt(max-min+1)+min;
                    lvl=1;
                }
                else if(lvlcount<lvlsetter2)
                {
                    min=5;max=10;
                    num1opp = rand.nextInt(max-min+1)+min;
                    num2opp = rand.nextInt(max-min+1)+min;
                    lvl=2;
                }
                else if(lvlcount<lvlsetter3) {
                    min=10;max=30;
                    num1opp = rand.nextInt(max-min+1)+min;
                    min=1;max=7;
                    num2opp = rand.nextInt(max-min+1)+min;
                    lvl=3;
                }
                else if(lvlcount<lvlsetter4) {
                    min=10;max=30;
                    num1opp = rand.nextInt(max-min+1)+min;
                    min=3;max=10;
                    num2opp = rand.nextInt(max-min+1)+min;
                    lvl=4;
                }
                else if(lvlcount<lvlsetter5) {
                    min=30;max=50;
                    num1opp = rand.nextInt(max-min+1)+min;
                    min=10;max=20;
                    num2opp = rand.nextInt(max-min+1)+min;
                    lvl=5;
                }
                else if(lvlcount<lvlsetter6) {
                    min = 10;
                    max = 100;
                    num1opp = rand.nextInt(max - min + 1) + min;
                    num2opp = rand.nextInt(max - min + 1) + min;
                    lvl = 6;
                }
                A.setText("" + num1opp);
                B.setText("" + num2opp);
                divopp = (int)(num1opp / num2opp);
                break;
        }
        OptionGeneratoropp();
    }

    public void opponentQuestion()
    {
        View oppview = findViewById(R.id.rightoppview);
        oppview.setVisibility(View.VISIBLE);
        TextView coverright = findViewById(R.id.cover2);
        coverright.setVisibility(View.INVISIBLE);
        checkMessageopp("+");
    }

    public void resetSubCount()
    {
        subended=false;
        if(subcd!=null)
            subcd.cancel();
        resetSubTime();
        subcd=subCounter(subtime);
    }

    public void resetSubTime()
    {
        subtime=7000;
    }

    public CountDownTimer countdown(CountDownTimer cd)
    {
        final TextView time=findViewById(R.id.CountDown);
        cd=new CountDownTimer(timeLeft,1000) {
            @Override
            public void onTick(long l) {
                timeLeft=l;
                UpdateTimer(time,l);
            }

            @Override
            public void onFinish() {
                mref.child("Multiplayer").child(Id+"").removeEventListener(scoreeventlistener);
                mref.child("Multiplayer").child(Id+"").child("GameRoomid").removeEventListener(oppscoreshowlistener);
                getOpponentId();
            }
        }.start();
        return cd;
    }

    boolean subended=false;

    public CountDownTimer subCounter(long timeopp)
    {
        final TextView timeopptext=findViewById(R.id.subTime);
        CountDownTimer cd=new CountDownTimer(timeopp,1000) {
            @Override
            public void onTick(long l) {
                UpdateTimer(timeopptext,l);
            }

            @Override
            public void onFinish() {
                View oppview = findViewById(R.id.rightoppview);
                oppview.setVisibility(View.INVISIBLE);
                TextView coverright = findViewById(R.id.cover2);
                coverright.setVisibility(View.VISIBLE);
                subended=true;
            }
        }.start();
        return cd;
    }

    public void UpdateTimer(TextView tv,long tl)
    {
        int min=0,sec=0;
        if(tv.equals(findViewById(R.id.CountDown))) {
            min = (int) (timeLeft / 1000) / 60;
            sec = (int) (timeLeft / 1000) % 60;
            String FormatedTime=String.format(Locale.getDefault(),"%02d:%02d",min,sec);
            tv.setText(FormatedTime);
            /*if(sec<=05)
                tv.setTextColor(Color.parseColor("#D81B60"));
            else
                tv.setTextColor(Color.parseColor("#81000000"));*/
        }
        else if(tv.equals(findViewById(R.id.startcountdown)))
        {
            //min=(int) (startcount/1000)/60;
            sec=(int) (tl/1000)%60;
            tv.setText(""+sec);
            /*if(sec<=03)
                tv.setTextColor(Color.parseColor("#D81B60"));
            else
                tv.setTextColor(Color.parseColor("#81000000"));*/
        }
        else if(tv.equals(findViewById(R.id.subTime)))
        {
            sec=(int) (tl/1000)%60;
            tv.setText(""+sec);
            if(sec<=03)
                tv.setTextColor(Color.parseColor("#D81B60"));
            else
                tv.setTextColor(Color.parseColor("#81000000"));
        }

    }


    public void MakeInviCD(final TextView tv,final long tl)
    {
        //connectPlayers();
        mref.child("Multiplayer").child(Id+"").child("GameRoomid").removeEventListener(waitingListener);
        connected=true;
        final TextView test = findViewById(R.id.cover1);
        CountDownTimer cd=new CountDownTimer(startcount,1000) {
            @Override
            public void onTick(long l) {
                if(tv.equals(findViewById(R.id.startcountdown)))
                {
                    startcount=l;
                    UpdateTimer(tv,l);
                }
            }

            @Override
            public void onFinish() {
                tv.setVisibility(View.INVISIBLE);
                if(tv.equals(findViewById(R.id.startcountdown)))
                {
                    makeVisible();
                }
            }
        }.start();
    }

    public void makeVisible()
    {
        final TextView A= findViewById(R.id.display);
        final TextView B= findViewById(R.id.display2);
        final TextView Res= findViewById(R.id.display3);
        final TextView operator= findViewById(R.id.operator);
        final Button op1 = findViewById(R.id.option1);
        final Button op2 = findViewById(R.id.option2);
        final Button op3 = findViewById(R.id.option3);
        final Button op4 = findViewById(R.id.option4);
        TextView coverleft = findViewById(R.id.cover1);

        coverleft.setVisibility(View.INVISIBLE);
        A.setVisibility(View.VISIBLE);
        B.setVisibility(View.VISIBLE);
        operator.setVisibility(View.VISIBLE);
        Res.setVisibility(View.VISIBLE);
        op1.setVisibility(View.VISIBLE);
        op2.setVisibility(View.VISIBLE);
        op3.setVisibility(View.VISIBLE);
        op4.setVisibility(View.VISIBLE);
        cdg=countdown(cdg);
        subcd=subCounter(subtime);
        started=true;
    }

    void updateScoreFirebase()
    {
        final TextView coverl=findViewById(R.id.cover1);
        mref = new Firebase("https://fragsout.firebaseio.com/");
        mref.child("Multiplayer").child(Id+"").child("GameRoomid").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String gameroomid=(String)dataSnapshot.getValue();
                mref.child("GameRoom").child(gameroomid+"").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child(Id+"").hasChild("score")) {
                            mref.child("GameRoom").child(gameroomid+"").child(Id+"").child("score").removeValue();
                        }
                        else
                        {
                            mref.child("GameRoom").child(gameroomid+"").child(Id+"").child("score").setValue("0");
                        }
                        mref.child("GameRoom").child(gameroomid+"").child(Id+"").child("score").setValue(""+score);
                        mref.child("GameRoom").child(gameroomid+"").removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
                mref.child("Multiplayer").child(Id+"").child("GameRoomid").removeEventListener(this);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    void ReduceOppScore()
    {
        mref = new Firebase("https://fragsout.firebaseio.com/");
        mref.child("Multiplayer").child(Id+"").child("GameRoomid").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String gameroomid=(String)dataSnapshot.getValue();
                String oppId = "";
                String[] gameroomsplit = gameroomid.split("-");
                if(gameroomsplit[0].equals(Id))
                {
                    oppId=gameroomsplit[1];
                }
                else
                {
                    oppId=gameroomsplit[0];
                }
                final String finalOppId = oppId;
                mref.child("GameRoom").child(gameroomid+"").child(finalOppId+"").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String sc=(String)dataSnapshot.child("score").getValue();
                        int oppscore=Integer.parseInt(sc);
                        oppscore-=2;
                        mref.child("GameRoom").child(gameroomid+"").child(finalOppId +"").child("score").setValue(""+oppscore);
                        mref.child("GameRoom").child(gameroomid+"").child(finalOppId +"").removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
                mref.child("Multiplayer").child(Id+"").child("GameRoomid").removeEventListener(this);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    void getOpponentId()
    {
        mref = new Firebase("https://fragsout.firebaseio.com/");
        mref.child("Multiplayer").child(Id+"").child("GameRoomid").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String gameroomid=(String)dataSnapshot.getValue();
                String oppId = "";
                String[] gameroomsplit = gameroomid.split("-");
                if(gameroomsplit[0].equals(Id))
                {
                    oppId=gameroomsplit[1];
                }
                else
                {
                    oppId=gameroomsplit[0];
                }
                //test.setText(oppId);
                compareScore(oppId);
                mref.child("Multiplayer").child(Id+"").child("GameRoomid").removeEventListener(this);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    ValueEventListener oppscoreshowlistener;

    void ShowOpponentScore()
    {
        final TextView oppscoreshowtv = findViewById(R.id.cover2);
        mref = new Firebase("https://fragsout.firebaseio.com/");
        oppscoreshowlistener =new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String gameroomid=(String)dataSnapshot.getValue();
                String oppId = "";
                String[] gameroomsplit = gameroomid.split("-");
                if(gameroomsplit[0].equals(Id))
                {
                    oppId=gameroomsplit[1];
                }
                else
                {
                    oppId=gameroomsplit[0];
                }
                mref.child("GameRoom").child(gameroomid+"").child(oppId+"").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("score"))
                        {
                            String oppscore = (String)dataSnapshot.child("score").getValue();
                            oppscoreshowtv.setText("OPPONENT SCORE:\n"+oppscore);
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
        mref.child("Multiplayer").child(Id+"").child("GameRoomid").addValueEventListener(oppscoreshowlistener);
    }

    ValueEventListener scoreeventlistener;

    public void UpdateScore()
    {
        final TextView scoretextview = findViewById(R.id.score);
        final TextView bonusTime = findViewById(R.id.bonusTime);
        mref = new Firebase("https://fragsout.firebaseio.com/");
        scoreeventlistener= new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshotmult) {
                final String gameroomid=(String)dataSnapshotmult.child("GameRoomid").getValue();
                mref.child("GameRoom").child(gameroomid+"").child(Id+"").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("score")) {
                            String myscore = (String) dataSnapshot.child("score").getValue();
                            int currentscore=Integer.parseInt(myscore);
                            scoretextview.setText(myscore);
                            if(score>currentscore)
                            {
                                bonusTime.setTextColor(Color.RED);
                                bonusTime.setVisibility(View.VISIBLE);
                                bonusTime.setText("-2");
                                MakeInviCD(bonusTime,2000);
                            }
                            String sc = scoretextview.getText().toString();
                            score = Integer.parseInt(sc);
                        }
                        //if(dataSnapshotmult.hasChild("result"))
                        //mref.child("GameRoom").child(gameroomid+"").child(Id+"").removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
                //if(dataSnapshotmult.hasChild("result"))
                //mref.child("Multiplayer").child(Id+"").child("GameRoomid").removeEventListener(this);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        };
        mref.child("Multiplayer").child(Id+"").addValueEventListener(scoreeventlistener);
        String sc=scoretextview.getText().toString();
        score = Integer.parseInt(sc);
    }
    void compareScore(final String oppId)
    {
        mref = new Firebase("https://fragsout.firebaseio.com/");
        mref.child("Multiplayer").child(Id+"").child("GameRoomid").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final String gameroomid=(String)dataSnapshot.getValue();
                mref.child("GameRoom").child(gameroomid+"").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        String oppscore=(String)dataSnapshot.child(oppId).child("score").getValue();
                        String myscore=(String)dataSnapshot.child(Id).child("score").getValue();
                        int oppscoreint=Integer.parseInt(oppscore);
                        int myscoreint=Integer.parseInt(myscore);
                        if(oppscoreint>myscoreint)
                        {
                            mref.child("GameRoom").child(gameroomid+"").child(Id+"").child("result").setValue("LOSE");
                            showResult("LOSE");
                        }
                        else if (oppscoreint<myscoreint){
                            mref.child("GameRoom").child(gameroomid+"").child(Id+"").child("result").setValue("WIN");
                            showResult("WIN");
                        }
                        else {
                            mref.child("GameRoom").child(gameroomid+"").child(Id+"").child("result").setValue("DRAW");
                            showResult("DRAW");
                        }
                        mref.child("GameRoom").child(gameroomid+"").removeEventListener(this);
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
                mref.child("Multiplayer").child(Id+"").child("GameRoomid").removeEventListener(this);
                return;
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });

    }

    void showResult(String result)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Result:");
        builder.setMessage(result)
                .setCancelable(false)
                .setPositiveButton("EXIT",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(MultiplayerGameRoom.this, HomeScreen.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        clearFirebaseData();
                        startActivity(intent);
                    }
                });
        AlertDialog alert=builder.create();
        alert.show();
    }

    void getID()
    {
        Intent intent = getIntent();
        final String Id = intent.getStringExtra(MultiplayerOptionRoom.GameID);
        this.Id=Id;
    }

    @Override
    public void onBackPressed()
    {
        popupExit();
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
                        Intent intent = new Intent(MultiplayerGameRoom.this, HomeScreen.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        clearFirebaseData();
                        startActivity(intent);
                    }
                }).setNegativeButton("I'll Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        AlertDialog alert=builder.create();
        alert.show();
    }

    void clearFirebaseData()
    {
        mref = new Firebase("https://fragsout.firebaseio.com/");
        mref.child("Multiplayer").child(Id+"").child("GameRoomid").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(Tag,"removed");
                String gameroomid=(String)dataSnapshot.getValue();
                mref.child("GameRoom").child(gameroomid+"").removeValue();
                mref.child("Multiplayer").child(Id+"").child("GameRoomid").removeEventListener(this);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        mref.child("Multiplayer").child(Id+"").removeValue();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mref.child("Multiplayer").child(Id+"").removeEventListener(scoreeventlistener);
        mref.child("Multiplayer").child(Id+"").child("GameRoomid").removeEventListener(waitingListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mref.child("Multiplayer").child(Id+"").removeEventListener(scoreeventlistener);
        mref.child("Multiplayer").child(Id+"").child("GameRoomid").removeEventListener(waitingListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mref.child("Multiplayer").child(Id+"").removeEventListener(scoreeventlistener);
        mref.child("Multiplayer").child(Id + "").child("GameRoomid").removeEventListener(waitingListener);
    }
}
