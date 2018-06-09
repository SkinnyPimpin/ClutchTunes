package com.example.bennettdierckman.calclogin;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Bennett Dierckman on 12/22/2017.
 */

public class CreateRequest extends StringRequest {
    private static final String CREATE_REQUEST_URL = "https://cgi.soic.indiana.edu/~team51/CreateParty.php";  //PHP file location
    private Map<String, String> params;

    //Prepares android input to be sent to file location
    public CreateRequest(String partyTitle, String partyPassword, String partyDemographics, String partyRadius,  String username, String latitude, String longitude, Response.Listener<String> listener) {
        super(Method.POST, CREATE_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("partyTitle", partyTitle);
        params.put("partyPassword", partyPassword);
        params.put("partyDemographics", partyDemographics);
        params.put("partyRadius", partyRadius);
        params.put("username", username);
        params.put("longitude", longitude);
        params.put("latitude", latitude);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
