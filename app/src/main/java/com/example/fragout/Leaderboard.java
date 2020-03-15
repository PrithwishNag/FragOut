package com.example.fragout;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ListView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class Leaderboard extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        final Firebase fb = new Firebase("https://fragsout.firebaseio.com/");

        final ListView listview = findViewById(R.id.list);

        //final String []listarr = new String[1000];
        //final List<String> listarr = new ArrayList<String>();

        fb.child("Leaderboard").orderByChild("PlayerScore").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int numofplayers = (int) dataSnapshot.child("HighScoreAdd").getChildrenCount();
                int i=numofplayers-1;
                int rank=1;
                String []listarr = new String[numofplayers];
                for (DataSnapshot ds : dataSnapshot.child("HighScoreAdd").getChildren())
                {
                    String PlayerName = ds.getKey();
                    String PlayerScore = (String)ds.child("PlayerScore").getValue();
                    listarr[i]="Rank: "+rank+"\n"+PlayerName+" : "+PlayerScore;
                    i--;
                    rank++;
                }
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>
                        (Leaderboard.this, android.R.layout.simple_list_item_1, listarr);

                listview.setAdapter(adapter);
                fb.child("Leaderboard").child("HighScoreAdd").removeEventListener(this);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
