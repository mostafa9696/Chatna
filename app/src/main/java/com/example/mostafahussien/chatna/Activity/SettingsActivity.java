package com.example.mostafahussien.chatna.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mostafahussien.chatna.Fragment.EditProfileDialog;
import com.example.mostafahussien.chatna.R;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

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
    }

    @Override
    protected void onResume() {
        super.onResume();
        getUserData();
    }
    /*@Override
    protected void onDestroy() {
        super.onDestroy();
        database.child("online").setValue(false);
    }*/
    public void getUserData(){
        database.keepSynced(true); // save data (name,status, ... and any string,integer but not for image ) offline in cache

        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name=dataSnapshot.child("name").getValue().toString();
                image=dataSnapshot.child("image").getValue().toString();
                status=dataSnapshot.child("status").getValue().toString();
                gender=dataSnapshot.child("gender").getValue().toString();
                userName.setText(name);
                userStatus.setText(status);

                if(!image.equals("default")) {
                    //Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.profileimage).into(imageView);

                    // save image offline in cache
                    Picasso.with(SettingsActivity.this).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                            .placeholder(R.drawable.profileimage).into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                        }
                        @Override
                        public void onError() {
                            // if app cannot load image offline then load default image
                            Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.profileimage).into(imageView);

                        }
                    });
                }
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
                    setAspectRatio(1,1)
                    // .setMinCropWindowSize(500,500)  // new
                    .start(this);
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK){
                showProgressDialog();
                Uri resultUri=result.getUri();
                final byte[]thumb_bytes=compressImage(resultUri);
                StorageReference filePath=storageReference.child("profile_images").child(userID + ".jpg");
                final StorageReference thumb_filePath=storageReference.child("profile_images").child("thumbs").child(userID + ".jpg");
                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){
                            final String download_uri=task.getResult().getDownloadUrl().toString();
                            UploadTask uploadTask=thumb_filePath.putBytes(thumb_bytes);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                    String thumb_downloadURI=thumb_task.getResult().getDownloadUrl().toString();
                                    if(thumb_task.isSuccessful()){
                                        Map update_map=new HashMap();
                                        update_map.put("image",download_uri);
                                        update_map.put("thumb_image",thumb_downloadURI);
                                        database.updateChildren(update_map).addOnCompleteListener(new OnCompleteListener() {
                                            @Override
                                            public void onComplete(@NonNull Task task) {
                                                if(task.isComplete()){
                                                    progressDialog.dismiss();
                                                    Toast.makeText(getApplicationContext(),"Image updated successfuly",Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                                    } else {
                                        progressDialog.dismiss();
                                        Toast.makeText(getApplicationContext(),"Cannot Upload Thumbnail !",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(getApplicationContext(),"Cannot Upload Image !",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e("Crop Image Error : ", error.toString() );
            }
        }
    }
    public void showProgressDialog(){
        progressDialog = new ProgressDialog(SettingsActivity.this);
        progressDialog.setTitle("Uploading Image...");
        progressDialog.setMessage("Please wait until uploading image finish.");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }
    public byte[] compressImage(Uri resultUri){
        File thumb_file=new File(resultUri.getPath());                                                      // new
        Bitmap thumb_bitmap=new Compressor(this)                                    // new (thumbnail) to decrease the size of image
                .setMaxHeight(200)
                .setMaxWidth(200)
                .setQuality(75)
                .compressToBitmap(thumb_file);
        ByteArrayOutputStream byteArrayOutputStream=new ByteArrayOutputStream();
        thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }
}
