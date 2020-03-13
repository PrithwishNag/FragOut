package com.example.fragout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class MultiplayerOptionRoom extends AppCompatActivity {

    private static final String Tag=MainActivity.class.getSimpleName();
    public static final String GameID = "Keyx";

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
                clearFirebaseData();
            }
        });

        room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Firebase childref = mref.child("Multiplayer").child(""+id).child("waiting");
                childref.setValue("1");
                cancel.setVisibility(View.VISIBLE);
                room.setVisibility(View.INVISIBLE);
                MatchFinder("Finding...");
            }
        });

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

    void MatchChecker()
    {
        final TextView updatetext=findViewById(R.id.MatchmakingUpdate);
        mref = new Firebase("https://fragsout.firebaseio.com/");
        final DatabaseReference multiplayer = FirebaseDatabase.getInstance().getReference().child("Multiplayer");
        multiplayer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                playercount = (int) dataSnapshot.getChildrenCount();
                String player,waiting;
                if(dataSnapshot.child(id+"").hasChild("GameRoomid")) {
                    return;
                }
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    player = ds.getKey();

                    if (!player.equals(Long.toString(id))) {
                        if(!dataSnapshot.child(id+"").hasChild("GameRoomid")) {
                            waiting = ds.child("waiting").getValue(String.class);
                            if (waiting.equals("1")){
                                multiplayer.child(player + "").child("waiting").setValue("0");
                                AfterFound(player);
                                multiplayer.removeEventListener(this);
                                break;
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void AfterFound(final String player)
    {
        final TextView updatetext = findViewById(R.id.MatchmakingUpdate);
        final String gameroomid=id+"-"+player;
        final String gameroominverseid=player+"-"+id;

        mref = new Firebase("https://fragsout.firebaseio.com/");
        mref.child("GameRoom").addValueEventListener(new com.firebase.client.ValueEventListener(){
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(gameroominverseid))
                {
                    mref.child("Multiplayer").child(id+"").child("GameRoomid").setValue(gameroominverseid);
                    updatetext.setText("MATCH FOUND!!!");
                    GoToRoom();
                    mref.child("GameRoom").removeEventListener(this);
                }
                else
                {
                    Firebase childref = mref.child("GameRoom").child(gameroomid).child("Found");
                    childref.setValue("true");
                    mref.child("Multiplayer").child(id+"").child("GameRoomid").setValue(gameroomid);
                    updatetext.setText("MATCH FOUND!!!");
                    GoToRoom();
                    mref.child("GameRoom").removeEventListener(this);
                }
                mref.child("GameRoom").removeEventListener(this);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
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
