package com.example.bennettdierckman.calclogin;

/**
 * Created by rachelhackett on 3/1/18.
 */

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class locationRequest extends StringRequest {
    private static final String LOCATION_REQUEST_URL = "https://cgi.soic.indiana.edu/~team51/locationTest.php";  //PHP file location
    private Map<String, String> params;

    //Prepares android input to be sent to file location
    public locationRequest(String latitude, String longitude, com.android.volley.Response.Listener<String> listener) {
        super(Method.POST, LOCATION_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("latitude", latitude);
        params.put("longitude", longitude);
        // params.put("success"), false);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
