package com.example.bennettdierckman.calclogin;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONException;
import org.json.JSONObject;

//import com.npkiser.youtube_test.YoutubeConnector;


/**
 * Created by noahkiser
 */

public class PlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    private YouTubePlayerView playerView;
    private String firstSong = "";
    private String firstSongTitle = "";
    private String username = "";
    private String password = "";
    private String title = "";
    private String songTitle = "";
    private String[] arrayOfTitles;
    private String songID = "";
    private String[] arrayOfIDs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_player);

        playerView = (YouTubePlayerView) findViewById(R.id.player_view);
        playerView.initialize(YoutubeConnector.KEY, this);

        Intent intent = getIntent();
        //Reassigns the variables passed through .putExtras
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        title  = intent.getStringExtra("title");

        songID = intent.getStringExtra("songID");
        arrayOfIDs = songID.split("\\s+");
        firstSong = arrayOfIDs[1];
        //firstSong = firstSong.substring(1);
        Log.d("PLAYER_ACTIVITY FIRST ", firstSong + " " + title);


    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
        youTubePlayer.setPlaybackEventListener(playbackEventListener);
        if(!b) {
            youTubePlayer.loadVideo(firstSong);
            youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
            youTubePlayer.setPlaybackEventListener(playbackEventListener);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        Toast.makeText(this, "Initialization Failed", Toast.LENGTH_LONG).show();

    }

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {
        @Override
        public void onBuffering(boolean arg0) {
        }
        @Override
        public void onPaused() {
        }
        @Override
        public void onPlaying() {
        }
        @Override
        public void onSeekTo(int arg0) {
        }
        @Override
        public void onStopped() {
        }
    };
    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {
        @Override
        public void onAdStarted() {
        }
        @Override
        public void onError(YouTubePlayer.ErrorReason arg0) {
        }
        @Override
        public void onLoaded(String arg0) {
            Log.d("ON_VIDEO_id/title: ", songID + " " + title);
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        // Gets Response array from php
                        JSONObject jsonResponse = new JSONObject(response);
                        //Assign values of $response['email'] and $response['username'] to java boolean variables
                        boolean success = jsonResponse.getBoolean("success");
                        if (success){
                            Toast.makeText(PlayerActivity.this, "song marked as played", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(PlayerActivity.this, "failed to mark song as played", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            //prepares CreateRequest with the appropriate data, so that the php file cann be called and the party can
            //be added to the database
            MarkAsPlayedRequest markAsPlayedRequest = new MarkAsPlayedRequest(title, " "+firstSong, responseListener);
            RequestQueue queue = Volley.newRequestQueue(PlayerActivity.this);
            queue.add(markAsPlayedRequest);
        }
        @Override
        public void onLoading() {
        }
        @Override
        public void onVideoEnded() {
            Log.d("ON_VIDEO_END id/title: ", songID + " " + title);
            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        // Gets Response array from php
                        JSONObject jsonResponse = new JSONObject(response);
                        //Assign values of $response['email'] and $response['username'] to java boolean variables
                        //boolean markedAsPlayed = jsonResponse.getBoolean("markedAsPlayed");
                        String songID = jsonResponse.getString("songID");
                        //Log.d("MARKED_AS_PLAYED ", String.valueOf(markedAsPlayed));
                        if (songID.length() >= 1){
                            Intent intent = new Intent(PlayerActivity.this, PlayerActivity.class);
                            intent.putExtra("username", username);
                            intent.putExtra("password", password);
                            intent.putExtra("songID", songID);
                            intent.putExtra("title", title);
                            startActivity(intent);
                        }
                        else{
                            Toast.makeText(PlayerActivity.this, "Please have attendee add songs to playlist", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };
            //prepares CreateRequest with the appropriate data, so that the php file cann be called and the party can
            //be added to the database
            FetchNextVideoRequest fetchNextVideoRequest = new FetchNextVideoRequest(title, " "+firstSong, responseListener);
            RequestQueue queue = Volley.newRequestQueue(PlayerActivity.this);
            queue.add(fetchNextVideoRequest);
        }
        @Override
        public void onVideoStarted() {
        }
    };
}