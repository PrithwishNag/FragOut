package com.example.fragout;

import android.os.CountDownTimer;

import java.util.Random;

public class MultiChoiceFunctions {
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


}
