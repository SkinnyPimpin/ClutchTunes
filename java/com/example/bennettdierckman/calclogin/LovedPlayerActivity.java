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

public class LovedPlayerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

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

        setContentView(R.layout.activity_loved_player);

        playerView = (YouTubePlayerView) findViewById(R.id.loved_player_view);
        playerView.initialize(YoutubeConnector.KEY, this);

        Intent intent = getIntent();
        //Reassigns the variables passed through .putExtras
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        songID = intent.getStringExtra("videoID");
        //firstSong = firstSong.substring(1);
        Log.d("PLAYER_ACTIVITY FIRST ", songID);


    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
        youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
        youTubePlayer.setPlaybackEventListener(playbackEventListener);
        if(!b) {
            youTubePlayer.loadVideo(songID);
//            youTubePlayer.setPlayerStateChangeListener(playerStateChangeListener);
//            youTubePlayer.setPlaybackEventListener(playbackEventListener);
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
        }
        @Override
        public void onLoading() {
        }
        @Override
        public void onVideoEnded() {

        }
        @Override
        public void onVideoStarted() {
        }
    };
}