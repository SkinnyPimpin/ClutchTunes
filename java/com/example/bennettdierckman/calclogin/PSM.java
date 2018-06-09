package com.example.bennettdierckman.calclogin;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

//https://www.youtube.com/watch?v=GrdkT5yICz8 INCREDIBALLY USEFULL Toutorial for
//populating listview with data from mysql database

//Useful tutorial explaining HttpURLConnection POST
//https://www.studytutorial.in/android-httpurlconnection-post-and-get-request-tutorial

public class PSM extends Fragment {
    //=============================================================================
    //initialize variable 'partyRefreshSwipe' of type 'SwipeRefreshLayout'
    private SwipeRefreshLayout partyRefreshSwipe;
    //==========================================================================

    //declare ListView, and arrayAdapter
    ListView lv;
    ArrayAdapter<String> adapter;
    //Address of the php page *Needs http:// to function*
    String address = "http://cgi.soic.indiana.edu/~team51/PSMRequest.php";
    InputStream is = null; //accesses entity's data
    String[] data;  //initialize string array data

    //// Initialize Longitude Latitude and String values of them
    double lon;
    double lat;
    String longitude;
    String latitude;
    static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;  //
////////////////////////////////////////////////////////////

    public PSM() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_psm, container, false);

        //////////////////////    ////////////////////    /////////////   ///////////
        //At the application side we have to call the function getService and pass the ID of the system service (say POWER_SERVICE) to get an handle to the service.
        locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        //Calling the get location method sets our initialized variables above equal to users long and lat
        getLocation();
        //Now we can assign those double values to String
        longitude = String.valueOf(lon);
        latitude = String.valueOf(lat);
        //////////////////////    ////////////////////    /////////////   ///////////

        Bundle bundle = getArguments();
        //Check bundle arguments are correct via log statement (Click run in bottom left to see)
        //Assigns the bundles arguments to variables to be used in this java file
        final String username = bundle.getString("username");
        final String password = bundle.getString("password");

        //Cancel button sends back to Profile fragment
        Button bCancel = (Button) view.findViewById(R.id.bEndParty);

        //get listView1 from xml
        lv = (ListView) view.findViewById(R.id.lvParties);

        //ALLOW NETWORK IN MAIN THREAD
        StrictMode.setThreadPolicy ( (new StrictMode.ThreadPolicy.Builder().permitNetwork().build() ) ) ;

        new SendPostRequest().execute();

        //=============================================================================
        //get swipe refresh layout from xml
        // Lookup the swipe container view
        partyRefreshSwipe = (SwipeRefreshLayout) view.findViewById(R.id.partyRefreshSwipe);

        // SET SWIPE LISTENER which triggers new data loading async task which enables us to have a loading spinner)
        partyRefreshSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                new SendPostRequest().execute();
                partyRefreshSwipe.setRefreshing(false);
            }
        });
