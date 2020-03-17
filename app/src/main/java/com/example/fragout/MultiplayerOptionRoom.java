package com.example.fragout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableContainer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;

public class MultiplayerOptionRoom extends AppCompatActivity {

    private static final String Tag=MainActivity.class.getSimpleName();
    public static final String GameID = "Keyx";

    final DatabaseReference multiplayer = FirebaseDatabase.getInstance().getReference().child("Multiplayer");

    String gameroom;

    Firebase mref;
    public static long id=idGenerator();
    int playercount;
    int cancelled=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_option_room);

        final TextView updatetext = findViewById(R.id.MatchmakingUpdate);
        updatetext.setText("");

        mref = new Firebase("https://fragsout.firebaseio.com/");

        final Button cancel=findViewById(R.id.cancel);
        final Button room=findViewById(R.id.room);

        cancel.setVisibility(View.INVISIBLE);

        if(!isOnline())
        {
            updatetext.setText("You Are Offline");
            room.setEnabled(false);
            room.setBackgroundResource(R.drawable.disabled_button_design);
        }

        String message = checkMessage();
        if(message!=null)
        {
            clearFirebaseData();
            id=idGenerator();
            Firebase childref = mref.child("Multiplayer").child(""+id).child("waiting");
            childref.setValue("1");
            cancel.setVisibility(View.VISIBLE);
            room.setVisibility(View.INVISIBLE);
            MatchFinder(message);
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancel.setVisibility(View.INVISIBLE);
                updatetext.setText("");
                room.setVisibility(View.VISIBLE);
                if(NotFoundListenner!=null)
                    mref.child("Multiplayer").child(""+id).removeEventListener(NotFoundListenner);
                if(MatchCheckerListenner!=null)
                    mref.child("Multiplayer").removeEventListener(MatchCheckerListenner);
                //mref.removeEventListener(MatchCheckerListenner);
                clearFirebaseData();
            }
        });

        room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Firebase childref = mref.child("Multiplayer").child(""+id).child("waiting");
                childref.setValue("1");
                childref = mref.child("Multiplayer").child(""+id).child("Name");
                childref.setValue(""+HomeScreen.Name);
                cancel.setVisibility(View.VISIBLE);
                room.setVisibility(View.INVISIBLE);
                MatchFinder("Finding...");
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

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        clearFirebaseData();
        finish();
    }

    public String checkMessage()
    {
        Intent intent = getIntent();
        String message=intent.getStringExtra(MultiplayerGameRoom.priority);
        return message;
    }

    /*@Override
    public void onStop() {
        super.onStop();
        final Button cancel=findViewById(R.id.cancel);
        final Button room=findViewById(R.id.room);
        final TextView updatetext = findViewById(R.id.MatchmakingUpdate);
        cancel.setVisibility(View.INVISIBLE);
        updatetext.setText("");
        room.setVisibility(View.VISIBLE);
        clearFirebaseData();
    }*/

    void MatchFinder(String message)
    {
        TextView updatetext=findViewById(R.id.MatchmakingUpdate);
        updatetext.setText(message);
        MatchChecker();
    }

    static long idGenerator()
    {
        Date date=new Date();
        long id = date.getTime();
        return id;
    }

    ValueEventListener MatchCheckerListenner;
    void MatchChecker()
    {
        final TextView updatetext=findViewById(R.id.MatchmakingUpdate);
        mref = new Firebase("https://fragsout.firebaseio.com/");
        mref.child("Multiplayer").addValueEventListener(MatchCheckerListenner = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                playercount = (int) dataSnapshot.getChildrenCount();
                String player,waiting;
                if(dataSnapshot.child(id+"").hasChild("GameRoomid")) {
                    return;
                }
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    player = ds.getKey();
                    Log.d(Tag, player);
                    if (!player.equals(Long.toString(id))) {
                        if (!dataSnapshot.child(id + "").hasChild("GameRoomid")) {
                            waiting = ds.child("waiting").getValue(String.class);
                            if (waiting.equals("1")) {
                                mref.child("Multiplayer").child(player + "").child("OpponentId").setValue(""+id);
                                mref.child("Multiplayer").child(player + "").child("waiting").setValue("0");
                                AfterFound(player);
                                mref.child("Multiplayer").removeEventListener(this);
                                break;
                            }
                        }
                    } /*else {
                        NotFound();
                        break;
                    }*/
                }
//                mref.child("Multiplayer").removeEventListener(this);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        //mref.child("Multiplayer").addValueEventListener(MatchCheckerListenner);
        //mref.child("Multiplayer").removeEventListener(MatchCheckerListenner);
    }

    ValueEventListener NotFoundListenner = null;
    void NotFound()
    {
        mref.child("Multiplayer").child(""+id).addValueEventListener(NotFoundListenner = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String waiting = (String) dataSnapshot.child("waiting").getValue();
                if(waiting.equals("0"))
                {
                    String player = (String)dataSnapshot.child("OpponentId").getValue();
                    mref.child("Multiplayer").child(player + "").child("waiting").setValue("0");
                    mref.child("Multiplayer").child(player + "").child("OpponentId").setValue(""+id);
                    AfterFound(player);
                    mref.child("Multiplayer").child(""+id).removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        //mref.child("Multiplayer").child(""+id).addValueEventListener(NotFoundListenner);
    }

    void AfterFound(final String player)
    {
        //mref.child("Multiplayer").child(""+id).removeEventListener(NotFoundListenner);

        final TextView updatetext = findViewById(R.id.MatchmakingUpdate);
        final String gameroomid = id+"-"+player;
        final String gameroominverseid = player+"-"+id;

        mref = new Firebase("https://fragsout.firebaseio.com/");
        mref.child("GameRoom").addValueEventListener(new com.firebase.client.ValueEventListener(){
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                mref.child("Multiplayer").child(id+"").child("GameRoomid").setValue(gameroominverseid);
                mref.child("Multiplayer").child(player+"").child("GameRoomid").setValue(gameroominverseid);
                updatetext.setText("MATCH FOUND!!!");
                delay();
                /*if(dataSnapshot.hasChild(gameroominverseid))
                {
                    mref.child("Multiplayer").child(id+"").child("GameRoomid").setValue(gameroominverseid);
                    //mref.child("Multiplayer").child(player+"").child("GameRoomid").setValue(gameroominverseid);
                    updatetext.setText("MATCH FOUND!!!");
                    GoToRoom();
                    //mref.child("GameRoom").removeEventListener(this);
                }
                else
                {
                    Firebase childref = mref.child("GameRoom").child(gameroomid).child("Found");
                    childref.setValue("true");
                    mref.child("Multiplayer").child(id+"").child("GameRoomid").setValue(gameroomid);
                    updatetext.setText("MATCH FOUND!!!");
                    GoToRoom();
                    mref.child("GameRoom").removeEventListener(this);
                }*/
                mref.child("GameRoom").removeEventListener(this);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    void delay()
    {
        long dtime = 2000;
        CountDownTimer cd = new CountDownTimer(dtime,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                GoToRoom();
            }
        }.start();
    }

    void GoToRoom() {
        mref = new Firebase("https://fragsout.firebaseio.com/");
        String id = Long.toString(MultiplayerOptionRoom.id);
        Intent intent = new Intent(MultiplayerOptionRoom.this, MultiplayerGameRoom.class);
        intent.putExtra(GameID, id);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    void clearFirebaseData()
    {
        mref = new Firebase("https://fragsout.firebaseio.com/");
        mref.child("Multiplayer").child(id+"").removeValue();
    }
}
