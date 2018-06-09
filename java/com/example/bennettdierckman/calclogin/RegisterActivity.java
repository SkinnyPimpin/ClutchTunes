package com.example.bennettdierckman.calclogin;


// Regex Help https://stackoverflow.com/questions/38395910/java-regex-cant-start-or-end-with-slash-or-space-and-no-consecutive-slashes

// THIS IS WHERE DATA VALIDATION OCCOURS

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
//ALLOWS RESPONSE FROM PHP
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class RegisterActivity extends AppCompatActivity {

    // Initializers
    Bitmap bitmap;
    boolean check = true;
    Button SelectImageGallery, UploadImageServer;
    ImageView imageView;
    EditText imageName;
    ProgressDialog progressDialog ;
    String GetImageNameEditText; //this will become username from bundle
    String ImageName = "image_name" ;
    String ImagePath = "image_path" ;
    String ServerUploadPath ="https://cgi.soic.indiana.edu/~team51/img_upload_to_server.php" ;
////////////////

    boolean ofAge = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
//pulls from XML layout and assigns to java varialbe
        final EditText etUsername = (EditText) findViewById(R.id.etUsername);
        final EditText etPass = (EditText) findViewById(R.id.etPass);
        final EditText etPass2 = (EditText) findViewById(R.id.etPass2);
        final EditText etFirstName = (EditText) findViewById(R.id.etFirstName);
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        //D.O.B
        final EditText etMonth = (EditText) findViewById(R.id.etMonth);
        final EditText etDay = (EditText) findViewById(R.id.etDay);
        final EditText etYear = (EditText) findViewById(R.id.etYear);

        final Button bRegister = (Button) findViewById(R.id.bRegister);
        final Button bCancel = (Button) findViewById(R.id.bEndParty);

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(intent);
            }
        });

        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //turns text from previously defined variables to strings for REGEX validation process
                final String username = etUsername.getText().toString();
                final String password1 = etPass.getText().toString();
                final String password2 = etPass2.getText().toString();
                final String first_name = etFirstName.getText().toString();
                final String email = etEmail.getText().toString();
                String month = etMonth.getText().toString();
                String day = etDay.getText().toString();
                String year = etYear.getText().toString();

                //VALIDATION    VALIDATION    VALIDATION
                //Seperate message for if username is empty
                if(username.isEmpty()/* this does the same thing -> username.matches("")*/){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("username Empty")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                //Seperate message for if username is INVALID
                }else if (!username.matches("(?!.*/)[A-Za-z0-9][A-Za-z0-9 ]{0,18}[A-Za-z0-9]")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("invalid username")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                //seperate message for if password 1 is empty  (does not care if password 2 is empty cause it only cares that they match)
                }else if (password1.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("Password Empty")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                //seperate message for if password 1 is INVALID
                }else if (!password1.matches("(?!.*/)[A-Za-z0-9][A-Za-z0-9 ]{0,18}[A-Za-z0-9]")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("Password Must be Alpha Numeric, cant begin or end with a space")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                //Seperate message for if First name is empty
                }else if (first_name.isEmpty()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("First Name Empty")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                // Checks if First name is VALID, IT WILL NOT ALLOW ANY SPACES BETWEEN CHARACTERS
                }else if (!first_name.matches("(?!.*/)[A-Za-z][A-Za-z]{0,10}[A-Za-z]")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("Invalid First Name")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                //Checks if Email field is left empty   OR || if it Doesnt match androids EMAIL PATTERN MATCHER
                }else if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches() ) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("Not A Valid Email")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                //Checks if DOB fields are left empty
                }else if ( (month.isEmpty() || day.isEmpty() || year.isEmpty()) || (!month.matches("[0-9]{0,2}") || !day.matches("[0-9]{0,2}") || !year.matches("[0-9]{0,4}")) || (year.length()==3 || year.length()==1) ) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("Not A Valid Date")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                }else if (etPass.getText().toString().equals(etPass2.getText().toString())) {
                    //Correct incorrect month input
                    if (month.length()==1){
                        month = "0" + month;
                        // NOW THAT THE DATA IS VALID, Checks if PASSWORD 1 Matches PASSWORD 2,  if true, then it trys sending to php through RegisterRequest.php
                    }
                    if (day.length()==1) {
                        day = "0" + day;
                    }
                    if (year.length()==2) {
                        if (Integer.valueOf(year) < 20) {
                            year = "20" + year;
                        } else {
                            year = "19" + year;
                        }
                    }
                    /////////////////////////////
                    int intMonth = Integer.parseInt(month);
                    int intYear = Integer.parseInt(year);
                    int intDay = Integer.parseInt(day);

                    if (intMonth <= 12){
                        if(((month.equals("01")||month.equals("03")||month.equals("05")||month.equals("07")||month.equals("08")||month.equals("10")||month.equals("12")) && intDay <= 31) || (month.equals("04")||month.equals("06")||month.equals("09")||month.equals("11") && intDay <= 30)  || (month.equals("02") && intDay <=29)){
                            //Assemble dob straang to be sent to db via RegisterRequest
                            String DOB = month + "/" + day + "/" + year;
                            //PHP SECTION
                            Response.Listener<String> responseListener = new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        // Gets Response array from php
                                        JSONObject jsonResponse = new JSONObject(response);
                                        //Assign values of $response['email'] and $response['username'] to java boolean variables
                                        boolean emailSuccess = jsonResponse.getBoolean("email");
                                        boolean usernameSuccess = jsonResponse.getBoolean("username");
                                        //Conditionals with statements that either register the user, or inform them the email or username is taken
                                        if (emailSuccess && usernameSuccess) {
                                            GetImageNameEditText = username;
                                            int drawableResourceId = RegisterActivity.this.getResources().getIdentifier("profilepic", "drawable", RegisterActivity.this.getPackageName());
                                            bitmap = getBitmapFromVectorDrawable(RegisterActivity.this, drawableResourceId);
                                            ImageUploadToServerFunction();
                                            Toast.makeText(RegisterActivity.this,  "Confirmation Email has been sent to your inbox.", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            RegisterActivity.this.startActivity(intent);
                                        } else if (usernameSuccess) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                            builder.setMessage("Email Taken")
                                                    .setNegativeButton("Retry", null)
                                                    .create()
                                                    .show();
                                        } else {  //(emailSuccess)
                                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                                            builder.setMessage("Username Taken")
                                                    .setNegativeButton("Retry", null)
                                                    .create()
                                                    .show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            RegisterRequest registerRequest = new RegisterRequest(username, password1, first_name, email, DOB, responseListener);
                            RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                            queue.add(registerRequest);
                            //END PHP SECTION
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                            builder.setMessage("Not a valid birthday")
                                    .setNegativeButton("Retry", null)
                                    .create()
                                    .show();
                        }
                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        builder.setMessage("Not a valid birthday")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                    }
                    /////////////////////////////
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setMessage("Passwords No Match")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                }
            }
        });
    }

    public static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        @SuppressLint("RestrictedApi") Drawable drawable = AppCompatDrawableManager.get()
                .getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    // ImageUploadToServerFunction()  with AsyncTaskUploadClass
    public void ImageUploadToServerFunction(){
        ByteArrayOutputStream byteArrayOutputStreamObject ;
        byteArrayOutputStreamObject = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStreamObject);
        byte[] byteArrayVar = byteArrayOutputStreamObject.toByteArray();
        final String ConvertImage = Base64.encodeToString(byteArrayVar, Base64.DEFAULT);

        class AsyncTaskUploadClass extends AsyncTask<Void,Void,String> {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                progressDialog = ProgressDialog.show(RegisterActivity.this,"Registering account","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {
                super.onPostExecute(string1);
                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();

                // Printing uploading success message coming from server on android app.
                Toast.makeText(RegisterActivity.this,string1,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... params) {
                ImageProcessClass imageProcessClass = new ImageProcessClass();
                HashMap<String,String> HashMapParams = new HashMap<String,String>();
                HashMapParams.put(ImageName, GetImageNameEditText);
                HashMapParams.put(ImagePath, ConvertImage);
                String FinalData = imageProcessClass.ImageHttpRequest(ServerUploadPath, HashMapParams);

                return FinalData;
            }
        }
        AsyncTaskUploadClass AsyncTaskUploadClassOBJ = new AsyncTaskUploadClass();
        AsyncTaskUploadClassOBJ.execute();
    }
    ////////////////
