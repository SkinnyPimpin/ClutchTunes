package com.example.bennettdierckman.calclogin;

/**
 * Bennett Dierckman
 * 10/31/2017 updated
 *
 * https://www.youtube.com/watch?v=XT73k0ox7zc&t=2s                                                          Database/php Help
 * https://www.youtube.com/watch?v=M4VTi5-Aw20&t=654s and  https://www.youtube.com/watch?v=lHBfyQgkiAA&t=1s   Accessing Database
 *
 * https://www.tutorialspoint.com/android/android_php_mysql.htm
 * https://www.tutorialspoint.com/android/android_login_screen.htm
 *
 * */

        import com.android.volley.Response;
        import com.android.volley.toolbox.StringRequest;

        import java.util.HashMap;
        import java.util.Map;

public class RegisterRequest extends StringRequest {
    private static final String REGISTER_REQUEST_URL = "https://cgi.soic.indiana.edu/~team51/Register3.php";  //PHP file location
    private Map<String, String> params;

    //Prepares android input to be sent to file location
    public RegisterRequest(String username, String password1, String first_name, String email, String DOB, Response.Listener<String> listener) {
        super(Method.POST, REGISTER_REQUEST_URL, listener, null);
        params = new HashMap<>();
        params.put("username", username);
        params.put("password", password1);
        params.put("firstName", first_name);
        params.put("email", email);
        params.put("dob", DOB);
    }

    @Override
    public Map<String, String> getParams() {
        return params;
    }
}