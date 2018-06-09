package com.example.bennettdierckman.calclogin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class Create extends Fragment {

    public Create() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_create, container, false);
        //Get bundle arguments from Profile fragment
        Bundle bundle = getArguments();
        //Check that we are indeed, getting the bundle
        Log.d("Create-bundle: ", String.valueOf(bundle));
        //assigning bundle arguments to variables to be used in this java file
        final String username = bundle.getString("username");
        final String password = bundle.getString("password");

        //Assign the create_fragment.xml id's to variables
        final EditText etPartyTitle = (EditText) view.findViewById(R.id.etPartyTitle);
        final EditText etPartyPassword = (EditText) view.findViewById(R.id.etPartyPassword);
        Button bCancel = (Button) view.findViewById(R.id.bReturn2Party);
        Button bNext = (Button) view.findViewById(R.id.bNext);

        //Radio group section of the create_fragment.xml
        final RadioGroup partyDemographics = (RadioGroup) view.findViewById(R.id.rgPartyDemographics);
        final RadioGroup partyRadius = (RadioGroup) view.findViewById(R.id.rgPartyRadius);
        final RadioGroup partyPublicPrivate = (RadioGroup) view.findViewById(R.id.rgPublicPrivate);

        //If they hit the cancel button, SWITCH FROM FRAGMENT TO FRAGMENT
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Prepare Profile.java bundle
                Bundle bundle = new Bundle();
                //Set the bundle arguments
                bundle.putString("username", username);
                bundle.putString("password", password);
                //Begin Fragment transaction
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //Create new profile instance
                Profile profile = new Profile();
                //Set bundle arguments
                profile.setArguments(bundle);
                //Finish fragment transaction
                fragmentTransaction.replace(R.id.flcontent, profile);
                fragmentTransaction.commit();
            }
        });

        //When they hit the next button, SWITCH FROM FRAGMENT TO FRAGMENT
        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Gets the title and party password from above, turns into string for regular expressions
                final String title = etPartyTitle.getText().toString();

                //Gets value of RadioGroup Selection and turns its text to string
                final String demographics = ((RadioButton)view.findViewById(partyDemographics.getCheckedRadioButtonId()))
                        .getText().toString();
                final String radius = ((RadioButton)view.findViewById(partyRadius.getCheckedRadioButtonId()))
                        .getText().toString();
                final String publicPrivate = ((RadioButton)view.findViewById(partyPublicPrivate.getCheckedRadioButtonId()))
                        .getText().toString();

                //Check if title is empty
                if (title.isEmpty()) {
                    Toast.makeText(getActivity(),  "Enter a Party Title", Toast.LENGTH_LONG).show();
                    // Check if the title is malicious
                } else if (!title.matches("[a-zA-Z 0-9]*")) {
                    Toast.makeText(getActivity(),  "Party Title must be alpha numeric", Toast.LENGTH_LONG).show();

                    //EVerything is in accordance
                }else {
                    if (publicPrivate.equalsIgnoreCase("yes")) {
                        //Response listener to handle response from php file
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    // Gets Response array from php
                                    JSONObject jsonResponse = new JSONObject(response);
                                    //Assign values of $response['email'] and $response['username'] to java boolean variables
                                    boolean userIs18 = jsonResponse.getBoolean("userIs18");
                                    //Conditionals with statements that either register the user, or inform them the email or username is taken
                                    if (!userIs18 && demographics.equals("18+ Only")) {
                                        Bundle bundle = new Bundle();
                                        //Username is in bundle for when user eventually goes back to their profile
                                        bundle.putString("username", username);
                                        //Password is in bundle for when the user eventually ends the party
                                        bundle.putString("password", password);
                                        //The remaining bundle arguments are about the party
                                        bundle.putString("title", title);
                                        bundle.putString("demographics", "Not 18");
                                        bundle.putString("radius", radius);
                                        //Begining the transaction
                                        android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        //Create new Set_Password fragment instance
                                        Set_Password set_password = new Set_Password();
                                        //set bundle arguments
                                        set_password.setArguments(bundle);
                                        //finish transaction
                                        fragmentTransaction.replace(R.id.flcontent, set_password);
                                        fragmentTransaction.commit();
                                    } else {  //(emailSuccess)
                                        Bundle bundle = new Bundle();
                                        //Username is in bundle for when user eventually goes back to their profile
                                        bundle.putString("username", username);
                                        //Password is in bundle for when the user eventually ends the party
                                        bundle.putString("password", password);
                                        //The remaining bundle arguments are about the party
                                        bundle.putString("title", title);
                                        bundle.putString("demographics", demographics);
                                        bundle.putString("radius", radius);
                                        //Begining the transaction
                                        android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        //Create new Set_Password fragment instance
                                        Set_Password set_password = new Set_Password();
                                        //set bundle arguments
                                        set_password.setArguments(bundle);
                                        //finish transaction
                                        fragmentTransaction.replace(R.id.flcontent, set_password);
                                        fragmentTransaction.commit();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        //prepares CreateRequest with the appropriate data, so that the php file cann be called and the party can
                        //be added to the database
                        CheckDemographicRequest checkDemographicRequest = new CheckDemographicRequest(username, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                        queue.add(checkDemographicRequest);
                    }
                    else{
                        //Response listener to handle response from php file
                        Response.Listener<String> responseListener = new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    // Gets Response array from php
                                    JSONObject jsonResponse = new JSONObject(response);
                                    //Assign values of $response['email'] and $response['username'] to java boolean variables
                                    boolean userIs18 = jsonResponse.getBoolean("userIs18");
                                    //Conditionals with statements that either register the user, or inform them the email or username is taken
                                    if (!userIs18 && demographics.equals("18+ Only")) {
                                        Bundle bundle = new Bundle();
                                        //Username is in bundle for when user eventually goes back to their profile
                                        bundle.putString("username", username);
                                        //Password is in bundle for when the user eventually ends the party
                                        bundle.putString("password", password);
                                        //The remaining bundle arguments are about the party
                                        bundle.putString("title", title);
                                        bundle.putString("PartyPassword", "");
                                        bundle.putString("demographics", "Not 18");
                                        bundle.putString("radius", radius);
                                        //Begining the transaction
                                        android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        //Create new Initialize fragment instance
                                        Initialize Initialize = new Initialize();
                                        //set bundle arguments
                                        Initialize.setArguments(bundle);
                                        //finish transaction
                                        fragmentTransaction.replace(R.id.flcontent, Initialize);
                                        fragmentTransaction.commit();
                                    } else {  //(emailSuccess)
                                        Bundle bundle = new Bundle();
                                        //Username is in bundle for when user eventually goes back to their profile
                                        bundle.putString("username", username);
                                        //Password is in bundle for when the user eventually ends the party
                                        bundle.putString("password", password);
                                        //The remaining bundle arguments are about the party
                                        bundle.putString("title", title);
                                        bundle.putString("PartyPassword", "");
                                        bundle.putString("demographics", demographics);
                                        bundle.putString("radius", radius);
                                        //Begining the transaction
                                        android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        //Create new Initialize fragment instance
                                        Initialize Initialize = new Initialize();
                                        //set bundle arguments
                                        Initialize.setArguments(bundle);
                                        //finish transaction
                                        fragmentTransaction.replace(R.id.flcontent, Initialize);
                                        fragmentTransaction.commit();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        //prepares CreateRequest with the appropriate data, so that the php file cann be called and the party can
                        //be added to the database
                        CheckDemographicRequest checkDemographicRequest = new CheckDemographicRequest(username, responseListener);
                        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                        queue.add(checkDemographicRequest);
                    }

//                            //Larger bundle becuase this one has to carry all the party information to
//                            //Initialize.java, which will act as a confirm plans screen, and will launch CreateRequest
//                            // , this request will call the php files and enter the party in the db
//                                    Bundle bundle = new Bundle();
//                                    //Username is in bundle for when user eventually goes back to their profile
//                                    bundle.putString("username", username);
//                                    //Password is in bundle for when the user eventually ends the party
//                                    bundle.putString("password", password);
//                                    //The remaining bundle arguments are about the party
//                                    bundle.putString ("title", title);
//                                    bundle.putString("PartyPassword", PartyPassword);
//                                    bundle.putString("demographics", demographics);
//                                    bundle.putString("radius", radius);
//                                    bundle.putString("timeframe",timeframe);
//                                    //Begining the transaction
//                                    android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                                    //Create new Initialize fragment instance
//                                    Initialize Initialize = new Initialize();
//                                    //set bundle arguments
//                                    Initialize.setArguments(bundle);
//                                    //finish transaction
//                                    fragmentTransaction.replace(R.id.flcontent, Initialize);
//                                    fragmentTransaction.commit();
                }
            }
        });
        return view;
    }

}




