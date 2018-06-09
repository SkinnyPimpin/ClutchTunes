package com.example.bennettdierckman.calclogin;
// make create password field into regular text field
// and for joining party
//
// Party public or private instead of time frame
//
//  No radio buttons on create fragment either
//
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;

import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;



public class Initialize extends Fragment{

    //// Initialize Longitude Latitude and String values of them
    double lon;
    double lat;
    String longitude;
    String latitude;
    static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;  //
    ////////////////////////////////////////////////////////////


    public Initialize() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_initialize, container, false);

        //////////////////////    ////////////////////    /////////////   ///////////
        //At the application side we have to call the function getService and pass the ID of the system service (say POWER_SERVICE) to get an handle to the service.
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        //Calling the get location method sets our initialized variables above equal to users long and lat
        getLocation();
        //Now we can assign those double values to String
        longitude = String.valueOf(lon);
        latitude = String.valueOf(lat);
        //////////////////////    ////////////////////    /////////////   ///////////

        //Get bundle arguments
        Bundle bundle = getArguments();
        //Assign bundle arguments to variables to be used in this java file
        final String username = bundle.getString("username");
        final String password = bundle.getString("password");
        final String title = bundle.getString("title");
        final String partyPassword = bundle.getString("PartyPassword");
        final String demographics = bundle.getString("demographics");
        final String radius = bundle.getString("radius");

        //Create values to hold content of initialize_fragment.xml text views
        TextView tvTitle = (TextView) view.findViewById(R.id.tvPartyTitle);
        TextView tvPassword = (TextView) view.findViewById(R.id.tvPartyPassword);
        TextView tvDemographics = (TextView) view.findViewById(R.id.tvPartyDemographics);
        TextView tvRadius = (TextView) view.findViewById(R.id.tvPartyRadius);

        //Assigns bundle arguments to text view variables from above
        tvTitle.setText(title);
        if (partyPassword.equals("")) {
            tvPassword.setText("Public Party");
        }else{
            tvPassword.setText(partyPassword);

        }
        tvPassword.setText(partyPassword);
        tvDemographics.setText(demographics);
        tvRadius.setText(radius);

        //Bringing in buttons for next section of code
        Button bGoBack = (Button) view.findViewById(R.id.bReturn2Party);
        Button bStartParty = (Button) view.findViewById(R.id.bStartParty);
        Button bScreenShot = (Button) view.findViewById(R.id.bScreenShot);

        //When the start party button is selected -> SWITCH FROM FRAGMENT TO FRAGMENT
        bStartParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dbTitle = title.replace(" ", "_");
                //Response listener to handle response from php file
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Gets Response array from php
                            JSONObject jsonResponse = new JSONObject(response);
                            //Success represents the party being successfully created and put in the database
                            boolean partySuccess = jsonResponse.getBoolean("success");
                            boolean titleSuccess = jsonResponse.getBoolean("availableTitle");
                            //Loging the value of boolean partySuccess for testing/debugging purposes
                            Log.d("we are here", String.valueOf(partySuccess));
                            //if the party Create php is successfull ( if(party success) )
                            if (titleSuccess && partySuccess) {
                                //Create bundle for HostParty Fragment
                                Bundle bundle = new Bundle();
                                //Add this to bundle so the party title can be displayed on the HostParty Fragment
                                bundle.putString("title", title);
                                //for eventuall Profile fragment use
                                bundle.putString("username", username);
                                bundle.putString("password", password);
                                //Begining the fragment transaction
                                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                //Creating a new instance of HostParty
                                HostParty hostParty = new HostParty();
                                hostParty.setArguments(bundle);
                                //Finishing fragment transaction
                                fragmentTransaction.replace(R.id.flcontent, hostParty);
                                fragmentTransaction.commit();

                                //if registration fails
                            } else if (!titleSuccess) {
                                Toast.makeText(getActivity(), "Sorry, that party title is already in use", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getActivity(), "Something Went Wrong!", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                //prepares CreateRequest with the appropriate data, so that the php file cann be called and the party can
                //be added to the database
                CreateRequest createRequest = new CreateRequest(dbTitle, partyPassword, demographics, radius, username, latitude, longitude, responseListener);
                RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                queue.add(createRequest);
            }
        });

        //The following occours when go back button is selected
        bGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create bundle with information of profile
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                bundle.putString("password", password);
                //Begin transaction FRAGMENT->Fragment
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //Create new instance of Create Fragment
                Create Create = new Create();
                Create.setArguments(bundle);
                //finish fragment transaction
                fragmentTransaction.replace(R.id.flcontent, Create);
                fragmentTransaction.commit();
            }
        });

        //The following occours when take screenshot button is selected
        bScreenShot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeScreenshot();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Screenshot sent to device's gallery.")
                        .setNegativeButton("Okay", null)
                        .create()
                        .show();
            }
        });
        return view;
    }
    //////////////////////////////////////////////////////////////////////////////
    //location sources
    //https://hafizahusairi.com/2017/08/26/tutorial-get-current-latitude-longitude-android-studio-2017/
    //https://www.youtube.com/watch?v=oihuhw0ayXA
    //https://www.geodatasource.com/developers/php
    /////////////////////////////////////////////////////////////////////////
    public void getLocation() {
        // IF WE DONT HAVE PERMISSION GRANTED
        if(ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // REQUEST PERMISSION
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            // IF WE HAVE PERMISSION, get last known location from network provider
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            // if that location is not null
            if (location != null) {
                lat = location.getLatitude(); // assign lattitude value to already initialized lat double
                lon = location.getLongitude(); // assign longitude value to already initialized lon double
                Log.d("here is lattitude", String.valueOf(lat));
                Log.d("here is longitude", String.valueOf(lon));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,@NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        /// If we have to request perissions.. after requesting it, if permission is granted, call get location
        switch (requestCode) {
            case REQUEST_LOCATION:
                getLocation();
                break;
        }
    }
//////////////////////////////////////////////////////////////////////////////

    private void takeScreenshot() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getActivity().getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

        } catch (Throwable e) {
            // Several error may come out with file handling or DOM
            e.printStackTrace();
        }
    }

}