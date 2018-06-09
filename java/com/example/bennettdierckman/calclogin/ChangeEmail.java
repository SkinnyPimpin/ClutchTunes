package com.example.bennettdierckman.calclogin;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChangeEmail extends Fragment {


    public ChangeEmail() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_email, container, false);
            Bundle bundle = getArguments();
            Log.d("ChangeEmail-bundle: ", String.valueOf(bundle));
            final String username = bundle.getString("username");
            final String password = bundle.getString("password");

        final EditText etNewEmail = view.findViewById(R.id.etNewEmail);
        Button bSaveChanges = view.findViewById(R.id.bNext);
        Button bGoBack = view.findViewById(R.id.bReturn2Party);

        bSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newEmail = etNewEmail.getText().toString();

                if (newEmail.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(newEmail).matches() ) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Not A Valid Email")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                }else {
                    //Response listener to handle response from php file
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                // Gets Response array from php
                                JSONObject jsonResponse = new JSONObject(response);
                                //Assign values of $response['email'] and $response['username'] to java boolean variables
                                boolean success = jsonResponse.getBoolean("success");
                                //Conditionals with statements that either register the user, or inform them the email or username is taken
                                if (success) {
                                    Toast.makeText(getActivity().getApplicationContext(), "Email updated", Toast.LENGTH_LONG).show();
                                    //Create new bundle for Profile fragment
                                    Bundle bundle = new Bundle();
                                    bundle.putString("username", username);
                                    bundle.putString("password", password);
                                    //Begin fragment transacion
                                    android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    //Create new instance of Profile fragment
                                    EditProfile editProfile = new EditProfile();
                                    editProfile.setArguments(bundle);
                                    //Finish fragment transaction
                                    fragmentTransaction.replace(R.id.flcontent, editProfile);
                                    fragmentTransaction.commit();
                                } else {  //(emailSuccess)
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setMessage("Email Taken")
                                            .setNegativeButton("Retry", null)
                                            .create()
                                            .show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    //prepares CreateRequest with the appropriate data, so that the php file cann be called and the party can
                    //be added to the database
                    ChangeEmailRequest changeEmailRequest = new ChangeEmailRequest(newEmail, username, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                    queue.add(changeEmailRequest);
                }
            }
        });

        bGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                //Set the bundle arguments
                bundle.putString("username", username);
                bundle.putString("password", password);

                //Begin Fragment transaction
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //Create new profile instance
                EditProfile editProfile = new EditProfile();
                //Set bundle arguments
                editProfile.setArguments(bundle);
                //Finish fragment transaction
                fragmentTransaction.replace(R.id.flcontent, editProfile);
                fragmentTransaction.commit();
            }
        });
        return view;
    }

}
