package com.example.bennettdierckman.calclogin;
/*
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
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
//Allows Response from PHP
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //pulls from XML layout file activity_login.xml and assigns to java varialbe
        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etPassword = (EditText) findViewById(R.id.etPartyPassword);
        final TextView tvRegisterLink = (TextView) findViewById(R.id.tvRegisterLink);
        final TextView tvRecoverLink = (TextView) findViewById(R.id.tvRecoverAccount);
        final Button bLogin = (Button) findViewById(R.id.bSignIn);

        //this handles what happens when the register text view on activity_login is tapped
        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Switching from activity to another activity [activity -> activity = done with Intents]
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        //this handles what happens when the recover text view on activity_login is tapped
        tvRecoverLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get the username to make sure its not empty or 'malicious'
                final String username = etUsername.getText().toString();
                //Checks if the username is empty
                if(username.isEmpty()/* this does the same thing -> username.matches("")*/){
                    //if empty send them try again message
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("Enter a valid username to recover its account's password")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                    //Seperate message for if username is INVALID (checking for sql injectors)
                }else if (!username.matches("(?!.*/)[A-Za-z0-9][A-Za-z0-9 ]{0,18}[A-Za-z0-9]")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("Enter a valid username to recover its account's password")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                } else {
                    // Response received from the server
                    //This is where java file is listining for a response from the php file
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                //(response) in the next line is an array passed from the php file thru the code
                                // echo JSON_encode(response);
                                JSONObject jsonResponse = new JSONObject(response);
                                //if usernameExists = true, we have an actual user, and email has been sent
                                // email is sent via the php file.
                                boolean usernameExists = jsonResponse.getBoolean("usernameExists");
                                //log tag used when creating, and debugging to ensure that the boolean value of
                                // username exists is being recieved here in the java file
                                Log.d("usernameExists:", String.valueOf(usernameExists));
                                //if the username doesent exist (!usernameExists);
                                if (!usernameExists) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                    builder.setMessage(username + " was not found in out database, please enter a valid username.")
                                            .setNegativeButton("Retry", null)
                                            .create()
                                            .show();
                                //usernameExists is true and the email has been sent, Here we notify them of this
                                }else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                    builder.setMessage("A recovery email has been sent to " + username + "'s email")
                                            .setNegativeButton("Retry", null)
                                            .create()
                                            .show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    //This is where we begin the process of getting variables ready to be sent to php file
                    // //on server via RecoverRequest.java
                    RecoverRequest recoverRequest = new RecoverRequest( username, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                    queue.add(recoverRequest);
                }
            }
        });

        //this handles what happens when the loginButton on activity_login is tapped
        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //turns text from previously defined variables to strings for REGEX validation process
                final String username = etUsername.getText().toString();
                final String password = etPassword.getText().toString();
                //checks if username is empty
                if(username.isEmpty()/* this does the same thing -> username.matches("")*/){
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("username Empty")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                //Seperate message for if username is INVALID
                }else if (!username.matches("(?!.*/)[A-Za-z0-9][A-Za-z0-9 ]{0,18}[A-Za-z0-9]")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("invalid username")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                //seperate message for if password is empty
                }else if (password.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("Password Empty")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                //seperate message for if password is INVALID
                }else if (!password.matches("(?!.*/)[A-Za-z0-9][A-Za-z0-9 ]{0,18}[A-Za-z0-9]")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                    builder.setMessage("Password Must be Alpha Numeric, cant begin or end with a space")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                } else {
                    // Response received from the server
                    //This is where java file is listining for a response from the php file
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                //(response) in the next line is an array passed from the php file thru the code
                                // echo JSON_encode(response);
                                JSONObject jsonResponse = new JSONObject(response);
                                //if success = true, Their password matches their usermame
                                //if unVerified = true, Their account is not verified and they must do so before being
                                //able to login
                                boolean success = jsonResponse.getBoolean("success");
                                boolean unVerified = jsonResponse.getBoolean("unVerified");
                                Log.d("we are here", String.valueOf(success));
                                Log.d("we are unVerified", String.valueOf(unVerified));
                                //if their user and pass are correct, and their account HAS BEEN verified
                                if (success && !unVerified) {
                                    //Switch from login activity to ProfileActivity(containing the hamburgermenu) Via Intent
                                    Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                                    //intent.put extra assignes a new variable "usernamme" the value of username, for
                                    //Profile.java (the fragment called in the onCreate of ProfileActivity)
                                    intent.putExtra("username", username);
                                    intent.putExtra("password", password);
                                    LoginActivity.this.startActivity(intent);
                                //user pass match but they need to verify email
                                } else if (success && unVerified){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                    builder.setMessage("You need to verify your email")
                                            .setNegativeButton("Retry", null)
                                            .create()
                                            .show();
                                }
                                //Insuccsful login (if username doesent exist)
                                else if (!success && unVerified){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                    builder.setMessage("Login Failed")
                                            .setNegativeButton("Retry", null)
                                            .create()
                                            .show();
                                //Insuccsful login (if username exists but password dosent match)
                                }else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                                    builder.setMessage("Login Failed")
                                            .setNegativeButton("Retry", null)
                                            .create()
                                            .show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    //This is where we begin the process of getting variables ready to be sent to php file
                    // //on server via RecoverRequest.java
                    LoginRequest loginRequest = new LoginRequest(username, password, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                    queue.add(loginRequest);
                }
            }
        });
    }
}