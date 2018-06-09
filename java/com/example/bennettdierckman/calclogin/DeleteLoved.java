package com.example.bennettdierckman.calclogin;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class DeleteLoved extends Fragment {


    public DeleteLoved() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_delete_loved, container, false);
        Bundle bundle = getArguments();
        final String username = bundle.getString("username");
        final String password = bundle.getString("password");
        final String videoTitle = bundle.getString("videoTitle");
        final String videoID = bundle.getString("videoID");
        Log.d("Delete Loved Bundle : ", String.valueOf(bundle));


        TextView tvVideoTitle = (TextView) view.findViewById(R.id.tvVideoTitle);
        Button bGoBack = (Button) view.findViewById(R.id.bGoBack);
        Button bDelete = (Button) view.findViewById(R.id.bDelete);

        tvVideoTitle.setText(videoTitle);

        //SWITCH FROM FRAGMENT TO FRAGMENT
        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Gets Response array from php
                            JSONObject jsonResponse = new JSONObject(response);
                            //Assign values of $response['email'] and $response['username'] to java boolean variables
                            boolean success = jsonResponse.getBoolean("success");
                            Log.d("DROP SUCCESSFULL  ", String.valueOf(success));
                            //Conditionals with statements that either register the user, or inform them the email or username is taken
                            if (success) {
                                Toast.makeText(getActivity().getApplicationContext(), "Video removed from your LoveList", Toast.LENGTH_LONG).show();
                                //Create new bundle for Profile fragment
                                Bundle bundle = new Bundle();
                                bundle.putString("username", username);
                                bundle.putString("password", password);
                                //Begin fragment transacion
                                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                //Create new instance of Profile fragment
                                Profile profile = new Profile();
                                profile.setArguments(bundle);
                                //Finish fragment transaction
                                fragmentTransaction.replace(R.id.flcontent, profile);
                                fragmentTransaction.commit();
                            }else{
                                Toast.makeText(getActivity(),"Unable to drop loved song.", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                //prepares CreateRequest with the appropriate data, so that the php file cann be called and the party can
                //be added to the database
                DropLovedRequest dropLovedRequest = new DropLovedRequest(username, videoID, responseListener);
                RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                queue.add(dropLovedRequest);
            }
        });
        bGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                //asssign arguments
                bundle.putString("videoTitle", videoTitle);
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
            }
        });
        return view;
    }

}
