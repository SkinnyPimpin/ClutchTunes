package com.example.bennettdierckman.calclogin;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bennett Dierckman on 12/22/2017.
 */

public class PSMRequest extends StringRequest {
    private static final String END_REQUEST_URL = "https://cgi.soic.indiana.edu/~team51/PSMRequest.php";  //PHP file location
    private Map<String, String> params;

    //Prepares android input to be sent to file location
    public PSMRequest(Response.Listener<String> listener) {
        super(Method.POST, END_REQUEST_URL, listener, null);
        params = new HashMap<>();
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}