package com.example.bennettdierckman.calclogin;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class Set_Password extends Fragment {


    public Set_Password() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_set_password, container, false);

        //Get bundle arguments
        Bundle bundle = getArguments();
        //Assign bundle arguments to variables to be used in this java file
        final String username = bundle.getString("username");
        final String password = bundle.getString("password");
        final String title = bundle.getString("title");
        final String demographics = bundle.getString("demographics");
        final String radius = bundle.getString("radius");

        //Create values to hold content of initialize_fragment.xml text views
        final EditText etPartyPassword = (EditText) view.findViewById(R.id.etPartyPassword);
        //Bringing in buttons for next section of code
        Button bGoBack = (Button) view.findViewById(R.id.bReturn2Party);
        Button bNext = (Button) view.findViewById(R.id.bNext);

        //When the start party button is selected -> SWITCH FROM FRAGMENT TO FRAGMENT
        bNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String partyPassword = etPartyPassword.getText().toString();

                if (partyPassword.isEmpty()) {
                    Toast.makeText(getActivity(), "Enter a Party Password", Toast.LENGTH_LONG).show();
                    //Check if password is malicious
                } else if (!partyPassword.matches("(?!.*/)[A-Za-z0-9][A-Za-z0-9 ]{0,18}[A-Za-z0-9]")) {
                    Toast.makeText(getActivity(), "Party Password must be alpha numeric", Toast.LENGTH_LONG).show();
                    //EVerything is in accordance
                } else {
                    Bundle bundle = new Bundle();
                    //Username is in bundle for when user eventually goes back to their profile
                    bundle.putString("username", username);
                    //Password is in bundle for when the user eventually ends the p
                    bundle.putString("password", password);
                    //The remaining bundle arguments are about the party
                    bundle.putString("title", title);
                    bundle.putString("demographics", demographics);
                    bundle.putString("PartyPassword", partyPassword);
                    bundle.putString("radius", radius);
                    //Begining the transaction
                    android.support.v4.app.FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    android.support.v4.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    //Create new Set_Password fragment instance
                    Initialize initialize = new Initialize();
                    //set bundle arguments
                    initialize.setArguments(bundle);
                    //finish transaction
                    fragmentTransaction.replace(R.id.flcontent, initialize);
                    fragmentTransaction.commit();
                }
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

        return view;

    }

}


