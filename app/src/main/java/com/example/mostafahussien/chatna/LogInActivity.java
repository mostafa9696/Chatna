package com.example.mostafahussien.chatna;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.wang.avi.AVLoadingIndicatorView;

public class LogInActivity extends AppCompatActivity {
    LinearLayout registerLayout;
    Button logIn;
    String mail,password;
    private EditText userMail,userPass;
    private AVLoadingIndicatorView indicatorView;
    private FirebaseAuth auth;
    private TextView appName;
    private Typeface typeface;
    private DatabaseReference userDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        }
        registerLayout=(LinearLayout) findViewById(R.id.register_layout);
        logIn=(Button)findViewById(R.id.login);
        userMail=(EditText)findViewById(R.id.user_login_mail);
        userPass=(EditText)findViewById(R.id.user_login_password);
        appName=(TextView)findViewById(R.id.tv_app_name);
        indicatorView= (AVLoadingIndicatorView) findViewById(R.id.progress);
        typeface=Typeface.createFromAsset(getAssets(),"Kurale-Regular.ttf");
        appName.setTypeface(typeface);
        userDB= FirebaseDatabase.getInstance().getReference().child("users");
        auth= FirebaseAuth.getInstance();
    }

    public void login(View view) {
        mail=userMail.getText().toString();
        password=userPass.getText().toString();
        if(TextUtils.isEmpty(mail)||TextUtils.isEmpty(password)){
            Toast.makeText(getApplicationContext(),"Please filled above data!",Toast.LENGTH_LONG).show();
        } else {
            indicatorView.show();
            loginUser();
        }
    }
    public void loginUser(){
        logIn.setEnabled(false);
        auth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    indicatorView.hide();
                    String deviceTokenID= FirebaseInstanceId.getInstance().getToken();              // used for store it in DB to use in push notification and notification send to this token device id or other devices which user login his account on different devices
                    String currentID=auth.getCurrentUser().getUid();
                    userDB.child(currentID).child("device_token").setValue(deviceTokenID).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent intent=new Intent(LogInActivity.this,MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                    });

                }else {
                    indicatorView.hide();
                    Toast.makeText(getApplicationContext(),"Cannot sign in, please try again.",Toast.LENGTH_LONG).show();
                }
                logIn.setEnabled(true);
            }
        });
    }
    public void register(View view) {
        Intent intent=new Intent(LogInActivity.this,RegisterActivity.class);
        startActivity(intent);
    }
}
