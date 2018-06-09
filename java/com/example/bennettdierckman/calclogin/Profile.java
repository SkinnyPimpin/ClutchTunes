package com.example.bennettdierckman.calclogin;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;


public class Profile extends Fragment {

    String address = "http://cgi.soic.indiana.edu/~team51/populateProfile.php"; // address for php that populates fields with existing account information
    String address2 = "http://cgi.soic.indiana.edu/~team51/LovedListRequest.php"; // address for php that populates fields with existing account information
    //initialize variable 'partyRefreshSwipe' of type 'SwipeRefreshLayout'
    private SwipeRefreshLayout lovedListRefreshSwipe;
    InputStream is = null; //accesses entity's data
    String[] data;  //initialize string array data
    //declare ListView, and arrayAdapter
    ListView lv;
    ArrayAdapter<String> adapter;

    public Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        new SendPostRequest().execute();
        new SendLovedRequest().execute();
        Bundle bundle = getArguments();
        Log.d("profile-bundle: ", String.valueOf(bundle));
        final String username = bundle.getString("username");
        final String password = bundle.getString("password");

        ImageView profilePic = view.findViewById(R.id.profilePic);
        ImageButton editBtn = view.findViewById(R.id.ibEdit);
        TextView tvUsername = (TextView) view.findViewById(R.id.tvUsername);
        //get swipe refresh layout from xml
        lovedListRefreshSwipe = (SwipeRefreshLayout) view.findViewById(R.id.lovedListRefreshSwipe);
        lv = (ListView) view.findViewById(R.id.lvLovedList);

        tvUsername.setText(username);
        //Grabs users photo from server
        URL url = null;
        try {
            url = new URL("https://cgi.soic.indiana.edu/~team51/images/"+username+".png");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Bitmap bmp = null;
        try {
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        //assign photo to profilePic ImageView
        profilePic.setImageBitmap(bmp);

        // SET SWIPE LISTENER which triggers new data loading async task which enables us to have a loading spinner)
        lovedListRefreshSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new SendPostRequest().execute();
                lovedListRefreshSwipe.setRefreshing(false);
            }
        });

        //SET ON ITEM CLICK LISTENER
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String theVideo = data[i];
                Bundle bundle = new Bundle();
                //asssign arguments
                bundle.putString("videoTitle", theVideo);
                bundle.putString("username", username);
                bundle.putString("password", password);
                //Begin transaction
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //Create new Profile fragment instance
                LovedVideoOptions lovedVideoOptions  = new LovedVideoOptions();
                lovedVideoOptions.setArguments(bundle);
                //finish transaction
                fragmentTransaction.replace(R.id.flcontent, lovedVideoOptions);
                fragmentTransaction.commit();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Prepare Profile.java bundle
                Bundle bundle = new Bundle();
                //Set the bundle arguments
                bundle.putString("username", username);
                bundle.putString("password", password);
                //Begin Fragment transaction
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //Create new profile instance
                EditProfile editProfile = new EditProfile();
                //Set bundle arguments
                editProfile.setArguments(bundle);
                //Finish fragment transaction
                fragmentTransaction.replace(R.id.flcontent, editProfile);
                fragmentTransaction.commit();
            }
        });
        return view;
    }

    /////////////////// BEGIN SEND POST REQUEST METHOD//////////
    public class SendPostRequest extends AsyncTask<String, Void, String> {

        Bundle bundle = getArguments();
        //Check bundle arguments are correct via log statement (Click run in bottom left to see)
        //Assigns the bundles arguments to variables to be used in this java file
        final String username = bundle.getString("username");
        final String password = bundle.getString("password");

        protected void onPreExecute(){}

        @Override
        protected String doInBackground(String... arg0) {
            try{
                URL url = new URL(address);

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("username", username);
                Log.d("Params", postDataParams.toString());

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
            //Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG).show();
            super.onPostExecute(result);

            TextView tvBio = getView().findViewById(R.id.userBiography);

            try {
                JSONObject jsonObj = new JSONObject(result);
                Log.d("HERE IS JSONObj", String.valueOf(jsonObj));
                String biography = jsonObj.getString("biography");
                tvBio.setText(biography);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class SendLovedRequest extends AsyncTask<String, Void, String> {
        Bundle bundle = getArguments();
        //Check bundle arguments are correct via log statement (Click run in bottom left to see)
        //Assigns the bundles arguments to variables to be used in this java file
        final String username = bundle.getString("username");

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {
            try{
                URL url = new URL(address2);

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("username", username.replace(" ", "_"));
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
                        data[i] = data[i].substring(1, data[i].length() - 1).replace("\\", "").replace("u2013", "–");
                    }
                    Log.d("Here is data Extra Low:", Arrays.toString(data));
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.playlist_item, R.id.songs_list, data);
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
////////////////////////////////////////////////////////////////////////////////////////////////////


}
