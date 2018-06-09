
package com.example.bennettdierckman.calclogin;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

public class ConfirmEnd extends Fragment {

    public ConfirmEnd() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_confirm_end, container, false);
        //Get arguments from HostParty fragment
        Bundle bundle = getArguments();
        final String title = bundle.getString("title");
        final String username = bundle.getString("username");
        final String password = bundle.getString("password");
        //Set buttons from ConfrirmEnd_fragment equal to new variables to be used in this java file
        Button bEndParty = (Button) view.findViewById(R.id.bEndParty);
        Button bCancel = (Button) view.findViewById(R.id.bManageAccount);
        final EditText etAccountPassword = (EditText) view.findViewById(R.id.accountPassword);

        //They want to end the party, we now check the password entered against the password in the bundle and then
        //  SWITCH FROM FRAGMENT TO FRAGMENT
        bEndParty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String accountPassword = etAccountPassword.getText().toString();
                //If the password in the bundle is equal to the account password defined above, fragment transact to Profile
                //.equals() method is used here becuase we're comparing two strings
                if (password.equals(accountPassword)) {
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
                            Profile profile = new Profile();
                            profile.setArguments(bundle);
                            //Finish fragment transaction
                            fragmentTransaction.replace(R.id.flcontent, profile);
                            fragmentTransaction.commit();
                        }
                    };
                    //prepares CreateRequest with the appropriate data, so that the php file cann be called and the party can
                    //be added to the database
                    EndRequest endRequest = new EndRequest(title, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
                    queue.add(endRequest);
                }
                else{
                    Toast.makeText(getActivity(),  "Password Incorrect, Make sure your using your account's password", Toast.LENGTH_LONG).show();
                }
            }
        });
        //User hits cancel button and returns to HostParty fragment
        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Prepares bundle arguments for HostParty fragment
                Bundle bundle = new Bundle();
                //HostParty Fragment needs title, additionally username and password
                bundle.putString("title", title);
                bundle.putString("username", username);
                bundle.putString("password", password);
                //Begining fragment transaction
                android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                //Create new instance of HostParty
                HostParty hostParty = new HostParty();
                //Set bundle arguments
                hostParty.setArguments(bundle);
                //Finish transaction
                fragmentTransaction.replace(R.id.flcontent, hostParty);
                fragmentTransaction.commit();
            }
        });
        return view;
    }

}
