package com.example.fragout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class Home extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.home_layout,container,false);
        Button playadd=(Button) view.findViewById(R.id.playAdd);
        Button playmult=(Button) view.findViewById(R.id.playMult);

        playadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayAdd();
            }
        });
        /*playmult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlayMult();
            }
        });*/

        return view;
    }

    public void PlayAdd()
    {
        Intent intent=new Intent(getActivity(), PlayScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /*public void PlayMult()
    {
        Intent intent=new Intent(getActivity(), PlayScreenMult.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }*/

}
