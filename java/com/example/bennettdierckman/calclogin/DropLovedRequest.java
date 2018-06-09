package com.example.bennettdierckman.calclogin;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

class DropLovedRequest extends StringRequest{
    //url that the php file is at
    private static final String ADD_SONG_REQUEST_URL = "https://cgi.soic.indiana.edu/~team51/DropLovedRequest.php";
    //Binds the paramaters as assigned in loginActivity.java
    private Map<String, String> params;

    //Prepares android input to be sent to file location
    public DropLovedRequest(String username, String videoID, Response.Listener<String> listener) {
        super(Request.Method.POST, ADD_SONG_REQUEST_URL, listener, null);
        params = new HashMap<>();
        //assigns username from java file to new variable username that will be called in mysqli_escape(_POST['username'])
        params.put("username", username );
        params.put("videoTitle", videoID );
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
