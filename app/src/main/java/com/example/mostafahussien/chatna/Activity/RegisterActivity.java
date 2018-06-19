package com.example.mostafahussien.chatna.Activity;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.mostafahussien.chatna.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
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
    private AVLoadingIndicatorView indicatorView;
    private DatabaseReference database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
        auth=FirebaseAuth.getInstance();
        userName=(EditText)findViewById(R.id.user_name);
        userPasssword=(EditText)findViewById(R.id.user_password);
        userConfirmPassword=(EditText)findViewById(R.id.user_confirm_password);
        userEmail=(EditText)findViewById(R.id.user_Email);
        button=(Button)findViewById(R.id.register);
        radioGroup = (RadioGroup) findViewById(R.id.gender);
        indicatorView= (AVLoadingIndicatorView) findViewById(R.id.progress);
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
        if(TextUtils.isEmpty(name)||TextUtils.isEmpty(email)||TextUtils.isEmpty(pass)||gender.equals("empty")){
            Toast.makeText(getApplicationContext(),"Please filled above data!",Toast.LENGTH_LONG).show();
        }
        else if(!pass.equals(confirmPass))
        {
            Toast.makeText(getApplicationContext(),"Pssword not match Confirm Password !",Toast.LENGTH_LONG).show();
        }else {
            indicatorView.show();
            registerUser();
        }
    }
    public void registerUser(){
        button.setEnabled(false);
        auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user=auth.getCurrentUser();
                            String userID=user.getUid();
                            String deviceTokenID=FirebaseInstanceId.getInstance().getToken();
                            database=FirebaseDatabase.getInstance().getReference().child("users").child(userID);
                            HashMap<String,String>userMap=new HashMap<>();
                            userMap.put("name",name);
                            userMap.put("status","Hello I am using Chatna App");
                            userMap.put("image","default");
                            userMap.put("thumb_image","default");
                            userMap.put("gender",gender);
                            userMap.put("device_token",deviceTokenID);
                            userMap.put("email",userEmail.getText().toString());
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
                                        button.setEnabled(true);
                                    }
                                }
                            });

                        } else {
                            indicatorView.hide();
                            String error="Cannot sign in, Please try again !";
                            try{
                                    throw task.getException();
                            }catch (FirebaseAuthWeakPasswordException e){
                                error="Please enter a strong password (more than 5 char)";
                            } catch (FirebaseAuthInvalidCredentialsException e){
                                error="Please enter a valid email";
                            }
                            catch (FirebaseAuthUserCollisionException e){
                                error="This email was used before";
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                            Toast.makeText(RegisterActivity.this, error,
                                    Toast.LENGTH_SHORT).show();
                        }
                        button.setEnabled(true);
                    }
                });
    }


}
