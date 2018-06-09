package com.example.bennettdierckman.calclogin;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class AddVideo extends Fragment {

    private EditText searchInput;
    private ListView videosFound;

    private Handler handler;
    private List<VideoList> searchResults;

    public List<String> existingVideoTitles;
    public List<String> existingVideoInfluences;

    public AddVideo() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_video, container, false);

        searchInput = (EditText)view.findViewById(R.id.search_input);
        videosFound = (ListView)view.findViewById(R.id.videos_found);

        handler = new Handler();

        searchInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_DONE){
                    searchOnYoutube(v.getText().toString());
                    return false;
                }
                return true;
            }
        });
        addClickListener();

        return view;
    }

    private void searchOnYoutube(final String keywords){
        new Thread(){
            public void run(){
                YoutubeConnector yc = new YoutubeConnector(getActivity().getApplicationContext());
                searchResults = yc.search(keywords);
                handler.post(new Runnable(){
                    public void run(){
                        updateVideosFound();
                    }
                });
            }
        }.start();
    }
    private void updateVideosFound(){
        ArrayAdapter<VideoList> adapter = new ArrayAdapter<VideoList>(getActivity().getApplicationContext(), R.layout.video_list, searchResults){
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                if(convertView == null){
                    convertView = getLayoutInflater().inflate(R.layout.video_list, parent, false);
                }
                ImageView thumbnail = (ImageView)convertView.findViewById(R.id.video_thumbnail);
                TextView title = (TextView)convertView.findViewById(R.id.video_title);
                //TextView description = (TextView)convertView.findViewById(R.id.video_description);

                VideoList searchResult = searchResults.get(position);

                Picasso.with(getActivity().getApplicationContext()).load(searchResult.getThumbnailURL()).into(thumbnail);
                title.setText(searchResult.getTitle());
                //description.setText(searchResult.getDescription());
                return convertView;
            }
        };

        videosFound.setAdapter(adapter);
    }

    private void addClickListener(){
        videosFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> av, View v, int pos,
                                    long id) {
                Bundle bundle = getArguments();
                final String title = bundle.getString("title");
                final String username = bundle.getString("username");
                final String password = bundle.getString("password");
                existingVideoInfluences = bundle.getStringArrayList("existingVideoInfluences");
                existingVideoTitles = bundle.getStringArrayList("existingVideoTitles");
                Log.d("AttendeeParty Bundle : ", String.valueOf(bundle));

//                VideoList List = new VideoList();
//                String songTitle = List.getTitle();
//                String songURL = List.getThumbnailURL();
//                Log.d("Song Title", List.getTitle().toString());

                String songID = searchResults.get(pos).getId();
                String songTitle = searchResults.get(pos).getTitle();
                final String thumbnialURL = searchResults.get(pos).getThumbnailURL();
                Log.d("Song Title", songTitle);
                Log.d("Song ID", songID);
                Log.d("THUMBNAIL URL", thumbnialURL);

                //Response listener to handle response from php file
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Gets Response array from php
                            JSONObject jsonResponse = new JSONObject(response);
                            //Assign values of $response['email'] and $response['username'] to java boolean variables
                            boolean inserted = jsonResponse.getBoolean("inserted");
                            //Conditionals with statements that either register the user, or inform them the email or username is taken
                            if (!inserted) {
                                Toast.makeText(getActivity(),  "That song is already in the playlist", Toast.LENGTH_LONG).show();
                            }else {
                                Bundle bundle = new Bundle();
                                bundle.putString("username", username);
                                bundle.putString("title", title);
                                bundle.putString("password", password);
                                //bundle.putString("thumbnailURL", thumbnialURL);
                                bundle.putStringArrayList("existingVideoInfluences", (ArrayList<String>) existingVideoInfluences);
                                bundle.putStringArrayList("existingVideoTitles", (ArrayList<String>) existingVideoTitles);
                                //Begining the transaction
                                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                //Create new Initialize fragment instance
                                AttendeeParty attendeeParty = new AttendeeParty();
                                //set bundle arguments
                                attendeeParty.setArguments(bundle);
                                Toast.makeText(getActivity(),  "Song Added to Playlist", Toast.LENGTH_LONG).show();
                                //finish transaction
                                fragmentTransaction.replace(R.id.flcontent, attendeeParty);
                                fragmentTransaction.commit();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                //prepares CreateRequest with the appropriate data, so that the php file cann be called and the party can
                //be added to the database
                AddVideoRequest addVideoRequest = new AddVideoRequest(songTitle.replace("\\", "").replace("u2013", "–"), songID, title.replace(" ", "_"), thumbnialURL, responseListener);
                RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                queue.add(addVideoRequest);
            }

        });
    }

}
