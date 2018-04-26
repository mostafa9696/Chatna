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
import com.example.mostafahussien.chatna.Model.Conv;
import com.example.mostafahussien.chatna.ViewHolder.ConvViewHolder;
import com.example.mostafahussien.chatna.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class ChatsFragment extends Fragment {
    View view;
    private RecyclerView convList;

    private DatabaseReference convDB;
    private DatabaseReference messageDB;
    private DatabaseReference usersDB;

    private FirebaseAuth auth;
    private String userOnline,userName,userImage;
    private String currentUserID,lastMsg;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view= inflater.inflate(R.layout.fragment_chats, container, false);
        convList = (RecyclerView) view.findViewById(R.id.conv_list);
        auth = FirebaseAuth.getInstance();

        currentUserID = auth.getCurrentUser().getUid();

        convDB = FirebaseDatabase.getInstance().getReference().child("chat").child(currentUserID);

        convDB.keepSynced(true);
        usersDB = FirebaseDatabase.getInstance().getReference().child("users");
        messageDB = FirebaseDatabase.getInstance().getReference().child("Messages").child(currentUserID);
        usersDB.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        convList.setLayoutManager(linearLayoutManager);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query=convDB.orderByChild("timestamp");
        FirebaseRecyclerOptions<Conv> options =
                new FirebaseRecyclerOptions.Builder<Conv>()
                        .setQuery(query, Conv.class)
                        .build();
        FirebaseRecyclerAdapter<Conv,ConvViewHolder> adapter=new FirebaseRecyclerAdapter<Conv, ConvViewHolder> (options) {
            @Override
            protected void onBindViewHolder(final ConvViewHolder holder, int position, final Conv model) {
                final String bindedUserID = getRef(position).getKey();
                lastMsg="";
                Query lastMessageQuery = messageDB.child(bindedUserID).limitToLast(1);
                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        lastMsg = dataSnapshot.child("message").getValue().toString();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }
                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }
                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                usersDB.child(bindedUserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        userName = dataSnapshot.child("name").getValue().toString();
                        userImage = dataSnapshot.child("thumb_image").getValue().toString();

                        if(dataSnapshot.hasChild("online")) {
                            userOnline = dataSnapshot.child("online").getValue().toString();
                        }
                        holder.setConvInfo(userName,userImage,userOnline,lastMsg,getContext());
                        holder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent chatIntent = new Intent(getContext(), ChatActiity.class);
                                chatIntent.putExtra("userID", bindedUserID);
                                chatIntent.putExtra("userName", userName);
                                chatIntent.putExtra("image", userImage);
                                startActivity(chatIntent);
                            }
                        });
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public ConvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_row, parent, false);
                return new ConvViewHolder(view);
            }
        };
        adapter.startListening();
        convList.setAdapter(adapter);
    }
}

