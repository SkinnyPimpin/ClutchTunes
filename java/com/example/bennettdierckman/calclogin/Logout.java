package com.example.bennettdierckman.calclogin;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


public class Logout extends Fragment {

    public Logout() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_logout, container, false);
        //Get the bundles arguments from ProfileActivity
        Bundle bundle = getArguments();
        //test code for debuging (double checking that bundle values are as they should be)
        Log.d("profile-bundle: ", String.valueOf(bundle));
        //Assigns that pulled in bundle to new variables to be referenced here in this java file
        final String username = bundle.getString("username");
        final String password = bundle.getString("password");
        //Assigns viewId's from fragment_logout to variables to be referenced here
        Button bYes = (Button) view.findViewById(R.id.bYes);
        Button bCancel = (Button) view.findViewById(R.id.bEndParty);

       //if they select Yes button, (confirming their logout)
       //SWITCH FROM this FRAGMENT TO LoginActivity ACTIVITY
        bYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent is used for this
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            }
        });
        //if they hit the cancel button, they are redirected back to their Profile.java (which requires 2 bundle arguments)
        //SWITCH FROM FRAGMENT TO FRAGMENT
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Preparing bundle for Profile.java fragment
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                bundle.putString("password", password);
                //Begin Fragment Transaction
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //Prepare new Profile fragment instance
                Profile profile = new Profile();
                profile.setArguments(bundle);
                //Finish transaction
                fragmentTransaction.replace(R.id.flcontent, profile);
                fragmentTransaction.commit();
            }
        });
        return view;
    }

}
