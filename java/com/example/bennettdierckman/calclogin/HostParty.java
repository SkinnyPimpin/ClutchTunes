package com.example.bennettdierckman.calclogin;

import android.app.AlertDialog;
import android.content.Intent;
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
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONException;
import org.json.JSONObject;

public class HostParty extends Fragment {

    public HostParty() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_host_party, container, false);

        //get bundle arguments from Initialize fragment
        Bundle bundle = getArguments();
        final String title = bundle.getString("title");
        final String username = bundle.getString("username");
        final String password = bundle.getString("password");

        //Get end party button from HostParty_fragment.xml file
        Button bEndParty = (Button) view.findViewById(R.id.bEndParty);
        //get fetch video button from HostParty_fragment.xml file
        Button bFetchVideo = (Button) view.findViewById(R.id.bFetchInitialVideo);
        //Get partyTitle text view from HostParty_fragment.xml
        TextView tvTitle = (TextView) view.findViewById(R.id.tvPartyTitle);


        //Set that text equal to the title bundle argument
        tvTitle.setText(title);

        //When end party button is selected, SWITCH FROM FRAGMENT TO FRAGMENT
        bEndParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create new bundle
                Bundle bundle = new Bundle();
                //Add title for ConfirmEnd fragment
                bundle.putString("title", title);
                //Add user and pass for Profile fragment
                bundle.putString("username", username);
                bundle.putString("password", password);
                //Begin fragment transaction
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //Create new instance of ConfirmEnd fragment
                ConfirmEnd confirmEnd = new ConfirmEnd();
                confirmEnd.setArguments(bundle);
                //Finish fragment transaction
                fragmentTransaction.replace(R.id.flcontent, confirmEnd);
                fragmentTransaction.commit();
            }
        });

        //when fetch vido button is clicked
        bFetchVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Response listener to handle response from php file
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Gets Response array from php
                            JSONObject jsonResponse = new JSONObject(response);
                            //Assign values of $response['email'] and $response['username'] to java boolean variables
                            boolean weHaveASong = jsonResponse.getBoolean("weHaveASong");
                            String songID = jsonResponse.getString("songID");
                            //String songTitle = jsonResponse.getString("songTitle");
                            Log.d("HOST_PARTY songIDStrin ", songID);

                            if (songID.length() >= 1){
//                                //Create new bundle
//                                Bundle bundle = new Bundle();
//                                //Add title for ConfirmEnd fragment
//                                bundle.putString("title", title);
//                                //Add user and pass for Profile fragment
//                                bundle.putString("username", username);
//                                bundle.putString("password", password);
//                                bundle.putString("songID", songID);
                                Intent intent = new Intent(getActivity().getBaseContext(), PlayerActivity.class);
                                intent.putExtra("username", username);
                                intent.putExtra("password", password);
                                intent.putExtra("songID", songID);
                                intent.putExtra("title", title);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(getActivity(), "Please have attendees add songs to playlist", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                //prepares CreateRequest with the appropriate data, so that the php file cann be called and the party can
                //be added to the database
                FetchVideoRequest fetchVideoRequest = new FetchVideoRequest( title, responseListener);
                RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                queue.add(fetchVideoRequest);
            }
        });
        return view;
    }
}
