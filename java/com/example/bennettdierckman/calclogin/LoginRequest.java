package com.example.bennettdierckman.calclogin;

/**
 * Created by Bennett Dierckman on 10/28/2017.
 *
 * Bennett Dierckman
 * 10/31/2017 updated
 *
 * https://www.youtube.com/watch?v=XT73k0ox7zc&t=2s                                                          Database/php Help
 * https://www.youtube.com/watch?v=M4VTi5-Aw20&t=654s and  https://www.youtube.com/watch?v=lHBfyQgkiAA&t=1s   Accessing Database
 *
 * https://www.tutorialspoint.com/android/android_php_mysql.htm
 * https://www.tutorialspoint.com/android/android_login_screen.htm
 *
 *
 */

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginRequest extends StringRequest {
    private static final String LOGIN_REQUEST_URL = "https://cgi.soic.indiana.edu/~team51/Login3.php";
    private Map<String, String> params;

//Prepares android input to be sent to file location
    public LoginRequest(String username, String password, Response.Listener<String> listener) {
        super(Method.POST, LOGIN_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("password", password);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}