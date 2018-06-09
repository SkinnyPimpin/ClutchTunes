package com.example.bennettdierckman.calclogin;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by The Hamilton County Hustlers on 4/4/18.
 */

class FetchVideoRequest extends StringRequest {
    //url that the php file is at
    private static final String FETCH_VIDEO_REQUEST_URL = "https://cgi.soic.indiana.edu/~team51/FetchVideoRequest.php";
    //Binds the paramaters as assigned in loginActivity.java
    private Map<String, String> params;

    //Prepares android input to be sent to file location
    public FetchVideoRequest(String title, Response.Listener<String> listener) {
        super(Request.Method.POST, FETCH_VIDEO_REQUEST_URL, listener, null);
        params = new HashMap<>();
        //assigns username from java file to new variable username that will be called in mysqli_escape(_POST['username'])
        params.put("partyTitle", title.replace(" ", "_"));

    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
