package com.example.fragout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;


public class Leaderboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        final Firebase fb = new Firebase("https://fragsout.firebaseio.com/");

        final ListView listadd = findViewById(R.id.listadd);
        final ListView listsub = findViewById(R.id.listsub);
        final ListView listmult = findViewById(R.id.listmult);
        final ListView listdiv = findViewById(R.id.listdiv);

        getPlayers(fb,listadd,"HighScoreAdd");
        getPlayers(fb,listsub,"HighScoreSub");
        getPlayers(fb,listmult,"HighScoreMult");
        getPlayers(fb,listdiv,"HighScoreDiv");
    }

    void getPlayers(final Firebase fb, final ListView list, final String HighScoreType)
    {
        fb.child("Leaderboard").child(HighScoreType).orderByChild("PlayerScore").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int numofplayers = (int) dataSnapshot.getChildrenCount();
                int i=numofplayers-1;
                if(numofplayers==0)
                {
                    String []singlearr = new String[1];
                    singlearr[0]="No Players Yet!";
                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                            (Leaderboard.this, android.R.layout.simple_list_item_1, singlearr);

                    list.setAdapter(adapter);
                }
                else {
                    int rank = numofplayers;
                    String[] listarr = new String[numofplayers];
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        String PlayerName = ds.getKey();
                        String PlayerScore = Long.toString((Long) ds.child("PlayerScore").getValue());
                        listarr[i] = "Rank: " + rank + "\n" + PlayerName + " : " + PlayerScore;
                        i--;
                        rank--;
                    }
                    final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                            (Leaderboard.this, android.R.layout.simple_list_item_1, listarr);

                    list.setAdapter(adapter);
                }
                fb.child("Leaderboard").child(HighScoreType).removeEventListener(this);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
