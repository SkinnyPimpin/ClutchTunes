package com.example.bennettdierckman.calclogin;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Smith on 3/25/2018.
 */

public class WithinBoundsRequest extends StringRequest {
    private static final String BOUNDS_REQUEST_URL = "https://cgi.soic.indiana.edu/~team51/withinBounds.php";  //PHP file location
    private Map<String, String> params;

    //Prepares android input to be sent to file location
    public WithinBoundsRequest(String username, String partyTitle, String latitude, String longitude, Response.Listener<String> listener) {
        super(Request.Method.POST, BOUNDS_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("partyTitle", partyTitle);
        params.put("longitude", longitude);
        params.put("latitude", latitude);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
