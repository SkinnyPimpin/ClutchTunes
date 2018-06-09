package com.example.bennettdierckman.calclogin;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;

/**
 * A simple {@link Fragment} subclass.
 * https://stackoverflow.com/questions/28259534/how-to-rotate-image-in-imageview-on-button-click-each-time
 * Implements : https://github.com/jrvansuita/PickImage
 * https://androidjson.com/android-upload-image-server-using-php-mysql/
 */
public class EditProfile extends Fragment{

    // Initializers/////
    String address = "http://cgi.soic.indiana.edu/~team51/EditProfile.php"; // address for php that populates fields with existing account information
    InputStream is = null; //accesses entity's data
    Bitmap bitmap;
    boolean check = true;
    String imageName;
    ProgressDialog progressDialog ;
    String GetImageNameEditText; //this will become username from bundle
    String ImageName = "image_name" ;
    String ImagePath = "image_path" ;
    String ServerUploadPath ="https://cgi.soic.indiana.edu/~team51/img_upload_to_server.php" ;
    ////////////////

    public EditProfile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        //Get bundle arguments from Profile fragment
        Bundle bundle = getArguments();
        Log.d("EditProfile-bundle: ", String.valueOf(bundle));
        final String username = bundle.getString("username");
        final String password = bundle.getString("password");

        //This part of the code calls a method that sends a php to populate the fragment with the users existing information
        new SendPostRequest().execute();

        final ImageView imageView = (ImageView)view.findViewById(R.id.profilePic);
        imageName = username;
        GetImageNameEditText = imageName;

        ImageView SelectImageGallery = (ImageView) view.findViewById(R.id.profilePicPencil);
        ///
        TextView editPassword= view.findViewById(R.id.userPassword);
        ImageView passwordPencil = view.findViewById(R.id.imgPasswordPencil);
        //////
        TextView editUsername = view.findViewById(R.id.userUsername);
        //////
        TextView editFirstName = view.findViewById(R.id.userFirstName);
        ImageView firstNamePencil = view.findViewById(R.id.imgFirstNamePencil);
        //////
        TextView editDob = view.findViewById(R.id.userDOB); //dob has no pencil, changing dob not allowed
        //////
        TextView editEmail = view.findViewById(R.id.userEmail);
        ImageView emailPencil = view.findViewById(R.id.imgEmailPencil);
        //////
        TextView editBiography = view.findViewById(R.id.userBiography);
        ImageView biographyPencil = view.findViewById(R.id.imgBiographyPencil);
        //////
        Button bReturnProfile = view.findViewById(R.id.bReturnProfile);

        editUsername.setText(username);
        //// turn password into stars ////////
            String passwordStars = "";
                for(int i = 0; i < password.length(); i++)
                    passwordStars += " *";
        //////////////////////////////////////
        editPassword.setText(passwordStars);

        ////////get image from servver ///////
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
        //assign image from server to Profile picture image view
        imageView.setImageBitmap(bmp);



/////////////// START EDIT PENCILS and TEXT VIEWS //////////////////////////////////////////////////
        //////// PROFILE PIC /////
        SelectImageGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //PickImageDialog.build(new PickSetup()).show(getActivity());  /// LOOK HERE
                PickSetup setup = new PickSetup()
                        .setTitle("Select profile photo from:")
                        .setBackgroundColor(getResources().getColor(R.color.white))
                        .setIconGravity(Gravity.LEFT)
                        .setButtonOrientation(LinearLayoutCompat.HORIZONTAL);
                PickImageDialog.build(setup)
                        .setOnPickResult(new IPickResult() {
                            @Override
                            public void onPickResult(PickResult r) {
                                bitmap = r.getBitmap();
                                ImageUploadToServerFunction();
                                imageView.setImageBitmap(r.getBitmap());
                            }
                        }).show(getActivity().getSupportFragmentManager());
            }
        });

        //////// FIRSTNAME ////////////
        editFirstName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                //asssign arguments
                bundle.putString("username", username);
                bundle.putString("password", password);
                //Begin transaction
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //Create new Profile fragment instance
                ChangeFirstName changeFirstName = new ChangeFirstName();
                changeFirstName.setArguments(bundle);
                //finish transaction
                fragmentTransaction.replace(R.id.flcontent, changeFirstName);
                fragmentTransaction.commit();
            }
        });
        firstNamePencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                //asssign arguments
                bundle.putString("username", username);
                bundle.putString("password", password);
                //Begin transaction
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //Create new Profile fragment instance
                ChangeFirstName changeFirstName = new ChangeFirstName();
                changeFirstName.setArguments(bundle);
                //finish transaction
                fragmentTransaction.replace(R.id.flcontent, changeFirstName);
                fragmentTransaction.commit();
            }
        });
        ///////// DOB //////////////////
        editDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"Users not allowed to modify date of birth.", Toast.LENGTH_LONG).show();
            }
        });
        /////////  EMAIL //////////////
        editEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                //asssign arguments
                bundle.putString("username", username);
                bundle.putString("password", password);
                //Begin transaction
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //Create new Profile fragment instance
                ChangeEmailPt1 changeEmailPt1 = new ChangeEmailPt1();
                changeEmailPt1.setArguments(bundle);
                //finish transaction
                fragmentTransaction.replace(R.id.flcontent, changeEmailPt1);
                fragmentTransaction.commit();
            }
        });
        emailPencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                //asssign arguments
                bundle.putString("username", username);
                bundle.putString("password", password);
                //Begin transaction
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //Create new Profile fragment instance
                ChangeEmailPt1 changeEmailPt1 = new ChangeEmailPt1();
                changeEmailPt1.setArguments(bundle);
                //finish transaction
                fragmentTransaction.replace(R.id.flcontent, changeEmailPt1);
                fragmentTransaction.commit();
            }
        });
        //////////  USERNAME /////////
        editUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),  "Currently unable to change Username", Toast.LENGTH_LONG).show();

