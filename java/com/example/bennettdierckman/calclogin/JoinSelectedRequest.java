package com.example.bennettdierckman.calclogin;

import com.android.volley.toolbox.StringRequest;

/**
 * Created by BennettDierckman on 1/31/18.
 */

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class JoinSelectedRequest extends StringRequest {
    private static final String JOINSELECTED_REQUEST_URL = "https://cgi.soic.indiana.edu/~team51/JoinSelectedRequest.php";
    private Map<String, String> params;

    //Prepares android input to be sent to file location
    public JoinSelectedRequest(String title, String passwordInput, Response.Listener<String> listener) {
        super(Method.POST, JOINSELECTED_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("partyTitle", title);
        params.put("partyPassword", passwordInput);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}