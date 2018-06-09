package com.example.bennettdierckman.calclogin;


import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class LovedVideoOptions extends Fragment {

    public String address = "http://cgi.soic.indiana.edu/~team51/LovedThumbnailRequest.php"; // address for php that populates fields with existing account information
    public String videoID = "";
    public String thumbnailURL = "";

    public LovedVideoOptions() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_loved_video_options, container, false);
        new SendThumbnailRequest().execute();
        Bundle bundle = getArguments();
        Log.d("profile-bundle: ", String.valueOf(bundle));
        final String username = bundle.getString("username");
        final String password = bundle.getString("password");
        final String videoTitle = bundle.getString("videoTitle");

        ImageView ivThumbnail = view.findViewById(R.id.ivThumbnail);
        TextView tvVideoTitle = (TextView) view.findViewById(R.id.tvVideoTitle);
        Button bWatchVideo = (Button) view.findViewById(R.id.bWatchVideo);
        Button bRemoveVideo = (Button) view.findViewById(R.id.bRemove);
        Button bShareVideo= (Button) view.findViewById(R.id.bShare);
        Button bReturn2Profile = (Button) view.findViewById(R.id.bReturn2Profile);

        tvVideoTitle.setText(videoTitle);

        bReturn2Profile.setOnClickListener(new View.OnClickListener() {
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
                Profile profile = new Profile();
                //Set bundle arguments
                profile.setArguments(bundle);
                //Finish fragment transaction
                fragmentTransaction.replace(R.id.flcontent, profile);
                fragmentTransaction.commit();
            }
        });

        bRemoveVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create new bundle for Profile fragment
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                bundle.putString("password", password);
                bundle.putString("videoTitle", videoTitle);
                bundle.putString("videoID", videoID);
                //Begin fragment transacion
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //Create new instance of Profile fragment
                DeleteLoved deleteLoved = new DeleteLoved();
                deleteLoved.setArguments(bundle);
                //Finish fragment transaction
                fragmentTransaction.replace(R.id.flcontent, deleteLoved);
                fragmentTransaction.commit();
            }
        });

        bWatchVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("OPTIONS VIDEO ID", videoID);
                Intent intent = new Intent(getActivity().getBaseContext(), LovedPlayerActivity.class);
                intent.putExtra("username", username);
                intent.putExtra("password", password);
                Log.d("OPTIONS VIDEO ID", videoID);
                intent.putExtra("videoID", videoID);
                startActivity(intent);
            }
        });

        bShareVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //https://stackoverflow.com/questions/17167701/how-to-activate-share-button-in-android-app
                //Thank you, Basavaraj Hampali
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String youtubeLink = "https://www.youtube.com/watch?v=" + videoID;
                String shareBody = youtubeLink+ " \nHeard this song at a ClutchTunes Party check out the App yourself in the GooglePlay Store SOON!.";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Check out this Clutch Youtube video:");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        return view;
    }

    public class SendThumbnailRequest extends AsyncTask<String, Void, String> {
        Bundle bundle = getArguments();
        //Check bundle arguments are correct via log statement (Click run in bottom left to see)
        //Assigns the bundles arguments to variables to be used in this java file
        final String username = bundle.getString("username");
        final String videoTitle = bundle.getString("videoTitle");

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {
            try{
                URL url = new URL(address);

                JSONObject postDataParams = new JSONObject();
                postDataParams.put("username", username.replace(" ", "_"));
                postDataParams.put("videoTitle", videoTitle);

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
                JSONObject jsonObj = new JSONObject(result);
                Log.d("HERE IS JSONObj", String.valueOf(jsonObj));
                thumbnailURL = jsonObj.getString("thumbnailURL");
                videoID = jsonObj.getString("videoLink");
                final ImageView ivThumbnail = (ImageView) getView().findViewById(R.id.ivThumbnail);

                Picasso.with(getActivity().getApplicationContext()).load(thumbnailURL).into(ivThumbnail);

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
