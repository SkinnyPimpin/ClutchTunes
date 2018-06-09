package com.example.bennettdierckman.calclogin;
//this file is called from LoginActivity.java


import android.app.Activity;
import android.content.Intent;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Iterator;

public class ProfileActivity extends AppCompatActivity  {
    private DrawerLayout dl;
    private ActionBarDrawerToggle toggle;

    //Address of the php page *Needs http:// to function*
    String address = "http://cgi.soic.indiana.edu/~team51/populateMenuHeader.php";
    InputStream is = null; //accesses entity's data

    Intent tempIntent;

    //////////////////////////////////////////////                      <location>
//    protected static final String TAG = "location-updates-sample";
//
//    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;
//
//    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2;
//
//    private final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
//    private final static String LOCATION_KEY = "location-key";
//    private final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";
//
//    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
//    private static final int REQUEST_CHECK_SETTINGS = 10;
//
//    private ActivityProfileBinding mBinding;
//    private GoogleApiClient mGoogleApiClient;
//    private LocationRequest mLocationRequest;
//    private Location mCurrentLocation;
//    private Boolean mRequestingLocationUpdates;
//    private String mLastUpdateTime;
//    private String mLatitudeLabel;
//    private String mLongitudeLabel;
//    private String mLastUpdateTimeLabel;
    //////////////////////////////////////////////                      </location>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        StrictMode.setThreadPolicy ( (new StrictMode.ThreadPolicy.Builder().permitNetwork().build() ) ) ;

        new SendPostRequest().execute();

        //Navigation drawer layout assignments
        dl = (DrawerLayout) findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this,dl,R.string.open,R.string.close);
        dl.addDrawerListener(toggle);
        NavigationView nvDrawer = (NavigationView) findViewById(R.id.design_navigation_view);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //assigns content of navigation drawer
        setDrawerContent(nvDrawer);

        //Gets information from (if "Success & !unVerified") conditional of LoginActivity.java
        Intent intent = getIntent();
        //Reassigns the variables passed through .putExtras
        final String username = intent.getStringExtra("username");
        final String password = intent.getStringExtra("password");

        //Create tempIntent. This is used to help resolve the issue of profile_activity.xml showing up when login button
        //is pressed. Allows us to 'launchProfileActivity'
        this.tempIntent = intent;

        //Assigns values pulled in through .putExtras to be prepared to be sent to Proofile.java fragment
        //Activity -> Fragment, data is passed through bundles.
        Bundle bundle = new Bundle();
        bundle.putString ("username", username);
        bundle.putString("password", password);

        //Begin the Activity -> Fragment Transaction
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //Create a new instance of Profile.java Fragment
        Profile profile = new Profile();
        //Send the bundle. Failing to send bundle will result in app quiting becuase Profile.java Requires the bundle
        profile.setArguments(bundle);
        //Finishing transaction
        fragmentTransaction.replace(R.id.flcontent, profile);
        fragmentTransaction.commit();

    }



    //More menu Jazz /////////////////////////////////////////////////////////////////////////////////
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true ;
        }
        return super.onOptionsItemSelected(item);
    }

    //This is created to help resolve the profile_activity layout issue
    public void launchProfileActivity(){
        startActivity(this.tempIntent);
    }

    //gets intents from LoginActivity and assigns to bundle for Create.java Fragment
    public void launchCreateFragment() {
        //Gets the intent from LoginActivity.java
        Intent intent = getIntent();
        //assigns variables
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");

        //Prepare a new instance of Create.java
        Create Create = new Create();
        //Begin Fragment transaction
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flcontent, Create);
        //Assign bundle arguments
        Bundle bundle = new Bundle();
        bundle.putString ("username", username);
        bundle.putString("password", password);
        Create.setArguments(bundle);
        //finish transaction
        fragmentTransaction.commit();
    }

    //gets intents from LoginActivity and assigns to bundle for PSM.java Fragment
    public void launchJoinFragment() {
        //Gets the intent from LoginActivity.java
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");

        //Prepare a new instance of Create.java
        PSM psm = new PSM();
        //Begin Activity -> fragment transaction
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flcontent, psm);
        //assign bundle arguments
        Bundle bundle = new Bundle();
        bundle.putString ("username", username);
        bundle.putString("password", password);
        psm.setArguments(bundle);
        //finish transaction
        fragmentTransaction.commit();
    }

    //gets intents from LoginActivity and assigns to bundle for PSM.java Fragment
    public void launchLogoutFragment() {
        //Gets the intent from LoginActivity.java
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");
        //Prepare a new instance of Logout.java
        Logout logout = new Logout();
        //Begin Activity -> fragment transaction
        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flcontent, logout);
        //Set bunle arguments
        Bundle bundle = new Bundle();
        bundle.putString ("username", username);
        bundle.putString("password", password);
        logout.setArguments(bundle);
        //finish transaction
        fragmentTransaction.commit();
    }
    //Handles what happens when an option is selected from the menu
    public void selectItemDrawer(MenuItem menuItem) {
        Fragment myFragment = new Fragment();
        Activity myActivity = null;
        Class fragmentClass;
        //Switch statenemt for the different options of the menu
        switch (menuItem.getItemId()) {
            //Determines which option will become fragmentClass for try statement
            case R.id.profile:
                fragmentClass = Profile.class;
                break;
            case R.id.PSM:
                fragmentClass = PSM.class;
                break;
            case R.id.hostParty:
                fragmentClass = Create.class;
                break;
            case R.id.logout:
                fragmentClass = Logout.class;
                break;
            default:
                fragmentClass = Profile.class;
                break;
        }
        try {
            //if Profile.class, we call the launchProfileActivity method defined above
            if (fragmentClass == Profile.class){
                this.launchProfileActivity();  //Calling launchProfileActivity
            }
            //if Create.class, we call the launchCreateFragment method defined above
            else if(fragmentClass == Create.class){
                this.launchCreateFragment(); //Calling Create.java Fragment
            }
            //if PSM.class, we call the launchJoinFragment method defined above
            else if (fragmentClass == PSM.class){
                this.launchJoinFragment(); //Calling PSM.java Fragment
            }
            //Other wise we call the launchLogoutFragment method defined aboce
            else {
                this.launchLogoutFragment(); //Calling Logout Fragment
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
        //More menu Jazz
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        dl.closeDrawers();

    }
    private void setDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectItemDrawer(item);
                return true;
            }
        });
    }
    public class SendPostRequest extends AsyncTask<String, Void, String> {
        //Gets the intent from LoginActivity.java
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String password = intent.getStringExtra("password");

        NavigationView nvDrawer = (NavigationView) findViewById(R.id.design_navigation_view);
        View headerView = nvDrawer.getHeaderView(0);
        TextView menuUsername = (TextView) headerView.findViewById(R.id.menuUsername);
        TextView menuEmail = (TextView) headerView.findViewById(R.id.menuEmail);
        ImageView menuProfilePic = (ImageView) headerView.findViewById(R.id.menuProfilePic);

        protected void onPreExecute(){

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
            menuProfilePic.setImageBitmap(bmp);
        }

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
            Log.d("Result: ", result);

            super.onPostExecute(result);

            try {
                JSONObject jsonObj = new JSONObject(result);
                Log.d("here is jsonObj", String.valueOf(jsonObj));
                String email = jsonObj.getString("email");
                String firstName= jsonObj.getString("firstName");
                Log.d("here is EMAIL", email);
                menuUsername.setText(firstName);
                menuEmail.setText(email);

            } catch (JSONException e) {
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
}