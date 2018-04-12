package com.example.mostafahussien.chatna;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

public class UsersActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView imageView;
    private TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);
       textView=(TextView)findViewById(R.id.toolbar_text);
       imageView=(ImageView)findViewById(R.id.toolbar_icon);
       textView.setText("All Users");
       imageView.setImageResource(R.drawable.ic_users);
       recyclerView=(RecyclerView)findViewById(R.id.users_list);
       layoutManager=new LinearLayoutManager(this);
    }
}
