package com.example.bennettdierckman.calclogin;


import android.graphics.Color;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AttendeeInfluence extends Fragment {

    public List<String> existingVideoTitles;
    public List<String> existingVideoInfluences;

    public String address = "http://cgi.soic.indiana.edu/~team51/LikeDislikeRequest.php"; // address for php that populates fields with existing account information

    public AttendeeInfluence() {
        // Required empty public constructor
    }

    public String videoID = "";
    public String thumbnailURL = "";
    public boolean songLoved = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_attendee_influence, container, false);

        //get bundle arguments from Attendee Party fragment
        Bundle bundle = getArguments();
        final String partyTitle = bundle.getString("title");
        final String videoTitle = bundle.getString("videoTitle");
        final String username = bundle.getString("username");
        final String password = bundle.getString("password");
        existingVideoInfluences = bundle.getStringArrayList("existingVideoInfluences");
        existingVideoTitles = bundle.getStringArrayList("existingVideoTitles");
        Log.d("AttendeeInfluenc Bundle", String.valueOf(bundle)); //log value of bundle for debuging

        //Assign the ragment_attendee Influence.xml id's to variables
        final TextView tvSongTitle = (TextView) view.findViewById(R.id.tvSongTitle);
        final ImageView ivThumbnailURL = (ImageView) view.findViewById(R.id.ivThumbnailURL);
        final RelativeLayout myBLove = (RelativeLayout) view.findViewById(R.id.myBLoveThis);
        final RelativeLayout myBLike = (RelativeLayout) view.findViewById(R.id.myBLikeThis);
        final RelativeLayout myBDislike = (RelativeLayout) view.findViewById(R.id.myBDislike);
        final Button bReturn2Party = (Button) view.findViewById(R.id.bReturn2Party);

        //Picasso.with(getActivity().getApplicationContext()).load(thumbnailURL).into(ivThumbnailURL);
        tvSongTitle.setText(videoTitle);

        new SendPostRequest().execute();

        Boolean titleInList = false;
        for (String i: existingVideoTitles){
            if (i.equals(videoTitle)){
                titleInList = true;
                break;
            }
        }
        if(titleInList){
            int index = -1;
            for (int i = 0; i < existingVideoTitles.size(); i++) {
                if (existingVideoTitles.get(i).equals(videoTitle)) {
                    index = i;
                    break;
                }
            }
            if((existingVideoInfluences.get(index).equals("dislike")) || (existingVideoInfluences.get(index).equals("swapL4D"))){
                myBDislike.setBackgroundColor(Color.rgb(89,70,127));
                myBLike.setBackgroundColor(Color.rgb(108,108,108));
            }
            else if(existingVideoInfluences.get(index).equals("like") || existingVideoInfluences.get(index).equals("swapD4L")) {
                myBLike.setBackgroundColor(Color.rgb(89,30,204));
                myBDislike.setBackgroundColor(Color.rgb(108,108,108));
            }
        }

        myBLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!songLoved) {
                    myBLove.setBackgroundColor(Color.rgb(91, 15, 17));

                    Log.d("HERE IS TITLE goin in", videoTitle + " " + videoID);
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
                                    Toast.makeText(getActivity(), "LOVE SUCCESS", Toast.LENGTH_LONG).show();
                                    songLoved = true;
                                } else {  //(emailSuccess)
                                    Toast.makeText(getActivity(), "LOVE FAIL", Toast.LENGTH_LONG).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    //prepares AttendeeInfluence with the appropriate data, so that the php file cann be called and the user can
                    //influence the playlist in the database
                    AttendeeLoveRequest attendeeLoveRequest = new AttendeeLoveRequest(username, videoTitle, videoID, thumbnailURL, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                    queue.add(attendeeLoveRequest);
                }
                else{
                    Toast.makeText(getActivity(), "You already love this video", Toast.LENGTH_LONG).show();
                }
            }
        });

        myBLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean titleInList = false;
                for (String i: existingVideoTitles){
                    if (i.equals(videoTitle)){
                        titleInList = true;
                        break;
                    }
                }
                Log.d("LIKE TITLE IN LIST", String.valueOf(titleInList));
                if(titleInList){
                    int index = -1;
                    for (int i = 0; i < existingVideoTitles.size(); i++) {
                        if (existingVideoTitles.get(i).equals(videoTitle)) {
                            index = i;
                            break;
                        }
                    }
                    if(existingVideoInfluences.get(index).equals("dislike")){
                        existingVideoInfluences.set(index, "swapD4L");
                        requestInfluence(partyTitle, videoTitle, "swapD4L");
                        Toast.makeText(getActivity(),  "Swapped Dislike for like", Toast.LENGTH_SHORT).show();
                    }
                    else if (existingVideoInfluences.get(index).equals("swapL4D")){
                        existingVideoInfluences.set(index, "swapD4L");
                        requestInfluence(partyTitle, videoTitle, "swapD4L");
                        Toast.makeText(getActivity(),  "Swapped Dislike for Like", Toast.LENGTH_SHORT).show();
                    }
                    else if(existingVideoInfluences.get(index).equals("like") || existingVideoInfluences.get(index).equals("swapD4L")) {
                        Toast.makeText(getActivity(), "Unable to like song again", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    //no existing influence
                    existingVideoInfluences.add("like");
                    existingVideoTitles.add(videoTitle);
                    requestInfluence(partyTitle, videoTitle, "like");
                    Toast.makeText(getActivity(),  "Song Liked", Toast.LENGTH_SHORT).show();
                }
                myBLike.setBackgroundColor(Color.rgb(89,30,204));
                myBDislike.setBackgroundColor(Color.rgb(108,108,108));
            }
        });

        myBDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean titleInList = false;
                for (String i: existingVideoTitles){
                    if (i.equals(videoTitle)){
                        titleInList = true;
                        break;
                    }
                }
                Log.d("DISLIKE TITLE IN LIST", String.valueOf(titleInList));
                if(titleInList){
                    int index = -1;
                    for (int i = 0; i < existingVideoTitles.size(); i++) {
                        if (existingVideoTitles.get(i).equals(videoTitle)) {
                            index = i;
                            break;
                        }
                    }
                    if(existingVideoInfluences.get(index).equals("like")){
                        existingVideoInfluences.set(index, "swapL4D");
                        requestInfluence(partyTitle, videoTitle, "swapL4D");
                        Toast.makeText(getActivity(),  "Swapped Like for Dislike", Toast.LENGTH_SHORT).show();
                    }
                    else if (existingVideoInfluences.get(index).equals("swapD4L")){
                        existingVideoInfluences.set(index, "swapL4D");
                        requestInfluence(partyTitle, videoTitle, "swapL4D");
                        Toast.makeText(getActivity(),  "Swapped Like for Dislike", Toast.LENGTH_SHORT).show();
                    }
                    else if(existingVideoInfluences.get(index).equals("dislike") || existingVideoInfluences.get(index).equals("swapL4D")){
                        Toast.makeText(getActivity(),  "Unable to Dislike song again", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    //no existing influence
                    existingVideoInfluences.add("dislike");
                    existingVideoTitles.add(videoTitle);
                    requestInfluence(partyTitle, videoTitle, "dislike");
                    Toast.makeText(getActivity(),  "Song Disliked", Toast.LENGTH_SHORT).show();
                }
                myBDislike.setBackgroundColor(Color.rgb(89,70,127));
                myBLike.setBackgroundColor(Color.rgb(108,108,108));
            }
        });

        bReturn2Party.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                //asssign arguments
                bundle.putString("title", partyTitle);
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
                fragmentTransaction.replace(R.id.flcontent, attendeeParty);
                fragmentTransaction.commit();
            }
        });

        //return the fragment's layout
        return view;
    }

    public void requestInfluence(String partyTitle, String videoTitle, String newInfluence){
            Response.Listener<String> responseListener = new Response.Listener<String>() {
            Bundle bundle = getArguments();
                final String videoTitle = bundle.getString("videoTitle");
            @Override
            public void onResponse(String response) {
                try {
                    // Gets Response array from php
                    JSONObject jsonResponse = new JSONObject(response);
                    final TextView tvNumLikes = (TextView) getView().findViewById(R.id.tvNumLikes);
                    final TextView tvNumDislikes = (TextView) getView().findViewById(R.id.tvNumDislikes);
                    //Assign values of $response['email'] and $response['username'] to java boolean variables
                    boolean success = jsonResponse.getBoolean("success");
                    boolean badSongDropped = jsonResponse.getBoolean("notClutchVideoDeleted");
                    int numLikes = jsonResponse.getInt("likes");
                    int numDislikes = jsonResponse.getInt("dislikes");
                    if (badSongDropped){
                        Toast.makeText(getActivity(),  "Not Clutch video removed from playlist", Toast.LENGTH_LONG).show();
                    }
                    Log.d("NOT CLUTCH VIDEO Drop:", String.valueOf(badSongDropped));
                    //Conditionals with statements that either register the user, or inform them the email or username is taken
                    if (success) {
                        tvNumDislikes.setText(String.valueOf(numDislikes));
                        tvNumLikes.setText(String.valueOf(numLikes));
                        Toast.makeText(getActivity(), "Playlist updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(),  "Unable To influence Playlist", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        //prepares AttendeeInfluence with the appropriate data, so that the php file cann be called and the user can
        //influence the playlist in the database
        AttendeeInfluenceRequest attendeeInfluenceRequest = new AttendeeInfluenceRequest(partyTitle.replace(" ", "_"), videoTitle.replace("\\", "").replace("u2013", "–"), newInfluence, responseListener);
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        queue.add(attendeeInfluenceRequest);
    }

    /////////////////// BEGIN SEND POST REQUEST METHOD//////////
    public class SendPostRequest extends AsyncTask<String, Void, String> {

        Bundle bundle = getArguments();
        //Check bundle arguments are correct via log statement (Click run in bottom left to see)
        //Assigns the bundles arguments to variables to be used in this java file
        final String partyTitle = bundle.getString("title");
        final String videoTitle = bundle.getString("videoTitle");
        final String username = bundle.getString("username");


        protected void onPreExecute(){}

        @Override
        protected String doInBackground(String... arg0) {
            try{
                URL url = new URL(address);

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("partyTitle", partyTitle.replace(" ", "_"));
                postDataParams.put("videoTitle", videoTitle);
                postDataParams.put("username", username);
                Log.d("Params", postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK){

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while((line = br.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    br.close();
                    return sb.toString();
                }
                else{
                    return new String("false : " + responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG).show();
            super.onPostExecute(result);

            TextView tvNumLikes = getView().findViewById(R.id.tvNumLikes);
            TextView tvNumDislikes = getView().findViewById(R.id.tvNumDislikes);
            final ImageView ivThumbnailURL = (ImageView) getView().findViewById(R.id.ivThumbnailURL);
            final RelativeLayout myBLove = (RelativeLayout) getView().findViewById(R.id.myBLoveThis);


            try {
                JSONObject jsonObj = new JSONObject(result);
                Log.d("HERE IS JSONObj", String.valueOf(jsonObj));
                int likes = jsonObj.getInt("likes");
                int dislikes = jsonObj.getInt("dislikes");
                thumbnailURL = jsonObj.getString("thumbnailURL");
                videoID = jsonObj.getString("videoLink");
                boolean userLoves = jsonObj.getBoolean("userLoves");

                Picasso.with(getActivity().getApplicationContext()).load(thumbnailURL).into(ivThumbnailURL);
                tvNumLikes.setText(String.valueOf(likes));
                tvNumDislikes.setText(String.valueOf(dislikes));
                if (userLoves){
                    myBLove.setBackgroundColor(Color.rgb(91,15,17));
                    songLoved = true;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString();
    }
////////////////////////////////////////////////////////////////////////////////////////////////////
}
