package com.example.mostafahussien.chatna;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TableLayout;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private TabLayout tabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        toolbar=(Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chatna");
        viewPager=(ViewPager)findViewById(R.id.main_tabPager);
        adapter=new ViewPagerAdapter(getSupportFragmentManager());
        tabLayout=(TabLayout)findViewById(R.id.main_tabs);
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.request_selector));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.chat_selector));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.users_selector_));
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }

    @Override
    protected void onStart() {
        super.onStart();
        // check if user is signed in (not-null) to update UI
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if(currentUser==null){
            startLoginScreen();
        } else {
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
            mAuth.signOut();
            startLoginScreen();
        }else if(id==R.id.acc_settings){
            Intent intent=new Intent(this,SettingsActivity.class);
            startActivity(intent);
        } else if(id==R.id.users) {
            Intent intent=new Intent(this,UsersActivity.class);
            startActivity(intent);
        }
        return true;
    }
}