//=============================================================================



        //SET ON ITEM CLICK LISTENER
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String theTitle = data[i];
                Log.d("the title up top: ", theTitle);

                //Response listener to handle response from php file
                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Gets Response array from php
                            JSONObject jsonResponse = new JSONObject(response);
                            //Success represents the passwords being equal
                            boolean partyIsPublic = jsonResponse.getBoolean("partyIsPublic");
                            //Loging the value of boolean partySuccess for testing/debugging purposes
                            Log.d("Party password Correct", "adsfasdf");
                            Log.d("Party is Public: ", String.valueOf(partyIsPublic));
                            //if the JoinSelectedRequest.php is successful ( if(success) )
                            if(partyIsPublic){
                                List<String> existingVideoInfluences = new ArrayList<>();
                                List<String> existingVideoTitles = new ArrayList<>();
                                Bundle bundle = new Bundle();
                                //asssign arguments
                                bundle.putString("username", username);
                                bundle.putStringArrayList("existingVideoInfluences", (ArrayList<String>) existingVideoInfluences);
                                bundle.putStringArrayList("existingVideoTitles", (ArrayList<String>) existingVideoTitles);
                                bundle.putString("title", theTitle);
                                bundle.putString("password", password);
                                //Begin transaction
                                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                //Create new Profile fragment instance
                                AttendeeParty attendeeParty = new AttendeeParty();
                                attendeeParty.setArguments(bundle);
                                //finish transaction
                                fragmentTransaction.replace(R.id.flcontent, attendeeParty);
                                fragmentTransaction.commit();

                            }else{
                                ArrayList<String> existingVideoInfluences = new ArrayList<>();
                                ArrayList<String> existingVideoTitles = new ArrayList<>();
                                Bundle bundle = new Bundle();
                                //asssign arguments
                                bundle.putString("username", username);
                                bundle.putStringArrayList("existingVideoInfluences", existingVideoInfluences);
                                bundle.putStringArrayList("existingVideoTitles", existingVideoTitles);
                                bundle.putString("password", password);
                                bundle.putString("title", theTitle);
                                //Begin transaction
                                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                //Create new Profile fragment instance
                                AttendeeJoinSelected attendeeJoinSelected = new AttendeeJoinSelected();
                                attendeeJoinSelected.setArguments(bundle);
                                //finish transaction
                                fragmentTransaction.replace(R.id.flcontent, attendeeJoinSelected);
                                fragmentTransaction.commit();                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                };
                //prepares CreateRequest with the appropriate data, so that the php file cann be called and the party can
                //be added to the database
                Log.d("Title Going in", theTitle);
                PublicRequest publicRequest = new PublicRequest(theTitle.replace(" ", "_"), responseListener);
                RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                queue.add(publicRequest);
            }
        });


        // if they hit the cancel button they are sent back to Profile Fragment, which requires a bundle otherwise
        // app will fail
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Create new bundle
                Bundle bundle = new Bundle();
                //asssign arguments
                bundle.putString("username", username);
                bundle.putString("password", password);
                //Begin transaction
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //Create new Profile fragment instance
                Profile profile = new Profile();
                profile.setArguments(bundle);
                //finish transaction
                fragmentTransaction.replace(R.id.flcontent, profile);
                fragmentTransaction.commit();
            }
        });
        return view;
    }
///////////////////////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////////////////
    public class SendPostRequest extends AsyncTask<String, Void, String> {
        Bundle bundle = getArguments();
        //Check bundle arguments are correct via log statement (Click run in bottom left to see)
        //Assigns the bundles arguments to variables to be used in this java file
        final String username = bundle.getString("username");
        final String password = bundle.getString("password");

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {
            try{
                URL url = new URL(address);

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("username", username);
                postDataParams.put("latitude", latitude);
                postDataParams.put("longitude", longitude);
                Log.d("HERE ARE LE Params: ", postDataParams.toString());

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(postDataParams));

                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK){

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while((line = br.readLine()) != null) {
                        sb.append(line);
                        break;
                    }
                    br.close();
                    return sb.toString();
                }
                else{
                    return new String("false : " + responseCode);
                }
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                Log.d("Here is result", result);
                if (result.equals("[]")){
                    lv.setAdapter(null);
                }
                else {
                    result = result.substring(1, result.length() - 1);
                    data = result.split(",");
                    for (int i = 0; i < data.length; i++) {
                        data[i] = data[i].substring(1, data[i].length() - 1);
                        data[i] = data[i].replace("_", " ");
                    }

                    Log.d("Here is data Extra Low:", Arrays.toString(data));
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.white_text_list, R.id.parties_list, data);
                    lv.setAdapter(adapter);
                }

            }catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getPostDataString(JSONObject params) throws Exception {

        StringBuilder result = new StringBuilder();
        boolean first = true;

        Iterator<String> itr = params.keys();

        while(itr.hasNext()){

            String key = itr.next();
            Object value = params.get(key);

            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(key, "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(value.toString(), "UTF-8"));
        }
        return result.toString();
    }

    /////////////////////////////////////////////////////////////////////////////
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


}
