package com.example.mostafahussien.chatna.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import com.example.mostafahussien.chatna.R;
import com.example.mostafahussien.chatna.Adapter.ViewPagerAdapter;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private TabLayout tabLayout;
    private boolean fromFacebook;
    private DatabaseReference reference;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private FirebaseUser currentUser;
    private TextView toolbarText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        prefs = getSharedPreferences("chatna_pref", MODE_PRIVATE);
        editor = getSharedPreferences("chatna_pref", MODE_PRIVATE).edit();
        fromFacebook = prefs.getBoolean("fromFace",false);
        mAuth=FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null) {
            reference = FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getCurrentUser().getUid());
        }
        toolbar=(Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        toolbarText=(TextView)findViewById(R.id.toolbar_text);
        toolbarText.setText("Chatna");
        viewPager=(ViewPager)findViewById(R.id.main_tabPager);
        adapter=new ViewPagerAdapter(getSupportFragmentManager());
        tabLayout=(TabLayout)findViewById(R.id.main_tabs);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.request_selector));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.chat_selector));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.users_selector_));
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
        currentUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentUser==null){
            startLoginScreen();
        }else if(fromFacebook==false){
            reference.child("online").setValue("true");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(currentUser != null&&fromFacebook==false) {
            reference.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    public void startLoginScreen(){
        Intent intent=new Intent(this,LogInActivity.class);
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
         getMenuInflater().inflate(R.menu.menu,menu);
         return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id=item.getItemId();
        if(id==R.id.log_out){
            if(fromFacebook){
                LoginManager.getInstance().logOut();
                mAuth.signOut();
                editor.putBoolean("fromFace", false);
                editor.apply();
            } else {
                reference.child("online").setValue(ServerValue.TIMESTAMP);
                mAuth.signOut();
            }

            startLoginScreen();
        } else if(id==R.id.acc_settings){
            Intent intent=new Intent(this,SettingsActivity.class);
            startActivity(intent);
        } else if(id==R.id.users) {
            Intent intent=new Intent(this,UsersActivity.class);
            startActivity(intent);
        }
        return true;
    }
}