// ImageProcessClass
    public class ImageProcessClass{
        public String ImageHttpRequest(String requestURL,HashMap<String, String> PData) {

            StringBuilder stringBuilder = new StringBuilder();

            try {
                URL url;
                HttpURLConnection httpURLConnectionObject ;
                OutputStream OutPutStream;
                BufferedWriter bufferedWriterObject ;
                BufferedReader bufferedReaderObject ;
                int RC ;

                url = new URL(requestURL);

                httpURLConnectionObject = (HttpURLConnection) url.openConnection();
                httpURLConnectionObject.setReadTimeout(19000);
                httpURLConnectionObject.setConnectTimeout(19000);
                httpURLConnectionObject.setRequestMethod("POST");
                httpURLConnectionObject.setDoInput(true);
                httpURLConnectionObject.setDoOutput(true);

                OutPutStream = httpURLConnectionObject.getOutputStream();

                bufferedWriterObject = new BufferedWriter(new OutputStreamWriter(OutPutStream, "UTF-8"));
                bufferedWriterObject.write(bufferedWriterDataFN(PData));
                bufferedWriterObject.flush();
                bufferedWriterObject.close();

                OutPutStream.close();

                RC = httpURLConnectionObject.getResponseCode();

                if (RC == HttpsURLConnection.HTTP_OK) {
                    bufferedReaderObject = new BufferedReader(new InputStreamReader(httpURLConnectionObject.getInputStream()));
                    stringBuilder = new StringBuilder();
                    String RC2;

                    while ((RC2 = bufferedReaderObject.readLine()) != null){
                        stringBuilder.append(RC2);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return stringBuilder.toString();
        }

        private String bufferedWriterDataFN(HashMap<String, String> HashMapParams) throws UnsupportedEncodingException {
            StringBuilder stringBuilderObject;
            stringBuilderObject = new StringBuilder();

            for (Map.Entry<String, String> KEY : HashMapParams.entrySet()) {

                if (check) {
                    check = false;
                }
                else {
                    stringBuilderObject.append("&");
                }

                stringBuilderObject.append(URLEncoder.encode(KEY.getKey(), "UTF-8"));
                stringBuilderObject.append("=");
                stringBuilderObject.append(URLEncoder.encode(KEY.getValue(), "UTF-8"));
            }

            return stringBuilderObject.toString();
        }

    }


}