//                Bundle bundle = new Bundle();
//                //asssign arguments
//                bundle.putString("username", username);
//                bundle.putString("password", password);
//                //Begin transaction
//                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                //Create new Profile fragment instance
//                ChangeUsername changeUsername = new ChangeUsername();
//                changeUsername.setArguments(bundle);
//                //finish transaction
//                fragmentTransaction.replace(R.id.flcontent, changeUsername);
//                fragmentTransaction.commit();
            }
        });
//        usernamePencil.setOnClickListener(new View.OnClickListener() {
//           @Override
//            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                //asssign arguments
//                bundle.putString("username", username);
//                bundle.putString("password", password);
//                //Begin transaction
//                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                //Create new Profile fragment instance
//                ChangeUsername changeUsername = new ChangeUsername();
//                changeUsername.setArguments(bundle);
//                //finish transaction
//                fragmentTransaction.replace(R.id.flcontent, changeUsername);
//                fragmentTransaction.commit();
//            }
//        });
        ////////// PASSWORD ////////////
        editPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                //asssign arguments
                bundle.putString("username", username);
                bundle.putString("password", password);
                //Begin transaction
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //Create new Profile fragment instance
                ChangePassword changePassword= new ChangePassword();
                changePassword.setArguments(bundle);
                //finish transaction
                fragmentTransaction.replace(R.id.flcontent, changePassword);
                fragmentTransaction.commit();
            }
        });
        passwordPencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                //asssign arguments
                bundle.putString("username", username);
                bundle.putString("password", password);
                //Begin transaction
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //Create new Profile fragment instance
                ChangePassword changePassword= new ChangePassword();
                changePassword.setArguments(bundle);
                //finish transaction
                fragmentTransaction.replace(R.id.flcontent, changePassword);
                fragmentTransaction.commit();
            }
        });
        ////////// BIOGRAPHY ///////////
        editBiography.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                //asssign arguments
                bundle.putString("username", username);
                bundle.putString("password", password);
                //Begin transaction
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //Create new Profile fragment instance
                ChangeBiography changeBiography = new ChangeBiography();
                changeBiography.setArguments(bundle);
                //finish transaction
                fragmentTransaction.replace(R.id.flcontent, changeBiography);
                fragmentTransaction.commit();
            }
        });
        biographyPencil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                //asssign arguments
                bundle.putString("username", username);
                bundle.putString("password", password);
                //Begin transaction
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //Create new Profile fragment instance
                ChangeBiography changeBiography = new ChangeBiography();
                changeBiography.setArguments(bundle);
                //finish transaction
                fragmentTransaction.replace(R.id.flcontent, changeBiography);
                fragmentTransaction.commit();
            }
        });
///////////////////////////////////////// END EDIT PENCILS and EDIT TEXT VIEWS /////////////////////

///// RETURN TO PROFLE BUTTON  //////////////////////////////////////////////////////////////
        bReturnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
/////////////////////////////////////// END SAVE CHANGES and CANCEL BUTTON ////////////////////////////
        return view;
    }
/////////////////// END ON CREATE METHOD ///////////////////

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
                progressDialog = ProgressDialog.show(getActivity(),"Image is Uploading","Please Wait",false,false);
            }

            @Override
            protected void onPostExecute(String string1) {
                super.onPostExecute(string1);
                // Dismiss the progress dialog after done uploading.
                progressDialog.dismiss();

                // Printing uploading success message coming from server on android app.
                Toast.makeText(getActivity().getApplicationContext(),string1,Toast.LENGTH_LONG).show();
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

            TextView tvEmail = getView().findViewById(R.id.userEmail);
            TextView tvFirstName = getView().findViewById(R.id.userFirstName);
            TextView tvDob = getView().findViewById(R.id.userDOB);
            TextView tvBiography = getView().findViewById(R.id.userBiography);

            try {
                JSONObject jsonObj = new JSONObject(result);
                Log.d("HERE IS JSONObj", String.valueOf(jsonObj));
                String email = jsonObj.getString("email");
                String firstName= jsonObj.getString("firstName");
                String dob = jsonObj.getString("dob");
                String dialogue = jsonObj.getString("biography");
                tvEmail.setText(email);
                tvFirstName.setText(firstName);
                tvDob.setText(dob);
                tvBiography.setText(dialogue);

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
////////////////////////////////////////////////////////////////////////////////////////////////////
}
