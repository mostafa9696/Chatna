package com.example.mostafahussien.chatna;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttpDownloader;
import com.squareup.picasso.Picasso;


public class Chatna extends Application {
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
    }
}
