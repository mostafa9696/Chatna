package com.example.mostafahussien.chatna.Fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.mostafahussien.chatna.Activity.ProfileActivity;
import com.example.mostafahussien.chatna.Adapter.FriendRequestAdapter;
import com.example.mostafahussien.chatna.Listener.OnPressFriendRequestUser;
import com.example.mostafahussien.chatna.R;
import com.example.mostafahussien.chatna.Model.users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class RequestsFragment extends Fragment {
    private RecyclerView recyclerView;
    private DatabaseReference requestsDB, userDB;
    private FirebaseAuth auth;
    private String currentUserID;
    private View view;
    private ImageView noFriendsRequest;
    private String requestType;
    private FriendRequestAdapter adapter;
    private List<String> requestIDS;
    private List<users> requestedUsers;
    private boolean rec;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_requests, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.requests_list);
        noFriendsRequest=(ImageView)view.findViewById(R.id.no_friend_request);
        auth = FirebaseAuth.getInstance();
        currentUserID = auth.getCurrentUser().getUid();
        requestsDB = FirebaseDatabase.getInstance().getReference().child("friends_req");
        userDB = FirebaseDatabase.getInstance().getReference().child("users");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        requestIDS = new ArrayList<>();
        requestedUsers = new ArrayList<>();
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        requestIDS.clear();
        requestedUsers.clear();
        rec=false;
        adapter=new FriendRequestAdapter(requestedUsers, getContext(),requestIDS, new OnPressFriendRequestUser() {
            @Override
            public void pressRequestedUser(users user, String visitedUserID) {
                Intent intent=new Intent(getContext(),ProfileActivity.class);
                intent.putExtra("userID",visitedUserID);
                intent.putExtra("user",user);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        requestsDB.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    requestType = snapshot.child("request_type").getValue().toString();
                    if (requestType.equals("received")) {
                        rec=true;
                        String id=snapshot.getKey().toString();
                        requestIDS.add(id);
                        userDB.child(id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                users user = dataSnapshot.getValue(users.class);
                                requestedUsers.add(user);
                                adapter.notifyDataSetChanged();
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                        if(requestedUsers.size()==0){
                            noFriendsRequest.setVisibility(View.VISIBLE);
                        }else {
                            noFriendsRequest.setVisibility(View.GONE);
                        }
                    }
                }
                if(!rec){
                    noFriendsRequest.setVisibility(View.VISIBLE);
                }else {
                    noFriendsRequest.setVisibility(View.GONE);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
