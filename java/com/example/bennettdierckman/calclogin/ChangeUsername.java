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


/**
 * A simple {@link Fragment} subclass.
 */
public class ChangeUsername extends Fragment {

    public ChangeUsername() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_username, container, false);
        Bundle bundle = getArguments();
        final String username = bundle.getString("username");
        final String password = bundle.getString("password");
        Log.d("ChangeUsername Bundle:", String.valueOf(bundle));

        final EditText etCurrentPassword = view.findViewById(R.id.etConfirmNewPassword);
        Button bNext = view.findViewById(R.id.bNext);
        Button bGoBack = view.findViewById(R.id.bReturn2Party);

        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentPassword = etCurrentPassword.getText().toString();

                if (!currentPassword.equals(password)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Password does not match Account's current password.")
                            .setNegativeButton("Retry", null)
                            .create()
                            .show();
                }else {
                    //Response listener to handle response from php file
                    //Create new bundle for Profile fragment
                    Bundle bundle = new Bundle();
                    bundle.putString("username", username);
                    bundle.putString("password", password);
                    //Begin fragment transacion
                    android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    //Create new instance of Profile fragment
                    ChangeUsernamePt2 changeUsername2 = new ChangeUsernamePt2();
                    changeUsername2.setArguments(bundle);
                    //Finish fragment transaction
                    fragmentTransaction.replace(R.id.flcontent, changeUsername2);
                    fragmentTransaction.commit();
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

