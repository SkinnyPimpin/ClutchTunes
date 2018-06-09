package com.example.bennettdierckman.calclogin;


import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePasswordPt2 extends Fragment {


    public ChangePasswordPt2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_password_pt2, container, false);
        Bundle bundle = getArguments();
        final String username = bundle.getString("username");
        final String password = bundle.getString("password");
        Log.d("ChangePassword Bundle:", String.valueOf(bundle));

        final EditText etNewPassword = view.findViewById(R.id.etNewPassword);
        final EditText etConfirmNewPassword = view.findViewById(R.id.etConfirmNewPassword);
        Button bSaveChanges = view.findViewById(R.id.bNext);
        Button bGoBack = view.findViewById(R.id.bReturn2Party);

        bSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newPassword = etNewPassword.getText().toString();
                String confirmNewPassword = etConfirmNewPassword.getText().toString();

                if (!newPassword.matches("(?!.*/)[A-Za-z0-9][A-Za-z0-9 ]{0,18}[A-Za-z0-9]")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Password Must be Alpha Numeric, cant begin or end with a space")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                }else if(!confirmNewPassword.equals(newPassword)){
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Passwords do not match")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                }else {
                    //Response listener to handle response from php file
                    Response.Listener<String> responseListener = new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //Create new bundle for Profile fragment
                            Bundle bundle = new Bundle();
                            bundle.putString("username", username);
                            bundle.putString("password", password);
                            //Begin fragment transacion
                            android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            //Create new instance of Profile fragment
                            EditProfile editProfile = new EditProfile();
                            editProfile.setArguments(bundle);
                            //Finish fragment transaction
                            fragmentTransaction.replace(R.id.flcontent, editProfile);
                            fragmentTransaction.commit();
                        }
                    };
                    //prepares CreateRequest with the appropriate data, so that the php file cann be called and the party can
                    //be added to the database
                    ChangePasswordRequest changePasswordRequest = new ChangePasswordRequest(newPassword, username, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                    queue.add(changePasswordRequest);
                }
            }
        });

        bGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

}
