package com.example.mostafahussien.chatna;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UsersActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ImageView imageView;
    private TextView textView;
    private DatabaseReference databaseReference;
    private String currentID;
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
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        currentID= FirebaseAuth.getInstance().getCurrentUser().getUid();
    }
    @Override
    protected void onStart() {
        super.onStart();
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("users");
        FirebaseRecyclerOptions<users> options =
                new FirebaseRecyclerOptions.Builder<users>()
                        .setQuery(query, users.class)
                        .build();

        FirebaseRecyclerAdapter<users,UsersViewHolder> adapter=new FirebaseRecyclerAdapter<users, UsersViewHolder> (options) {
            @Override
            public UsersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_row, parent, false);
                return new UsersViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(UsersViewHolder holder, final int position, final users model) {
                final String bindedUserID = getRef(position).getKey();
                    holder.setUserInfo(model.getName(), model.getStatus(), model.getImage(), getApplicationContext());
                    holder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(UsersActivity.this, ProfileActivity.class);
                            intent.putExtra("user", model);
                            intent.putExtra("userID", bindedUserID);
                            startActivity(intent);
                        }
                    });
            }
        };
        adapter.startListening();
        recyclerView.setAdapter(adapter);
    }
}
