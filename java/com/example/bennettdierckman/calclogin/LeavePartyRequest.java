package com.example.bennettdierckman.calclogin;

/**
 * Created by BennettDierckman on 1/31/18.
 */

import com.android.volley.toolbox.StringRequest;
import com.android.volley.Response;
import java.util.HashMap;
import java.util.Map;

public class LeavePartyRequest extends StringRequest {
    private static final String LEAVEPARTY_REQUEST_URL = "https://cgi.soic.indiana.edu/~team51/LeavePartyRequest.php";
    private Map<String, String> params;

    //Prepares android input to be sent to file location
    public LeavePartyRequest(String title, Response.Listener<String> listener) {
        super(Method.POST, LEAVEPARTY_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("partyTitle", title.replace(" ", "_"));
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}