package com.example.bennettdierckman.calclogin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class AttendeeJoinSelected extends Fragment {


    public AttendeeJoinSelected() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_attendee_join_selected, container, false);
        Bundle bundle = getArguments();
        final String title = bundle.getString("title");
        final String username = bundle.getString("username");
        final String password = bundle.getString("password");
        final List<String> existingVideoInfluences = bundle.getStringArrayList("existingVideoInfluences");
        final List<String> existingVideoTitles = bundle.getStringArrayList("existingVideoTitles");
        Log.d("Join Selected Bundle : ", String.valueOf(bundle));

        Button bJoinParty = (Button) view.findViewById(R.id.bJoinParty);
        Button bCancel = (Button) view.findViewById(R.id.bEndParty);
        final TextView tvPartyTitle = (TextView) view.findViewById(R.id.tvPartyTitle);
        final EditText etPartyPassword = (EditText) view.findViewById(R.id.etPartyPassword);

        tvPartyTitle.setText(title);

        bJoinParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String passwordInput = etPartyPassword.getText().toString();
////////////////////////////////////////////////////////////////////////////////////////
                if(passwordInput.isEmpty()){
                    passwordInput = "";
                    //Response listener to handle response from php file
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                // Gets Response array from php
                                JSONObject jsonResponse = new JSONObject(response);
                                //Success represents the passwords being equal
                                boolean partySuccess = jsonResponse.getBoolean("success");
                                //Loging the value of boolean partySuccess for testing/debugging purposes
                                Log.d("Party password Correct", String.valueOf(partySuccess));
                                //if the JoinSelectedRequest.php is successful ( if(success) )
                                if(partySuccess){
                                    Bundle bundle = new Bundle();
                                    //asssign arguments
                                    bundle.putString("title", title);
                                    bundle.putString("username", username);
                                    bundle.putString("password", password);
                                    bundle.putStringArrayList("existingVideoInfluences", (ArrayList<String>) existingVideoInfluences);
                                    bundle.putStringArrayList("existingVideoTitles", (ArrayList<String>) existingVideoTitles);
                                    //Begin transaction
                                    android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    //Create new Profile fragment instance
                                    AttendeeParty attendeeParty = new AttendeeParty();
                                    attendeeParty.setArguments(bundle);
                                    //finish transaction
                                    fragmentTransaction.replace(R.id.flcontent, attendeeParty);
                                    fragmentTransaction.commit();

                                }else{
                                    Toast.makeText(getActivity(),  "Incorrect Password", Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    //prepares CreateRequest with the appropriate data, so that the php file cann be called and the party can
                    //be added to the database
                    JoinSelectedRequest joinSelectedRequest = new JoinSelectedRequest(title, passwordInput, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                    queue.add(joinSelectedRequest);
                }
                ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                else if (!passwordInput.matches("(?!.*/)[A-Za-z0-9][A-Za-z0-9 ]{0,18}[A-Za-z0-9]")){
                    Toast.makeText(getActivity(),  "Party Password must be alpha numeric", Toast.LENGTH_LONG).show();
                }else{
                    //Response listener to handle response from php file
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                // Gets Response array from php
                                JSONObject jsonResponse = new JSONObject(response);
                                //Success represents the passwords being equal
                                boolean partySuccess = jsonResponse.getBoolean("success");
                                //Loging the value of boolean partySuccess for testing/debugging purposes
                                Log.d("Party password Correct", String.valueOf(partySuccess));
                                //if the JoinSelectedRequest.php is successful ( if(success) )
                                if(partySuccess){
                                    Bundle bundle = new Bundle();
                                    //asssign arguments
                                    bundle.putString("title", title);
                                    bundle.putString("username", username);
                                    bundle.putString("password", password);
                                    bundle.putStringArrayList("existingVideoInfluences", (ArrayList<String>) existingVideoInfluences);
                                    bundle.putStringArrayList("existingVideoTitles", (ArrayList<String>) existingVideoTitles);
                                    //Begin transaction
                                    android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    //Create new Profile fragment instance
                                    AttendeeParty attendeeParty = new AttendeeParty();
                                    attendeeParty.setArguments(bundle);
                                    //finish transaction
                                    fragmentTransaction.replace(R.id.flcontent, attendeeParty);
                                    fragmentTransaction.commit();

                                }else{
                                    Toast.makeText(getActivity(),  "Incorrect Password", Toast.LENGTH_LONG).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    //prepares CreateRequest with the appropriate data, so that the php file cann be called and the party can
                    //be added to the database
                    JoinSelectedRequest joinSelectedRequest = new JoinSelectedRequest(title, passwordInput, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                    queue.add(joinSelectedRequest);
                }
            }
        });
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                //asssign arguments
                bundle.putString("username", username);
                bundle.putString("password", password);
                //Begin transaction
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //Create new Profile fragment instance
                PSM psm = new PSM();
                psm.setArguments(bundle);
                //finish transaction
                fragmentTransaction.replace(R.id.flcontent, psm);
                fragmentTransaction.commit();
            }
        });
        return view;
    }
}
