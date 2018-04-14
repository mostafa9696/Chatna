package com.example.mostafahussien.chatna;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.HashMap;

public class LogInActivity extends AppCompatActivity {
    private LinearLayout registerLayout;
    private Button logIn;
    private String mail, password,currentID;
    private EditText userMail, userPass;
    private AVLoadingIndicatorView indicatorView;
    private FirebaseAuth auth;
    private TextView appName;
    private Typeface typeface;
    private DatabaseReference userDB;
    private CallbackManager callbackManager;
    private AccessToken accessToken;
    private LoginButton loginButton;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("aa4", "aaa");
        setContentView(R.layout.activity_log_in);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
        registerLayout = (LinearLayout) findViewById(R.id.register_layout);
        logIn = (Button) findViewById(R.id.login);
        userMail = (EditText) findViewById(R.id.user_login_mail);
        userPass = (EditText) findViewById(R.id.user_login_password);
        appName = (TextView) findViewById(R.id.tv_app_name);
        indicatorView = (AVLoadingIndicatorView) findViewById(R.id.progress);
        typeface = Typeface.createFromAsset(getAssets(), "Kurale-Regular.ttf");
        appName.setTypeface(typeface);
        userDB = FirebaseDatabase.getInstance().getReference().child("users");
        auth = FirebaseAuth.getInstance();

    }

    public void initFacebookLogin() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        loginButton = (LoginButton) findViewById(R.id.fb_login);
        callbackManager = CallbackManager.Factory.create();
        loginButton.setReadPermissions("email", "public_profile", "user_status");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                accessToken = loginResult.getAccessToken();
                handleFacebookAccessToken();
                Log.e("yy2,LoginResult", loginResult.getAccessToken().getUserId() );
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), "Can not login with facebook.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void handleFacebookAccessToken() {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        auth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final FirebaseUser user = auth.getCurrentUser();
                    currentID = user.getUid();
                    userDB.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            SharedPreferences.Editor editor = getSharedPreferences("chatna_pref", MODE_PRIVATE).edit();
                            editor.putBoolean("fromFace", true);
                            editor.apply();
                            if(dataSnapshot.hasChild(currentID)){
                                navigateToHomeScreen(true);
                            } else {
                                String deviceTokenID = FirebaseInstanceId.getInstance().getToken();
                                HashMap<String,String> userMap=new HashMap<>();
                                userMap.put("name",user.getDisplayName());
                                userMap.put("status","Hello I am using Chatna App");
                                userMap.put("image", String.valueOf(user.getPhotoUrl()));
                                userMap.put("thumb_image",String.valueOf(user.getPhotoUrl()));
                                userMap.put("gender","male");
                                userMap.put("device_token",deviceTokenID);
                                userDB.child(currentID).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Intent intent=new Intent(LogInActivity.this,MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }else {
                                            Toast.makeText(LogInActivity.this, "Cannot sign in, Please try again !",
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                } else {
                    Toast.makeText(getApplicationContext(),"Cannot login with facebook.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    public void login(View view) {
        mail = userMail.getText().toString();
        password = userPass.getText().toString();
        if (TextUtils.isEmpty(mail) || TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Please filled above data!", Toast.LENGTH_LONG).show();
        } else {
            indicatorView.show();
            loginUser();
        }
    }

    public void loginUser() {
        logIn.setEnabled(false);
        auth.signInWithEmailAndPassword(mail, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    indicatorView.hide();
                    currentID=auth.getCurrentUser().getUid();
                    navigateToHomeScreen(false);

                } else {
                    indicatorView.hide();
                    Toast.makeText(getApplicationContext(), "Cannot sign in, please try again.", Toast.LENGTH_LONG).show();
                }
                logIn.setEnabled(true);
            }
        });
    }
    public void navigateToHomeScreen(final boolean fromFace){
        String deviceTokenID = FirebaseInstanceId.getInstance().getToken();              // used for store it in DB to use in push notification and notification send to this token device id or other devices which user login his account on different devices
        userDB.child(currentID).child("device_token").setValue(deviceTokenID).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
    public void register(View view) {
        Intent intent = new Intent(LogInActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void FBlogin(View view) {
        initFacebookLogin();
    }
}
