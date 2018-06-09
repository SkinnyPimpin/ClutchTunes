package com.example.bennettdierckman.calclogin;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bennett Dierckman on 12/22/2017.
 */

public class EndRequest extends StringRequest {
    private static final String END_REQUEST_URL = "https://cgi.soic.indiana.edu/~team51/endParty2.php";  //PHP file location
    private Map<String, String> params;

    //Prepares android input to be sent to file location
    public EndRequest(String title, Response.Listener<String> listener) {
        super(Method.POST, END_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("title", title);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
