package com.example.mostafahussien.chatna;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import static com.facebook.FacebookSdk.getApplicationContext;


public class FriendsFragment extends Fragment {
    private RecyclerView recyclerView;
    private DatabaseReference friendsDB,userDB;
    private FirebaseAuth auth;
    private String currentUserID;
    private View view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_friends,container,false);
        recyclerView=(RecyclerView)view.findViewById(R.id.friends_list);
        auth=FirebaseAuth.getInstance();
        currentUserID=auth.getCurrentUser().getUid();
        friendsDB= FirebaseDatabase.getInstance().getReference().child("friends").child(currentUserID);
        userDB=FirebaseDatabase.getInstance().getReference().child("users");
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query=FirebaseDatabase.getInstance().getReference().child("friends").child(currentUserID);

        FirebaseRecyclerOptions<friends> options =
                new FirebaseRecyclerOptions.Builder<friends>()
                        .setQuery(query, friends.class)
                        .build();

        FirebaseRecyclerAdapter<friends,FriendsViewHolder> adapter=new FirebaseRecyclerAdapter<friends, FriendsViewHolder> (options) {
            @Override
            public FriendsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_row, parent, false);
                return new FriendsViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(final FriendsViewHolder holder, int position, final friends model) {
                String list_user_id=getRef(position).getKey();
                userDB.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name=dataSnapshot.child("name").getValue().toString();
                        String thumb_image=dataSnapshot.child("thumb_image").getValue().toString();
                        holder.setFriendInfo(name, model.getDate(),thumb_image, getApplicationContext());
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        adapter.startListening();
        Log.e("ww7", String.valueOf(adapter.getItemCount()));
        recyclerView.setAdapter(adapter);
    }


}
