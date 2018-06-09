package com.example.bennettdierckman.calclogin;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

class AttendeeInfluenceRequest extends StringRequest {
    //url that the php file is at
    private static final String ADD_SONG_REQUEST_URL = "https://cgi.soic.indiana.edu/~team51/AttendeeInfluenceRequest.php";
    //Binds the paramaters as assigned in loginActivity.java
    private Map<String, String> params;

    //Prepares android input to be sent to file location
    public AttendeeInfluenceRequest(String partyTitle, String videoTitle, String userInfluence, Response.Listener<String> listener) {
        super(Request.Method.POST, ADD_SONG_REQUEST_URL, listener, null);
        params = new HashMap<>();
        //assigns username from java file to new variable username that will be called in mysqli_escape(_POST['username'])
        params.put("partyTitle", partyTitle );
        params.put("videoTitle",videoTitle );
        params.put("userInfluence", userInfluence);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
