package com.example.mostafahussien.chatna.Fragment;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.wang.avi.AVLoadingIndicatorView;

/**
 * Created by Mostafa Hussien on 28/03/2018.
 */

public class EditProfileDialog extends DialogFragment implements View.OnClickListener{
    EditText userName,userStatus;
    Button save;
    Context context;
    Dialog dialog;
    View dialogView;
    String name,status,uid;
    DatabaseReference reference;
    FirebaseUser user;
    AVLoadingIndicatorView indicatorView;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater=getActivity().getLayoutInflater();
        dialogView=inflater.inflate(R.layout.edit_profile_layout,null);
        dialog=new Dialog(getActivity());
        dialog.setContentView(dialogView);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        save=(Button) dialogView.findViewById(R.id.save_change);
        indicatorView= (AVLoadingIndicatorView)dialogView.findViewById(R.id.progress);
        save.setOnClickListener(this);
        initViews();
        initFirebase();
        return dialog;
    }
    public void initViews(){
        userName=(EditText)dialogView.findViewById(R.id.edit_user_name);
        userStatus=(EditText)dialogView.findViewById(R.id.edit_status);
        name=getArguments().getString("name");
        status=getArguments().getString("status");
        userName.setText(name);
        userStatus.setText(status);
    }
    public void initFirebase(){
        user= FirebaseAuth.getInstance().getCurrentUser();
        uid=user.getUid();
        reference= FirebaseDatabase.getInstance().getReference().child("users").child(uid);
    }
    @Override
    public void onClick(View view) {
        indicatorView.show();
        name=userName.getText().toString();
        status=userStatus.getText().toString();
        reference.child("name").setValue(name);
        reference.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(context,"Cannot edit profile pleas try again!",Toast.LENGTH_LONG).show();
                }
                indicatorView.hide();
                dialog.dismiss();
            }
        });


    }
}
