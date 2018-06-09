package com.example.bennettdierckman.calclogin;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by bennettdierckman on 1/11/18.
 */

public class RecoverRequest extends StringRequest {

    //url that the php file is at
    private static final String RECOVER_REQUEST_URL = "https://cgi.soic.indiana.edu/~team51/recoverRequest.php";
    //Binds the paramaters as assigned in loginActivity.java
    private Map<String, String> params;

    //Prepares android input to be sent to file location
    public RecoverRequest(String username, Response.Listener<String> listener) {
        super(Method.POST, RECOVER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        //assigns username from java file to new variable username that will be called in mysqli_escape(_POST['username'])
        params.put("username", username);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}
