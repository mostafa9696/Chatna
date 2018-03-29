package com.example.mostafahussien.chatna;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        registerLayout=(LinearLayout) findViewById(R.id.register_layout);
        logIn=(Button)findViewById(R.id.login);
        userMail=(EditText)findViewById(R.id.user_login_mail);
        userPass=(EditText)findViewById(R.id.user_login_password);
        appName=(TextView)findViewById(R.id.tv_app_name);
        indicatorView= (AVLoadingIndicatorView) findViewById(R.id.progress);
        typeface=Typeface.createFromAsset(getAssets(),"Kurale-Regular.ttf");
        appName.setTypeface(typeface);

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
        auth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    indicatorView.hide();
                    Intent intent=new Intent(LogInActivity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }else {
                    indicatorView.hide();
                    Toast.makeText(getApplicationContext(),"Cannot sign in, please try again.",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public void register(View view) {
        Intent intent=new Intent(LogInActivity.this,RegisterActivity.class);
        startActivity(intent);
    }
}
