package com.example.mostafahussien.chatna;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {
    private RecyclerView recyclerView;
    private DatabaseReference requestsDB, userDB;
    private FirebaseAuth auth;
    private String currentUserID;
    View view;
    String requestType;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_requests, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.requests_list);
        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        requestsDB = FirebaseDatabase.getInstance().getReference().child("friends_req").child(currentUserID);
        userDB = FirebaseDatabase.getInstance().getReference().child("users");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        final List<String> requestIDS = new ArrayList<>();
        final List<users> requestedUsers = new ArrayList<>();
        //Query query=FirebaseDatabase.getInstance().getReference().child("friends").child(currentUserID).child("request_type").equalTo("send");
        requestsDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    requestType = snapshot.child("request_type").getValue().toString();
                    if (requestType.equals("received")) {
                        requestIDS.add(snapshot.getKey().toString());
                    }
                }
                for (String id : requestIDS) {
                    userDB.child(id).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            users user = dataSnapshot.getValue(users.class);
                            requestedUsers.add(user);
                            // notifiy adapter set changed
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }

                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
