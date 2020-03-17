package com.example.fragout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class HomeScreen extends AppCompatActivity {

    public static final String OP_TYPE = "Key1";
    public static final String PLAY_TYPE = "Key2";
    private static final String Tag=MainActivity.class.getSimpleName();

    public static String Name ;
    public static String Password ;
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

        Button multiplayer = findViewById(R.id.multiplayer);
        Button leaderboard = findViewById(R.id.Leaderboard);
        mref = new Firebase("https://fragsout.firebaseio.com/");

        checkSavedName("Account Sign-In");
        Button login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeAccount("Account Sign-In");
            }
        });

        initLevelExperience();
        checkSavedStuff();
        CreateUserFirebase(Name,Password);
        FillExperienceBar(Experience);

        leaderboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeScreen.this,Leaderboard.class);
                startActivity(intent);
            }
        });

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

    public void ImportantInstructions(String name, String pass)
    {
        initLevelExperience();
        checkSavedStuff();
        CreateUserFirebase(name,pass);
        FillExperienceBar(Experience);
    }

    public void checkSavedName(String Hint)
    {
        final SharedPreferences prefs = this.getSharedPreferences("myKey", Context.MODE_PRIVATE);
        if(!prefs.contains("Don't Show Again"))
        {
            if(!isOnline())
            {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("WARNING OFFLINE");
                builder.setMessage("Play offline!\nBut progress will be recorded only when you are online!")
                        .setCancelable(true)
                        .setPositiveButton("Continue",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        })
                        .setNegativeButton("Don't Show Again!",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                SharedPreferences.Editor editor = prefs.edit();
                                editor.putInt("Don't Show Again",1);
                                editor.apply();
                            }
                        });
                AlertDialog alert=builder.create();
                alert.show();
            }
        }
        if(!prefs.contains("Name"))
        {
            if(!isOnline())
            {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("OFFLINE");
                builder.setMessage("Unable to connect!\nPlease Check Your connection and restart the app!\n(This is needed for One-Time-Syncing)")
                        .setCancelable(false)
                        .setPositiveButton("Exit",new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        });
                AlertDialog alert=builder.create();
                alert.show();
                return;
            }

            final Context context = this;

            final Dialog dialog = new Dialog(context);
            dialog.setContentView(R.layout.save_name_dialog_design);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            final EditText name_ed = dialog.findViewById(R.id.nameInput);
            final EditText pass_ed = dialog.findViewById(R.id.passwordInput);
            name_ed.setHint(Hint);

            dialog.setCancelable(false);

            Button dialogButton = dialog.findViewById(R.id.Save);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    final String name=name_ed.getText().toString().trim();
                    final String pass=pass_ed.getText().toString();
                    if(name.equals("")||pass.equals("")) {
                        checkSavedName("Enter Your Name");
                    }
                    else {
                        Password = pass;
                        mref.child("Users").addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.hasChild(name) && !dataSnapshot.child(name).child("Password").getValue().equals(pass))
                                {
                                   checkSavedName("Username Already Exists");
                                }
                                else if(dataSnapshot.hasChild(name) && dataSnapshot.child(name).child("Password").getValue().equals(pass))
                                {
                                    switchToExistingAccount(name);
                                }
                                else
                                    confirmCreateAccount(name,pass);
                                mref.child("Users").removeEventListener(this);
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }
                }
            });
            dialog.show();
        }
        else
        {
            String name = prefs.getString("Name","");
            Name=name;
            String pass = prefs.getString("Password","");
            Password=pass;
            TextView name_tv = findViewById(R.id.Name);
            name_tv.setText(""+name);
            ImportantInstructions(Name, Password);
        }
    }

    public int getHighScore(String HighScore) {
        SharedPreferences prefs = this.getSharedPreferences("myKey", Context.MODE_PRIVATE);
        int hs = prefs.getInt(HighScore, 0);
        return hs;
    }

    public void setHighScore(String HighScoreString,int HighScore)
    {
        SharedPreferences prefs = this.getSharedPreferences("myKey", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(HighScoreString,HighScore);
        editor.commit();
    }

    public void confirmCreateAccount(final String name, final String pass)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("CREATE ACCOUNT");
        builder.setMessage("Do you want to CREATE a new account \""+name+"\"?!")
                .setCancelable(true)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        saveName(name,pass);
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        checkSavedName("Enter Username");
                    }
                });
        AlertDialog alert=builder.create();
        alert.show();
    }

    public void switchToExistingAccount(final String name)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("SWITCH ACCOUNT");
        builder.setMessage("Do you want to switch to the existing account \""+name+"\"?!")
                .setCancelable(false)
                .setPositiveButton("Yes",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ExistingAccountData(name);
                    }
                })
                .setNegativeButton("No",new DialogInterface.OnClickListener(){
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ChangeAccount("Username Already Exists");
                }
            });
        AlertDialog alert=builder.create();
        alert.show();
    }

    public void ExistingAccountData(final String name)
    {
        mref.child("Users").child(name).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                setExperience(Integer.parseInt((String)dataSnapshot.child("Experience").getValue()));
                setExperience_level(Integer.parseInt((String)dataSnapshot.child("Level").getValue()));
                setHighScore("HighScoreAdd",((Long) dataSnapshot.child("HighScoreAdd").getValue()).intValue());
                setHighScore("HighScoreSub",((Long) dataSnapshot.child("HighScoreSub").getValue()).intValue());
                setHighScore("HighScoreMult",((Long) dataSnapshot.child("HighScoreMult").getValue()).intValue());
                setHighScore("HighScoreDiv",((Long) dataSnapshot.child("HighScoreDiv").getValue()).intValue());
                saveName(name,(String) dataSnapshot.child("Password").getValue());
                ImportantInstructions(name,(String) dataSnapshot.child("Password").getValue());
                mref.child("Users").child(name).removeEventListener(this);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public void saveName(String name, String pass)
    {
        Name=name;
        Password =pass;
        SharedPreferences prefs = this.getSharedPreferences("myKey", Context.MODE_PRIVATE);
        prefs.edit().putString("Name",name).apply();
        prefs.edit().putString("Password",pass).apply();
        TextView name_tv = findViewById(R.id.Name);
        name_tv.setText(""+name);
        ImportantInstructions(name,pass);
    }

    void CreateUserFirebase(String name, String Password)
    {
        mref.child("Users").child(""+name).child("Password").setValue(""+Password);
        mref.child("Users").child(""+name).child("Experience").setValue(""+Experience);
        mref.child("Users").child(""+name).child("Level").setValue(""+Experience_level);

        String HighScoreString="HighScoreAdd";
        int HighScore = getHighScore(HighScoreString);
        mref.child("Users").child(""+HomeScreen.Name).child(HighScoreString).setValue(HighScore);
        if(HighScore!=0) {
            mref.child("Leaderboard").child(""+HighScoreString).child(""+HomeScreen.Name).child("PlayerScore").setValue(HighScore);
        }

        HighScoreString="HighScoreSub";
        HighScore = getHighScore(HighScoreString);
        mref.child("Users").child(""+HomeScreen.Name).child(HighScoreString).setValue(HighScore);
        if(HighScore!=0) {
            mref.child("Leaderboard").child(""+HighScoreString).child(""+HomeScreen.Name).child("PlayerScore").setValue(HighScore);
        }

        HighScoreString="HighScoreMult";
        HighScore = getHighScore(HighScoreString);
        mref.child("Users").child(""+HomeScreen.Name).child(HighScoreString).setValue(HighScore);
        if(HighScore!=0) {
            mref.child("Leaderboard").child(""+HighScoreString).child(""+HomeScreen.Name).child("PlayerScore").setValue(HighScore);
        }

        HighScoreString="HighScoreDiv";
        HighScore = getHighScore(HighScoreString);
        mref.child("Users").child(""+HomeScreen.Name).child(HighScoreString).setValue(HighScore);
        if(HighScore!=0) {
            mref.child("Leaderboard").child(""+HighScoreString).child(""+HomeScreen.Name).child("PlayerScore").setValue(HighScore);
        }
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

    void ChangeAccount(String Hint)
    {
        if(!isOnline())
        {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("OFFLINE");
            builder.setMessage("Unable to connect!\nPlease Check Your connection and restart the app!\n(This is needed for One-Time-Syncing)")
                    .setCancelable(true)
                    .setPositiveButton("Ok",new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });
            AlertDialog alert=builder.create();
            alert.show();
            return;
        }

        final Context context = this;

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.save_name_dialog_design);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        final EditText name_ed = dialog.findViewById(R.id.nameInput);
        final EditText pass_ed = dialog.findViewById(R.id.passwordInput);
        name_ed.setHint(Hint);

        dialog.setCancelable(true);

        Button dialogButton = dialog.findViewById(R.id.Save);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                final String name=name_ed.getText().toString().trim();
                final String pass=pass_ed.getText().toString();
                if(name.equals("")||pass.equals("")) {
                    checkSavedName("Enter Username");
                }
                else {
                    Password = pass;
                    mref.child("Users").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild(name) && !dataSnapshot.child(name).child("Password").getValue().equals(pass))
                            {
                                ChangeAccount("Username Already Exists");
                            }
                            else if(dataSnapshot.hasChild(name) && dataSnapshot.child(name).child("Password").getValue().equals(pass))
                            {
                                switchToExistingAccount(name);
                            }
                            else
                            {
                                ChangeAccount("You Already Have an Account! Can't Create One more");
                            }
                            mref.child("Users").removeEventListener(this);
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
            }
        });
        dialog.show();
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
