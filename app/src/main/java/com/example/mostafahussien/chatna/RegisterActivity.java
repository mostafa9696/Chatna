package com.example.mostafahussien.chatna;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText userName,userEmail,userPasssword,userConfirmPassword;
    private RadioGroup radioGroup;
    private RadioButton radioButton;
    private Button button;
    private String name,email,pass,confirmPass;
    private FirebaseAuth auth;
    private String gender="empty";
    private Toolbar toolbar;
    private AVLoadingIndicatorView indicatorView;
    private DatabaseReference database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        auth=FirebaseAuth.getInstance();
        userName=(EditText)findViewById(R.id.user_name);
        userPasssword=(EditText)findViewById(R.id.user_password);
        userConfirmPassword=(EditText)findViewById(R.id.user_confirm_password);
        userEmail=(EditText)findViewById(R.id.user_Email);
        button=(Button)findViewById(R.id.register);
        radioGroup = (RadioGroup) findViewById(R.id.gender);
        indicatorView= (AVLoadingIndicatorView) findViewById(R.id.progress);
        toolbar=(Toolbar)findViewById(R.id.register_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Create New Account");
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.female_radio_button) {
                    gender="female";
                } else if (checkedId == R.id.male_radio_button) {
                    gender="male";
                }
            }
        });
        //dialog=
    }

    public void register(View view) {
        name=userName.getText().toString();
        email=userEmail.getText().toString();
        pass=userPasssword.getText().toString();
        confirmPass=userConfirmPassword.getText().toString();
        Log.e("cc5", gender);
        if(TextUtils.isEmpty(name)||TextUtils.isEmpty(email)||TextUtils.isEmpty(pass)||gender.equals("empty")){
            Toast.makeText(getApplicationContext(),"Please filled above data!",Toast.LENGTH_LONG).show();
        }
        //if(!pass.equals(confirmPass))                         // unComment it last after testing
        else  if(false)
        {
            Toast.makeText(getApplicationContext(),"Pssword not match Confirm Password !",Toast.LENGTH_LONG).show();
        }else {
            indicatorView.show();
            registerUser();
        }
    }
    public void registerUser(){
        Log.e("ww2",email);
        Log.e("ww2",pass);
        auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user=auth.getCurrentUser();
                            String userID=user.getUid();
                            database=FirebaseDatabase.getInstance().getReference().child("users").child(userID);
                            HashMap<String,String>userMap=new HashMap<>();
                            userMap.put("name",name);
                            userMap.put("status","Hello I am using Chatna App");
                            userMap.put("image","profileimage");
                            userMap.put("thumb_image","default");
                            userMap.put("gender",gender);
                            database.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        indicatorView.hide();
                                        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else {
                                        indicatorView.hide();
                                        Toast.makeText(RegisterActivity.this, "Cannot sign in, Please try again !",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            indicatorView.hide();
                            Toast.makeText(RegisterActivity.this, "Cannot sign in, Please try again !",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
    }


}
