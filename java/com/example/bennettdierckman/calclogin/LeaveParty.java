package com.example.bennettdierckman.calclogin;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class LeaveParty extends Fragment {


    public LeaveParty() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leave_party, container, false);
        Bundle bundle = getArguments();
        final String title = bundle.getString("title");
        final String username = bundle.getString("username");
        final String password = bundle.getString("password");
        Log.d("LeaveParty Bundle : ", String.valueOf(bundle));

        Button bReturnParty = (Button) view.findViewById(R.id.bReturnParty);
        Button bLeaveParty = (Button) view.findViewById(R.id.bLeaveParty);

        //SWITCH FROM FRAGMENT TO FRAGMENT
        bLeaveParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Gets Response array from php
                            JSONObject jsonResponse = new JSONObject(response);
                            //Success represents the passwords being equal
                            boolean leavePartySuccess = jsonResponse.getBoolean("success");
                            //Loging the value of boolean partySuccess for testing/debugging purposes
                            Log.d("user has left Party ", String.valueOf(leavePartySuccess));
                            //if the JoinSelectedRequest.php is successful ( if(success) )
                            if(leavePartySuccess){
                                Bundle bundle = new Bundle();
                                //asssign arguments
                                bundle.putString("username", username);
                                bundle.putString("password", password);
                                //Begin transaction
                                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                //Create new Profile fragment instance
                                Profile profile = new Profile();
                                profile.setArguments(bundle);
                                //finish transaction
                                fragmentTransaction.replace(R.id.flcontent, profile);
                                fragmentTransaction.commit();
                            }else{
                                Toast.makeText(getActivity(),"Incorrect Password", Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                //prepares CreateRequest with the appropriate data, so that the php file cann be called and the party can
                //be added to the database
                LeavePartyRequest leavePartyRequest = new LeavePartyRequest(title, responseListener);
                RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                queue.add(leavePartyRequest);
            }
        });
        bReturnParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                //asssign arguments
                bundle.putString("title", title);
                bundle.putString("username", username);
                bundle.putString("password", password);
                //Begin transaction
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //Create new Profile fragment instance
                AttendeeParty attendeeParty = new AttendeeParty();
                attendeeParty.setArguments(bundle);
                //finish transaction
                fragmentTransaction.replace(R.id.flcontent, attendeeParty);
                fragmentTransaction.commit();
            }
        });
        return view;
    }

}
