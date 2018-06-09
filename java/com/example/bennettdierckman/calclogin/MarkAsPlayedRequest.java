package com.example.bennettdierckman.calclogin;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

class MarkAsPlayedRequest extends StringRequest{
    //url that the php file is at
    private static final String MarkAsPlayed_REQUEST_URL = "https://cgi.soic.indiana.edu/~team51/MarkAsPlayedRequest.php";
    //Binds the paramaters as assigned in loginActivity.java
    private Map<String, String> params;

    //Prepares android input to be sent to file location
    public MarkAsPlayedRequest(String partyTitle, String songID, Response.Listener<String> listener) {
        super(Request.Method.POST, MarkAsPlayed_REQUEST_URL, listener, null);
        params = new HashMap<>();
        //assigns username from java file to new variable username that will be called in mysqli_escape(_POST['username'])
        Log.d("FETCH NEXT REQUEST", partyTitle + " " + songID);
        params.put("partyTitle", partyTitle.replace(" ", "_"));
        params.put("songID", songID.substring(1));
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
