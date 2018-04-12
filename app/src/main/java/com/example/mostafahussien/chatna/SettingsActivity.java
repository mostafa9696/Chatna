package com.example.mostafahussien.chatna;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {
    private DatabaseReference database;
    private FirebaseUser current_user;
    private String userID,name,gender,status,image;
    private CircleImageView imageView;
    private TextView userName,userStatus;
    private Button changeImage,editProfile;
    private static final int GALLARY_PICKER=1;
    private StorageReference storageReference;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        current_user= FirebaseAuth.getInstance().getCurrentUser();
        userID=current_user.getUid();
        storageReference= FirebaseStorage.getInstance().getReference();
        database= FirebaseDatabase.getInstance().getReference().child("users").child(userID);
        userName=(TextView) findViewById(R.id.text_user_name);
        userStatus=(TextView) findViewById(R.id.text_user_status);
        imageView=(CircleImageView)findViewById(R.id.profile_image);
        changeImage=(Button)findViewById(R.id.change_image);
        editProfile=(Button)findViewById(R.id.change_status);
        getUserData();
    }
    public void getUserData(){
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name=dataSnapshot.child("name").getValue().toString();
                image=dataSnapshot.child("image").getValue().toString();
                status=dataSnapshot.child("status").getValue().toString();
                gender=dataSnapshot.child("gender").getValue().toString();
                userName.setText(name);
                userStatus.setText(status);
                if(image.equals("default"))
                    Picasso.with(SettingsActivity.this).load(R.drawable.profileimage).into(imageView);
                else
                    Picasso.with(SettingsActivity.this).load(image).into(imageView);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void editProfile(View view) {
        DialogFragment dialogFragment = new EditProfileDialog();
        Bundle bundle=new Bundle();
        bundle.putString("name",name);
        bundle.putString("status",status);
        dialogFragment.setArguments(bundle);
        dialogFragment.show(getSupportFragmentManager(), "EditProfileDialog");
    }

    public void changeImage(View view) {
        Intent gallaryIntent=new Intent();
        gallaryIntent.setType("image/*");
        gallaryIntent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(gallaryIntent,"Select Profile Image"),GALLARY_PICKER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLARY_PICKER&&resultCode==RESULT_OK){
            Uri imageUri=data.getData();
            CropImage.activity(imageUri).
                    setAspectRatio(1,1).start(this);
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){                // chosen image return to setting activity
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                progressDialog=new ProgressDialog(SettingsActivity.this);
                progressDialog.setTitle("Uploading image ...");
                progressDialog.setMessage("Please wait while finish uploading image.");
                progressDialog.setCancelable(false);
                progressDialog.show();
                Uri resultUri=result.getUri();
                StorageReference filePath=storageReference.child("profile_images").child(userID + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            String download_url=task.getResult().getDownloadUrl().toString();
                            database.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(),"Image uploaded successfully !",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(getApplicationContext(),"Cannot Upload Image !",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        }
    }
}
