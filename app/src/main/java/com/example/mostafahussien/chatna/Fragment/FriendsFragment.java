package com.example.mostafahussien.chatna.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mostafahussien.chatna.Activity.ChatActiity;
import com.example.mostafahussien.chatna.Activity.ProfileActivity;
import com.example.mostafahussien.chatna.ViewHolder.FriendsViewHolder;
import com.example.mostafahussien.chatna.R;
import com.example.mostafahussien.chatna.Model.friends;
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
                final String list_user_id=getRef(position).getKey();
                userDB.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String name=dataSnapshot.child("name").getValue().toString();
                        String thumb_image=dataSnapshot.child("thumb_image").getValue().toString();
                        if(dataSnapshot.hasChild("online")) {
                            String user_online = dataSnapshot.child("online").getValue().toString();
                            holder.setUserOnline(user_online);
                        }
                        holder.setFriendInfo(name, model.getDate(),thumb_image, getApplicationContext());
                        handleClickEvent(holder,list_user_id,name,thumb_image);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
    public void handleClickEvent(final FriendsViewHolder holder, final String list_user_id, final String name,final String imageUri){
        holder.viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getContext(), ProfileActivity.class);
                intent.putExtra("userID",list_user_id);
                intent.putExtra("notfication",true);
                startActivity(intent);
            }
        });

        holder.userChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent=new Intent(getContext(),ChatActiity.class);
                chatIntent.putExtra("userID",list_user_id);
                chatIntent.putExtra("userName",name);
                chatIntent.putExtra("image",imageUri);
                startActivity(chatIntent);
            }
        });
    }

}
