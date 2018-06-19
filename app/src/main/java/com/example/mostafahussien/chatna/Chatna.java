package com.example.mostafahussien.chatna;

import android.app.Application;
import android.content.Context;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;


public class Chatna extends Application {
    DatabaseReference database;
    FirebaseAuth auth;

    @Override
    public void onCreate() {
        super.onCreate();

        // save firebase data (strings and integers only not image uri) offline
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        // for save image uri offline
        Picasso.Builder builder=new Picasso.Builder(this);
        builder.downloader(new OkHttpDownloader(this,Integer.MAX_VALUE));
        Picasso built=builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
        auth=FirebaseAuth.getInstance();
        if(auth.getCurrentUser() != null) {
            database = FirebaseDatabase.getInstance().getReference().child("users").child(auth.getUid());
            database.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        database.child("online").onDisconnect().setValue(ServerValue.TIMESTAMP);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
}
